package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bdb.bz.BDBHeatmapTableDataBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class HeatmapTableDataBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBHeatmapTableDataBzService {

	private static final Logger log = Logger.getLogger(HeatmapTableDataBzServiceJSONImpl.class.getName());
	
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
			User user = getUserFromToken();

			String entityId = obtainStringValue("entityId", null);
			Integer entityKind = obtainIntegerValue("entityKind", null);
			String elementId = obtainStringValue("elementId", null);
			String elementSubId = obtainStringValue("elementSubId", null);
			String shoppingId = obtainStringValue("shoppingId", null);
			String subentityId = obtainStringValue("subentityId", null);
			//String periodType = obtainStringValue("periodId", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);
			//String movieId = obtainStringValue("movieId", null);
			//String voucherType = obtainStringValue("voucherType", null);
			//Integer dayOfWeek = obtainIntegerValue("dayOfWeek", null);
			//Integer timezone = obtainIntegerValue("timezone", null);
			Integer top = obtainIntegerValue("top", null);
			
			List<DashboardIndicatorData> list = dao.getUsingFilters(Arrays.asList(entityId), entityKind,
					Arrays.asList(elementId), Arrays.asList(elementSubId), shoppingId,
					CollectionFactory.createList(subentityId.split(",")), null, fromStringDate, toStringDate,
					null, null, null, null, null, null, null, null);

			Map<String, Map<Integer, Integer>> dataSet = CollectionFactory.createMap();
			Map<String, Integer> toBeOrderedDataSet = CollectionFactory.createMap(); 
	        ValueComparator bvc = new ValueComparator(toBeOrderedDataSet);
	        @SuppressWarnings("unchecked")
			TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);

			Map<Integer, Integer> data = null;
			Integer value = null;
			Map<Integer, Integer> totals = CollectionFactory.createMap();
			totals.put(DashboardIndicatorData.TIME_ZONE_ALL, 0);
			totals.put(DashboardIndicatorData.TIME_ZONE_MORNING, 0);
			totals.put(DashboardIndicatorData.TIME_ZONE_NOON, 0);
			totals.put(DashboardIndicatorData.TIME_ZONE_AFTERNOON, 0);
			totals.put(DashboardIndicatorData.TIME_ZONE_NIGHT, 0);
			
			for( DashboardIndicatorData obj : list ) {
				if( isValidForUser(user, obj)) {
					String key = obj.getElementSubName();
					if(!obj.getTimeZone().equals(DashboardIndicatorData.TIME_ZONE_ALL) && StringUtils.hasText(key)) {
						// Data for this timezone
						data = dataSet.get(key);
						if( data == null ) data = CollectionFactory.createMap();
						value = data.get(obj.getTimeZone());
						if( value == null ) value = 0;
						value++;
						data.put(obj.getTimeZone(), value);

						totals.put(obj.getTimeZone(), totals.get(obj.getTimeZone()) + 1);

						// Data for total timezones
						value = data.get(DashboardIndicatorData.TIME_ZONE_ALL);
						if( value == null ) value = 0;
						value++;
						data.put(DashboardIndicatorData.TIME_ZONE_ALL, value);
						dataSet.put(key, data);

						totals.put(DashboardIndicatorData.TIME_ZONE_ALL, totals.get(DashboardIndicatorData.TIME_ZONE_ALL) + 1);

						toBeOrderedDataSet.put(key, value);
					}
				}
			}
			
			// Order the dataset
			sorted_map.putAll(toBeOrderedDataSet);
			if( top != null && top > 0 ) {
				Map<String, Map<Integer, Integer>> temp = CollectionFactory.createMap();
				totals.clear();
				Iterator<String> x = sorted_map.keySet().iterator();
				int myCount = 0;
				while(x.hasNext() && myCount < top ) {
					String key = x.next();
					Map<Integer, Integer> val = dataSet.get(key);
					for( int i = DashboardIndicatorData.TIME_ZONE_ALL; i <= DashboardIndicatorData.TIME_ZONE_NIGHT; i++) {
						totals.put(i, (totals.containsKey(i) ? totals.get(i) : 0) + (val.containsKey(i) ? val.get(i) : 0));
					}
					temp.put(key, val);
					myCount++;
				}
				
				dataSet.clear();
				dataSet.putAll(temp);
			}

			// y Categories
			List<String> yCategories = CollectionFactory.createList();
			yCategories.add("Todos");
			yCategories.add("Mañana");
			yCategories.add("Mediodia");
			yCategories.add("Tarde");
			yCategories.add("Noche");

			// x Categories
			List<String> xCategories = CollectionFactory.createList();
			Map<String, String> xCategoriesMap = CollectionFactory.createMap();
			{
				int counter = 0;
				Iterator<String> i = dataSet.keySet().iterator();
				while(i.hasNext()) {
					String dat = i.next();
					counter++;
					xCategories.add(String.valueOf(counter));
					xCategoriesMap.put(String.valueOf(counter), dat);
				}
			}
			
			JSONObject ret = new JSONObject();
			
			JSONArray xCategoriesJson = new JSONArray();
			JSONArray descriptionJson = new JSONArray();
			for( String cat : xCategories ) {
				xCategoriesJson.put("Area " + cat);
				JSONArray element = new JSONArray();
				element.put(cat);
				element.put(xCategoriesMap.get(cat));
				descriptionJson.put(element);
			}
			ret.put("xCategories", xCategoriesJson);
			ret.put("description", descriptionJson);
			
			JSONArray yCategoriesJson = new JSONArray();
			for( String cat : yCategories ) {
				yCategoriesJson.put(cat);
			}
			ret.put("yCategories", yCategoriesJson);

			int row = 0;
			JSONArray dataJson = new JSONArray();

			for( String cat : xCategories ) {
				String key = xCategoriesMap.get(cat);
				data = dataSet.get(key);
				if( data == null ) data = CollectionFactory.createMap();
				if( data.get(DashboardIndicatorData.TIME_ZONE_ALL) == null ) data.put(DashboardIndicatorData.TIME_ZONE_ALL, 0);
				if( data.get(DashboardIndicatorData.TIME_ZONE_MORNING) == null ) data.put(DashboardIndicatorData.TIME_ZONE_MORNING, 0);
				if( data.get(DashboardIndicatorData.TIME_ZONE_NOON) == null ) data.put(DashboardIndicatorData.TIME_ZONE_NOON, 0);
				if( data.get(DashboardIndicatorData.TIME_ZONE_AFTERNOON) == null ) data.put(DashboardIndicatorData.TIME_ZONE_AFTERNOON, 0);
				if( data.get(DashboardIndicatorData.TIME_ZONE_NIGHT) == null ) data.put(DashboardIndicatorData.TIME_ZONE_NIGHT, 0);
				
				JSONArray element = new JSONArray();
				for( int i = DashboardIndicatorData.TIME_ZONE_ALL; i <= DashboardIndicatorData.TIME_ZONE_NIGHT; i++ ) {
					element = new JSONArray();
					element.put(row);
					element.put(i);
					int tot = totals.containsKey(i) ? totals.get(i) : 0;
					if( tot > 0 )
						element.put((int)(data.get(i) * 100 / totals.get(i)));
					else
						element.put((int)0);
					element.put((int)(data.get(i)));
					dataJson.put(element);
				}
				row++;
			}
			ret.put("data", dataJson);

			if( top != null && top > 0 ) {
				JSONArray orderedJson = new JSONArray();
				Iterator<String> x = sorted_map.keySet().iterator();
				int myCount = 0;
				while(x.hasNext() && myCount < top ) {
					String key = x.next();
					Map<Integer, Integer> val = dataSet.get(key);
					int count = val.get(DashboardIndicatorData.TIME_ZONE_ALL);
					int total = totals.get(DashboardIndicatorData.TIME_ZONE_ALL);
					int percent = total > 0 ? (count * 100 / total) : 0;
					JSONArray element = new JSONArray();
					element.put(key);
					element.put(count);
					element.put(percent + "%");
					orderedJson.put(element);
					myCount++;
				}
				ret.put("ordered", orderedJson);
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

	@SuppressWarnings("rawtypes")
	class ValueComparator implements Comparator {
	    Map<?,?> base;

	    public ValueComparator(Map<?,?> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with
	    // equals.
	    public int compare(Object a, Object b) {
	        if ((Integer)base.get(a) >= (Integer)base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
}
