package mobi.allshoppings.bdb.bz.spi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.util.CollectionFactory;

import mobi.allshoppings.bdb.bz.BDBGetBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.AuditLogDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.AuditLog;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.tools.Range;

public class BDBAuditLogBzServiceJSONImpl extends BDBRestBaseServerResource implements BDBGetBzService {

	private static final Logger log = Logger.getLogger(BDBAuditLogBzServiceJSONImpl.class.getName());
	
	@Autowired
	private AuditLogDAO dao;
	
	public String[] getListFields() {
		return new String[] {
				"identifier",
				"userId",
				"eventDate",
				"eventType",
				"description",
				"meta",
				"entityId",
				"entityKind",
				"creationDateTime",
				"lastUpdate"
		};
	}

	@Override
    public String retrieve() {
    	long start = markStart();
		JSONObject returnValue = null;
		try {
			List<AuditLog> list = new ArrayList<AuditLog>();
			
			// validate authToken
			User user = this.getUserFromToken();

			// get range, if not defined use default value
			Range range = this.obtainRange();
			
			// Get Search query
			String q = this.obtainStringValue(Q, null);

			// Additional Search Fields
			Map<String, String> additionalFields = CollectionFactory.createMap();

			// Get Status query
			String status = this.obtainStringValue(STATUS, null);

			// Get User ID Query
			String userId = this.obtainStringValue("userId", null);

			// Get Event Type Query
			byte eventType = this.obtainByteValue("eventType", (byte) -1);

			// Get order
			String order = "eventDate descend";

			// Get the output fields
			String[] fields = this.obtainOutputFields(AuditLog.class, getListFields());

			// Get the language;
			String lang = this.obtainLang();
			
			Map<String,String> attributes = CollectionFactory.createMap();
			
			// Staus filter
			List<Byte> statusList = null;
			if( StringUtils.hasText(status)) {
				if( status.contains(",")) {
					String tmpStatus[] = status.split(",");
					statusList = CollectionFactory.createList();
					for( String t : tmpStatus ) 
						statusList.add(Byte.parseByte(t));
				} else {
					statusList = Arrays.asList(Byte.parseByte(status));
				}
			}

			// retrieve all elements
			long millisPre = new Date().getTime();
			if( q != null && !q.trim().equals("")) {
				
				if( StringUtils.hasText(status) )
					additionalFields.put("status", status);

				list = dao.getUsingIndex(
								AuditLog.class.getName(), q, user.getSecuritySettings().getRole().equals(Role.ADMIN) 
								? null : user.getViewLocation(),
								StringUtils.hasText(status) 
								? statusList : null, range, 
								additionalFields, order, lang);
			} else {
				list = dao.getUsingUserAndTypeAndRange(userId, eventType, range, order, attributes, true);
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
					getI18NMessage("es_AR", "service." + AuditLog.class.getSimpleName() + "BzService"),
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
