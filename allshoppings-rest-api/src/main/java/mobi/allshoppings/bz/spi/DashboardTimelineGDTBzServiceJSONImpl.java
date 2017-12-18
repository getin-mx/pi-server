package mobi.allshoppings.bz.spi;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bz.DashboardTimelineGDTBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class DashboardTimelineGDTBzServiceJSONImpl
extends RestBaseServerResource
implements DashboardTimelineGDTBzService {

	private static final Logger log = Logger.getLogger(DashboardTimelineGDTBzServiceJSONImpl.class.getName());
	
	@Autowired
	private DashboardIndicatorDataDAO dao;

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
			Integer entityKind = obtainIntegerValue("entityKind", null);
			String elementId = obtainStringValue("elementId", null);
			String elementSubId = obtainStringValue("elementSubId", null);
			String shoppingId = obtainStringValue("shoppingId", null);
			String subentityId = obtainStringValue("subentityId", null);
			String periodType = obtainStringValue("periodId", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);

			List<DashboardIndicatorData> list = dao.getUsingFilters(entityId, entityKind, elementId,
					elementSubId, shoppingId, subentityId, periodType, fromStringDate, toStringDate,
					null, null, null, null, null, null, null, null);

			List<String> categories = Arrays.asList(new String[] {"Dom","Lun","Mar","Mie","Jue","Vie","Sab"});

			// Creates the order list and alias map
			Map<String, String> aliasMap = CollectionFactory.createMap();
			aliasMap.put("1", "Mañana");
			aliasMap.put("2", "Mediodia");
			aliasMap.put("3", "Tarde");
			aliasMap.put("4", "Noche");

			// Creates the result Map
			Map<String, Integer[]> resultMap = CollectionFactory.createMap();
			resultMap.put("1", new Integer[] {0,0,0,0,0,0,0});
			resultMap.put("2", new Integer[] {0,0,0,0,0,0,0});
			resultMap.put("3", new Integer[] {0,0,0,0,0,0,0});
			resultMap.put("4", new Integer[] {0,0,0,0,0,0,0});

			for(DashboardIndicatorData obj : list) {
				String key = String.valueOf(obj.getTimeZone());
				Integer[] vect = resultMap.get(key);
				vect[obj.getDayOfWeek()-1] += obj.getDoubleValue().intValue();
				resultMap.put(key, vect);
			}
			
			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();
			for( int i = 1; i <= 4; i++ ) {
				String key = String.valueOf(i);
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("name", aliasMap.get(key));
				jsonObj.put("type", "spline");
				jsonObj.put("data", resultMap.get(key));
				jsonArray.put(jsonObj);
			}
			
			// Returns the final value
			JSONObject ret = new JSONObject();
			ret.put("categories", categories);
			ret.put("series", jsonArray);
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
	
	public String getDateName(Date date) {
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int dof = cal.get(Calendar.DAY_OF_WEEK);
		
		switch(dof) {
		case Calendar.SUNDAY:
			sb.append("Dom ");
			break;
		case Calendar.MONDAY:
			sb.append("Lun ");
			break;
		case Calendar.TUESDAY:
			sb.append("Mar ");
			break;
		case Calendar.WEDNESDAY:
			sb.append("Mie ");
			break;
		case Calendar.THURSDAY:
			sb.append("Jue ");
			break;
		case Calendar.FRIDAY:
			sb.append("Vie ");
			break;
		case Calendar.SATURDAY:
			sb.append("Sab ");
			break;
		}

		sb.append(sdf.format(date));
		
		return sb.toString();
	}
}
