package mobi.allshoppings.bz.spi;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bz.DashboardRelationDataBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.DashboardIndicatorAliasDAO;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardIndicatorAlias;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class DashboardRelationDataBzServiceJSONImpl
extends RestBaseServerResource
implements DashboardRelationDataBzService {

	private static final Logger log = Logger.getLogger(DashboardRelationDataBzServiceJSONImpl.class.getName());
	private static final DecimalFormat df = new DecimalFormat("#.00");
	
	@Autowired
	private DashboardIndicatorDataDAO dao;
	@Autowired
	private DashboardIndicatorAliasDAO diAliasDao;

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

			String factor1 = obtainStringValue("factor1", null);
			String factor2 = obtainStringValue("factor2", null);
			String number = obtainStringValue("number", null);
			
			List<DashboardIndicatorData> list = dao.getUsingFilters(entityId, entityKind, elementId,
					elementSubId, shoppingId, subentityId, periodType, fromStringDate, toStringDate,
					movieId, voucherType, dayOfWeek, timeZone, null, null, null, null);

			// Creates the order list and alias map
			List<String> orderList = CollectionFactory.createList();
			orderList.add(factor1);
			orderList.add(factor2);

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

			// Creates the result map
			Map<String, Integer> resultMap = CollectionFactory.createMap();
			for(DashboardIndicatorData obj : list) {
				String key = obj.getElementSubName();
				String orderKey = obj.getElementSubId();
				if(orderList.contains(orderKey)) {
					aliasMap.put(orderKey, key);
					Integer value = resultMap.get(key);
					if( value == null )
						value = new Integer(0);
					value += obj.getDoubleValue().intValue();
					resultMap.put(key, value);
				}
			}

			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();
			for( String orderKey : orderList ) {
				String key = aliasMap.get(orderKey);
				jsonArray.put(key);
			}

			// Returns the final value
			JSONObject ret = new JSONObject();
			ret.put("titles", jsonArray);
			
			Double value = 0d;
			if( resultMap.get(aliasMap.get(factor1)).intValue() != 0 ) {
				double val1 = resultMap.get(aliasMap.get(factor1)).doubleValue();
				double val2 = resultMap.get(aliasMap.get(factor2)).doubleValue();
				if("true".equals(number)) {
					value = val1 / val2;
				} else {
					value = (val2 * 100) / val1;
				}
			}

			if("true".equals(number)) {
				ret.put("data", df.format(value));
			} else {
				ret.put("data", df.format(value) + "%");
			}
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
