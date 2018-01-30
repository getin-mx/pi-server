package mobi.allshoppings.dashboards;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.apdevice.APHHelper;
import mobi.allshoppings.apdevice.impl.APDVisitHelperImpl;
import mobi.allshoppings.dao.APDeviceDAO;
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
import mx.getin.Constants;
import mx.getin.dao.StoreItemByHourDAO;
import mx.getin.dao.StoreRevenueByHourDAO;
import mx.getin.model.StoreItemByHour;
import mx.getin.model.StoreRevenueByHour;

public class DashboardAPDeviceMapperService {

	private static final Logger log = Logger.getLogger(DashboardAPDeviceMapperService.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
	private static final TimeZone GMT = TimeZone.getTimeZone(Constants.GMT_TIMEZONE_ID);
	
	private static final SimpleDateFormat dateSDF = new SimpleDateFormat(Constants.DATE_FORMAT);
	private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final Gson gson = GsonFactory.getInstance();
	private final Calendar CALENDAR = Calendar.getInstance();
	private final Calendar AUX_CALENDAR = Calendar.getInstance();
	public static final String ELEMENT_SUB_ID_HOURLY_OCCUPATION_PEDERSTIANS = "occupation_hourly_peasants";
	public static final String ELEMENT_SUB_ID_HOURLY_OCCUPATION_VISITS = "occupation_hourly_visits";
	public static final String ELEMENT_SUB_ID_HOURLY_OCCUPATION_VIEWER = "occupation_hourly_viewer";
	public static final String ELEMENT_SUB_ID_HOURLY_PERMANENCE_VISITS = "permanence_hourly_visits";
	public static final String ELEMENT_SUB_ID_HOURLY_PERMANCENCE_PEDERSTIANS = "permanence_hourly_peasents";
	public static final String ELEMENT_SUB_ID_HOURLY_PERMANENCE_VIEWERS = "permanence_hourly_viewers";
	public static final String ELEMENT_SUB_ID_HOURLY_TICKETS = "visitor_hourly_tickets";
	public static final String ELEMENT_SUB_ID_HOURLY_ITEMS = "visitor_hourly_items";
	public static final String ELEMENT_SUB_ID_HOURLY_REVENUE = "visitor_hourly_revenue";
	public static final String ELEMENT_SUB_ID_TOTAL_PEDERSTIANS = "visitor_total_peasents";
	public static final String ELEMENT_SUB_ID_TOTAL_TICKETS = "visitor_total_tickets";
	public static final String ELEMENT_SUB_ID_TOTAL_ITEMS = "visitor_total_items";
	public static final String ELEMENT_SUB_ID_TOTAL_REVENUE = "visitor_total_revenue";
	public static final String ELEMENT_SUB_ID_TOTAL_VIEWERS = "visitor_total_viewer";
	public static final String ELEMENT_SUB_ID_TOTAL_VISITS = "visitor_total_visits";
	public static final String ELEMENT_SUB_ID_HEATMAP_DATA = "heatmap_data";
	public static final String ELEMENT_SUB_NAME_VISITS = "Visitas";
	public static final String ELEMENT_SUB_NAME_PEDERSTIANS = "Paseantes";
	public static final String ELEMENT_SUB_NAME_TICKETS = "Tickets";
	public static final String ELEMENT_SUB_NAME_ITEMS = "Items Vendidos";
	public static final String ELEMENT_SUB_NAME_REVENUE = "Revenue";
	public static final String ELEMENT_SUB_NAME_VIEWERS = "Miradores";
	public static final String ELEMENT_SUB_NAME_HEATMAP_DATA = "Heat Map Data";
	public static final Map<String, String> INDICATORS_ALIASES;
	
	static {
		INDICATORS_ALIASES = new HashMap<>(15);
		INDICATORS_ALIASES.put(ELEMENT_SUB_ID_HOURLY_OCCUPATION_PEDERSTIANS, ELEMENT_SUB_NAME_PEDERSTIANS);
		INDICATORS_ALIASES.put(ELEMENT_SUB_ID_HOURLY_OCCUPATION_VISITS, ELEMENT_SUB_NAME_VISITS);
		INDICATORS_ALIASES.put(ELEMENT_SUB_ID_HOURLY_OCCUPATION_VIEWER, ELEMENT_SUB_NAME_VIEWERS);
		INDICATORS_ALIASES.put(ELEMENT_SUB_ID_HOURLY_PERMANCENCE_PEDERSTIANS, ELEMENT_SUB_NAME_PEDERSTIANS);
		INDICATORS_ALIASES.put(ELEMENT_SUB_ID_HOURLY_PERMANENCE_VISITS, ELEMENT_SUB_NAME_VISITS);
		INDICATORS_ALIASES.put(ELEMENT_SUB_ID_HOURLY_PERMANENCE_VIEWERS, ELEMENT_SUB_NAME_VIEWERS);
		INDICATORS_ALIASES.put(ELEMENT_SUB_ID_HOURLY_TICKETS, ELEMENT_SUB_NAME_TICKETS);
		INDICATORS_ALIASES.put(ELEMENT_SUB_ID_HOURLY_REVENUE, ELEMENT_SUB_NAME_REVENUE);
		INDICATORS_ALIASES.put(ELEMENT_SUB_ID_TOTAL_PEDERSTIANS, ELEMENT_SUB_NAME_PEDERSTIANS);
		INDICATORS_ALIASES.put(ELEMENT_SUB_ID_TOTAL_ITEMS, ELEMENT_SUB_NAME_ITEMS);
		INDICATORS_ALIASES.put(ELEMENT_SUB_ID_TOTAL_REVENUE, ELEMENT_SUB_NAME_REVENUE);
		INDICATORS_ALIASES.put(ELEMENT_SUB_ID_TOTAL_TICKETS, ELEMENT_SUB_NAME_TICKETS);
		INDICATORS_ALIASES.put(ELEMENT_SUB_ID_TOTAL_VIEWERS, ELEMENT_SUB_NAME_VIEWERS);
		INDICATORS_ALIASES.put(ELEMENT_SUB_ID_TOTAL_VISITS, ELEMENT_SUB_NAME_VISITS);
		INDICATORS_ALIASES.put(ELEMENT_SUB_ID_HEATMAP_DATA, ELEMENT_SUB_NAME_HEATMAP_DATA);
	}

	/**
	 * DAOs 
	 */
	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private DashboardIndicatorDataDAO dao;
	@Autowired
	private ShoppingDAO shoppingDao;
	@Autowired
	private FloorMapDAO floorMapDao;
	@Autowired
	private WifiSpotDAO wifiSpotDao;
	@Autowired
	private APHHelper aphHelper;
	@Autowired
	private APDeviceDAO apdDao;
	@Autowired
	private FloorMapJourneyDAO fmjDao;
	@Autowired
	private MacVendorDAO macVendorDao;
	@Autowired
	private StoreTicketDAO stDao;
	@Autowired
	private StoreTicketByHourDAO sthDao;
	@Autowired
	private StoreItemDAO siDao;
	@Autowired
	private StoreItemByHourDAO sihDao;
	@Autowired
	private StoreRevenueDAO srDao;
	@Autowired
	private StoreRevenueByHourDAO srhDao;
	@Autowired
	private InnerZoneDAO innerzoneDao;
	/*@Autowired
	private ExternalAPHotspotDAO eaphDao;*/

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

	static {
		dateSDF.setTimeZone(GMT);
	}
	
	// General Driver ----------------------------------------------------------------------------------------------------------------------------------------
	public void createDashboardDataForDays(String baseDir, Date fromDate, Date toDate, List<String> entityIds,
			List<Integer> phases, boolean deletePreviousRecords) throws ASException {
		try {
			buildCaches(true);

			Date curDate = new Date(fromDate.getTime());
			while( curDate.before(toDate)) {
				
				if( CollectionUtils.isEmpty(phases) || phases.contains(PHASE_WIFI_HEATMAP))
					createHeatmapDashboardForDay(baseDir, curDate);

				if( CollectionUtils.isEmpty(phases) || phases.contains(PHASE_APDEVICE_HEATMAP))
					createAPDeviceHeatmapDashboardForDay(baseDir, curDate);

				if( CollectionUtils.isEmpty(phases) || phases.contains(PHASE_EXTERNAL_APDEVICE_HEATMAP))
					createExternalAPDeviceHeatmapDashboardForDay(entityIds, curDate);

				if( CollectionUtils.isEmpty(phases) || phases.contains(PHASE_FLOORMAP_TRACKING))
					createFloorMapTrackingForDay(curDate, entityIds, deletePreviousRecords, dateSDF.format(fromDate),
							curDate.getTime() +Constants.DAY_IN_MILLIS < toDate.getTime());

				if( CollectionUtils.isEmpty(phases) || phases.contains(PHASE_APDVISIT))
					createAPDVisitPerformanceDashboardForDay(curDate, entityIds, null, null, !deletePreviousRecords,
							false, false);
				curDate = new Date(curDate.getTime() + Constants.DAY_IN_MILLIS);

			}

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getLocalizedMessage(), e);
		}
	}

	// HeatMaps ---------------------------------------------------------------------------------------------------------------------------------------------
	public void createHeatmapDashboardForDay(String baseDir, Date date) throws ASException {

		log.log(Level.INFO, "Starting to create Heatmap Dashboard for Day " + date + "...");
		long startTime = System.currentTimeMillis();

		Date processDate = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		Date limitDate = DateUtils.addDays(processDate, 1);

		log.log(Level.INFO, "ProcessDate " + processDate);
		log.log(Level.INFO, "LimitDate " + limitDate);

		// All Wifi Data
		DumperHelper<DeviceWifiLocationHistory> dumper = new DumpFactory<DeviceWifiLocationHistory>().build(
				baseDir, DeviceWifiLocationHistory.class, false);
		Iterator<DeviceWifiLocationHistory> i = dumper.iterator(processDate, limitDate, false);

		Map<DashboardIndicatorData, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();
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
								// heatmap ----------------------------------------------------------------------------------------------
								// ------------------------------------------------------------------------------------------------------
								obj = buildBasicDashboardIndicatorData(ELEMENT_SUB_ID_HEATMAP_DATA,
										ELEMENT_SUB_NAME_HEATMAP_DATA, wifiSpot.getIdentifier(), wifiSpot.getIdentifier(),
										location.getCreationDateTime(), DashboardIndicatorData.PERIOD_TYPE_DAILY,
										shopping.getIdentifier(), null, shopping, floorMap.getFloor(),
										shopping.getIdentifier(), EntityKind.KIND_SHOPPING, forDate);
								if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
								else {
									obj.setSubentityId(wifiSpot.getFloorMapId());
									obj.setSubentityName(floorMap.getFloor());
									indicatorsSet.put(obj, obj);
								}
								obj.setDoubleValue(obj.getDoubleValue() + 1);
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

		log.log(Level.INFO, "Starting Write Procedure for " +indicatorsSet.hashCode() +" indicators...");

		// Finally, save all the information
		saveIndicatorSet(indicatorsSet);

		dumper.dispose();
		
		long endTime = System.currentTimeMillis();
		log.log(Level.INFO, "Finished to create Heatmap Dashboard for Day " + date + " in "
					+ (endTime - startTime) + "ms");

	}

	// HeatMaps ---------------------------------------------------------------------------------------------------------------------------------------------
	public void createAPDeviceHeatmapDashboardForDay(String baseDir, Date date) throws ASException {

		log.log(Level.INFO, "Starting to create APDevice Heatmap Dashboard for Day " + date + "...");
		long startTime = System.currentTimeMillis();

		Date processDate = new Date(date.getTime());
		CALENDAR.setTime(date);
		CALENDAR.add(Calendar.DATE, 1);
		Date limitDate = CALENDAR.getTime();

		log.log(Level.INFO, "ProcessDate " + processDate);
		log.log(Level.INFO, "LimitDate " + limitDate);

		Map<DashboardIndicatorData, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();

		for( String floorMapIdentifier : systemConfiguration.getFloorMapTracking()) {
			try {
				FloorMap floorMap = floorMapDao.get(floorMapIdentifier, true);
				List<WifiSpot> wifiSpotList = wifiSpotDao.getUsingFloorMapId(floorMap.getIdentifier());
				Store store = storeDao.get(floorMap.getShoppingId(), true);
				Shopping shopping = null;
				try { shopping = shoppingDao.get(floorMap.getShoppingId(), true); }
				catch( Exception e ) {}
				List<String> devices = CollectionFactory.createList();

				for( WifiSpot ws : wifiSpotList ) {
					if( StringUtils.hasText(ws.getApDevice())) {
						if( !devices.contains(ws.getApDevice()))
							devices.add(ws.getApDevice());
					}
				}

				// All Wifi Data
				DumperHelper<APHotspot> dumper = new DumpFactory<APHotspot>().build(
						baseDir, APHotspot.class, false);
				Iterator<JSONObject> i = dumper.jsonIterator(processDate, limitDate, false);

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
									
									// heatmap ----------------------------------------------------------------------------------------------
									// ------------------------------------------------------------------------------------------------------
									obj = buildBasicDashboardIndicatorData("	", "Heat Map Data",
											wifiSpot.getIdentifier(), wifiSpot.getIdentifier(),
											hotspot.getCreationDateTime(), DashboardIndicatorData.PERIOD_TYPE_DAILY,
											store.getIdentifier(), store, shopping, floorMap.getFloor(),
											store.getIdentifier(), EntityKind.KIND_SHOPPING, forDate);
									if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
									else {
										obj.setSubentityId(wifiSpot.getFloorMapId());
										obj.setSubentityName(floorMap.getFloor());
										indicatorsSet.put(obj, obj);
									}
									obj.setDoubleValue(obj.getDoubleValue() + (100 - hotspot.getSignalDB()));
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

		log.log(Level.INFO, "Starting Write Procedure for " +indicatorsSet.size() +"indicators...");

		// Finally, save all the information
		saveIndicatorSet(indicatorsSet);


		long endTime = System.currentTimeMillis();
		log.log(Level.INFO, "Finished to create APDevice Heatmap Dashboard for Day " + date + " in "
					+ (endTime - startTime) + "ms");

	}

	// External APD Heatmap
	public void createExternalAPDeviceHeatmapDashboardForDay(List<String> entityIds, Date date) throws ASException {

		log.log(Level.INFO, "Starting to create External APDevice Heatmap Dashboard for Day " + date + "...");
		long startTime = System.currentTimeMillis();

		Date processDate = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		Date limitDate = DateUtils.addDays(processDate, 1);

		log.log(Level.INFO, "ProcessDate " + processDate);
		log.log(Level.INFO, "LimitDate " + limitDate);

		Map<DashboardIndicatorData, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();

		List<String> pList = CollectionFactory.createList();
		
		if( entityIds == null || entityIds.isEmpty()) pList.addAll(floorMapCache.keySet());
		else pList.addAll(entityIds);

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
					List<ExternalAPHotspot> list = new ArrayList<>();
					//eaphDao.getUsingHostnameAndDates(devices, processDate, limitDate);
					for(String dev : devices) {
						DumperHelper<ExternalAPHotspot> dumper = new DumpFactory<ExternalAPHotspot>().build(null,
								ExternalAPHotspot.class, false);
						dumper.setFilter(dev);
						for(Iterator<ExternalAPHotspot> in = dumper.iterator(processDate, limitDate, false);
								in.hasNext();) list.add(in.next());
						dumper.dispose();
					}
					for( ExternalAPHotspot hotspot : list ) {

						for( WifiSpot wifiSpot : wifiSpotList ) {
							if( wifiSpot.getApDevice().equals(hotspot.getHostname())) {

								// Checkin data
								DashboardIndicatorData obj;
								String forDate = sdf.format(hotspot.getCreationDateTime());
								
								// heatmap ----------------------------------------------------------------------------------------------
								obj = EntityKind.KIND_SHOPPING == entityKind ? buildBasicDashboardIndicatorData(
										ELEMENT_SUB_ID_HEATMAP_DATA, ELEMENT_SUB_NAME_HEATMAP_DATA, wifiSpot.getIdentifier(),
										wifiSpot.getIdentifier(), hotspot.getCreationDateTime(),
										DashboardIndicatorData.PERIOD_TYPE_DAILY, shopping.getIdentifier(),
										null, shopping, floorMap.getFloor(), shopping.getIdentifier(),
										EntityKind.KIND_SHOPPING, forDate) : buildBasicDashboardIndicatorData(
													ELEMENT_SUB_ID_HEATMAP_DATA, ELEMENT_SUB_NAME_HEATMAP_DATA, wifiSpot.getIdentifier(),
													wifiSpot.getIdentifier(), hotspot.getCreationDateTime(),
													DashboardIndicatorData.PERIOD_TYPE_DAILY, store.getIdentifier(),
													store, shopping, floorMap.getFloor(), store.getIdentifier(),
													EntityKind.KIND_SHOPPING, forDate);
								if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
								else {
									obj.setSubentityId(wifiSpot.getFloorMapId());
									obj.setSubentityName(floorMap.getFloor());
									indicatorsSet.put(obj, obj);
								}
								obj.setDoubleValue(obj.getDoubleValue() + (100 - hotspot.getSignalDB()));
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

		log.log(Level.INFO, "Starting Write Procedure for " +indicatorsSet.size() +"indicators...");

		// Finally, save all the information
		saveIndicatorSet(indicatorsSet);

		long endTime = System.currentTimeMillis();
		log.log(Level.INFO, "Finished to create APDevice Heatmap Dashboard for Day " + date + " in "
					+ (endTime - startTime) + "ms");

	}


	// Floor Map Tracking --------------------------------------------------------------------------------------------------------------------------------
	public void createFloorMapTrackingForDay(Date date, List<String> entityIds, boolean deletePreviousRecords,
			String begginingDate, boolean lastDay) throws ASException {

		log.log(Level.INFO, "Starting to create Floor Map Tracking Dashboard for Day " + date + "...");
		long startTime = System.currentTimeMillis();

		String processDate = dateSDF.format(date.getTime());
		CALENDAR.setTime(date);
		CALENDAR.add(Calendar.DATE, 1);
		String limitDate = dateSDF.format(CALENDAR.getTime());

		log.log(Level.INFO, "ProcessDate " + processDate);
		log.log(Level.INFO, "LimitDate " + limitDate);

		List<String> pList = CollectionFactory.createList();
		if( entityIds == null || entityIds.isEmpty()) pList.addAll(systemConfiguration.getFloorMapTracking());
		else pList.addAll(entityIds);
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

				Map<String, List<APHEntry>> macEntries = CollectionFactory.createMap();
				Map<Key, FloorMapJourney> journies = CollectionFactory.createMap();
				for(String hostname : apDeviceIds) {
					DumperHelper<APHEntry> dumper = new DumpFactory<APHEntry>().build(
							null, APHEntry.class, false);
					dumper.setFilter(hostname);
					for(APHEntry entry : APDVisitHelperImpl.integrateAPHE(dumper, apDevices.get(hostname),
							processDate, getTimezoneForEntity(floorMapIdentifier, EntityKind.KIND_SHOPPING),
							CALENDAR, AUX_CALENDAR, (short) -1, (short) -1)) {
						List<APHEntry> entryList = macEntries.get(entry.getMac());
						if(entryList == null) {
							entryList = CollectionFactory.createList();
							macEntries.put(entry.getMac(), entryList);
						}
						entryList.add(entry);
					}
					dumper.dispose();
					long count = 0;
					for(String mac : macEntries.keySet()) {
						
						if(count % 1000 == 0 )
							log.log(Level.INFO, "Processing record " + count + " of " + macEntries.size() + "...");
						
						count++;
						List<APHEntry> aphes = macEntries.get(mac);
						
						// Find the minimal slot time for this mac address.
						// It means, the first time that this mac was saw for any of the APDevices
						int minimalSlot = Integer.MAX_VALUE;
						int maximalSlot = 0;
						int maxDataCount = 0;
						int maxVisitTimeThreshold = 0;
						boolean valid = false;
						for( APHEntry entry : aphes ) {
							try {
								List<Integer> arr = aphHelper.artificiateRSSI(entry, apDevices.get(hostname));
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
								int distance = (myMaximalSlot - myMinimalSlot) / 3;
								valid = entry.getMaxRssi() >= apd.getVisitPowerThreshold() &&
										distance >= apd.getVisitTimeThreshold() && distance <= apd.getVisitMaxThreshold();

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
								int signal = Integer.MIN_VALUE;

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
							for(Integer slot : aphHelper.timeslotToList(journey.getWifiPoints())) {
								String wifiSpotId = journey.getWifiPoints().get(String.valueOf(slot));
								if( null == wifiSpotId ) continue;
								String chr = wifiSpotCache.get(wifiSpotId).getWordAlias();
								if(journey.getWord().size() == 0 
										|| !journey.getWord().get(journey.getWord().size() - 1).equals(chr))
									journey.getWord().add(chr);
							}
							journey.setDataCount(journey.getWifiPoints().size());
							journey.setWordLength(journey.getWord().size());
							
							// Creates the user journey
							if( journey.getWordLength() > 1 ) {
								try {
									journies.put(journey.getKey(), journey);
								} catch( Exception e ) {
									log.log(Level.WARNING, e.getMessage(), e);
								}
							}
						} if(deletePreviousRecords) {
							for(FloorMapJourney toDelte : fmjDao.getUsingFloorMapAndMacAndDate(floorMapIdentifier,
									mac, processDate, limitDate, null, null))
								fmjDao.delete(toDelte);
						}//if must delete previous records 
					}
				}//fetches all macs
				
				log.log(Level.INFO, "Witting database with " +journies.size() +" results...");
				fmjDao.createOrUpdate(null, CollectionFactory.createList(journies.values()), true);
								
			}
			long endTime = System.currentTimeMillis();
			log.log(Level.INFO, "Finished to create Floor Map Tracking Dashboard for Day " + date + " in "
					+ (endTime - startTime) + "ms");

		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getLocalizedMessage(), e);
		}
	}

	// apd_visitor performance -------------------------------------------------------------------------------------------------------------------------

	public void createAPDVisitPerformanceDashboardForDay(Date date, List<String> entityIds, Integer entityKind,
			List<APDVisit> data, boolean isDailyProcess, boolean lastDay, boolean onlyVisits) throws ASException {
		log.log(Level.INFO, "Starting to create apd_visitor Performance Dashboard for Day " + date + "...");
		long startTime = System.currentTimeMillis();
		CALENDAR.setTime(date);

		log.log(Level.INFO, "ProcessDate " + date);

		try {
			if(entityIds == null) entityIds = CollectionFactory.createList();
			if(entityIds.isEmpty()) {
				if(storeCache.isEmpty()) buildCaches(false);
				for(String id : storeCache.keySet()) {
					if(!entityIds.contains(storeCache.get(id).getIdentifier()))
						entityIds.add(storeCache.get(id).getIdentifier());
				}
				log.log(Level.INFO, "Entities are: " +entityIds);
			}
			Map<DashboardIndicatorData, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();
			List<APDVisit> list;
			for(String entityId : entityIds) {
				String shoppingId = null;
				String subentityId = null;
				
				// Looks for all visit records
				indicatorsSet.clear();
				Store store = null;
				TimeZone tz = null;
				String sLimitDate = null;
				int offset;
				if(data == null || data.size() == 0) {
					// Prepares the Object Query
					Date dateFrom = new Date(date.getTime());
					Date dateTo = new Date(dateFrom.getTime() + Constants.DAY_IN_MILLIS);
					store = storeCache.get(entityId);
					if( store == null ) {
						store = storeDao.get(entityId, true);
						if( store == null ) return;
						storeCache.put(entityId, store);
					}
					tz = TimeZone.getTimeZone(store.getTimezone());
					offset = tz.getOffset(dateFrom.getTime()) *-1;
					if(offset > 0 && dateTo.getTime() < dateTo.getTime() +offset) {
						dateTo.setTime(dateTo.getTime() +offset);
					}
					sLimitDate = dateSDF.format(dateFrom);
					list = CollectionFactory.createList();
					DumperHelper<APDVisit> visitDumper = new DumpFactory<APDVisit>()
							.build(null, APDVisit.class, false);
					visitDumper.setFilter(entityId);
					Iterator<APDVisit> i = visitDumper.iterator(dateFrom, dateTo, false);
					while(i.hasNext()) list.add(i.next());
					visitDumper.dispose();
				} else list = data;
				if(list.size() == 0) {
					log.log(Level.INFO, "Done - No visits were given to process");
					continue;
				}
				Calendar init = Calendar.getInstance();
				Calendar finish = Calendar.getInstance();
				TimeZone mxTz = TimeZone.getTimeZone("Mexico/General");
				init.setTimeZone(mxTz);
				finish.setTimeZone(mxTz);
				boolean skip = false;
				log.log(Level.INFO, list.size() + " records to process... ");
				for(APDVisit v : list ) {
					if(sLimitDate != null && v.getForDate() != null && !v.getForDate().equals(sLimitDate)) continue;
					try {
						Shopping shopping = null;
						InnerZone zone = null;
						
						if( entityKind == null ) entityKind = EntityKind.KIND_BRAND;
						if( entityKind.equals(EntityKind.KIND_STORE)) entityKind = EntityKind.KIND_BRAND;
						
						if( entityKind.equals(EntityKind.KIND_BRAND)) {
							store = storeCache.get(String.valueOf(v.getEntityId()));
							if( store == null ) {
								store = storeDao.get(String.valueOf(v.getEntityId()), true);
								skip = store == null;
								if(skip) break;
								storeCache.put(String.valueOf(v.getEntityId()), store);
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
						CALENDAR.setTimeZone(tz);
						
						if( store != null || shopping != null || zone != null ) {
							DashboardIndicatorData obj;
							if( v.getCheckinType().equals(APDVisit.CHECKIN_PEASANT) ) {
								// visitor_total_peasents -------------------------------------------------------------------------------
								// ------------------------------------------------------------------------------------------------------
								obj = buildBasicDashboardIndicatorData("apd_visitor", "Visitantes", ELEMENT_SUB_ID_TOTAL_PEDERSTIANS,
										ELEMENT_SUB_NAME_PEDERSTIANS, v.getCheckinStarted(), DashboardIndicatorData.PERIOD_TYPE_DAILY,
										shoppingId, store, shopping, null, entityId, entityKind, v.getForDate());
								if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
								else indicatorsSet.put(obj, obj);
								obj.setDoubleValue(obj.getDoubleValue() + 1);
								
								if(!v.getHidePermanence() ) {
									// permanence_hourly_peasents ---------------------------------------------------------------------------
									// ------------------------------------------------------------------------------------------------------
									obj = buildBasicDashboardIndicatorData("apd_permanence", "Permanencia",
											ELEMENT_SUB_ID_HOURLY_PERMANCENCE_PEDERSTIANS, ELEMENT_SUB_NAME_PEDERSTIANS, v.getCheckinStarted(),
											DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId, store, shopping, null,
											entityId, entityKind, v.getForDate());
									if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
									else indicatorsSet.put(obj, obj);
									obj.setDoubleValue(obj.getDoubleValue()
											+ calculateDiffTime(v.getCheckinFinished(), v.getCheckinStarted()));
									obj.setRecordCount(obj.getRecordCount() + 1);
								}

								// occupation_total_peasents -------------------------------------------------------------------------------
								// ------------------------------------------------------------------------------------------------------
								init.setTime(v.getCheckinStarted());
								finish.setTime(v.getCheckinFinished());
								int day = init.get(Calendar.DAY_OF_MONTH);
								while (init.get(Calendar.HOUR_OF_DAY) <= finish.get(Calendar.HOUR_OF_DAY) &&
										day == init.get(Calendar.DAY_OF_MONTH)) {
									obj = buildBasicDashboardIndicatorData("apd_occupation", "Ocupacion",
											ELEMENT_SUB_ID_HOURLY_OCCUPATION_PEDERSTIANS, ELEMENT_SUB_NAME_PEDERSTIANS, init.getTime(),
											DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId, store, shopping, null,
											entityId, entityKind, v.getForDate());
									if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
									else indicatorsSet.put(obj, obj);
									obj.setDoubleValue(obj.getDoubleValue() + 1);
									init.add(Calendar.HOUR_OF_DAY, 1);
									
								}

							} else if( v.getCheckinType().equals(APDVisit.CHECKIN_VISIT) ) {

								// visitor_total_visits ---------------------------------------------------------------------------------
								// ------------------------------------------------------------------------------------------------------
								obj = buildBasicDashboardIndicatorData("apd_visitor", "Visitantes", ELEMENT_SUB_ID_TOTAL_VISITS,
										ELEMENT_SUB_NAME_VISITS, v.getCheckinStarted(), DashboardIndicatorData.PERIOD_TYPE_DAILY,
										shoppingId, store, shopping, null, entityId, entityKind, v.getForDate());
								if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
								else indicatorsSet.put(obj, obj);
								obj.setDoubleValue(obj.getDoubleValue() + 1);

								if( !v.getHidePermanence()) {
									// permanence_hourly_visits -------------------------------------------------------------------------------
									// ------------------------------------------------------------------------------------------------------
									obj = buildBasicDashboardIndicatorData("apd_permanence", "Permanencia",
											ELEMENT_SUB_ID_HOURLY_PERMANENCE_VISITS, ELEMENT_SUB_NAME_VISITS, v.getCheckinStarted(),
											DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId, store, shopping, null,
											entityId, entityKind, v.getForDate());
									if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
									else indicatorsSet.put(obj, obj);
									obj.setDoubleValue(obj.getDoubleValue()
											+ calculateDiffTime(v.getCheckinFinished(), v.getCheckinStarted()));
									obj.setRecordCount(obj.getRecordCount() + 1);
								}
								// occupation_total_visits ---------------------------------------------------------------------------------
								// ------------------------------------------------------------------------------------------------------
								
								init.setTime(v.getCheckinStarted());
								finish.setTime(v.getCheckinFinished());
								int day = init.get(Calendar.DAY_OF_MONTH);
								while (init.get(Calendar.HOUR_OF_DAY) <= finish.get(Calendar.HOUR_OF_DAY) &&
										day == init.get(Calendar.DAY_OF_MONTH)) {
									obj = buildBasicDashboardIndicatorData("apd_occupation", "Ocupacion",
											ELEMENT_SUB_ID_HOURLY_OCCUPATION_VISITS, ELEMENT_SUB_NAME_VISITS, init.getTime(),
											DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId, store, shopping, null,
											entityId, entityKind, v.getForDate());
									if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
									else indicatorsSet.put(obj, obj);
									obj.setDoubleValue(obj.getDoubleValue() + 1);
									init.add(Calendar.HOUR_OF_DAY, 1);
										
								}
							} else if(v.getCheckinType().equals(APDVisit.CHECKIN_VIEWER)) {
								
								obj = buildBasicDashboardIndicatorData("apd_visitor", "Visitantes",
										ELEMENT_SUB_ID_TOTAL_VIEWERS, ELEMENT_SUB_NAME_VIEWERS, v.getCheckinStarted(),
										DashboardIndicatorData.PERIOD_TYPE_DAILY, shoppingId, store, shopping, null,
										subentityId, entityKind, v.getForDate()) ;
								if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
								else indicatorsSet.put(obj, obj);
								obj.setDoubleValue(obj.getDoubleValue() + 1);
								if(!v.getHidePermanence()) {
									obj = buildBasicDashboardIndicatorData("apd_permanence", "Permanencia",
											ELEMENT_SUB_ID_HOURLY_PERMANENCE_VIEWERS, ELEMENT_SUB_NAME_VIEWERS,
											v.getCheckinStarted(), null, shoppingId, store, shopping, null,
											entityId, entityKind, v.getForDate());
									if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
									else indicatorsSet.put(obj, obj);
									obj.setDoubleValue(obj.getDoubleValue()
											+ calculateDiffTime(v.getCheckinFinished(), v.getCheckinStarted()));
									obj.setRecordCount(obj.getRecordCount() + 1);
								}//add viewers permanence
								init.setTime(v.getCheckinStarted());
								finish.setTime(v.getCheckinFinished());
								int day = init.get(Calendar.DAY_OF_MONTH);
								while (init.get(Calendar.HOUR_OF_DAY) <= finish.get(Calendar.HOUR_OF_DAY) &&
										day == init.get(Calendar.DAY_OF_MONTH)) {
									obj = buildBasicDashboardIndicatorData("apd_occupation", "Ocupacion",
											ELEMENT_SUB_ID_HOURLY_OCCUPATION_VIEWER, ELEMENT_SUB_NAME_VIEWERS,
											init.getTime(), null, shoppingId, store, shopping, null, entityId, entityKind,
											v.getForDate());
									if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
									else indicatorsSet.put(obj, obj);
									obj.setDoubleValue(obj.getDoubleValue() + 1);
									init.add(Calendar.HOUR_OF_DAY, 1);
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
				} if(skip) continue;
				log.log(Level.INFO, "Starting Write Procedure for " +indicatorsSet.size() +" visits...");

				// save all the visits
				saveIndicatorSet(indicatorsSet);
				
				// Looks for tickets, revenue & items
				isDailyProcess = !isDailyProcess;
				if(null != entityKind && !lastDay && !onlyVisits) {
					if( entityKind.equals(EntityKind.KIND_BRAND)) {
						createStoreTicketDataForDates(dateSDF.format(date), dateSDF.format(date), subentityId, isDailyProcess);
						createStoreItemDataForDates(dateSDF.format(date), dateSDF.format(date), subentityId, isDailyProcess);
						createStoreRevenueDataForDates(dateSDF.format(date), dateSDF.format(date), subentityId, isDailyProcess);
					}
					if( entityKind.equals(EntityKind.KIND_STORE)) {
						createStoreTicketDataForDates(dateSDF.format(date), dateSDF.format(date), entityId, isDailyProcess);
						createStoreItemDataForDates(dateSDF.format(date), dateSDF.format(date), entityId, isDailyProcess);
						createStoreRevenueDataForDates(dateSDF.format(date), dateSDF.format(date), entityId, isDailyProcess);
					}
				}
				long endTime = System.currentTimeMillis();
				log.log(Level.INFO, "Finished to create apd_visitor Performance Dashboard for Day " + date + " in "
						+ (endTime - startTime) + "ms");
			}
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getLocalizedMessage(), e);
		}
	}

	// General utilities ---------------------------------------------------------------------------------------------------------------------------
	public double calculateDiffTime(Date quit, Date enter) {
		return (quit.getTime() - enter.getTime());
	}

	@Deprecated
	public void saveIndicatorAliasSet(List<DashboardIndicatorAlias> aliases) throws ASException {}

	public void saveIndicatorSet(Map<DashboardIndicatorData, DashboardIndicatorData> indicatorsSet) throws ASException {

		//List<DashboardIndicatorAlias> aliases = createAliasList(indicatorsSet);
		//saveIndicatorAliasSet(aliases);

		dao.createOrUpdate(null, CollectionFactory.createList(indicatorsSet.values()), true);
	}

	@Deprecated
	public List<DashboardIndicatorAlias> createAliasList(Map<DashboardIndicatorData, DashboardIndicatorData> indicatorsSet)
			throws ASException {
		/*List<DashboardIndicatorAlias> aliases = CollectionFactory.createList();

		for(DashboardIndicatorData data : indicatorsSet.values()) {
			DashboardIndicatorAlias alias = new DashboardIndicatorAlias(
					data.getEntityId(), data.getEntityKind(), data.getScreenName(), data.getElementId(),
					data.getElementName(), data.getElementSubId(), data.getElementSubName(), data.getSubentityId(),
					data.getSubentityName());
			alias.setKey(diAliasDao.createKey(alias));
			if(!aliases.contains(alias)) aliases.add(alias);

			alias = new DashboardIndicatorAlias(data.getEntityId(), data.getEntityKind(), data.getScreenName(),
					data.getElementId().replace("ticket_", "promo_"), data.getElementName(), data.getElementSubId(),
					data.getElementSubName(), data.getSubentityId(), data.getSubentityName());
			alias.setKey(diAliasDao.createKey(alias));
			if(!aliases.contains(alias)) aliases.add(alias);
		}

		return aliases;*/return null;
	}
	
	/**
	 * Creates dashboard indicators for tickets: daily &amp; hourly indicators.
	 * @param fromDate - The starting date to create indicators.
	 * @param toDate - The last date to create indicators.
	 * @param storeId - The store which indicators are being created.
	 * @param deletePreviousRecords - If previous indicators must be removed.
	 * @throws ASException - If something goes wrong.
	 * @throws ParseException - If a date has an invalid format.
	 */
	public void createStoreTicketDataForDates(String fromDate, String toDate, String storeId,
			boolean deletePreviousRecords) throws ASException, ParseException{
		
		log.log(Level.INFO, "Starting to create store tickets Dashboard for Day " + fromDate + " to: " + toDate +"..." );
		long startTime = System.currentTimeMillis();
		
		try {
			Map<DashboardIndicatorData, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();
			List<StoreTicket> tickets =  stDao.getUsingStoreIdAndDatesAndRange(
					storeId, fromDate, toDate, null, null, false);
			
			Store store = storeDao.get(storeId);
			if( store != null ) {
				
				if(deletePreviousRecords) {
					for(DashboardIndicatorData did : dao.getUsingFilters(null, null, "apd_visitor",
							ELEMENT_SUB_ID_TOTAL_TICKETS, null, storeId, null, fromDate, toDate, null, null, null, null,
							null, null, null, null))
						dao.delete(did);
				}
				
				for( StoreTicket ticket: tickets){
					
					DashboardIndicatorData obj;
					String forDate = ticket.getDate();

					// visitor_total_tickets --------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					obj = buildBasicDashboardIndicatorData("apd_visitor", "Visitantes", ELEMENT_SUB_ID_TOTAL_TICKETS,
							ELEMENT_SUB_NAME_TICKETS, sdf.parse(ticket.getDate()), DashboardIndicatorData.PERIOD_TYPE_DAILY,
							store.getShoppingId(), store, null, null, store.getBrandId(), EntityKind.KIND_BRAND,
							forDate);
					if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
					else indicatorsSet.put(obj, obj);
					obj.setDoubleValue(obj.getDoubleValue() + ticket.getQty());
					
					// visitor_hourly_tickets -------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					List<StoreTicketByHour> hours = sthDao.getUsingStoreIdAndDateAndRange(store.getIdentifier(), ticket.getDate(), "00:00", "23:00", null, null, true);
					for( StoreTicketByHour th : hours ) {
						obj = buildBasicDashboardIndicatorData("apd_visitor", "Visitantes", ELEMENT_SUB_ID_HOURLY_TICKETS,
								ELEMENT_SUB_NAME_TICKETS, sdf2.parse(th.getDate() + " " + th.getHour()),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, store.getShoppingId(), store, null, null,
								store.getBrandId(), EntityKind.KIND_BRAND, forDate);
						if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
						else indicatorsSet.put(obj, obj);
						obj.setDoubleValue(obj.getDoubleValue() + th.getQty());
					}//creates daily indicators
				}//creates indicators for the given tickets
				log.log(Level.INFO, "Starting Write Procedure for " +indicatorsSet.size() +" indicators ...");
				// Finally, save all the information
				saveIndicatorSet(indicatorsSet);
			} else {
				
				log.log(Level.INFO, "Store with id " + storeId + " not found!");
			}// Store not founds
		} catch (ASException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}//if something goes wrong
		long endTime = System.currentTimeMillis();
		log.log(Level.INFO, "Finished to create store tickets Dashboard for Day " + fromDate + " to: " + toDate + " total time: "+ (endTime - startTime) + "ms");
	}//createStoreTicketDataForDates
	
	/**
	 * Creates dashboard indicators for the given items; daily &amp; hourly.
	 * @param fromDate - Begining date to create indicators.
	 * @param toDate - Limit date to create indicators.
	 * @param storeId - The store for which indicators are being created.
	 * @param deletePreviousRecords - if previous indicators are being created.
	 * @throws ASException - If something goes wrong.
	 * @throws ParseException - If any date has an invalid date.
	 */
	public void createStoreItemDataForDates(String fromDate,String toDate, String storeId, boolean deletePreviousRecords)
			throws ASException, ParseException {
		
		log.log(Level.INFO, "Starting to create store items Dashboard for Day " + fromDate + " to: " + toDate +"..." );
		long startTime = System.currentTimeMillis();
		
		try {
			Map<DashboardIndicatorData, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();
			List<StoreItem> items =  siDao.getUsingStoreIdAndDatesAndRange(storeId, fromDate, toDate, null, null, false);
			
			Store store = storeDao.get(storeId);
			if( store != null ) {

				if(deletePreviousRecords) {
					for(DashboardIndicatorData did : dao.getUsingFilters(null, null, "apd_visitor", ELEMENT_SUB_ID_TOTAL_ITEMS,
							ELEMENT_SUB_NAME_ITEMS, storeId, null, fromDate, toDate, null, null, null, null, null, null, null, null))
						dao.delete(did);
				}
				
				for( StoreItem item: items){		
					
					DashboardIndicatorData obj;
					String forDate = item.getDate();

					// visitor_total_itemss --------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					obj = buildBasicDashboardIndicatorData("apd_visitor", "Visitantes", ELEMENT_SUB_ID_TOTAL_ITEMS,
							ELEMENT_SUB_NAME_ITEMS, sdf.parse(item.getDate()), DashboardIndicatorData.PERIOD_TYPE_DAILY,
							store.getShoppingId(), store, null, null, store.getBrandId(), EntityKind.KIND_BRAND,
							forDate);
					if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
					else indicatorsSet.put(obj, obj);
					obj.setDoubleValue(obj.getDoubleValue() + item.getQty());
					
					// visitor_hourly_tickets -------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					List<StoreItemByHour> hours = sihDao.getUsingStoreIdAndDateAndRange(store.getIdentifier(), item.getDate(), "00:00", "23:00", null, null, true);
					for(StoreItemByHour th : hours ) {
						obj = buildBasicDashboardIndicatorData("apd_visitor", "Visitantes", ELEMENT_SUB_ID_HOURLY_ITEMS,
								ELEMENT_SUB_NAME_ITEMS, sdf2.parse(th.getDate() + " " + th.getHour()),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, store.getShoppingId(), store, null, null,
								store.getBrandId(), EntityKind.KIND_BRAND, forDate);
						if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
						else indicatorsSet.put(obj, obj);
						obj.setDoubleValue(obj.getDoubleValue() + th.getQty());
					}//creates hourly items
				}//creates indicators for all items
				log.log(Level.INFO, "Starting Write Procedure for " +indicatorsSet.size() +" indicators...");
				// Finally, save all the information
				saveIndicatorSet(indicatorsSet);
			} else {
				log.log(Level.INFO, "Store with id " + storeId + " not found!");
			}// Store not found
		} catch (ASException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}//if something goes wrong
		long endTime = System.currentTimeMillis();
		log.log(Level.INFO, "Finished to create store itemss Dashboard for Day " + fromDate + " to: " + toDate + " total time: "+ (endTime - startTime) + "ms");
	}//createStoreItemDataForDates

	/**
	 * Creates dashboard indicators for store revenue; daily &amp; hourly.
	 * @param fromDate - The starting date to create dashboard indicators.
	 * @param toDate - The last date to create indicators.
	 * @param storeId - The store for which the indicators will be created.
	 * @param deletePreviousRecords - If previous indicators need to be deleted.
	 * @throws ASException - If something goes wrong.
	 * @throws ParseException - If any date has an invalid format. 
	 */
	public void createStoreRevenueDataForDates(String fromDate,String toDate, String storeId,
			boolean deletePreviousRecords) throws ASException,ParseException{
		
		log.log(Level.INFO, "Starting to create store revenue Dashboard for Day " + fromDate + " to: " + toDate +"..." );
		long startTime = System.currentTimeMillis();
		
		try {
			Map<DashboardIndicatorData, DashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();
			List<StoreRevenue> revenues =  srDao.getUsingStoreIdAndDatesAndRange(storeId, fromDate, toDate, null,
					null, false);
			
			Store store = storeDao.get(storeId);
			if( store != null ) {
				if(deletePreviousRecords) {
					for(DashboardIndicatorData did : dao.getUsingFilters(null, null, "apd_visitor",
							"visitor_total_revenue", null, storeId, null, fromDate, toDate, null, null, null, null,
							null, null, null, null))
						dao.delete(did);
				}
				for( StoreRevenue revenue: revenues){			
					DashboardIndicatorData obj;
					String forDate = revenue.getDate();
					// visitor_total_revenues --------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					obj = buildBasicDashboardIndicatorData("apd_visitor", "Visitantes", ELEMENT_SUB_ID_TOTAL_REVENUE,
							ELEMENT_SUB_NAME_REVENUE, sdf.parse(revenue.getDate()), DashboardIndicatorData.PERIOD_TYPE_DAILY,
							store.getShoppingId(), store, null, null, store.getBrandId(), EntityKind.KIND_BRAND,
							forDate);
					if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
					else indicatorsSet.put(obj, obj);
					obj.setDoubleValue(obj.getDoubleValue() + revenue.getQty());
					// visitor_hourly_revenue -------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------------------
					List<StoreRevenueByHour> hours = srhDao.getUsingStoreIdAndDateAndRange(store.getIdentifier(), revenue.getDate(), "00:00", "23:00", null, null, true);
					for(StoreRevenueByHour th : hours ) {
						obj = buildBasicDashboardIndicatorData("apd_visitor", "Visitantes", ELEMENT_SUB_ID_HOURLY_REVENUE,
								ELEMENT_SUB_NAME_REVENUE, sdf2.parse(th.getDate() + " " + th.getHour()),
								DashboardIndicatorData.PERIOD_TYPE_DAILY, store.getShoppingId(), store, null, null,
								store.getBrandId(), EntityKind.KIND_BRAND, forDate);
						if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
						else indicatorsSet.put(obj, obj);
						obj.setDoubleValue(obj.getDoubleValue() + th.getQty());
					}
				}
				log.log(Level.INFO, "Starting Write Procedure for " +indicatorsSet.size() +" indicators...");

				// Finally, save all the information
				saveIndicatorSet(indicatorsSet);

			} else {
				// Store not found
				log.log(Level.INFO, "Store with id " + storeId + " not found!");
			}
			
		} catch (ASException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		
		long endTime = System.currentTimeMillis();
		log.log(Level.INFO, "Finished to create store revenue Dashboard for Day " + fromDate + " to: " + toDate
				+ " total time: "+ (endTime - startTime) + "ms");
	}
	
	public DashboardIndicatorData buildBasicDashboardIndicatorData(String elementId, String elementName,
			String elementSubId, String elementSubName, Date date, String periodType, String shoppingId,
			Store store, Shopping shopping, String subentityName, String entityId, Integer entityKind,
			String forDate) throws ASException, ParseException {

		DashboardIndicatorData obj = new DashboardIndicatorData();
		obj.setEntityId(entityId);
		obj.setEntityKind(entityKind);
		
		obj.setElementId(elementId);
		obj.setElementName(elementName);
		obj.setElementSubId(elementSubId);
		obj.setElementSubName(elementSubName);
		
		obj.setTimeZone(getTimeZone(date));
		
		obj.setStringDate(forDate);
		
		CALENDAR.clear();
		CALENDAR.setTime(date);
		obj.setDayOfWeek(CALENDAR.get(Calendar.DAY_OF_WEEK));
		obj.setDate(date);
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

	public int getTimeZone(Date date) {
		CALENDAR.clear();
		CALENDAR.setTime(date);
		return CALENDAR.get(Calendar.HOUR_OF_DAY);
	}

	public void buildCaches(boolean withMacVendor) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, ASException {

		// Prepares Store cache
		storeCache = CollectionFactory.createMap();
		List<Store> stores = storeDao.getUsingLastUpdateStatusAndRange(null, null, false,
				Arrays.asList(StatusAware.STATUS_ENABLED), null, null, null, false);
		for( Store store : stores ) {
			if( StringUtils.hasText(store.getExternalId())) 
				storeCache.put(store.getExternalId(), store);
			storeCache.put(store.getIdentifier(), store);
		}

		List<Shopping> shoppings = shoppingDao.getUsingLastUpdateStatusAndRange(null, null, false,
				Arrays.asList(StatusAware.STATUS_ENABLED), null, null, null, false);
		shoppingCache = CollectionFactory.createMap();
		for(Shopping shopping : shoppings ) {
			shoppingCache.put(shopping.getIdentifier(), shopping);
		}

		List<InnerZone> zones = innerzoneDao.getUsingLastUpdateStatusAndRange(null, null, false,
				Arrays.asList(StatusAware.STATUS_ENABLED), null, null, null, false);
		zoneCache = CollectionFactory.createMap();
		for(InnerZone zone : zones ) {
			zoneCache.put(zone.getIdentifier(), zone);
		}

		List<WifiSpot> wifiSpots = wifiSpotDao.getUsingLastUpdateStatusAndRange(null, null, false,
				Arrays.asList(StatusAware.STATUS_ENABLED), null, null, null, false);
		wifiSpotCache = CollectionFactory.createMap();
		for(WifiSpot wifiSpot : wifiSpots) {
			wifiSpotCache.put(wifiSpot.getIdentifier(), wifiSpot);
		}

		List<FloorMap> floorMaps = floorMapDao.getUsingLastUpdateStatusAndRange(null, null, false,
				Arrays.asList(StatusAware.STATUS_ENABLED), null, null, null, false);
		floorMapCache = CollectionFactory.createMap();
		for(FloorMap floorMap : floorMaps) {
			floorMapCache.put(floorMap.getIdentifier(), floorMap);
		}

		if( withMacVendor ) {
			List<MacVendor> macVendors = macVendorDao.getUsingLastUpdateStatusAndRange(null, null, false,
					Arrays.asList(StatusAware.STATUS_ENABLED), null, null, null, false);
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

}
