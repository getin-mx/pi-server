package mobi.allshoppings.bz.spi;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.SystemConfigurationBzService;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;


/**
 *
 */
public class SystemConfigurationBzServiceJSONImpl extends RestBaseServerResource implements SystemConfigurationBzService {

	private static final Logger log = Logger.getLogger(SystemConfigurationBzServiceJSONImpl.class.getName());
	
	@Autowired
	private SystemConfiguration systemConfiguration;

	private BzFields bzFields = BzFields.getBzFields(getClass());

	/**
	 * Obtains information about a shopping
	 * 
	 * @return A JSON representation of the selected fields for a shopping
	 */
	@Override
	public String retrieve() {
		
		long start = markStart();
		JSONObject returnValue;
		try {
			// validate authToken
			User user = this.getUserFromToken();

			// Obtains the shopping entity
			String level = obtainStringValue(LEVEL, BzFields.LEVEL_PUBLIC);

			// Obtains the user JSON representation
			final String[] fields = obtainOutputFields(bzFields, level);
			returnValue = getJSONRepresentationFromObject(systemConfiguration, fields);
			
			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.SystemConfigurationBzService"),
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
}
