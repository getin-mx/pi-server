package mobi.allshoppings.bz.spi;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.StoreListBzService;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserEntityCache;
import mobi.allshoppings.tools.Range;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class StoreListBzServiceJSONImpl extends RestBaseServerResource implements StoreListBzService {

    private static final Logger log = Logger.getLogger(StoreListBzServiceJSONImpl.class.getName());

    @Autowired
    private ShoppingDAO dao;
    
    private BzFields bzFields = BzFields.getBzFields(getClass());

    @Override
    public String retrieve() {
    	
    	long start = markStart();
		JSONObject returnValue = null;
		try {
			List<Shopping> shoppings = new ArrayList<Shopping>();
			
			// validate authToken
			User user = this.getUserFromToken();
			
			// get level, if not defined use default value
			String level = this.obtainStringValue(LEVEL, BzFields.LEVEL_LIST);
			Range range = this.obtainRange();
			
			// retrieve all shoppings
			shoppings = dao.getUsingStatusAndRangeInCache(new Vector<Integer>(), range, 
					user, UserEntityCache.TYPE_FAVORITES_FIRST, 
					"uCountry asc, uProvince asc, uIdentifier asc");
			log.info("Number of shoppings found [" + shoppings.size() + "]");
			returnValue = this.getJSONRepresentationFromArrayOfObjects(shoppings, this.obtainOutputFields(bzFields, level));

			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.StoreListBzService"),
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
}
