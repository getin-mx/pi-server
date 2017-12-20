package mobi.allshoppings.bz.spi;


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.DashboardFunnelDataBzService;
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
public class DashboardFunnelDataBzServiceJSONImpl
extends RestBaseServerResource
implements DashboardFunnelDataBzService {

	private static final Logger log = Logger.getLogger(DashboardFunnelDataBzServiceJSONImpl.class.getName());
	
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
			String subIdOrder = obtainStringValue("subIdOrder", null);
			String country = obtainStringValue("country", null);
			String province = obtainStringValue("province", null);
			String city = obtainStringValue("city", null);

			List<DashboardIndicatorData> list = dao.getUsingFilters(entityId,
					entityKind, elementId, elementSubId, shoppingId,
					subentityId, periodType, fromStringDate, toStringDate,
					movieId, voucherType, dayOfWeek, timeZone, null, country, province, city);

			// Creates the order list and alias map
			List<String> orderList = CollectionFactory.createList();
			if(StringUtils.hasText(subIdOrder))
				orderList.addAll(Arrays.asList(subIdOrder.split(",")));
			
			Map<String, String> aliasMap = CollectionFactory.createMap();
			if(!CollectionUtils.isEmpty(orderList)) {
				for( String order : orderList ) {
					try {
						DashboardIndicatorAlias alias = diAliasDao.getUsingFilters(entityId, entityKind,
								elementId, order);
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
				if(!StringUtils.hasText(subIdOrder) || orderList.contains(orderKey)) {
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
			if(!StringUtils.hasText(subIdOrder)) {
				Iterator<String> i = resultMap.keySet().iterator();
				while(i.hasNext()) {
					String key = i.next();
					JSONArray arr = new JSONArray();
					arr.put(key);
					arr.put(resultMap.get(key) == null ? 0 : resultMap.get(key));
					jsonArray.put(arr);
				}
			} else {
				for( String orderKey : orderList ) {
					String key = aliasMap.get(orderKey);
					JSONArray arr = new JSONArray();
					arr.put(key);
					arr.put(resultMap.get(key) == null ? 0 : resultMap.get(key));
					jsonArray.put(arr);
				}
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
