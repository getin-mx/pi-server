package mobi.allshoppings.bz.spi;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.DashboardBrandTableDataBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class DashboardBrandTableDataBzServiceJSONImpl
extends RestBaseServerResource
implements DashboardBrandTableDataBzService {

	private static final Logger log = Logger.getLogger(DashboardBrandTableDataBzServiceJSONImpl.class.getName());
	
	@Autowired
	private DashboardIndicatorDataDAO dao;
	@Autowired
	private StoreDAO storeDao;

	private DecimalFormat df = new DecimalFormat("##.00");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	private DecimalFormat df2 = new DecimalFormat("###,###,###");
	
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
			byte entityKind = obtainByteValue("entityKind", (byte) -1);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);
			Boolean onlyExternalIds = obtainBooleanValue("onlyExternalIds", false);

			// Get all the stores that matches the brand
			List<Store> tmpStores = storeDao.getUsingBrandAndStatus(entityId, Arrays.asList(
					new Byte(StatusAware.STATUS_ENABLED)), "name"); 
			List<Store> stores = CollectionFactory.createList();
			for( Store store : tmpStores ) {
				if( isValidForUser(user, store))
					stores.add(store);
			}
			
			// Ordered Store List
			List<String> storeNames = CollectionFactory.createList();
			Map<String, Store> storeCacheByName = CollectionFactory.createMap();
			Map<String, Store> storeCacheById = CollectionFactory.createMap();
			
			for(Store store : stores ) {
				if( !onlyExternalIds || StringUtils.hasText(store.getExternalId())) {
					storeNames.add(store.getName());
					storeCacheByName.put(store.getName(), store);
					storeCacheById.put(store.getIdentifier(), store);
				}
			}
			
			Collections.sort(storeNames);
			
			// Creates the data map
			Map<String, Map<String, Double>> data = CollectionFactory.createMap();
			Map<String, Double> totalsData = CollectionFactory.createMap();
			
			// Starts to Collect the data
			List<DashboardIndicatorData> list;
			Double value;
			Double totalValue;
			Integer count;
			Integer totalCount = 0;
			Iterator<String> i = storeCacheById.keySet().iterator();
			Double totalLower = 99999999999D;
			Double totalHigher = 0D;
			while(i.hasNext()) {
				Double lower = 99999999999D;
				Double higher = 0D;
				
				Map<String, Double> storeData = CollectionFactory.createMap();
				String storeId = i.next();
				
				// peasents
				list = dao.getUsingFilters(entityId, entityKind, Arrays.asList(new String[] {"apd_visitor"}),
						"visitor_total_peasents", null, storeId, null, fromStringDate, toStringDate, null,
						null, (byte) -1, (byte) -1, null, null, null, null);
				value = 0D;
				for(DashboardIndicatorData obj : list)
					value += obj.getDoubleValue();
				storeData.put("peasents", value);
				totalValue = totalsData.get("peasents");
				if( totalValue == null ) totalValue = 0D;
				totalValue += value;
				totalsData.put("peasents", totalValue);
				
				// visits
				list = dao.getUsingFilters(entityId, entityKind, Arrays.asList(new String[] {"apd_visitor"}),
						"visitor_total_visits", null, storeId, null, fromStringDate, toStringDate, null, null,
						(byte) -1, (byte) -1, null, null, null, null);
				value = 0D;
				Map<String, Double> datesCache = CollectionFactory.createMap();
				Date d1 = sdf2.parse(fromStringDate);
				Date d2 = sdf2.parse(toStringDate);
				while( d1.before(d2) || d1.equals(d2)) {
					datesCache.put(sdf2.format(d1), totalValue);
					d1 = new Date(d1.getTime() + 86400000);
				}
				
				for(DashboardIndicatorData obj : list) {
					value += obj.getDoubleValue();
					
					Double dValue = datesCache.get(obj.getStringDate());
					if( dValue == null ) dValue = 0D;
					dValue += obj.getDoubleValue();
					datesCache.put(obj.getStringDate(), dValue);
				}
				
				Iterator<String> it = datesCache.keySet().iterator();
				while(it.hasNext()) {
					String key = it.next();
					Date d = sdf2.parse(key);
					Double val = datesCache.get(key);
					
					// internally calculates lower
					if( val < lower || !storeData.containsKey("lower") || val == lower && d.getTime() > storeData.get("lower")) { 
						storeData.put("lower", Double.valueOf(d.getTime()));
						lower = val;
					}
					
					if( val < totalLower || !totalsData.containsKey("lower") || val == totalLower && d.getTime() > totalsData.get("lower")) { 
						totalsData.put("lower", Double.valueOf(d.getTime()));
						totalLower = val;
					}
					
					// internally calculates higher
					if( val > higher ||  !storeData.containsKey("higher") || val == higher && d.getTime() > storeData.get("higher")) {
						storeData.put("higher", Double.valueOf(d.getTime()));
						higher = val;
					}
					if( val > totalHigher ||  !totalsData.containsKey("higher") || val == totalHigher && d.getTime() > totalsData.get("higher")) {
						totalsData.put("higher", Double.valueOf(d.getTime()));
						totalHigher = val;
					}
				}
				
				storeData.put("visits", value);
				totalValue = totalsData.get("visits");
				if( totalValue == null ) totalValue = 0D;
				totalValue += value;
				totalsData.put("visits", totalValue);
				
				// tickets
				list = dao.getUsingFilters(entityId, entityKind, Arrays.asList(new String[] {"apd_visitor"}),
						"visitor_total_tickets", null, storeId, null, fromStringDate, toStringDate, null,
						null, (byte) -1, (byte) -1, null, null, null, null);
				value = 0D;
				for(DashboardIndicatorData obj : list)
					value += obj.getDoubleValue();
				storeData.put("tickets", value);
				totalValue = totalsData.get("tickets");
				if( totalValue == null ) totalValue = 0D;
				totalValue += value;
				totalsData.put("tickets", totalValue);
				
				// revenue
				list = dao.getUsingFilters(entityId, entityKind, Arrays.asList(new String[] {"apd_visitor"}),
						"visitor_total_revenue", null, storeId, null, fromStringDate, toStringDate,
						null, null, (byte) -1, (byte) -1, null, null, null, null);
				value = 0D;
				for(DashboardIndicatorData obj : list)
					value += obj.getDoubleValue();
				storeData.put("revenue", value);
				totalValue = totalsData.get("revenue");
				if( totalValue == null ) totalValue = 0D;
				totalValue += value;
				totalsData.put("revenue", totalValue);
				
				// peasents_conversion
				if( storeData.get("peasents") != 0)
					storeData.put("peasents_conversion", (storeData.get("visits") * 100 ) / storeData.get("peasents") );
				else
					storeData.put("peasents_conversion", 0D);

				// tickets_conversion
				if( storeData.get("visits") != 0)
					storeData.put("tickets_conversion", (storeData.get("tickets") * 100 ) / storeData.get("visits"));
				else
					storeData.put("tickets_conversion", 0D);
				
				// permanence
				list = dao.getUsingFilters(entityId, entityKind, Arrays.asList(new String[] {"apd_permanence"}),
						"permanence_hourly_visits", null, storeId, null, fromStringDate, toStringDate, null,
						null, (byte) -1, (byte) -1, null, null, null, null);
				value = 0D;
				count = 0;
				for(DashboardIndicatorData obj : list) {
					if( obj.getDoubleValue() != null ) value += obj.getDoubleValue();
					else log.log(Level.WARNING, "Inconsistent DashboardIndicator: " + obj.toString());
					count += obj.getRecordCount();
				}
				storeData.put("permanence", value / count / 60000);
				totalValue = totalsData.get("permanence");
				if( totalValue == null ) totalValue = 0D;
				totalValue += value;
				totalsData.put("permanence", totalValue);
				totalCount += count;
				
				// Saves the data
				data.put(storeId, storeData);
			}

			// total calculations
			if( totalCount > 0 )
				totalsData.put("permanence", totalsData.get("permanence") / totalCount / 60000);
			else 
				totalsData.put("permanence", 0D);

			if( totalsData.get("peasents") != null && totalsData.get("peasents") != 0)
				totalsData.put("peasents_conversion", (totalsData.get("visits") * 100) / totalsData.get("peasents"));
			else
				totalsData.put("peasents_conversion", 0D);

			if( totalsData.get("visits") != null && totalsData.get("visits") != 0){
				totalsData.put("tickets_conversion", (totalsData.get("tickets") * 100 ) / totalsData.get("visits"));
			}
			else{
				totalsData.put("tickets_conversion", 0D);
			}

			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();
			
			// Titles row
			JSONArray titles = new JSONArray();
			titles.put("Tienda");
			titles.put("Paseantes");
			titles.put("Visitantes");
			titles.put("Tickets");
			titles.put("Ventas");
			titles.put("Paseantes/Visitantes");
			titles.put("Visitantes/Tickets");
			titles.put("Día más Alto");
			titles.put("Día más Bajo");
			titles.put("Permanencia Promedio");
			jsonArray.put(titles);
			
			// Values Array
			for( String name : storeNames ) {
				JSONArray row = new JSONArray();
				Store store = storeCacheByName.get(name);
				Map<String, Double> rowData = data.get(store.getIdentifier());

				if( rowData != null ) {
					row.put(name);
					row.put(df2.format(Math.round(rowData.get("peasents"))));
					row.put(df2.format(Math.round(rowData.get("visits"))));
					row.put(df2.format(Math.round(rowData.get("tickets"))));
					row.put("$ " + df2.format(Math.round(rowData.get("revenue"))));
					row.put(df.format(rowData.get("peasents_conversion")) + "%");
					row.put(df.format(rowData.get("tickets_conversion")) + "%");
					row.put(rowData.get("higher") == null ? "-" : getDateName(new Date(rowData.get("higher").longValue())));
					row.put(rowData.get("lower") == null ? "-" : getDateName(new Date(rowData.get("lower").longValue())));
					row.put(String.valueOf(Math.round(rowData.get("permanence"))) + " mins");

					jsonArray.put(row);
				}
			}

			// Totals row
			JSONArray totals = new JSONArray();
			totals.put("Totales");
			totals.put(totalsData.get("peasents") == null ? "-" : df2.format(Math.round(totalsData.get("peasents"))));
			totals.put(totalsData.get("visits") == null ? "-" : df2.format(Math.round(totalsData.get("visits"))));
			totals.put(totalsData.get("tickets") == null ? "-" : df2.format(Math.round(totalsData.get("tickets"))));
			totals.put(totalsData.get("revenue") == null ? "-" : "$ " + df2.format(Math.round(totalsData.get("revenue"))));
			totals.put(totalsData.get("peasents_conversion") == null ? "-" : df.format(totalsData.get("peasents_conversion")) + "%");
			totals.put(totalsData.get("tickets_conversion") == null ? "-" : df.format(totalsData.get("tickets_conversion")) + "%");
			totals.put(totalsData.get("higher") == null ? "-" : getDateName(new Date(totalsData.get("higher").longValue())));
			totals.put(totalsData.get("lower") == null ? "-" : getDateName(new Date(totalsData.get("lower").longValue())));
			totals.put(totalsData.get("permanence") == null ? "-" : String.valueOf(Math.round(totalsData.get("permanence"))) + " mins");
			
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
