package mobi.allshoppings.cinepolis.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.cinepolis.vista.voucher.VistaVoucherService;
import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.DeviceLocationDAO;
import mobi.allshoppings.dao.MovieDAO;
import mobi.allshoppings.dao.ShowtimeDAO;
import mobi.allshoppings.dao.spi.CinemaDAOJDOImpl;
import mobi.allshoppings.dao.spi.DeviceInfoDAOJDOImpl;
import mobi.allshoppings.dao.spi.DeviceLocationDAOJDOImpl;
import mobi.allshoppings.dao.spi.MovieDAOJDOImpl;
import mobi.allshoppings.dao.spi.ShowtimeDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.geocoding.impl.GeoCodingHelperGMapsImpl;
import mobi.allshoppings.model.Cinema;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.DeviceLocation;
import mobi.allshoppings.model.Movie;
import mobi.allshoppings.model.Showtime;
import mobi.allshoppings.model.Voucher;
import mobi.allshoppings.model.embedded.AlternateLocation;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;

import org.json.JSONException;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

public class SendMovieTicketsService {

	private static final Logger log = Logger.getLogger(SendMovieTicketsService.class.getName());

	/**
	 * Base key
	 */
	private static final String BASE_ID = "cinepolis_mx";

	/**
	 * Internal date formatter
	 */
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm z");

	/**
	 * Campaign special to use
	 */
	private static final String CAMPAIGN_SPECIAL_ID = "1430288511084";
	
	/**
	 * DAOs 
	 */
	private CinemaDAO cinemaDao = new CinemaDAOJDOImpl();
	private ShowtimeDAO showtimeDao = new ShowtimeDAOJDOImpl();
	private MovieDAO movieDao = new MovieDAOJDOImpl();
	private DeviceInfoDAO deviceInfoDao = new DeviceInfoDAOJDOImpl();
	private DeviceLocationDAO deviceLocationDao = new DeviceLocationDAOJDOImpl();
	private GeoCodingHelper geocoder = new GeoCodingHelperGMapsImpl();
	private VistaDataService vistaDataService;
	
	/**
	 * Updates the movies list
	 * @throws ParseException 
	 * @throws JSONException 
	 */
	public void doProcess(Date forDate, long timeLimit, String serviceUrl,
			long locationUpdateLimit, List<String> restrictToDevices,
			List<String> forcedDevices, boolean disableShowAfterSend,
			List<String> cinemaIds, boolean useAltLocations,
			boolean fakeCoupons, String forShowtime, boolean ignoreLocks, boolean disableOlder) throws ASException,
			JSONException, ParseException {
		
		// Initialization settings
		deviceLocationDao.setGeocoder(geocoder);
		deviceInfoDao.setDeviceLocationDao(deviceLocationDao);
		
		log.log(Level.INFO, "Starting to send movie tickets for Cinepolis");

		Date limitDateTime = new Date(new Date().getTime() + timeLimit);
		Date now = new Date();

		RestTemplate restTemplate = new RestTemplate();
		HttpMessageConverter<?> formHttpMessageConverter = new MappingJackson2HttpMessageConverter();
		HttpMessageConverter<?> stringHttpMessageConverternew = new StringHttpMessageConverter();
		restTemplate.setMessageConverters(Arrays.asList(new HttpMessageConverter<?>[]{formHttpMessageConverter, stringHttpMessageConverternew}));


		List<Integer> statuses = Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED});
		List<Cinema> cinemas = cinemaIds == null ? cinemaDao.getUsingBrandAndStatusAndRange(BASE_ID, statuses, null, null) : cinemaDao.getUsingIdList(cinemaIds, true);

		for( Cinema cinema : cinemas ) {
			log.log(Level.INFO, "Starting to send movie tickets for Cinepolis cinema " + cinema.getName());
			List<Showtime> candidateShows = showtimeDao.getUsingCinemaAndDateAndStatusAndRange(cinema.getIdentifier(), forDate, null, null, "showTime");
			List<Showtime> finalShows = CollectionFactory.createList();

			if( StringUtils.hasText(forShowtime)) {
				finalShows.add(showtimeDao.get(forShowtime, true));
			} else {
				// Select shows between time range
				StringBuffer sb = new StringBuffer();
				for(Showtime show : candidateShows ) {
					try {
						sb.append(show.getShowTime()).append(" ");
						Date showDateTime = sdf.parse(show.getShowDate() + " " + show.getShowTime() + " CDT");
						if( showDateTime.before(limitDateTime) && now.before(showDateTime)) {
							log.log(Level.INFO, "Show " + show + " is between valid ranges ");
							if(!show.getStatus().equals(StatusAware.STATUS_VIEWED)) {
								finalShows.add(show);
							}
						} else if( now.after(showDateTime) && show.getStatus().equals(StatusAware.STATUS_ENABLED)) {
							show.setStatus(StatusAware.STATUS_VIEWED);
							showtimeDao.update(show);
						}
					} catch (ParseException e) {
						log.log(Level.SEVERE, e.getMessage(), e);
					}
				}

				log.log(Level.INFO, finalShows.size() + " shows found");
				log.log(Level.INFO, "Recorded show times where " + sb.toString());
			}
			
			// For each show
			for( Showtime show : finalShows ) {
				Movie movie = movieDao.get(show.getMovie().getIdentifier(), true);
				log.log(Level.INFO, "Movie is: " + movie.getName());

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
											new Integer(5), new Integer(cinema.getRadius().intValue()), cinema.getBrandId(),
											lastLocationUpdate, true);

					if( useAltLocations ) {
						for( AlternateLocation location : cinema.getAlternateLocations() ) {
							log.log(Level.WARNING, "Alternate Location found: " + location.getName());
							List<DeviceInfo> tmpdevices = deviceInfoDao.getByProximity(
									new GeoPoint(location.getLatitude(), location
											.getLongitude(), geocoder
											.encodeGeohash(location.getLatitude(),
													location.getLongitude())),
													new Integer(5),
													new Integer(location.getRadius() != null ? location
																	.getRadius().intValue()
																	: cinema.getRadius().intValue()),
													cinema.getBrandId(),
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
									if ( distance < cinema.getRadius().intValue() ) {
										devices.add(device);
									} else {
										if( useAltLocations ) {
											for( AlternateLocation location : cinema.getAlternateLocations() ) {
												log.log(Level.WARNING, "Alternate Location found: " + location.getName());
												distance = geocoder.calculateDistance(dl.getLat(), dl.getLon(), location.getLatitude(), location.getLongitude());
												if ( distance < 200 ) {
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

				// Completes info with vista data service
				try {
					vistaDataService = VistaDataService.getInstance();
					Map<String, Object> ds = vistaDataService.getShowtimeAttributes(cinema.getAlternateIdentifier(), show.getInternalIdentifier());
					show.setAvailableSeats(Integer.parseInt(String.valueOf(ds.get("availableSeats"))));
					show.setScreen((String)ds.get("screen"));
				} catch( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}

				// Retrieve Price Information
				String format = CinepolisFormatMapper.map(show.getFormatName());
				Double price = cinema.getPriceForFormat(format);
				if( price != null ) {

					VistaVoucherService vs = VistaVoucherService.getInstance();
					
					List<Map<String, Object>> longDevices = CollectionFactory.createList();
					for( String device : devices ) {
						Map<String, Object> dev = CollectionFactory.createMap();
						List<String> coupons = CollectionFactory.createList();
						if( fakeCoupons ) {
							coupons.add("00000000001");
							coupons.add("00000000002");
							coupons.add("00000000003");
						} else {
							List<Voucher> vouchers = vs.get(3, format, device, show.getIdentifier());
							for( Voucher v : vouchers ) {
								coupons.add(v.getCode());
							}
						}
						dev.put("deviceUUID", device);
						dev.put("coupons", coupons);
						longDevices.add(dev);
					}
					
					// Forms the Data object to send to the main server
					Map<String, Object> obj = CollectionFactory.createMap();
					obj.put("devices", devices);
					obj.put("longDevices", longDevices);
					obj.put("campaignSpecialId", CAMPAIGN_SPECIAL_ID);
					obj.put("actionPrepend", "tickets/");
					obj.put("brandId", cinema.getBrandId());
					obj.put("imageUrl", movie.getAvatarId());
					obj.put("ignoreLocks", ignoreLocks);
					obj.put("disableOlder", disableOlder);
					
					obj.put("entityId", show.getIdentifier());
					obj.put("description", cinema.getName() + " - " + movie.getName() + " - " + show.getShowDate() + " " + show.getShowTime());

					Map<String, Object> extras = CollectionFactory.createMap();
					extras.put("name", movie.getName());
					extras.put("format", show.getFormatName().toUpperCase());
					extras.put("screen", show.getScreen());
					extras.put("price", price);
					extras.put("description", "Entrada a precio especial de $" + extras.get("price"));
					extras.put("availableSeats", show.getAvailableSeats());
					extras.put("showDateTime", sdf.parse(show.getShowDate() + " " + show.getShowTime() + " CDT"));
					extras.put("cinema", cinema.getName());
					extras.put("rate", movie.getRate());
					extras.put("length", movie.getLenght());
					extras.put("movieGender", movie.getMovieGender());
					extras.put("showtimeId", show.getIdentifier());

					obj.put("extras", extras);

					// Log
					log.log(Level.INFO, "Sending Movie " + movie.getName() + " for cinema " + cinema.getName() + " to users " + devices.toString());

					int maxRetries = 3;
					while( maxRetries > 0 ) {
						// Sends the data to the server
						try {
							Object result = restTemplate.postForObject(serviceUrl, obj, Object.class);
							System.out.println(result);
							if( result.toString().contains("status=500")) {
								maxRetries--;
							} else {
								// Disables the show to not process it again
								if( disableShowAfterSend ) {
									show.setStatus(StatusAware.STATUS_VIEWED);
								}
								showtimeDao.update(show);
								maxRetries = 0;
							}
						} catch( Exception e ) {
							maxRetries--;
							try {Thread.sleep(1000);} catch(Exception e1){}
						}
					}

					// Just send one movie per showtime
					break;
				}
			}
		}

		log.log(Level.INFO, "Send movie tickets for Cinepolis finished");

	}

}
