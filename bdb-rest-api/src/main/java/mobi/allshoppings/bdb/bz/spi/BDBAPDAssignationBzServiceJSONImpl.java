package mobi.allshoppings.bdb.bz.spi;

import static mx.getin.Constants.sdf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.util.CollectionFactory;

import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.Range;

public class BDBAPDAssignationBzServiceJSONImpl extends BDBCrudBzServiceJSONImpl<APDAssignation> implements BDBCrudBzService {

	private static final String ENTITYID = "entityId";
	private static final String ENTITYKIND = "entityKind";
	private static final String HOSTNAME = "hostname";
	private static final String ACTIVE = "active";

	@Autowired
	private BrandDAO brandDao;

	@Autowired
	private StoreDAO storeDao;
	
	@Autowired
	private ShoppingDAO shoppingDao;
	
	@Autowired
	private APDAssignationDAO dao;
	
	@Autowired
	private APDeviceHelper apdeviceHelper;

	/**
	 * Get Add mandatory fields
	 * 
	 * @return A list with all the fields needed for an add request
	 */
	public String[] getMandatoryAddFields() {
		return new String[] {
				"hostname"
		};
	}

	@Override
	public String[] getMandatoryUpdateFields() {
		return new String[] {
				"identifier",
				"hostname"
		};
	}

	@Override
	public String[] getListFields() {
		return new String[] {
				"identifier",
				"entityId",
				"entityKind",
				"hostname",
				"fromDate",
				"toDate",
				"active",
				"name"
		};
	}

	@Override
	public String[] getRetrieveFields() {
		return new String[] {
				"identifier",
				"entityId",
				"entityKind",
				"brandId",
				"brandName",
				"hostname",
				"fromDate",
				"toDate",
				"active",
				"name"
		};
	}

	@Override
	public String retrieve()
	{
		long start = markStart();
		JSONObject returnValue;
		try {
			// obtain the id and validates the auth token
			User user = getUserFromToken();
			String identifier = obtainIdentifier(GENERAL_IDENTIFIER_KEY);
			final APDAssignation obj = myDao.get(identifier);
			
			// Get the output fields
			String[] fields = this.obtainOutputFields(APDAssignationAdapter.class, getRetrieveFields());

			// Obtains the user JSON representation
			returnValue = getJSONRepresentationFromObject(adapt(obj), fields);

			// track action
			trackerHelper.enqueue( user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service." + myClazz.getSimpleName() + "BzService"), 
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
	public String list() {
    	long start = markStart();
		JSONObject returnValue = null;
		try {
			List<APDAssignation> list = new ArrayList<APDAssignation>();
			
			// validate authToken
			User user = this.getUserFromToken();

			// get range, if not defined use default value
			Range range = this.obtainRange();
			
			// Get Status query
			String status = this.obtainStringValue(STATUS, null);
	
			// Get order
			String order = this.obtainStringValue(ORDER, null);

			// Get the output fields
			String[] fields = this.obtainOutputFields(APDAssignationAdapter.class, getListFields());

			// Get hostname
			String hostname = this.obtainStringValue(HOSTNAME, null);

			// Get entityId
			String entityId = this.obtainStringValue(ENTITYID, null);

			// Get entityKnd
			byte entityKind = this.obtainByteValue(ENTITYKIND, (byte) -1);

			// Get entityKnd
			boolean active = this.obtainBooleanValue(ACTIVE, false);

			Map<String,String> attributes = CollectionFactory.createMap();
			
			// Staus filter
			List<Byte> statusList = null;
			if( StringUtils.hasText(status))
				statusList = Arrays.asList(Byte.parseByte(status));

			// retrieve all elements
			long millisPre = new Date().getTime();
			Date forDate = true == active ? new Date() : null;
			
			if( StringUtils.hasText(hostname) ) {
				list = ((APDAssignationDAO)myDao).getUsingHostnameAndDate(hostname, forDate);
			} else if ( StringUtils.hasText(entityId)) {
				list = ((APDAssignationDAO)myDao).getUsingEntityIdAndEntityKindAndDate(entityId, entityKind, forDate);
			} else {
				list = myDao.getUsingStatusAndRange(statusList, range, order, attributes, true);
			}
			
			long diff = new Date().getTime() - millisPre;
			
			// Logs the result
			log.info("Number of elements found [" + list.size() + "] in " + diff + " millis");
			List<APDAssignationAdapter> adapters = CollectionFactory.createList();
			for( APDAssignation obj : list )
				try {
					adapters.add(adapt(obj));
				} catch( ASException e ) {
					log.log(Level.WARNING, e.getMessage(), e);
				}

			returnValue = this.getJSONRepresentationFromArrayOfObjects(adapters, fields);
			
			if( attributes.containsKey("recordCount"))
				returnValue.put("recordCount", Long.valueOf(attributes.get("recordCount")));
			else 
				returnValue.put("recordCount", list.size());
				
			
			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service." + myClazz.getSimpleName() + "BzService"),
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
			returnValue = this.getJSONRepresentationFromException(e);
		} finally {
			markEnd(start);
		}
		return returnValue.toString();
	}

	/**
	 * Creates an adaptation for an APDAssignation
	 * 
	 * @param obj
	 *            The object to adapt
	 * @return The fully adapted object
	 * @throws ASException
	 */
	public APDAssignationAdapter adapt(APDAssignation obj) throws ASException {
		
		APDAssignationAdapter adapter = new APDAssignationAdapter();
		adapter.setKey(obj.getKey());
		adapter.setEntityId(obj.getEntityId());
		adapter.setEntityKind(obj.getEntityKind());
		adapter.setHostname(obj.getHostname());
		adapter.setFromDate(obj.getFromDate());
		adapter.setToDate(obj.getToDate());

		if( obj.getToDate() != null && obj.getToDate().before(new Date()))
			adapter.setActive(false);
		else 
			adapter.setActive(true);
		
		if( obj.getEntityKind() == EntityKind.KIND_STORE) {
			Store tmp = storeDao.get(obj.getEntityId());
			adapter.setName(tmp.getName());
			
			Brand tmpb = brandDao.get(tmp.getBrandId());
			adapter.setBrandId(tmpb.getIdentifier());
			adapter.setBrandName(tmpb.getName());
			
		} else if( obj.getEntityKind() == EntityKind.KIND_SHOPPING) {
			Shopping tmp = shoppingDao.get(obj.getEntityId());
			adapter.setName(tmp.getName());
		}

		return adapter;
	}
	
	/**
	 * Internal adapter to add a store name to the assignation
	 * 
	 * @author mhapanowicz
	 *
	 */
	public class APDAssignationAdapter extends APDAssignation {
		private static final long serialVersionUID = 1L;

		private String name;
		private String brandId;
		private String brandName;
		private Boolean active;
		
		public APDAssignationAdapter() {
			// Default Constructor
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the active
		 */
		public Boolean getActive() {
			return active;
		}

		/**
		 * @param active the active to set
		 */
		public void setActive(Boolean active) {
			this.active = active;
		}

		/**
		 * @return the brandId
		 */
		public String getBrandId() {
			return brandId;
		}

		/**
		 * @param brandId the brandId to set
		 */
		public void setBrandId(String brandId) {
			this.brandId = brandId;
		}

		/**
		 * @return the brandName
		 */
		public String getBrandName() {
			return brandName;
		}

		/**
		 * @param brandName the brandName to set
		 */
		public void setBrandName(String brandName) {
			this.brandName = brandName;
		}
		
	}
	
	@Override
	public void config() {
		setMyDao(dao);
		setMyClazz(APDAssignation.class);
	}

	@Override
	public void preModify(APDAssignation obj, JSONObject json ) throws ASException {

		try {
			if( json.has("fromDate") && StringUtils.hasText(json.getString("fromDate"))) {
				String sFromDate = json.getString("fromDate");
				Date fromDate = sdf.parse(sFromDate);
				obj.setFromDate(fromDate);
			} else {
				Date fromDate = sdf.parse(sdf.format(new Date()));
				obj.setFromDate(fromDate);
			}
			try {
				json.remove("fromDate");
			} catch( Exception e ) {}

			if( json.has("toDate") && StringUtils.hasText(json.getString("toDate"))) {
				String sToDate = json.getString("toDate");
				if(sToDate.equalsIgnoreCase("now")) {
					Date toDate = sdf.parse(sdf.format(new Date()));
					obj.setToDate(toDate);
				} else {
					Date toDate = sdf.parse(sToDate);
					obj.setToDate(toDate);
				}
			} else {
				obj.setToDate(null);
			}
			try {
				json.remove("toDate");
			} catch( Exception e ) {}

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	@Override
	public void postChange(APDAssignation obj) throws ASException {
		try {
			apdeviceHelper.updateAssignationsUsingAPDevice(obj.getHostname());
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@Override
	public void postAdd(APDAssignation obj) throws ASException {
		try {
			apdeviceHelper.updateAssignationsUsingAPDevice(obj.getHostname());
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@Override
	public void postDelete(APDAssignation obj) throws ASException {
		try {
			apdeviceHelper.updateAssignationsUsingAPDevice(obj.getHostname());
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@Override
	public void setKey(APDAssignation obj, JSONObject seed) throws ASException {
		obj.setKey(dao.createKey(obj));
	}

}
