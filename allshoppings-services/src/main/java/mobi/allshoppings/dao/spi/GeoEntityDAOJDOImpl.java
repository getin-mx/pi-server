package mobi.allshoppings.dao.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.GeoEntityDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.DistanceComparator;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.geocoding.impl.GeoCodingHelperGMapsImpl;
import mobi.allshoppings.model.GeoEntity;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.adapter.LocationAwareAdapter;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tx.PersistenceProvider;

public class GeoEntityDAOJDOImpl extends GenericDAOJDO<GeoEntity> implements GeoEntityDAO {

	private static final Logger log = Logger.getLogger(GeoEntityDAOJDOImpl.class.getName());
	
	private KeyHelper keyHelper = new KeyHelperGaeImpl();
	private GeoCodingHelper geocoder = new GeoCodingHelperGMapsImpl();

	public GeoEntityDAOJDOImpl() {
		super(GeoEntity.class);
	}
	
	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(GeoEntity.class);
	}

	@Override
	public List<GeoEntity> getUsingEntityAndKind(String identifier, byte entityKind) throws ASException {
		return getUsingEntityAndKind(null, identifier, entityKind, true);
	}

	@Override
	public List<GeoEntity> getUsingEntityAndKind(String identifier, byte entityKind, boolean detachable) throws ASException {
		return getUsingEntityAndKind(null, identifier, entityKind, detachable);
	}

	@Override
	public List<GeoEntity> getUsingEntityAndKind(PersistenceProvider pp, String identifier, byte entityKind, boolean detachable) throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<GeoEntity> ret = CollectionFactory.createList();
		
		try {
			Query query = pm.newQuery(GeoEntity.class);

			// Parameter Declaration
			StringBuffer parametersDeclaration = new StringBuffer();
			parametersDeclaration.append("String entityIdParam, Integer entityKindParam");
			query.declareParameters(parametersDeclaration.toString());
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("entityIdParam", identifier);
			parameters.put("entityKindParam", entityKind);
			
			// Filter declaration
			StringBuffer filter = new StringBuffer();
			filter.append("entityId == entityIdParam && entityKind == entityKindParam");
			query.setFilter(filter.toString());
			
			// Executes the query
			@SuppressWarnings("unchecked")
			List<GeoEntity> objs = (List<GeoEntity>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (GeoEntity obj : objs) {
					if( detachable ) 
						ret.add(pm.detachCopy(obj));
					else
						ret.add(obj);
				}
			}
			
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}
		
		return ret;

	}

	@Override
	public GeoEntity getUniqueUsingEntityAndKind(String entityId, byte entityKind)
			throws ASException {
		List<GeoEntity> l = getUsingEntityAndKind(entityId, entityKind);
		if( l.size() != 1 ) {
			log.log(Level.SEVERE, "Geo entity incorrect for entity id " + entityId + " and entityKind " + entityKind);
			if( l.size() == 0 ) throw ASExceptionHelper.notFoundException();
			if( l.size() > 1 ) throw ASExceptionHelper.notUniqueException();
		}
		return(l.get(0));
	}

	@Override
	public 	List<GeoEntity> getByProximity(GeoPoint geo, byte entityKind, int presition, boolean includeAdjacents, boolean independentOnly, boolean detachable) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<GeoEntity> ret = CollectionFactory.createList();
		String[] boxes = includeAdjacents ? new String[9] : new String[1];
		
		try {
			if( includeAdjacents ) {
				boxes[GeoCodingHelperGMapsImpl.CENTER] = geo.getGeohash().substring(0, geo.getGeohash().length() - (presition  + 1));
				boxes[GeoCodingHelperGMapsImpl.TOP] = GeoCodingHelperGMapsImpl.calculateAdjancent(boxes[GeoCodingHelperGMapsImpl.CENTER], GeoCodingHelperGMapsImpl.TOP);
				boxes[GeoCodingHelperGMapsImpl.BOTTOM] = GeoCodingHelperGMapsImpl.calculateAdjancent(boxes[GeoCodingHelperGMapsImpl.CENTER], GeoCodingHelperGMapsImpl.BOTTOM);
				boxes[GeoCodingHelperGMapsImpl.LEFT] = GeoCodingHelperGMapsImpl.calculateAdjancent(boxes[GeoCodingHelperGMapsImpl.CENTER], GeoCodingHelperGMapsImpl.LEFT);
				boxes[GeoCodingHelperGMapsImpl.RIGHT] = GeoCodingHelperGMapsImpl.calculateAdjancent(boxes[GeoCodingHelperGMapsImpl.CENTER], GeoCodingHelperGMapsImpl.RIGHT);
				boxes[GeoCodingHelperGMapsImpl.TOPLEFT] = GeoCodingHelperGMapsImpl.calculateAdjancent(boxes[GeoCodingHelperGMapsImpl.LEFT], GeoCodingHelperGMapsImpl.TOP);
				boxes[GeoCodingHelperGMapsImpl.TOPRIGHT] = GeoCodingHelperGMapsImpl.calculateAdjancent(boxes[GeoCodingHelperGMapsImpl.RIGHT], GeoCodingHelperGMapsImpl.TOP);
				boxes[GeoCodingHelperGMapsImpl.BOTTOMLEFT] = GeoCodingHelperGMapsImpl.calculateAdjancent(boxes[GeoCodingHelperGMapsImpl.LEFT], GeoCodingHelperGMapsImpl.BOTTOM);
				boxes[GeoCodingHelperGMapsImpl.BOTTOMRIGHT] = GeoCodingHelperGMapsImpl.calculateAdjancent(boxes[GeoCodingHelperGMapsImpl.RIGHT], GeoCodingHelperGMapsImpl.BOTTOM);
			} else {
				boxes[0] = geo.getGeohash().substring(0, geo.getGeohash().length() - (presition  + 1));
			}

			for( int i = 0; i < boxes.length; i++ ) {
				if( StringUtils.hasLength(boxes[i])) {
					Query query = pm.newQuery(GeoEntity.class);

					// Filter declaration
					StringBuffer filter = new StringBuffer();
					filter.append("geohash").append(".matches('").append(boxes[i]).append(".*')");
					filter.append(" && entityKind == " + entityKind);
					if( independentOnly ) filter.append(" && independent == true");
					query.setFilter(filter.toString());

					// Executes the query
					@SuppressWarnings("unchecked")
					List<GeoEntity> objs = (List<GeoEntity>)query.execute();
					if (objs != null) {
						// force to read
						for (GeoEntity obj : objs) {
							if(!ret.contains(obj)) {
								if( detachable ) 
									ret.add(pm.detachCopy(obj));
								else
									ret.add(obj);
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}
		
		return ret;
	}

	@Override
	public Shopping getNearestShopping(GeoPoint geo) throws ASException {
		return getNearestShopping(geo, true);
	}

	@Override
	public Shopping getNearestShopping(GeoPoint geo, boolean detachable)
			throws ASException {

		return null;
	}

	@Override
	public LocationAwareAdapter getNearestInterestingPoint(GeoPoint geo) throws ASException {
		return getNearestInterestingPoint(geo, true);
	}

	@Override
	public LocationAwareAdapter getNearestInterestingPoint(GeoPoint geo, boolean detachable)
			throws ASException {
		
//		String cacheKey = "nearestInterestingPoint_" + geo.getGeohash().substring(0,6);
//		LocationAwareAdapter bsx = cacheHelper != null ? (LocationAwareAdapter)cacheHelper.get(cacheKey) : null;
//		if( null != bsx ) return bsx;
		
//		List<GeoEntity> geoShoppings = null;
//		List<GeoEntity> geoStores = null;
		List<GeoEntity> geos = new ArrayList<GeoEntity>();
		
		log.log(Level.FINE, "Finding Nearest Interesting Points begins");
		long start = new Date().getTime();

		// Find Shoppings
//		for( int i = 7; i < 13; i++ ) {
//			geoShoppings = getByProximity(geo, EntityKind.KIND_SHOPPING, i, false, true, detachable);
//			if( geoShoppings != null && geoShoppings.size() > 0 ) i = 14;
//		}
		
//		if( geoShoppings == null || geoShoppings.size() == 0 ) {
//			for( int i = 11; i < 13; i++ ) {
//				geoShoppings = getByProximity(geo, EntityKind.KIND_SHOPPING, i, true, true, detachable);
//				if( geoShoppings != null && geoShoppings.size() > 0 ) i = 14;
//			}
//		}
		
		// Find Stores
//		for( int i = 7; i < 13; i++ ) {
//			geoStores = getByProximity(geo, EntityKind.KIND_STORE, i, false, true, detachable);
//			if( geoStores != null && geoStores.size() > 0 ) i = 14;
//		}
		
//		if( geoStores == null || geoStores.size() == 0 ) {
//			for( int i = 11; i < 13; i++ ) {
//				geoStores = getByProximity(geo, EntityKind.KIND_STORE, i, true, true, detachable);
//				if( geoStores != null && geoStores.size() > 0 ) i = 14;
//			}
//		}

		// Merge the two lists
//		if( geoShoppings != null ) geos.addAll(geoShoppings);
//		if( geoStores    != null ) geos.addAll(geoStores);
		
		if( geos == null || geos.size() == 0 )
			throw ASExceptionHelper.notFoundException();

		List<LocationAwareAdapter> adaptedGeos = CollectionFactory.createList();
		for( GeoEntity obj : geos ) {
			LocationAwareAdapter adapter = new LocationAwareAdapter();
			adapter.setIdentifier(obj.getEntityId());
			adapter.setKind(obj.getEntityKind());
			adapter.setLat(obj.getLat());
			adapter.setLon(obj.getLon());
			adapter.setDistance(geocoder.calculateDistance(geo.getLat(), geo.getLon(), adapter.getLat(), adapter.getLon()));
			adaptedGeos.add(adapter);
		}
		Collections.sort(adaptedGeos, new DistanceComparator());

		long end = new Date().getTime();
		log.log(Level.FINE, "Found nearest interesting point in " + (end - start) + "ms");
		
		// Complete adaptations
//		LocationAwareAdapter adapter = adaptedGeos.get(0);

//		if( adapter.getKind() == EntityKind.KIND_SHOPPING ) {
//			Shopping s = shoppingDao.get(adapter.getIdentifier(), true);
//			adapter.setAvatarId(s.getAvatarId());
//			adapter.setName(s.getName());
//		}
//		
//		if( adapter.getKind() == EntityKind.KIND_STORE ) {
//			Store s = storeDao.get(adapter.getIdentifier(), true);
//			adapter.setAvatarId(s.getAvatarId());
//			adapter.setName(s.getName());
//		}
		
//		if(cacheHelper != null) cacheHelper.put(cacheKey, adaptedGeos.get(0));
		return adaptedGeos.get(0);
	}

}
