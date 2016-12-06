package mobi.allshoppings.uec.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.DeviceLocationDAO;
import mobi.allshoppings.dao.FavoriteDAO;
import mobi.allshoppings.dao.FinancialEntityDAO;
import mobi.allshoppings.dao.GeoEntityDAO;
import mobi.allshoppings.dao.OfferDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.UserEntityCacheDAO;
import mobi.allshoppings.dao.spi.BrandDAOJDOImpl;
import mobi.allshoppings.dao.spi.DeviceLocationDAOJDOImpl;
import mobi.allshoppings.dao.spi.FavoriteDAOJDOImpl;
import mobi.allshoppings.dao.spi.FinancialEntityDAOJDOImpl;
import mobi.allshoppings.dao.spi.GeoEntityDAOJDOImpl;
import mobi.allshoppings.dao.spi.OfferDAOJDOImpl;
import mobi.allshoppings.dao.spi.ShoppingDAOJDOImpl;
import mobi.allshoppings.dao.spi.UserEntityCacheDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.geocoding.impl.GeoCodingHelperGMapsImpl;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.DeviceLocation;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.FinancialEntity;
import mobi.allshoppings.model.GeoEntity;
import mobi.allshoppings.model.Offer;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserEntityCache;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CollectionUtils;
import mobi.allshoppings.uec.UserEntityCacheBzService;

import org.springframework.util.StringUtils;

public class UserEntityCacheBzServiceImpl implements UserEntityCacheBzService {

	private static final Logger log = Logger.getLogger(UserEntityCacheBzServiceImpl.class.getName());
	
	private FavoriteDAO favoriteDao = new FavoriteDAOJDOImpl();
	private ShoppingDAO shoppingDao = new ShoppingDAOJDOImpl();
	private FinancialEntityDAO financialEntityDao = new FinancialEntityDAOJDOImpl();
	private BrandDAO brandDao = new BrandDAOJDOImpl();
	private OfferDAO offerDao = new OfferDAOJDOImpl();
	private UserEntityCacheDAO userEntityCacheDao = new UserEntityCacheDAOJDOImpl();
	private DeviceLocationDAO deviceLocationDao = new DeviceLocationDAOJDOImpl();
	private GeoEntityDAO geDao = new GeoEntityDAOJDOImpl();
	private GeoCodingHelper geocoder = new GeoCodingHelperGMapsImpl();

	private final static SimpleDateFormat dateOnlySDF = new SimpleDateFormat("yyyyMMdd"); 
	
	@Override
	public UserEntityCache getCountryList() throws ASException {
		
		final UserEntityCache base = userEntityCacheDao.getUsingKindAndListName("countries", EntityKind.KIND_SHOPPING, false);
		if( userEntityCacheDao.needsUpdate(base, EntityKind.KIND_SHOPPING)) {
			List<Shopping> shoppings = shoppingDao.getAll();
			for(Shopping s : shoppings) {
				if(StringUtils.hasText(s.getAddress().getCountry()) && !base.getEntities().contains(s.getAddress().getCountry())) {
					base.getEntities().add(s.getAddress().getCountry());
				}
			}

			userEntityCacheDao.createOrUpdate(base);

		}
		
		return base;
	}

	@Override
	public UserEntityCache get(User user, int kind, int returnType ) throws ASException {
		
		UserEntityCache ret = null;
		ViewLocation vl = user.getViewLocation();
		
		if( user.getViewLocation() == null || !StringUtils.hasText(user.getViewLocation().getCountry())) {
			return null;
		}
		
		// Process for the base entity cache
		UserEntityCache base = userEntityCacheDao.getUsingKindAndViewLocation(user.getViewLocation(), kind, UserEntityCache.TYPE_NORMAL_SORT, false);
		if( userEntityCacheDao.needsUpdate(base, vl)) base = rebuildUsingViewLocationAndKind(vl, kind);
		
		// Check for normal sort. If this is the case, we only need to check the 
		// user view location and get the default UserEntityCache for this 
		if( returnType == UserEntityCache.TYPE_NORMAL_SORT ) {
			
			ret = base;
			
		// Check for Favorites first sort. In this case, we need to merge the  
		// normal and favorites list
		} else if( returnType == UserEntityCache.TYPE_FAVORITES_FIRST ) {
			UserEntityCache fav = userEntityCacheDao.getUsingKindAndFavorite(user, kind, UserEntityCache.TYPE_FAVORITES_ONLY, false);
			if( userEntityCacheDao.needsUpdate(fav, user)) fav = rebuildUsingUserAndKind(user, kind);
			
			ret = new UserEntityCache(user.getIdentifier(), kind, returnType, UserEntityCache.DEFAULT_CACHE_DURATION );
			ret.setKey(userEntityCacheDao.createKey(user.getIdentifier(), kind, returnType));
			ret.setLastUpdate(new Date());
			ret.getEntities().addAll(fav.getEntities());
			for( String id : base.getEntities() ) {
				if(!ret.getEntities().contains(id)) ret.getEntities().add(id);
			}

		// Check for bundle sort (only for offers) . In this case, we need to merge the  
		// normal and favorites list
		} else if( returnType == UserEntityCache.TYPE_BUNDLE ) {
			UserEntityCache fav = userEntityCacheDao.getUsingKindAndFavorite(user, kind, UserEntityCache.TYPE_FAVORITES_ONLY, false);
			if( userEntityCacheDao.needsUpdate(fav, user)) fav = rebuildUsingUserAndKind(user, kind);
			UserEntityCache myOffers = userEntityCacheDao.getUsingKindAndFavorite(user, kind, UserEntityCache.TYPE_MY_OFFERS, false);
			
			ret = new UserEntityCache(user.getIdentifier(), kind, returnType, UserEntityCache.DEFAULT_CACHE_DURATION );
			ret.setKey(userEntityCacheDao.createKey(user.getIdentifier(), kind, returnType));
			ret.setLastUpdate(new Date());
			for( String id : myOffers.getEntities() ) {
				if(!ret.getEntities().contains(id)) ret.getEntities().add(id);
			}
			for( String id : base.getEntities() ) {
				if(!ret.getEntities().contains(id)) ret.getEntities().add(id);
			}
			
		// Other case, just delegate to the actual user entity cache  
		} else  {
			ret = userEntityCacheDao.getUsingKindAndFavorite(user, kind, returnType, false);
			if( userEntityCacheDao.needsUpdate(ret, user)) {
				rebuildUsingUserAndKind(user, kind);
				ret = userEntityCacheDao.getUsingKindAndFavorite(user, kind, returnType, false);
			}

		}
		
		return ret;
	}
	
	@Override
	public UserEntityCache rebuildUsingUserAndKind( User user, int kind ) throws ASException {
		switch(kind) {
		case EntityKind.KIND_SHOPPING:
			return rebuildShoppingsEntityCache(user);
		case EntityKind.KIND_BRAND:
			return rebuildBrandsEntityCache(user);
		case EntityKind.KIND_FINANCIAL_ENTITY:
			return rebuildFinancialEntitiesEntityCache(user);
		case EntityKind.KIND_OFFER:
			return rebuildOffersEntityCache(user);
		}
		return null;
	}
	
	@Override
	public void rebuildUsingViewLocation( ViewLocation vl ) throws ASException {
		UserEntityCache uec = null;
		
		uec = userEntityCacheDao.getUsingKindAndViewLocation(vl, EntityKind.KIND_SHOPPING, UserEntityCache.TYPE_NORMAL_SORT, false);
		if( userEntityCacheDao.needsUpdate(uec, vl)) rebuildUsingViewLocationAndKind(vl, EntityKind.KIND_SHOPPING);
		
		uec = userEntityCacheDao.getUsingKindAndViewLocation(vl, EntityKind.KIND_BRAND, UserEntityCache.TYPE_NORMAL_SORT, false);
		if( userEntityCacheDao.needsUpdate(uec, vl)) rebuildUsingViewLocationAndKind(vl, EntityKind.KIND_BRAND);
		
		uec = userEntityCacheDao.getUsingKindAndViewLocation(vl, EntityKind.KIND_FINANCIAL_ENTITY, UserEntityCache.TYPE_NORMAL_SORT, false);
		if( userEntityCacheDao.needsUpdate(uec, vl)) rebuildUsingViewLocationAndKind(vl, EntityKind.KIND_FINANCIAL_ENTITY);
		
		uec = userEntityCacheDao.getUsingKindAndViewLocation(vl, EntityKind.KIND_OFFER, UserEntityCache.TYPE_NORMAL_SORT, false);
		if( userEntityCacheDao.needsUpdate(uec, vl)) rebuildUsingViewLocationAndKind(vl, EntityKind.KIND_OFFER);

	}
	
	@Override
	public UserEntityCache rebuildUsingViewLocationAndKind( ViewLocation vl, int kind ) throws ASException {
		switch(kind) {
		case EntityKind.KIND_SHOPPING:
			return rebuildViewLocationShoppingsEntityCache(vl);
		case EntityKind.KIND_BRAND:
			return rebuildViewLocationBrandsEntityCache(vl);
		case EntityKind.KIND_FINANCIAL_ENTITY:
			return rebuildViewLocationFinancialEntitiesEntityCache(vl);
		case EntityKind.KIND_OFFER:
			return rebuildViewLocationOffersEntityCache(vl);
		}
		return null;
	}
	
	/**
	 * Rebuilds Shopping Entity Cache according to its View Location
	 * 
	 * @param vl
	 *            ViewLocation to use
	 * @return
	 * @throws ASException
	 */
	private UserEntityCache rebuildViewLocationShoppingsEntityCache(ViewLocation vl) throws ASException {
		List<Shopping> shoppings = shoppingDao.getByViewLocationAndStatus(vl,
				StatusHelper.statusActive(),
				"uCountry asc, uProvince asc, uIdentifier asc");

		// Normal Round First
		final UserEntityCache uec1 = userEntityCacheDao.getUsingKindAndViewLocation(vl, EntityKind.KIND_SHOPPING, UserEntityCache.TYPE_NORMAL_SORT, true);
		uec1.getEntities().clear();
		for( Shopping shopping : shoppings ) {
			uec1.getEntities().add(shopping.getIdentifier());
		}

		userEntityCacheDao.createOrUpdate(uec1);
		return uec1;
	}
	
	/**
	 * Rebuilds a Shopping User Entity Cache
	 * 
	 * @param user
	 *            The affected user
	 * @return
	 * @throws ASException
	 */
	private UserEntityCache rebuildShoppingsEntityCache(User user) throws ASException {
		List<String> favorites = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_SHOPPING);
		UserEntityCache uec1 = userEntityCacheDao.getUsingKindAndViewLocation(user.getViewLocation(), EntityKind.KIND_SHOPPING, UserEntityCache.TYPE_NORMAL_SORT, false);
		if( userEntityCacheDao.needsUpdate(uec1, EntityKind.KIND_SHOPPING)) uec1 = rebuildViewLocationShoppingsEntityCache(user.getViewLocation());
		
		// Favorites Only Round now
		final UserEntityCache uec3 = userEntityCacheDao.getUsingKindAndFavorite(user, EntityKind.KIND_SHOPPING, UserEntityCache.TYPE_FAVORITES_ONLY, true);
		uec3.getEntities().clear();
		for( String favorite : favorites ) {
			if( uec1.getEntities().contains(favorite)) uec3.getEntities().add(favorite);
		}

		userEntityCacheDao.createOrUpdate(uec3);
		return uec3;
	}

	/**
	 * Rebuilds Financial Entity Entity Cache according to its View Location
	 * 
	 * @param vl
	 *            ViewLocation to use
	 * @return
	 * @throws ASException
	 */
	private UserEntityCache rebuildViewLocationFinancialEntitiesEntityCache(ViewLocation vl) throws ASException {
		List<FinancialEntity> entities = financialEntityDao.getUsingViewLocationAndStatus(vl, StatusHelper.statusActive());

		// This one is used to filter favorites for local (currentCountry) entities only
		Set<String> localEntities = CollectionFactory.createSet();

		// Normal Round First
		final UserEntityCache uec1 = userEntityCacheDao.getUsingKindAndViewLocation(vl, EntityKind.KIND_FINANCIAL_ENTITY, UserEntityCache.TYPE_NORMAL_SORT, true);
		uec1.getEntities().clear();
		for( FinancialEntity financialEntity : entities ) {
			uec1.getEntities().add(financialEntity.getIdentifier());
			localEntities.add(financialEntity.getIdentifier());
		}

		userEntityCacheDao.createOrUpdate(uec1);
		return uec1;
	}

	/**
	 * Rebuilds a Financial Entity User Entity Cache
	 * 
	 * @param user
	 *            The affected user
	 * @return
	 * @throws ASException
	 */
	private UserEntityCache rebuildFinancialEntitiesEntityCache(User user) throws ASException {
		List<String> favorites = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_FINANCIAL_ENTITY);
		UserEntityCache uec1 = userEntityCacheDao.getUsingKindAndViewLocation(user.getViewLocation(), EntityKind.KIND_FINANCIAL_ENTITY, UserEntityCache.TYPE_NORMAL_SORT, false);
		if( userEntityCacheDao.needsUpdate(uec1, EntityKind.KIND_FINANCIAL_ENTITY)) uec1 = rebuildViewLocationFinancialEntitiesEntityCache(user.getViewLocation());
		
		// Favorites Only Round now
		final UserEntityCache uec3 = userEntityCacheDao.getUsingKindAndFavorite(user, EntityKind.KIND_FINANCIAL_ENTITY, UserEntityCache.TYPE_FAVORITES_ONLY, true);
		uec3.getEntities().clear();
		for( String favorite : favorites ) {
			if( uec1.getEntities().contains(favorite)) uec3.getEntities().add(favorite);
		}

		userEntityCacheDao.createOrUpdate(uec3);
		return uec3;
	}

	/**
	 * Rebuilds Brand Entity Cache according to its View Location
	 * 
	 * @param vl
	 *            ViewLocation to use
	 * @return
	 * @throws ASException
	 */
	private UserEntityCache rebuildViewLocationBrandsEntityCache(ViewLocation vl) throws ASException {
		List<Brand> brands = brandDao.getByViewLocationAndStatus(vl, StatusHelper.statusActive(), "uIdentifier asc");

		// Normal Round First
		final UserEntityCache uec1 = userEntityCacheDao.getUsingKindAndViewLocation(vl, EntityKind.KIND_BRAND, UserEntityCache.TYPE_NORMAL_SORT, true);
		uec1.getEntities().clear();
		for( Brand brand : brands ) {
			uec1.getEntities().add(brand.getIdentifier());
		}

		userEntityCacheDao.createOrUpdate(uec1);
		return uec1;
	}

	/**
	 * Rebuilds a Brand User Entity Cache
	 * 
	 * @param user
	 *            The affected user
	 * @return
	 * @throws ASException
	 */
	private UserEntityCache rebuildBrandsEntityCache(User user) throws ASException {
		List<String> favorites = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_BRAND);
		UserEntityCache uec1 = userEntityCacheDao.getUsingKindAndViewLocation(user.getViewLocation(), EntityKind.KIND_BRAND, UserEntityCache.TYPE_NORMAL_SORT, false);
		if( userEntityCacheDao.needsUpdate(uec1, EntityKind.KIND_BRAND)) uec1 = rebuildViewLocationBrandsEntityCache(user.getViewLocation());
		
		// Favorites Only Round now
		final UserEntityCache uec3 = userEntityCacheDao.getUsingKindAndFavorite(user, EntityKind.KIND_BRAND, UserEntityCache.TYPE_FAVORITES_ONLY, true);
		uec3.getEntities().clear();
		for( String favorite : favorites ) {
			if( uec1.getEntities().contains(favorite)) uec3.getEntities().add(favorite);
		}

		userEntityCacheDao.createOrUpdate(uec3);
		return uec3;
	}

	/**
	 * Rebuilds Offers Entity Cache according to its View Location
	 * 
	 * @param vl
	 *            ViewLocation to use
	 * @return
	 * @throws ASException
	 */
	private UserEntityCache rebuildViewLocationOffersEntityCache(ViewLocation vl) throws ASException {
		List<Offer> offers = offerDao.getByViewLocationAndDate(vl, new Date(), "creationDateTime desc");
		long nowOnlyDate = getDateOnly(new Date());
		Date now = new Date();
		
		// Normal Round First
		final UserEntityCache uec1 = userEntityCacheDao.getUsingKindAndViewLocation(vl, EntityKind.KIND_OFFER, UserEntityCache.TYPE_NORMAL_SORT, true);
		uec1.getEntities().clear();
		for( Offer offer : offers ) {
			if( getDateOnly(offer.getValidFrom()) <= nowOnlyDate && getDateOnly(offer.getValidTo()) >= nowOnlyDate && offer.appliesForDate(now)) {
				uec1.getEntities().add(offer.getIdentifier());
			}
		}

		userEntityCacheDao.createOrUpdate(uec1);
		return uec1;
	}

	/**
	 * Rebuilds an Offer User Entity Cache
	 * 
	 * @param user
	 *            The affected user
	 * @return
	 * @throws ASException
	 */
	private UserEntityCache rebuildOffersEntityCache(User user) throws ASException {
		List<String> favorites = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_OFFER);
		List<String> favoriteBrands = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_BRAND);
		List<String> favoriteShoppings = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_SHOPPING);
		List<String> favoriteFinancialEntities = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_FINANCIAL_ENTITY);
		UserEntityCache uec1 = userEntityCacheDao.getUsingKindAndViewLocation(user.getViewLocation(), EntityKind.KIND_OFFER, UserEntityCache.TYPE_NORMAL_SORT, false);
		if( userEntityCacheDao.needsUpdate(uec1, EntityKind.KIND_OFFER)) uec1 = rebuildViewLocationOffersEntityCache(user.getViewLocation());
		
		List<Offer> offers = offerDao.getByViewLocationAndDate(user.getViewLocation(), new Date(), "creationDateTime desc");
		long nowOnlyDate = getDateOnly(new Date());
		
		// Remove favorites out of range
		for( Offer offer : offers ) {
			if(!( getDateOnly(offer.getValidFrom()) <= nowOnlyDate && getDateOnly(offer.getValidTo()) >= nowOnlyDate ))
				if( favorites.contains(offer.getIdentifier())) favorites.remove(offer.getIdentifier());
		}
		
		// Favorites Only now
		final UserEntityCache uec3 = userEntityCacheDao.getUsingKindAndFavorite(user, EntityKind.KIND_OFFER, UserEntityCache.TYPE_FAVORITES_ONLY, true);
		uec3.getEntities().clear();
		for( String favorite : favorites ) {
			if( uec1.getEntities().contains(favorite)) uec3.getEntities().add(favorite);
		}

		userEntityCacheDao.createOrUpdate(uec3);
		
		// My Offers: This is special only for this kind
		final UserEntityCache uec4 = userEntityCacheDao.getUsingKindAndFavorite(user, EntityKind.KIND_OFFER, UserEntityCache.TYPE_MY_OFFERS, true);
		uec4.getEntities().clear();
		for( String favorite : favorites ) {
			if( uec1.getEntities().contains(favorite)) { 
				uec4.getEntities().add(favorite);
			}
		}
		
		if( !CollectionUtils.isEmpty(favoriteBrands) || !CollectionUtils.isEmpty(favoriteShoppings) || !CollectionUtils.isEmpty(favoriteFinancialEntities)) {
			for( Offer offer : offers ) {
				if(!uec4.getEntities().contains(offer.getIdentifier())) {
					if( getDateOnly(offer.getValidFrom()) <= nowOnlyDate && getDateOnly(offer.getValidTo()) >= nowOnlyDate && offer.appliesForToday()) {
						if (joint(favoriteBrands, offer.getBrands())
								&& joint(favoriteShoppings, offer.getShoppings())
								&& joint(favoriteFinancialEntities,	offer.getAvailableFinancialEntities())) {

							uec4.getEntities().add(offer.getIdentifier());
						}
					}
				}
			}
		}

		userEntityCacheDao.createOrUpdate(uec4);

		// Bundle
		final UserEntityCache uecBundle = userEntityCacheDao.getUsingKindAndFavorite(user, EntityKind.KIND_OFFER, UserEntityCache.TYPE_BUNDLE, true);
		uecBundle.getEntities().clear();
		UserEntityCache myOffers = uec4;
		
		for( String id : myOffers.getEntities() ) {
			if(!uecBundle.getEntities().contains(id)) uecBundle.getEntities().add(id);
		}
		for( String id : uec1.getEntities() ) {
			if(!uecBundle.getEntities().contains(id)) uecBundle.getEntities().add(id);
		}
		userEntityCacheDao.createOrUpdate(uecBundle);

		
		// Geo Priority: This is special only for this kind
		final UserEntityCache uec5 = userEntityCacheDao.getUsingKindAndFavorite(user, EntityKind.KIND_OFFER, UserEntityCache.TYPE_GEOPRIORITY, true);
		uec5.getEntities().clear();
		try {
			// Need to know the last user location and to translate it as a geo point
			DeviceLocation dl = deviceLocationDao.getLastUsingUserId(user.getIdentifier());
			GeoPoint geo = geocoder.getGeoPoint(dl.getLat(), dl.getLon());
			
			// Get all the nearby offers, and add their identifier to a list
			List<GeoEntity> list = geDao.getByProximity(geo, EntityKind.KIND_OFFER, 8, true, true, true);
			List<String> geoList = new ArrayList<String>();
			for( GeoEntity g : list ) {
				if( !geoList.contains(g.getEntityId())) {
					geoList.add(g.getEntityId());
				}
			}

			// And now, match the uecBundle entities with the ones in the list
			for(String identifier : uecBundle.getEntities()) {
				if( geoList.contains(identifier)) {
					if( !uec5.getEntities().contains(identifier)) {
						uec5.getEntities().add(identifier);
					}
				}
			}

		} catch( ASException e ) {
			if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE )
				log.log(Level.SEVERE, e.getMessage(), e);
			uec5.getEntities().addAll(uecBundle.getEntities());
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			uec5.getEntities().addAll(uecBundle.getEntities());
		}
		
		userEntityCacheDao.createOrUpdate(uec5);
		return uec3;
	}
	
	/**
	 * Checks a joint condition. It means, that first we validate if both arrays
	 * has elements, and then, if both has, run a collection disjoint
	 * 
	 * @param a
	 *            First array to check
	 * @param b
	 *            Second array to check
	 * @return the joint condition value
	 */
	private boolean joint(Collection<String> a, Collection<String> b) {
		if( a == null || a.size() == 0 ) return true;
		if( b == null || b.size() == 0 ) return true;
		if(( a instanceof List ) && ((List<String>)a).size() == 1 && ((List<String>)a).get(0).equals("null")) return true;
		if(( b instanceof List ) && ((List<String>)b).size() == 1 && ((List<String>)b).get(0).equals("null")) return true;
		return (!Collections.disjoint(a, b));
	}

	/**
	 * Returns the Date part of a Time Stamp in long format
	 * 
	 * @param d
	 *            The Time Stamp
	 * @return a long formatted value for this date
	 */
	private long getDateOnly(Date d) {
		return Long.valueOf(dateOnlySDF.format(d));
	}
	
}
