package mobi.allshoppings.bz.spi;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.BrandBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
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
import mobi.allshoppings.model.adapter.BrandAdapter;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;
import mobi.allshoppings.model.adapter.StoreAdapter;
import mobi.allshoppings.model.tools.StatusHelper;


/**
 *
 */
public class BrandBzServiceJSONImpl extends RestBaseServerResource implements BrandBzService {

	private static final Logger log = Logger.getLogger(BrandBzServiceJSONImpl.class.getName());

	@Autowired
	private BrandDAO dao;

	@Autowired
	private ShoppingDAO shoppingDao;
	@Autowired
	private CheckinDAO checkinDao;
	@Autowired
	private StoreDAO storeDao;
	
	@Autowired
	private SystemConfiguration systemConfiguration;
	
	@Autowired
	private FavoriteDAO favoriteDao;

	private static final String IDENTIFIER = "brandId";
	private static final String SHOPPING_IDENTIFIER = "shoppingId";
	private BzFields bzFields = BzFields.getBzFields(getClass());

	/**
	 * Obtains information about a brand
	 * 
	 * @return A JSON representation of the selected fields for a brand
	 */
	@Override
	public String retrieve() {
		
		long start = markStart();
		JSONObject returnValue;
		try {
			// validate authToken
			User user = this.getUserFromToken();
			
			// obtain the id
			final String brandId = obtainIdentifier(IDENTIFIER);
			final String shoppingId = obtainIdentifier(SHOPPING_IDENTIFIER);
			Brand obj = dao.get(brandId);
			
			// Just a simple hack to avoid sending false info
			if( obj.getAreaId() != null && obj.getAreaId().size() == 1 && obj.getAreaId().get(0).equals("null")) {
				obj.getAreaId().clear();
			}
			
			// track action
			trackerHelper.enqueue( user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.BrandBzService") + obj.getName(), 
					null, null);

			final BrandAdapter brand;
			List<StoreAdapter> stores = new ArrayList<StoreAdapter>();

			// Obtains the brand entity
			String level = obtainStringValue(LEVEL, systemConfiguration.getDefaultLevelOnBrandBzService());
			brand = new GenericAdapterImpl<BrandAdapter>().adapt(obj);
			Favorite favorite = favoriteDao.getUsingUserAndEntityAndKind(user, brand.getIdentifier(),
					EntityKind.KIND_BRAND, true);
			if( favorite != null ) brand.setFavorite(true);
			brand.setPoints(user.getPoints());

			// Favorite and Checkin counters
			brand.setFavoriteCount(favoriteDao.getEntityFavoriteCount(brand.getIdentifier(), EntityKind.KIND_BRAND));
			brand.setCheckinCount(checkinDao.getEntityCheckinCount(brand.getIdentifier(), EntityKind.KIND_BRAND));

			if( StringUtils.hasText(shoppingId) )  {
				// We have a brand from a shopping, so we must have the store
				Shopping shopping = shoppingDao.get(shoppingId, true);
				brand.setShoppingAvatarId(shopping.getAvatarId());
				stores = new GenericAdapterImpl<StoreAdapter>()
						.adaptList(storeDao.getUsingBrandAndShoppingAndStatus(
								brandId, shoppingId,
								StatusHelper.statusActive(), null));
			} else {
				// We have a standard brand call (means get me all the stores)
				brand.setShoppingAvatarId(brand.getAvatarId());
				stores = new GenericAdapterImpl<StoreAdapter>()
						.adaptList(storeDao.getUsingUserAndBrandAndStatus(user,
								brandId, StatusHelper.statusActive(), null));
			}
			log.info("retrieved [" + stores.size() + "] stores");

			// Obtains the user JSON representation
			final String[] fields = obtainOutputFields(bzFields, level);
			returnValue = getJSONRepresentationFromObject(brand, fields);

			// Stores Favorite Management
			List<String> favoriteShoppings = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_SHOPPING);
			for(StoreAdapter store : stores ) {
				if( favoriteShoppings.contains(store.getShoppingId())) store.setFavorite(true);
				try {
					Shopping shopping = shoppingDao.get(store.getShoppingId());
					if( shopping.getAvatarId() != null ) store.setAvatarId(shopping.getAvatarId());
				} catch( Exception e ) {}
			}

			returnValue.append("stores", this.getJSONRepresentationFromArrayOfObjects(stores, new String[] 
					{"shoppingId", "shoppingName", "avatarId", "favorite", "storeNumber", "floorNumber"}));

//			setCachedJSONString(returnValue.toString());

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
