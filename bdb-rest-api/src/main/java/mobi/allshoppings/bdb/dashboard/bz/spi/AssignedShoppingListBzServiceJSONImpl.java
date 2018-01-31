package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.model.adapter.NameAndIdAdapter;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class AssignedShoppingListBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(AssignedBrandListBzServiceJSONImpl.class.getName());

	@Autowired
	private ShoppingDAO shoppingDao;

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
			List<Shopping> shoppingList = new ArrayList<>();
			long diff = 0;
			
			long millisPre = System.currentTimeMillis();

			// Admin users
			if( user.getSecuritySettings().getRole().equals(Role.ADMIN)) {

				shoppingList = shoppingDao.getUsingLastUpdateStatusAndRange(null, null, false,
						Arrays.asList(StatusAware.STATUS_ENABLED ), null, "name", null, false);
				diff = System.currentTimeMillis() - millisPre;
				log.info("Number of shoppings found [" + shoppingList.size() + "] in " + diff + " millis");

			} else {
				// Shopping Users
				if( user.getSecuritySettings().getRole().equals(Role.SHOPPING)) {
					Shopping b = shoppingDao.get(user.getIdentifier(), true);
					shoppingList.add(b);
				} else  if( user.getSecuritySettings().getRole().equals(Role.STORE)) {
					for( String storeId : user.getSecuritySettings().getStores()) {
						try {
							Store store = storeDao.get(storeId, true);
							Shopping b = shoppingDao.get(store.getShoppingId(), true);
							if(!shoppingList.contains(b)) 
								shoppingList.add(b);
						} catch( Exception e ) {}
					}
				} else {
					for( String identifier : user.getSecuritySettings().getShoppings()) {
						try {
							Shopping o = shoppingDao.get(identifier, true);
							shoppingList.add(o);
						} catch( Exception e ) {}
					}
				}
			}

			diff = new Date().getTime() - millisPre;

			// Logs the result
			log.info("Number of brands sorted [" + shoppingList.size() + "] in " + diff + " millis");

			// Creates the list
			List<NameAndIdAdapter> adapter = CollectionFactory.createList();
			for( Shopping b : shoppingList ) {
					NameAndIdAdapter obj = new NameAndIdAdapter();
					obj.setIdentifier(b.getIdentifier());
					obj.setName(b.getName());
					obj.setAvatarId(b.getAvatarId());
					adapter.add(obj);
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
