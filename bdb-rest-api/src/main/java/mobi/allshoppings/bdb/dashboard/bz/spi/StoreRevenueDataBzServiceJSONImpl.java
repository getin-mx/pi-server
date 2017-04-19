package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import com.inodes.util.CollectionFactory;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBPostBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.StoreRevenueDAO;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreRevenue;


/**
 *
 */
public class StoreRevenueDataBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService, BDBPostBzService {

	private static final Logger log = Logger.getLogger(StoreRevenueDataBzServiceJSONImpl.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final long ONE_DAY = 86400000;
	
	@Autowired
	private StoreRevenueDAO dao;
	@Autowired
	private DashboardAPDeviceMapperService mapper;
	@Autowired
	private StoreDAO storeDao;
	
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
			obtainUserIdentifier(true);
			
			String storeId = obtainStringValue("storeId", null);
			String fromDate = obtainStringValue("fromDate", null);
			String toDate = obtainStringValue("toDate", null);

			List<StoreRevenue> list = dao.getUsingStoreIdAndDatesAndRange(storeId, fromDate, toDate, null, "date", false);
			Map<String, StoreRevenue> tmp = CollectionFactory.createMap();
			for( StoreRevenue obj : list )
				tmp.put(obj.getDate(), obj);
			
			Date curDate = sdf.parse(fromDate);
			Date limitDate = sdf.parse(toDate);
			JSONArray jsonArray = new JSONArray();
			JSONArray dateArray = new JSONArray();
			while( curDate.before(limitDate) || curDate.equals(limitDate) ) {
				dateArray.put(sdf.format(curDate));
				StoreRevenue obj = tmp.get(sdf.format(curDate));
				if( obj == null ) {
					jsonArray.put(0);
				} else {
					jsonArray.put(obj.getQty());
				}
				curDate = new Date(curDate.getTime() + ONE_DAY);
			}
			
			// Returns the final value
			JSONObject ret = new JSONObject();
			ret.put("storeId", storeId);
			ret.put("fromDate", fromDate);
			ret.put("toDate", toDate);
			
			ret.put("data", jsonArray);
			ret.put("dates", dateArray);
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

	@Override
	public String change(JsonRepresentation entity) {
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			obtainUserIdentifier(true);
			
			JSONObject json = entity.getJsonObject();
			String storeId = json.getString("storeId");
			String fromDate = json.getString("fromDate");
			String toDate = json.getString("toDate");
			JSONArray arr = json.getJSONArray("data");
			
			Store store = storeDao.get(storeId, true);
			
			Date curDate = sdf.parse(fromDate);
			Date limitDate = sdf.parse(toDate);
			int i = 0;
			while( curDate.before(limitDate) || curDate.equals(limitDate) ) {
				double qty = arr.getDouble(i);
				StoreRevenue obj;
				try {
					obj = dao.getUsingStoreIdAndDate(storeId, sdf.format(curDate), true);
					obj.setQty(qty);
					dao.update(obj);
				} catch( Exception e ) {
					obj = new StoreRevenue();
					obj.setStoreId(storeId);
					obj.setBrandId(store.getBrandId());
					obj.setDate(sdf.format(curDate));
					obj.setQty(qty);
					obj.setKey(dao.createKey());
					dao.create(obj);
				}
				i++;
				curDate = new Date(curDate.getTime() + ONE_DAY);
			}
			mapper.createStoreRevenueDataForDates(fromDate, toDate, storeId);
			
			return generateJSONOkResponse().toString();
			
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
