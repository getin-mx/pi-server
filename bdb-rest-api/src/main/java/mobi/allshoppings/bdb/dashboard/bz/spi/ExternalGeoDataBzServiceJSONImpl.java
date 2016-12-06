package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.ExternalGeoDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.ExternalGeo;
import mobi.allshoppings.tools.Range;


/**
 *
 */
public class ExternalGeoDataBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(ExternalGeoDataBzServiceJSONImpl.class.getName());

	@Autowired
	private ExternalGeoDAO dao;
	
	/**
	 * Obtains a list of FloorMap points
	 * 
	 * @return A JSON representation of the selected fields for a FloorMap
	 */
	@Override
	public String retrieve()
	{
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			obtainUserIdentifier(true);

			String entityId = obtainStringValue("entityId", null);
			Integer entityKind = obtainIntegerValue("entityKind", null);
			String period = obtainStringValue("period", null);
			Integer type = obtainIntegerValue("type", null);

			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();

			Range range = new Range(0,500);
			String order = "connections desc";
			if( !StringUtils.hasText(period) || period.equalsIgnoreCase("last")) {
				order = "period desc," + order;
				period = null;
			}
			
			List<ExternalGeo> list = null;
			List<Integer> typeList = type == null ? Arrays.asList(new Integer[] { ExternalGeo.TYPE_GPS,
					ExternalGeo.TYPE_GPS_HOME, ExternalGeo.TYPE_GPS_WORK, ExternalGeo.TYPE_WIFI })
					: Arrays.asList(new Integer[] { type });			
			
			for( Integer myType : typeList ) {
				list = dao.getUsingEntityIdAndPeriod(null, entityId, entityKind, myType, period, range, "connections desc", true);

				for(ExternalGeo obj : list ) {
					JSONObject json = new JSONObject();
					// json.put("externalReference", obj.getExternalReference());
					json.put("connections", obj.getConnections());
					json.put("lat", obj.getLat());
					json.put("lon", obj.getLon());
					json.put("type", obj.getType());

					jsonArray.put(json);
				}
			}

			// Returns the final value
			JSONObject ret = new JSONObject();
			ret.put("recordCount", jsonArray.length());
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
