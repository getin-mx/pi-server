package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.APUptimeDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APUptime;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class OpenTimesDataBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(OpenTimesDataBzServiceJSONImpl.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final long ONE_DAY = 86400000;

	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private APDAssignationDAO apdaDao;
	@Autowired
	private APUptimeDAO apuDao;
	@Autowired
	private APDeviceDAO devDao;

	/**
	 * Obtains information about a user
	 * 
	 * @return A JSON representation of the selected fields for a user
	 */
	@Override
	public String retrieve()
	{
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			User user = getUserFromToken();

			String entityId = obtainStringValue("entityId", null);
			Integer entityKind = obtainIntegerValue("entityKind", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);

			// Get all the stores that matches the brand
			List<Store> stores = CollectionFactory.createList();
			if( entityKind.equals(EntityKind.KIND_BRAND)) {
				List<Store> tmpStores = storeDao.getUsingBrandAndStatus(entityId, Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED}), "name"); 
				for( Store store : tmpStores ) {
					if( isValidForUser(user, store))
						stores.add(store);
				}
			} else if( entityKind.equals(EntityKind.KIND_STORE)) {
				stores.add(storeDao.get(entityId));
			}
			
			// Ordered Store List
			List<String> storeNames = CollectionFactory.createList();
			Map<String, Store> storeCacheByName = CollectionFactory.createMap();
			Map<String, Store> storeCacheById = CollectionFactory.createMap();

			for(Store store : stores ) {
				storeNames.add(store.getName());
				storeCacheByName.put(store.getName(), store);
				storeCacheById.put(store.getIdentifier(), store);
			}

			Collections.sort(storeNames);

			// Gets the data
			Map<String, Map<Date, List<String>>> data = CollectionFactory.createMap();
			Date toDate = new Date(sdf.parse(toStringDate).getTime());
			Date curDate = new Date(sdf.parse(fromStringDate).getTime());
			
			Calendar cal = Calendar.getInstance();
			List<APDAssignation> devAssig;
			APDevice dev;
			String openCloseTime;

			while(curDate.before(toDate) || curDate.equals(toDate)) {
				for( Store store : stores ) {
					String openTime = null;
					String closeTime = null;
					
					List<APDAssignation> assigs = apdaDao.getUsingEntityIdAndEntityKindAndDate(store.getIdentifier(), EntityKind.KIND_STORE, curDate);
					for( APDAssignation assig : assigs ) {
						APUptime apu = apuDao.getUsingHostnameAndDate(assig.getHostname(), curDate);
						List<String> times = CollectionFactory.createList();
						times.addAll(apu.getRecord().keySet());
						Collections.sort(times);
						
						for( String time : times ) {
							if( apu.getRecord().get(time).equals(1)) {
								if( openTime == null )
									openTime = time;
								if( closeTime == null || time.compareTo(closeTime) > 0 )
									closeTime = time;
							}
						}
					}
					
					Map<Date, List<String>> data2 = data.get(store.getIdentifier());
					if( data2 == null ) data2 = CollectionFactory.createMap();
					List<String> data3 = data2.get(curDate);
					if( data3 == null ) data3 = CollectionFactory.createList();
					data3.add(openTime);
					data3.add(closeTime);
					data2.put(curDate, data3);
					data.put(store.getIdentifier(), data2);
					
				}
				curDate = new Date(curDate.getTime() + ONE_DAY);
			}

						
			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();

			// Titles row
			JSONArray titles = new JSONArray();
			titles.put("Tienda");
			titles.put("Apertura/Cierre");
			titles.put("Dia");
			titles.put("Apertura");
			titles.put("Cierre");
			jsonArray.put(titles);

			// Values Array
			for( String name : storeNames ) {
				Store store = storeCacheByName.get(name);
				Map<Date, List<String>> rowData = data.get(store.getIdentifier());
				List<Date> dates = CollectionFactory.createList();
				dates.addAll(rowData.keySet());
				Collections.sort(dates);
				devAssig = apdaDao.getUsingEntityIdAndEntityKind(store.getIdentifier(),
						EntityKind.KIND_STORE);
				if(devAssig != null && !devAssig.isEmpty()) {
					dev = devDao.get(devAssig.get(0).getHostname());
					switch(cal.get(Calendar.DAY_OF_WEEK)) {
					case Calendar.SUNDAY :
						openCloseTime = dev.getVisitStartSun() +"/" +dev.getVisitEndSun();
						break;
					case Calendar.MONDAY :
						openCloseTime = dev.getVisitStartMon() +"/" +dev.getVisitEndMon();
						break;
					case Calendar.TUESDAY :
						openCloseTime = dev.getVisitStartTue() +"/" +dev.getVisitEndTue();
						break;
					case Calendar.WEDNESDAY :
						openCloseTime = dev.getVisitStartWed() +"/" +dev.getVisitEndWed();
						break;
					case Calendar.THURSDAY :
						openCloseTime = dev.getVisitStartThu() +"/" +dev.getVisitEndThu();
						break;
					case Calendar.FRIDAY :
						openCloseTime = dev.getVisitStartFri() +"/" +dev.getVisitEndFri();
						break;
					case Calendar.SATURDAY :
						openCloseTime = dev.getVisitStartSat() +"/" +dev.getVisitEndSat();
						break;
					default :
						openCloseTime = "";
					}
				} else openCloseTime = "";
				
				Iterator<Date> i = dates.iterator();
				while( i.hasNext()) {
					Date key = i.next();
					JSONArray row = new JSONArray();
					row.put(name);
					cal.setTime(key);
					row.put(openCloseTime);
					row.put(getDateName(key));
					List<String> times = rowData.get(key);
					row.put((times.get(0) == null ? "-" : times.get(0)));
					row.put((times.get(1) == null ? "-" : times.get(1)));
					jsonArray.put(row);
				}
				
			}

			// Returns the final value
			return jsonArray.toString();

		} catch (ASException e) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE || 
					e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE) {
				log.log(Level.INFO, e.getMessage());
			} else {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			return getJSONRepresentationFromException(e).toString();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} finally {
			markEnd(start);
		}
	}

	public boolean isValidForUser(User user, Store store) {
		if( user.getSecuritySettings().getRole().equals(Role.STORE)) {
			if( user.getSecuritySettings().getStores().contains(store.getIdentifier()))
				return true;
			else
				return false;
		} else 
			return true;
	}

	public String getDateName(Date date) {
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int dof = cal.get(Calendar.DAY_OF_WEEK);

		switch(dof) {
		case Calendar.SUNDAY:
			sb.append("Dom ");
			break;
		case Calendar.MONDAY:
			sb.append("Lun ");
			break;
		case Calendar.TUESDAY:
			sb.append("Mar ");
			break;
		case Calendar.WEDNESDAY:
			sb.append("Mie ");
			break;
		case Calendar.THURSDAY:
			sb.append("Jue ");
			break;
		case Calendar.FRIDAY:
			sb.append("Vie ");
			break;
		case Calendar.SATURDAY:
			sb.append("Sab ");
			break;
		}

		sb.append(sdf.format(date));

		return sb.toString();
	}
}
