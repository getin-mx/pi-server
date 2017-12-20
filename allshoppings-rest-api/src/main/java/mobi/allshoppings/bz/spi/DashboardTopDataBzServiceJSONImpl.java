package mobi.allshoppings.bz.spi;


import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bz.DashboardTopDataBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class DashboardTopDataBzServiceJSONImpl
extends RestBaseServerResource
implements DashboardTopDataBzService {

	private static final Logger log = Logger.getLogger(DashboardTopDataBzServiceJSONImpl.class.getName());

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
			String groupBy = obtainStringValue("groupBy", null);
			int top = obtainIntegerValue("top", -1);
			Boolean desc = obtainBooleanValue("desc", false);

			List<DashboardIndicatorData> list = dao.getUsingFilters(entityId,
					entityKind, elementId, elementSubId, shoppingId,
					subentityId, periodType, fromStringDate, toStringDate,
					movieId, voucherType, dayOfWeek, timeZone, null, null, null, null);

			Map<String, Integer> resultMap = CollectionFactory.createMap();
			ValueComparator bvc = new ValueComparator(resultMap, desc);
			@SuppressWarnings("unchecked")
			TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);

			// Creates the result map
			for(DashboardIndicatorData obj : list) {
				Field field = obj.getClass().getDeclaredField(groupBy);
				String groupKey = (String)field.get(obj);

				Integer val = resultMap.get(groupKey);
				if( val == null ) val = new Integer(0);
				val += obj.getDoubleValue().intValue();
				resultMap.put(groupKey, val);
			}


			// Order the dataset
			sorted_map.putAll(resultMap);
			int myCount = 0;
			if( top != -1 && top > 0 ) {
				Map<String, Integer> temp = CollectionFactory.createMap();
				Iterator<String> x = sorted_map.keySet().iterator();
				while(x.hasNext() && myCount < top ) {
					String key = x.next();
					Integer val = resultMap.get(key);
					temp.put(key, val);
					myCount++;
				}

				resultMap.clear();
				resultMap.putAll(temp);
			}

			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();
			if( top == -1 ) top = myCount;
			
			Iterator<String> x = sorted_map.keySet().iterator();
			myCount = 0;
			while(x.hasNext() && myCount < top ) {
				String key = x.next();
				Integer val = resultMap.get(key);
				JSONArray element = new JSONArray();
				element.put(key);
				element.put(val);
				jsonArray.put(element);
				myCount++;
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

	@SuppressWarnings("rawtypes")
	class ValueComparator implements Comparator {
		Map<?,?> base;
		boolean desc;

		public ValueComparator(Map<?,?> base, boolean desc) {
			this.base = base;
			this.desc = desc;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(Object a, Object b) {
			if( desc ) {
				if ((Integer)base.get(a) <= (Integer)base.get(b)) {
					return -1;
				} else {
					return 1;
				} // returning 0 would merge keys
			} else {
				if ((Integer)base.get(a) >= (Integer)base.get(b)) {
					return -1;
				} else {
					return 1;
				} // returning 0 would merge keys
			}
		}
	}
}
