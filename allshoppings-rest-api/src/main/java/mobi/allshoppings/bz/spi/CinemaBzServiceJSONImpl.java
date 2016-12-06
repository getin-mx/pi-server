package mobi.allshoppings.bz.spi;

import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.bz.CinemaBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Cinema;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class CinemaBzServiceJSONImpl
extends RestBaseServerResource
implements CinemaBzService {

	private static final Logger log = Logger.getLogger(CinemaBzServiceJSONImpl.class.getName());
	
	@Autowired
	private CinemaDAO dao;

	private static final String IDENTIFIER = "cinemaId";

	/**
	 * Obtains information about a user
	 * 
	 * @return A JSON representation of the selected fields for a user
	 */
	@Override
	public String retrieve()
	{
		long start = markStart();
		JSONObject returnValue;
		try {
			// validate authToken
			getUserFromToken();
			
			// obtain the id
			final String cinemaId = obtainIdentifier(IDENTIFIER);
			Cinema obj = dao.get(cinemaId);
			
			// Obtains the user JSON representation
			final String[] fields = obtainOutputFields(obj);
			returnValue = getJSONRepresentationFromObject(obj, fields);

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
