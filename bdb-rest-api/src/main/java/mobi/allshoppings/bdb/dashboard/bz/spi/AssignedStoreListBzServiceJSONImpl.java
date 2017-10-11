package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.model.adapter.NameAndIdAdapter;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class AssignedStoreListBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(AssignedStoreListBzServiceJSONImpl.class.getName());

	@Autowired
	private StoreDAO storeDao;

	/**
	 * Obtains a list of FloorMap points
	 * 
	 * @return A JSON representation of the selected fields for a FloorMap
	 */
	@Override
	public String retrieve()
	{
		long start = markStart();
		JSONObject returnValue = null;
		try {
			// obtain the id and validates the auth token
			User user = getUserFromToken();
			List<Store> storeList = CollectionFactory.createList();
			long diff = 0;
			
			long millisPre = new Date().getTime();

			String entityId = obtainStringValue("entityId", null);
			Integer entityKind = obtainIntegerValue("entityKind", null);
			Boolean onlyExternalIds = obtainBooleanValue("onlyExternalIds", false);
			Integer storeType = obtainIntegerValue("storeType", null);
			List<Integer> status = Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED});
			
			
			if( EntityKind.KIND_SHOPPING == entityKind ) {
				storeList = storeDao.getUsingShoppingAndStatus(entityId, status, "uName");
			} else if( EntityKind.KIND_BRAND == entityKind ) {
				storeList = storeDao.getUsingBrandAndStatus(entityId, status, "uName");
			}
			
			if( onlyExternalIds ) {
				List<Store> tmpList = CollectionFactory.createList();
				for( Store store : storeList ) {
					if(StringUtils.hasText(store.getExternalId())) {
						tmpList.add(store);
					}
				}
				storeList.clear();
				storeList.addAll(tmpList);
			}
			
			if( user.getSecuritySettings().getRole().equals(Role.STORE)) {
				List<Store> tmpList = CollectionFactory.createList();
				for( Store store : storeList ) {
					if(user.getSecuritySettings().getStores().contains(store.getIdentifier())) {
						tmpList.add(store);
					}
				}
				storeList.clear();
				storeList.addAll(tmpList);
			}
			
			diff = new Date().getTime() - millisPre;

			// Logs the result
			log.info("Number of stores found [" + storeList.size() + "] in " + diff + " millis");

			// Creates the list
			List<NameAndIdAdapter> adapter = CollectionFactory.createList();
			for( Store b : storeList ) {
					if(storeType == 0 || storeType == null || storeType == b.getStoreKind()) {
						NameAndIdAdapter obj = new NameAndIdAdapter();
						obj.setIdentifier(b.getIdentifier());
						obj.setName(b.getName());
						obj.setAvatarId(b.getAvatarId());
						adapter.add(obj);
					}
			}
			
			returnValue = this.getJSONRepresentationFromArrayOfObjects(
					adapter, this.obtainOutputFields(NameAndIdAdapter.class));

			return returnValue.toString();

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
