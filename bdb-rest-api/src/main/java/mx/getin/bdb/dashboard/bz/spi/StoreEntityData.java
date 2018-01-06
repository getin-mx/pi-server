package mx.getin.bdb.dashboard.bz.spi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import com.inodes.util.CollectionFactory;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBPostBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.StoreItem;
import mobi.allshoppings.model.StoreRevenue;
import mobi.allshoppings.model.StoreTicket;
import mobi.allshoppings.model.StoreTicketByHour;
import mobi.allshoppings.model.interfaces.ModelKey;
import mx.getin.model.StoreItemByHour;
import mx.getin.model.StoreRevenueByHour;

public abstract class StoreEntityData<T extends ModelKey> extends BDBRestBaseServerResource
		implements BDBDashboardBzService, BDBPostBzService {

	protected static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	protected static final long ONE_DAY = 86400000;
	protected static final long ONE_HOUR = 3600000;
	protected static final Logger log = Logger.getLogger(StoreEntityData.class.getName());
	protected static final String PREVIEW_METHOD = "previewFileUpdate";
	protected static final String UPLOAD_METHOD = "doFileUpdate";

	static {
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	@Override
	public String retrieve() {
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			obtainUserIdentifier(true);
			
			String storeId = obtainStringValue("storeId", null);
			String fromDate = obtainStringValue("fromDate", null);
			String toDate = obtainStringValue("toDate", null);
			String toHour = null;
			if(!StringUtils.hasText(fromDate) || !StringUtils.hasText(toDate)) {
				fromDate = obtainStringValue("date", null);
				toDate = obtainStringValue("fromHour", null);
				toHour = obtainStringValue("toHour", null);
			}

			boolean parsingHourly = StringUtils.hasText(toHour);
			
			List<T> list = daoGetUsingStoreIdAndDatesAndRange(storeId, fromDate, toDate, toHour);
			Map<String, T> tmp = CollectionFactory.createMap();
			for( T obj : list ) {
				if(obj instanceof StoreTicket) {
					tmp.put(((StoreTicket) obj).getDate(), obj);
				} else if(obj instanceof StoreRevenue) {
					tmp.put(((StoreRevenue) obj).getDate(), obj);
				} else if(obj instanceof StoreItem) {
					tmp.put(((StoreItem) obj).getDate(), obj);
				} else if(obj instanceof StoreTicketByHour) {
					tmp.put(((StoreTicketByHour) obj).getHour(), obj);
				} else if(obj instanceof StoreRevenueByHour) {
					tmp.put(((StoreRevenueByHour) obj).getHour(), obj);
				} else if(obj instanceof StoreItemByHour) {
					tmp.put(((StoreItemByHour) obj).getHour(), obj);
				}
			}
			
			Date curDate = sdf.parse(parsingHourly ? toDate : fromDate);
			Date limitDate = sdf.parse(parsingHourly ? toHour : toDate);
			JSONArray jsonArray = new JSONArray();
			JSONArray dateArray = new JSONArray();
			String parsedDate;
			while( curDate.before(limitDate) || curDate.equals(limitDate) ) {
				parsedDate = sdf.format(curDate);
				dateArray.put(parsedDate);
				T obj = tmp.get(parsedDate);
				if( obj == null ) {
					jsonArray.put(0);
				} else {
					if(obj instanceof StoreTicket) {
						jsonArray.put(((StoreTicket) obj).getQty());
					} else if(obj instanceof StoreRevenue) {
						jsonArray.put(((StoreRevenue) obj).getQty());
					} else if(obj instanceof StoreItem) {
						jsonArray.put(((StoreItem) obj).getQty());
					} else if(obj instanceof StoreTicketByHour) {
						jsonArray.put(((StoreTicketByHour) obj).getQty());
					} else if(obj instanceof StoreRevenueByHour) {
						jsonArray.put(((StoreRevenueByHour) obj).getQty());
					} else if(obj instanceof StoreItemByHour) {
						jsonArray.put(((StoreItemByHour) obj).getQty());
					}
				}
				curDate.setTime(curDate.getTime() +(parsingHourly ? ONE_HOUR : ONE_DAY));
			}
			
			// Returns the final value
			JSONObject ret = new JSONObject();
			ret.put("storeId", storeId);
			if(parsingHourly) {
				ret.put("date", fromDate);
				ret.put("fromHour", fromDate);
				ret.put("toHour", toHour);
			} else {
				ret.put("fromDate", fromDate);
				ret.put("toDate", toDate);
			}
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
	
	protected abstract List<T> daoGetUsingStoreIdAndDatesAndRange(String storeId, String fromDate,
			String toDateOrFromHour, String toHour) throws ASException;
	
}
