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

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.DashboardTimelineHourBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.DashboardConfigurationDAO;
import mobi.allshoppings.dao.DashboardIndicatorAliasDAO;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardConfiguration;
import mobi.allshoppings.model.DashboardIndicatorAlias;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.CollectionFactory;

/**
 *
 */
public class DashboardTimelineHourBzServiceJSONImpl extends RestBaseServerResource
		implements DashboardTimelineHourBzService {

	private static final Logger log = Logger.getLogger(DashboardTimelineHourBzServiceJSONImpl.class.getName());

	@Autowired
	private DashboardIndicatorDataDAO dao;
	@Autowired
	private DashboardIndicatorAliasDAO diAliasDao;
	@Autowired
	private DashboardConfigurationDAO dcDao;

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
			byte entityKind = obtainByteValue("entityKind", (byte) -1);
			String elementId = obtainStringValue("elementId", null);
			String elementSubId = obtainStringValue("elementSubId", null);
			String shoppingId = obtainStringValue("shoppingId", null);
			String subentityId = obtainStringValue("subentityId", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);
			String subIdOrder = obtainStringValue("subIdOrder", null);
			Boolean average = obtainBooleanValue("average", false);
			Boolean toMinutes = obtainBooleanValue("toMinutes", false);
			Boolean eraseBlanks = obtainBooleanValue("eraseBlanks", false);

			// Gets dashboard configuration for this session
			DashboardConfiguration config = new DashboardConfiguration(entityId, entityKind);
			try {
				config = dcDao.getUsingEntityIdAndEntityKind(entityId, entityKind, true);
			} catch (Exception e) {}

			List<String> categories = CollectionFactory.createList();

			// Creates the order list and alias map
			List<String> orderList = CollectionFactory.createList();
			if (StringUtils.hasText(subIdOrder)) orderList.addAll(Arrays.asList(subIdOrder.split(",")));

			Map<String, String> aliasMap = CollectionFactory.createMap();
			for (String order : orderList) {
				try {
					DashboardIndicatorAlias alias = diAliasDao.getUsingFilters(entityId, entityKind, elementId,
							order);
					aliasMap.put(order, alias.getElementSubName());
				} catch (ASException e) {
					log.log(Level.INFO, "Alias Not Found for subelementId " + order);
				}
			}

			// Creates the Hour Map
			Map<Integer, Integer> hourMap = CollectionFactory.createMap();
			for (int hourMapPosition = 0; hourMapPosition < 24; hourMapPosition++) {
				hourMap.put(hourMapPosition, hourMapPosition);
				categories.add(getHourName(hourMapPosition));
			}

			// Creates the result map
			Map<String, long[]> resultMap = CollectionFactory.createMap();
			for (String order : orderList) {
				String key = aliasMap.get(order);
				long[] valArray = resultMap.get(key);
				if (valArray == null) valArray = new long[hourMap.keySet().size()];
				resultMap.put(key, valArray);
			}

			// Creates the counterMap
			Map<String, int[]> counterMap = CollectionFactory.createMap();
			for (String order : orderList) {
				String key = aliasMap.get(order);
				int[] valArray = counterMap.get(key);
				if (valArray == null) valArray = new int[hourMap.keySet().size()];
				counterMap.put(key, valArray);
			}

			for (DashboardIndicatorData obj : dao.getUsingFilters(entityId, entityKind, elementId,
					elementSubId, shoppingId, subentityId, null, fromStringDate, toStringDate, null,
					null, (byte) -1, (byte) -1, null, null, null, null)) {
				if (isValidForUser(user, obj)) {
					String key = obj.getElementSubName();
					String orderKey = obj.getElementSubId();
					if (!StringUtils.hasText(subIdOrder) || orderList.contains(orderKey)) {
						aliasMap.put(orderKey, key);
						long[] valArray = resultMap.get(key);
						if (valArray == null) valArray = new long[hourMap.keySet().size()];
						int[] counterArray = counterMap.get(key);
						if (counterArray == null) counterArray = new int[hourMap.keySet().size()];

						try {
							// Position calc according to the timezone
							int position = hourMap.get(obj.getTimeZone());
							if (config.getTimezone().equals("-06:00")) {
								position--;
								if (position >= 24) position -= - 24;
							}

							if (average) {
								if (obj.getDoubleValue() != null)
									valArray[position] += obj.getDoubleValue().intValue();
								else
									log.log(Level.WARNING, "Inconsistent DashboardIndicator: " + obj.toString());

								counterArray[position] += obj.getRecordCount();
							} else {
								if (obj.getDoubleValue() != null)
									valArray[position] += obj.getDoubleValue().intValue();
								else
									log.log(Level.WARNING, "Inconsistent DashboardIndicator: " + obj.toString());
							}
							resultMap.put(key, valArray);
						} catch (Exception e) {
							log.log(Level.WARNING, e.getMessage());
							log.log(Level.WARNING, "Inconsistent DashboardIndicator: " + obj.toString());
						}
					}
				}
			}

			// Checks for average return
			if (average) {
				Iterator<String> i = resultMap.keySet().iterator();
				while (i.hasNext()) {
					String key = i.next();
					long[] valArray = resultMap.get(key);
					int[] counterArray = counterMap.get(key);
					for (int x = 0; x < valArray.length; x++) {
						if (counterArray[x] != 0) {
							valArray[x] /= toMinutes ? counterArray[x] / 60000 : counterArray[x];
						}
					}
				}
			}

			// Checks if it has to erase blank spaces
			if (eraseBlanks) {
				int[] eraseMap = new int[hourMap.keySet().size()];
				for (int i = 0; i < eraseMap.length; i++) eraseMap[i] = 0;

				for(long[] valArray : resultMap.values()) {
					for (int x = 0; x < valArray.length; x++)
						if (valArray[x] > 0)
							eraseMap[x]++;
				}

				int newCount = 0;
				for (int i = 0; i < eraseMap.length; i++)
					if (eraseMap[i] > 0)
						newCount++;

				Iterator<String> it = resultMap.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					long[] valArray = resultMap.get(key);
					long[] newArray = new long[newCount];
					int x = 0;
					for (int i = 0; i < valArray.length; i++) {
						if (eraseMap[i] > 0) {
							newArray[x] = valArray[i];
							x++;
						}
					}
					resultMap.put(key, newArray);
				}

				List<String> newCategories = CollectionFactory.createList();
				int i = 0;
				for (String item : categories) {
					if (eraseMap[i] > 0) newCategories.add(item);
					i++;
				}
				categories = newCategories;

			}

			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();
			if (StringUtils.hasText(subIdOrder)) {
				for (String orderKey : orderList) {
					String key = aliasMap.get(orderKey);
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("name", key);
					jsonObj.put("type", "spline");
					jsonObj.put("data", resultMap.get(key) == null ? 0 : resultMap.get(key));
					jsonArray.put(jsonObj);
				}
			} else {
				for(String key : resultMap.keySet()) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("name", key);
					jsonObj.put("type", "spline");
					jsonObj.put("data", resultMap.get(key) == null ? 0 : resultMap.get(key));
					jsonArray.put(jsonObj);
				}
			}

			// Returns the final value
			JSONObject ret = new JSONObject();
			ret.put("categories", categories);
			ret.put("series", jsonArray);
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

	public String getHourName(int hour) {
		return hour + ":00Hs";
	}

	public String getDateName(Date date) {
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
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
