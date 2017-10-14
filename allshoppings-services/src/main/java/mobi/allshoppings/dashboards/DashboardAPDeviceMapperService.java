package mobi.allshoppings.dashboards;

import java.sql.SQLException;
import java.text.ParseException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.DateUtils;
//import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;

import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.apdevice.APHHelper;
import mobi.allshoppings.dao.APDVisitDAO;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.APHEntryDAO;
import mobi.allshoppings.dao.DashboardIndicatorAliasDAO;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.dao.ExternalAPHotspotDAO;
import mobi.allshoppings.dao.FloorMapDAO;
import mobi.allshoppings.dao.FloorMapJourneyDAO;
import mobi.allshoppings.dao.InnerZoneDAO;
import mobi.allshoppings.dao.MacVendorDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.StoreItemDAO;
import mobi.allshoppings.dao.StoreRevenueDAO;
import mobi.allshoppings.dao.StoreTicketByHourDAO;
import mobi.allshoppings.dao.StoreTicketDAO;
import mobi.allshoppings.dao.WifiSpotDAO;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.model.DashboardIndicatorAlias;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.DeviceWifiLocationHistory;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.ExternalAPHotspot;
import mobi.allshoppings.model.FloorMap;
import mobi.allshoppings.model.FloorMapJourney;
import mobi.allshoppings.model.InnerZone;
import mobi.allshoppings.model.MacVendor;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreItem;
import mobi.allshoppings.model.StoreRevenue;
import mobi.allshoppings.model.StoreTicket;
import mobi.allshoppings.model.StoreTicketByHour;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.WifiSpot;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CollectionUtils;
import mobi.allshoppings.tools.GsonFactory;
import mobi.allshoppings.tools.Range;

public class DashboardAPDeviceMapperService {

	private static final Logger log = Logger.getLogger(DashboardAPDeviceMapperService.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * Entity Kind to use
	 */
	public static final long TWENTY_FOUR_HOURS = 86400000;

	private static final SimpleDateFormat dateSDF = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final Gson gson = GsonFactory.getInstance();
	private static final Calendar CALENDAR = Calendar.getInstance();

	/**
	 * DAOs 
	 */
	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private DashboardIndicatorDataDAO dao;
	@Autowired
	private DashboardIndicatorAliasDAO diAliasDao;
	@Autowired
	private ShoppingDAO shoppingDao;
	@Autowired
	private FloorMapDAO floorMapDao;
	@Autowired
	private WifiSpotDAO wifiSpotDao;
	@Autowired
	private APDeviceHelper apdHelper;
	@Autowired
	private APHHelper aphHelper;
	@Autowired
	private APHEntryDAO apheDao;
	@Autowired
	private APDeviceDAO apdDao;
	@Autowired
	private FloorMapJourneyDAO fmjDao;
	@Autowired
	private MacVendorDAO macVendorDao;
	@Autowired
	private APDVisitDAO apdvDao;
	@Autowired
	private StoreTicketDAO stDao;
	@Autowired
	private StoreTicketByHourDAO sthDao;
	@Autowired
	private StoreItemDAO siDao;
	@Autowired
	private StoreRevenueDAO srDao;
	@Autowired
	private InnerZoneDAO innerzoneDao;
	@Autowired
	private ExternalAPHotspotDAO eaphDao;

	@Autowired
	private SystemConfiguration systemConfiguration;

	private Map<String, Store> storeCache;
	private Map<String, Shopping> shoppingCache;
	private Map<String, WifiSpot> wifiSpotCache;
	private Map<String, FloorMap> floorMapCache;
	private Map<String, MacVendor> macVendorCache;
	private Map<String, InnerZone> zoneCache;

	// Phases
	public static final int PHASE_APDEVICE = 0;
	public static final int PHASE_WIFI_HEATMAP = 1;
	public static final int PHASE_APDEVICE_HEATMAP = 2;
	public static final int PHASE_FLOORMAP_TRACKING = 3;
	public static final int PHASE_APDVISIT = 4;
	public static final int PHASE_EXTERNAL_APDEVICE_HEATMAP = 5;

	// General Driver ----------------------------------------------------------------------------------------------------------------------------------------
	public void createDashboardDataForDays(String baseDir, Date fromDate, Date toDate, List<String> entityIds, List<Integer> phases) throws ASException {
		try {
			buildCaches(true);

			Date curDate = new Date(fromDate.getTime());
			while( curDate.before(toDate) || curDate.equals(toDate)) {
				
				if( CollectionUtils.isEmpty(phases) || phases.contains(PHASE_WIFI_HEATMAP))
					createHeatmapDashboardForDay(baseDir, curDate);

				if( CollectionUtils.isEmpty(phases) || phases.contains(PHASE_APDEVICE_HEATMAP))
					createAPDeviceHeatmapDashboardForDay(baseDir, curDate);

				if( CollectionUtils.isEmpty(phases) || phases.contains(PHASE_EXTERNAL_APDEVICE_HEATMAP))
					createExternalAPDeviceHeatmapDashboardForDay(entityIds, curDate);

				if( CollectionUtils.isEmpty(phases) || phases.contains(PHASE_FLOORMAP_TRACKING))
					createFloorMapTrackingForDay(curDate, entityIds);

				if( CollectionUtils.isEmpty(phases) || phases.contains(PHASE_APDVISIT))
					createAPDVisitPerformanceDashboardForDay(curDate, entityIds, null, null);

				curDate = new Date(curDate.getTime() + TWENTY_FOUR_HOURS);

			}

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getLocalizedMessage(), e);
		} finally {
			try {
				disposeCaches();
			} catch( Exception e ) {
				throw ASExceptionHelper.defaultException(e.getLocalizedMessage(), e);
			}
		}
	}

	// HeatMaps ---------------------------------------------------------------------------------------------------------------------------------------------
	public void createHeatmapDashboardForDay(String baseDir, Date date) throws ASException {

		log.log(Level.INFO, "Starting to create Heatmap Dashboard for Day " + date + "...");
		long startTime = new Date().getTime();

		Date processDate = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		Date limitDate = DateUtils.addDays(processDate, 1);

		log.log(Level.INFO, "ProcessDate " + processDate);
		log.log(Level.INFO, "LimitDate " + limitDate);

		// All Wifi Data
		DumperHelper<DeviceWifiLocationHistory> dumper = new DumpFactory<DeviceWifiLocationHistory>().build(baseDir, DeviceWifiLocationHistory.class);
		Iterator<DeviceWifiLocationHistory> i = dumper.iterator(processDate, limitDate);

		Map<String, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();
		while(i.hasNext()) {
			DeviceWifiLocationHistory location = i.next();
			try {

				// Checkin data
				DashboardIndicatorData obj;

				if( StringUtils.hasText(location.getWifiSpotId())) {
					WifiSpot wifiSpot = wifiSpotCache.get(location.getWifiSpotId());
					if(wifiSpot != null ) {
						FloorMap floorMap = floorMapCache.get(wifiSpot.getFloorMapId());
						if( floorMap != null ) {
							Shopping shopping = shoppingCache.get(floorMap.getShoppingId());
							if( shopping != null ) {

								String forDate = sdf.format(location.getCreationDateTime());
								TimeZone tz = TimeZone.getTimeZone(shopping.getTimezone());

								// heatmap ----------------------------------------------------------------------------------------------
								// ------------------------------------------------------------------------------------------------------
								obj = buildBasicDashboardIndicatorData(
										"heatmap", "Heat Map", wifiSpot.getIdentifier(),
										wifiSpot.getZoneName(), location.getCreationDateTime(),
										DashboardIndicatorData.PERIOD_TYPE_DAILY, shopping.getIdentifier(),
										null, shopping, floorMap.getFloor(), shopping.getIdentifier(), EntityKind.KIND_SHOPPING, tz, forDate);

								if(indicatorsSet.containsKey(obj.getKey().getName())) 
									obj = indicatorsSet.get(obj.getKey().getName());

								obj.setDoubleValue(obj.getDoubleValue() + 1);

								indicatorsSet.put(obj.getKey().getName(), obj);

								// heatmap ----------------------------------------------------------------------------------------------
								// ------------------------------------------------------------------------------------------------------
								obj = buildBasicDashboardIndicatorData(
										"heatmap_data", "Heat Map Data", wifiSpot.getIdentifier(),
										wifiSpot.getIdentifier(), location.getCreationDateTime(),
										DashboardIndicatorData.PERIOD_TYPE_DAILY, shopping.getIdentifier(),
										null, shopping, floorMap.getFloor(), shopping.getIdentifier(), EntityKind.KIND_SHOPPING, tz, forDate);
								obj.setSubentityId(wifiSpot.getFloorMapId());
								obj.setSubentityName(floorMap.getFloor());

								if(indicatorsSet.containsKey(obj.getKey().getName())) 
									obj = indicatorsSet.get(obj.getKey().getName());

								obj.setDoubleValue(obj.getDoubleValue() + 1);

								indicatorsSet.put(obj.getKey().getName(), obj);

							}
						}
					}
				}

			} catch( Exception e ) {
				if( !(e instanceof JSONException )) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}

		log.log(Level.INFO, "Starting Write Procedure...");

		// Finally, save all the information
		saveIndicatorSet(indicatorsSet);

		dumper.dispose();
		
		long endTime = new Date().getTime();
		log.log(Level.INFO, "Finished to create Heatmap Dashboard for Day " + date + " in " + (endTime - startTime) + "ms");

	}

	// HeatMaps ---------------------------------------------------------------------------------------------------------------------------------------------
	public void createAPDeviceHeatmapDashboardForDay(String baseDir, Date date) throws ASException {

		log.log(Level.INFO, "Starting to create APDevice Heatmap Dashboard for Day " + date + "...");
		long startTime = new Date().getTime();

		Date processDate = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		Date limitDate = DateUtils.addDays(processDate, 1);

		log.log(Level.INFO, "ProcessDate " + processDate);
		log.log(Level.INFO, "LimitDate " + limitDate);

		Map<String, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();

		for( String floorMapIdentifier : systemConfiguration.getFloorMapTracking()) {
			try {
				FloorMap floorMap = floorMapDao.get(floorMapIdentifier, true);
				List<WifiSpot> wifiSpotList = wifiSpotDao.getUsingFloorMapId(floorMap.getIdentifier());
				Store store = storeDao.get(floorMap.getShoppingId(), true);
				Shopping shopping = null;
				try { shopping = shoppingDao.get(floorMap.getShoppingId(), true); } catch( Exception e ) {}
				List<String> devices = CollectionFactory.createList();

				for( WifiSpot ws : wifiSpotList ) {
					if( StringUtils.hasText(ws.getApDevice())) {
						if( !devices.contains(ws.getApDevice()))
							devices.add(ws.getApDevice());
					}
				}

				// All Wifi Data
				DumperHelper<APHotspot> dumper = new DumpFactory<APHotspot>().build(baseDir, APHotspot.class);
				Iterator<JSONObject> i = dumper.jsonIterator(processDate, limitDate);

				while(i.hasNext()) {
					JSONObject json = i.next();
					try {
						if( devices.contains(json.getString("hostname")) && json.getInt("signalDB") > -60) {
							APHotspot hotspot = gson.fromJson(json.toString(), APHotspot.class);
							for( WifiSpot wifiSpot : wifiSpotList ) {
								if( wifiSpot.getZoneName().equals(hotspot.getHostname())) {

									// Checkin data
									DashboardIndicatorData obj;
									String forDate = sdf.format(hotspot.getCreationDateTime());
									TimeZone tz = store != null ? TimeZone.getTimeZone(store.getTimezone()) : TimeZone.getTimeZone(shopping.getTimezone());

									// heatmap ----------------------------------------------------------------------------------------------
									// ------------------------------------------------------------------------------------------------------
									obj = buildBasicDashboardIndicatorData(
											"heatmap", "Heat Map", wifiSpot.getIdentifier(),
											wifiSpot.getZoneName(), hotspot.getCreationDateTime(),
											DashboardIndicatorData.PERIOD_TYPE_DAILY, store.getIdentifier(),
											store, shopping, floorMap.getFloor(), store.getIdentifier(), EntityKind.KIND_SHOPPING, tz, forDate);

									if(indicatorsSet.containsKey(obj.getKey().getName())) 
										obj = indicatorsSet.get(obj.getKey().getName());

									obj.setDoubleValue(obj.getDoubleValue() + (100 - hotspot.getSignalDB()));

									indicatorsSet.put(obj.getKey().getName(), obj);

									// heatmap ----------------------------------------------------------------------------------------------
									// ------------------------------------------------------------------------------------------------------
									obj = buildBasicDashboardIndicatorData(
											"heatmap_data", "Heat Map Data", wifiSpot.getIdentifier(),
											wifiSpot.getIdentifier(), hotspot.getCreationDateTime(),
											DashboardIndicatorData.PERIOD_TYPE_DAILY, store.getIdentifier(),
											store, shopping, floorMap.getFloor(), store.getIdentifier(), EntityKind.KIND_SHOPPING, tz, forDate);
									obj.setSubentityId(wifiSpot.getFloorMapId());
									obj.setSubentityName(floorMap.getFloor());

									if(indicatorsSet.containsKey(obj.getKey().getName())) 
										obj = indicatorsSet.get(obj.getKey().getName());

									obj.setDoubleValue(obj.getDoubleValue() + (100 - hotspot.getSignalDB()));

									indicatorsSet.put(obj.getKey().getName(), obj);

								}
							}
						}

					} catch( Exception e ) {
						if( !(e instanceof JSONException )) {
							log.log(Level.SEVERE, e.getMessage(), e);
						}
					}
				}

				dumper.dispose();

			} catch( Exception e ) {
				// Assuming not found... do nothing
			}
		}

		log.log(Level.INFO, "Starting Write Procedure...");

		// Finally, save all the information
		saveIndicatorSet(indicatorsSet);


		long endTime = new Date().getTime();
		log.log(Level.INFO, "Finished to create APDevice Heatmap Dashboard for Day " + date + " in " + (endTime - startTime) + "ms");

	}

	// External APD Heatmap
	public void createExternalAPDeviceHeatmapDashboardForDay(List<String> entityIds, Date date) throws ASException {

		log.log(Level.INFO, "Starting to create External APDevice Heatmap Dashboard for Day " + date + "...");
		long startTime = new Date().getTime();

		Date processDate = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		Date limitDate = DateUtils.addDays(processDate, 1);

		log.log(Level.INFO, "ProcessDate " + processDate);
		log.log(Level.INFO, "LimitDate " + limitDate);

		Map<String, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();

		List<String> pList = CollectionFactory.createList();
		
		if( entityIds == null || entityIds.isEmpty())
			pList.addAll(floorMapCache.keySet());
		else
			pList.addAll(entityIds);

		for( String floorMapIdentifier : pList) {
			try {
				FloorMap floorMap = floorMapDao.get(floorMapIdentifier, true);
				List<WifiSpot> wifiSpotList = wifiSpotDao.getUsingFloorMapId(floorMap.getIdentifier());
				Shopping shopping = null;
				Store store = null;
				int entityKind = EntityKind.KIND_SHOPPING;

				try {
					shopping = shoppingDao.get(floorMap.getShoppingId(), true);
					entityKind = EntityKind.KIND_SHOPPING;
				} catch( Exception e ) {}

				try {
					store = storeDao.get(floorMap.getShoppingId(), true);
					entityKind = EntityKind.KIND_STORE;
				} catch( Exception e ) {}

				List<String> devices = CollectionFactory.createList();

				for( WifiSpot ws : wifiSpotList ) {
					if( StringUtils.hasText(ws.getApDevice())) {
						if( !devices.contains(ws.getApDevice()))
							devices.add(ws.getApDevice());
					}
				}

				if((shopping != null || store != null) && !devices.isEmpty()) {

					// All Wifi Data
					List<ExternalAPHotspot> list = eaphDao.getUsingHostnameAndDates(devices, processDate, limitDate);
					for( ExternalAPHotspot hotspot : list ) {

						for( WifiSpot wifiSpot : wifiSpotList ) {
							if( wifiSpot.getApDevice().equals(hotspot.getHostname())) {

								// Checkin data
								DashboardIndicatorData obj;
								String forDate = sdf.format(hotspot.getCreationDateTime());
								TimeZone tz = store != null ? TimeZone.getTimeZone(store.getTimezone()) : TimeZone.getTimeZone(shopping.getTimezone());

								// heatmap ----------------------------------------------------------------------------------------------
								// ------------------------------------------------------------------------------------------------------
								if( EntityKind.KIND_SHOPPING == entityKind ) {
									obj = buildBasicDashboardIndicatorData(
											"heatmap", "Heat Map", wifiSpot.getIdentifier(),
											wifiSpot.getZoneName(), hotspot.getCreationDateTime(),
											DashboardIndicatorData.PERIOD_TYPE_DAILY, shopping.getIdentifier(),
											null, shopping, floorMap.getFloor(), shopping.getIdentifier(), EntityKind.KIND_SHOPPING, tz, forDate);
								} else {
									obj = buildBasicDashboardIndicatorData(
											"heatmap", "Heat Map", wifiSpot.getIdentifier(),
											wifiSpot.getZoneName(), hotspot.getCreationDateTime(),
											DashboardIndicatorData.PERIOD_TYPE_DAILY, store.getIdentifier(),
											store, shopping, floorMap.getFloor(), store.getIdentifier(), EntityKind.KIND_SHOPPING, tz, forDate);
								}

								if(indicatorsSet.containsKey(obj.getKey().getName())) 
									obj = indicatorsSet.get(obj.getKey().getName());

								obj.setDoubleValue(obj.getDoubleValue() + (100 - hotspot.getSignalDB()));

								indicatorsSet.put(obj.getKey().getName(), obj);

								// heatmap ----------------------------------------------------------------------------------------------
								// ------------------------------------------------------------------------------------------------------
								if( EntityKind.KIND_SHOPPING == entityKind ) {
									obj = buildBasicDashboardIndicatorData(
											"heatmap_data", "Heat Map Data", wifiSpot.getIdentifier(),
											wifiSpot.getIdentifier(), hotspot.getCreationDateTime(),
											DashboardIndicatorData.PERIOD_TYPE_DAILY, shopping.getIdentifier(),
											null, shopping, floorMap.getFloor(), shopping.getIdentifier(), EntityKind.KIND_SHOPPING, tz, forDate);
								} else {
									obj = buildBasicDashboardIndicatorData(
											"heatmap_data", "Heat Map Data", wifiSpot.getIdentifier(),
											wifiSpot.getIdentifier(), hotspot.getCreationDateTime(),
											DashboardIndicatorData.PERIOD_TYPE_DAILY, store.getIdentifier(),
											store, shopping, floorMap.getFloor(), store.getIdentifier(), EntityKind.KIND_SHOPPING, tz, forDate);
								}
								obj.setSubentityId(wifiSpot.getFloorMapId());
								obj.setSubentityName(floorMap.getFloor());

								if(indicatorsSet.containsKey(obj.getKey().getName())) 
									obj = indicatorsSet.get(obj.getKey().getName());

								obj.setDoubleValue(obj.getDoubleValue() + (100 - hotspot.getSignalDB()));

								indicatorsSet.put(obj.getKey().getName(), obj);

							}
						}
					}
				}
			} catch( Exception e ) {
				if( !(e instanceof JSONException )) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}

		log.log(Level.INFO, "Starting Write Procedure...");

		// Finally, save all the information
		saveIndicatorSet(indicatorsSet);

		long endTime = new Date().getTime();
		log.log(Level.INFO, "Finished to create APDevice Heatmap Dashboard for Day " + date + " in " + (endTime - startTime) + "ms");

	}


	// Floor Map Tracking --------------------------------------------------------------------------------------------------------------------------------
	public void createFloorMapTrackingForDay(Date date, List<String> entityIds) throws ASException {

		log.log(Level.INFO, "Starting to create Floor Map Tracking Dashboard for Day " + date + "...");
		long startTime = new Date().getTime();

		Date processDate = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		Date limitDate = DateUtils.addDays(processDate, 1);

		log.log(Level.INFO, "ProcessDate " + processDate);
		log.log(Level.INFO, "LimitDate " + limitDate);

		List<String> pList = CollectionFactory.createList();
		if( entityIds == null || entityIds.isEmpty())
			pList.addAll(systemConfiguration.getFloorMapTracking());
		else
			pList.addAll(entityIds);
		
		try {
			for( String floorMapIdentifier : pList ) {
				FloorMap fm = floorMapDao.get(floorMapIdentifier, true);
				Map<String, WifiSpot> apMap = CollectionFactory.createMap();

				Map<String, APDevice> apDevices = CollectionFactory.createMap();
				List<String> apDeviceIds = CollectionFactory.createList();
				List<WifiSpot> list = wifiSpotDao.getUsingFloorMapId(fm.getIdentifier());
				for( WifiSpot ws : list ) {
					if( StringUtils.hasText(ws.getApDevice())) {
						try {
							apDevices.put(ws.getApDevice(), apdDao.get(ws.getApDevice(), true));
							apMap.put(ws.getApDevice(), ws);
							apDeviceIds.add(ws.getApDevice());
						} catch(ASException e ) {
							// Assuming not found... do nothing
						}
					}
				}

				List<String> macs = apheDao.getMacsUsingHostnameAndDates(apDeviceIds, date, date);
				log.log(Level.INFO, "recovered " + macs.size() + " macs");

				long count = 0;
				
				for( String mac : macs ) {
					
					if( count % 100 == 0 )
						log.log(Level.INFO, "Processing record " + count + " of " + macs.size() + "...");
					
					count++;
//					if( count > 100 ) break;
					
					List<APHEntry> aphes = CollectionFactory.createList();
					for( String apDeviceId : apDeviceIds) {
						try { aphes.add(apheDao.get(aphHelper.getHash(apDeviceId, mac, date ), true)); } catch( Exception e ) {}
					}
					
					// Find the minimal slot time for this mac address.
					// It means, the first time that this mac was saw for any of the APDevices
					int minimalSlot = 99999;
					int maximalSlot = 0;
					int maxDataCount = 0;
					int maxVisitTimeThreshold = 0;
					boolean valid = false;
					for( APHEntry entry : aphes ) {
						try {
							List<Integer> arr = aphHelper.timeslotToList(entry.getArtificialRssi());
							// Search for minimal slot
							int myMinimalSlot = arr.get(0);
							if( myMinimalSlot < minimalSlot )
								minimalSlot = myMinimalSlot;

							// Search for maximal slot
							int myMaximalSlot = arr.get(arr.size() -1);
							if( myMaximalSlot > maximalSlot )
								maximalSlot = myMaximalSlot;

							// Search for dataCount
							if( entry.getDataCount() > maxDataCount )
								maxDataCount = entry.getDataCount();
							
							// Search for max visit time threshold
							APDevice apd = apDevices.get(entry.getHostname());
							if( apd.getVisitMaxThreshold() > maxVisitTimeThreshold)
								maxVisitTimeThreshold = apd.getVisitMaxThreshold().intValue();
							
							// Try basic rules
							int distance = (int)((myMaximalSlot - myMinimalSlot) / 3);
							if( entry.getMaxRssi() >= apd.getVisitPowerThreshold())
								if( distance >= apd.getVisitTimeThreshold())
									if( distance <= apd.getVisitMaxThreshold() )
										valid = true;

						} catch( Exception e ) {
							// no element found
						}
					}

					// Discard if visit is too long
					if( maxDataCount / 3 > maxVisitTimeThreshold)
						valid = false;
					
					if( valid ) {
						// Create the final journey
						FloorMapJourney journey = new FloorMapJourney();
						journey.setDate(dateSDF.format(date));
						journey.setMac(mac);
						journey.setFloorMapId(fm.getIdentifier());
						journey.setKey(fmjDao.createKey(journey));

						// Navigate through the time slots, from minimum to maximum slot
						for( int i = minimalSlot; i <= maximalSlot; i++ ) {

							// And for each slot, try to find the closest APDevice
							String position = null;
							int signal = -999999;

							for( APHEntry entry : aphes ) {
								Integer val = entry.getArtificialRssi().get(String.valueOf(i)); 
								if( null != val && val > signal ) {
									signal = val;
									position = entry.getHostname();
								}
							}

							if( null != position && null != apMap.get(position) && null != journey.getWifiPoints() )
								journey.getWifiPoints().put(String.valueOf(i), apMap.get(position).getIdentifier());

						}

						// Add word for patter porpouses
						List<Integer> times = aphHelper.timeslotToList(journey.getWifiPoints());
						for(Integer slot : times) {
							String wifiSpotId = journey.getWifiPoints().get(String.valueOf(slot));
							if( null != wifiSpotId ) {
								String chr = wifiSpotCache.get(wifiSpotId).getWordAlias();
								if(journey.getWord().size() == 0 
										|| !journey.getWord().get(journey.getWord().size() - 1).equals(chr))
									journey.getWord().add(chr);
							}
						}
						journey.setDataCount(journey.getWifiPoints().size());
						journey.setWordLength(journey.getWord().size());
						
						// Creates the user journey
						if( journey.getWordLength() > 1 ) {
							try {
								fmjDao.create(journey);
							} catch( Exception e ) {
								log.log(Level.WARNING, e.getMessage(), e);
							}
						}
					}
					
				}
			}
			
			
			log.log(Level.INFO, "Starting Write Procedure...");

			long endTime = new Date().getTime();
			log.log(Level.INFO, "Finished to create Floor Map Tracking Dashboard for Day " + date + " in " + (endTime - startTime) + "ms");

		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getLocalizedMessage(), e);
		}
	}

	// apd_visitor performance -------------------------------------------------------------------------------------------------------------------------

	public void createAPDVisitPerformanceDashboardForDay(Date date, List<String> entityIds,
			Integer entityKind, List<APDVisit> data) throws ASException {

		log.log(Level.INFO, "Starting to create apd_visitor Performance Dashboard for Day " + date + "...");
		long startTime = System.currentTimeMillis();
		CALENDAR.setTime(date);
		Date processDate = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		Date limitDate = DateUtils.addDays(processDate, 1);

		log.log(Level.INFO, "ProcessDate " + processDate);
		log.log(Level.INFO, "LimitDate " + limitDate);

		try {

			// Prepares the Object Query
			Date dateFrom = CALENDAR.getTime();
			Date dateTo = new Date(dateFrom.getTime() + TWENTY_FOUR_HOURS);
			Range range = null;
			String entityId = null;
			if( !CollectionUtils.isEmpty( entityIds ))
				entityId = entityIds.get(0);
			String shoppingId = null;
			String subentityId = null;
			
			// Looks for all visit records
			Map<String, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();
			// FIXME: Be aware to get the APDV Records from Dump and not from Database!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
			List<APDVisit> list = (data == null || data.size() == 0) ?
					apdvDao.getUsingEntityIdAndEntityKindAndDate(entityId, entityKind, dateFrom, dateTo,
							null, range, null, null, false) : data;
			log.log(Level.INFO, list.size() + " records to process... ");
			for(APDVisit v : list ) {

				try {
					Store store = null;
					Shopping shopping = null;
					InnerZone zone = null;
					TimeZone tz = null;
					
					if( entityKind == null ) entityKind = EntityKind.KIND_BRAND;
					if( entityKind.equals(EntityKind.KIND_STORE)) entityKind = EntityKind.KIND_BRAND;
					
					if( entityKind.equals(EntityKind.KIND_BRAND)) {
						store = storeCache.get(String.valueOf(v.getEntityId()));
						if( store == null ) {
							store = storeDao.get(String.valueOf(v.getEntityId()), true);
							if( store == null ) return;
							storeCache.put(String.valueOf(v.getEntityId()), store);
							tz = TimeZone.getTimeZone(store.getTimezone());
						}
						tz = TimeZone.getTimeZone(store.getTimezone());
						entityId = store.getBrandId();
						shoppingId = store.getShoppingId();
						subentityId = store.getIdentifier();
					} else if( entityKind.equals(EntityKind.KIND_SHOPPING)) {
						shopping = shoppingCache.get(String.valueOf(v.getEntityId()));
						entityId = shopping.getIdentifier();
						shoppingId = shopping.getIdentifier();
						subentityId = shopping.getIdentifier();
						tz = TimeZone.getTimeZone(shopping.getTimezone());
					} else if( entityKind.equals(EntityKind.KIND_INNER_ZONE)) {
						zone = innerzoneDao.get(entityId);
						shoppingId = entityId;
						subentityId = entityId;
						tz = getTimezoneForEntity(entityId, EntityKind.KIND_INNER_ZONE);
					}
					CALENDAR.setTimeZone(tz);// TODO ponerlo arriba
					
					if( store != null || shopping != null || zone != null ) {
						DashboardIndicatorData obj;

						// visitor_total_records --------------------------------------------------------------------------------
						// ------------------------------------------------------------------------------------------------------
						obj = buildBasicDashboardIndicatorData(
								"apd_visitor", "Visitantes", "visitor_total_records",
								"Total", v.getCheckinStarted(),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
								store, shopping, null, entityId, entityKind, tz, v.getForDate());
						
						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());
						obj.setDoubleValue(obj.getDoubleValue() + 1);
						indicatorsSet.put(obj.getKey().getName(), obj);

						// Device selector
						if( v.getDevicePlatform() != null ) {
							if( v.getDevicePlatform().equalsIgnoreCase("ios")) {
								obj = buildBasicDashboardIndicatorData(
										"apd_visitor", "Visitantes", "visitor_total_records_ios",
										"Total iOS", v.getCheckinStarted(),
										DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
										store, shopping, null, entityId, entityKind, tz, v.getForDate());

								if(indicatorsSet.containsKey(obj.getKey().getName())) 
									obj = indicatorsSet.get(obj.getKey().getName());
								obj.setDoubleValue(obj.getDoubleValue() + 1);
								indicatorsSet.put(obj.getKey().getName(), obj);
							} else if( v.getDevicePlatform().equalsIgnoreCase("android")) {
								obj = buildBasicDashboardIndicatorData(
										"apd_visitor", "Visitantes", "visitor_total_records_android",
										"Total Android", v.getCheckinStarted(),
										DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
										store, shopping, null, entityId, entityKind, tz, v.getForDate());

								if(indicatorsSet.containsKey(obj.getKey().getName())) 
									obj = indicatorsSet.get(obj.getKey().getName());
								obj.setDoubleValue(obj.getDoubleValue() + 1);
								indicatorsSet.put(obj.getKey().getName(), obj);
							}
						}

						if( v.getCheckinType().equals(APDVisit.CHECKIN_PEASANT) ) {

							// visitor_total_peasents -------------------------------------------------------------------------------
							// ------------------------------------------------------------------------------------------------------
							obj = buildBasicDashboardIndicatorData(
									"apd_visitor", "Visitantes", "visitor_total_peasents",
									"Paseantes", v.getCheckinStarted(),
									DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
									store, shopping, null, entityId, entityKind, tz, v.getForDate());

							if(indicatorsSet.containsKey(obj.getKey().getName())) 
								obj = indicatorsSet.get(obj.getKey().getName());
							obj.setDoubleValue(obj.getDoubleValue() + 1);
							indicatorsSet.put(obj.getKey().getName(), obj);

							// Device selector
							if( v.getDevicePlatform() != null ) {
								if( v.getDevicePlatform().equalsIgnoreCase("ios")) {
									obj = buildBasicDashboardIndicatorData(
											"apd_visitor", "Visitantes", "visitor_total_peasents_ios",
											"Paseantes iOS", v.getCheckinStarted(),
											DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
											store, shopping, null, entityId, entityKind, tz, v.getForDate());

									if(indicatorsSet.containsKey(obj.getKey().getName())) 
										obj = indicatorsSet.get(obj.getKey().getName());
									obj.setDoubleValue(obj.getDoubleValue() + 1);
									indicatorsSet.put(obj.getKey().getName(), obj);
								} else if( v.getDevicePlatform().equalsIgnoreCase("android")) {
									obj = buildBasicDashboardIndicatorData(
											"apd_visitor", "Visitantes", "visitor_total_peasents_android",
											"Paseantes Android", v.getCheckinStarted(),
											DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
											store, shopping, null, entityId, entityKind, tz, v.getForDate());

									if(indicatorsSet.containsKey(obj.getKey().getName())) 
										obj = indicatorsSet.get(obj.getKey().getName());
									obj.setDoubleValue(obj.getDoubleValue() + 1);
									indicatorsSet.put(obj.getKey().getName(), obj);
								}
							}

							if( true != v.getHidePermanence() ) {

								// permanence_hourly_peasents ---------------------------------------------------------------------------
								// ------------------------------------------------------------------------------------------------------
								obj = buildBasicDashboardIndicatorData(
										"apd_permanence", "Permanencia", "permanence_hourly_peasents",
										"Paseantes", v.getCheckinStarted(),
										DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
										store, shopping, null, entityId, entityKind, tz, v.getForDate());

								if(indicatorsSet.containsKey(obj.getKey().getName())) 
									obj = indicatorsSet.get(obj.getKey().getName());
								obj.setDoubleValue(obj.getDoubleValue()
										+ calculateDiffTime(v.getCheckinFinished(), v.getCheckinStarted()));
								obj.setRecordCount(obj.getRecordCount() + 1);
								indicatorsSet.put(obj.getKey().getName(), obj);

								// Device selector
								if( v.getDevicePlatform() != null ) {
									if( v.getDevicePlatform().equalsIgnoreCase("ios")) {
										obj = buildBasicDashboardIndicatorData(
												"apd_permanence", "Permanencia", "permanence_hourly_peasents_ios",
												"Paseantes iOS", v.getCheckinStarted(),
												DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
												store, shopping, null, entityId, entityKind, tz, v.getForDate());

										if(indicatorsSet.containsKey(obj.getKey().getName())) 
											obj = indicatorsSet.get(obj.getKey().getName());
										obj.setDoubleValue(obj.getDoubleValue()
												+ calculateDiffTime(v.getCheckinFinished(),
														v.getCheckinStarted()));
										obj.setRecordCount(obj.getRecordCount() + 1);
										indicatorsSet.put(obj.getKey().getName(), obj);
									} else if( v.getDevicePlatform().equalsIgnoreCase("android")) {
										obj = buildBasicDashboardIndicatorData(
												"apd_permanence", "Permanencia", "permanence_hourly_peasents_android",
												"Paseantes Android", v.getCheckinStarted(),
												DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
												store, shopping, null, entityId, entityKind, tz, v.getForDate());

										if(indicatorsSet.containsKey(obj.getKey().getName())) 
											obj = indicatorsSet.get(obj.getKey().getName());
										obj.setDoubleValue(obj.getDoubleValue()
												+ calculateDiffTime(v.getCheckinFinished(),
														v.getCheckinStarted()));
										obj.setRecordCount(obj.getRecordCount() + 1);
										indicatorsSet.put(obj.getKey().getName(), obj);
									}
								}
							}

							// occupation_total_peasents -------------------------------------------------------------------------------
							// ------------------------------------------------------------------------------------------------------
							Calendar init = Calendar.getInstance();
							init.setTime(v.getCheckinStarted());
							Calendar finish = Calendar.getInstance();
							finish.setTime(v.getCheckinFinished());
							while (init.get(Calendar.HOUR_OF_DAY) <= finish.get(Calendar.HOUR_OF_DAY) &&
									init.get(Calendar.HOUR_OF_DAY) < 23) {
								obj = buildBasicDashboardIndicatorData(
										"apd_occupation", "Ocupacion", "occupation_hourly_peasants",
										"Paseantes", init.getTime(),
										DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
										store, shopping, null, entityId, entityKind, tz, v.getForDate());

								if(indicatorsSet.containsKey(obj.getKey().getName())) 
									obj = indicatorsSet.get(obj.getKey().getName());
								obj.setDoubleValue(obj.getDoubleValue() + 1);
								indicatorsSet.put(obj.getKey().getName(), obj);
								
								init.set(Calendar.HOUR_OF_DAY, init.get(Calendar.HOUR_OF_DAY) + 1);
								
							}

						} else if( v.getCheckinType().equals(APDVisit.CHECKIN_VISIT) ) {

							// visitor_total_visits ---------------------------------------------------------------------------------
							// ------------------------------------------------------------------------------------------------------
							obj = buildBasicDashboardIndicatorData(
									"apd_visitor", "Visitantes", "visitor_total_visits",
									"Visitas", v.getCheckinStarted(),
									DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
									store, shopping, null, entityId, entityKind, tz, v.getForDate());

							if(indicatorsSet.containsKey(obj.getKey().getName())) 
								obj = indicatorsSet.get(obj.getKey().getName());
							obj.setDoubleValue(obj.getDoubleValue() + 1);
							indicatorsSet.put(obj.getKey().getName(), obj);

							// Device selector
							if( v.getDevicePlatform() != null ) {
								if( v.getDevicePlatform().equalsIgnoreCase("ios")) {
									obj = buildBasicDashboardIndicatorData(
											"apd_visitor", "Visitantes", "visitor_total_visits_ios",
											"Visitas iOS", v.getCheckinStarted(),
											DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
											store, shopping, null, entityId, entityKind, tz, v.getForDate());

									if(indicatorsSet.containsKey(obj.getKey().getName())) 
										obj = indicatorsSet.get(obj.getKey().getName());
									obj.setDoubleValue(obj.getDoubleValue() + 1);
									indicatorsSet.put(obj.getKey().getName(), obj);
								} else if( v.getDevicePlatform().equalsIgnoreCase("android")) {
									obj = buildBasicDashboardIndicatorData(
											"apd_visitor", "Visitantes", "visitor_total_visits_android",
											"Visitas Android", v.getCheckinStarted(),
											DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
											store, shopping, null, entityId, entityKind, tz, v.getForDate());

									if(indicatorsSet.containsKey(obj.getKey().getName())) 
										obj = indicatorsSet.get(obj.getKey().getName());
									obj.setDoubleValue(obj.getDoubleValue() + 1);
									indicatorsSet.put(obj.getKey().getName(), obj);
								}
							}

							if( true != v.getHidePermanence()) {
								// permanence_hourly_visits -------------------------------------------------------------------------------
								// ------------------------------------------------------------------------------------------------------
								obj = buildBasicDashboardIndicatorData(
										"apd_permanence", "Permanencia", "permanence_hourly_visits",
										"Visitas", v.getCheckinStarted(),
										DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
										store, shopping, null, entityId, entityKind, tz, v.getForDate());

								if(indicatorsSet.containsKey(obj.getKey().getName())) 
									obj = indicatorsSet.get(obj.getKey().getName());
								obj.setDoubleValue(obj.getDoubleValue()
										+ calculateDiffTime(v.getCheckinFinished(), v.getCheckinStarted()));
								obj.setRecordCount(obj.getRecordCount() + 1);
								indicatorsSet.put(obj.getKey().getName(), obj);

								// Device selector
								if( v.getDevicePlatform() != null ) {
									if( v.getDevicePlatform().equalsIgnoreCase("ios")) {
										obj = buildBasicDashboardIndicatorData(
												"apd_permanence", "Permanencia", "permanence_hourly_visits_ios",
												"Visitas iOS", v.getCheckinStarted(),
												DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
												store, shopping, null, entityId, entityKind, tz, v.getForDate());

										if(indicatorsSet.containsKey(obj.getKey().getName())) 
											obj = indicatorsSet.get(obj.getKey().getName());
										obj.setDoubleValue(obj.getDoubleValue()
												+ calculateDiffTime(v.getCheckinFinished(),
														v.getCheckinStarted()));
										obj.setRecordCount(obj.getRecordCount() + 1);
										indicatorsSet.put(obj.getKey().getName(), obj);
									} else if( v.getDevicePlatform().equalsIgnoreCase("android")) {
										obj = buildBasicDashboardIndicatorData(
												"apd_permanence", "Permanencia",
												"permanence_hourly_visits_android", "Visitas Android",
												v.getCheckinStarted(),
												DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
												store, shopping, null, entityId, entityKind, tz,
												v.getForDate());

										if(indicatorsSet.containsKey(obj.getKey().getName())) 
											obj = indicatorsSet.get(obj.getKey().getName());
										obj.setDoubleValue(obj.getDoubleValue()
												+ calculateDiffTime(v.getCheckinFinished(),
														v.getCheckinStarted()));
										obj.setRecordCount(obj.getRecordCount() + 1);
										indicatorsSet.put(obj.getKey().getName(), obj);
									}
								}
							}
							// occupation_total_visits ---------------------------------------------------------------------------------
							// ------------------------------------------------------------------------------------------------------
							
							Calendar init = Calendar.getInstance();
							init.setTime(v.getCheckinStarted());
							Calendar finish = Calendar.getInstance();
							finish.setTime(v.getCheckinFinished());
							while (init.get(Calendar.HOUR_OF_DAY) <= finish.get(Calendar.HOUR_OF_DAY) &&
									init.get(Calendar.HOUR_OF_DAY) < 23) {
								obj = buildBasicDashboardIndicatorData(
										"apd_occupation", "Ocupacion", "occupation_hourly_visits", 
										"Visitas", init.getTime(),
										DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId,
										store, shopping, null, entityId, entityKind, tz, v.getForDate());

								if(indicatorsSet.containsKey(obj.getKey().getName())) 
									obj = indicatorsSet.get(obj.getKey().getName());
								obj.setDoubleValue(obj.getDoubleValue() + 1);
								indicatorsSet.put(obj.getKey().getName(), obj);
								
								init.set(Calendar.HOUR_OF_DAY, init.get(Calendar.HOUR_OF_DAY) + 1);
									
							}
						}
					} else {
						// Store not found
						log.log(Level.INFO, "Entity with id " + v.getEntityId() + " not found!");
					}

				} catch( ASException e ) {
					// Store not found
					log.log(Level.INFO, "Entity with id " + v.getEntityId() + " not found!");
				}

			}
			// Looks for ticket
			if( null != entityKind ) {
				if( entityKind.equals(EntityKind.KIND_BRAND)) {
					createStoreTicketDataForDates(sdf.format(date), sdf.format(date), subentityId);
					createStoreItemDataForDates(sdf.format(date), sdf.format(date), subentityId);
					createStoreRevenueDataForDates(sdf.format(date), sdf.format(date), subentityId);
				}
				if( entityKind.equals(EntityKind.KIND_STORE)) {
					createStoreTicketDataForDates(sdf.format(date), sdf.format(date), entityId);
					createStoreItemDataForDates(sdf.format(date), sdf.format(date), entityId);
					createStoreRevenueDataForDates(sdf.format(date), sdf.format(date), entityId);
				}
			}
			
			log.log(Level.INFO, "Starting Write Procedure...");

			// Finally, save all the information
			saveIndicatorSet(indicatorsSet);

			long endTime = new Date().getTime();
			log.log(Level.INFO, "Finished to create apd_visitor Performance Dashboard for Day " + date + " in " + (endTime - startTime) + "ms");

		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getLocalizedMessage(), e);
		}
	}

	// General utilities ---------------------------------------------------------------------------------------------------------------------------
	public double calculateDiffTime(Date quit, Date enter) {
		return (quit.getTime() - enter.getTime());
	}

	public void saveIndicatorAliasSet(List<DashboardIndicatorAlias> aliases) throws ASException {
		for( DashboardIndicatorAlias alias : aliases ) {
			try {
				diAliasDao.delete(alias.getIdentifier());
			} catch( Exception e ) {}

			diAliasDao.create(alias);
		}
	}

	public void saveIndicatorSet(Map<String, DashboardIndicatorData> indicatorsSet) throws ASException {

		List<DashboardIndicatorAlias> aliases = createAliasList(indicatorsSet);
		saveIndicatorAliasSet(aliases);

		Iterator<String> x = indicatorsSet.keySet().iterator();
		while(x.hasNext()) {
			String key = x.next();

			try {
				dao.delete(key);
			} catch( Exception e ) {}
		}
		
		List<DashboardIndicatorData> values = CollectionFactory.createList();
		values.addAll(indicatorsSet.values());
		dao.createOrUpdate(null, values, true);
	}

	public List<DashboardIndicatorAlias> createAliasList(Map<String, DashboardIndicatorData> indicatorsSet) throws ASException {
		List<DashboardIndicatorAlias> aliases = CollectionFactory.createList();

		Iterator<String> x = indicatorsSet.keySet().iterator();
		while(x.hasNext()) {
			String key = x.next();
			DashboardIndicatorData data = indicatorsSet.get(key);
			DashboardIndicatorAlias alias = new DashboardIndicatorAlias(
					data.getEntityId(), data.getEntityKind(),
					data.getScreenName(), data.getElementId(),
					data.getElementName(), data.getElementSubId(),
					data.getElementSubName(), data.getSubentityId(),
					data.getSubentityName());
			alias.setKey(diAliasDao.createKey(alias));
			if(!aliases.contains(alias)) aliases.add(alias);

			alias = new DashboardIndicatorAlias(
					data.getEntityId(), data.getEntityKind(),
					data.getScreenName(), data.getElementId().replace("ticket_", "promo_"),
					data.getElementName(), data.getElementSubId(),
					data.getElementSubName(), data.getSubentityId(),
					data.getSubentityName());
			alias.setKey(diAliasDao.createKey(alias));
			if(!aliases.contains(alias)) aliases.add(alias);
		}

		return aliases;
	}
	
	
	public void createStoreTicketDataForDates(String fromDate, String toDate, String storeId) throws ASException,ParseException{
		
		log.log(Level.INFO, "Starting to create store tickets Dashboard for Day " + fromDate + " to: " + toDate +"..." );
		long startTime = new Date().getTime();
		
		try {
			Map<String, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();
			List<StoreTicket> tickets =  stDao.getUsingStoreIdAndDatesAndRange(storeId, fromDate, toDate, null, null, false);
			
			Store store = storeDao.get(storeId);
			if( store != null ) {
				
				for( StoreTicket ticket: tickets){
					
					DashboardIndicatorData obj;
					String forDate = ticket.getDate();
					TimeZone tz = TimeZone.getTimeZone(store.getTimezone());
					
					// visitor_total_tickets --------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					
					obj = buildBasicDashboardIndicatorData(
							"apd_visitor", "Visitantes", "visitor_total_tickets",
							"Tickets", sdf.parse(ticket.getDate()),
							DashboardIndicatorData.PERIOD_TYPE_DAILY, store.getShoppingId(),
							store, null, null, store.getBrandId(), EntityKind.KIND_BRAND, tz, forDate);
	
					if(indicatorsSet.containsKey(obj.getKey().getName())) 
						obj = indicatorsSet.get(obj.getKey().getName());
					obj.setDoubleValue(obj.getDoubleValue() + ticket.getQty());
					indicatorsSet.put(obj.getKey().getName(), obj);
					
					// visitor_hourly_tickets -------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					List<StoreTicketByHour> hours = sthDao.getUsingStoreIdAndDateAndRange(store.getIdentifier(), ticket.getDate(), "00:00", "23:00", null, null, true);
					for( StoreTicketByHour th : hours ) {
						obj = buildBasicDashboardIndicatorData(
								"apd_visitor", "Visitantes", "visitor_hourly_tickets",
								"Tickets", sdf2.parse(th.getDate() + " " + th.getHour()),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, store.getShoppingId(),
								store, null, null, store.getBrandId(), EntityKind.KIND_BRAND, tz, forDate);
		
						if(indicatorsSet.containsKey(obj.getKey().getName())) 
							obj = indicatorsSet.get(obj.getKey().getName());
						obj.setDoubleValue(obj.getDoubleValue() + th.getQty());
						indicatorsSet.put(obj.getKey().getName(), obj);
					}
					
				}
				log.log(Level.INFO, "Starting Write Procedure...");

				// Finally, save all the information
				saveIndicatorSet(indicatorsSet);

			} else {
				// Store not found
				log.log(Level.INFO, "Store with id " + storeId + " not found!");
			}
			
		} catch (ASException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		
		long endTime = new Date().getTime();
		log.log(Level.INFO, "Finished to create store tickets Dashboard for Day " + fromDate + " to: " + toDate + " total time: "+ (endTime - startTime) + "ms");
	}
	
	public void createStoreItemDataForDates(String fromDate,String toDate, String storeId) throws ASException,ParseException{
		
		log.log(Level.INFO, "Starting to create store items Dashboard for Day " + fromDate + " to: " + toDate +"..." );
		long startTime = new Date().getTime();
		
		try {
			Map<String, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();
			List<StoreItem> items =  siDao.getUsingStoreIdAndDatesAndRange(storeId, fromDate, toDate, null, null, false);
			
			
			Store store = storeDao.get(storeId);
			if( store != null ) {
				
				for( StoreItem item: items){		
					
					DashboardIndicatorData obj;
					String forDate = item.getDate();
					TimeZone tz = TimeZone.getTimeZone(store.getTimezone());
	
					// visitor_total_itemss --------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					
					obj = buildBasicDashboardIndicatorData(
							"apd_visitor", "Visitantes", "visitor_total_items",
							"Items Vendidos", sdf.parse(item.getDate()),
							DashboardIndicatorData.PERIOD_TYPE_DAILY, store.getShoppingId(),
							store, null, null, store.getBrandId(), EntityKind.KIND_BRAND, tz, forDate );
	
					if(indicatorsSet.containsKey(obj.getKey().getName())) 
						obj = indicatorsSet.get(obj.getKey().getName());
					obj.setDoubleValue(obj.getDoubleValue() + item.getQty());
					indicatorsSet.put(obj.getKey().getName(), obj);

				}
				log.log(Level.INFO, "Starting Write Procedure...");

				// Finally, save all the information
				saveIndicatorSet(indicatorsSet);

			} else {
				// Store not found
				log.log(Level.INFO, "Store with id " + storeId + " not found!");
			}
			
		} catch (ASException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		
		long endTime = new Date().getTime();
		log.log(Level.INFO, "Finished to create store itemss Dashboard for Day " + fromDate + " to: " + toDate + " total time: "+ (endTime - startTime) + "ms");
	}

	public void createStoreRevenueDataForDates(String fromDate,String toDate, String storeId) throws ASException,ParseException{
		
		log.log(Level.INFO, "Starting to create store revenue Dashboard for Day " + fromDate + " to: " + toDate +"..." );
		long startTime = new Date().getTime();
		
		try {
			Map<String, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();
			List<StoreRevenue> revenues =  srDao.getUsingStoreIdAndDatesAndRange(storeId, fromDate, toDate, null, null, false);
			
			
			Store store = storeDao.get(storeId);
			if( store != null ) {
				
				for( StoreRevenue revenue: revenues){		
					
					DashboardIndicatorData obj;
					String forDate = revenue.getDate();
					TimeZone tz = TimeZone.getTimeZone(store.getTimezone());
	
					// visitor_total_revenues --------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					
					obj = buildBasicDashboardIndicatorData(
							"apd_visitor", "Visitantes", "visitor_total_revenue",
							"Revenue", sdf.parse(revenue.getDate()),
							DashboardIndicatorData.PERIOD_TYPE_DAILY, store.getShoppingId(),
							store, null, null, store.getBrandId(), EntityKind.KIND_BRAND, tz, forDate );
	
					if(indicatorsSet.containsKey(obj.getKey().getName())) 
						obj = indicatorsSet.get(obj.getKey().getName());
					obj.setDoubleValue(obj.getDoubleValue() + revenue.getQty());
					indicatorsSet.put(obj.getKey().getName(), obj);
				}
				log.log(Level.INFO, "Starting Write Procedure...");

				// Finally, save all the information
				saveIndicatorSet(indicatorsSet);

			} else {
				// Store not found
				log.log(Level.INFO, "Store with id " + storeId + " not found!");
			}
			
		} catch (ASException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		
		long endTime = new Date().getTime();
		log.log(Level.INFO, "Finished to create store revenue Dashboard for Day " + fromDate + " to: " + toDate + " total time: "+ (endTime - startTime) + "ms");
	}
	
	public DashboardIndicatorData buildBasicDashboardIndicatorData(String elementId, String elementName,
			String elementSubId, String elementSubName, Date date, String periodType, String shoppingId,
			Store store, Shopping shopping, String subentityName, String entityId, Integer entityKind,
			TimeZone tz, String forDate) throws ASException, ParseException {

		DashboardIndicatorData obj = new DashboardIndicatorData();
		obj.setEntityId(entityId);
		obj.setEntityKind(entityKind);
		
		obj.setElementId(elementId);
		obj.setElementName(elementName);
		obj.setElementSubId(elementSubId);
		obj.setElementSubName(elementSubName);
		
		CALENDAR.setTime(date);
		CALENDAR.setTimeZone(TimeZone.getTimeZone("GMT"));
		date = CALENDAR.getTime();
		int i = getTimeZone(tz, date);// TODO remove
		obj.setTimeZone(getTimeZone(tz, date));
		
		//CALENDAR.setTimeZone(tz);
		
		obj.setStringDate(forDate);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date day = sdf.parse(forDate);
		@SuppressWarnings("deprecation")
		int dayOfWeek = day.getDay() == 0 ? 7 : day.getDay() + 1; 
		obj.setDayOfWeek(dayOfWeek);
		try { obj.setDate(CALENDAR.getTime()); } catch(Exception e ){}
		
		obj.setMovieId(null);
		obj.setMovieName(null);
		if( store != null ) {
			obj.setSubentityId(store.getIdentifier());
			obj.setSubentityName(subentityName != null ? subentityName : store.getName());
			obj.setCountry(store.getAddress().getCountry());
			obj.setCity(store.getAddress().getCity());
			obj.setProvince(store.getAddress().getProvince());
		} else if( entityKind.equals(EntityKind.KIND_SHOPPING)) {
			obj.setSubentityId(entityId);
			obj.setSubentityName(shopping.getName());
			obj.setCountry(shopping.getAddress().getCountry());
			obj.setCity(shopping.getAddress().getCity());
			obj.setProvince(shopping.getAddress().getProvince());
		} else if( entityKind.equals(EntityKind.KIND_INNER_ZONE)) {
			InnerZone zone = zoneCache.get(entityId);
			obj.setSubentityId(entityId);
			obj.setSubentityName(zone.getName());
			obj.setCountry(null);
			obj.setCity(null);
			obj.setProvince(null);
		}
		obj.setVoucherType(null);
		obj.setPeriodType(periodType);

		obj.setKey(dao.createKey(obj));

		return obj;
	}

	public int getTimeZone(TimeZone tz, Date date) {
		CALENDAR.setTime(date);
		CALENDAR.setTimeZone(tz);
		return CALENDAR.get(Calendar.HOUR_OF_DAY);
	}

	public String getDeviceType(String mac) {
		return apdHelper.getDevicePlatform(mac, macVendorCache).toLowerCase();
	}

	public void buildCaches(boolean withMacVendor) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, ASException {

		// Prepares Store cache
		storeCache = CollectionFactory.createMap();
		List<Store> stores = storeDao.getUsingLastUpdateStatusAndRange(null, null, false,
				Arrays.asList(new Integer[] { StatusAware.STATUS_ENABLED }), null, null, null, false);
		for( Store store : stores ) {
			if( StringUtils.hasText(store.getExternalId())) 
				storeCache.put(store.getExternalId(), store);
			storeCache.put(store.getIdentifier(), store);
		}

		List<Shopping> shoppings = shoppingDao.getUsingLastUpdateStatusAndRange(null, null, false,
				Arrays.asList(new Integer[] { StatusAware.STATUS_ENABLED }), null, null, null, false);
		shoppingCache = CollectionFactory.createMap();
		for(Shopping shopping : shoppings ) {
			shoppingCache.put(shopping.getIdentifier(), shopping);
		}

		List<InnerZone> zones = innerzoneDao.getUsingLastUpdateStatusAndRange(null, null, false,
				Arrays.asList(new Integer[] { StatusAware.STATUS_ENABLED }), null, null, null, false);
		zoneCache = CollectionFactory.createMap();
		for(InnerZone zone : zones ) {
			zoneCache.put(zone.getIdentifier(), zone);
		}

		List<WifiSpot> wifiSpots = wifiSpotDao.getUsingLastUpdateStatusAndRange(null, null, false,
				Arrays.asList(new Integer[] { StatusAware.STATUS_ENABLED }), null, null, null, false);
		wifiSpotCache = CollectionFactory.createMap();
		for(WifiSpot wifiSpot : wifiSpots) {
			wifiSpotCache.put(wifiSpot.getIdentifier(), wifiSpot);
		}

		List<FloorMap> floorMaps = floorMapDao.getUsingLastUpdateStatusAndRange(null, null, false,
				Arrays.asList(new Integer[] { StatusAware.STATUS_ENABLED }), null, null, null, false);
		floorMapCache = CollectionFactory.createMap();
		for(FloorMap floorMap : floorMaps) {
			floorMapCache.put(floorMap.getIdentifier(), floorMap);
		}

		if( withMacVendor ) {
			List<MacVendor> macVendors = macVendorDao.getUsingLastUpdateStatusAndRange(null, null, false,
					Arrays.asList(new Integer[] { StatusAware.STATUS_ENABLED }), null, null, null, false);
			macVendorCache = CollectionFactory.createMap();
			for(MacVendor macVendor : macVendors) {
				macVendorCache.put(macVendor.getIdentifier(), macVendor);
			}
		}

		log.log(Level.INFO, "General Cache Built");

	}

	/**
	 * Obtains the time zone for an entity Id
	 * 
	 * @param entityId
	 *            Entity Id to inspect
	 * @param entityKind
	 *            Entity Kind to inspect
	 * @return The Time zone for the requested entity
	 */
	private TimeZone getTimezoneForEntity(String entityId, Integer entityKind) {
		
		if( entityKind.equals(EntityKind.KIND_STORE)) {
			try {
				Store obj = storeDao.get(entityId, true);
				TimeZone ret = TimeZone.getTimeZone(obj.getTimezone());
				return ret;
			} catch( Exception e ) {
				log.log(Level.WARNING, e.getMessage(), e);
			}
		} else if( entityKind.equals(EntityKind.KIND_INNER_ZONE)) {
			try {
				InnerZone obj = innerzoneDao.get(entityId, true);
				return getTimezoneForEntity(obj.getEntityId(), obj.getEntityKind());
			} catch( Exception e ) {
				log.log(Level.WARNING, e.getMessage(), e);
			}
		} else if( entityKind.equals(EntityKind.KIND_SHOPPING)) {
			try {
				Shopping obj = shoppingDao.get(entityId, true);
				TimeZone ret = TimeZone.getTimeZone(obj.getTimezone());
				return ret;
			} catch( Exception e ) {
				log.log(Level.WARNING, e.getMessage(), e);
			}
		}

		return TimeZone.getDefault();
	}

	private void disposeCaches() throws SQLException {
	}
}
