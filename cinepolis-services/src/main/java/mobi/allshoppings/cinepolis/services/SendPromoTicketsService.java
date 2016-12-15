package mobi.allshoppings.cinepolis.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import mobi.allshoppings.cinepolis.vista.loyalty.VistaLoyaltyService;
import mobi.allshoppings.dao.CampaignSpecialDAO;
import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.DeviceLocationDAO;
import mobi.allshoppings.dao.spi.CampaignSpecialDAOJDOImpl;
import mobi.allshoppings.dao.spi.CinemaDAOJDOImpl;
import mobi.allshoppings.dao.spi.DeviceInfoDAOJDOImpl;
import mobi.allshoppings.dao.spi.DeviceLocationDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.geocoding.impl.GeoCodingHelperGMapsImpl;
import mobi.allshoppings.model.CampaignSpecial;
import mobi.allshoppings.model.Cinema;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.DeviceLocation;
import mobi.allshoppings.model.Voucher;
import mobi.allshoppings.model.embedded.AlternateLocation;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;

public class SendPromoTicketsService {

	private static final Logger log = Logger.getLogger(SendPromoTicketsService.class.getName());

	/**
	 * Base key
	 */
	private static final String BASE_ID = "cinepolis_mx";

	/**
	 * Internal date formatter
	 */
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
	private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Campaign special to use
	 */
	private static final String CAMPAIGN_SPECIAL_IDS[] = new String[] {
		"1432724531038", /* Bagui */ 
		"1432724594627" /* Crepa */ };

	/**
	 * DAOs 
	 */
	private CinemaDAO cinemaDao = new CinemaDAOJDOImpl();
	private DeviceInfoDAO deviceInfoDao = new DeviceInfoDAOJDOImpl();
	private DeviceLocationDAO deviceLocationDao = new DeviceLocationDAOJDOImpl();
	private CampaignSpecialDAO campaignSpecialDao = new CampaignSpecialDAOJDOImpl();
	private GeoCodingHelper geocoder = new GeoCodingHelperGMapsImpl();

	/**
	 * Updates the movies list
	 * @throws ParseException 
	 * @throws JSONException 
	 */
	public void doProcess(Date forDate, String serviceUrl,
			long locationUpdateLimit, List<String> restrictToDevices,
			List<String> forcedDevices, List<String> cinemaIds,
			boolean useAltLocations, boolean fakeCoupons,
			String forCampaignSpecialId, boolean ignoreLocks) throws ASException, JSONException,
			ParseException {

		// Initialization settings
		deviceLocationDao.setGeocoder(geocoder);
		deviceInfoDao.setDeviceLocationDao(deviceLocationDao);

		log.log(Level.INFO, "Starting to send promo tickets for Cinepolis");

		// Defines redeem limit time
		Date validDateTime = sdf.parse(sdf2.format(new Date()) + " 14:00:00 CDT");
		Date limitDateTime = sdf.parse(sdf2.format(new Date()) + " 16:00:00 CDT");
		Date now = new Date();

		RestTemplate restTemplate = new RestTemplate();
		HttpMessageConverter<?> formHttpMessageConverter = new MappingJackson2HttpMessageConverter();
		HttpMessageConverter<?> stringHttpMessageConverternew = new StringHttpMessageConverter();
		restTemplate.setMessageConverters(Arrays.asList(new HttpMessageConverter<?>[]{formHttpMessageConverter, stringHttpMessageConverternew}));


		List<Integer> statuses = Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED});
		List<Cinema> cinemas = cinemaIds == null ? cinemaDao.getUsingBrandAndStatusAndRange(BASE_ID, statuses, null, null) : cinemaDao.getUsingIdList(cinemaIds, true);

		for( Cinema cinema : cinemas ) {
			log.log(Level.INFO, "Starting to send promo tickets for Cinepolis cinema " + cinema.getName());

			// Locate candidate devices
			// Only when there are no restrictToDevices
			List<String> devices = CollectionFactory.createList();
			if( restrictToDevices == null || restrictToDevices.size() == 0) {
				Date lastLocationUpdate = new Date((long)(new Date().getTime() - (locationUpdateLimit)));
				List<DeviceInfo> candidateDevices = deviceInfoDao.getByProximity(
						new GeoPoint(cinema.getAddress().getLatitude(), cinema
								.getAddress().getLongitude(), geocoder
								.encodeGeohash(cinema.getAddress().getLatitude(),
										cinema.getAddress().getLongitude())),
										new Integer(5), new Integer(500), cinema.getBrandId(),
										lastLocationUpdate, true);

				if( useAltLocations ) {
					for( AlternateLocation location : cinema.getAlternateLocations() ) {
						log.log(Level.WARNING, "Alternate Location found: " + location.getName());
						List<DeviceInfo> tmpdevices = deviceInfoDao.getByProximity(
								new GeoPoint(location.getLatitude(), location
										.getLongitude(), geocoder
										.encodeGeohash(location.getLatitude(),
												location.getLongitude())),
												new Integer(5), new Integer(500), cinema.getBrandId(),
												lastLocationUpdate, true);
						for( DeviceInfo obj : tmpdevices ) {
							if(!candidateDevices.contains(obj)) candidateDevices.add(obj);
						}
					}
				}

				for( DeviceInfo obj : candidateDevices ) {
					if( !devices.contains(obj.getIdentifier())) devices.add(obj.getIdentifier());
				}
			}

			// Restricted devices Algorithms
			// It checks main and alternate locations
			if( restrictToDevices != null ) {
				for( String device : restrictToDevices ) {
					if(!devices.contains(device)) {
						try {
							DeviceLocation dl = deviceLocationDao.get(device, true);
							if(( now.getTime() - dl.getLastUpdate().getTime()) <= locationUpdateLimit ) {
								int distance = geocoder.calculateDistance(dl.getLat(), dl.getLon(), cinema.getAddress().getLatitude(), cinema.getAddress().getLongitude());
								if ( distance < 500 ) {
									devices.add(device);
								} else {
									if( useAltLocations ) {
										for( AlternateLocation location : cinema.getAlternateLocations() ) {
											log.log(Level.WARNING, "Alternate Location found: " + location.getName());
											distance = geocoder.calculateDistance(dl.getLat(), dl.getLon(), location.getLatitude(), location.getLongitude());
											if ( distance < 500 ) {
												if(!devices.contains(device)) {
													devices.add(device);
												}
											}
										}
									}
								}
							}
						} catch( Exception e ) {
							log.log(Level.SEVERE, "Forced Device " + device + " not found in location database");
						}
					}
				}
			}

			// Forced devices algorithm
			// These devices are ALWAYS notified, even if they are not in range
			if( forcedDevices != null ) {
				for( String device : forcedDevices ) {
					if(!devices.contains(device)) {
						devices.add(device);
					}
				}
			}
			
			log.log(Level.INFO, devices.size() + " devices found");

			// Selects Campaign Special to use
			int index = randInt(0, CAMPAIGN_SPECIAL_IDS.length -1);
			CampaignSpecial cs;
			if( StringUtils.hasText(forCampaignSpecialId)) {
				cs = campaignSpecialDao.get(forCampaignSpecialId, true);
			} else {
				cs = campaignSpecialDao.get(CAMPAIGN_SPECIAL_IDS[index], true);
			}

			// Forms the Data object to send to the main server
			Map<String, Object> obj = CollectionFactory.createMap();
			obj.put("campaignSpecialId", cs.getIdentifier());
			obj.put("actionPrepend", "promos/");
			obj.put("brandId", cinema.getBrandId());
			obj.put("ignoreLocks", ignoreLocks);

			Map<String, Object> extras = CollectionFactory.createMap();
			// FIXME: I'm hardcoded
			if( cs.getName().startsWith("Crepa")) {
				extras.put("price", 35);
				extras.put("loyaltyFormat", 70119);
			} else {
				extras.put("price", 34);
				extras.put("loyaltyFormat", 70118);
			}
			extras.put("cinemaId", cinema.getIdentifier());
			extras.put("cinema", cinema.getName());
			extras.put("validFrom", validDateTime);
			extras.put("validIn", "Válido exclusivamente en Cinecafé© y/o Coffee Tree© y/o Baguis© que se ubiquen dentro del complejo ");
			extras.put("limitDateTime", limitDateTime);

			// Gets Loyalty coupons
			VistaLoyaltyService vs = VistaLoyaltyService.getInstance();

			List<Map<String, Object>> longDevices = CollectionFactory.createList();
			for( String device : devices ) {
				Map<String, Object> dev = CollectionFactory.createMap();
				List<String> coupons = CollectionFactory.createList();
				if( fakeCoupons ) {
					coupons.add("00000000001");
				} else {
					List<Voucher> vouchers = vs.get(1, extras.get("loyaltyFormat").toString(), device, cs.getIdentifier());
					for( Voucher v : vouchers ) {
						coupons.add(v.getCode());
					}
				}
				dev.put("deviceUUID", device);
				dev.put("coupons", coupons);
				longDevices.add(dev);
			}

			obj.put("devices", devices);
			obj.put("longDevices", longDevices);
			
			obj.put("entityId", cinema.getIdentifier());
			obj.put("description", cinema.getName() + " - " + cs.getName());

			obj.put("extras", extras);
			
			// Log
			log.log(Level.INFO, "Sending Promo " + cs.getName() + " for cinema " + cinema.getName() + " to users " + devices.toString());
			
			// Sends the data to the server
			int maxRetries = 3;
			while( maxRetries > 0 ) {
				try {
					Object result = restTemplate.postForObject(serviceUrl, obj, Object.class);
					System.out.println(result);
					if( result.toString().contains("status=500")) {
						maxRetries--;
					} else {
						maxRetries = 0;
					}
				} catch( Exception e ) {
					maxRetries--;
					try {Thread.sleep(1000);} catch(Exception e1){}
				}
			}

		}

		log.log(Level.INFO, "Send promo tickets for Cinepolis finished");

	}

	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}
}
