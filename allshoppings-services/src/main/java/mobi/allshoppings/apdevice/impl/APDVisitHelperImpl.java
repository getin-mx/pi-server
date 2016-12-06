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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.apdevice.APDVisitHelper;
import mobi.allshoppings.apdevice.APHHelper;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.APDVisitDAO;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.APHEntryDAO;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;

public class APDVisitHelperImpl implements APDVisitHelper {

	private static final Logger log = Logger.getLogger(APDVisitHelperImpl.class.getName());
	private static final SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat tf2 = new SimpleDateFormat("HHmm");
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private APDVisitDAO apdvDao;
	
	@Autowired
	private APDeviceDAO apdDao;

	@Autowired
	private APDAssignationDAO apdaDao;
	
	@Autowired
	private StoreDAO storeDao;
	
	@Autowired
	private APHHelper aphHelper;
	
	@Autowired
	private APHEntryDAO apheDao;

	@Autowired
	private DashboardIndicatorDataDAO didDao;
	
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
	public void generateAPDVisits(List<String> brandIds, List<String> storeIds, Date fromDate, Date toDate, boolean deletePreviousRecords, boolean updateDashboards) throws ASException {

		List<Store> stores = CollectionFactory.createList();
		Map<String, APDevice> apdCache = CollectionFactory.createMap();
		Map<String, APDAssignation> assignmentsCache = CollectionFactory.createMap();
		boolean cacheBuilt = false;

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
			
			for( Store store : stores ) {

				// Try to delete previous records if needed
				if(deletePreviousRecords) {
					apdvDao.deleteUsingEntityIdAndEntityKindAndDate(store.getIdentifier(), EntityKind.KIND_STORE,
							curDate, new Date(curDate.getTime() + 86400000));
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
								Arrays.asList(new String[] { assigs.get(0).getHostname() }), curDate, curDate, false);

						log.log(Level.INFO, "Processing " + entries.size() + " APHEntries...");
						List<APDVisit> objs = CollectionFactory.createList();
						for(APHEntry entry : entries ) {
							List<APDVisit> visitList = aphEntryToVisits(entry, apdCache, assignmentsCache);
							for(APDVisit visit : visitList )
								if(!objs.contains(visit))
									objs.add(visit);
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
						List<APHEntry> entries = apheDao.getUsingHostnameAndDates(hostnames, curDate, curDate, false);
						for( APHEntry entry : entries ) {
							if(!cache.containsKey(entry.getMac()))
								cache.put(entry.getMac(), new ArrayList<APHEntry>());
							
							cache.get(entry.getMac()).add(entry);
						}
						
						log.log(Level.INFO, "Processing " + cache.size() + " APHEntries...");
						List<APDVisit> objs = CollectionFactory.createList();
						Iterator<String> i = cache.keySet().iterator();
						while(i.hasNext()) {
							String key = i.next();
							List<APHEntry> e = cache.get(key);
							List<APDVisit> visitList = aphEntryToVisits(e, apdCache, assignmentsCache);
							for(APDVisit visit : visitList )
								if(!objs.contains(visit))
									objs.add(visit);
						}
						
						log.log(Level.INFO, "Saving " + objs.size() + " APDVisits...");
						try {
							apdvDao.createOrUpdate(null, objs, true);
						} catch( Exception e ) {}
						
					}
				}
				
				// Try to update dashboard if needed
				if(updateDashboards) {
					if(!cacheBuilt) {
						try {
							mapper.buildCaches(false);
							cacheBuilt = true;
						} catch( Exception e ) {
							throw ASExceptionHelper.defaultException(e.getMessage(), e);
						}
					}

					didDao.deleteUsingSubentityIdAndElementIdAndDate(store.getIdentifier(),
							Arrays.asList(new String[] { "apd_visitors", "apd_permanence" }), curDate, curDate);
					mapper.createAPDVisitPerformanceDashboardForDay(curDate,
							Arrays.asList(new String[] { store.getIdentifier() }));
				}

			}
			
			curDate = new Date(curDate.getTime() + 86400000);
		}
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
	public List<APDVisit> aphEntryToVisits(APHEntry entry, Map<String, APDevice> apdCache, Map<String, APDAssignation> assignmentsCache) throws ASException {
		List<APHEntry> entries = CollectionFactory.createList();
		entries.add(entry);
		return aphEntryToVisits(entries, apdCache, assignmentsCache);
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
	public List<APDVisit> aphEntryToVisits(List<APHEntry> entries, Map<String, APDevice> apdCache, Map<String, APDAssignation> assignmentsCache) throws ASException {

		// Validates entries
		if( CollectionUtils.isEmpty(entries))
			throw ASExceptionHelper.invalidArgumentsException();

		// Merges all the time slots
		List<Integer> slots = CollectionFactory.createList();
		if( entries.size() > 1 ) {
			for( APHEntry entry : entries ) {
				List<Integer> tmpSlots = aphHelper.timeslotToList(entry.getArtificialRssi());
				for( Integer i : tmpSlots ) {
					if(!slots.contains(i))
						slots.add(i);
				}
			}
			Collections.sort(slots);
		} else {
			slots = aphHelper.timeslotToList(entries.get(0).getArtificialRssi());
		}

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
		List<APDVisit> ret = CollectionFactory.createList();
		Integer lastSlot = null;
		Integer lastVisitSlot = null;
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
					
					// Closes open visits in case of slot continuity disruption
					if( lastSlot != null && slot != (lastSlot + 1)) {
						if( currentVisit != null ) {
							currentVisit.setCheckinFinished(aphHelper.slotToDate(curEntry.getDate(), lastSlot));
							addPermanenceCheck(currentVisit, currentPeasant, dev);
							if(isVisitValid(currentVisit, dev))
								ret.add(currentVisit);
							currentVisit = null;
						}

						if( currentPeasant != null ) {
							currentPeasant.setCheckinFinished(aphHelper.slotToDate(curEntry.getDate(), lastSlot));
							if(isPeasantValid(currentPeasant, dev))
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
						
						// Checks for power for visit
						if( value >= dev.getVisitPowerThreshold()) {
							if( currentVisit == null )
								currentVisit = createVisit(curEntry, curDate, null, assignments.get(curEntry.getHostname()));
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
							if(isPeasantValid(currentPeasant, dev))
								ret.add(currentPeasant);
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
				if(isPeasantValid(currentPeasant, apd.get(curEntry.getHostname())))
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
	private boolean isPeasantValid(APDVisit visit, APDevice device) throws ParseException {
		
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
	private APDVisit createVisit(APHEntry source, Date date, DeviceInfo device, APDAssignation assign) throws ASException {
		
		String entityId = assign.getEntityId();
		Integer entityKind = assign.getEntityKind();
		
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
}
