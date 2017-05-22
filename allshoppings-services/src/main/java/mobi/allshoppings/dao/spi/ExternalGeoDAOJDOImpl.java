package mobi.allshoppings.dao.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.datastore.JDOConnection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Cursor;
import com.inodes.datanucleus.model.JDOCursorHelper;
import com.inodes.datanucleus.model.Key;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;

import mobi.allshoppings.dao.ExternalGeoDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.ExternalGeo;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CollectionUtils;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public class ExternalGeoDAOJDOImpl extends GenericDAOJDO<ExternalGeo> implements ExternalGeoDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ExternalGeoDAOJDOImpl.class.getName());
	@Autowired
	private GeoCodingHelper geocoder;

	public ExternalGeoDAOJDOImpl() {
		super(ExternalGeo.class);
	}

	@Override
	public Key createKey(ExternalGeo obj) throws ASException {
		String seed = getHash(obj.getLat(), obj.getLon(), obj.getPeriod(), obj.getEntityId(), obj.getEntityKind(), obj.getType());
		return keyHelper.createStringUniqueKey(ExternalGeo.class, seed);
	}

	@Override
	public String getHash(Float lat, Float lon, String period, String entityId, Integer entityKind, Integer type) {
		String seed = geocoder.encodeGeohash(lat, lon).substring(0,7)
				+ ":" + period
				+ ":" + entityId
				+ ":" + entityKind
				+ ":" + type;
		return seed;
	}

	@Override
	public int getType(int checkinType, int hour) {
		if( checkinType == APDVisit.CHECKIN_PEASANT ) {
			if( hour >= 2 && hour <= 5 ) {
				return ExternalGeo.TYPE_GPS_HOME_PEASANT;
			} else {
				return ExternalGeo.TYPE_GPS_WORK_PEASANT;
			}
		} else {
			if( hour >= 2 && hour <= 5 ) {
				return ExternalGeo.TYPE_GPS_HOME;
			} else {
				return ExternalGeo.TYPE_GPS_WORK;
			}
		}
	}
	
	@Override
	public List<ExternalGeo> getUsingVenueAndPeriod(PersistenceProvider pp, String venue, String period, Range range, String order, boolean detachable) throws ASException {

		List<ExternalGeo> returnedObjs = CollectionFactory.createList();
		
		PersistenceManager pm;
		if( pp == null ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(ExternalGeo.class);

			// parameters
			if( StringUtils.hasText(venue)) {
				declaredParams.add("String venueParam");
				filters.add("venue == venueParam");
				parameters.put("venueParam", venue);
			}
			if( StringUtils.hasText(period)) {
				declaredParams.add("String periodParam");
				filters.add("period == periodParam");
				parameters.put("periodParam", period);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			// Adds a cursor to the ranged query
			if( range != null ) {
				if( StringUtils.hasText(range.getCursor())) {
					// Query q = the same query that produced the cursor
					// String cursorString = the string from storage
					Cursor cursor = Cursor.fromWebSafeString(range.getCursor());
					Map<String, Object> extensionMap = new HashMap<String, Object>();
					extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
					query.setExtensions(extensionMap);
					query.setRange(0, (range.getTo() - range.getFrom()));
				} else {
					query.setRange(range.getFrom(), range.getTo());
				}
			}

			// Sets order as required
			if( StringUtils.hasText(order)) query.setOrdering(order);

			@SuppressWarnings("unchecked")
			List<ExternalGeo> objs = (List<ExternalGeo>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (ExternalGeo obj : objs) {
					if( detachable )
						returnedObjs.add(pm.detachCopy(obj));
					else
						returnedObjs.add(obj);
				}
			}

		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			if( null == pp ) pm.close();
		}

		return returnedObjs;

	}

	@Override
	public String getEntityIdAndPeriods(PersistenceProvider pp, String entityId, Integer entityKind, Integer type, String period) throws ASException {

		JSONArray returnedObjs = new JSONArray();
		
		PersistenceManager pm;
		if( pp == null ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(ExternalGeo.class);

			// parameters
			if( StringUtils.hasText(entityId)) {
				declaredParams.add("String entityIdParam");
				filters.add("entityId == entityIdParam");
				parameters.put("entityIdParam", entityId);
			}
			if( entityKind != null ) {
				declaredParams.add("Integer entityKindParam");
				filters.add("entityKind == entityKindParam");
				parameters.put("entityKindParam", entityKind);
			}
			if( type != null ) {
				declaredParams.add("Integer typeParam");
				filters.add("type == typeParam");
				parameters.put("typeParam", type);
			}
			if( StringUtils.hasText(period)) {
				declaredParams.add("String periodParam");
				filters.add("period == periodParam");
				parameters.put("periodParam", period);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			query.setResult("distinct venue, period");

			@SuppressWarnings("unchecked")
			Collection<Object[]> objs = (Collection<Object[]>)query.executeWithMap(parameters);
			Iterator<Object[]> i = objs.iterator();
			while(i.hasNext()) {
				Object[] row = (Object[])i.next();
				JSONObject json = new JSONObject();
				json.put("venue", (String)row[0]);
				json.put("period", (String)row[1]);
				returnedObjs.put(json);
			}
			
		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			if( null == pp ) pm.close();
		}

		return returnedObjs.toString();

	}

	@Override
	public List<ExternalGeo> getUsingEntityIdAndPeriod(PersistenceProvider pp, String entityId, Integer entityKind, Integer type, String period, Range range, String order, boolean detachable) throws ASException {

		List<ExternalGeo> returnedObjs = CollectionFactory.createList();
		
		PersistenceManager pm;
		if( pp == null ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(ExternalGeo.class);

			// parameters
			if( StringUtils.hasText(entityId)) {
				declaredParams.add("String entityIdParam");
				filters.add("entityId == entityIdParam");
				parameters.put("entityIdParam", entityId);
			}
			if( entityKind != null ) {
				declaredParams.add("Integer entityKindParam");
				filters.add("entityKind == entityKindParam");
				parameters.put("entityKindParam", entityKind);
			}
			if( type != null ) {
				declaredParams.add("Integer typeParam");
				filters.add("type == typeParam");
				parameters.put("typeParam", type);
			}
			if( StringUtils.hasText(period)) {
				declaredParams.add("String periodParam");
				filters.add("period == periodParam");
				parameters.put("periodParam", period);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			// Adds a cursor to the ranged query
			if( range != null ) {
				if( StringUtils.hasText(range.getCursor())) {
					// Query q = the same query that produced the cursor
					// String cursorString = the string from storage
					Cursor cursor = Cursor.fromWebSafeString(range.getCursor());
					Map<String, Object> extensionMap = new HashMap<String, Object>();
					extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
					query.setExtensions(extensionMap);
					query.setRange(0, (range.getTo() - range.getFrom()));
				} else {
					query.setRange(range.getFrom(), range.getTo());
				}
			}

			// Sets order as required
			if( StringUtils.hasText(order)) query.setOrdering(order);

			@SuppressWarnings("unchecked")
			List<ExternalGeo> objs = (List<ExternalGeo>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (ExternalGeo obj : objs) {
					if( detachable )
						returnedObjs.add(pm.detachCopy(obj));
					else
						returnedObjs.add(obj);
				}
			}

		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			if( null == pp ) pm.close();
		}

		return returnedObjs;

	}

	@Override
	public String getVenuesAndPeriods(PersistenceProvider pp, String venue, String period) throws ASException {

		JSONArray returnedObjs = new JSONArray();
		
		PersistenceManager pm;
		if( pp == null ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(ExternalGeo.class);

			// parameters
			if( StringUtils.hasText(venue)) {
				declaredParams.add("String venueParam");
				filters.add("venue == venueParam");
				parameters.put("venueParam", venue);
			}
			if( StringUtils.hasText(period)) {
				declaredParams.add("String periodParam");
				filters.add("period == periodParam");
				parameters.put("periodParam", period);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			query.setResult("distinct venue, period");

			@SuppressWarnings("unchecked")
			Collection<Object[]> objs = (Collection<Object[]>)query.executeWithMap(parameters);
			Iterator<Object[]> i = objs.iterator();
			while(i.hasNext()) {
				Object[] row = (Object[])i.next();
				JSONObject json = new JSONObject();
				json.put("venue", (String)row[0]);
				json.put("period", (String)row[1]);
				returnedObjs.put(json);
			}
			
		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			if( null == pp ) pm.close();
		}

		return returnedObjs.toString();

	}

	@Override
	public void deleteUsingEntityIdAndPeriod(PersistenceProvider pp, List<String> entityId, Integer entityKind, Integer type, String period) throws ASException {
		
		PersistenceManager pm;
		if( pp == null ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}

		try{

			// Obtains DB Connection
			JDOConnection jdoConn = pm.getDataStoreConnection();
			DB db = (DB)jdoConn.getNativeConnection();

			// Set one, generate first filter
			List<BasicDBObject> parts = CollectionFactory.createList();
			if( !CollectionUtils.isEmpty(entityId))
				parts.add(new BasicDBObject("entityId", new BasicDBObject("$in", entityId.toArray(new String[entityId.size()]))));
			if(null != entityKind)
				parts.add(new BasicDBObject("entityKind", entityKind));
			if(null != type)
				parts.add(new BasicDBObject("type", type));
			if( StringUtils.hasText(period))
				parts.add(new BasicDBObject("period", period));
			BasicDBObject query = new BasicDBObject("$and", parts);

			db.getCollection("ExternalGeo").remove(query);
			jdoConn.close();
			
			pm.evictAll(true, APDVisit.class);
						
			return;

		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			if( null == pp ) pm.close();
		}

	}
}
