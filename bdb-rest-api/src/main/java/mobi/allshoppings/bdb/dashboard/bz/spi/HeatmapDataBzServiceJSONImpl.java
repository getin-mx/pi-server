package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.dao.FloorMapDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.FloorMap;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class HeatmapDataBzServiceJSONImpl extends BDBRestBaseServerResource implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(HeatmapDataBzServiceJSONImpl.class.getName());

	@Autowired
	private DashboardIndicatorDataDAO dao;
	@Autowired
	private FloorMapDAO floorMapDao;

	/**
	 * Obtains a Dashboard report prepared to form a shopping center heatmap
	 * 
	 * @return A JSON representation of the selected graph
	 */
	@Override
	public String retrieve()
	{
		long start = markStart();
		try {
			// obtain the id and validates the auth token
//			obtainUserIdentifier(true);

			String subentityId = obtainStringValue("floormapId", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);
			//Integer dayOfWeek = obtainIntegerValue("dayOfWeek", null);
			//Integer timeZone = obtainIntegerValue("timezone", null);

			if(!StringUtils.hasText(subentityId)) throw ASExceptionHelper.invalidArgumentsException("subentityId");
			
			FloorMap floorMap = floorMapDao.get(subentityId, true);
			String shoppingId = floorMap.getShoppingId();
			
			List<DashboardIndicatorData> list = dao.getUsingFilters(Arrays.asList(shoppingId), (byte) 0,
					Arrays.asList("heatmap_data"), null, null, Arrays.asList(subentityId), null,
					fromStringDate, toStringDate, null, null, (byte) -1, (byte) -1, null, null, null, null);

			// Creates the total value indicator
			double totalValue = 0D;

			// Creates the result map
			Map<String, Long> resultMap = CollectionFactory.createMap();
			for(DashboardIndicatorData obj : list) {
				String key = obj.getElementSubName();
				Long value = resultMap.get(key);
				if( value == null )
					value = new Long(0);
				value += obj.getDoubleValue().longValue();
				totalValue += obj.getDoubleValue().longValue();
				resultMap.put(key, value);
			}

			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();
			Iterator<String> i = resultMap.keySet().iterator();
			while(i.hasNext()) {
				String key = i.next();
				JSONArray arr = new JSONArray();
				arr.put(key);
				arr.put(resultMap.get(key) == null ? 0 : resultMap.get(key) * 100 / totalValue);
				jsonArray.put(arr);
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
