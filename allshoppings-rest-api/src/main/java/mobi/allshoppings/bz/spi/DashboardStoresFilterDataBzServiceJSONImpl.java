package mobi.allshoppings.bz.spi;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.DashboardStoresFilterDataBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class DashboardStoresFilterDataBzServiceJSONImpl
extends RestBaseServerResource
implements DashboardStoresFilterDataBzService {

	private static final Logger log = Logger.getLogger(DashboardStoresFilterDataBzServiceJSONImpl.class.getName());
	
	@Autowired
	private StoreDAO storeDao;

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
			// obtainUserIdentifier();

			String entityId = obtainStringValue("entityId", null);
			Boolean onlyExternalIds = obtainBooleanValue("onlyExternalIds", false);

			// Get all the stores that matches the brand
			List<Store> stores = storeDao.getUsingBrandAndStatus(entityId,
					Arrays.asList(new Byte[] {StatusAware.STATUS_ENABLED}), "name"); 
			
			// Ordered Store List
			List<String> storeNames = CollectionFactory.createList();
			Map<String, Store> storeCacheByName = CollectionFactory.createMap();
			
			for(Store store : stores ) {
				if( !onlyExternalIds || StringUtils.hasText(store.getExternalId())) {
					storeNames.add(store.getName());
					storeCacheByName.put(store.getName(), store);
				}
			}
			
			Collections.sort(storeNames);
			
			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();

			// Values Array
			for( String name : storeNames ) {
				Store store = storeCacheByName.get(name);
				JSONObject row = new JSONObject();
				row.put("identifier", store.getIdentifier());
				row.put("name", store.getName());
				jsonArray.put(row);
			}
			
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
	
}
