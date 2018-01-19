package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.model.adapter.NameAndIdAdapter;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class AssignedBrandListBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(AssignedBrandListBzServiceJSONImpl.class.getName());

	@Autowired
	private BrandDAO brandDao;

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
			List<Brand> brandList = CollectionFactory.createList();
			long diff = 0;
			
			long millisPre = new Date().getTime();

			// Admin users
			if( user.getSecuritySettings().getRole().equals(Role.ADMIN)) {

				brandList = brandDao.getUsingLastUpdateStatusAndRange(null, null, false,
						Arrays.asList(StatusAware.STATUS_ENABLED), null, "name", null, false);
				diff = new Date().getTime() - millisPre;
				log.info("Number of brands found [" + brandList.size() + "] in " + diff + " millis");

			} else {
				// Brand Users
				if( user.getSecuritySettings().getRole().equals(Role.BRAND)) {
					Brand b = brandDao.get(user.getIdentifier(), true);
					brandList.add(b);
				} else  if( user.getSecuritySettings().getRole().equals(Role.STORE)) {
					for( String storeId : user.getSecuritySettings().getStores()) {
						try {
							Store store = storeDao.get(storeId, true);
							Brand b = brandDao.get(store.getBrandId(), true);
							if(!brandList.contains(b)) 
								brandList.add(b);
						} catch( Exception e ) {}
					}
				}
			}

			diff = new Date().getTime() - millisPre;

			// Logs the result
			log.info("Number of brands sorted [" + brandList.size() + "] in " + diff + " millis");

			// Creates the list
			List<NameAndIdAdapter> adapter = CollectionFactory.createList();
			for( Brand b : brandList ) {
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
