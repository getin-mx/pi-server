package mobi.allshoppings.bz.spi;


import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.NearMeBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.DeviceLocationDAO;
import mobi.allshoppings.dao.FavoriteDAO;
import mobi.allshoppings.dao.GeoEntityDAO;
import mobi.allshoppings.dao.OfferDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.DistanceComparator;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.DeviceLocation;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.GeoEntity;
import mobi.allshoppings.model.Offer;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.adapter.LocationAwareAdapter;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

/**
 *
 */
public class NearMeBzServiceJSONImpl extends RestBaseServerResource implements NearMeBzService {

	private static final Logger log = Logger.getLogger(NearMeBzServiceJSONImpl.class.getName());

	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private FavoriteDAO favoriteDao;
	@Autowired
	private ShoppingDAO shoppingDao;
	@Autowired
	private OfferDAO offerDao;
	@Autowired
	private BrandDAO brandDao;
	@Autowired
	private GeoEntityDAO geDao;
	@Autowired
	private DeviceLocationDAO deviceLocationDao;
	@Autowired
	private GeoCodingHelper geocoder;

	private static final String ENTITY_KIND = "kind";

	private BzFields bzFields = BzFields.getBzFields(getClass());

	@Override
	public String retrieve() {
		
		long start = markStart();
		JSONObject returnValue = null;
		try {
			// validate authToken
			User user = this.getUserFromToken();
			byte entityKind = EntityKind.resolveByName(this.obtainStringValue(ENTITY_KIND, null));
			if( entityKind == -1 ) entityKind = EntityKind.ALL;

			// get level, if not defined use default value
			String level = this.obtainStringValue(LEVEL, BzFields.LEVEL_LIST);
			Range range = this.obtainRange();

			// Get Search query
			Double lat = this.obtainDoubleValue(LAT, null);
			Double lon = this.obtainDoubleValue(LON, null);
			String deviceUUID = this.obtainStringValue(DEVICE_UUID, null);
			Integer presition = this.obtainIntegerValue(PRESITION, systemConfiguration.getDefaultGeoEntityPresition());
			if(( lat == null || lon == null ) && !StringUtils.hasText(deviceUUID) ) {
				List<String> invalidFields = CollectionFactory.createList();
				invalidFields.add(LAT);
				invalidFields.add(LON);
				invalidFields.add(DEVICE_UUID);
				throw ASExceptionHelper.invalidArgumentsException(invalidFields);
			}

			if( lat == null || lat == 0 || lon == null || lon == 0 ) {
				try {
					DeviceLocation loc = deviceLocationDao.get(deviceUUID, true);
					lat = loc.getLat();
					lon = loc.getLon();
				} catch( ASException e ) {
					if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
						throw e;
					}
				}
			}
			
			long millisPre = new Date().getTime();

			// Get favoritesFirst Option
			List<String> favoriteShoppings = CollectionFactory.createList();
			List<String> favoriteOffers = CollectionFactory.createList();
			List<String> favoriteRewards = CollectionFactory.createList();
			List<String> favoriteBrands = CollectionFactory.createList();

			GeoPoint p = geocoder.getGeoPoint(lat, lon);
			List<GeoEntity> geos = CollectionFactory.createList();
			if(entityKind == EntityKind.ALL || entityKind == EntityKind.KIND_SHOPPING ) {
				favoriteShoppings = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_SHOPPING);
				geos.addAll(geDao.getByProximity(p, EntityKind.KIND_SHOPPING, presition, true, true, true));
			}
			if(entityKind == EntityKind.ALL || entityKind == EntityKind.KIND_OFFER ) {
				favoriteOffers = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_OFFER);
				geos.addAll(geDao.getByProximity(p, EntityKind.KIND_OFFER, presition, true, true, true));
			}
			if(entityKind == EntityKind.ALL || entityKind == EntityKind.KIND_REWARD ) {
				favoriteRewards = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_REWARD);
				geos.addAll(geDao.getByProximity(p, EntityKind.KIND_REWARD, presition, true, true, true));
			}
			if(entityKind == EntityKind.ALL || entityKind == EntityKind.KIND_BRAND ) {
				favoriteBrands = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_BRAND);
				geos.addAll(geDao.getByProximity(p, EntityKind.KIND_BRAND, presition, true, true, true));
			}

			List<LocationAwareAdapter> adaptedGeos = CollectionFactory.createList();
			for( GeoEntity obj : geos ) {
				LocationAwareAdapter adapter = new LocationAwareAdapter();
				adapter.setIdentifier(obj.getEntityId());
				adapter.setKind(obj.getEntityKind());
				adapter.setLat(obj.getLat());
				adapter.setLon(obj.getLon());
				if( obj.getEntityKind() == EntityKind.KIND_SHOPPING && favoriteShoppings.contains(obj.getEntityId())) {
					adapter.setFavorite(true);
				}
				if( obj.getEntityKind() == EntityKind.KIND_OFFER && favoriteOffers.contains(obj.getEntityId())) {
					adapter.setFavorite(true);
				}
				if( obj.getEntityKind() == EntityKind.KIND_REWARD && favoriteRewards.contains(obj.getEntityId())) {
					adapter.setFavorite(true);
				}
				if( obj.getEntityKind() == EntityKind.KIND_BRAND && favoriteBrands.contains(obj.getEntityId())) {
					adapter.setFavorite(true);
				}
				adapter.setDistance(geocoder.calculateDistance(lat, lon, adapter.getLat(), adapter.getLon()));
				adaptedGeos.add(adapter);
			}
			Collections.sort(adaptedGeos, new DistanceComparator());

			List<LocationAwareAdapter> tmp = CollectionFactory.createList();
			List<LocationAwareAdapter> ret = CollectionFactory.createList();
			for( int i = range.getFrom(); i < adaptedGeos.size() && i < range.getTo(); i++ ) {
				try {
					LocationAwareAdapter obj = adaptedGeos.get(i);
					if( obj.getKind() == EntityKind.KIND_SHOPPING ) {
						Shopping obj2 = shoppingDao.get(obj.getIdentifier(), true);
						obj.setName(obj2.getName());
						obj.setAvatarId(obj2.getAvatarId());
						obj.setDescription(obj2.getDescription());
					}
					if( obj.getKind() == EntityKind.KIND_BRAND) {
						Brand obj2 = brandDao.get(obj.getIdentifier(), true);
						obj.setName(obj2.getName());
						obj.setAvatarId(obj2.getAvatarId());
						obj.setDescription(obj2.getDescription());
					}
					if( obj.getKind() == EntityKind.KIND_OFFER) {
						Offer obj2 = offerDao.get(obj.getIdentifier(), true);
						obj.setName(obj2.getName());
						obj.setAvatarId(obj2.getAvatarId());
						obj.setDescription(obj2.getDescription());
						obj.setOfferTypeId(obj2.getOfferTypeId());
					}
					tmp.add(obj);
				} catch( ASException e ) {
					if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE )
						throw e;
				}
			}

			// Copy the max locations to the definitive array
			for( int i = 0; i < systemConfiguration.getMaxNearMeLocations() && i < tmp.size(); i++ ) 
				ret.add(tmp.get(i));

			returnValue = this.getJSONRepresentationFromArrayOfObjects(ret, this.obtainOutputFields(bzFields, level));

			// Logs the result
			long diff = new Date().getTime() - millisPre;
			log.info("Number of near geo points found [" + adaptedGeos.size() + "] in " + diff + " millis");

			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.NearMeBzService"),
					null, null);

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
			returnValue = this.getJSONRepresentationFromException(e);
		} finally {
			markEnd(start);
		}
		return returnValue.toString();
	}
}
