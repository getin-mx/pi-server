package mx.getin.bdb.dashboard.bz.spi;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;

import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.StoreItemDAO;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Store;
import mx.getin.dao.StoreItemByHourDAO;
import mx.getin.model.StoreItemByHour;

/**
 * @author ignacio
 *
 */
public class StoreItemByHourDataBzServiceJSONImpl extends StoreEntityData<StoreItemByHour> {

	@Autowired
	private StoreItemByHourDAO dao;
	@Autowired
	private StoreItemDAO stDao;
	@Autowired
	private DashboardAPDeviceMapperService mapper;
	@Autowired
	private StoreDAO storeDao;
	
	private static final SimpleDateFormat hdf = new SimpleDateFormat("HH:mm");
	
	@Override
	public String change(JsonRepresentation entity) {
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			obtainUserIdentifier(true);

			JSONObject json = entity.getJsonObject();

			String method = "";
			if( json.has("method")) {
				method = json.getString("method");
			}
			if(method.equalsIgnoreCase(PREVIEW_METHOD)) {
				return "{"+PREVIEW_METHOD +":comingsoon}";
			} else if(method.equalsIgnoreCase(UPLOAD_METHOD)) {
				return "{"+UPLOAD_METHOD +":comingsoon}";
			} else {
				return "{defaultMethod:comingsoon}";
			}
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
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e))
					.toString();
		} finally {			
			markEnd(start);
		}
	}

	@Override
	protected List<StoreItemByHour> daoGetUsingStoreIdAndDatesAndRange(String storeId, String fromDate,
			String toDateOrFromHour, String toHour, boolean order) throws ASException {
		return dao.getUsingStoreIdAndDateAndRange(storeId, fromDate, toDateOrFromHour, toHour, null,
				order ? "hour" : null, false);
	}

	@Override
	protected StoreItemByHour daoGetUsingStoreIdAndDate(String storeId, String date, String hour) throws ASException {
		return hour == null ? stDao.getUsingStoreIdAndDate(storeId, date, true) :
			dao.getUsingStoreIdAndDateAndHour(storeId, date, hour, true);
	}

	@Override
	protected void daoUpdate(StoreItemByHour obj) throws ASException {
		dao.update(obj);
	}

	@Override
	protected void createStoreData(Store store, double qty, String date, String hour) throws ASException {
		StoreItemByHour obj = new StoreItemByHour();
		obj.setStoreId(store.getIdentifier());
		obj.setBrandId(store.getBrandId());
		obj.setDate(date);
		obj.setHour(hour);
		obj.setQty(qty);
		obj.setKey(dao.createKey());
		dao.create(obj);
	}

}
