package mobi.allshoppings.bdb.dashboard.bz.spi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.StoreTicketByHourDAO;
import mobi.allshoppings.dao.StoreTicketDAO;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreTicket;
import mobi.allshoppings.model.StoreTicketByHour;
import mx.getin.bdb.dashboard.bz.spi.StoreEntityData;

/**
 *
 */
public class StoreTicketByHourDataBzServiceJSONImpl extends StoreEntityData<StoreTicketByHour> {

	private static final SimpleDateFormat hdf = new SimpleDateFormat("HH:mm");
	
	@Autowired
	private StoreTicketByHourDAO dao;
	@Autowired
	private StoreTicketDAO stDao;
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

			String method = "";
			if( json.has("method")) {
				method = json.getString("method");
			}
			if(method.equalsIgnoreCase(PREVIEW_METHOD)) {
				return "{"+PREVIEW_METHOD +":comingsoon}";
			} else if(method.equalsIgnoreCase(UPLOAD_METHOD)) {
				return "{"+UPLOAD_METHOD +":comingsoon}";
			} else {
			
				String storeId = json.getString("storeId");
				String date = json.getString("date");
				String fromHour = json.getString("fromHour");
				String toHour = json.getString("toHour");
				JSONArray arr = json.getJSONArray("data");
	
				Store store = storeDao.get(storeId, true);
	
				Date curHour = hdf.parse(fromHour);
				Date limitHour = hdf.parse(toHour);
				int i = 0;
				while( curHour.before(limitHour) || curHour.equals(limitHour) ) {
					int qty = arr.getInt(i);
					StoreTicketByHour obj;
					try {
						//obj = 
						obj.setQty(qty);
						dao.update(obj);
					} catch( Exception e ) {
						
					}
					i++;
					curHour = new Date(curHour.getTime() + ONE_HOUR);
				}
	
				mapper.createStoreTicketDataForDates(date, date, storeId, true);
	
				return generateJSONOkResponse().toString();
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
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} finally {			
			markEnd(start);
		}
	}

	@Override
	protected List<StoreTicketByHour> daoGetUsingStoreIdAndDatesAndRange(String storeId, String fromDate,
			String toDateOrFromHour, String toHour, boolean order) throws ASException {
		return dao.getUsingStoreIdAndDateAndRange(storeId, fromDate, toDateOrFromHour, toHour, null,
				order ? "hour" : null, false);
	}

	@Override
	protected StoreTicketByHour daoGetUsingStoreIdAndDate(String storeId, String date, String hour) throws ASException {
		return hour == null ? stDao.getUsingStoreIdAndDate(storeId, date, true) :
			dao.getUsingStoreIdAndDateAndHour(storeId, date, hour, true);
	}

	@Override
	protected void daoUpdate(StoreTicketByHour obj) throws ASException {
		dao.update(obj);
	}

	@Override
	protected void createStoreData(Store store, double qty, String date, String hour) throws ASException {
		StoreTicketByHour obj = new StoreTicketByHour();
		obj.setStoreId(store.getIdentifier());
		obj.setBrandId(store.getBrandId());
		obj.setDate(date);
		obj.setHour(hour);
		obj.setQty(qty);
		obj.setKey(dao.createKey());
		dao.create(obj);
	}	

}