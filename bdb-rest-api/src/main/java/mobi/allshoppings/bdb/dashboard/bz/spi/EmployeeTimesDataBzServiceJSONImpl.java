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
import mobi.allshoppings.dao.APDMAEmployeeDAO;
import mobi.allshoppings.dao.APDVisitDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDMAEmployee;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class EmployeeTimesDataBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(EmployeeTimesDataBzServiceJSONImpl.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
	private static final long ONE_DAY = 86400000;

	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private APDMAEmployeeDAO apdmaeDao;
	@Autowired
	private APDVisitDAO apdvDao;

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

			List<APDMAEmployee> employees = CollectionFactory.createList();
			if( entityKind.equals(EntityKind.KIND_BRAND))
				employees.addAll(apdmaeDao.getUsingEntityIdAndRange(entityId, entityKind, null, null, null, false));
			
			for(Store store : stores ) {
				storeNames.add(store.getName());
				storeCacheByName.put(store.getName(), store);
				storeCacheById.put(store.getIdentifier(), store);
				employees.addAll(apdmaeDao.getUsingEntityIdAndRange(entityId, entityKind, null, null, null, false));
			}

			Collections.sort(storeNames);

			// Ordered employee list
			List<String> employeeNames = CollectionFactory.createList();
			Map<String, APDMAEmployee> employeeCacheByName = CollectionFactory.createMap();
			
			for(APDMAEmployee employee : employees ) {
				employeeNames.add(employee.getDescription());
				employeeCacheByName.put(employee.getDescription(), employee);
				employeeCacheByName.put(employee.getMac(), employee);
			}
			
			
			// Gets the data
			Map<String, Map<String, Map<Date, List<Date>>>> data = CollectionFactory.createMap();
			Date toDate = new Date(sdf.parse(toStringDate).getTime());
			Date curDate = new Date(sdf.parse(fromStringDate).getTime());

			while(curDate.before(toDate) || curDate.equals(toDate)) {
				for( Store store : stores ) {

						Date postDate = new Date(curDate.getTime() + ONE_DAY);

						List<APDVisit> emps = apdvDao.getUsingEntityIdAndEntityKindAndDate(store.getIdentifier(), EntityKind.KIND_STORE, curDate, postDate, APDVisit.CHECKIN_EMPLOYEE, null, null, false);
						for( APDVisit apdv : emps ) {
							Map<String, Map<Date, List<Date>>> data2 = data.get(store.getIdentifier());
							if( data2 == null ) data2 = CollectionFactory.createMap();
							Map<Date, List<Date>> data3 = data2.get(apdv.getMac());
							if( data3 == null ) data3 = CollectionFactory.createMap();
							List<Date> data4 = data3.get(curDate);
							if( data4 == null ) {
								data4 = CollectionFactory.createList();
								data4.add(null);
								data4.add(null);
							}
							
							if( data4.get(0) == null || apdv.getCheckinStarted().before(data4.get(0)))
								data4.set(0, apdv.getCheckinStarted());

							if( data4.get(1) == null || apdv.getCheckinFinished().after(data4.get(1)))
								data4.set(1, apdv.getCheckinFinished());
							
							data3.put(curDate, data4);
							data2.put(apdv.getMac(), data3);
							data.put(store.getIdentifier(), data2);
									
						}

				}
				curDate = new Date(curDate.getTime() + ONE_DAY);
			}

						
			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();

			// Titles row
			JSONArray titles = new JSONArray();
			titles.put("Tienda");
			titles.put("Empleado");
			titles.put("Dia");
			titles.put("Entrada");
			titles.put("Salida");
			jsonArray.put(titles);

			// Values Array
			for( String name : storeNames ) {
				Store store = storeCacheByName.get(name);
				for( String empName : employeeNames ) {
					APDMAEmployee employee = employeeCacheByName.get(empName);

					Map<String, Map<Date, List<Date>>> data2 = data.get(store.getIdentifier());
					if( data2 != null ) {
						
						Map<Date, List<Date>> data3 = data2.get(employee.getMac());
						if( data3 != null ) {

							List<Date> dates = CollectionFactory.createList();
							dates.addAll(data3.keySet());
							Collections.sort(dates);

							Iterator<Date> i = dates.iterator();
							while( i.hasNext()) {
								Date key = i.next();
								JSONArray row = new JSONArray();
								row.put(name);
								row.put(empName);
								row.put(getDateName(key));
								List<Date> times = data3.get(key);
								row.put((times.get(0) == null ? "-" : sdf2.format(times.get(0))));
								row.put((times.get(1) == null ? "-" : sdf2.format(times.get(1))));
								jsonArray.put(row);
							}
						}
					}
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
