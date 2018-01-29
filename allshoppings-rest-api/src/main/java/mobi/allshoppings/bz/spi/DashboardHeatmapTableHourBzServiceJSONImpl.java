package mobi.allshoppings.bz.spi;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.DashboardHeatmapTableHourBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.DashboardConfigurationDAO;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardConfiguration;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.CollectionFactory;

/**
 *
 */
public class DashboardHeatmapTableHourBzServiceJSONImpl extends RestBaseServerResource
		implements DashboardHeatmapTableHourBzService {

	private static final Logger log = Logger.getLogger(DashboardHeatmapTableHourBzServiceJSONImpl.class.getName());

	@Autowired
	private DashboardIndicatorDataDAO dao;
	/*@Autowired
	private DashboardConfigurationDAO dcDao;*/

	/**
	 * Obtains information about a user
	 * 
	 * @return A JSON representation of the selected fields for a user
	 */
	@Override
	public String retrieve() {
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
			String periodType = obtainStringValue("periodId", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);
			String movieId = obtainStringValue("movieId", null);
			String voucherType = obtainStringValue("voucherType", null);
			Integer dayOfWeek = obtainIntegerValue("dayOfWeek", null);
			Integer timezone = obtainIntegerValue("timezone", null);
			Boolean average = obtainBooleanValue("average", false);
			Boolean toMinutes = obtainBooleanValue("toMinutes", false);
			Boolean eraseBlanks = obtainBooleanValue("eraseBlanks", false);

			List<String> lElementId = StringUtils.hasText(elementId) ? Arrays.asList(elementId.split(",")) : null;
			List<String> lElementSubId = StringUtils.hasText(elementSubId) ? Arrays.asList(elementSubId.split(","))
					: null;

			List<DashboardIndicatorData> list = dao.getUsingFilters(entityId, entityKind, lElementId, lElementSubId,
					shoppingId, subentityId, periodType, fromStringDate, toStringDate, movieId, voucherType, dayOfWeek,
					timezone, null, null, null, null);

			/*/ Gets dashboard configuration for this session
			DashboardConfiguration config = new DashboardConfiguration(entityId, entityKind);
			try {
				config = dcDao.getUsingEntityIdAndEntityKind(entityId, entityKind, true);
			} catch (Exception e) {
			}*/

			// Data
			Map<String, Map<Integer, Map<Integer, Long>>> yData = CollectionFactory.createMap();
			Map<String, Map<Integer, Map<Integer, Long>>> yCounter = CollectionFactory.createMap();

			// y Categories
			List<String> yCategories = CollectionFactory.createList();
			for (int i = 0; i < 24; i++) {
				yCategories.add(i + ":00");
			}

			// Creates a collection with elementSubIds returned from the
			// persisted objects
			if (CollectionUtils.isEmpty(lElementSubId)) {
				lElementSubId = CollectionFactory.createList();
				for (DashboardIndicatorData obj : list) {
					if (isValidForUser(user, obj)) {
						if (!lElementSubId.contains(obj.getElementSubId()))
							lElementSubId.add(obj.getElementSubId());
					}
				}
			}
			if (CollectionUtils.isEmpty(lElementSubId)) {
				lElementSubId = Arrays.asList("default");
			}

			// Creates the initial data
			for (String ele : lElementSubId) {
				Map<Integer, Map<Integer, Long>> ySubData = CollectionFactory.createMap();
				Map<Integer, Map<Integer, Long>> ySubCounter = CollectionFactory.createMap();

				for (int i = 0; i < 24; i++) {
					ySubData.put(i, new HashMap<Integer, Long>());
					ySubCounter.put(i, new HashMap<Integer, Long>());
				}

				yData.put(ele, ySubData);
				yCounter.put(ele, ySubCounter);
			}

			// x Categories
			List<String> xCategories = CollectionFactory.createList();
			xCategories.add("Lunes");
			xCategories.add("Martes");
			xCategories.add("Miercoles");
			xCategories.add("Jueves");
			xCategories.add("Viernes");
			xCategories.add("Sabado");
			xCategories.add("Domingo");

			// Sets data
			for (DashboardIndicatorData obj : list) {
				if (isValidForUser(user, obj)) {
					// Position calc according to the timezone
					int position = obj.getTimeZone();
					/*if (config.getTimezone().equals("-06:00")) {
						position = position - 1;
						if (position >= 24)
							position = position - 24;
					}*/

					Map<Integer, Long> xData = yData.get(obj.getElementSubId()).get(position);

					// Sets the double value
					try {
						obj.setDayOfWeek(mapDayOfWeek(obj.getDayOfWeek()));
						Long val = xData.get(obj.getDayOfWeek());
						if (val == null)
							val = 0L;
						val += obj.getDoubleValue().intValue();
						xData.put(obj.getDayOfWeek(), val);

						// Sets the record count
						xData = yCounter.get(obj.getElementSubId()).get(position);
						val = xData.get(obj.getDayOfWeek());
						if (val == null)
							val = 0L;
						if (obj.getRecordCount() != null)
							val += obj.getRecordCount();
						else
							log.log(Level.WARNING, "Inconsistent DashboardIndicator: " + obj.toString());
						xData.put(obj.getDayOfWeek(), val);
					} catch (Exception e) {
						log.log(Level.WARNING, e.getMessage());
						log.log(Level.WARNING, "Inconsistent DashboardIndicator: " + obj.toString());
					}
				}
			}

			// Checks Average
			if (average) {
				Iterator<String> ix = lElementSubId.iterator();
				while (ix.hasNext()) {
					String ele = ix.next();
					Iterator<Integer> i1 = yData.get(ele).keySet().iterator();
					while (i1.hasNext()) {
						Integer key1 = i1.next();
						Map<Integer, Long> xData = yData.get(ele).get(key1);
						Map<Integer, Long> xCounter = yCounter.get(ele).get(key1);
						Iterator<Integer> i2 = xData.keySet().iterator();
						while (i2.hasNext()) {
							Integer key2 = i2.next();
							Long val = xData.get(key2);
							if (val != null) {
								Long count = xCounter.get(key2);
								if (count != null && count != 0) {
									if (toMinutes) {
										val = val / count / 60000;
									} else {
										val = val / count;
									}
									xData.put(key2, val);
								}
							}
						}
					}
				}
			}

			int maxIdx = 0;
			int idx = 0;
			// Checks Erase Blanks
			if (eraseBlanks) {

				int maxVal = 0;
				Iterator<String> ix = lElementSubId.iterator();
				while (ix.hasNext()) {

					String ele = ix.next();
					List<String> newYCategories = CollectionFactory.createList();
					for (int i = 0; i < 24; i++) {
						Map<Integer, Long> xData = yData.get(ele).get(i);
						if (xData.size() > 0)
							newYCategories.add(i + ":00");
						else
							yData.get(ele).remove(i);
					}
					if (newYCategories.size() >= maxVal) {
						maxVal = newYCategories.size();
						yCategories = newYCategories;
						maxIdx = idx;
					}
					idx++;
				}
			}

			// Creates the yPositions map
			Map<Integer, Integer> yPositions = CollectionFactory.createMap();
			Set<Integer> keySet = yData.get(lElementSubId.get(maxIdx)).keySet();
			List<Integer> keyList = CollectionFactory.createList();
			keyList.addAll(keySet);
			Collections.sort(keyList);
			Iterator<Integer> i = keyList.iterator();
			int count = 0;
			while (i.hasNext()) {
				Integer key = i.next();
				yPositions.put(key, count);
				count++;
			}

			// Writes the results
			JSONObject ret = new JSONObject();

			JSONArray xCategoriesJson = new JSONArray();
			for (String cat : xCategories) {
				xCategoriesJson.put(cat);
			}
			ret.put("xCategories", xCategoriesJson);

			JSONArray yCategoriesJson = new JSONArray();
			for (String cat : yCategories) {
				yCategoriesJson.put(cat);
			}
			ret.put("yCategories", yCategoriesJson);

			JSONArray dataJson = new JSONArray();
			JSONArray element = new JSONArray();
			for (int y = 0; y < 24; y++) {

				// Try to find any data for the time slot
				boolean hasAnyY = false;
				for (int j = 0; j < lElementSubId.size(); j++) {
					if (yData.get(lElementSubId.get(j)).get(y) != null) {
						for (int x = 0; x <= 7; x++) {
							Map<Integer, Long> xTmpData = yData.get(lElementSubId.get(j)).get(y);
							if (xTmpData != null) {
								Long tmpVal = xTmpData.get(x);
								if (tmpVal != null && tmpVal != 0) {
									hasAnyY = true;
								}
							}
						}
					}
				}

				// If has any element fills the space
				if (hasAnyY) {
					for (int x = 0; x <= 7; x++) {
						element = new JSONArray();
						element.put(x);
						element.put(yPositions.get(y));

						boolean hasAnyX = false;
						for (int j = 0; j < lElementSubId.size(); j++) {
							Map<Integer, Long> xTmpData = yData.get(lElementSubId.get(j)).get(y);
							if (xTmpData != null) {
								Long tmpVal = xTmpData.get(x);
								if (tmpVal != null) {
									element.put(tmpVal);
									hasAnyX = true;
								} else {
									element.put(0);
								}
							} else {
								element.put(0);
							}
						}

						// If has any element for the day, adds it
						if (hasAnyX)
							dataJson.put(element);

					}
				}
			}
			ret.put("data", dataJson);

			return ret.toString();

		} catch (ASException e) {
			if (e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE
					|| e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE) {
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

	private int mapDayOfWeek(Integer dayOfWeek) {
		int result = 0;
		if (dayOfWeek != 1) {
			result = dayOfWeek - 2;
		} else {
			result = 6;
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	class ValueComparator implements Comparator {
		Map<?, ?> base;

		public ValueComparator(Map<?, ?> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(Object a, Object b) {
			if ((Integer) base.get(a) >= (Integer) base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}
}
