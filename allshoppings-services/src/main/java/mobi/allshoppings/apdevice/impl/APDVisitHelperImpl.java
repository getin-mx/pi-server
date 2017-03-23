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
import mobi.allshoppings.dao.APHEntryDAO;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.dao.InnerZoneDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
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
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

public class APDVisitHelperImpl implements APDVisitHelper {

	private static final Logger log = Logger.getLogger(APDVisitHelperImpl.class.getName());
	private static final SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat tf2 = new SimpleDateFormat("HHmm");
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final int VISIT_PERCENTAGE = 25;
	private static final List<String> BANNED = Arrays.asList("00:00:00:00:00:00");

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
	private APHEntryDAO apheDao;
	
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
	public void generateAPDVisits(List<String> brandIds, List<String> storeIds, Date fromDate, Date toDate, boolean deletePreviousRecords, boolean updateDashboards, boolean onlyEmployees, boolean onlyDashboards) throws ASException {

		List<Store> stores = CollectionFactory.createList();
		Map<String, APDevice> apdCache = CollectionFactory.createMap();
		Map<String, APDAssignation> assignmentsCache = CollectionFactory.createMap();
		boolean cacheBuilt = false;
		Range range = null;
		
		if(!CollectionUtils.isEmpty(storeIds)) {
			stores = storeDao.getUsingIdList(storeIds);
		} else if(!CollectionUtils.isEmpty(brandIds)) {
			for(String brandId : brandIds ) {
				stores.addAll(storeDao.getUsingBrandAndStatus(brandId, 
						Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED}), null));
			}
		} else {
			stores.addAll(storeDao.getUsingBrandAndStatus(null, 
					Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED}), null));
		}
		
		Date curDate = new Date(fromDate.getTime());
		while( curDate.before(toDate) || (fromDate.equals(toDate) && curDate.equals(toDate))) {

			try {
				for( Store store : stores ) {

					try {
						if(!onlyDashboards) {

							List<String> blackListMacs =getBlackListByStore(store);
							List<String> employeeListMacs = getEmployeeListByStore(store);

							// Try to delete previous records if needed
							if(deletePreviousRecords) {
								apdvDao.deleteUsingEntityIdAndEntityKindAndDate(store.getIdentifier(), EntityKind.KIND_STORE,
										curDate, new Date(curDate.getTime() + 86400000), 
										onlyEmployees ? APDVisit.CHECKIN_EMPLOYEE : null);
							}

							List<APDAssignation> assigs = apdaDao.getUsingEntityIdAndEntityKindAndDate(store.getIdentifier(), EntityKind.KIND_STORE, curDate);
							if( !CollectionUtils.isEmpty(assigs)) {
								if( assigs.size() == 1 ) {

									assignmentsCache.clear();
									assignmentsCache.put(assigs.get(0).getHostname(), assigs.get(0));
									if(!apdCache.containsKey(assigs.get(0).getHostname()))
										apdCache.put(assigs.get(0).getHostname(), apdDao.get(assigs.get(0).getHostname(), true));

									log.log(Level.INFO, "Fetching APHEntries for " + assigs.get(0).getHostname() + " and " + curDate + "...");
									List<APHEntry> entries = apheDao.getUsingHostnameAndDates(
											Arrays.asList(new String[] { assigs.get(0).getHostname() }), curDate, curDate, range, false);

									// Employee check
									if( onlyEmployees ) {
										List<APHEntry> tmp = CollectionFactory.createList();
										tmp.addAll(entries);
										entries.clear();
										for( APHEntry entry : tmp ) {
											if(employeeListMacs.contains(entry.getMac().toUpperCase()))
												entries.add(entry);
										}
									}

									log.log(Level.INFO, "Processing " + entries.size() + " APHEntries...");
									List<APDVisit> objs = CollectionFactory.createList();
									for(APHEntry entry : entries ) {
										aphHelper.artificiateRSSI(entry, apdCache.get(entry.getHostname()));
										List<APDVisit> visitList = aphEntryToVisits(entry, apdCache, assignmentsCache,blackListMacs,employeeListMacs);
										for(APDVisit visit : visitList )
											if(!objs.contains(visit)) {
												if(!onlyEmployees || visit.getCheckinType().equals(APDVisit.CHECKIN_EMPLOYEE))
													objs.add(visit);
											}
									}

									log.log(Level.INFO, "Saving " + objs.size() + " APDVisits...");
									try {
										apdvDao.createOrUpdate(null, objs, true);
									} catch( Exception e ) {}

								} else {
									Map<String, List<APHEntry>> cache = CollectionFactory.createMap();
									List<String> hostnames = CollectionFactory.createList();
									assignmentsCache.clear();
									for( APDAssignation assig : assigs ) { 
										hostnames.add(assig.getHostname());
										assignmentsCache.put(assig.getHostname(), assig);
										if(!apdCache.containsKey(assig.getHostname()))
											apdCache.put(assig.getHostname(), apdDao.get(assig.getHostname(), true));
									}

									log.log(Level.INFO, "Fetching APHEntries for " + hostnames + " and " + curDate + "...");
									List<APHEntry> entries = apheDao.getUsingHostnameAndDates(hostnames, curDate, curDate, range, false);
									for( APHEntry entry : entries ) {
										if(!cache.containsKey(entry.getMac()))
											cache.put(entry.getMac(), new ArrayList<APHEntry>());

										aphHelper.artificiateRSSI(entry, apdCache.get(entry.getHostname()));
										cache.get(entry.getMac()).add(entry);
									}

									log.log(Level.INFO, "Processing " + cache.size() + " APHEntries...");
									List<APDVisit> objs = CollectionFactory.createList();
									Iterator<String> i = cache.keySet().iterator();
									while(i.hasNext()) {
										String key = i.next();
										List<APHEntry> e = cache.get(key);
										List<APDVisit> visitList = aphEntryToVisits(e, apdCache, assignmentsCache,blackListMacs,employeeListMacs);
										for(APDVisit visit : visitList )
											if(!objs.contains(visit))
												if(!objs.contains(visit)) {
													if(!onlyEmployees || visit.getCheckinType().equals(APDVisit.CHECKIN_EMPLOYEE))
														objs.add(visit);
												}
									}

									log.log(Level.INFO, "Saving " + objs.size() + " APDVisits...");
									try {
										apdvDao.createOrUpdate(null, objs, true);
									} catch( Exception e ) {}

								}
							}
						}
						
						// Try to update dashboard if needed
						if(updateDashboards && !onlyEmployees) {
							if(!cacheBuilt) {
								try {
									mapper.buildCaches(false);
									cacheBuilt = true;
								} catch( Exception e ) {
									throw ASExceptionHelper.defaultException(e.getMessage(), e);
								}
							}

							didDao.deleteUsingSubentityIdAndElementIdAndDate(store.getIdentifier(),
									Arrays.asList(new String[] { "apd_visitor", "apd_permanence" }), curDate, curDate);
							mapper.createAPDVisitPerformanceDashboardForDay(curDate,
									Arrays.asList(new String[] { store.getIdentifier() }), EntityKind.KIND_STORE);
						}
					} catch( Exception e ) {
						log.log(Level.SEVERE, e.getMessage(), e);
					}

				} 
			} catch( Exception e1 ) {
				log.log(Level.SEVERE, e1.getMessage(), e1);
			}
			
			curDate = new Date(curDate.getTime() + 86400000);
		}
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
	 * Writes a list of APDVisits in the database
	 * 
	 * @param shoppingIds
	 *            List of shoppings to process
	 * @param fromDate
	 *            Date to start processing
	 * @param toDate
	 *            Date to stop processing
	 * @throws ASException
	 */
	@Override
	public void generateAPDVisits(List<String> shoppingIds, Date fromDate, Date toDate, boolean deletePreviousRecords, boolean updateDashboards, boolean onlyEmployees, boolean onlyDashboards) throws ASException {

		Map<String, APDevice> apdCache = CollectionFactory.createMap();
		Map<String, APDAssignation> assignmentsCache = CollectionFactory.createMap();
		boolean cacheBuilt = false;
		Range range = null;

		List<String> subShoppingIds = CollectionFactory.createList();
		
		if(!CollectionUtils.isEmpty(shoppingIds)) {
			subShoppingIds.addAll(shoppingIds);
		} else {
			List<Shopping> shoppings = CollectionFactory.createList();
			shoppings.addAll(shoppingDao.getUsingStatusAndRange( 
				Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED}), null, null));
			for(Shopping shopping : shoppings ) 
				subShoppingIds.add(shopping.getIdentifier());
		}

		Map<String, Integer> entities = getEntities(subShoppingIds, null, null);

		Date curDate = new Date(fromDate.getTime());
		while( curDate.before(toDate) || (fromDate.equals(toDate) && curDate.equals(toDate))) {

			try {

				for( String entityId : entities.keySet() ) {

					Integer entityKind = entities.get(entityId);
					
					try {

						if(!onlyDashboards) {

							List<String> blackListMacs = CollectionFactory.createList();
							List<String> employeeListMacs = CollectionFactory.createList();

							// Try to delete previous records if needed
							if(deletePreviousRecords) {
								apdvDao.deleteUsingEntityIdAndEntityKindAndDate(entityId, entityKind,
										curDate, new Date(curDate.getTime() + 86400000),
										onlyEmployees ? APDVisit.CHECKIN_EMPLOYEE : null);
							}

							List<APDAssignation> assigs = apdaDao.getUsingEntityIdAndEntityKindAndDate(entityId, entityKind, curDate);
							if( !CollectionUtils.isEmpty(assigs)) {
								if( assigs.size() == 1 ) {

									assignmentsCache.clear();
									assignmentsCache.put(assigs.get(0).getHostname(), assigs.get(0));
									if(!apdCache.containsKey(assigs.get(0).getHostname()))
										apdCache.put(assigs.get(0).getHostname(), apdDao.get(assigs.get(0).getHostname(), true));

									log.log(Level.INFO, "Fetching APHEntries for " + assigs.get(0).getHostname() + " and " + curDate + "...");
									List<APHEntry> entries = apheDao.getUsingHostnameAndDates(
											Arrays.asList(new String[] { assigs.get(0).getHostname() }), curDate, curDate, range, false);

									// Employee check
									if( onlyEmployees ) {
										List<APHEntry> tmp = CollectionFactory.createList();
										tmp.addAll(entries);
										entries.clear();
										for( APHEntry entry : tmp ) {
											if(employeeListMacs.contains(entry.getMac().toUpperCase()))
												entries.add(entry);
										}
									}

									log.log(Level.INFO, "Processing " + entries.size() + " APHEntries...");
									List<APDVisit> objs = CollectionFactory.createList();
									for(APHEntry entry : entries ) {
										aphHelper.artificiateRSSI(entry, apdCache.get(entry.getHostname()));
										List<APDVisit> visitList = aphEntryToVisits(entry, apdCache, assignmentsCache,blackListMacs,employeeListMacs);
										for(APDVisit visit : visitList )
											if(!objs.contains(visit))
												if(!objs.contains(visit)) {
													if(!onlyEmployees || visit.getCheckinType().equals(APDVisit.CHECKIN_EMPLOYEE))
														objs.add(visit);
												}
									}

									log.log(Level.INFO, "Saving " + objs.size() + " APDVisits...");
									try {
										apdvDao.createOrUpdate(null, objs, true);
									} catch( Exception e ) {}

								} else {
									Map<String, List<APHEntry>> cache = CollectionFactory.createMap();
									List<String> hostnames = CollectionFactory.createList();
									assignmentsCache.clear();
									for( APDAssignation assig : assigs ) { 
										hostnames.add(assig.getHostname());
										assignmentsCache.put(assig.getHostname(), assig);
										if(!apdCache.containsKey(assig.getHostname()))
											apdCache.put(assig.getHostname(), apdDao.get(assig.getHostname(), true));
									}

									log.log(Level.INFO, "Fetching APHEntries for " + hostnames + " and " + curDate + "...");

									List<APHEntry> entries = apheDao.getUsingHostnameAndDates(hostnames, curDate, curDate, range, false);
									for( APHEntry entry : entries ) {
										if(!cache.containsKey(entry.getMac()))
											cache.put(entry.getMac(), new ArrayList<APHEntry>());

										aphHelper.artificiateRSSI(entry, apdCache.get(entry.getHostname()));
										cache.get(entry.getMac()).add(entry);
									}

									log.log(Level.INFO, "Processing " + cache.size() + " APHEntries...");
									List<APDVisit> objs = CollectionFactory.createList();
									Iterator<String> i = cache.keySet().iterator();
									while(i.hasNext()) {
										String key = i.next();
										List<APHEntry> e = cache.get(key);
										List<APDVisit> visitList = aphEntryToVisits(e, apdCache, assignmentsCache,blackListMacs,employeeListMacs);
										for(APDVisit visit : visitList )
											if(!objs.contains(visit))
												if(!objs.contains(visit)) {
													if(!onlyEmployees || visit.getCheckinType().equals(APDVisit.CHECKIN_EMPLOYEE))
														objs.add(visit);
												}
									}

									log.log(Level.INFO, "Saving " + objs.size() + " APDVisits...");
									try {
										apdvDao.createOrUpdate(null, objs, true);
									} catch( Exception e ) {}

								}
							}
						}

						// Try to update dashboard if needed
						if(updateDashboards && !onlyEmployees) {
							if(!cacheBuilt) {
								try {
									mapper.buildCaches(false);
									cacheBuilt = true;
								} catch( Exception e ) {
									throw ASExceptionHelper.defaultException(e.getMessage(), e);
								}
							}

							didDao.deleteUsingSubentityIdAndElementIdAndDate(entityId,
									Arrays.asList(new String[] { "apd_visitor", "apd_permanence" }), curDate, curDate);
							mapper.createAPDVisitPerformanceDashboardForDay(curDate,
									Arrays.asList(new String[] { entityId }), entityKind);
						}

					} catch( Exception e ) {
						log.log(Level.SEVERE, e.getMessage(), e);
					}
				}

			} catch( Exception e1 ) {
				log.log(Level.SEVERE, e1.getMessage(), e1);
			}
			
			curDate = new Date(curDate.getTime() + 86400000);
		}
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
		log.log(Level.INFO, "Initial macs:  " + macs.size() + " macs");

		macs.clear();
		//--- Start black list ------------
		//Load blackListbyShopping for shopping
		if( StringUtils.hasText(store.getShoppingId())) {
			List<APDMABlackList> blackListbyShopping = apmaBlDao.getUsingEntityIdAndRange(store.getShoppingId(), EntityKind.KIND_SHOPPING, null, null, null, false);
			for( APDMABlackList shop : blackListbyShopping ) {
				if (!macs.contains(shop.getMac().toUpperCase().trim())){
					macs.add(shop.getMac().toUpperCase().trim());	
				}
			}
			log.log(Level.INFO,"(" +store.getIdentifier()+ ") -- Load black list for Shopping: " + blackListbyShopping.size() + " macs");
		}

		//Load blackListbyShopping for brand
		List<APDMABlackList> blackListbyBrand = apmaBlDao.getUsingEntityIdAndRange(store.getBrandId(), EntityKind.KIND_BRAND, null, null, null, false);
		for( APDMABlackList brand : blackListbyBrand ) {
			if (!macs.contains(brand.getMac().toUpperCase().trim())){
				macs.add(brand.getMac().toUpperCase().trim());	
			}
		}
		log.log(Level.INFO,"(" +store.getIdentifier()+ ") -- Load black list for Brand: " + blackListbyBrand.size() + " macs");


		//Load blackListbyShopping for store
		List<APDMABlackList> blackListbyStore = apmaBlDao.getUsingEntityIdAndRange(store.getIdentifier(), EntityKind.KIND_STORE, null, null, null, false);
		for( APDMABlackList st : blackListbyStore ) {
			if (!macs.contains(st.getMac().toUpperCase().trim())){
				macs.add(st.getMac().toUpperCase().trim());	
			}
		}
		//Load blackListbyShopping for store
		log.log(Level.INFO,"(" +store.getIdentifier()+ ") -- Load black list for Store: " + blackListbyStore.size() + " macs");


		log.log(Level.INFO, "TOTAL Blacklist Entries: " + macs.size() + " macs");

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
		log.log(Level.INFO, "Initial macs:  " + macs.size() + " macs");

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
			log.log(Level.INFO,"(" +store.getIdentifier()+ ") -- Load Employees in list for Shopping: " + employeesbyShopping.size() + " macs");
		}

		//Load blackListbyShopping for brand
		List<APDMAEmployee> employeesbyBrand = apmaEDao.getUsingEntityIdAndRange(store.getBrandId(), EntityKind.KIND_BRAND, null, null, null, false);
		for( APDMAEmployee emp_brand : employeesbyBrand ) {
			if (!macs.contains(emp_brand.getMac().toUpperCase().trim())){
				macs.add(emp_brand.getMac().toUpperCase().trim());	
			}
		}
		log.log(Level.INFO,"(" +store.getIdentifier()+ ") -- Load Employees in list for Brand: " + employeesbyBrand.size() + " macs");


		//Load blackListbyShopping for store
		List<APDMAEmployee> employeesbyStore = apmaEDao.getUsingEntityIdAndRange(store.getIdentifier(), EntityKind.KIND_STORE, null, null, null, false);
		for( APDMAEmployee emp_sto : employeesbyStore ) {
			if (!macs.contains(emp_sto.getMac().toUpperCase().trim())){
				macs.add(emp_sto.getMac().toUpperCase().trim());	
			}
		}
		//Load blackListbyShopping for store
		log.log(Level.INFO,"(" +store.getIdentifier()+ ") -- Load Employees list for Store: " + employeesbyStore.size() + " macs");


		log.log(Level.INFO, "TOTAL Employees: " + macs.size() + " macs");

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
	public List<APDVisit> aphEntryToVisits(APHEntry entry, Map<String, APDevice> apdCache, Map<String, APDAssignation> assignmentsCache,List<String> blackListMacs, List<String> employeeListMacs) throws ASException {
		List<APHEntry> entries = CollectionFactory.createList();
		entries.add(entry);
		return aphEntryToVisits(entries, apdCache, assignmentsCache,blackListMacs,employeeListMacs);
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
	public List<APDVisit> aphEntryToVisits(List<APHEntry> entries, Map<String, APDevice> apdCache, Map<String, APDAssignation> assignmentsCache, List<String> blackListMacs, List<String> employeeListMacs) throws ASException {
		
		int COMMON = 0;
		int CASABLANCA = 1;
		
		List<String> casablancaHosts = Arrays.asList("gihs-0328","gihs-0329","gihs-0331","gihs-0326","gihs-0327");
		
		int mode = 0;
		for( APHEntry entry : entries ) {
			if( casablancaHosts.contains(entry.getHostname()))
				mode = CASABLANCA;
		}
		
		switch(mode) {
		case 1:
			return aphEntryToVisitsCasablanca(entries, apdCache, assignmentsCache, blackListMacs, employeeListMacs);
		default:
			return aphEntryToVisitsCommon(entries, apdCache, assignmentsCache, blackListMacs, employeeListMacs);
		}
	}

	/**
	 * Converts an APHEntry to a visit list (Commons version)
	 * 
	 * @param entry
	 *            The entry to convert
	 * @return A list with created visits
	 * @throws ASException
	 */
	private List<APDVisit> aphEntryToVisitsCasablanca(List<APHEntry> entries, Map<String, APDevice> apdCache, Map<String, APDAssignation> assignmentsCache, List<String> blackListMacs, List<String> employeeListMacs) throws ASException {

		// Validates entries
		if( CollectionUtils.isEmpty(entries))
			throw ASExceptionHelper.invalidArgumentsException();

		// Work variables
		List<APDVisit> ret = CollectionFactory.createList();
		Boolean isEmployee = false;		
		
		// Merges all the time slots
		List<Integer> slots = CollectionFactory.createList();
		if( entries.size() > 1 ) {
			for( APHEntry entry : entries ) {
				// Checks for BlackList
				if(!blackListMacs.contains(entry.getMac().toUpperCase().trim())){
					List<Integer> tmpSlots = aphHelper.timeslotToList(entry.getArtificialRssi());
					for( Integer i : tmpSlots ) {
						if(!slots.contains(i))
							slots.add(i);
					}
					// If the mac address is contained in the employee list,
					// then activates the empoloyee flag
					if(employeeListMacs.contains(entry.getMac().toUpperCase().trim())){
						isEmployee= true;
					}
				}
			}
			Collections.sort(slots);
		} else {
			// Checks for BlackList
			if(!blackListMacs.contains(entries.get(0).getMac().toUpperCase().trim())){
				slots = aphHelper.timeslotToList(entries.get(0).getArtificialRssi());
				// If the mac address is contained in the employee list,
				// then activates the empoloyee flag
				if(employeeListMacs.contains(entries.get(0).getMac().toUpperCase().trim())){
					isEmployee= true;
				}
			}
		}
		
		if(slots.isEmpty())
			return ret;
		
		// Adds all the devices in the cache
		Map<String,APDevice> apd = CollectionFactory.createMap();
		if( apdCache == null || apdCache.size() == 0 ) {
			for( APHEntry entry : entries ) {
				apd.put(entry.getHostname(), apdDao.get(entry.getHostname(), true));
			}
		} else {
			apd.putAll(apdCache);
		}
		
		// Adds all the assignments in the cache
		Map<String,APDAssignation> assignments = CollectionFactory.createMap();
		if( assignmentsCache == null || assignmentsCache.size() == 0 ) {
			for( APHEntry entry : entries ) {
				try {
					assignments.put(entry.getHostname(),
							apdaDao.getOneUsingHostnameAndDate(entry.getHostname(), sdf.parse(entry.getDate())));
				} catch( Exception e ) {
					log.log(Level.SEVERE, "Error parsing date " + entry.getDate(), e);
				}
			}
		} else {
			assignments.putAll(assignmentsCache);
		}

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

				// Controls banned mac addresses
				if( BANNED.contains(curEntry.getMac().toLowerCase())) {
					ret = CollectionFactory.createList();
					return ret;
				}
				
				// Controls invalid value
				if( value > -1 ) {
					ret = CollectionFactory.createList();
					return ret;
				}
				
				if( value != null ) {

					Date curDate = aphHelper.slotToDate(curEntry.getDate(), slot);
					APDevice dev = apd.get(curEntry.getHostname());
					dev.completeDefaults();
					
					// Closes open visits in case of slot continuity disruption
					if( lastSlot != null && slot != (lastSlot + 1) && (slot - lastSlot) > (dev.getVisitGapThreshold() * 3)) {
						if( currentVisit != null ) {
							currentVisit.setCheckinFinished(aphHelper.slotToDate(curEntry.getDate(), lastSlot));
							addPermanenceCheck(currentVisit, currentPeasant, dev);
							if(isVisitValid(currentVisit, dev))
								ret.add(currentVisit);
							currentVisit = null;
						}

						if( currentPeasant != null ) {
							currentPeasant.setCheckinFinished(aphHelper.slotToDate(curEntry.getDate(), lastSlot));
							if(isPeasantValid(currentPeasant, dev, isEmployee))
								ret.add(currentPeasant);
							currentPeasant = null;
						}
					}

					
					// If there is a peasant threshold
					if( dev.getPeasantPowerThreshold() == null 
							|| value >= dev.getPeasantPowerThreshold() ) {

						// Add a new peasant if there is no peasant active
						if( currentPeasant == null )
							currentPeasant = createPeasant(curEntry, curDate, null, assignments.get(curEntry.getHostname()));
						lastPeasantSlot = slot;
						// Checks for power for visit
						if( value >= dev.getVisitPowerThreshold()) {
							if( currentVisit == null )
								currentVisit = createVisit(curEntry, curDate, null, assignments.get(curEntry.getHostname()), isEmployee);
							currentVisit.addInRangeSegment();
							lastVisitSlot = slot;
						} else {
							// Closes the visit if it was too far for more time than specified in visit gap threshold
							if( currentVisit != null ) {
								currentVisit.addOffRangeSegment();
								// 30DB Tolerance ... it should be a parameter
								if( value > (dev.getVisitPowerThreshold() - 30)) {
									lastVisitSlot = slot;
								} else {
									if(( slot - lastVisitSlot ) > (dev.getVisitGapThreshold() * 3)) {
										int finishSlot = slot;
										if((lastVisitSlot + (dev.getVisitDecay() * 3)) < finishSlot)
											finishSlot = (int)(lastVisitSlot + (dev.getVisitDecay() * 3));

										currentVisit.setCheckinFinished(aphHelper.slotToDate(curEntry.getDate(), finishSlot));
										addPermanenceCheck(currentVisit, currentPeasant, dev);
										if(isVisitValid(currentVisit, dev))
											ret.add(currentVisit);
										currentVisit = null;
									}
								}
							}
						}

					} else {
						if( lastPeasantSlot != null ) { 
							if( currentVisit != null )
								currentVisit.addOffRangeSegment();
							
							if(( slot - lastPeasantSlot ) > (dev.getVisitGapThreshold() * 3)) {

								int finishSlot = slot;
								if((lastPeasantSlot + (dev.getVisitDecay() * 3)) < finishSlot)
									finishSlot = (int)(lastPeasantSlot + (dev.getVisitDecay() * 3));

								// Closes open visits
								if( currentVisit != null ) {
									currentVisit.setCheckinFinished(aphHelper.slotToDate(curEntry.getDate(), finishSlot));
									addPermanenceCheck(currentVisit, currentPeasant, dev);
									if(isVisitValid(currentVisit, dev))
										ret.add(currentVisit);
									currentVisit = null;
								}

								if( currentPeasant != null ) {
									currentPeasant.setCheckinFinished(aphHelper.slotToDate(curEntry.getDate(), finishSlot));
									if(isPeasantValid(currentPeasant, dev,isEmployee))
										ret.add(currentPeasant);
									currentPeasant = null;
								}
							}
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
				APDevice dev1 = apd.get(curEntry.getHostname());
				if((lastVisitSlot + (dev1.getVisitDecay() * 3)) < finishSlot)
					finishSlot = (int)(lastVisitSlot + (dev1.getVisitDecay() * 3));
				currentVisit.setCheckinFinished(aphHelper.slotToDate(curEntry.getDate(), finishSlot));
				addPermanenceCheck(currentVisit, currentPeasant, apd.get(curEntry.getHostname()));
				if(isVisitValid(currentVisit, apd.get(curEntry.getHostname())))
					ret.add(currentVisit);
				currentVisit = null;
			}
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		try {
			if( currentPeasant != null ) {
				currentPeasant.setCheckinFinished(aphHelper.slotToDate(curEntry.getDate(), lastSlot));
				if(isPeasantValid(currentPeasant, apd.get(curEntry.getHostname()),isEmployee))
					ret.add(currentPeasant);
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
		for( APDVisit visit : ret ) {
			if( visit.getCheckinType().equals(APDVisit.CHECKIN_VISIT))
				count++;
		}
		if( count > repepatThreshold ) {
			List<APDVisit> tmp = CollectionFactory.createList();
			tmp.addAll(ret);
			ret.clear();
			for( APDVisit v : tmp ) {
				if(!v.getCheckinType().equals(APDVisit.CHECKIN_VISIT))
					ret.add(v);
			}
		}
		// End Checks for max visits per day using RepeatThreshold
		
		return ret;
	}

	/**
	 * Converts an APHEntry to a visit list (Commons version)
	 * 
	 * @param entry
	 *            The entry to convert
	 * @return A list with created visits
	 * @throws ASException
	 */
	private List<APDVisit> aphEntryToVisitsCommon(List<APHEntry> entries, Map<String, APDevice> apdCache, Map<String, APDAssignation> assignmentsCache, List<String> blackListMacs, List<String> employeeListMacs) throws ASException {

		// Validates entries
		if( CollectionUtils.isEmpty(entries))
			throw ASExceptionHelper.invalidArgumentsException();

		// Work variables
		List<APDVisit> ret = CollectionFactory.createList();
		Boolean isEmployee = false;		
		
		// Merges all the time slots
		List<Integer> slots = CollectionFactory.createList();
		if( entries.size() > 1 ) {
			for( APHEntry entry : entries ) {
				// Checks for BlackList
				if(!blackListMacs.contains(entry.getMac().toUpperCase().trim())){
					List<Integer> tmpSlots = aphHelper.timeslotToList(entry.getArtificialRssi());
					for( Integer i : tmpSlots ) {
						if(!slots.contains(i))
							slots.add(i);
					}
					// If the mac address is contained in the employee list,
					// then activates the empoloyee flag
					if(employeeListMacs.contains(entry.getMac().toUpperCase().trim())){
						isEmployee= true;
					}
				}
			}
			Collections.sort(slots);
		} else {
			// Checks for BlackList
			if(!blackListMacs.contains(entries.get(0).getMac().toUpperCase().trim())){
				slots = aphHelper.timeslotToList(entries.get(0).getArtificialRssi());
				// If the mac address is contained in the employee list,
				// then activates the empoloyee flag
				if(employeeListMacs.contains(entries.get(0).getMac().toUpperCase().trim())){
					isEmployee= true;
				}
			}
		}
		
		if(slots.isEmpty())
			return ret;
		
		// Adds all the devices in the cache
		Map<String,APDevice> apd = CollectionFactory.createMap();
		if( apdCache == null || apdCache.size() == 0 ) {
			for( APHEntry entry : entries ) {
				apd.put(entry.getHostname(), apdDao.get(entry.getHostname(), true));
			}
		} else {
			apd.putAll(apdCache);
		}
		
		// Adds all the assignments in the cache
		Map<String,APDAssignation> assignments = CollectionFactory.createMap();
		if( assignmentsCache == null || assignmentsCache.size() == 0 ) {
			for( APHEntry entry : entries ) {
				try {
					assignments.put(entry.getHostname(),
							apdaDao.getOneUsingHostnameAndDate(entry.getHostname(), sdf.parse(entry.getDate())));
				} catch( Exception e ) {
					log.log(Level.SEVERE, "Error parsing date " + entry.getDate(), e);
				}
			}
		} else {
			assignments.putAll(assignmentsCache);
		}

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

				if( value != null ) {

					Date curDate = aphHelper.slotToDate(curEntry.getDate(), slot);
					APDevice dev = apd.get(curEntry.getHostname());
					dev.completeDefaults();
					
					// Closes open visits in case of slot continuity disruption
					if( lastSlot != null && slot != (lastSlot + 1) && (slot - lastSlot) > (dev.getVisitGapThreshold() * 3)) {
						if( currentVisit != null ) {
							currentVisit.setCheckinFinished(aphHelper.slotToDate(curEntry.getDate(), lastSlot));
							addPermanenceCheck(currentVisit, currentPeasant, dev);
							if(isVisitValid(currentVisit, dev))
								ret.add(currentVisit);
							currentVisit = null;
						}

						if( currentPeasant != null ) {
							currentPeasant.setCheckinFinished(aphHelper.slotToDate(curEntry.getDate(), lastSlot));
							if(isPeasantValid(currentPeasant, dev, isEmployee))
								ret.add(currentPeasant);
							currentPeasant = null;
						}
					}

					
					// If there is a peasant threshold
					if( dev.getPeasantPowerThreshold() == null 
							|| value >= dev.getPeasantPowerThreshold() ) {

						// Add a new peasant if there is no peasant active
						if( currentPeasant == null )
							currentPeasant = createPeasant(curEntry, curDate, null, assignments.get(curEntry.getHostname()));
						lastPeasantSlot = slot;
						// Checks for power for visit
						if( value >= dev.getVisitPowerThreshold()) {
							if( currentVisit == null )
								currentVisit = createVisit(curEntry, curDate, null, assignments.get(curEntry.getHostname()), isEmployee);
							lastVisitSlot = slot;
						} else {
							// Closes the visit if it was too far for more time than specified in visit gap threshold
							if( currentVisit != null ) {
								// 30DB Tolerance ... it should be a parameter
								if( value > (dev.getVisitPowerThreshold() - 30)) {
									lastVisitSlot = slot;
								} else {
									if((( slot - lastVisitSlot ) * 3) > dev.getVisitGapThreshold()) {
										currentVisit.setCheckinFinished(aphHelper.slotToDate(curEntry.getDate(), lastSlot));
										addPermanenceCheck(currentVisit, currentPeasant, dev);
										if(isVisitValid(currentVisit, dev))
											ret.add(currentVisit);
										currentVisit = null;
									}
								}
							}
						}

					} else {
						if( lastPeasantSlot != null ) {
							if( dev.getVisitGapThreshold() == null ) dev.setVisitGapThreshold(10L);
							if((( slot - lastPeasantSlot ) * 3) > dev.getVisitGapThreshold()) {

								// Closes open visits
								if( currentVisit != null ) {
									currentVisit.setCheckinFinished(aphHelper.slotToDate(curEntry.getDate(), lastSlot));
									addPermanenceCheck(currentVisit, currentPeasant, dev);
									if(isVisitValid(currentVisit, dev))
										ret.add(currentVisit);
									currentVisit = null;
								}

								if( currentPeasant != null ) {
									currentPeasant.setCheckinFinished(aphHelper.slotToDate(curEntry.getDate(), lastSlot));
									if(isPeasantValid(currentPeasant, dev,isEmployee))
										ret.add(currentPeasant);
									currentPeasant = null;
								}
							}
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
				currentVisit.setCheckinFinished(aphHelper.slotToDate(curEntry.getDate(), lastSlot));
				addPermanenceCheck(currentVisit, currentPeasant, apd.get(curEntry.getHostname()));
				if(isVisitValid(currentVisit, apd.get(curEntry.getHostname())))
					ret.add(currentVisit);
				currentVisit = null;
			}
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		try {
			if( currentPeasant != null ) {
				currentPeasant.setCheckinFinished(aphHelper.slotToDate(curEntry.getDate(), lastSlot));
				if(isPeasantValid(currentPeasant, apd.get(curEntry.getHostname()),isEmployee))
					ret.add(currentPeasant);
				currentPeasant = null;
			}
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		return ret;
	}

	private void addPermanenceCheck(APDVisit visit, APDVisit peasant, APDevice device ) {
		long time = (long)(visit.getCheckinFinished().getTime() - visit.getCheckinStarted().getTime()) / 60000;
		if( time >= device.getVisitCountThreshold())
			visit.setHidePermanence(false);
		else
			visit.setHidePermanence(true);
		
		try {
			if (isVisitValid(visit, device) && peasant != null && (peasant.getCheckinStarted().before(visit.getCheckinStarted())
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
	private boolean isVisitValid(APDVisit visit, APDevice device) throws ParseException {
		
		int time = (int)(visit.getCheckinFinished().getTime() - visit.getCheckinStarted().getTime()) / 60000;
		
		// Validate Minimum time for visit  
		if( time < device.getVisitTimeThreshold())
			return false;

		// Validate Maximum time for visit  
		if( time > device.getVisitMaxThreshold())
			return false;

		// Validate Hour of day  
		int t = Integer.valueOf(tf2.format(visit.getCheckinStarted()));
		int ts = 0;
		int te = 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(visit.getCheckinStarted());
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		switch(dayOfWeek) {
		case Calendar.SUNDAY:
			if( !device.getVisitsOnSun()) return false;
			ts = Integer.valueOf(tf2.format(tf.parse(device.getVisitStartSun())));
			te = Integer.valueOf(tf2.format(tf.parse(device.getVisitEndSun())));
			break;
		case Calendar.MONDAY:
			if( !device.getVisitsOnMon()) return false;
			ts = Integer.valueOf(tf2.format(tf.parse(device.getVisitStartMon())));
			te = Integer.valueOf(tf2.format(tf.parse(device.getVisitEndMon())));
			break;
		case Calendar.TUESDAY:
			if( !device.getVisitsOnTue()) return false;
			ts = Integer.valueOf(tf2.format(tf.parse(device.getVisitStartTue())));
			te = Integer.valueOf(tf2.format(tf.parse(device.getVisitEndTue())));
			break;
		case Calendar.WEDNESDAY:
			if( !device.getVisitsOnWed()) return false;
			ts = Integer.valueOf(tf2.format(tf.parse(device.getVisitStartWed())));
			te = Integer.valueOf(tf2.format(tf.parse(device.getVisitEndWed())));
			break;
		case Calendar.THURSDAY:
			if( !device.getVisitsOnThu()) return false;
			ts = Integer.valueOf(tf2.format(tf.parse(device.getVisitStartThu())));
			te = Integer.valueOf(tf2.format(tf.parse(device.getVisitEndThu())));
			break;
		case Calendar.FRIDAY:
			if( !device.getVisitsOnFri()) return false;
			ts = Integer.valueOf(tf2.format(tf.parse(device.getVisitStartFri())));
			te = Integer.valueOf(tf2.format(tf.parse(device.getVisitEndFri())));
			break;
		case Calendar.SATURDAY:
			if( !device.getVisitsOnSat()) return false;
			ts = Integer.valueOf(tf2.format(tf.parse(device.getVisitStartSat())));
			te = Integer.valueOf(tf2.format(tf.parse(device.getVisitEndSat())));
			break;
		}
		
		if( ts >= te )
			te = te + 2400;
		
		if( te > 2400 && t < ts )
			t = t + 2400;

		if( t < ts || t >= te )
			return false;
		
		// Validate Monitor Hour
		t = Integer.valueOf(tf2.format(visit.getCheckinStarted()));
		ts = Integer.valueOf(tf2.format(tf.parse(device.getMonitorStart())));
		te = Integer.valueOf(tf2.format(tf.parse(device.getMonitorEnd())));

		if( ts >= te )
			te = te + 2400;
		
		if( te > 2400 && t < ts )
			t = t + 2400;

		if( t < ts || t >= te )
			return false;

		// Validates Pass Hour 
		t = Integer.valueOf(tf2.format(visit.getCheckinStarted()));
		ts = Integer.valueOf(tf2.format(tf.parse(device.getPassStart())));
		te = Integer.valueOf(tf2.format(tf.parse(device.getPassEnd())));

		if( ts >= te )
			te = te + 2400;
		
		if( te > 2400 && t < ts )
			t = t + 2400;

		if( t < ts || t >= te )
			return false;

		// Total segments percentage check
		if( null != visit.getInRangeSegments() && visit.getInRangeSegments() > 0 
				&& null != visit.getTotalSegments() && visit.getTotalSegments() > 0 ) {
			if((visit.getInRangeSegments() * 100 / visit.getTotalSegments()) < VISIT_PERCENTAGE)
				return false;
		}
		
		// If validations are passed, return true
		return true;
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
	private boolean isPeasantValid(APDVisit visit, APDevice device,Boolean isEmployee) throws ParseException {
		
		// Employees DOESN't generate Peasants!
		if( isEmployee )
			return false;
		
		// Validate Hour of day
		int t = Integer.valueOf(tf2.format(visit.getCheckinStarted()));
		int ts = 0;
		int te = 0;
		
		// Validate Monitor Hour
		t = Integer.valueOf(tf2.format(visit.getCheckinStarted()));
		ts = Integer.valueOf(tf2.format(tf.parse(device.getMonitorStart())));
		te = Integer.valueOf(tf2.format(tf.parse(device.getMonitorEnd())));

		if( ts >= te )
			te = te + 2400;
		
		if( te > 2400 && t < ts )
			t = t + 2400;

		if( t < ts || t >= te )
			return false;

		// Validates Pass Hour 
		t = Integer.valueOf(tf2.format(visit.getCheckinStarted()));
		ts = Integer.valueOf(tf2.format(tf.parse(device.getPassStart())));
		te = Integer.valueOf(tf2.format(tf.parse(device.getPassEnd())));

		if( ts >= te )
			te = te + 2400;
		
		if( te > 2400 && t < ts )
			t = t + 2400;

		if( t < ts || t >= te )
			return false;

		// If validations are passed, return true
		return true;
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
	private APDVisit createVisit(APHEntry source, Date date, DeviceInfo device, APDAssignation assign, Boolean isEmployee) throws ASException {
		
		String entityId = assign.getEntityId();
		Integer entityKind = assign.getEntityKind();
		
		APDVisit visit = new APDVisit();
		visit.setApheSource(source.getIdentifier());
		visit.setCheckinStarted(date);
		if(isEmployee)
			visit.setCheckinType(APDVisit.CHECKIN_EMPLOYEE);
		else
			visit.setCheckinType(APDVisit.CHECKIN_VISIT);
		visit.setEntityId(entityId);
		visit.setEntityKind(entityKind);
		visit.setMac(source.getMac());
		visit.setDevicePlatform(source.getDevicePlatform());
		visit.setVerified(false);
		visit.setDeviceUUID(device == null ? null : device.getDeviceUUID());
		visit.setUserId(null);
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
	private APDVisit createPeasant(APHEntry source, Date date, DeviceInfo device, APDAssignation assign) throws ASException {
		
		String entityId = assign.getEntityId();
		Integer entityKind = assign.getEntityKind();
		
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
		peasant.setKey(apdvDao.createKey(peasant));
		
		return peasant;
	}
	
	@Override
	public void fakeVisitsWith(String storeId, String fakeWithStoreId, Date fromDate, Date toDate ) throws ASException {
				
		List<APDVisit> list1 = apdvDao.getUsingEntityIdAndEntityKindAndDate(storeId, EntityKind.KIND_STORE, fromDate, toDate, null, null, null, null, false);
		List<APDVisit> list2 = apdvDao.getUsingEntityIdAndEntityKindAndDate(fakeWithStoreId, EntityKind.KIND_STORE, fromDate, toDate, null, null, null, null, false);
		
		List<APDVisit> lp = CollectionFactory.createList();
		List<APDVisit> lv = CollectionFactory.createList();

		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		
		Map<Integer, Integer> l2p = CollectionFactory.createMap();
		Map<Integer, Integer> l2v = CollectionFactory.createMap();
		int l2pc = 0;
		int l2vc = 0;
		int l1pc = 0;
		int l1vc = 0;
		
		for( APDVisit obj : list2 ) {
			if(obj.getCheckinType().equals(APDVisit.CHECKIN_PEASANT)) {
				l2pc++;
				int key = Integer.parseInt(sdf.format(obj.getCheckinStarted()));
				Integer val = l2p.get(key);
				if( val == null ) val = 0;
				val++;
				l2p.put(key, val);
			}
			if(obj.getCheckinType().equals(APDVisit.CHECKIN_VISIT)) {
				l2vc++;
				int key = Integer.parseInt(sdf.format(obj.getCheckinStarted()));
				Integer val = l2v.get(key);
				if( val == null ) val = 0;
				val++;
				l2v.put(key, val);
			}
		}
		
		for( APDVisit obj : list1 ) {
			if(obj.getCheckinType().equals(APDVisit.CHECKIN_PEASANT)) {
				l1pc++;
				lp.add(obj);
			}
			if(obj.getCheckinType().equals(APDVisit.CHECKIN_VISIT)) {
				l1vc++;
				lv.add(obj);
			}
		}

		int idx = 0;
		int x = 0;
		for( int i = 11; i < 20; i++ ) {
			int xl2p = l2p.get(i);
			float perc = (xl2p * 100 / l2pc);
			
			int count = (int)(perc * l1pc / 100);
			log.log(Level.INFO, count + " peasants for " + storeId + " and hour " + i);			
			
			x = 0;
			while(x < count && idx < lp.size()) {
				APDVisit obj = lp.get(idx);
				idx++;
				x++;
				Calendar cal = Calendar.getInstance();
				cal.setTime(obj.getCheckinStarted());
				cal.set(Calendar.HOUR_OF_DAY, i);
				obj.setCheckinStarted(cal.getTime());
				cal.setTime(obj.getCheckinFinished());
				cal.set(Calendar.HOUR_OF_DAY, i);
				obj.setCheckinFinished(cal.getTime());
				if(obj.getCheckinFinished().before(obj.getCheckinStarted())) {
					cal.setTime(obj.getCheckinFinished());
					cal.set(Calendar.HOUR_OF_DAY, i+1);
					obj.setCheckinFinished(cal.getTime());
				}
				apdvDao.update(obj);
			}
			
		}

	
		idx = 0;
		x = 0;
		for( int i = 11; i < 20; i++ ) {
			int xl2v = l2v.get(i);
			float perc = (xl2v * 100 / l2vc);
			
			int count = (int)(perc * l1vc / 100);
			log.log(Level.INFO, count + " visits for " + storeId + " and hour " + i);

			x = 0;
			while(x < count && idx < lv.size()) {
				APDVisit obj = lv.get(idx);
				idx++;
				x++;
				Calendar cal = Calendar.getInstance();
				cal.setTime(obj.getCheckinStarted());
				cal.set(Calendar.HOUR_OF_DAY, i);
				obj.setCheckinStarted(cal.getTime());
				cal.setTime(obj.getCheckinFinished());
				cal.set(Calendar.HOUR_OF_DAY, i);
				obj.setCheckinFinished(cal.getTime());
				if(obj.getCheckinFinished().before(obj.getCheckinStarted())) {
					cal.setTime(obj.getCheckinFinished());
					cal.set(Calendar.HOUR_OF_DAY, i+1);
					obj.setCheckinFinished(cal.getTime());
				}
				apdvDao.update(obj);
			}
			
		}

	}
	
}
