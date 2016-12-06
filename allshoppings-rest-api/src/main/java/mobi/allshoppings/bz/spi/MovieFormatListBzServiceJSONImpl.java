package mobi.allshoppings.bz.spi;

import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.bz.MovieFormatListBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.exception.ASExceptionHelper;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 */
public class MovieFormatListBzServiceJSONImpl
extends RestBaseServerResource
implements MovieFormatListBzService {

	private static final Logger log = Logger.getLogger(MovieFormatListBzServiceJSONImpl.class.getName());
	
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

			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();
			JSONObject json;
			
			json = new JSONObject();
			json.put("identifier", "2D");
			json.put("name", "2D");
			jsonArray.put(json);

			json = new JSONObject();
			json.put("identifier", "3D");
			json.put("name", "3D");
			jsonArray.put(json);

			json = new JSONObject();
			json.put("identifier", "IMAX2D");
			json.put("name", "IMAX2D");
			jsonArray.put(json);

			json = new JSONObject();
			json.put("identifier", "IMAX3D");
			json.put("name", "IMAX3D");
			jsonArray.put(json);

			json = new JSONObject();
			json.put("identifier", "4DX2D");
			json.put("name", "4DX2D");
			jsonArray.put(json);

			json = new JSONObject();
			json.put("identifier", "4DX3D");
			json.put("name", "4DX3D");
			jsonArray.put(json);

			// Returns the final value
			JSONObject ret = new JSONObject();
			ret.put("data", jsonArray);
			return ret.toString();

		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} finally {
			markEnd(start);
		}
	}
}
