package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.StoreRevenueDAO;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreRevenue;
import mx.getin.bdb.dashboard.bz.spi.StoreEntityData;


/**
 *
 */
public class StoreRevenueDataBzServiceJSONImpl extends StoreEntityData<StoreRevenue> {

	@Autowired
	private StoreRevenueDAO dao;
	@Autowired
	private DashboardAPDeviceMapperService mapper;
	@Autowired
	private StoreDAO storeDao;
	
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
			mapper.createStoreRevenueDataForDates(fromDate, toDate, storeId, true);

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

	@Override
	protected List<StoreRevenue> daoGetUsingStoreIdAndDatesAndRange(String storeId, String fromDate,
			String toDate, String toHour) throws ASException {
		return dao.getUsingStoreIdAndDatesAndRange(storeId, fromDate, toDate, null, "date", false);
	}
}
