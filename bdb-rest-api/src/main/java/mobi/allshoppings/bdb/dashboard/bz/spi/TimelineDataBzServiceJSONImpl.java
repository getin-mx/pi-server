package mobi.allshoppings.bdb.dashboard.bz.spi;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.bdb.bz.BDBTimelineDataBzService;
import mobi.allshoppings.dao.DashboardIndicatorAliasDAO;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardIndicatorAlias;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class TimelineDataBzServiceJSONImpl extends BDBRestBaseServerResource implements BDBTimelineDataBzService {

	private static final Logger log = Logger.getLogger(TimelineDataBzServiceJSONImpl.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private DashboardIndicatorDataDAO dao;
	@Autowired
	private StoreDAO daoSRt;

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
			User user = getUserFromToken();

			String entityId = obtainStringValue("entityId", null);
			Integer entityKind = obtainIntegerValue("entityKind", null);
			String elementId = obtainStringValue("elementId", null);
			String elementSubId = obtainStringValue("elementSubId", null);
			String subentityId = obtainStringValue("subentityId", null);
			String periodType = obtainStringValue("periodType", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);
			Integer dayOfWeek = obtainIntegerValue("dayOfWeek", null);
			Integer timezone = obtainIntegerValue("timezone", null);
			String subIdOrder = obtainStringValue("subIdOrder", null);
			Boolean eraseBlanks = obtainBooleanValue("eraseBlanks", false);
			String region = obtainStringValue("region", null);
			String format = obtainStringValue("storeFormat", null);
			String district = obtainStringValue("disctrict", null);
			String orderx = obtainStringValue("order", null);
			List<DashboardIndicatorData> list = CollectionFactory.createList();
			List<String> subname = CollectionFactory.createList();
			boolean notEmptySubentity = StringUtils.hasText(subentityId);
			boolean singleData = notEmptySubentity ||
					(!StringUtils.hasText(region) && !StringUtils.hasText(format) && !StringUtils.hasText(district));
			if(singleData) {
				if(notEmptySubentity) subname.add(subentityId);
			} else {
				for(Store i : daoSRt.getUsingRegionAndFormatAndDistrict(entityId, null, null,
						StatusHelper.statusActive(), region, format, district, orderx)) {
					subname.add(i.getIdentifier()); 
				}
			}
			list = dao.getUsingFilters(singleData ? Arrays.asList(entityId) : null, entityKind, Arrays.asList(elementId),
					notEmptySubentity ? Arrays.asList(elementSubId) : null, null, subname, null,
							fromStringDate, toStringDate, null, null, dayOfWeek, timezone, null, null, null, null);
			
			log.log(Level.INFO, list.size() + " dashboard elements found");

			Date fromDate = DateUtils.truncate(sdf.parse(fromStringDate), Calendar.DATE);
			Date toDate = DateUtils.truncate(sdf.parse(toStringDate), Calendar.DATE);
			Date thisDate = new Date(fromDate.getTime());
			
			List<String> categories = CollectionFactory.createList();

			// Creates the order list and alias map
			List<String> orderList = CollectionFactory.createList();
			if(StringUtils.hasText(subIdOrder))
				orderList.addAll(Arrays.asList(subIdOrder.split(",")));

			// TODO USE MAPPER MAP
			Map<String, String> aliasMap = CollectionFactory.createMap();
			if(!CollectionUtils.isEmpty(orderList)) {
				for( String order : orderList ) {
					try {
						DashboardIndicatorAlias alias = diAliasDao.getUsingFilters(entityId, entityKind, elementId, order);
						aliasMap.put(order, alias.getElementSubName());
					} catch( ASException e ) {
						log.log(Level.INFO, "Alias Not Found for subelementId " + order);
					}
				}
			}

			// Creates the Date Map
			Map<Date, Integer> dateMap = CollectionFactory.createMap();
			int dateMapPosition = 0;
			while( thisDate.before(toDate) || thisDate.equals(toDate)) {
				if( !dateMap.containsKey(calculateDateFrame(thisDate, periodType))) {
					dateMap.put(calculateDateFrame(thisDate, periodType), dateMapPosition);
					categories.add(getDateName(thisDate, periodType));
					dateMapPosition++;
				}
				thisDate = DateUtils.addDays(thisDate, 1);
			}
			
			// Creates the result map
			Map<String, Double[]> resultMap = CollectionFactory.createMap();
			if(!CollectionUtils.isEmpty(orderList)) {
				for( String order : orderList ) {
					String key = aliasMap.get(order);
					Double[] valArray = resultMap.get(key);
					if( valArray == null ) {
						valArray = new Double[dateMap.keySet().size()];
						for( int i = 0; i < dateMap.keySet().size(); i++ ) {
							valArray[i] = new Double(0.0);
						}
					}
					resultMap.put(key, valArray);
				}
			}

			for(DashboardIndicatorData obj : list) {
				if( isValidForUser(user, obj)) {
					Date objDate = calculateDateFrame(DateUtils.truncate(obj.getDate(), Calendar.DATE), periodType);
					if (!(objDate.compareTo(fromDate) >= 0 && objDate.compareTo(toDate) <= 0)) continue;
					String key = obj.getElementSubName();
					String orderKey = obj.getElementSubId();
					if(!StringUtils.hasText(subIdOrder) || orderList.contains(orderKey)) {
						aliasMap.put(orderKey, key);
						Double[] valArray = resultMap.get(key);
						if( valArray == null ) {
							valArray = new Double[dateMap.keySet().size()];
							for( int i = 0; i < dateMap.keySet().size(); i++ ) {
								valArray[i] = new Double(0.0);
							}
						}
						int position = dateMap.get(objDate);
						valArray[position] += obj.getDoubleValue();
						resultMap.put(key, valArray);
					}
				}
			}

			// Checks if it has to erase blank spaces
 			if( eraseBlanks) {
				Double[] eraseMap = new Double[dateMap.keySet().size()];
				for( int i = 0; i < eraseMap.length; i++ )
					eraseMap[i] = 0.0;

				Iterator<String> it = resultMap.keySet().iterator();
				while(it.hasNext()) {
					String key = it.next();
					Double[] valArray = resultMap.get(key);
					for( int x = 0; x < valArray.length; x++ )
						if( valArray[x] > 0 ) eraseMap[x]++;
				}
				
				int newCount = 0;
				for( int i = 0; i < eraseMap.length; i++ )
					if( eraseMap[i] > 0 ) newCount++;

				it = resultMap.keySet().iterator();
				while(it.hasNext()) {
					String key = it.next();
					Double[] valArray = resultMap.get(key);
					Double[] newArray = new Double[newCount];
					int x = 0;
					for( int i = 0; i < valArray.length; i++ ) {
						if(eraseMap[i] > 0 ) {
							newArray[x] = valArray[i];
							x++;
						}
					}
					resultMap.put(key, newArray);
				}

				List<String> newCategories = CollectionFactory.createList();
				int i = 0;
				for( String item : categories ) {
					if( eraseMap[i] > 0 )
						newCategories.add(item);
					i++;
				}
				categories = newCategories;
				
			}

			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();
			if(!StringUtils.hasText(subIdOrder)) {
				Iterator<String> i = resultMap.keySet().iterator();
				while(i.hasNext()) {
					String key = i.next();
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("name", key);
					jsonObj.put("type", "spline");
					jsonObj.put("data", resultMap.get(key) == null ? 0 : resultMap.get(key));
					jsonArray.put(jsonObj);
				}
			} else {
				for( String orderKey : orderList ) {
					if(!orderKey.equals("visitor_total_revenue")){
						String key = aliasMap.get(orderKey);
						JSONObject jsonObj = new JSONObject();
						jsonObj.put("name", key);
						jsonObj.put("type", "spline");
						jsonObj.put("data", resultMap.get(key) == null ? 0 : resultMap.get(key));
						jsonArray.put(jsonObj);
					}else{
						String key = aliasMap.get(orderKey);
						JSONObject jsonObj = new JSONObject();
						jsonObj.put("name", key);
						jsonObj.put("type", "column");
						jsonObj.put("yAxis", 1);
						jsonObj.put("data", resultMap.get(key) == null ? 0 : resultMap.get(key));
						jsonArray.put(jsonObj);
					}
					
				}
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

	public String getDateName(Date date, String periodType) {
		StringBuffer sb = new StringBuffer();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if( periodType == null ) periodType = "";

		if( periodType.equals("W")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			int curDay = cal.get(Calendar.DAY_OF_YEAR);
			cal.set(Calendar.DAY_OF_YEAR, 1);
			while( cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				cal.add(Calendar.DATE, 1);
			}
			int firstDay = cal.get(Calendar.DAY_OF_YEAR);
			int offset = curDay - firstDay;
			int week = (offset / 7) + 1;
			
			sb.append("Sem ").append(week).append(" ").append(sdf.format(date));
			
		} else if( periodType.equals("M")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			int month = cal.get(Calendar.MONTH);
			switch(month) {
			case Calendar.JANUARY:
				sb.append("Enero ");
				break;
			case Calendar.FEBRUARY:
				sb.append("Febrero ");
				break;
			case Calendar.MARCH:
				sb.append("Marzo ");
				break;
			case Calendar.APRIL:
				sb.append("Abril ");
				break;
			case Calendar.MAY:
				sb.append("Mayo ");
				break;
			case Calendar.JUNE:
				sb.append("Junio ");
				break;
			case Calendar.JULY:
				sb.append("Julio ");
				break;
			case Calendar.AUGUST:
				sb.append("Agosto ");
				break;
			case Calendar.SEPTEMBER:
				sb.append("Septiembre ");
				break;
			case Calendar.OCTOBER:
				sb.append("Octubre ");
				break;
			case Calendar.NOVEMBER:
				sb.append("Noviembre ");
				break;
			case Calendar.DECEMBER:
				sb.append("Diciembre ");
				break;
			}
				
			sb.append(sdf.format(date));
			
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
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
		}
		
		return sb.toString();
	}
	
	public Date calculateDateFrame(Date forDate, String periodType) {
		Calendar cal = Calendar.getInstance();
		if( periodType == null ) periodType = "";
		if( periodType.equals("W")) {
			cal.setTime(forDate);
			while( cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				cal.add(Calendar.DATE, -1);
			}
			return cal.getTime();
		} else if( periodType.equals("M")) {
			cal.setTime(forDate);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			return cal.getTime();
		} else {
			return forDate;
		}
	}
}