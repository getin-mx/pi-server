package mobi.allshoppings.bz.spi;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.bz.FloorMapListBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.FloorMapDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.adapter.FloorMapAdapter;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class FloorMapListBzServiceJSONImpl extends RestBaseServerResource implements FloorMapListBzService {

    private static final Logger log = Logger.getLogger(FloorMapListBzServiceJSONImpl.class.getName());

    @Autowired
    private FloorMapDAO dao;
    @Autowired
    private ShoppingDAO shoppingDao;
    
    private BzFields bzFields = BzFields.getBzFields(getClass());

    @Override
    public String retrieve() {
    	
    	long start = markStart();
		JSONObject returnValue = null;
		try {
			List<FloorMapAdapter> floorMaps = new ArrayList<FloorMapAdapter>();
			
			// validate authToken
			User user = this.getUserFromToken();

			// get level, if not defined use default value
			String level = this.obtainStringValue(LEVEL, BzFields.LEVEL_LIST);
			Range range = this.obtainRange();

			// set adapter options
			Map<String,Object> options = CollectionFactory.createMap();
			options.put(FloorMapAdapter.OPTIONS_SHOPPINGDAO, shoppingDao);
			
			// retrieve all Floor Maps
			long millisPre = new Date().getTime();
			floorMaps = new GenericAdapterImpl<FloorMapAdapter>().adaptList(
					dao.getUsingStatusAndUserAndRange(null, user, range),
					user.getIdentifier(), null, null, options);
			long diff = new Date().getTime() - millisPre;
			
			// Logs the result
			log.info("Number of floor maps found [" + floorMaps.size() + "] in " + diff + " millis");
			returnValue = this.getJSONRepresentationFromArrayOfObjects(floorMaps, this.obtainOutputFields(bzFields, level));
			
			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.FloorMapListBzService"),
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
