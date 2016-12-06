package mobi.allshoppings.bz.spi;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.FinancialEntityDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserEntityCache;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;
import mobi.allshoppings.model.adapter.NameAndIdAdapter;
import mobi.allshoppings.model.adapter.NameAndIdAndFavoriteAdapter;
import mobi.allshoppings.model.adapter.OfferAdapter;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CollectionUtils;
import mobi.allshoppings.uec.UserEntityCacheBzService;

import org.springframework.beans.factory.annotation.Autowired;

public class OfferRestServerResource extends RestBaseServerResource {

	private static final Logger logger = Logger.getLogger(OfferRestServerResource.class.getName());

	private Map<String, Object> sessionCache = CollectionFactory.createMap();

	@Autowired
	private ShoppingDAO shoppingDao;
	@Autowired
	private BrandDAO brandDao;
	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private FinancialEntityDAO financialEntityDao;
	@Autowired
	private UserEntityCacheBzService uecService;

	public OfferAdapter completeAdaptationWithoutLists(OfferAdapter obj, String requester) throws ASException {

		obj.setRequester(requester);

		if(!CollectionUtils.isEmpty(obj.getShoppings())) {
			String key = CollectionUtils.firstElement(obj.getShoppings());
			if( null != key ) { 
				try {
					Shopping shopping = sessionCache.containsKey(key) ? (Shopping)sessionCache.get(key) : shoppingDao.get(key);
					if( shopping != null ) {
						obj.setShoppingId(shopping.getIdentifier());
						obj.setShoppingName(shopping.getName());
						sessionCache.put(key, shopping);
					}
				} catch( ASException e ) {
					if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					}
				}
			}
		}

		if(!CollectionUtils.isEmpty(obj.getBrands())) {
			String key = CollectionUtils.firstElement(obj.getBrands());
			if( null != key ) { 
				try {
					Brand brand = sessionCache.containsKey(key) ? (Brand)sessionCache.get(key) : brandDao.get(key);
					if( brand != null ) {
						obj.setBrandId(brand.getIdentifier());
						obj.setBrandName(brand.getName());
						sessionCache.put(key, brand);
					}
				} catch( ASException e ) {
					if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					}
				}
			}
		}

		if(!CollectionUtils.isEmpty(obj.getStores())) {
			String key = CollectionUtils.firstElement(obj.getStores());
			if( null != key ) {
				try {
					Store store = sessionCache.containsKey(key) ? (Store)sessionCache.get(key) : storeDao.get(key);
					if( store != null ) {
						obj.setStoreId(store.getIdentifier());
						obj.setStoreName(store.getName());
						sessionCache.put(key, store);
					}
				} catch( ASException e ) {
					if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					}
				}

			}
		}

		return obj;

	}
	
	public OfferAdapter completeAdaptationFor(OfferAdapter obj, User requester) throws ASException {
		try {

			obj.setRequester(requester.getIdentifier());

			UserEntityCache uecShopping        = uecService.get(requester, EntityKind.KIND_SHOPPING, UserEntityCache.TYPE_FAVORITES_ONLY);
			UserEntityCache uecBrand           = uecService.get(requester, EntityKind.KIND_BRAND, UserEntityCache.TYPE_FAVORITES_ONLY);
			UserEntityCache uecFinancialEntity = uecService.get(requester, EntityKind.KIND_FINANCIAL_ENTITY, UserEntityCache.TYPE_FAVORITES_ONLY);

			obj.setShoppingList(new GenericAdapterImpl<NameAndIdAndFavoriteAdapter>()
					.adaptList(shoppingDao.getUsingIdsAndStatusAndRangeInCache(
							obj.getShoppings(), null, null, requester,
							UserEntityCache.TYPE_FAVORITES_FIRST, "uCountry asc, uProvince asc, uIdentifier asc"), obj
							.getRequester(), NameAndIdAndFavoriteAdapter.class, uecShopping.getEntities(), null));

			obj.setBrandList(new GenericAdapterImpl<NameAndIdAndFavoriteAdapter>().adaptList(
					brandDao.getUsingIdsAndStatusAndRangeInCache(
							obj.getBrands(), null, null, requester,
							UserEntityCache.TYPE_FAVORITES_FIRST), obj
							.getRequester(), NameAndIdAndFavoriteAdapter.class, uecBrand.getEntities(), null));

			obj.setFinancialEntityList(new GenericAdapterImpl<NameAndIdAndFavoriteAdapter>().adaptList(
					financialEntityDao.getUsingIdsAndStatusAndRangeInCache(
							obj.getAvailableFinancialEntities(), null, null, requester,
							UserEntityCache.TYPE_FAVORITES_FIRST, "uIdentifier"), obj
							.getRequester(), NameAndIdAndFavoriteAdapter.class, uecFinancialEntity.getEntities(), null));

			obj.setStoreList(new ArrayList<NameAndIdAdapter>());

			if( obj.getShoppingList().size() > 0 ) {
				obj.setShoppingId(obj.getShoppingList().get(0).getIdentifier());
				obj.setShoppingName(obj.getShoppingList().get(0).getName());
			}

			if( obj.getBrandList().size() > 0 ) {
				obj.setBrandId(obj.getBrandList().get(0).getIdentifier());
				obj.setBrandName(obj.getBrandList().get(0).getName());
			}

			if( obj.getStoreList().size() > 0 ) {
				obj.setStoreId(obj.getStoreList().get(0).getIdentifier());
				obj.setStoreName(obj.getStoreList().get(0).getName());
			}

			return obj;
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

}
