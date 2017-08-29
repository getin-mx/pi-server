package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.APDMAEmployeeDAO;
import mobi.allshoppings.dao.EmployeeLogDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDMAEmployee;
import mobi.allshoppings.model.EmployeeLog;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;
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
	private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private APDMAEmployeeDAO apdmaeDao;
	@Autowired
	private EmployeeLogDAO employeeLogDao;
	
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
			String employeeId = obtainStringValue("employeeId", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);

			// Get all the stores that matches the brand
			Map<String, Store> storeMap = CollectionFactory.createMap();
			if( entityKind.equals(EntityKind.KIND_BRAND)) {
				List<Store> tmpStores = storeDao.getUsingBrandAndStatus(entityId, Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED}), null); 
				for( Store store : tmpStores ) {
					if( isValidForUser(user, store))
						storeMap.put(store.getIdentifier(), store);
				}
			} else if( entityKind.equals(EntityKind.KIND_STORE)) {
				Store store = storeDao.get(entityId, true);
				storeMap.put(store.getIdentifier(), store);
			}
			List<String> storeList = CollectionFactory.createList();
			storeList.addAll(storeMap.keySet());
			
			// Get all employees 
			Map<String, APDMAEmployee> employeeMap = CollectionFactory.createMap();
			if( entityKind.equals(EntityKind.KIND_BRAND)) {
				List<APDMAEmployee> tmpList = apdmaeDao.getUsingEntityIdAndRange(entityId, entityKind, null, null, null, false);
				for( APDMAEmployee obj : tmpList ) {
					employeeMap.put(obj.getMac(), obj);
				}
				Iterator<Store> i = storeMap.values().iterator();
				while(i.hasNext()) {
					Store store = i.next();
					tmpList = apdmaeDao.getUsingEntityIdAndRange(store.getIdentifier(), EntityKind.KIND_STORE, null, null, null, false);
					for( APDMAEmployee obj : tmpList ) {
						employeeMap.put(obj.getMac(), obj);
					}
				}
			
			} else if( entityKind.equals(EntityKind.KIND_STORE)) {
				List<APDMAEmployee> tmpList = apdmaeDao.getUsingEntityIdAndRange(entityId, entityKind, null, null, null, false);
				for( APDMAEmployee obj : tmpList ) {
					employeeMap.put(obj.getMac(), obj);
				}
			} 

			Date fromDate = sdf.parse(fromStringDate);
			Date toDate = sdf.parse(toStringDate);
			toDate = new Date(toDate.getTime() + 86400000);
			
			EmployeeLogTableRep table = new EmployeeLogTableRep();
			List<EmployeeLog> emps = employeeLogDao.getUsingEntityIdAndEntityKindAndDate(employeeId, storeList, EntityKind.KIND_STORE, fromDate, toDate, null, "checkinStarted,employeeId", null, false);
			for( EmployeeLog obj : emps ) {
				table.addRecord(obj.getEntityId(), obj.getEntityKind(), obj.getMac(), obj.getCheckinStarted(),
						obj.getCheckinFinished(), storeMap, employeeMap);
			}

			JSONObject resp = new JSONObject();
			JSONArray data = new JSONArray();
			Collections.sort(table.records, new Comparator<EmployeeLogRecordRep>() {

				@Override
				public int compare(EmployeeLogRecordRep o1, EmployeeLogRecordRep o2) {
					if( !o1.getStoreName().equals(o2.getStoreName())) {
						return o1.getStoreName().compareTo(o2.getStoreName());
					}
					if( !o1.getEmployeeName().equals(o2.getEmployeeName())) {
						return o1.getEmployeeName().compareTo(o2.getEmployeeName());
					}
					
					if( !o1.getCheckinStarted().equals(o2.getCheckinStarted())) {
						return o1.getCheckinStarted().compareTo(o2.getCheckinStarted());
					}
					return 0;
				}
			}); 
			for( EmployeeLogRecordRep r : table.records ) {
				data.put(r.toJSONObject(storeMap, employeeMap));
			}
			resp.put("data", data);
			return resp.toString();
						
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

	public class EmployeeLogRecordRep {
		
		private String entityId;
		private Integer entityKind;
		private String storeName;
		private String employeeName;
		private String mac;
		private Date checkinStarted;
		private Date checkinFinished;
		private String forDate;

		public EmployeeLogRecordRep(String entityId, Integer entityKind, String mac, Date checkinStarted,
				Date checkinFinished, Map<String, Store> storeMap, Map<String, APDMAEmployee> employeeMap) {
			super();
			this.entityId = entityId;
			this.entityKind = entityKind;
			this.mac = mac;
			this.checkinStarted = checkinStarted;
			this.checkinFinished = checkinFinished;
			this.forDate = sdf.format(checkinStarted);
			storeName = storeMap.get(entityId).getName();
			employeeName = employeeMap.get(mac).getDescription();
		}

		/**
		 * @return the entityId
		 */
		public String getEntityId() {
			return entityId;
		}
		
		/**
		 * @param entityId the entityId to set
		 */
		public void setEntityId(String entityId) {
			this.entityId = entityId;
		}
		
		/**
		 * @return the entityKind
		 */
		public Integer getEntityKind() {
			return entityKind;
		}
		
		/**
		 * @param entityKind the entityKind to set
		 */
		public void setEntityKind(Integer entityKind) {
			this.entityKind = entityKind;
		}
		
		/**
		 * @return the storeName
		 */
		public String getStoreName() {
			return storeName;
		}

		/**
		 * @param storeName the storeName to set
		 */
		public void setStoreName(String storeName) {
			this.storeName = storeName;
		}

		/**
		 * @return the employeeName
		 */
		public String getEmployeeName() {
			return employeeName;
		}

		/**
		 * @param employeeName the employeeName to set
		 */
		public void setEmployeeName(String employeeName) {
			this.employeeName = employeeName;
		}

		/**
		 * @return the forDate
		 */
		public String getForDate() {
			return forDate;
		}

		/**
		 * @param forDate the forDate to set
		 */
		public void setForDate(String forDate) {
			this.forDate = forDate;
		}

		/**
		 * @return the mac
		 */
		public String getMac() {
			return mac;
		}
		
		/**
		 * @param mac the mac to set
		 */
		public void setMac(String mac) {
			this.mac = mac;
		}
		
		/**
		 * @return the checkinStarted
		 */
		public Date getCheckinStarted() {
			return checkinStarted;
		}
		
		/**
		 * @param checkinStarted the checkinStarted to set
		 */
		public void setCheckinStarted(Date checkinStarted) {
			this.checkinStarted = checkinStarted;
		}
		
		/**
		 * @return the checkinFinished
		 */
		public Date getCheckinFinished() {
			return checkinFinished;
		}
		
		/**
		 * @param checkinFinished the checkinFinished to set
		 */
		public void setCheckinFinished(Date checkinFinished) {
			this.checkinFinished = checkinFinished;
		}
		
		public JSONObject toJSONObject(Map<String, Store> storeMap, Map<String, APDMAEmployee> employeeMap) {
			JSONObject ret = new JSONObject();
			
			ret.put("store", storeMap.get(entityId).getName());
			ret.put("employee", employeeMap.get(mac).getDescription());
			ret.put("date", sdf3.format(checkinStarted));
			ret.put("start", sdf2.format(checkinStarted));
			ret.put("finish", sdf2.format(checkinFinished));
			
			return ret;
		}
	}
	
	public class EmployeeLogTableRep {
		private List<EmployeeLogRecordRep> records;

		public EmployeeLogTableRep() {
			records = CollectionFactory.createList();
		}

		/**
		 * @return the records
		 */
		public List<EmployeeLogRecordRep> getRecords() {
			return records;
		}

		/**
		 * @param records the records to set
		 */
		public void setRecords(List<EmployeeLogRecordRep> records) {
			this.records = records;
		}

		public void addRecord(String entityId, Integer entityKind, String mac, Date checkinStarted,
				Date checkinFinished, Map<String, Store> storeMap, Map<String, APDMAEmployee> employeeMap) {
			
			EmployeeLogRecordRep r = null;
			String forDate = sdf.format(checkinStarted);
			for( EmployeeLogRecordRep rec : records ) {
				if (rec.getEntityId().equals(entityId) && rec.getEntityKind().equals(entityKind)
						&& rec.getMac().equals(mac) && rec.getForDate().equals(forDate)) {
					r = rec;
					break;
				}	
			}

			if( r == null ) {
				records.add(new EmployeeLogRecordRep(entityId, entityKind, mac, checkinStarted, checkinFinished,
						storeMap, employeeMap));
			} else {
				if( r.getCheckinStarted().after(checkinStarted)) {
					r.setCheckinStarted(checkinStarted);
				}
				if( r.getCheckinFinished().before(checkinFinished)) {
					r.setCheckinFinished(checkinFinished);
				}
			}
			
		}

		/**
		 * Gets all the headers in a JSON Array
		 * @return
		 */
		public JSONArray getJSONHeaders() {

			// Titles row
			JSONArray titles = new JSONArray();

			titles.put("Tienda");
			titles.put("Empleado");
			titles.put("Dia");
			titles.put("Entrada");
			titles.put("Salida");

			return titles;
		}

	}
}
