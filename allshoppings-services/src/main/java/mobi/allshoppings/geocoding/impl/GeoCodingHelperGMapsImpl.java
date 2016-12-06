package mobi.allshoppings.geocoding.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.dao.AddressComponentsCacheDAO;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.DeviceLocationDAO;
import mobi.allshoppings.dao.GeoEntityDAO;
import mobi.allshoppings.dao.OfferDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.model.Address;
import mobi.allshoppings.model.AddressComponentsCache;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.DeviceLocation;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.GeoEntity;
import mobi.allshoppings.model.Offer;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.URLParamEncoder;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderStatus;

public class GeoCodingHelperGMapsImpl implements GeoCodingHelper {

	private final static Logger log = Logger.getLogger(GeoCodingHelperGMapsImpl.class.getName());

	@Autowired
	SystemConfiguration systemConfiguration;
	@Autowired
	ShoppingDAO shoppingDao;
	@Autowired
	BrandDAO brandDao;
	@Autowired
	StoreDAO storeDao;
	@Autowired
	OfferDAO offerDao;
	@Autowired
	UserDAO userDao;
	@Autowired
	DeviceLocationDAO dlDao;
	@Autowired
	AddressComponentsCacheDAO accDao;

	@Autowired
	GeoEntityDAO geDao;

	public static long ONE_DAY = 86400000;
	
	// Geo Hashing Stuff ------------------------------------------------------------------------------------------
	public static int BOTTOM = 0;
	public static int TOP = 1;
	public static int LEFT = 2;
	public static int RIGHT = 3;
	public static int TOPLEFT = 4;
	public static int TOPRIGHT = 5;
	public static int BOTTOMLEFT = 6;
	public static int BOTTOMRIGHT = 7;
	public static int CENTER = 8;

	private static int ODD = 0;
	private static int EVEN = 1;

	private static int[] BITS = {16,8,4,2,1};

	private static String[] BASE32 = { "0", "1", "2", "3", "4", "5", "6", "7",
		"8", "9", "b", "c", "d", "e", "f", "g", "h", "j", "k", "m", "n",
		"p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };

	private static String BASE32C = "0123456789bcdefghjkmnpqrstuvwxyz";

	private static String[][] NEIGHBORS = new String[4][2];
	private static String[][] BORDERS = new String[4][2];

	static {
		NEIGHBORS[RIGHT][EVEN] = new String("bc01fg45238967deuvhjyznpkmstqrwx");
		NEIGHBORS[LEFT][EVEN] = new String("238967debc01fg45kmstqrwxuvhjyznp");
		NEIGHBORS[TOP][EVEN] = new String("p0r21436x8zb9dcf5h7kjnmqesgutwvy");
		NEIGHBORS[BOTTOM][EVEN] = new String("14365h7k9dcfesgujnmqp0r2twvyx8zb");

		NEIGHBORS[BOTTOM][ODD] = NEIGHBORS[LEFT][EVEN];
		NEIGHBORS[TOP][ODD] = NEIGHBORS[RIGHT][EVEN];
		NEIGHBORS[LEFT][ODD] = NEIGHBORS[BOTTOM][EVEN];
		NEIGHBORS[RIGHT][ODD] = NEIGHBORS[TOP][EVEN];

		BORDERS[RIGHT][EVEN] = new String("bcfguvyz");
		BORDERS[LEFT][EVEN] = new String("0145hjnp");
		BORDERS[TOP][EVEN] = new String("prxz");
		BORDERS[BOTTOM][EVEN] = new String("028b");

		BORDERS[BOTTOM][ODD] = BORDERS[LEFT][EVEN];
		BORDERS[TOP][ODD] = BORDERS[RIGHT][EVEN];
		BORDERS[LEFT][ODD] = BORDERS[BOTTOM][EVEN];
		BORDERS[RIGHT][ODD] = BORDERS[TOP][EVEN];
	}
	
	// Geo Hashing Stuff ------------------------------------------------------------------------------------------

	/**
	 * Calculates an adjacent GeoBox
	 * 
	 * @param srcHash
	 *            The source geohash to calculate from
	 * @param direction
	 *            The adjacent direction to calculate
	 * @return Resolved Adjacent GeoBox
	 */
	public static String calculateAdjancent(String srcHash, int direction) {
		srcHash = srcHash.toLowerCase();
		if(srcHash.length() < 1 ) return "";
		String lastChar = srcHash.substring(srcHash.length() - 1);
		int type = (srcHash.length() %2 ) == 1 ? ODD : EVEN;
		String base = srcHash.substring(0, srcHash.length() - 1);
		if( BORDERS[direction][type].indexOf(lastChar) != -1 ) 
			base = calculateAdjancent(base, direction);
		return base + BASE32[NEIGHBORS[direction][type].indexOf(lastChar)];
	}

	/**
	 * Creates a geohash according to a geographic point
	 * 
	 * @param p
	 *            An input geo point
	 * @return A geohash encoded geo point
	 */
	@Override
	public String encodeGeohash(double latitude, double longitude) {
		boolean even = true;
		double lat[] = {-90.0, 90.0};
		double lon[] = {-180.0, 180.0};
		double mid;
		int bit = 0;
		int ch = 0;
		int precision = 13;
		StringBuffer geohash = new StringBuffer();

		while (geohash.length() < precision) {
			if( even ) {
				mid = (lon[0] + lon[1]) / 2;
				if( longitude > mid ) {
					ch |= BITS[bit];
					lon[0] = mid;
				} else {
					lon[1] = mid;
				}
			} else {
				mid = (lat[0] + lat[1]) / 2;
				if( latitude > mid ) {
					ch |= BITS[bit];
					lat[0] = mid;
				} else {
					lat[1] = mid;
				}
			}

			even = !even;
			if( bit < 4 ) bit++;
			else {
				geohash.append(BASE32[ch]);
				bit = 0;
				ch = 0;
			}
		}
		return geohash.toString();
	}

	private double[] refineInterval(double[] interval, int cd, int mask) {
		if(( cd & mask ) != 0 ) {
			interval[0] = (interval[0] + interval[1]) / 2;
		} else {
			interval[1] = (interval[0] + interval[1])/2;
		}
		return interval;
	}

	/**
	 * Creates a GeoPoint based in a geohash
	 */
	@Override
	public GeoPoint decodeGeohash(String geohash) {
		boolean even = true;
		double lat[] = {-90.0, 90.0};
		double lon[] = {-180.0, 180.0};
		//		double laterr = 90.0;
		//		double lonerr = 180.0;
		int mask;

		for( int i = 0; i < geohash.length(); i++ ) {
			char c = geohash.charAt(i);
			int cd = BASE32C.indexOf(c);
			for( int j = 0; j < 5; j++ ) {
				mask = BITS[j];
				if( even ) {
					//					lonerr /= 2;
					lon = refineInterval(lon, cd, mask);
				} else {
					//					laterr /= 2;
					lat = refineInterval(lat, cd, mask);
				}
				even = !even;
			}
		}
		return new GeoPoint((lat[0] + lat[1])/2, (lon[0] + lon[1])/2, geohash);
	}

	@Override
	public GeoPoint getGeoPointFromAddress(String address) throws ASException {

		final Geocoder geocoder = new Geocoder();
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(address).setLanguage("en").getGeocoderRequest();
		GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
		if( geocoderResponse.getStatus().equals(GeocoderStatus.ZERO_RESULTS)) {
			throw ASExceptionHelper.notFoundException();
		}
		if( !geocoderResponse.getStatus().equals(GeocoderStatus.OK) ) {
			throw ASExceptionHelper.notAcceptedException();
		}
		double lat = geocoderResponse.getResults().get(0).getGeometry().getLocation().getLat().doubleValue();
		double lon = geocoderResponse.getResults().get(0).getGeometry().getLocation().getLng().doubleValue();
		GeoPoint ret = new GeoPoint(lat, lon, encodeGeohash(lat, lon));
		return ret;
	}

	@Override
	public void rebuildShoppingGeoPoints() throws ASException {
		Iterator<Shopping> i = shoppingDao.getAll(true).iterator();
		long startDate = new Date().getTime();
		int count = 0;

		while(i.hasNext()) {
			Shopping obj = i.next();
			try {
				updateShoppingGeoPoint(obj);
				count++;
			} catch(ASException e ) {
				log.log(Level.WARNING, "Invalid Geo Points defined for " + obj.getIdentifier(), e);
			}

		}

		long endDate = new Date().getTime();
		log.info("GeoPoints, " + count + " shoppings updated in " + (endDate - startDate) + " millis");

	}

	@Override
	public void rebuildShoppingGeoEntities() throws ASException {
		Iterator<Shopping> i = shoppingDao.getAll(true).iterator();
		long startDate = new Date().getTime();
		int count = 0;

		while(i.hasNext()) {
			Shopping obj = i.next();
			try {
				addGeoEntity(obj);
				count++;
			} catch(ASException e ) {
				if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_ALREADYEXISTS_CODE ) {
					log.log(Level.WARNING, "Invalid Geo Points defined for " + obj.getIdentifier(), e);
				}
			}

		}

		long endDate = new Date().getTime();
		log.info("GeoEntities, " + count + " shoppings updated in " + (endDate - startDate) + " millis");

	}

	@Override
	public void rebuildBrandGeoEntities() throws ASException {
		Iterator<Brand> i = brandDao.getAll(true).iterator();
		long startDate = new Date().getTime();
		int count = 0;

		while(i.hasNext()) {
			Brand obj = i.next();
			try {
				addGeoEntity(obj);
				count++;
			} catch(ASException e ) {
				log.log(Level.WARNING, "Invalid Geo Points defined for " + obj.getIdentifier(), e);
			}

		}

		long endDate = new Date().getTime();
		log.info("GeoEntities, " + count + " brands updated in " + (endDate - startDate) + " millis");

	}

	@Override
	public void rebuildOfferGeoEntities() throws ASException {
		Iterator<Offer> i = offerDao.getAll(true).iterator();
		long startDate = new Date().getTime();
		int count = 0;

		while(i.hasNext()) {
			Offer obj = i.next();
			try {
				addGeoEntity(obj);
				count++;
			} catch(ASException e ) {
				log.log(Level.WARNING, "Invalid Geo Points defined for " + obj.getIdentifier(), e);
			}

		}

		long endDate = new Date().getTime();
		log.info("GeoEntities, " + count + " offers updated in " + (endDate - startDate) + " millis");

	}

	@Override
	public void addUserDefaultLocation() throws ASException {
		Iterator<User> i = userDao.getAll(true).iterator();
		long startDate = new Date().getTime();
		int count = 0;

		while(i.hasNext()) {
			User obj = i.next();
			try {
				if( null == obj.getViewLocation() ) {
					obj.setViewLocation(new ViewLocation());
				}
				if( !StringUtils.hasText(obj.getViewLocation().getCountry())) {
					obj.getViewLocation().setCountry(systemConfiguration.getDefaultViewLocationCountry());
					userDao.updateWithoutChangingMail(obj);
				}
				count++;
			} catch(ASException e ) {
				log.log(Level.WARNING, "Updating user " + obj.getIdentifier(), e);
			}

		}

		long endDate = new Date().getTime();
		log.info("DefaultLocation, " + count + " users updated in " + (endDate - startDate) + " millis");
	}
	
	@Override
	public void calculateBrandLocationUsingStores(Brand brand) throws ASException {
		// this is not usefull anymore
	}
	
	@Override
	public void calculateStoreLocation(Store store) throws ASException {
		Shopping shopping = shoppingDao.get(store.getShoppingId(), true);
		if( null == store.getAddress() ) store.setAddress(new Address());
		store.getAddress().setCountry(shopping.getAddress().getCountry());
		storeDao.update(store);
	}
	
	@Override
	public void addStoreDefaultLocation() throws ASException {
		Iterator<Store> i = storeDao.getAll(true).iterator();
		long startDate = new Date().getTime();
		int count = 0;

		while(i.hasNext()) {
			Store obj = i.next();
			try {
				if( null == obj.getAddress()) {
					obj.setAddress(new Address());
				}
				if( !StringUtils.hasText(obj.getAddress().getCountry())) {
					obj.getAddress().setCountry(systemConfiguration.getDefaultViewLocationCountry());
					storeDao.update(obj);
				}
				count++;
			} catch(ASException e ) {
				log.log(Level.WARNING, "Updating store " + obj.getIdentifier(), e);
			}
		}

		long endDate = new Date().getTime();
		log.info("DefaultLocation, " + count + " stores updated in " + (endDate - startDate) + " millis");
	}

	@Override
	public void addBrandDefaultLocation() throws ASException {
		Iterator<Brand> i = brandDao.getAll(true).iterator();
		long startDate = new Date().getTime();
		int count = 0;

		while(i.hasNext()) {
			Brand obj = i.next();
			try {
				if(!StringUtils.hasText(obj.getCountry())) {
					obj.setCountry(systemConfiguration.getDefaultViewLocationCountry());
					brandDao.update(obj);
				}
				count++;
			} catch(ASException e ) {
				log.log(Level.WARNING, "Updating brand " + obj.getIdentifier(), e);
			}

		}

		long endDate = new Date().getTime();
		log.info("DefaultLocation, " + count + " brands updated in " + (endDate - startDate) + " millis");
	}

	@Override
	public void addOfferDefaultLocation() throws ASException {
		Iterator<Offer> i = offerDao.getAll(true).iterator();
		long startDate = new Date().getTime();
		int count = 0;

		while(i.hasNext()) {
			Offer obj = i.next();
			try {
				if(!StringUtils.hasText(obj.getCountry())) {
					obj.setCountry(systemConfiguration.getDefaultCountry());
					offerDao.update(obj);
				}
				count++;
			} catch(ASException e ) {
				log.log(Level.WARNING, "Updating offer " + obj.getIdentifier(), e);
			}

		}

		long endDate = new Date().getTime();
		log.info("DefaultLocation, " + count + " offers updated in " + (endDate - startDate) + " millis");
	}

	@Override
	public void updateShoppingGeoPoint(Shopping obj) throws ASException {
		StringBuffer sb = new StringBuffer();
		sb.append(obj.getAddress().getStreetName());
		sb.append(obj.getAddress().getStreetNumber()).append(",");
		sb.append(obj.getAddress().getCity()).append(",");
		sb.append(obj.getAddress().getProvince()).append(",");
		sb.append(obj.getAddress().getCountry()).append(",");

		try {
			GeoPoint p = getGeoPointFromAddress(sb.toString());
			obj.getAddress().setLatitude(p.getLat());
			obj.getAddress().setLongitude(p.getLon());
		} catch(ASException e ) {
			log.log(Level.WARNING, "Invalid Geo Points defined for " + obj.getIdentifier() + ": " + sb.toString(), e);
			throw e;
		} catch(Throwable t) {
			log.log(Level.SEVERE, t.getMessage(), t);
			throw ASExceptionHelper.defaultException(t.getMessage(), t);
		}
	}

	@Override
	public void addGeoEntity(Shopping obj) throws ASException {

		if( obj.getStatus() == StatusAware.STATUS_ENABLED ) {
			GeoEntity ge;
			List<GeoEntity> geList = geDao.getUsingEntityAndKind(obj.getIdentifier(), EntityKind.KIND_SHOPPING);

			if(geList.size() > 0 ) {
				ge = geList.get(0);
				if( obj.getAddress() != null ) {
					ge.setLastUpdate(new Date());
					ge.setLat(obj.getAddress().getLatitude());
					ge.setLon(obj.getAddress().getLongitude());
					ge.setGeohash(encodeGeohash(ge.getLat(), ge.getLon()));
					ge.setIndependent(true);
					geDao.update(ge);
				}
			} else {
				if( obj.getAddress() != null ) {
					ge = new GeoEntity();
					ge.setEntityId(obj.getIdentifier());
					ge.setEntityKind(EntityKind.resolveByClass(obj.getClass()));
					ge.setLastUpdate(new Date());
					ge.setLat(obj.getAddress().getLatitude());
					ge.setLon(obj.getAddress().getLongitude());
					ge.setGeohash(encodeGeohash(ge.getLat(), ge.getLon()));
					ge.setIndependent(true);
					ge.setKey(geDao.createKey());
					geDao.create(ge);
				}
			}
			
			List<Store> storeList = storeDao.getUsingShoppingAndStatus(obj.getIdentifier(), StatusHelper.statusActive(), null);
			for(Store store : storeList ) {
				addGeoEntity(store);
			}
			
		} else {
			removeGeoEntity(obj.getIdentifier(), EntityKind.KIND_SHOPPING);
			List<Store> storeList = storeDao.getUsingShoppingAndStatus(obj.getIdentifier(), StatusHelper.statusActive(), null);
			for(Store store : storeList ) {
				addGeoEntity(store);
			}
		}
	}

	@Override
	public void addGeoEntity(Store obj) throws ASException {

		boolean brandEnabled = true;
		boolean shoppingEnabled = true;
		
		try {
			Brand parentBrand = brandDao.get(obj.getBrandId(), true);
			brandEnabled = parentBrand.getStatus() == StatusAware.STATUS_ENABLED;
		} catch( Exception e ) {}
		
		try {
			Shopping parentShopping = shoppingDao.get(obj.getShoppingId(), true);
			shoppingEnabled = parentShopping.getStatus() == StatusAware.STATUS_ENABLED;
		} catch( Exception e ) {}
		
		if( obj.getStatus() == StatusAware.STATUS_ENABLED && brandEnabled && shoppingEnabled) {
			try {
				List<GeoEntity> geList = geDao.getUsingEntityAndKind(obj.getIdentifier(), EntityKind.KIND_STORE);

				if( StringUtils.hasText(obj.getShoppingId())) {
					// Shopping inside store
					GeoEntity shoppingGE = geDao.getUniqueUsingEntityAndKind(obj.getShoppingId(), EntityKind.KIND_SHOPPING);
					GeoEntity ge;

					if( geList.size() == 0 ) {
						ge = new GeoEntity();
						ge.setEntityId(obj.getIdentifier());
						ge.setEntityKind(EntityKind.KIND_STORE);
						ge.setLastUpdate(new Date());
						ge.setLat(shoppingGE.getLat());
						ge.setLon(shoppingGE.getLon());
						ge.setGeohash(shoppingGE.getGeohash());
						ge.setIndependent(false);
						ge.setKey(geDao.createKey());
						geDao.create(ge);
					}
				} else {
					// Street Store
					GeoEntity ge;

					for( GeoEntity geo : geList ) {
						geDao.delete(geo);
					}

					ge = new GeoEntity();
					ge.setEntityId(obj.getIdentifier());
					ge.setEntityKind(EntityKind.KIND_STORE);
					ge.setLastUpdate(new Date());
					ge.setLat(obj.getAddress().getLatitude());
					ge.setLon(obj.getAddress().getLongitude());
					ge.setGeohash(encodeGeohash(ge.getLat(), ge.getLon()));
					ge.setIndependent(true);
					ge.setKey(geDao.createKey());
					geDao.create(ge);
				}
				
			} catch( ASException e ) {
				if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
					log.log(Level.SEVERE, e.getMessage(), e);
					throw e;
				}
			}

		} else {
			removeGeoEntity(obj.getIdentifier(), EntityKind.KIND_STORE);
		}

		// Activate all associated offers from this store
		List<Offer> associatedOffers = offerDao.getActiveUsingBrandAndRange(obj.getBrandId(), null);
		for( Offer offer : associatedOffers ) {
			addGeoEntity(offer);
		}

	}

	@Override
	public void addGeoEntity(Brand obj) throws ASException {

		int count = 0;
		long startDate = new Date().getTime();

		Iterator<Store> i = storeDao.getUsingBrandAndStatus(
				obj.getIdentifier(), StatusHelper.statusNotDisabled(), null)
				.iterator();
		while(i.hasNext()) {
			Store store = i.next();
			try {
				addGeoEntity(store);
				count++;
			} catch( ASException e ) {
				if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_ALREADYEXISTS_CODE 
						&& e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) 
					throw e;
			}
		}

		long endDate = new Date().getTime();
		log.info("GeoEntities, " + count + " stores updated for brand " + obj.getName() + " in " + (endDate - startDate) + " millis");
	}

	@Override
	public void addGeoEntity(Offer obj) throws ASException {
		
		if( obj == null ) throw ASExceptionHelper.invalidArgumentsException(); 
		if( obj.getValidFrom().after(new Date()) || obj.getValidTo().before(new Date())) return;
		if( geDao.getUsingEntityAndKind(obj.getIdentifier(), EntityKind.KIND_OFFER).size() > 0 ) 
			removeGeoEntity(obj.getIdentifier(), EntityKind.KIND_OFFER);

		int count = 0;
		long startDate = new Date().getTime();

		Set<GeoPoint> geos = CollectionFactory.createSet();

		// First, get all the geo entities by shopping
		Iterator<String> i = obj.getShoppings().iterator();
		while(i.hasNext()) {
			String identifier = i.next();
			Iterator<GeoEntity> l = geDao.getUsingEntityAndKind(identifier, EntityKind.KIND_SHOPPING).iterator();
			while(l.hasNext()) {
				GeoEntity geo = l.next();
				geos.add(geo.getGeoPoint());
			}
		}

		// We must decide... if we have store list... or a brand list
		if( obj.getStores().size() > 0 ) {
			Iterator<String> stores = obj.getStores().iterator();
			while(stores.hasNext()) {
				Iterator<GeoEntity> l = geDao.getUsingEntityAndKind(stores.next(), EntityKind.KIND_STORE).iterator();
				while(l.hasNext()) {
					GeoEntity geo = l.next();
					geos.add(geo.getGeoPoint());
				}
			}
		} else {
			// Now, we scan the brands
			Iterator<String> brands = obj.getBrands().iterator();
			while(brands.hasNext()) {
				String brand = brands.next();
				Iterator<Store> stores = storeDao.getUsingBrandAndStatus(brand, StatusHelper.statusActive(), null).iterator();
				while(stores.hasNext()) {
					Store store = stores.next();
					Iterator<GeoEntity> l = geDao.getUsingEntityAndKind(store.getIdentifier(), EntityKind.KIND_STORE).iterator();
					while(l.hasNext()) {
						GeoEntity geo = l.next();
						geos.add(geo.getGeoPoint());
					}
				}
			}
		}

		// OK, now I get all the available geo points.... so I will create the entities;
		count = 0;
		Iterator<GeoPoint> p = geos.iterator();
		while(p.hasNext()) {
			GeoPoint gp = p.next();
			GeoEntity ge = new GeoEntity();
			ge.setEntityId(obj.getIdentifier());
			ge.setEntityKind(EntityKind.KIND_OFFER);
			ge.setLastUpdate(new Date());
			ge.setLat(gp.getLat());
			ge.setLon(gp.getLon());
			ge.setGeohash(gp.getGeohash());
			ge.setKey(geDao.createKey());
			geDao.create(ge);
			count++;
		}

		long endDate = new Date().getTime();
		log.info("GeoEntities, " + count + " offer points updated in " + (endDate - startDate) + " millis");
	}

	@Override
	public void evictDeadOffers() throws ASException {
		Iterator<Offer> i = offerDao.getAll(true).iterator();
		while(i.hasNext()) {
			Offer obj = i.next();
			if( obj.getValidTo().before(new Date())) {
				try {
					Iterator<GeoEntity> i2 = geDao.getUsingEntityAndKind(obj.getIdentifier(), EntityKind.KIND_OFFER).iterator();
					while(i2.hasNext()) {
						GeoEntity ge = i2.next();
						geDao.delete(ge);
					}
				} catch(ASException e ) {
					if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
						throw e;
					}
				}
			}
		}
	}

	@Override
	public GeoPoint getGeoPoint(double lat, double lon) throws ASException {
		GeoPoint gp = new GeoPoint();
		gp.setLat(lat);
		gp.setLon(lon);
		gp.setGeohash(encodeGeohash(lat, lon));
		return gp;
	}

	@Override
	public Integer calculateDistance(double myLat, double myLon,
			double otherLat, double otherLon) {

		final int R = 6371; // Radius of the earth

		Double latDistance = deg2rad(otherLat - myLat);
		Double lonDistance = deg2rad(otherLon- myLon);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(deg2rad(myLat)) * Math.cos(deg2rad(otherLat))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters
		return (int)distance;
	}


	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	@Override
	public void removeGeoEntity(String identifier, Integer entityKind) throws ASException {
		List<GeoEntity> l = geDao.getUsingEntityAndKind(identifier, entityKind);
		for( GeoEntity ge : l ) {
			geDao.delete(ge);
		}
	}

	@Override
	public void addGeoEntity(String identifier, Integer entityKind)
			throws ASException {

		if(!StringUtils.hasText(identifier) || entityKind == null ) throw ASExceptionHelper.forbiddenException();

		switch( entityKind ) {
		case EntityKind.KIND_SHOPPING:
		{
			Shopping obj = shoppingDao.get(identifier, true);
			addGeoEntity(obj);
			break;
		}
		case EntityKind.KIND_OFFER:
		{
			Offer obj = offerDao.get(identifier, true);
			addGeoEntity(obj);
			break;
		}
		case EntityKind.KIND_BRAND:
		{
			Brand obj = brandDao.get(identifier, true);
			addGeoEntity(obj);
			break;
		}
		case EntityKind.KIND_STORE:
		{
			Store obj = storeDao.get(identifier, true);
			addGeoEntity(obj);
			break;
		}
		}
	}
	
	@Override
	public boolean isUserNearShopping(User user, Set<String>candidates) {
		
		try {
			List<DeviceLocation> locs = dlDao
					.getUsingUserIdentifierAndLastUpdate(user.getIdentifier(),
							new Date(new Date().getTime() - ONE_DAY));
			List<Shopping> list = shoppingDao.getUsingIdList(CollectionFactory.createList(candidates));

			// Checks in range for every shopping location and every device location
			for (Shopping shopping : list) {
				for (DeviceLocation loc : locs) {
					if (calculateDistance(loc.getLat(), loc.getLon(), shopping
							.getAddress().getLatitude(), shopping.getAddress()
							.getLongitude()) < shopping.getFenceSize()) {
						return true;
					}
				}
			}
		} catch( Exception e ) {
			if(!( e instanceof ASException ) || (((ASException)e).getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE))
				log.log(Level.SEVERE, e.getMessage(), e);
		}

		return false;
	}

	@Override
	public String getCountryUsingCityAndState(String cityCommaState) throws IOException, URISyntaxException {
		String s = new URI("http://api.geonames.org/search?q="
				+ URLParamEncoder.encode(org.apache.commons.lang3.StringUtils.stripAccents(cityCommaState))
				+ "&featureClass=P&username=myusername").toASCIIString();
		URL url = new URL(s);
	    byte[] bContents = IOUtils.toByteArray(url.openStream());
	    String data = new String(bContents);
	    String[] parts1 = data.split("<countryName>");
	    String[] parts2 = parts1[1].split("</countryName>");
	    return parts2[0];
	}

	/**
	 * Gets a GeoPoint from the possible arguments that can determine it. In
	 * this case, it tries to find a GeoPoint from lat and lon sent from a
	 * user, but if the user doesn't have those attributes (no GPS perhaps?) then
	 * tries to find the GeoPoint from the last known location of the user
	 * device.
	 * 
	 * @param deviceUUID
	 *            User Device UUID
	 * @param lat
	 *            User provided latitude
	 * @param lon
	 *            User provided longitude
	 * @return A fully functional GeoPoint, or null if the parameters sent were
	 *         not enough to calculate one
	 * @throws ASException
	 */
	@Override
	public GeoPoint getGeoPoint(Double lat, Double lon, String deviceUUID) throws ASException {

		boolean hasLocation = true;
		if(( lat == null || lat == 0 || lon == null || lon == 0 ) && (StringUtils.hasText(deviceUUID))) {
			try {
				DeviceLocation loc = dlDao.get(deviceUUID, true);
				lat = loc.getLat();
				lon = loc.getLon();
			} catch( ASException e ) {
				if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
					hasLocation = false;
				}
			}
		}

		if( lat == null || lat == 0 || lon == null || lon == 0 ) 
			hasLocation = false;

		if(hasLocation) {
			GeoPoint gp = getGeoPoint(lat, lon);
			return gp;
		} else {
			return null;
		}
	}

	@Override
	public Address getAddressUsingGeohash(String geohash) throws IOException, URISyntaxException {
		GeoPoint point = decodeGeohash(geohash);
		String s1 = new URI("http://api.geonames.org/findNearbyPostalCodesJSON?lat=" + point.getLat() + "&lng=" + point.getLon() + "&username=allshoppings").toASCIIString();
		URL url1 = new URL(s1);
	    byte[] bContents1 = IOUtils.toByteArray(url1.openStream());
	    JSONObject data1 = new JSONObject(new String(bContents1));
	    
	    String s2 = new URI("http://api.geonames.org/findNearbyPlaceNameJSON?lat=" + point.getLat() + "&lng=" + point.getLon() + "&username=allshoppings").toASCIIString();
		URL url2 = new URL(s2);
	    byte[] bContents2 = IOUtils.toByteArray(url2.openStream());
	    JSONObject data2 = new JSONObject(new String(bContents2));

	    Address obj = new Address();
	    obj.setLatitude(point.getLat());
	    obj.setLongitude(point.getLon());

	    JSONObject cpData = (JSONObject)data1.getJSONArray("postalCodes").get(0);
	    JSONObject ctData = (JSONObject)data2.getJSONArray("geonames").get(0);

	    obj.setZipCode(cpData.getString("postalCode"));
	    obj.setNeighborhood(cpData.getString("placeName"));
	    obj.setCity(cpData.getString("adminName2"));
	    obj.setProvince(cpData.getString("adminName1"));
	    obj.setCountry(ctData.getString("countryName"));
	    
	    return obj;
	}

	@Override
	public AddressComponentsCache getAddressHLComponents(double lat, double lon) throws ASException {
		String geoHash = encodeGeohash(lat, lon).substring(0,5);
		AddressComponentsCache acc = null;
		try {
			acc = accDao.get(geoHash, true);
		} catch( ASException e ) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
				acc = getAddressHLComponentsInternal(lat, lon);
				acc.setKey(accDao.createKey(geoHash));
				accDao.create(acc);
			}
		}
		
		return acc;
		
	}	
	
	private AddressComponentsCache getAddressHLComponentsInternal(double lat, double lon) throws ASException {
		AddressComponentsCache ret = new AddressComponentsCache();

		try {
			URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" 
					+ lat + "," + lon + "&key=AIzaSyCQ__rn2cT0gWdqGGsw5G1VkjG8np9WhEA");
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer sb = new StringBuffer();
			String line;

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();

			JSONObject json = new JSONObject(sb.toString());
			JSONArray arr = json.getJSONArray("results");
			for( int i = 0; i < arr.length(); i++ ) {
				JSONObject obj = arr.getJSONObject(i);
				JSONArray addressComponents = obj.getJSONArray("address_components");
				for( int j = 0; j < addressComponents.length(); j++ ) {
					JSONObject component = addressComponents.getJSONObject(j);
					JSONArray types = component.getJSONArray("types");
					for( int k = 0; k < types.length(); k++ ) {
						if( types.getString(0).equals("country"))
							ret.setCountry(component.getString("long_name"));
						if( types.getString(0).equals("locality") || types.getString(0).equals("administrative_area_level_2"))
							ret.setCity(component.getString("long_name"));
						if( types.getString(0).equals("administrative_area_level_1"))
							ret.setProvince(component.getString("long_name"));
						if( types.getString(0).equals("neighborhood"))
							ret.setNeighborhood(component.getString("long_name"));
					}
				}
			}
			return ret;
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	@Override
	public String[] getAdjacentPoints(GeoPoint geo, int presition) {
		String[] boxes = new String[9];
		boxes[GeoCodingHelperGMapsImpl.CENTER] = geo.getGeohash().substring(0, presition);
		boxes[GeoCodingHelperGMapsImpl.TOP] = GeoCodingHelperGMapsImpl.calculateAdjancent(boxes[GeoCodingHelperGMapsImpl.CENTER], GeoCodingHelperGMapsImpl.TOP);
		boxes[GeoCodingHelperGMapsImpl.BOTTOM] = GeoCodingHelperGMapsImpl.calculateAdjancent(boxes[GeoCodingHelperGMapsImpl.CENTER], GeoCodingHelperGMapsImpl.BOTTOM);
		boxes[GeoCodingHelperGMapsImpl.LEFT] = GeoCodingHelperGMapsImpl.calculateAdjancent(boxes[GeoCodingHelperGMapsImpl.CENTER], GeoCodingHelperGMapsImpl.LEFT);
		boxes[GeoCodingHelperGMapsImpl.RIGHT] = GeoCodingHelperGMapsImpl.calculateAdjancent(boxes[GeoCodingHelperGMapsImpl.CENTER], GeoCodingHelperGMapsImpl.RIGHT);
		boxes[GeoCodingHelperGMapsImpl.TOPLEFT] = GeoCodingHelperGMapsImpl.calculateAdjancent(boxes[GeoCodingHelperGMapsImpl.LEFT], GeoCodingHelperGMapsImpl.TOP);
		boxes[GeoCodingHelperGMapsImpl.TOPRIGHT] = GeoCodingHelperGMapsImpl.calculateAdjancent(boxes[GeoCodingHelperGMapsImpl.RIGHT], GeoCodingHelperGMapsImpl.TOP);
		boxes[GeoCodingHelperGMapsImpl.BOTTOMLEFT] = GeoCodingHelperGMapsImpl.calculateAdjancent(boxes[GeoCodingHelperGMapsImpl.LEFT], GeoCodingHelperGMapsImpl.BOTTOM);
		boxes[GeoCodingHelperGMapsImpl.BOTTOMRIGHT] = GeoCodingHelperGMapsImpl.calculateAdjancent(boxes[GeoCodingHelperGMapsImpl.RIGHT], GeoCodingHelperGMapsImpl.BOTTOM);
		return boxes;
	}
	
}
