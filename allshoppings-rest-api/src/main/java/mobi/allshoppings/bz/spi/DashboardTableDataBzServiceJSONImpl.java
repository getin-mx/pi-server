package mobi.allshoppings.bz.spi;


import java.lang.reflect.Field;
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
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.DashboardTableDataBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.dao.DashboardIndicatorAliasDAO;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Cinema;
import mobi.allshoppings.model.DashboardIndicatorAlias;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class DashboardTableDataBzServiceJSONImpl
extends RestBaseServerResource
implements DashboardTableDataBzService {

	private static final Logger log = Logger.getLogger(DashboardTableDataBzServiceJSONImpl.class.getName());
	
	@Autowired
	private DashboardIndicatorDataDAO dao;
	@Autowired
	private DashboardIndicatorAliasDAO diAliasDao;
	@Autowired
	private CinemaDAO cinemaDao;

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
			byte entityKind = obtainByteValue("entityKind", (byte) -1);
			String elementId = obtainStringValue("elementId", null);
			String elementSubId = obtainStringValue("elementSubId", null);
			String shoppingId = obtainStringValue("shoppingId", null);
			String subentityId = obtainStringValue("subentityId", null);
			String periodType = obtainStringValue("periodId", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);
			String movieId = obtainStringValue("movieId", null);
			String voucherType = obtainStringValue("voucherType", null);
			byte dayOfWeek = obtainByteValue("dayOfWeek", (byte) -1);
			byte timeZone = obtainByteValue("timezone", (byte) -1);
			String subIdOrder = obtainStringValue("subIdOrder", null);
			String groupBy = obtainStringValue("groupBy", null);
			String groupName = obtainStringValue("groupName", null);
			String country = obtainStringValue("country", null);
			String province = obtainStringValue("province", null);
			String city = obtainStringValue("city", null);
			Boolean obtainGroupNameByCinema = obtainBooleanValue("obtainGroupNameByCinema", false);

			List<String> idList = CollectionFactory.createList();
			if(StringUtils.hasText(elementId))
				idList.addAll(Arrays.asList(elementId.split(",")));

			List<DashboardIndicatorData> list = dao.getUsingFilters(entityId,
					entityKind, idList, elementSubId, shoppingId,
					subentityId, periodType, fromStringDate, toStringDate,
					movieId, voucherType, dayOfWeek, timeZone, null, country, province, city);

			Map<String, Map<String, Integer>> resultMap = CollectionFactory.createMap();
			
			// Creates the order list and alias map
			List<String> orderList = CollectionFactory.createList();
			if(StringUtils.hasText(subIdOrder))
				orderList.addAll(Arrays.asList(subIdOrder.split(",")));

			Map<String, String> aliasMap = CollectionFactory.createMap();
			if(!CollectionUtils.isEmpty(orderList)) {
				for( String order : orderList ) {
					try {
						if( order.contains("|")) {
							String parts[] = order.split("\\|");
							DashboardIndicatorAlias alias = diAliasDao.getUsingFilters(entityId, entityKind, parts[0], parts[1]);
							aliasMap.put(order, alias.getElementId() + "|" + alias.getElementSubName());
						} else {
							DashboardIndicatorAlias alias = diAliasDao.getUsingFilters(entityId, entityKind, elementId.contains(",") ? null : elementId, order);
							aliasMap.put(order, alias.getElementSubName());
						}
					} catch( ASException e ) {
						log.log(Level.INFO, "Alias Not Found for subelementId " + order);
						aliasMap.put(order, order);
					}
				}
			}

			// Creates the result map
			Map<String, Integer> totalsMap = CollectionFactory.createMap();
			for(DashboardIndicatorData obj : list) {
				if( idList.size() == 0 || idList.contains(obj.getElementId()) ) {
					Field field = obj.getClass().getDeclaredField(groupBy);
					String groupKey = (String)field.get(obj);

					Map<String, Integer> values = resultMap.get(groupKey);
					if( values == null ) values = CollectionFactory.createMap();
					String orderKey = orderList.contains(obj.getElementSubId()) ? obj.getElementSubId() : obj.getElementId() + "|" + obj.getElementSubId();
					String key = aliasMap.get(orderKey);
					aliasMap.put(orderKey, key);
					Integer val = values.get(key);
					if( val == null ) val = new Integer(0);
					val += obj.getDoubleValue().intValue();
					values.put(key, val);
					resultMap.put(groupKey, values);

					Integer totalRow = totalsMap.get(key);
					if( totalRow == null ) totalRow = new Integer(0);
					totalRow += obj.getDoubleValue().intValue();
					totalsMap.put(key, totalRow);
				}
			}
			
			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();
			
			// Titles row
			JSONArray titles = new JSONArray();
			titles.put(groupName);
			for( String columnId : orderList ) {
				titles.put(aliasMap.get(columnId));
			}
			jsonArray.put(titles);
			
			// Values Array
			Iterator<String> i = resultMap.keySet().iterator();
			while(i.hasNext()) {
				String groupKey = i.next();
				JSONArray val = new JSONArray();
				Map<String, Integer> row = resultMap.get(groupKey);
				if(obtainGroupNameByCinema) {
					try {
						Cinema cinema = cinemaDao.get(groupKey,true);
						if( cinema.getStatus().equals(Cinema.STATUS_ENABLED)) {
							val.put(cinema.getName());
							for( String orderKey : orderList ) {
								String key = aliasMap.get(orderKey);
								Integer value = row.get(key) == null ? 0 : row.get(key);
								if( orderKey.endsWith("revenue")) {
									val.put("$" + value);
								} else {
									val.put(value);
								}
							}
							jsonArray.put(val);
						}
					} catch( ASException e ) {}
				} else {
					val.put(groupKey);
					for( String orderKey : orderList ) {
						String key = aliasMap.get(orderKey);
						Integer value = row.get(key) == null ? 0 : row.get(key);
						if( orderKey.endsWith("revenue")) {
							val.put("$" + value);
						} else {
							val.put(value);
						}
					}
					jsonArray.put(val);
				}
			}

			// Totals row
			JSONArray totals = new JSONArray();
			totals.put("Totales");
			for( String orderKey : orderList ) {
				String key = aliasMap.get(orderKey);
				Integer value = totalsMap.get(key) == null ? 0 : totalsMap.get(key);
				if( orderKey.endsWith("revenue")) {
					totals.put("$" + value);
				} else {
					totals.put(value);
				}
			}
			jsonArray.put(totals);
			
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
