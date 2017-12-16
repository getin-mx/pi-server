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

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.bdb.bz.BDBTimelineHourBzService;
import mobi.allshoppings.dao.DashboardConfigurationDAO;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardConfiguration;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.CollectionFactory;

/**
 * Returns data for the Hourly chart at the dashboard
 * @author Matias Hapanowicz
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 2.0, december 2017
 * @since Allshoppings
 */
public class TimelineHourBzServiceJSONImpl extends BDBRestBaseServerResource implements BDBTimelineHourBzService {

	private static final Logger log = Logger.getLogger(TimelineHourBzServiceJSONImpl.class.getName());
	@Autowired
	private DashboardIndicatorDataDAO dao;
	@Autowired
	private DashboardConfigurationDAO dcDao;

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
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);
			Boolean average = obtainBooleanValue("average", false);
			Boolean toMinutes = obtainBooleanValue("toMinutes", false);
			Boolean eraseBlanks = obtainBooleanValue("eraseBlanks", false);

			if(!StringUtils.hasText(entityId)) throw ASExceptionHelper.invalidArgumentsException("entityId");
			if(!StringUtils.hasText(subentityId)) throw ASExceptionHelper.invalidArgumentsException("subentityId");
			
			// Gets dashboard configuration for this session
			DashboardConfiguration config;
			try {
				config = dcDao.getUsingEntityIdAndEntityKind(entityId, entityKind, true);
			} catch( Exception e ) {
				config = new DashboardConfiguration(entityId, entityKind);
			}
			
			List<String> categories = CollectionFactory.createList();

			// Creates the Hour Map
			for( int hourMapPosition = 0; hourMapPosition < 24; hourMapPosition++ ) {
				categories.add(getHourName(hourMapPosition));
			}
			
			// Creates the result map
			Map<String, long[]> resultMap = CollectionFactory.createMap();
			
			// Creates the counterMap
			Map<String, int[]> counterMap = CollectionFactory.createMap();
			
			for(DashboardIndicatorData obj : dao.getUsingFilters(Arrays.asList(entityId), entityKind,
					Arrays.asList(elementId), elementSubId == null ? null : Arrays.asList(elementSubId),
							shoppingId, CollectionFactory.createList(subentityId.split(",")), null,
							fromStringDate, toStringDate, null, null, null, null, null, null, null, null)) {
				if(!isValidForUser(user, obj)) continue;
				String key = obj.getElementSubName();
				long[] valArray = resultMap.get(key);
				if( valArray == null ) valArray = new long[24];
				int[] counterArray = counterMap.get(key);
				if( counterArray == null ) counterArray = new int[24];
				// Position calc according to the timezone
				int position = obj.getTimeZone();
				if( config.getTimezone().equals("-06:00")) {
					position = position - 1;
					if( position >= 24 )
						position = position - 24;
				}

				if( average ) {
					if( obj.getDoubleValue() != null )
						valArray[position] += obj.getDoubleValue().intValue();
					else 
						log.log(Level.WARNING, "Inconsistent DashboardIndicator: " + obj.toString());

					if( obj.getRecordCount() != null )
						counterArray[position] += obj.getRecordCount().intValue();
					else 
						log.log(Level.WARNING, "Inconsistent DashboardIndicator: " + obj.toString());

				} else {
					if( obj.getDoubleValue() != null )
						valArray[position] += obj.getDoubleValue().intValue();
					else 
						log.log(Level.WARNING, "Inconsistent DashboardIndicator: " + obj.toString());
				}
				resultMap.put(key, valArray);
			}

			// Checks for average return & Checks if it has to erase blank spaces
			boolean[] eraseMap = null;
			if( average || eraseBlanks) {
				Iterator<String> it = resultMap.keySet().iterator();
				if(eraseBlanks) {
					eraseMap = new boolean[24];
				} while(it.hasNext()) {
					String key = it.next();
					long[] valArray = resultMap.get(key);
					int[] counterArray = counterMap.get(key);
					for( int x = 0; x < valArray.length; x++) {
						if(eraseBlanks && valArray[x] > 0) eraseMap[x] = true;
						if(counterArray == null) continue;
						if( counterArray[x] != 0 ) {
							valArray[x] = toMinutes ? new Long(Math.round(valArray[x] / counterArray[x] / 60000))
									: new Long(Math.round(valArray[x] / counterArray[x]));
						}
					}
				}
			} if(eraseBlanks) {
				int newCount = 0;
				for( int i = 0; i < eraseMap.length; i++ ) if(eraseMap[i]) newCount++;
				List<String> newCategories = CollectionFactory.createList();
				Iterator<String> nIt = resultMap.keySet().iterator();
				while(nIt.hasNext()) {
					String key = nIt.next();
					long[] valArray = resultMap.get(key);
					long[] newArray = new long[newCount];
					int x = 0;
					for( int i = 0; i < valArray.length; i++ ) {
						if(eraseMap[i]) {
							newArray[x] = valArray[i];
							x++;
						}
					}
					resultMap.put(key, newArray);
				}

				int i = 0;
				for( String item : categories ) {
					if(eraseMap[i]) newCategories.add(item);
					i++;
				}
				categories = newCategories;
				
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

	public String getHourName(int hour) {
		return hour + ":00Hs";
	}
	
	public String getDateName(Date date) {
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		switch(cal.get(Calendar.DAY_OF_WEEK)) {
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
