package mobi.allshoppings.bz.spi;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bz.MovieListBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.MovieDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Movie;

/**
 *
 */
public class MovieListBzServiceJSONImpl
extends RestBaseServerResource
implements MovieListBzService {

	private static final Logger log = Logger.getLogger(MovieListBzServiceJSONImpl.class.getName());
	
	@Autowired
	private MovieDAO dao;

	/**
	 * Obtains information about a user
	 * 
	 * @return A JSON representation of the selected fields for a user
	 */
	@Override
	public String retrieve()
	{
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			// obtainUserIdentifier();

			String entityId = obtainStringValue("entityId", null);
//			List<Integer> status = Arrays.asList(new Integer[] {Movie.STATUS_ENABLED});
			List<Movie> list = dao.getUsingBrandAndStatusAndRange(entityId, null, null, "name");

			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();
			for( Movie obj : list ) {
				JSONObject json = new JSONObject();
				json.put("identifier", obj.getIdentifier());
				json.put("name", obj.getName());
				jsonArray.put(json);
			}

			// Returns the final value
			JSONObject ret = new JSONObject();
			ret.put("data", jsonArray);
			return ret.toString();

		} catch (ASException e) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE || 
					e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE) {
				log.log(Level.INFO, e.getMessage());
			} else {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			return getJSONRepresentationFromException(e).toString();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} finally {
			markEnd(start);
		}
	}
}
