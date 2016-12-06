package mobi.allshoppings.bdb.bz.spi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.util.StringUtils;

import com.inodes.util.CollectionFactory;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.Range;


/**
 *
 */
public abstract class BDBCrudByCountryBzServiceJSONImpl<T extends ModelKey>
extends BDBCrudBzServiceJSONImpl<T> {

	protected static final Logger log = Logger.getLogger(BDBCrudByCountryBzServiceJSONImpl.class.getName());

	public abstract String sanitizeOrder(String country, String order, Map<String, String> additionalFields);
	
	public String list() {
    	long start = markStart();
		JSONObject returnValue = null;
		try {
			List<T> list = new ArrayList<T>();
			
			// validate authToken
			User user = this.getUserFromToken();

			// get range, if not defined use default value
			Range range = this.obtainRange();
			
			// Get Search query
			String q = this.obtainStringValue(Q, null);

			// Get Country query
			String country = this.obtainStringValue(COUNTRY, null);

			// Get Status query
			String status = this.obtainStringValue(STATUS, null);

			// Additional Search Fields
			Map<String, String> additionalFields = CollectionFactory.createMap();

			// Get order
			String order = this.obtainStringValue(ORDER, null);

			// Get the output fields
			String[] fields = this.obtainOutputFields(myClazz, getListFields());

			// Get the language;
			String lang = this.obtainLang();
			
			Map<String,String> attributes = CollectionFactory.createMap();
			
			// Staus filter
			List<Integer> statusList = null;
			if( StringUtils.hasText(status))
				statusList = Arrays.asList(new Integer[] {Integer.parseInt(status)});
			
			// retrieve all elements
			long millisPre = new Date().getTime();
			if( q != null && !q.trim().equals("")) {

				if( StringUtils.hasText(status) )
					additionalFields.put("status", status);
				
				order = sanitizeOrder(country, order, additionalFields);
				
				list = myDao.getUsingIndex(
								myClazz.getName(), q, user.getSecuritySettings().getRole().equals(Role.ADMIN) 
								? null : user.getViewLocation(),
								StringUtils.hasText(status) 
								? Arrays.asList(new Integer[] {Integer.valueOf(status)}) : null, range, 
								additionalFields, order, lang);
			} else {
				list = myDao.getUsingStatusAndRangeAndCountry(statusList, range, country, order, attributes, true);
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
