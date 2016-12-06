package mobi.allshoppings.bz.spi;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.ShoppingBzService;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.CheckinDAO;
import mobi.allshoppings.dao.FavoriteDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Favorite;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;
import mobi.allshoppings.model.adapter.ShoppingAdapter;
import mobi.allshoppings.model.adapter.StoreAdapter;
import mobi.allshoppings.model.tools.StatusHelper;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;


/**
 *
 */
public class ShoppingBzServiceJSONImpl extends RestBaseServerResource implements ShoppingBzService {

	private static final Logger log = Logger.getLogger(ShoppingBzServiceJSONImpl.class.getName());

	@Autowired
	private ShoppingDAO dao;

	@Autowired
	private BrandDAO brandDao;
	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private FavoriteDAO favoriteDao;
	@Autowired
	private CheckinDAO checkinDao;
	
	@Autowired
	private SystemConfiguration systemConfiguration;

	private static final String IDENTIFIER = "shoppingId";
	private static final String BRAND_IDENTIFIER = "brandId";
	private BzFields bzFields = BzFields.getBzFields(getClass());

	/**
	 * Obtains information about a shopping
	 * 
	 * @return A JSON representation of the selected fields for a shopping
	 */
	@Override
	public String retrieve() {
		
		long start = markStart();
		JSONObject returnValue;
		try {
			// validate authToken
			User user = this.getUserFromToken();

			// obtain the id
			final String shoppingId = obtainIdentifier(IDENTIFIER);
			final String brandId = obtainIdentifier(BRAND_IDENTIFIER);
			Shopping obj = dao.get(shoppingId);

			// Hack to see only 1 or 3 images... not 2
			if( obj.getPhotoId() != null && obj.getPhotoId().size() == 2 ) {
				obj.getPhotoId().remove(1);
			}
			
    		// track action
    		trackerHelper.enqueue( user, getRequestIP(),
    				getRequestAgent(), getFullRequestURI(),
    				getI18NMessage("es_AR", "service.ShoppingBzService") + obj.getName(), 
    				null, null);

			final ShoppingAdapter shopping;
			List<StoreAdapter> stores = new ArrayList<StoreAdapter>();

			// Obtains the shopping entity
			String level = obtainStringValue(LEVEL, systemConfiguration.getDefaultLevelOnShoppingBzService());
			shopping = new GenericAdapterImpl<ShoppingAdapter>().adapt(obj);
			Favorite favorite = favoriteDao.getUsingUserAndEntityAndKind(user, shopping.getIdentifier(), EntityKind.KIND_SHOPPING, true);
			if( favorite != null ) shopping.setFavorite(true);
			shopping.setPoints(user.getPoints());

			// Favorite and Checkin counters
			shopping.setFavoriteCount(favoriteDao.getEntityFavoriteCount(shopping.getIdentifier(), EntityKind.KIND_SHOPPING));
			shopping.setCheckinCount(checkinDao.getEntityCheckinCount(shopping.getIdentifier(), EntityKind.KIND_SHOPPING));
			
			if( StringUtils.hasText(brandId) )  {
				// We have a shopping from a brand, so we must have the store
				Brand brand = brandDao.get(brandId, true);
				shopping.setBrandAvatarId(brand.getAvatarId());
				stores = new GenericAdapterImpl<StoreAdapter>()
						.adaptList(storeDao.getUsingBrandAndShoppingAndStatus(
								brandId, shoppingId,
								StatusHelper.statusActive(), null));
			} else {
				// We have a standard shopping call (means get me all the stores)
				shopping.setBrandAvatarId(shopping.getAvatarId());
				stores = new GenericAdapterImpl<StoreAdapter>()
						.adaptList(storeDao.getUsingShoppingAndStatus(
								shoppingId, StatusHelper.statusActive(), null));
			}
			log.info("retrieved [" + stores.size() + "] stores");

			// Obtains the user JSON representation
			final String[] fields = obtainOutputFields(bzFields, level);
			returnValue = getJSONRepresentationFromObject(shopping, fields);

			// Stores Favorite Management
			List<String> favoriteBrands = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_BRAND);
			for(StoreAdapter store : stores ) {
				if( favoriteBrands.contains(store.getBrandId())) store.setFavorite(true);
			}
			returnValue.append("stores", this.getJSONRepresentationFromArrayOfObjects(stores, new String[] 
					{"brandId", "brandName", "avatarId", "favorite", "storeNumber", "floorNumber"}));

    	} catch (ASException e) {
    		if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE || 
    				e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE) {
    			log.log(Level.INFO, e.getMessage());
    		} else {
    			log.log(Level.SEVERE, e.getMessage(), e);
    		}
    		returnValue = getJSONRepresentationFromException(e);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			returnValue = getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e));
		} finally {
			markEnd(start);
		}
		
		return returnValue.toString();
	}
}
