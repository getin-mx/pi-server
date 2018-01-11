package mobi.allshoppings.apdevice.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.apdevice.APDVisitHelper;
import mobi.allshoppings.apdevice.APHHelper;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.APDMABlackListDAO;
import mobi.allshoppings.dao.APDMAEmployeeDAO;
import mobi.allshoppings.dao.APDVisitDAO;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.dao.InnerZoneDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDMABlackList;
import mobi.allshoppings.model.APDMAEmployee;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.InnerZone;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.CollectionFactory;

/**
 * This class is responsible for building visits from APHEntries.
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @author Matias Hapanowics
 * @version 1.5, january 2018
 * @since Allshoppings
 */
public class APDVisitHelperImpl implements APDVisitHelper {

	/**
	 * Auxiliary calendars for visit & peasant generation.
	 */
	private final Calendar START_CALENDAR = Calendar.getInstance();
	private final Calendar END_CALENDAR = Calendar.getInstance();
	private final Calendar WORK_CALENDAR = Calendar.getInstance();
	
	public static final String ALGORITHM_MARKII = "markII";
	
	private static final Logger log = Logger.getLogger(APDVisitHelperImpl.class.getName());
	private static final SimpleDateFormat tf2 = new SimpleDateFormat("HHmm");
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
	
	/**
	 * Minimun required percentage to consider a peasant as a visit. At least, 25% of the total
	 * time he was in range must be in range for visit (not just one slot in range).
	 */
	private static final byte VISIT_PERCENTAGE = 25;
	
	/**
	 * Container list for banned MAC addresses.
	 */
	private static final List<String> BANNED = Arrays.asList("00:00:00:00:00:00");
	private static final short MILLIS_TO_TWENTY_SECONDS_SLOT = 20000;
	private static final short MAXIMUM_TIME_SLOTS = 6 *60 *60 /20;
	
	private static final List<String> INVALID_MACS;
	private static final List<String> INVALID_VENDORS;
	
	static {
		sdf.setTimeZone(GMT);
		INVALID_MACS = CollectionFactory.createList(new String[] {
				"00:00:00:00:00:00", "FF:FF:FF:FF:FF:FF"
		});
		INVALID_VENDORS = CollectionFactory.createList(new String[] {
				"DA:A1:19", "92:68:C3", "9C:93:4E", "70:B3:D5:7E:B", "1C:7D:22", "08:00:72", "08:00:37", "00:00:AA",
				"00:00:0", "F8:D0:27", "B0:E8:92", "AC:18:26", "A4:EE:57", "9C:AE:D3", "64:EB:8C", "44:D2:44",
				"38:9D:92", "00:26:AB", "00:00:48", "FC:3F:DB", "FC:15:B4", "F4:CE:46", "F4:30:B9", "F4:03:43",
				"F0:92:1C", "EC:EB:B8", "EC:B1:D7", "EC:9A:74", "EC:8E:B5", "E8:F7:24", "E8:39:35", "E4:11:5B",
				"E0:07:1B", "DC:4A:3E", "D8:D3:85", "D8:9D:67", "D8:94:03", "D4:C9:EF", "D4:85:64", "D0:BF:9C",
				"D0:7E:28", "D0:67:26", "CC:3E:5F", "C8:D3:FF", "C8:CB:B8", "C8:B5:AD", "C4:34:6B", "BC:EA:FA",
				"B8:AF:67", "B4:B5:2F", "B4:99:BA", "B0:5A:DA", "AC:E2:D3", "AC:16:2D", "A8:BD:27", "A4:5D:36",
				"A0:D3:C1", "A0:B3:CC", "A0:8C:FD", "A0:48:1C", "A0:2B:B8", "A0:1D:48", "9C:DC:71", "9C:B6:54",
				"9C:8E:99", "98:F2:B3", "98:E7:F4", "98:4B:E1", "94:F1:28", "94:57:A5", "94:3F:C2", "94:18:82",
				"8C:DC:D4", "88:51:FB", "84:34:97", "80:CE:62", "80:C1:6E", "80:8D:B7", "78:E7:D1", "78:E3:B5",
				"78:AC:C0", "78:48:59", "74:46:A0", "70:5A:0F", "70:10:6F", "6C:C2:17", "6C:3B:E5", "68:B5:99",
				"64:51:06", "64:31:50", "5C:B9:01", "5C:8A:38", "58:20:B1", "50:65:F3", "48:DF:37", "48:BA:4E",
				"48:0F:CF", "44:48:C1", "44:31:92", "44:1E:A1", "40:B9:3C", "40:B0:34", "40:A8:F0", "3C:D9:2B", 
				"3C:A8:2A", "3C:52:82", "3C:4A:92", "38:EA:A7", "38:63:BB", "38:17:C3", "34:FC:B9", "34:64:A9",
				"30:E1:71", "30:8D:99", "2C:76:8A", "2C:59:E5", "2C:44:FD", "2C:41:38", "2C:27:D7", "2C:23:3A",
				"28:92:4A", "28:80:23", "24:F2:7F", "24:BE:05", "20:A6:CD", "1C:C1:DE", "1C:98:EC", "18:A9:05",
				"18:60:24", "14:58:D0", "14:02:EC", "10:60:4B", "10:1F:74", "08:2E:5F", "08:00:09", "04:09:73",
				"00:FD:45", "00:9C:02", "00:80:A0", "00:80:5F", "00:60:B0", "00:50:8B", "00:30:C1", "00:30:6E",
				"00:26:55", "00:25:B3", "00:24:81", "00:23:7D", "00:22:64", "00:21:5A", "00:1F:29", "00:1E:0B",
				"00:1C:C4", "00:1B:78", "00:1A:4B", "00:19:BB", "00:18:FE", "00:18:71", "00:17:A4", "00:17:08",
				"00:16:35", "00:15:60", "00:14:C2", "00:14:38", "00:13:21", "00:12:79", "00:11:85", "00:11:0A",
				"00:10:E3", "00:10:83", "00:0F:61", "00:0F:20", "00:0E:B3", "00:0E:7F", "00:0D:9D", "00:0B:CD",
				"00:0A:57", "00:08:C7", "00:08:83", "00:08:02", "00:04:EA", "00:02:A5", "10:00:00:00", "10:00:00:0",
				"F8:DB:88", "F8:CA:B8", "F8:BC:12", "F8:B1:56", "F4:8E:38", "F0:4D:A2", "F0:1F:AF", "EC:F4:BB",
				"E4:F0:04", "E0:DB:55", "E0:D8:48", "D8:9E:F3", "D4:BE:D9", "D4:AE:52", "D4:81:D7", "D0:94:66",
				"D0:67:E5", "D0:43:1E", "C8:1F:66", "BC:30:5B", "B8:CA:3A", "B8:AC:6F", "B8:2A:72", "B4:E1:0F",
				"B0:83:FE", "A4:BA:DB", "A4:4C:C8", "A4:1F:72", "98:90:96", "98:40:BB", "90:B1:1C", "8C:EC:4B",
				"8C:CF:09", "84:8F:69", "84:7B:EB", "84:2B:2B", "80:18:44", "7C:C9:5A", "78:45:C4", "78:2B:CB",
				"74:E6:E2", "74:86:7A", "64:00:6A", "5C:F9:DD", "5C:26:0A", "58:8A:5A", "54:9F:35", "50:9A:4C",
				"4C:76:25", "48:4D:7E", "44:A8:42", "40:5C:FD", "34:E6:D7", "34:17:EB", "28:F1:0E", "24:B6:FD",
				"20:47:47", "20:04:0F", "1C:40:24", "18:FB:7B", "18:DB:F2", "18:A9:9B", "18:66:DA", "18:03:73",
				"14:FE:B5", "14:B3:1F", "14:9E:CF", "14:18:77", "10:98:36", "10:7D:1A", "08:00:1B", "00:C0:4F",
				"00:B0:D0", "60:48:", "00:26:B9", "00:25:BD", "25:64", "24:00:00:00:00", "00:23:AE", "22:19",
				"00:21:9B", "21:70", "00:1E:C9", "00:1E:4F", "00:1D:09", "00:1C:23", "00:1A:A0", "00:19:B9",
				"00:18:8B", "00:16:F0", "00:15:C5", "15:30", "14:22", "13:72", "12:48", "00:12:3F", "11:43",
				"00:0F:1F", "00:0D:56", "00:0B:DB", "87:4", "00:06:5B", "14:4", "97", "FC:CF:62", "E4:1F:13",
				"A8:97:DC", "98:BE:94", "90:4E:91:7", "74:99:75", "6C:AE:8B", "5C:F3:FC", "40:F2:E9", "34:40:B5",
				"10:00:5A", "08:17:F4", "08:00:5A", "60:94", "50:76", "25:03", "22:00", "00:21:5E", "20:35",
				"00:1A:64", "00:18:B1", "00:17:EF", "00:14:5E", "11:25", "00:10:D9", "00:0D:60", "00:09:6B",
				"62:9", "00:04:AC", "25:5", "C4:2F:90", "C0:56:E3", "BC:AD:28", "B4:A3:82", "A4:14:37",
				"54:C4:15", "4C:BD:8F", "44:19:B6", "28:57:BE", "18:68:CB", "68:C4:4D"
		});
	}
	
	@Autowired
	private APDVisitDAO apdvDao;
	
	@Autowired
	private APDeviceDAO apdDao;

	@Autowired
	private APDAssignationDAO apdaDao;
	
	@Autowired
	private ShoppingDAO shoppingDao;

	@Autowired
	private StoreDAO storeDao;
	
	@Autowired
	private APHHelper aphHelper;
	
	@Autowired
	private APDMABlackListDAO apmaBlDao;
	
	@Autowired
	private APDMAEmployeeDAO apmaEDao;

	@Autowired
	private DashboardIndicatorDataDAO didDao;
	
	@Autowired
	private InnerZoneDAO innerzoneDao;

	@Autowired
	private DashboardAPDeviceMapperService mapper;
	
	/**
	 * Writes a list of APDVisits in the database
	 * 
	 * @param brandIds
	 *            List of brands to process
	 * @param storeIds
	 *            List of stores to process
	 * @param fromDate
	 *            Date to start processing
	 * @param toDate
	 *            Date to stop processing
	 * @throws ASException
	 */
	@Override
	public void generateAPDVisits(List<String> brandIds, List<String> storeIds, List<String> ignoredBrands,
			Date fromDate, Date toDate, boolean deletePreviousRecords, boolean updateDashboards,
			boolean onlyEmployees, boolean onlyDashboards, boolean isDailyProcess, byte startHour,
			byte endHour) throws ASException {

		List<Store> stores = null;
		Map<String, APDevice> apdCache = CollectionFactory.createMap();
		Map<String, APDAssignation> assignmentsCache = CollectionFactory.createMap();
		DumperHelper<APHEntry> dumpHelper;
		boolean cacheBuilt = false;

		// Phase 1, determines entities to process ---------------------------------------------------------------------
		if(!CollectionUtils.isEmpty(storeIds)) {
			stores = storeDao.getUsingIdList(storeIds);
		} if(!CollectionUtils.isEmpty(brandIds)) {
			if(stores == null) stores = CollectionFactory.createList();
			for(String brandId : brandIds )
				stores.addAll(storeDao.getUsingBrandAndStatus(brandId, StatusHelper.statusActive(), null));
		} if(stores == null) {
			stores = CollectionFactory.createList();
			stores.addAll(storeDao.getUsingBrandAndStatus(null, StatusHelper.statusActive(), null));
		}
		
		List<String> eids = CollectionFactory.createList();
		for(Store store : stores) {
			if(!ignoredBrands.contains(store.getBrandId()))
				eids.add(store.getIdentifier());
		}

		Map<String, Integer> entities = getEntities(null, null, eids);

		// Phase 2, Gets entities to process ---------------------------------------------------------------------

		DumperHelper<APDVisit> apdvDumper = null;
		if(startHour < 0) apdvDumper = new DumpFactory<APDVisit>().build(null, APDVisit.class, false);
		
		boolean reverse = fromDate.compareTo(toDate) > 0;
		
		Date curDate = new Date(fromDate.getTime());
		String sFromDate = sdf.format(fromDate);
		Date toZonedDate = new Date(toDate.getTime());
		boolean overtime = false;
		boolean lastDate;
		
		int in;
		while( (!reverse && (curDate.before(toZonedDate) || (fromDate.equals(toZonedDate) &&
				curDate.equals(toZonedDate)))) || (reverse && (toZonedDate.before(curDate) ||
						toZonedDate.equals(fromDate) && toZonedDate.equals(curDate)))) {

			lastDate = curDate.getTime() +DAY_IN_MILLIS >= toZonedDate.getTime();
			
			try {
				log.log(Level.INFO, "entityIds are: " + entities);
				
				in = 0;
				for( String entityId : entities.keySet() ) {

					Integer entityKind = entities.get(entityId);
					String name = null;
					Store store = null;
					
					if( entityKind.equals(EntityKind.KIND_STORE)) {
						store = storeDao.get(entityId);
						name = store.getName();
					} else if ( entityKind.equals(EntityKind.KIND_INNER_ZONE)) {
						InnerZone iz = innerzoneDao.get(entityId);
						name = iz.getName();
						store = storeDao.get(iz.getEntityId());
					}
					
					log.log(Level.INFO, "Processing " + name + " for " + curDate + "...");

					TimeZone tz = TimeZone.getTimeZone(store.getTimezone());
					int offset = tz.getOffset(curDate.getTime()) * -1;
					if(!onlyDashboards && offset > 0 && toZonedDate.getTime() < toDate.getTime() +offset) {
						toZonedDate.setTime(toDate.getTime() +offset);
						overtime = true;
						lastDate = curDate.getTime() +DAY_IN_MILLIS >= toZonedDate.getTime();
					}//adds an time limit offset to match local timezone
					
					try {
						List<APDVisit> objs = CollectionFactory.createList();
						if(!onlyDashboards) {

							List<String> blackListMacs = getBlackListByStore(store);
							List<String> employeeListMacs = getEmployeeListByStore(store);
							
							blackListMacs.addAll(INVALID_MACS);
							
							// Determine which antennas are valid for this entity and date 
							String forDate = sdf.format(curDate);
							List<APDAssignation> assigs = apdaDao.getUsingEntityIdAndEntityKindAndDate(
									entityId, entityKind, curDate);
							if(CollectionUtils.isEmpty(assigs)) continue;
							assignmentsCache.clear();
							if( assigs.size() == 1 ) {

								assignmentsCache.put(assigs.get(0).getHostname(),
										assigs.get(0));
								if(!apdCache.containsKey(assigs.get(0).getHostname()))
									apdCache.put(assigs.get(0).getHostname(),
											apdDao.get(assigs.get(0).getHostname(), true));

								// Get APHE records
								log.log(Level.INFO, "Fetching APHEntries for " + name + " and " + forDate +
										" using " + assigs.get(0).getHostname() + "...");
								dumpHelper = new DumpFactory<APHEntry>().build(null, APHEntry.class, lastDate && overtime);
								dumpHelper.setFilter(assigs.get(0).getHostname());
								
								Iterator<APHEntry> i = integrateAPHE(dumpHelper, apdCache.get(
										assigs.get(0).getHostname()), forDate, tz, sFromDate, lastDate && overtime,
										START_CALENDAR, END_CALENDAR, startHour, endHour).iterator();
								dumpHelper.dispose();

								while( i.hasNext() ) {
									APHEntry entry = i.next();
									// Employee check
									if(!onlyEmployees || employeeListMacs.contains(entry.getMac().toUpperCase())) {
										List<APDVisit> visitList = aphEntryToVisits(entry, apdCache, assignmentsCache,
												blackListMacs, employeeListMacs, tz);
										for(APDVisit visit : visitList ) {
											if(!onlyEmployees ||
													visit.getCheckinType().equals(APDVisit.CHECKIN_EMPLOYEE)) {
												objs.add(visit);
											}
										}
									}
								}
							} else {
								Map<String, List<APHEntry>> cache = CollectionFactory.createMap();
								List<String> hostnames = CollectionFactory.createList();
								for( APDAssignation assig : assigs ) { 
									hostnames.add(assig.getHostname());
									assignmentsCache.put(assig.getHostname(), assig);
									if(!apdCache.containsKey(assig.getHostname()))
										apdCache.put(assig.getHostname(), apdDao.get(assig.getHostname(), true));
								}

								log.log(Level.INFO, "Fetching APHEntries for " + name + " and " + curDate + "...");
								log.log(Level.INFO, "Fetching APHEntries for " + hostnames + " and " + curDate + "...");
								// Get APHE records
								for( String hostname : hostnames ) {
									dumpHelper = new DumpFactory<APHEntry>().build(null, APHEntry.class, lastDate && overtime);
									dumpHelper.setFilter(hostname);
									
									List<APHEntry> i = integrateAPHE(dumpHelper, apdCache.get(hostname),
											forDate, tz, sFromDate, lastDate && overtime, START_CALENDAR, END_CALENDAR,
											startHour, endHour);
									dumpHelper.dispose();

									Map<APHEntry, List<String>> slotsToSplit = CollectionFactory.createMap();
									for(Iterator<APHEntry> it = i.iterator(); it.hasNext();) {
										APHEntry entry = it.next();
										if(cache.containsKey(entry.getMac())) {
											for(APHEntry e : cache.get(entry.getMac())) {
												for(String slot : e.getRssi().keySet()) {
													if(entry.getRssi().containsKey(slot)) {
														if(!slotsToSplit.containsKey(e))
															slotsToSplit.put(e, new ArrayList<String>());
														slotsToSplit.get(e).add(slot);
													}
												}
											}
											boolean originalDeleted = false;
											for(APHEntry toSplit : slotsToSplit.keySet()) {
												for(String slot : slotsToSplit.get(toSplit)) {
													Integer otherPow = toSplit.getRssi().get(slot);
													if(otherPow == null) continue;
													Integer thisPow = entry.getRssi().get(slot);
													if(thisPow == null) continue;
													boolean removingFromOther = thisPow > otherPow;
													APHEntry needsSplit = null;
													if(removingFromOther) {
														toSplit.getRssi().remove(slot);
														if(toSplit.getRssi().size() < 2) {
															cache.get(toSplit.getMac()).remove(toSplit);
															break;
														} else needsSplit = toSplit;
													} else {
														entry.getRssi().remove(slot);
														if(entry.getRssi().size() < 2) {
															it.remove();
															originalDeleted = true;
															break;
														} else needsSplit = entry;
													} if(needsSplit != null) {
														List<String> slots = CollectionFactory.createList(
																needsSplit.getRssi().keySet());
														Collections.sort(slots);
														Map<String, Integer> newRssi = CollectionFactory.createMap();
														int minRssi = Integer.MAX_VALUE;
														int maxRssi = Integer.MIN_VALUE;
														for(String separatedSlots : slots) {
															if(separatedSlots.compareTo(slot) > 0) {
																int rssi = needsSplit.getRssi().remove(separatedSlots);
																newRssi.put(separatedSlots, rssi);
																if(minRssi > rssi) minRssi = rssi;
																if(maxRssi < rssi) maxRssi = rssi;
															}
														} if(newRssi.size() >= 2) {
															APHEntry _new = new APHEntry();
															_new.setDataCount(newRssi.size());
															_new.setDate(needsSplit.getDate());
															_new.setHostname(needsSplit.getHostname());
															_new.setMac(needsSplit.getMac());
															_new.setMinRssi(minRssi);
															_new.setMaxRssi(maxRssi);
															_new.setRssi(newRssi);
															cache.get(_new.getMac()).add(_new);
														}
													}
												} if(originalDeleted) break;
											}
											slotsToSplit.clear();
										} else {
											cache.put(entry.getMac(), new ArrayList<APHEntry>());
											cache.get(entry.getMac()).add(entry);
										}
									}
								}

								log.log(Level.INFO, "Processing " + cache.size() + " APHEntries...");
								for(String mac :  cache.keySet()) {
									if(cache.get(mac).isEmpty()) continue;
									List<APDVisit> visitList = aphEntryToVisits(cache.get(mac), apdCache,
											assignmentsCache, blackListMacs, employeeListMacs, tz);
									for(APDVisit visit : visitList ) {
										if(!onlyEmployees || visit.getCheckinType().equals(APDVisit.CHECKIN_EMPLOYEE)) {
											objs.add(visit);
										}
									}
								}
							} if(objs.size() == 0) {
								log.log(Level.INFO, "No data found for " +name +", skipping...");
								continue;
							}

							if(startHour < 0) {
								log.log(Level.INFO, "Saving " + objs.size() + " APDVisits...");
								for( APDVisit obj : objs ) apdvDumper.dump(obj);
							}
						}
						
						// Try to update dashboard if needed
						if(updateDashboards && !onlyEmployees) {
							if(!cacheBuilt) {
								try {
									log.log(Level.INFO, "Building caches for dashboard mapper...");
									mapper.buildCaches(false);
									cacheBuilt = true;
								} catch( Exception e ) {
									throw ASExceptionHelper.defaultException(e.getMessage(), e);
								}
							} if(deletePreviousRecords) {
								if(overtime && lastDate) {//both true -> just delete the offset timezones
									byte offsetHours = (byte) (-offset /(1000 *60 *60));
									Date delDate = offsetHours < 0 ? new Date(curDate.getTime() -DAY_IN_MILLIS) :
										new Date(curDate.getTime());
									didDao.deleteUsingSubentityIdAndElementIdAndDateAndTimezoneOffset(entityId,
											Arrays.asList("apd_visitor", "apd_permanence", "apd_occupation" ),
											delDate, delDate, GMT, offsetHours);
								} else {//one was false -> delete the whole day (stills needs one more day) 
									didDao.deleteUsingSubentityIdAndElementIdAndDate(entityId,
											Arrays.asList("apd_visitor", "apd_permanence", "apd_occupation" ),
											curDate, curDate, GMT);
								}
							} if( objs.size() > 0 || onlyDashboards) {
								mapper.createAPDVisitPerformanceDashboardForDay(curDate,
										Arrays.asList(new String[] { entityId }), entityKind, objs, isDailyProcess,
										lastDate, startHour >= 0);
							}
						}
					} catch( Exception e ) {
						log.log(Level.SEVERE, e.getMessage(), e);
					}
					
					log.log(Level.INFO, "Progress: " +String.format("%.2f", ((++in *100d) /entities.size())
							*((curDate.getTime() -fromDate.getTime() +1d)
									/(toDate.getTime() -fromDate.getTime()))) +"%");

				} 
			} catch( Exception e1 ) {
				log.log(Level.SEVERE, e1.getMessage(), e1);
			}
			
			curDate = new Date(curDate.getTime() + DAY_IN_MILLIS *(reverse ? -1 : 1));
			if(startHour < 0) apdvDumper.flush();
			
		}
		
		if(startHour < 0) apdvDumper.dispose();
		
	}//generateAPDVisit
	
	/**
	 * Loads the APHE for the given antenna. Based on the specified local time, it discards any APHE
	 * outside the monitoring schedule and any APHE with only two or less identical MAC address.
	 * @param dumpHelper - The dump helper to load the APHE.
	 * @param calibration - The device description to load the monitoring schedule.
	 * @param entityId - The entityId for the entity whose APHE are being loaded.
	 * @param entityKind - The entity kind for the entity whose APHE are being loaded.
	 * @param forStringDate - The date for which the APHE are being loaded.
	 * @param tz - The entity's local timezone.
	 * @param begginingDate - The first date from where the APHE are being loaded.
	 * @param lastDay - If the given date is the last to load APHE. If so and if the local timezone requires it,
	 * loads APHE from the next date to prevent load missing data from the local timezone's prespective.
	 * @return List&lt;APHE&gt; - The loaded APHE for the given date (may include next a date's extra).
	 */
	public static List<APHEntry> integrateAPHE(DumperHelper<APHEntry> dumpHelper, APDevice calibration,
			String forStringDate, TimeZone tz, String begginingDate, boolean lastDay, Calendar startCal,
			Calendar endCal, short startHour, short endHour) {//TODO move to interface
		
		Map<String, APHEntry> map = CollectionFactory.createMap();
		List<APHEntry> res = CollectionFactory.createList();
		
		int year = Integer.parseInt(forStringDate.substring(0,4));
		int month = Integer.parseInt(forStringDate.substring(5,7)) -1;
		int day = Integer.parseInt(forStringDate.substring(8));
		
		int lowerLimit, higherLimit;
		
		startCal.clear();
		endCal.clear();
		
		startCal.setTimeZone(GMT);
		endCal.setTimeZone(GMT);
		
		startCal.set(year, month, day);
		endCal.set(year, month, day);
		endCal.add(Calendar.DATE, 1);
		endCal.add(Calendar.MILLISECOND, -1);

		int timezoneSlotsOffset = tz.getOffset(startCal.getTimeInMillis());
		
		Iterator<APHEntry> i = dumpHelper.iterator(startCal.getTime(), endCal.getTime(), lastDay);
			
		startCal.set(year, month, day, Integer.parseInt(calibration.getMonitorStart().substring(0, 2)),
				Integer.parseInt(calibration.getMonitorStart().substring(3, 5)));
		endCal.set(year, month, day, Integer.parseInt(calibration.getMonitorEnd().substring(0, 2)),
				Integer.parseInt(calibration.getMonitorEnd().substring(3, 5)));
		lowerLimit = (startCal.get(Calendar.MINUTE) *60 +startCal.get(Calendar.HOUR_OF_DAY) *60 *60) /20;
		higherLimit = (endCal.get(Calendar.MINUTE) *60 +endCal.get(Calendar.HOUR_OF_DAY) *60 *60) /20;
		if(higherLimit == 0) higherLimit = SLOT_NUMBER_IN_DAY;
		
		startCal.clear();
		startCal.set(Integer.parseInt(begginingDate.substring(0,4)), Integer.parseInt(begginingDate.substring(5,7)) -1,
				Integer.parseInt(begginingDate.substring(8)));
		
		timezoneSlotsOffset /= MILLIS_TO_TWENTY_SECONDS_SLOT;
		
		if(startHour >= 0) {
			startHour *= 60 *60 /20;
			endHour *= 60 *60 /20;
		}
		
		while(i.hasNext()) {
			APHEntry aphe = i.next();
			APHEntry mapped = map.get(aphe.getMac());
			if( mapped == null ) {
				mapped = new APHEntry();
				mapped.setCreationDateTime(aphe.getCreationDateTime());
				mapped.setDate(forStringDate);
				mapped.setDevicePlatform(aphe.getDevicePlatform());
				mapped.setHostname(aphe.getHostname());
				mapped.setMac(aphe.getMac());
				mapped.setKey(aphe.getKey());
			}
			
			// discards APHEntries outside the monitoring dates
			Map<String, Integer> rssi = aphe.getRssi();
			Map<String, Integer> newRssi = mapped.getRssi();
			Iterator<String> it = rssi.keySet().iterator();
			
			while(it.hasNext()) {
				String key = it.next();
				int slot = Integer.valueOf(key);
				int testSlot;
				slot += timezoneSlotsOffset;
				testSlot = slot;
				if(testSlot < 0) {
					endCal.clear();
					endCal.set(Integer.parseInt(aphe.getDate().substring(0, 4)),
							Integer.parseInt(aphe.getDate().substring(5, 7)) -1,
							Integer.parseInt(aphe.getDate().substring(8)));
					if(startCal.compareTo(endCal) >= 0) continue;
					testSlot += SLOT_NUMBER_IN_DAY;
				} else if(lastDay) continue;
				if(testSlot >= SLOT_NUMBER_IN_DAY) testSlot -= SLOT_NUMBER_IN_DAY;
				if(startHour >= 0 && (testSlot < startHour || testSlot > endHour)) continue;
				if(lowerLimit > higherLimit ?
						testSlot <= higherLimit || testSlot >= lowerLimit :
							testSlot >= lowerLimit && testSlot <= higherLimit) {
					Integer val = rssi.get(key);
					newRssi.put(String.valueOf(slot), val);
				}
			}
			mapped.setKey(aphe.getKey());//TODO quitar si el tamaño de RSSI es mayor a 6 hrs
			
			map.put(mapped.getMac(), mapped);
		}

		Iterator<APHEntry> ix = map.values().iterator();
		while(ix.hasNext()) {
			APHEntry aphe = ix.next();
			aphe.setDataCount(aphe.getRssi().size());
			if(aphe.getDataCount() < 2) continue;
			if(!BANNED.contains(aphe.getMac().toLowerCase())) {
				aphe.setMaxRssi(-9999);
				aphe.setMinRssi(0);
				Iterator<Integer> ix2 = aphe.getRssi().values().iterator();
				while(ix2.hasNext()) {
					Integer val = ix2.next();
					if( val < aphe.getMinRssi())
						aphe.setMinRssi(val);
					if( val > aphe.getMaxRssi())
						aphe.setMaxRssi(val);
				}
				// Controls banned mac addresses
				res.add(aphe);
			}
		}
		return res;
	}
	
	public Map<String, Integer> getEntities(List<String> shoppingIds, List<String> brandIds, List<String> storeIds) throws ASException {
		Map<String, Integer> ret = CollectionFactory.createMap();

		if( shoppingIds != null )
			for( String shopping : shoppingIds )
				ret.put(shopping, EntityKind.KIND_SHOPPING);
		
		if( brandIds != null )
			for( String brand : brandIds ) {
				List<Store> stores = storeDao.getUsingBrandAndStatus(brand, StatusHelper.statusActive(), null);
				for( Store store : stores )
					ret.put(store.getIdentifier(), EntityKind.KIND_STORE);
			}

		if( storeIds != null )
			for( String store : storeIds )
				ret.put(store, EntityKind.KIND_STORE);
		
		ret = aggregateZones(ret);
		
		return ret;
	}

	public Map<String, Integer> aggregateZones(Map<String, Integer> map) throws ASException {
		
		Map<String, Integer> newmap = CollectionFactory.createMap();
		Set<String> keys = CollectionFactory.createSet();
		keys.addAll(map.keySet());
		Iterator<String> i = keys.iterator();
		while(i.hasNext()) {
			String entityId = i.next();
			Integer entityKind = map.get(entityId);
			List<InnerZone> zones = innerzoneDao.getUsingEntityIdAndRange(entityId, entityKind, null, null, null, false);
			newmap = CollectionFactory.createMap();
			for( InnerZone zone : zones ) 
				newmap.put(zone.getIdentifier(), EntityKind.KIND_INNER_ZONE);
			newmap = aggregateZones(newmap);
			map.putAll(newmap);
		}
		
		return map;
	}
	
	/**
	 * 
	 * Generate a list of MacAddress of blacklist
	 * 
	 * @param store
	 * @return A list with MacAdress of blackList
	 * @throws ASException
	 */
	public List<String> getBlackListByStore(Store store) throws ASException{
		//declare list for macs
		List<String> macs = CollectionFactory.createList();
		log.log(Level.FINE, "Initial macs:  " + macs.size() + " macs");

		if( null != store ) {

			macs.clear();
			//--- Start black list ------------

			// Generic Black Lists
			List<APDMABlackList> blackListGen = apmaBlDao.getUsingEntityIdAndRange(null,
					null, null, null, null, false);
			for( APDMABlackList brand : blackListGen ) {
				if (!macs.contains(brand.getMac().toUpperCase().trim())){
					macs.add(brand.getMac().toUpperCase().trim());	
				}
			}
			log.log(Level.FINE,"(Generic) -- Load Generic black list: "
						+ blackListGen.size() + " macs");

			//Load blackListbyShopping for shopping
			if( StringUtils.hasText(store.getShoppingId())) {
				List<APDMABlackList> blackListbyShopping =
						apmaBlDao.getUsingEntityIdAndRange(store.getShoppingId(),
								EntityKind.KIND_SHOPPING, null, null, null, false);
				for( APDMABlackList shop : blackListbyShopping ) {
					if (!macs.contains(shop.getMac().toUpperCase().trim())){
						macs.add(shop.getMac().toUpperCase().trim());	
					}
				}
				log.log(Level.FINE,"(" +store.getIdentifier()
						+") -- Load black list for Shopping: " 
						+ blackListbyShopping.size() + " macs");
			}

			//Load blackListbyShopping for brand
			List<APDMABlackList> blackListbyBrand =
					apmaBlDao.getUsingEntityIdAndRange(store.getBrandId(),
							EntityKind.KIND_BRAND, null, null, null, false);
			for( APDMABlackList brand : blackListbyBrand ) {
				if (!macs.contains(brand.getMac().toUpperCase().trim())){
					macs.add(brand.getMac().toUpperCase().trim());	
				}
			}
			log.log(Level.FINE,"(" +store.getIdentifier()+
					") -- Load black list for Brand: " + blackListbyBrand.size()
					+ " macs");


			//Load blackListbyShopping for store
			List<APDMABlackList> blackListbyStore =
					apmaBlDao.getUsingEntityIdAndRange(store.getIdentifier(),
							EntityKind.KIND_STORE, null, null, null, false);
			for( APDMABlackList st : blackListbyStore ) {
				if (!macs.contains(st.getMac().toUpperCase().trim())){
					macs.add(st.getMac().toUpperCase().trim());	
				}
			}
			//Load blackListbyShopping for store
			log.log(Level.FINE,"(" +store.getIdentifier()+ ") -- Load black list for Store: " + blackListbyStore.size() + " macs");
		}

		log.log(Level.FINE, "TOTAL Blacklist Entries: " + macs.size() + " macs");

		//--- End black list --------------
		return macs;
	}
	
	/**
	 * 
	 * Generate a list of MacAddress of employees
	 * 
	 * @param store
	 * @return A list with MacAdress of employees
	 * @throws ASException
	 */
	public List<String> getEmployeeListByStore(Store store) throws ASException{
		//declare list for macs
		List<String> macs = CollectionFactory.createList();
		log.log(Level.FINE, "Initial macs:  " + macs.size() + " macs");

		if( null != store ) {
			macs.clear();
			//--- Start black list ------------
			//Load blackListbyShopping for shopping
			if( StringUtils.hasText(store.getShoppingId())) {
				List<APDMAEmployee> employeesbyShopping = apmaEDao.getUsingEntityIdAndRange(store.getShoppingId(), EntityKind.KIND_SHOPPING, null, null, null, false);
				for( APDMAEmployee emp_shop : employeesbyShopping ) {
					if (!macs.contains(emp_shop.getMac().toUpperCase().trim())){
						macs.add(emp_shop.getMac().toUpperCase().trim());	
					}
				}
				log.log(Level.FINE,"(" +store.getIdentifier()+ ") -- Load Employees in list for Shopping: " + employeesbyShopping.size() + " macs");
			}

			//Load blackListbyShopping for brand
			List<APDMAEmployee> employeesbyBrand = apmaEDao.getUsingEntityIdAndRange(store.getBrandId(), EntityKind.KIND_BRAND, null, null, null, false);
			for( APDMAEmployee emp_brand : employeesbyBrand ) {
				if (!macs.contains(emp_brand.getMac().toUpperCase().trim())){
					macs.add(emp_brand.getMac().toUpperCase().trim());	
				}
			}
			log.log(Level.FINE,"(" +store.getIdentifier()+ ") -- Load Employees in list for Brand: " + employeesbyBrand.size() + " macs");


			//Load blackListbyShopping for store
			List<APDMAEmployee> employeesbyStore = apmaEDao.getUsingEntityIdAndRange(store.getIdentifier(), EntityKind.KIND_STORE, null, null, null, false);
			for( APDMAEmployee emp_sto : employeesbyStore ) {
				if (!macs.contains(emp_sto.getMac().toUpperCase().trim())){
					macs.add(emp_sto.getMac().toUpperCase().trim());	
				}
			}
			//Load blackListbyShopping for store
			log.log(Level.FINE,"(" +store.getIdentifier()+ ") -- Load Employees list for Store: " + employeesbyStore.size() + " macs");
		}

		log.log(Level.FINE, "TOTAL Employees: " + macs.size() + " macs");

		//--- End black list --------------
		return macs;
	}
	
	/**
	 * Converts an APHEntry to a visit list
	 * 
	 * @param entry
	 *            The entry to convert
	 * @return A list with created visits
	 * @throws ASException
	 */
	@Override
	public List<APDVisit> aphEntryToVisits(APHEntry entry, Map<String, APDevice> apdCache,
			Map<String, APDAssignation> assignmentsCache,List<String> blackListMacs,
			List<String> employeeListMacs, TimeZone tz) throws ASException {
		List<APHEntry> entries = CollectionFactory.createList();
		entries.add(entry);
		return aphEntryToVisits(entries, apdCache, assignmentsCache, blackListMacs,
				employeeListMacs, tz);
	}	

	/**
	 * Converts an APHEntry to a visit list
	 * 
	 * @param entry
	 *            The entry to convert
	 * @return A list with created visits
	 * @throws ASException
	 */
	@SuppressWarnings("unused")
	@Override
	public List<APDVisit> aphEntryToVisits(List<APHEntry> entries, Map<String, APDevice> apdCache,
			Map<String, APDAssignation> assignmentsCache, List<String> blackListMacs,
			List<String> employeeListMacs, TimeZone tz) throws ASException {

		/*int COMMON = 0;
		int MARKII = 1;

		try {
			Date markIIDate = sdf.parse("2017-04-01");

			int mode = 0;
			for( APHEntry entry : entries ) {
				if( systemConfiguration.getMark2APDevices().contains(entry.getHostname()))
					mode = MARKII;
				APDAssignation assig = assignmentsCache.get(entry.getHostname());
				if( null != assig && assig.getFromDate().after(markIIDate))
					mode = MARKII;
			}

			if(ALGORITHM_MARKII.equals(systemConfiguration.getForceAlgorithm()))
				mode = MARKII;
			
			switch(mode) {
			case 1:*/
				return aphEntryToVisitsMarkII(entries, apdCache, assignmentsCache,
						blackListMacs, employeeListMacs, tz);
			/*default:
				return aphEntryToVisitsCommon(entries, apdCache, assignmentsCache,
						blackListMacs, employeeListMacs, tz);
			}
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}*/
	}

	/**
	 * Converts an APHEntry to a visit list (Commons version)
	 * 
	 * @param entry
	 *            The entry to convert
	 * @return A list with created visits
	 * @throws ASException
	 */
	private List<APDVisit> aphEntryToVisitsMarkII(List<APHEntry> entries, Map<String, APDevice> apdCache,
			Map<String, APDAssignation> assignmentsCache, List<String> blackListMacs,
			List<String> employeeListMacs, TimeZone tz) throws ASException {

		// Validates entries
		if( CollectionUtils.isEmpty(entries)) throw ASExceptionHelper.invalidArgumentsException();
		List<APDVisit> res = CollectionFactory.createList();

		// Work variables
		Boolean isEmployee = false;
		
		// Merges all the time slots
		List<Integer> slots = null;
		if(entries.size() > 1) {
			slots = CollectionFactory.createList();
			Set<Integer> rawSlots = CollectionFactory.createSet();
			for( APHEntry entry : entries ) {
				// Checks for BlackList
				String mac = entry.getMac().toUpperCase().trim();
				if(!blackListMacs.contains(mac)){
					boolean invalid = false;
					for(String vendor : INVALID_VENDORS) {
						if(mac.startsWith(vendor)) {
							invalid = true;
							break;
						}
					} if(invalid) continue;
					rawSlots.addAll(aphHelper.artificiateRSSI(entry, apdCache.get(entry.getHostname())));
					// If the mac address is contained in the employee list,
					// then activates the empoloyee flag
					isEmployee = employeeListMacs.contains(entry.getMac().toUpperCase().trim());
				}
			}
			slots.addAll(rawSlots);
			Collections.sort(slots);
		} else {
			String mac = entries.get(0).getMac().toUpperCase().trim();
			if(!blackListMacs.contains(mac)) {
				// Checks for BlackList
				for(String vendor : INVALID_VENDORS) {
					if(mac.startsWith(vendor)) return res;
				}
				slots = aphHelper.artificiateRSSI(entries.get(0), apdCache.get(entries.get(0).getHostname()));
				// If the mac address is contained in the employee list,
				// then activates the empoloyee flag
				isEmployee = employeeListMacs.contains(entries.get(0).getMac().toUpperCase().trim());
			}
		}
		
		if(slots == null || slots.isEmpty()) return res;
		
		// if the device has been here for six hours or more, then it doesnt count.
		// Every slot represents 20 s, so 6H = 6 *60 *60 s; then maximum number of
		// slots is 6 *60 *60 /20 = 1080
		if(entries.size() == 1 && slots.size() >= MAXIMUM_TIME_SLOTS) return res;
		
		// Adds all the devices in the cache
		Map<String, APDevice> apd = CollectionFactory.createMap();
		if( apdCache == null || apdCache.size() == 0 ) {
			for( APHEntry entry : entries ) apd.put(entry.getHostname(), apdDao.get(entry.getHostname(), true));
		} else apd.putAll(apdCache);
		
		// Adds all the assignments in the cache
		Map<String,APDAssignation> assignments = CollectionFactory.createMap();
		if( assignmentsCache == null || assignmentsCache.size() == 0 ) {
			for( APHEntry entry : entries ) {
				try {
					assignments.put(entry.getHostname(), apdaDao.getOneUsingHostnameAndDate(entry.getHostname(),
							sdf.parse(entry.getDate())));
				} catch( Exception e ) {
					log.log(Level.SEVERE, "Error parsing date " + entry.getDate(), e);
				}
			}
		} else assignments.putAll(assignmentsCache);

		// Defines temporary work variables
		Integer lastSlot = null;
		Integer lastVisitSlot = null;
		Integer lastPeasantSlot = null;
		APDVisit currentVisit = null;
		APDVisit currentPeasant = null;
		APHEntry curEntry = null;
		
		// Now iterate the slots on each entry
		for(Integer slot : slots ) {
			try {
				// Identifies the power and device
				Integer value = null;
				for( APHEntry entry : entries ) {
					Integer tValue = entry.getArtificialRssi().get(String.valueOf(slot));
					if( tValue != null && (value == null || tValue > value)) {
						value = tValue;
						curEntry = entry;
					}
				}
				// Controls invalid values
				if(value == null) {
					// Updates the last slot
					continue;
				} if( value > -1 ) return CollectionFactory.createList();
				
				Date curDate = aphHelper.slotToDate(curEntry, slot, tz);
				APDevice dev = apd.get(curEntry.getHostname());
				dev.completeDefaults();
				
				// Closes open visits in case of slot continuity disruption
				if(lastSlot != null) {
					int testSlot = slot;
					if(testSlot < 0) testSlot += SLOT_NUMBER_IN_DAY;
					else if(testSlot >= SLOT_NUMBER_IN_DAY) testSlot -= SLOT_NUMBER_IN_DAY;
					int testLastSlot = lastSlot;
					if(testLastSlot < 0) testLastSlot += SLOT_NUMBER_IN_DAY;
					else if(testLastSlot >= SLOT_NUMBER_IN_DAY)
						testLastSlot -= SLOT_NUMBER_IN_DAY;
					if(testSlot > ((testLastSlot + 2) %SLOT_NUMBER_IN_DAY)
							&& (testSlot -testLastSlot) > (dev.getVisitGapThreshold()
									*APHHelper.MINUTE_TO_TWENTY_SECONDS_SLOT)) { 
						if( currentVisit != null ) {
							currentVisit.setCheckinFinished(
									aphHelper.slotToDate(curEntry, lastSlot, tz));
							addPermanenceCheck(currentVisit, currentPeasant, dev, tz);
							if(isVisitValid(currentVisit, dev, isEmployee, WORK_CALENDAR, tz))
								res.add(currentVisit);
							currentVisit = null;
						}

						if( currentPeasant != null ) {
							currentPeasant.setCheckinFinished(aphHelper.slotToDate(
									curEntry, lastSlot, tz));
							if(isPeasantValid(currentPeasant, dev, isEmployee,
									assignments.get(curEntry.getHostname()).getEntityKind()))
								res.add(currentPeasant);
							currentPeasant = null;
						}
					}
				}
				// If there is a peasant threshold
				if(dev.getPeasantPowerThreshold() == null 
						|| value >= dev.getPeasantPowerThreshold()) {
					// Add a new peasant if there is no peasant active
					if( currentPeasant == null )
						currentPeasant = createPeasant(curEntry, curDate, null,
								assignments.get(curEntry.getHostname()));
					lastPeasantSlot = slot;
					// Checks for power for visit
					if( value >= dev.getVisitPowerThreshold() ) {
						if( currentVisit == null )
							currentVisit = createVisit(curEntry, curDate, null,
									assignments.get(curEntry.getHostname()), isEmployee);
						currentVisit.addInRangeSegment();
						lastVisitSlot = slot;
					} else if( currentVisit != null ) {
						// Closes the visit if it was too far for more time than specified in visit gap threshold
						currentVisit.addOffRangeSegment();
						// 30DB Tolerance ... it should be a parameter
						if( value > (dev.getVisitPowerThreshold() - 30)) {
							lastVisitSlot = slot;
						} else {
							int testSlot = slot;
							int testLastVS = lastVisitSlot;
							if(testSlot < 0) testSlot += SLOT_NUMBER_IN_DAY;
							else if(testSlot >= SLOT_NUMBER_IN_DAY)
								testSlot -= SLOT_NUMBER_IN_DAY;
							if(testLastVS < 0) testLastVS += SLOT_NUMBER_IN_DAY;
							else if(testLastVS >= SLOT_NUMBER_IN_DAY)
								testLastVS -= SLOT_NUMBER_IN_DAY;
							if((testSlot - testLastVS) > (dev.getVisitGapThreshold()
									*APHHelper.MINUTE_TO_TWENTY_SECONDS_SLOT)) {
								int finishSlot = slot;
								if((lastVisitSlot + (dev.getVisitDecay()
										*APHHelper.MINUTE_TO_TWENTY_SECONDS_SLOT)) <
										testSlot)
									finishSlot = (int)(lastVisitSlot
											+(dev.getVisitDecay()
											*APHHelper.MINUTE_TO_TWENTY_SECONDS_SLOT));
								currentVisit.setCheckinFinished(aphHelper.slotToDate(
										curEntry, finishSlot, tz));
								addPermanenceCheck(currentVisit, currentPeasant, dev,
										tz);
								if(isVisitValid(currentVisit, dev, isEmployee, WORK_CALENDAR, tz))
									res.add(currentVisit);
								currentVisit = null;
							}
						}
					}
				} else if( lastPeasantSlot != null ) { 
					if( currentVisit != null ) currentVisit.addOffRangeSegment();
					
					int testSlot = slot;
					int testLastPeasant = lastPeasantSlot;
					if(testSlot < 0) testSlot += SLOT_NUMBER_IN_DAY;
					else if(testSlot >= SLOT_NUMBER_IN_DAY)
						testSlot -= SLOT_NUMBER_IN_DAY;
					if(testLastPeasant < 0) testLastPeasant += SLOT_NUMBER_IN_DAY;
					else if(testLastPeasant >= SLOT_NUMBER_IN_DAY)
						testLastPeasant -= SLOT_NUMBER_IN_DAY;
					if((testSlot - testLastPeasant) > (dev.getVisitGapThreshold()
							*APHHelper.MINUTE_TO_TWENTY_SECONDS_SLOT)) {

						int finishSlot = slot;
						if((lastPeasantSlot + (dev.getVisitDecay()
								*APHHelper.MINUTE_TO_TWENTY_SECONDS_SLOT)) < testSlot)
							finishSlot = (int)(lastPeasantSlot + (dev.getVisitDecay()
									*APHHelper.MINUTE_TO_TWENTY_SECONDS_SLOT));

						// Closes open visits
						if( currentVisit != null ) {
							currentVisit.setCheckinFinished(aphHelper.slotToDate(
									curEntry, finishSlot, tz));
							addPermanenceCheck(currentVisit, currentPeasant, dev, tz);
							if(isVisitValid(currentVisit, dev, isEmployee, WORK_CALENDAR, tz))
								res.add(currentVisit);
							currentVisit = null;
						} if( currentPeasant != null ) {
							currentPeasant.setCheckinFinished(aphHelper.slotToDate(
									curEntry, finishSlot, tz));
							if(isPeasantValid(currentPeasant, dev, isEmployee,
									assignments.get(curEntry.getHostname()).getEntityKind()))
								res.add(currentPeasant);
							currentPeasant = null;
						}
					}
				}
				// Updates the last slot
				lastSlot = slot;
			} catch( Exception e ) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}

		// Closes open visits in case of slot continuity disruption
		try {
			if( currentVisit != null ) {
				int finishSlot = lastSlot;
				APDevice dev = apd.get(curEntry.getHostname());
				if((lastVisitSlot + (dev.getVisitDecay()
						*APHHelper.MINUTE_TO_TWENTY_SECONDS_SLOT)) < finishSlot)
					finishSlot = (int)(lastVisitSlot + (dev.getVisitDecay()
							*APHHelper.MINUTE_TO_TWENTY_SECONDS_SLOT));
				currentVisit.setCheckinFinished(aphHelper.slotToDate(curEntry, finishSlot, tz));
				addPermanenceCheck(currentVisit, currentPeasant, apd.get(curEntry.getHostname()), tz);
				if(isVisitValid(currentVisit, apd.get(curEntry.getHostname()), isEmployee, WORK_CALENDAR, tz))
					res.add(currentVisit);
				currentVisit = null;
			}
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		try {
			if( currentPeasant != null ) {
				currentPeasant.setCheckinFinished(aphHelper.slotToDate(
						curEntry, lastSlot, tz));
				if(isPeasantValid(currentPeasant, apd.get(curEntry.getHostname()), isEmployee,
						assignments.get(curEntry.getHostname()).getEntityKind()))
					res.add(currentPeasant);
				currentPeasant = null;
			}
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		
		// Checks for max visits per day using RepeatThreshold
		int repepatThreshold = 0;
		for( APHEntry entry : entries ) {
			APDevice dev = apd.get(entry.getHostname());
			if(dev.getRepeatThreshold() > repepatThreshold)
				repepatThreshold = dev.getRepeatThreshold();
		}

		int count = 0;
		for( APDVisit visit : res ) {
			if( visit.getCheckinType().equals(APDVisit.CHECKIN_VISIT))
				count++;
		}
		if( count > repepatThreshold ) {// TODO discard changes from external context
			List<APDVisit> tmp = CollectionFactory.createList();
			tmp.addAll(res);
			res.clear();
			boolean one = false;
			for( APDVisit v : tmp ) {
				if(!v.getCheckinType().equals(APDVisit.CHECKIN_VISIT)) res.add(v);
				else if(!one) {
					res.add(v);
					one = true;
				}
				/*else {
					v.setCheckinType(APDVisit.CHECKIN_PEASANT);
					res.add(v);
				}*/
			}
		}
		// End Checks for max visits per day using RepeatThreshold
		
		return res;
	}

	private void addPermanenceCheck(APDVisit visit, APDVisit peasant, APDevice device,
			TimeZone tz) {
		long time = (long)(visit.getCheckinFinished().getTime()
				-visit.getCheckinStarted().getTime()) / 60000;
		visit.setHidePermanence(time < device.getVisitCountThreshold());
		try {
			if (isVisitValid(visit, device, false, WORK_CALENDAR, tz) && peasant != null &&
					(peasant.getCheckinStarted().before(visit.getCheckinStarted())
					|| peasant.getCheckinStarted().equals(visit.getCheckinStarted())))
				peasant.setHidePermanence(true);
		} catch(Exception e ) {}
		
	}
	
	/**
	 * Checks if a visit is valid according the device parameters
	 * 
	 * @param visit
	 *            The visit to check
	 * @param device
	 *            The device that contains the parameters
	 * @return true if valid, false if not
	 * @throws ParseException
	 */
	public static boolean isVisitValid(APDVisit visit, APDevice device, // TODO move to interface
			boolean isEmployee, Calendar cal, TimeZone tz) throws ParseException {
		
		int t;
		cal.clear();
		if(visit != null) {
			Long time = (visit.getCheckinFinished().getTime()  -visit.getCheckinStarted().getTime()) / 60000;
			
			// Validate Minimum time for visit  // TODO change name for getVisitMin
			if( time < device.getVisitTimeThreshold()) return false;

			// Validate Maximum time for visit  
			if( time > device.getVisitMaxThreshold()) return false;

			// Employees doesn't generate visits
			if( isEmployee ) return false;
			
			// Total segments percentage check 
			if( null != visit.getInRangeSegments() && visit.getInRangeSegments() > 0 
					&& null != visit.getTotalSegments() && visit.getTotalSegments() > 0 &&
					visit.getInRangeSegments() * 100 / visit.getTotalSegments() <
					VISIT_PERCENTAGE) return false; // TODO param
			
			visit.setDuration(time);
			
			t = Integer.valueOf(tf2.format(visit.getCheckinStarted()));
			cal.setTime(visit.getCheckinStarted());
		} else {
			long time = System.currentTimeMillis();
			t = Integer.valueOf(tf2.format(time));
			cal.setTimeInMillis(time);
		}
		
		// Validate Monitor Hour & days  
		
		tf2.setTimeZone(tz);
		int ts = 0;
		int te = 0;
		switch(cal.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SUNDAY :
			if(!device.getVisitsOnSun()) return false;
			ts = Integer.valueOf(device.getVisitStartSun().substring(0,2)
					+device.getVisitStartSun().substring(3));
			te = Integer.valueOf(device.getVisitEndSun().substring(0, 2)
					+device.getVisitEndSun().substring(3));
			break;
		case Calendar.MONDAY :
			if(!device.getVisitsOnMon()) return false;
			ts = Integer.valueOf(device.getVisitStartMon().substring(0, 2)
					+device.getVisitStartMon().substring(3));
			te = Integer.valueOf(device.getVisitEndMon().substring(0, 2)
					+device.getVisitEndMon().substring(3));
			break;
		case Calendar.TUESDAY :
			if(!device.getVisitsOnTue()) return false;
			ts = Integer.valueOf(device.getVisitStartTue().substring(0, 2)
					+device.getVisitStartTue().substring(3));
			te = Integer.valueOf(device.getVisitEndTue().substring(0, 2)
					+device.getVisitEndTue().substring(3));
			break;
		case Calendar.WEDNESDAY :
			if(!device.getVisitsOnWed()) return false;
			ts = Integer.valueOf(device.getVisitStartWed().substring(0, 2)
					+device.getVisitStartWed().substring(3));
			te = Integer.valueOf(device.getVisitEndWed().substring(0, 2)
					+device.getVisitEndWed().substring(3));
			break;
		case Calendar.THURSDAY :
			if(!device.getVisitsOnThu()) return false;
			ts = Integer.valueOf(device.getVisitStartThu().substring(0, 2)
					+device.getVisitStartThu().substring(3));
			te = Integer.valueOf(device.getVisitEndThu().substring(0, 2)
					+device.getVisitEndThu().substring(3));
			break;
		case Calendar.FRIDAY :
			if(!device.getVisitsOnFri()) return false;
			ts = Integer.valueOf(device.getVisitStartFri().substring(0, 2)
					+device.getVisitStartFri().substring(3));
			te = Integer.valueOf(device.getVisitEndFri().substring(0, 2)
					+device.getVisitEndFri().substring(3));
			break;
		case Calendar.SATURDAY :
			if(!device.getVisitsOnSat()) return false;
			ts = Integer.valueOf(device.getVisitStartSat().substring(0, 2)
					+device.getVisitStartSat().substring(3));
			te = Integer.valueOf(device.getVisitEndSat().substring(0, 2)
					+device.getVisitEndSat().substring(3));
			break;
		} if(te == 0) te = 2400;

		if( ts > te ) te += 2400;
		return te > t && t >= ts;
	}


	/**
	 * Checks if a peasant is valid according the device parameters
	 * 
	 * @param visit
	 *            The visit to check
	 * @param device
	 *            The device that contains the parameters
	 * @return true if valid, false if not
	 * @throws ParseException
	 */
	private boolean isPeasantValid(APDVisit visit, APDevice device,Boolean isEmployee, int entityKind) 
		throws ParseException {
		
		if( isEmployee ) visit.setCheckinType(APDVisit.CHECKIN_EMPLOYEE);
		
		if(!isEmployee && entityKind == EntityKind.KIND_INNER_ZONE) return false;
		
		Long time = (visit.getCheckinFinished().getTime() -visit.getCheckinStarted().getTime()) / 60000;
		
		// TODO add minimum peasant time
		
		visit.setDuration(time);
		return time <= 60 *60;
	}

	/**
	 * Creates a visit
	 * 
	 * @param source
	 *            The APHEntry source
	 * @param date
	 *            Visit Date Start
	 * @param device
	 *            DeviceInfo to attach
	 * @return A new fully formed visit
	 * @throws ASException
	 */
	private APDVisit createVisit(APHEntry source, Date date, DeviceInfo device, APDAssignation assign,
			Boolean isEmployee) throws ASException {
		
		String entityId = assign.getEntityId();
		Integer entityKind = assign.getEntityKind();
		WORK_CALENDAR.clear();
		
		APDVisit visit = new APDVisit();
		visit.setApheSource(source.getIdentifier());
		visit.setCheckinStarted(date);
		visit.setCheckinType(APDVisit.CHECKIN_VISIT);
		visit.setEntityId(entityId);
		visit.setEntityKind(entityKind);
		visit.setMac(source.getMac());
		visit.setDevicePlatform(source.getDevicePlatform());
		visit.setVerified(false);
		visit.setDeviceUUID(device == null ? null : device.getDeviceUUID());
		visit.setUserId(null);
		try {
			WORK_CALENDAR.setTime(sdf.parse(source.getDate()));
			if(source.getShiftDay() == APHEntry.NEXT)
				WORK_CALENDAR.add(Calendar.DATE, 1);
			else if(source.getShiftDay() == APHEntry.PREVIOUS)
				WORK_CALENDAR.add(Calendar.DATE, -1);
			visit.setForDate(sdf.format(WORK_CALENDAR.getTime()));
		} catch(ParseException e) {
			visit.setForDate(source.getDate());
		}
		visit.setKey(apdvDao.createKey(visit));
		
		return visit;
	}

	/**
	 * Creates a peasant
	 * 
	 * @param source
	 *            The APHEntry source
	 * @param date
	 *            Visit Date Start
	 * @param device
	 *            DeviceInfo to attach
	 * @return A new fully formed visit
	 * @throws ASException
	 */
	private APDVisit createPeasant(APHEntry source, Date date, DeviceInfo device, APDAssignation assign)
			throws ASException {
		
		String entityId = assign.getEntityId();
		Integer entityKind = assign.getEntityKind();
		WORK_CALENDAR.clear();
		
		APDVisit peasant = new APDVisit();
		peasant.setApheSource(source.getIdentifier());
		peasant.setCheckinStarted(date);
		peasant.setCheckinType(APDVisit.CHECKIN_PEASANT);
		peasant.setEntityId(entityId);
		peasant.setEntityKind(entityKind);
		peasant.setMac(source.getMac());
		peasant.setDevicePlatform(source.getDevicePlatform());
		peasant.setVerified(false);
		peasant.setDeviceUUID(device == null ? null : device.getDeviceUUID());
		peasant.setUserId(null);
		try {
			WORK_CALENDAR.setTime(sdf.parse(source.getDate()));
			if(source.getShiftDay() == APHEntry.NEXT)
				WORK_CALENDAR.add(Calendar.DATE, 1);
			else if(source.getShiftDay() == APHEntry.PREVIOUS)
				WORK_CALENDAR.add(Calendar.DATE, -1);
			peasant.setForDate(sdf.format(WORK_CALENDAR.getTime()));
		} catch(ParseException e) {
			peasant.setForDate(source.getDate());
		}
		peasant.setKey(apdvDao.createKey(peasant));

		return peasant;
	}
	
}//APDVisit Helper Implementation
