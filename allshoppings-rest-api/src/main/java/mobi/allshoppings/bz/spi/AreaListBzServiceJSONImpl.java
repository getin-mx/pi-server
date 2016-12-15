package mobi.allshoppings.bz.spi;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.TableListBzService;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.AreaDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Area;
import mobi.allshoppings.model.User;

/**
 *
 */
public class AreaListBzServiceJSONImpl extends RestBaseServerResource implements TableListBzService {

    private static final Logger log = Logger.getLogger(AreaListBzServiceJSONImpl.class.getName());

    @Autowired
    private AreaDAO dao;
    
    private BzFields bzFields = BzFields.getBzFields(getClass());

    @Override
    public String retrieve() {
    	
		long start = markStart();
		JSONObject returnValue = null;
		try {
			List<Area> list = new ArrayList<Area>();
			
			// validate authToken
			User user = this.getUserFromToken();
			String lang = this.obtainLang();
			
			// get level, if not defined use default value
			String level = this.obtainStringValue(LEVEL, BzFields.LEVEL_LIST);
			
			// retrieve all offerTypes
			list = dao.getAll();
			log.info("Number of records found [" + list.size() + "]");
			returnValue = this.getJSONRepresentationFromArrayOfObjects(list, this.obtainOutputFields(bzFields, level), lang);
			
			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.AreaListBzService"),
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
