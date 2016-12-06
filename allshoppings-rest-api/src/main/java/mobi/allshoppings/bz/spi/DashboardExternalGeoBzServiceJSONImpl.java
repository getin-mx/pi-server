package mobi.allshoppings.bz.spi;


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.bz.DashboardTopDataBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.ExternalGeoDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.ExternalGeo;
import mobi.allshoppings.tools.Range;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;


/**
 *
 */
public class DashboardExternalGeoBzServiceJSONImpl
extends RestBaseServerResource
implements DashboardTopDataBzService {

	private static final Logger log = Logger.getLogger(DashboardExternalGeoBzServiceJSONImpl.class.getName());

	@Autowired
	private ExternalGeoDAO dao;

	/**
	 * Obtains information about an external geo map
	 * 
	 * @return A JSON representation of the selected fields for an external geo map
	 */
	@Override
	public String retrieve()
	{
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			// obtainUserIdentifier();

			String venue = obtainStringValue("venue", null);
			String period = obtainStringValue("period", null);
			Boolean names = obtainBooleanValue("names", false);

			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();

			if( names == true ) {

				jsonArray = new JSONArray(dao.getVenuesAndPeriods(null, venue, period));
				
			} else {

				Range range = new Range(0,500);
				List<ExternalGeo> list = dao.getUsingVenueAndPeriod(null, venue, period, range, "connections desc", true);

				for(ExternalGeo obj : list ) {
					JSONObject json = new JSONObject();
					// json.put("externalReference", obj.getExternalReference());
					json.put("connections", obj.getConnections());
					json.put("lat", obj.getLat());
					json.put("lon", obj.getLon());

					if(!StringUtils.hasText(venue)) json.put("venue", obj.getVenue());
					if(!StringUtils.hasText(period)) json.put("period", obj.getPeriod());

					jsonArray.put(json);
				}
			}

			// Returns the final value
			return jsonArray.toString();

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
