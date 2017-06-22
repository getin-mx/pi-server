package mobi.allshoppings.bdb.bz.spi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.dao.APDMAEmployeeDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDMAEmployee;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.tools.Range;

public class BDBAPDMAEmployeeBzServiceJSONImpl extends BDBCrudBzServiceJSONImpl<APDMAEmployee> implements BDBCrudBzService {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private APDMAEmployeeDAO dao;

	@Override
	public String[] getMandatoryAddFields() {
		return new String[] {
				"entityId",
				"entityKind",
				"mac",
				"description"
		};
	}

	@Override
	public String[] getMandatoryUpdateFields() {
		return new String[] {
				"identifier",
				"entityId",
				"entityKind",
				"mac",
				"description"
		};
	}

	@Override
	public String[] getListFields() {
		return new String[] {
				"identifier",
				"entityId",
				"entityKind",
				"mac",
				"userId",
				"deviceUUID",
				"devicePlatform",
				"description",
				"creationDateTime",
				"lastUpdate",
				"fromDate",
				"toDate"
		};
	}
		
	@Override
	public void config() {
		setMyDao(dao);
		setMyClazz(APDMAEmployee.class);
	}

	@Override
	public void setKey(APDMAEmployee obj, JSONObject seed) throws ASException {
		obj.setKey(dao.createKey());
	}
	
	@Override
	public void prePersist(APDMAEmployee obj, JSONObject seed) throws ASException {
		obj.setMac(obj.getMac().toLowerCase());
		
		if( null == obj.getFromDate())
			try {
				obj.setFromDate(sdf.parse(sdf.format(new Date())));
			} catch (ParseException e) {
			}
		else
			try {
				obj.setFromDate(sdf.parse(sdf.format(obj.getFromDate())));
			} catch (ParseException e) {
			}
		
		if( null != obj.getToDate())
			try {
				obj.setToDate(sdf.parse(sdf.format(obj.getToDate())));
			} catch (ParseException e) {
			}

	}

	public String list() {
    	long start = markStart();
		JSONObject returnValue = null;
		try {
			List<APDMAEmployee> list = new ArrayList<APDMAEmployee>();
			
			// validate authToken
			User user = this.getUserFromToken();

			// get range, if not defined use default value
			Range range = this.obtainRange();
			
			// Get Search query
			String q = this.obtainStringValue(Q, null);

			// Additional Search Fields
			Map<String, String> additionalFields = CollectionFactory.createMap();

			// Get EntityId query
			String entityId = this.obtainStringValue(ENTITY_ID, null);

			// Get EntityId query
			Integer entityKind = this.obtainIntegerValue(ENTITY_KIND, null);

			// Get Status query
			String status = this.obtainStringValue(STATUS, null);
	
			// Get order
			String order = this.obtainStringValue(ORDER, null);

			// Get the output fields
			String[] fields = this.obtainOutputFields(myClazz, getListFields());

			// Get the language;
			String lang = this.obtainLang();
			
			Map<String,String> attributes = CollectionFactory.createMap();
			
			// retrieve all elements
			long millisPre = new Date().getTime();
			if( q != null && !q.trim().equals("")) {
				
				if( StringUtils.hasText(status) )
					additionalFields.put("status", status);

				if( StringUtils.hasText(entityId) )
					additionalFields.put("entityId", entityId);

				list = myDao.getUsingIndex(
								myClazz.getName(), q, user.getSecuritySettings().getRole().equals(Role.ADMIN) 
								? null : user.getViewLocation(),
								StringUtils.hasText(status) 
								? Arrays.asList(new Integer[] {Integer.valueOf(status)}) : null, range, 
								additionalFields, order, lang);
			} else {
				list = dao.getUsingEntityIdAndRange(entityId, entityKind, range, order, attributes, false);
			}
			
			long diff = new Date().getTime() - millisPre;
			
			// Logs the result
			log.info("Number of elements found [" + list.size() + "] in " + diff + " millis");
			returnValue = this.getJSONRepresentationFromArrayOfObjects(list, fields);
			
			if( attributes.containsKey("recordCount"))
				returnValue.put("recordCount", Long.valueOf(attributes.get("recordCount")));
			else 
				returnValue.put("recordCount", list.size());
				
			
			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service." + myClazz.getSimpleName() + "BzService"),
					q, null);

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

}
