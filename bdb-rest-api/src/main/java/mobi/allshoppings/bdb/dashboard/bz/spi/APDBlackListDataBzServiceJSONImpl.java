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
import mobi.allshoppings.dao.APDMABlackListDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.StoreTicketDAO;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDMABlackList;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreTicket;


/**
 *
 */
public class APDBlackListDataBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService, BDBPostBzService {

	private static final Logger log = Logger.getLogger(APDBlackListDataBzServiceJSONImpl.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final long ONE_DAY = 86400000;
	
	@Autowired
	private APDMABlackListDAO dao;
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
			String entityId = obtainStringValue("entityId", null);
			Integer entityKind = obtainIntegerValue("entityKind", null);
			List<APDMABlackList> list = dao.getUsingEntityIdAndRange(entityId, entityKind, null, null, false);
			
		//save the values in the array for return
			Map<String, String> resultMap = CollectionFactory.createMap();
			for(APDMABlackList obj : list) {
				String key = obj.getEntityId();
				String value = obj.getMac();	
				resultMap.put(key, value);
			}
		// Returns the final value
			JSONObject ret = new JSONObject();
			ret.put("entityId", entityId);
			ret.put("entityKind", entityKind);
			
			ret.put("data", resultMap);
			
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
			String entityId = json.getString("entityId");
			String entityKind = json.getString("entityKind");
			
			
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
		} finally {			markEnd(start);
		}
	}
}
