package mobi.allshoppings.bz.spi;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.DeviceLocationBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.DeviceLocationDAO;
import mobi.allshoppings.dao.DeviceLocationHistoryDAO;
import mobi.allshoppings.dao.GeoEntityDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.DistanceComparator;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.geocoding.HotspotService;
import mobi.allshoppings.lock.LockHelper;
import mobi.allshoppings.model.AddressComponentsCache;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.DeviceLocation;
import mobi.allshoppings.model.DeviceLocationHistory;
import mobi.allshoppings.model.DeviceMessageLock;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.GeoEntity;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.adapter.DeviceLocationAdapter;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;
import mobi.allshoppings.model.adapter.HotSpotAdapter;
import mobi.allshoppings.model.adapter.LocationAwareAdapter;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.task.QueueTaskHelper;
import mobi.allshoppings.tools.CollectionFactory;

/**
 *
 */
public class DeviceLocationBzServicePIJSONImpl
extends RestBaseServerResource
implements DeviceLocationBzService {

	private static final Logger log = Logger.getLogger(DeviceLocationBzServicePIJSONImpl.class.getName());
	private static final int updateLimit = 1000 * 10 * 1;
	private static final SimpleDateFormat hourSDF = new SimpleDateFormat("HH");
	
	private static final String CINEPOLIS_APP = "cinepolis_mx";
	
	@Autowired
	private DeviceLocationDAO dao;
	@Autowired
	private DeviceInfoDAO deviceInfoDao;
	@Autowired
	private UserDAO userDao;
	@Autowired
	private KeyHelper keyHelper;
	@Autowired
	private GeoEntityDAO geoDao;
	@Autowired
	private ShoppingDAO shoppingDao;
	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private GeoCodingHelper geocoder;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private DeviceLocationHistoryDAO dlhDao;
	@Autowired
	private HotspotService hotspotService;
	@Autowired
	private LockHelper lockHelper;
	@Autowired
	private QueueTaskHelper queueHelper;

	private static final String IDENTIFIER = "deviceUUID";
	private BzFields bzFields = BzFields.getBzFields(getClass());
	private static Map<String, String> localCache = CollectionFactory.createMap();

	private final List<GeoPointAdapter> validZones = Arrays.asList(new GeoPointAdapter[] {
			new GeoPointAdapter(19.688831, -101.158199, 15000) /* Morelia */,
			new GeoPointAdapter(19.397, -99.282, 3000) /* Paseo Interlomas */,
			new GeoPointAdapter(19.503, -99.204, 3000) /* Town Center el Rosario */,
			new GeoPointAdapter(19.359, -99.271, 2000), /* Televisa */
			new GeoPointAdapter(25.637, -100.314, 3000), /* Galerías Valle Oriente */
			new GeoPointAdapter(25.6524623, -100.3327911, 5000) /* Edificio Los Soles / Monterrey */
	});

	private final List<String> validDevices = Arrays.asList(new String[] {});

	@Override
	public String retrieve() {
		long start = markStart();
		JSONObject returnValue;
		try {
			// validate authToken
			User user = this.getUserFromToken();

			// obtain the id
			final String deviceId = obtainIdentifier(IDENTIFIER, true);

			DeviceLocation obj = dao.get(deviceId, true);

			// Obtains the user JSON representation
			final String[] fields = obtainOutputFields(bzFields, systemConfiguration.getDefaultLevelOnUserBzService());
			returnValue = getJSONRepresentationFromObject(obj, fields);

			// track action
			trackerHelper.enqueue( user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.DeviceLocation.get"), 
					null, null);

		} catch (ASException e) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE || 
					e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE) {
				log.log(Level.INFO, e.getMessage());
			} else {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			returnValue = getJSONRepresentationFromException(e);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			returnValue = getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e));
		} finally {
			markEnd(start);
		}
		return returnValue.toString();

	}

	@Override
	public String post(final JsonRepresentation entity) {

		long start = markStart();
		try {
			final JSONObject obj = entity.getJsonObject();
			String userId = obtainUserIdentifier(false);
			JSONObject returnValue;

			boolean needsToUpdateDeviceInfo = false;
			String application = systemConfiguration.getDefaultAppId();

			//check mandatory fields
			log.fine("check mandatory fields");
			if (!hasDefaultParameters(obj, bzFields.DEFAULT_FIELDS.toArray(EMPTY_STRING_ARRAY))) {
				throw ASExceptionHelper.invalidArgumentsException(INVALID_DEFAULT_FIELDS);
			}
			
			// Checks if the user is an app
			User checkUser = null;
			try {
				if( userId != null ) {
					checkUser = userDao.get(userId, true);
				} else {
					log.log(Level.FINE, "No userId for this request.... ");
				}
			} catch( ASException e ) {
				log.log(Level.FINE, "No userId for this request.... ");
			}

			// Try to find or create the object
			boolean newLocation = false;
			DeviceLocation deviceLocation = null;
			try {
				deviceLocation = dao.get(obj.get("deviceUUID").toString(), true);
			} catch( ASException e ) {
				if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE) {
					newLocation = true;
					deviceLocation = new DeviceLocation();
					keyHelper.setKeyWithIdentifier(deviceLocation, obj.get("deviceUUID").toString());
				} else {
					throw e;
				}
			}

			// Creates a standing point for the last location obtained
			GeoPoint p1 = geocoder.getGeoPoint(
					deviceLocation.getLat() != null ? deviceLocation.getLat() : 0D,
							deviceLocation.getLon() != null ? deviceLocation.getLon() : 0D);

			// Sets object properties
			setPropertiesFromJSONObject(obj, deviceLocation, bzFields.READONLY_FIELDS);
			
			// Correct an older version misspelling
			if( obj.has("presition")) deviceLocation.setPrecision(obj.getDouble("presition"));

			// Try to get the associated device info
			boolean newDevice = false;
			DeviceInfo di = null;
			String userName = null;
			try {
				di = deviceInfoDao.get(deviceLocation.getDeviceUUID(), true);
				userName = di.getUserName();
			} catch( ASException e ) {
				if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE) {
					if(!StringUtils.hasText(userId) || userId.equals("null")) {
						log.log(Level.WARNING, "userId is null and there is no device to confirm it for deviceId " 
								+ deviceLocation.getDeviceUUID());
						log.log(Level.WARNING, obj.toString());
						return getJSONRepresentationFromException(ASExceptionHelper
								.notFoundException("userId is null and there is no device to confirm it for deviceId " 
										+ deviceLocation.getDeviceUUID())).toString();
					}
					newDevice = true;
					di = new DeviceInfo();
					setPropertiesFromJSONObject(obj, di, bzFields.READONLY_FIELDS);
					if( checkUser != null && mobi.allshoppings.model.UserSecurity.Role.APPLICATION == checkUser.getSecuritySettings().getRole()) {
						di.setUserId(deviceLocation.getDeviceUUID() + "@" + userId);
					} else {
						di.setUserId(userId);
					}
					if( StringUtils.hasText(obtainLangOrNull())) di.setLang(obtainLangOrNull());
					keyHelper.setKeyWithIdentifier(di, obj.get("deviceUUID").toString());
					needsToUpdateDeviceInfo = true;
					userName = di.getUserName();
				} else {
					throw e;
				}
			}

			// Updates device language
			if( di != null && StringUtils.hasText(obtainLangOrNull()) && !obtainLang().equals(di.getLang())) {
				di.setLang(obtainLang());
				needsToUpdateDeviceInfo = true;
			}

			// Updates associated user Identifiers
			if( userId == null ) {
				if( di != null ) userId = di.getUserId();
				deviceLocation.setUserId(userId);
				deviceLocation.setUserName(userName);
			} else {
				if( di != null && !userId.equals(di.getUserId()) && !di.getUserId().endsWith(userId) ) {
					di.setUserId(userId);
					needsToUpdateDeviceInfo = true;
					deviceLocation.setUserId(userId);
					deviceLocation.setUserName(userName);
				} else {
					deviceLocation.setUserId(di.getUserId());
					deviceLocation.setUserName(di.getUserName());
				}
			}

			// Filter just valid values
			if( deviceLocation.getLat() != 0 && deviceLocation.getLon() != 0 ) {

				boolean updateLimitOverpassed = deviceLocation.getLastUpdate() != null ? 
						(new Date() .getTime() - deviceLocation.getLastUpdate().getTime()) > updateLimit 
						: true;

				// Only process if distance with the previous point is greater than 100m or the update limit time is passed
				int dist = geocoder.calculateDistance(p1.getLat(), p1.getLon(), deviceLocation.getLat(), deviceLocation.getLon());

				// Calculate zone validity
				boolean isInValidZone = false; 
				for( GeoPointAdapter gp : validZones ) {
					int distZone = geocoder.calculateDistance(gp.getLat(), gp.getLon(), deviceLocation.getLat(), deviceLocation.getLon());
					if( distZone < gp.getMeassureDistance() ) {
						isInValidZone = true;
						break;
					}
				}
				
				if( !systemConfiguration.isTrackOnlyValidZones() || isInValidZone ) {
					if( updateLimitOverpassed || dist > 100 || ( StringUtils.hasText(userId) && !userId.equals(deviceLocation.getUserId()))) {

						// Try to find the nearest shopping to calculate City / Country
						try {
							AddressComponentsCache acc = geocoder.getAddressHLComponents(deviceLocation.getLat(), deviceLocation.getLon());
							deviceLocation.setCity(acc.getCity());
							deviceLocation.setProvince(acc.getProvince());
							deviceLocation.setCountry(acc.getCountry());
						} catch( ASException e ) {
							if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
								throw e;
							}
						}

						// GeoHash
						deviceLocation.setGeohash(geocoder.encodeGeohash(deviceLocation.getLat(), deviceLocation.getLon()));

						// Lock zones
						try {
							lockZones(deviceLocation.getLat(), deviceLocation.getLon(), deviceLocation.getDeviceUUID());
						} catch( ASException e ) {
							log.log(Level.SEVERE, e.getMessage(), e);
						}

						// Saves the deviceLocation ---------------------------------------------------------------------------------
						if( deviceLocation.getUserId() != null && (!systemConfiguration.isTrackOnlyValidDevices() || validDevices.contains(deviceLocation.getDeviceUUID()))) {

							// Saves the deviceInfo
							if( needsToUpdateDeviceInfo && di != null ) {
								try {
									if( newDevice ) {
										deviceInfoDao.create(di);
									} else {
										deviceInfoDao.update(di);
									}
								} catch( ASException e ) {
									log.log(Level.SEVERE, e.getMessage(), e);
								}
							}

							// Saves the deviceLocation
							try {
								if( newLocation ) {
									dao.create(deviceLocation);
								} else {
									dao.update(deviceLocation);
								}

								// And saves the deviceLocationHistory
								if(systemConfiguration.getSaveLocationHistory()) {
									DeviceLocationHistory dlh = dlhDao.build(deviceLocation);
									if( systemConfiguration.isEnqueueHistoryReplicableObjects()) {
										queueHelper.enqueueTransientInReplica(dlh);
									} else {						
										dlhDao.create(dlh);
									}
								}

								if( di != null && systemConfiguration.getDefaultBehavioursApps().contains(di.getAppId())) {
									hotspotService.add(deviceLocation.getDeviceUUID(), deviceLocation.getLat(), deviceLocation.getLon());
								} else if( di != null && StringUtils.hasText(di.getAppId())) {
									application = di.getAppId();
								}

							} catch( ASException e ) {
								log.log(Level.SEVERE, e.getMessage(), e);
							}
						}
					} else {
						log.log(Level.WARNING, "Not updating geo info... updateLimitOverpassed " + updateLimit + " and dist " + dist);
					}
				}

				// Prepares the return object
				final DeviceLocationAdapter adapter = new GenericAdapterImpl<DeviceLocationAdapter>().adapt(deviceLocation);
				adapter.setRequestInterval(systemConfiguration.getDeviceLocationRequestInterval());
				adapter.setReportInterval(systemConfiguration.getDeviceLocationReportInterval());

				adapter.setBeaconProximityUUID(systemConfiguration.getBeaconProximityUUID());
				if( deviceLocation.getCountry() != null ) {	
					GeoPoint p = geocoder.getGeoPoint(deviceLocation.getLat(), deviceLocation.getLon());
					String cacheKey = application + "_HotSpotAdapterList_" + p.getGeohash().substring(0, 5);
					List<HotSpotAdapter> bs = deserializeHotSpotList((String)localCache.get(cacheKey));
					if( bs != null ) {
						adapter.setNearSpots(bs);
					} else {
						Integer presition = systemConfiguration.getDefaultGeoEntityPresition();

						List<GeoEntity> geos = CollectionFactory.createList();
						geos.addAll(geoDao.getByProximity(p, EntityKind.KIND_SHOPPING, presition, true, false, true));
						geos.addAll(geoDao.getByProximity(p, EntityKind.KIND_STORE, presition, true, true, true));
						List<LocationAwareAdapter> adaptedGeos = CollectionFactory.createList();
						for( GeoEntity o : geos ) {
							LocationAwareAdapter a = new LocationAwareAdapter();
							a.setIdentifier(o.getEntityId());
							a.setKind(o.getEntityKind());
							a.setLat(o.getLat());
							a.setLon(o.getLon());
							a.setDistance(geocoder.calculateDistance(deviceLocation.getLat(), deviceLocation.getLon(), a.getLat(), a.getLon()));
							adaptedGeos.add(a);
						}
						Collections.sort(adaptedGeos, new DistanceComparator());

						List<String> geoIds = CollectionFactory.createList();
						for( int i = 0; i < adaptedGeos.size() && i < 10; i++ ) {
							LocationAwareAdapter ge = adaptedGeos.get(i);
							geoIds.add(ge.getIdentifier());
						}
						List<Shopping> hotSpots = shoppingDao.getUsingIdList(application.equals(CINEPOLIS_APP) ? systemConfiguration.getCinepolisHotspots() : geoIds);
						for( Shopping s : hotSpots ) {
							HotSpotAdapter spot = new HotSpotAdapter();
							spot.setIdentifier(s.getIdentifier());
							spot.setLat(s.getAddress().getLatitude());
							spot.setLon(s.getAddress().getLongitude());
							spot.setDistance(s.getFenceSize() != null ? s.getFenceSize() : systemConfiguration.getDefaultFenceSize());
							spot.setCheckinDistance(s.getCheckinAreaSize() != null ? s.getCheckinAreaSize() : systemConfiguration.getDefaultCheckinAreaSize());
							spot.setPointDistance(geocoder.calculateDistance(deviceLocation.getLat(), deviceLocation.getLon(), spot.getLat(), spot.getLon()));
							spot.setEntityKind(EntityKind.KIND_SHOPPING);
							if(geocoder.calculateDistance(deviceLocation.getLat(), deviceLocation.getLon(), spot.getLat(), spot.getLon()) < 30000) {
								adapter.getNearSpots().add(spot);
							}
						}
						List<Store> hotSpots2 = storeDao.getUsingIdList(application.equals(CINEPOLIS_APP) ? systemConfiguration.getCinepolisHotspots() : geoIds);
						for( Store s : hotSpots2 ) {
							HotSpotAdapter spot = new HotSpotAdapter();
							spot.setIdentifier(s.getIdentifier());
							spot.setLat(s.getAddress().getLatitude());
							spot.setLon(s.getAddress().getLongitude());
							spot.setDistance(s.getFenceSize() != null ? s.getFenceSize() : systemConfiguration.getDefaultFenceSize());
							spot.setCheckinDistance(s.getCheckinAreaSize() != null ? s.getCheckinAreaSize() : systemConfiguration.getDefaultCheckinAreaSize());
							spot.setPointDistance(geocoder.calculateDistance(deviceLocation.getLat(), deviceLocation.getLon(), spot.getLat(), spot.getLon()));
							spot.setEntityKind(EntityKind.KIND_STORE);
							adapter.getNearSpots().add(spot);
							if(geocoder.calculateDistance(deviceLocation.getLat(), deviceLocation.getLon(), spot.getLat(), spot.getLon()) < 30000) {
								adapter.getNearSpots().add(spot);
							}
						}

						if( application.equals(CINEPOLIS_APP)) {
							List<HotSpotAdapter> tmp = CollectionFactory.createList();
							tmp.addAll(adapter.getNearSpots());
							adapter.getNearSpots().clear();
							for( HotSpotAdapter hs : tmp ) {
								if( systemConfiguration.getCinepolisHotspots().contains(hs.getIdentifier())) {
									adapter.getNearSpots().add(hs);
								}
							}
						}

						try {
							localCache.put(cacheKey , serializeHotSpotList(adapter.getNearSpots()));
						} catch( Exception e ) {}
					}
				}

				// Dynamic timing adjustment
				int hour = Integer.parseInt(hourSDF.format(new Date()));
				if( hour >= 9 ) {
					HotSpotAdapter nearest = null;
					for( HotSpotAdapter spot : adapter.getNearSpots() ) {
						spot.setPointDistance(geocoder.calculateDistance(deviceLocation.getLat(), deviceLocation.getLon(), spot.getLat(), spot.getLon()));
						if( nearest == null || spot.getPointDistance() < nearest.getPointDistance() ) nearest = spot;
					}

					if( nearest != null ) {
						// Request Interval
						adapter.setRequestInterval(240000); // 4 mins
						if( nearest.getPointDistance() > 300 ) adapter.setRequestInterval(300000); // 5 mins
						if( nearest.getPointDistance() > 3000 ) adapter.setRequestInterval(600000); // 10 mins
						if( nearest.getPointDistance() > 5000 ) adapter.setRequestInterval(900000); // 15 mins
						if( nearest.getPointDistance() > 10000 ) adapter.setRequestInterval(1800000); // 30 mins

						// Wifi Interval
						if( nearest.getPointDistance() <= 300 ) adapter.setWifiInterval(120000); // 2 mins
						else adapter.setWifiInterval(3600000); // 1 hour

					}

					// Checkins
					if(!systemConfiguration.getDefaultBehavioursApps().contains(application)) {
						try {
							hotspotService.addWithKnownLocation(adapter, nearest);
						} catch( ASException e ) {
							if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_FORBIDDEN_CODE ) {
								// This means that the checkin time was exceded
								adapter.setRequestInterval(3600000); // 1 hour
								adapter.setReportInterval(3600000); // 1 hour
								adapter.setWifiInterval(3600000); // 1 hour
							}
						}
					}

				} else {
					adapter.setRequestInterval(3600000); // 1 hour
				}
				adapter.setReportInterval(adapter.getRequestInterval());
				adapter.setDwellTime(60000);

				// Obtains the user JSON representation
				final String[] fields = obtainOutputFields(bzFields, "all_public");
				returnValue = getJSONRepresentationFromObject(adapter, fields);

				return returnValue.toString();
			} else {
				log.log(Level.WARNING, "Null geo position information received...");
				return generateJSONOkResponse().toString();
			}

		} catch (JSONException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(e).toString();
		} finally {
			markEnd(start);
		}
	}

	private List<HotSpotAdapter> deserializeHotSpotList(String ser) {
		try {
			if( ser != null ) {
				List<HotSpotAdapter> ret = CollectionFactory.createList();
				JSONObject arr = new JSONObject(ser);
				JSONArray array = ((JSONArray)arr.get("list"));
				for( int i = 0; i < array.length(); i++ ) {
					String elem = array.getString(i);
					JSONObject jsonObj = new JSONObject(elem);
					HotSpotAdapter obj = new HotSpotAdapter();
					obj.setIdentifier(jsonObj.getString("identifier"));
					obj.setLat(jsonObj.getDouble("lat"));
					obj.setLon(jsonObj.getDouble("lon"));
					obj.setDistance(jsonObj.getInt("distance"));
					obj.setEntityKind(jsonObj.getInt("kind"));
					try {
						obj.setCheckinDistance(jsonObj.getInt("checkinDistance"));
					} catch( Exception e ) {}
					ret.add(obj);
				}
				return ret;
			} else {
				return null;
			}
		} catch( Exception e ) {
			return null;
		}
	}

	private String serializeHotSpotList(List<HotSpotAdapter> list) {
		try {
			List<String> array = CollectionFactory.createList(); 
			for( HotSpotAdapter obj : list ) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("identifier", obj.getIdentifier());
				jsonObj.put("lat", obj.getLat());
				jsonObj.put("lon", obj.getLon());
				jsonObj.put("distance", obj.getDistance());
				jsonObj.put("checkinDistance", obj.getCheckinDistance());
				jsonObj.put("kind", obj.getEntityKind());
				array.add(jsonObj.toString());
			}
			JSONObject ret = new JSONObject();
			ret.put("list", array);
			return ret.toString();
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}

	private void lockZones(double lat, double lon, String deviceId) throws ASException {
		String lockZonesString = systemConfiguration.getLockZones();
		String parts[] = lockZonesString.split(";");
		for( String part : parts ) {
			String components[] = part.split(",");
			if( components.length > 1 ) {
				double zoneLat = Double.parseDouble(components[0]);
				double zoneLon = Double.parseDouble(components[1]);
				int distance = geocoder.calculateDistance(zoneLat, zoneLon, lat, lon);
				if( distance < 50 ) {
					log.log(Level.WARNING, "device " + deviceId + " is beign locked!");
					// FIXME: Add subEntityId and subEntityKind
					lockHelper.deviceMessageLock(deviceId, DeviceMessageLock.SCOPE_GEO, null, new Date(), systemConfiguration.getDefaultProximityLock(), null, null);
					lockHelper.deviceMessageLock(deviceId, DeviceMessageLock.SCOPE_GLOBAL, null, new Date(), systemConfiguration.getDefaultProximityLock(), null, null);
				}
			}
		}
	}

	public class GeoPointAdapter extends GeoPoint {
		private double meassureDistance = 15000;
		public GeoPointAdapter(double lat, double lon, double meassureDistance) {
			super(lat, lon, "");
			this.setMeassureDistance(meassureDistance);
		}
		public double getMeassureDistance() {
			return meassureDistance;
		}
		public void setMeassureDistance(double meassureDistance) {
			this.meassureDistance = meassureDistance;
		}
	}

}
