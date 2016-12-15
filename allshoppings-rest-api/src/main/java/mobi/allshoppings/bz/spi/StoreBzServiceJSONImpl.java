package mobi.allshoppings.bz.spi;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.StoreBzService;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;


/**
 *
 */
public class StoreBzServiceJSONImpl extends RestBaseServerResource implements StoreBzService {

	private static final Logger log = Logger.getLogger(StoreBzServiceJSONImpl.class.getName());
	
	@Autowired
	private ShoppingDAO dao;
	@Autowired
	private SystemConfiguration systemConfiguration;

	private static final String IDENTIFIER = "shoppingId";
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

			// obtain the id
			final String storeId = obtainIdentifier(IDENTIFIER);
			final Shopping obj;

			// Obtains the shopping entity
			String level = obtainStringValue(LEVEL, systemConfiguration.getDefaultLevel());
			obj = dao.get(storeId);

			// Obtains the user JSON representation
			final String[] fields = obtainOutputFields(bzFields, level);
			returnValue = getJSONRepresentationFromObject(obj, fields);
			
    		// track action
    		trackerHelper.enqueue( user, getRequestIP(),
    				getRequestAgent(), getFullRequestURI(),
    				getI18NMessage("es_AR", "service.StoreBzService") + obj.getName(), 
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
