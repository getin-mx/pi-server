package mobi.allshoppings.bz.spi;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.DashboardTimelineDataByCinemaBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class DashboardTimelineDataByCinemaBzServiceJSONImpl
extends RestBaseServerResource
implements DashboardTimelineDataByCinemaBzService {

	private static final Logger log = Logger.getLogger(DashboardTimelineDataByCinemaBzServiceJSONImpl.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
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
			String movieId = obtainStringValue("movieId", null);
			String voucherType = obtainStringValue("voucherType", null);
			Integer dayOfWeek = obtainIntegerValue("dayOfWeek", null);
			Integer timezone = obtainIntegerValue("timezone", null);
			String country = obtainStringValue("country", null);
			String province = obtainStringValue("province", null);
			String city = obtainStringValue("city", null);

			// Creates the order list and alias map
			List<String> idList = CollectionFactory.createList();
			if(StringUtils.hasText(elementId))
				idList.addAll(Arrays.asList(elementId.split(",")));

			List<DashboardIndicatorData> list = dao.getUsingFilters(entityId,
					entityKind, idList, elementSubId, shoppingId,
					subentityId, periodType, fromStringDate, toStringDate,
					movieId, voucherType, dayOfWeek, timezone, null, country, province, city);

			Date fromDate = DateUtils.truncate(sdf.parse(fromStringDate), Calendar.DATE);
			Date toDate = DateUtils.truncate(sdf.parse(toStringDate), Calendar.DATE);
			Date thisDate = new Date(fromDate.getTime());
			
			List<String> categories = CollectionFactory.createList();

			// Creates the Date Map
			Map<Date, Integer> dateMap = CollectionFactory.createMap();
			int dateMapPosition = 0;
			while( thisDate.before(toDate) || thisDate.equals(toDate)) {
				dateMap.put(thisDate, dateMapPosition);
				categories.add(getDateName(thisDate));
				dateMapPosition++;
				thisDate = DateUtils.addDays(thisDate, 1);
			}
			
			// Creates the result map
			Map<String, Integer[]> resultMap = CollectionFactory.createMap();
			for( DashboardIndicatorData obj : list ) {
				if( idList.size() == 0 || idList.contains(obj.getElementId()) ) {
					String key = obj.getSubentityName();
					Integer[] valArray = resultMap.get(key);
					if( valArray == null ) {
						valArray = new Integer[dateMap.keySet().size()];
						for( int i = 0; i < dateMap.keySet().size(); i++ ) {
							valArray[i] = new Integer(0);
						}
					}
					resultMap.put(key, valArray);
				}
			}

			for(DashboardIndicatorData obj : list) {
				if( idList.size() == 0 || idList.contains(obj.getElementId()) ) {
					Date objDate = DateUtils.truncate(obj.getDate(), Calendar.DATE);
					String key = obj.getSubentityName();
					Integer[] valArray = resultMap.get(key);
					if( valArray == null ) {
						valArray = new Integer[dateMap.keySet().size()];
						for( int i = 0; i < dateMap.keySet().size(); i++ ) {
							valArray[i] = new Integer(0);
						}
					}
					int position = dateMap.get(objDate);
					valArray[position] += obj.getDoubleValue().intValue();
					resultMap.put(key, valArray);
				}
			}
			
			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();
			Iterator<String> i = resultMap.keySet().iterator();
			while(i.hasNext()) {
				String key = i.next();
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("name", key);
				jsonObj.put("type", "spline");
				jsonObj.put("data", resultMap.get(key) == null ? 0 : resultMap.get(key));
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
