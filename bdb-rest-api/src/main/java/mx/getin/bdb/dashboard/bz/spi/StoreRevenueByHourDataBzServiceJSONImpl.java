package mx.getin.bdb.dashboard.bz.spi;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;

import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.StoreRevenueDAO;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mx.getin.dao.StoreRevenueByHourDAO;
import mx.getin.model.StoreRevenueByHour;

/**
 * @author ignacio
 *
 */
public class StoreRevenueByHourDataBzServiceJSONImpl extends StoreEntityData<StoreRevenueByHour> {

	@Autowired
	private StoreRevenueByHourDAO dao;
	@Autowired
	private StoreRevenueDAO stDao;
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
	protected List<StoreRevenueByHour> daoGetUsingStoreIdAndDatesAndRange(String storeId, String fromDate,
			String toDateOrFromHour, String toHour) throws ASException {
		return dao.getUsingStoreIdAndDateAndRange(storeId, fromDate, toDateOrFromHour, toHour, null, "hour", false);
	}

	
}
