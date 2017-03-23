package mobi.allshoppings.dao.spi;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.datastore.JDOConnection;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;

import mobi.allshoppings.dao.APDVisitDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

public class APDVisitDAOJDOImpl extends GenericDAOJDO<APDVisit> implements APDVisitDAO {

	private static final Logger log = Logger.getLogger(APDVisitDAOJDOImpl.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
	
	public APDVisitDAOJDOImpl() {
		super(APDVisit.class);
	}

	@Override
	public Key createKey(APDVisit obj) throws ASException {
		return keyHelper.createStringUniqueKey(APDVisit.class, obj.getMac() + ":" + obj.getEntityId() + ":"
				+ obj.getEntityKind() + ":" + obj.getCheckinType() + ":" + sdf.format(obj.getCheckinStarted()));
	}

	@Override
	public List<APDVisit> getUsingEntityIdAndEntityKindAndDate(String entityId, Integer entityKind, Date fromDate,
			Date toDate, Integer checkinType, Range range, String order, Map<String, String> attributes, boolean detachable)
			throws ASException {

		List<APDVisit> ret = CollectionFactory.createList();
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// entityId Parameter
			if(StringUtils.hasText(entityId)) {
				declaredParams.add("String entityIdParam");
				filters.add("entityId == entityIdParam");
				parameters.put("entityIdParam", entityId);
			}

			// entityKind Parameter
			if(null != entityKind) {
				declaredParams.add("Integer entityKindParam");
				filters.add("entityKind == entityKindParam");
				parameters.put("entityKindParam", entityKind);
			}

			// entityKind Parameter
			if(null != checkinType) {
				declaredParams.add("Integer checkinTypeParam");
				filters.add("checkinType == checkinTypeParam");
				parameters.put("checkinTypeParam", checkinType);
			}

			// fromDate Parameter
			if(null != fromDate) {
				declaredParams.add("java.util.Date fromDateParam");
				filters.add("checkinStarted >= fromDateParam");
				parameters.put("fromDateParam", fromDate);
			}

			// toDate Parameter
			if(null != toDate) {
				declaredParams.add("java.util.Date toDateParam");
				filters.add("checkinStarted < toDateParam");
				parameters.put("toDateParam", toDate);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			// Do a counting of records
			if( attributes != null ) {
				query.setResult("count(this)");
				Long count = Long.parseLong(query.executeWithMap(parameters).toString());
				attributes.put("recordCount", String.valueOf(count));
				query.setResult(null);
			}
			
			if( StringUtils.hasText(order))
				query.setOrdering(order);
			
			if( range != null ) 
				query.setRange(range.getFrom(), range.getTo());

			log.log(Level.INFO, "APDVisitDao query executing...");

			@SuppressWarnings("unchecked")
			List<APDVisit> list = (List<APDVisit>)query.executeWithMap(parameters);
			log.log(Level.INFO, "APDVisitDao query executed... copying results...");

			for(APDVisit obj : list ) {
				if(detachable) {
					ret.add(pm.detachCopy(obj));
				} else {
					ret.add(obj);
				}
			}
			log.log(Level.INFO, "APDVisitDao query results copied");
			
			query.closeAll();
			return ret;
			
		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			pm.close();
		}

	}

	@Override
	public List<APDVisit> getUsingStoresAndDate(List<String> stores, Date fromDate, Date toDate, Range range, boolean detachable) throws ASException {

		List<APDVisit> ret = CollectionFactory.createList();
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// entityId Parameter
			if(!CollectionUtils.isEmpty(stores)) {
				declaredParams.add("java.util.List entityIdParam");
				filters.add("entityIdParam.contains(entityId)");
				parameters.put("entityIdParam", stores);
			}

			// entityKind Parameter
			filters.add("entityKind == " + EntityKind.KIND_STORE);
			filters.add("checkinType == " + APDVisit.CHECKIN_VISIT);

			// fromDate Parameter
			if(null != fromDate) {
				declaredParams.add("java.util.Date fromDateParam");
				filters.add("checkinStarted >= fromDateParam");
				parameters.put("fromDateParam", fromDate);
			}

			// toDate Parameter
			if(null != toDate) {
				declaredParams.add("java.util.Date toDateParam");
				filters.add("checkinStarted <= toDateParam");
				parameters.put("toDateParam", toDate);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			if( range != null ) 
				query.setRange(range.getFrom(), range.getTo());

			log.log(Level.INFO, "APDVisitDao query executing...");

			@SuppressWarnings("unchecked")
			List<APDVisit> list = (List<APDVisit>)query.executeWithMap(parameters);
			log.log(Level.INFO, "APDVisitDao query executed... copying results...");

			for(APDVisit obj : list ) {
				if(detachable) {
					ret.add(pm.detachCopy(obj));
				} else {
					ret.add(obj);
				}
			}
			log.log(Level.INFO, "APDVisitDao query results copied");
			
			query.closeAll();
			return ret;
			
		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			pm.close();
		}

	}

	@Override
	public List<APDVisit> getUsingAPHE(String identifier, boolean detachable) throws ASException {
		
		List<APDVisit> ret = CollectionFactory.createList();
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// identifier Parameter
			declaredParams.add("String identifierParam");
			filters.add("apheSource == identifierParam");
			parameters.put("identifierParam", identifier);

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			log.log(Level.INFO, "APDVisitDao query executing...");

			@SuppressWarnings("unchecked")
			List<APDVisit> list = (List<APDVisit>)query.executeWithMap(parameters);
			log.log(Level.INFO, "APDVisitDao query executed... copying results...");

			for(APDVisit obj : list ) {
				if(detachable) {
					ret.add(pm.detachCopy(obj));
				} else {
					ret.add(obj);
				}
			}
			log.log(Level.INFO, "APDVisitDao query results copied");
			
			query.closeAll();
			return ret;
			
		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			pm.close();
		}
		
	}
	
	@Override
	public Map<Integer, Integer> countUsingAPHE(String identifier) throws ASException {

		Map<Integer, Integer> ret = CollectionFactory.createMap();
		
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// identifier Parameter
			declaredParams.add("String identifierParam");
			filters.add("apheSource == identifierParam");
			parameters.put("identifierParam", identifier);

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			log.log(Level.INFO, "APDVisitDao query executing...");

			@SuppressWarnings("unchecked")
			List<APDVisit> list = (List<APDVisit>)query.executeWithMap(parameters);
			log.log(Level.INFO, "APDVisitDao query executed... copying results...");

			for(APDVisit obj : list ) {
				Integer val = ret.get(obj.getCheckinType());
				if( val == null ) val = new Integer(0);
				val++;
				ret.put(obj.getCheckinType(), val);
			}
			log.log(Level.INFO, "APDVisitDao query results copied");
			
			query.closeAll();
			return ret;
			
		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			pm.close();
		}

	}

	@Override
	public void deleteUsingEntityIdAndEntityKindAndDate(String entityId, Integer entityKind, Date fromDate, Date toDate, Integer checkinType) throws ASException {

		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// entityId Parameter
			if(StringUtils.hasText(entityId)) {
				declaredParams.add("String entityIdParam");
				filters.add("entityId == entityIdParam");
				parameters.put("entityIdParam", entityId);
			}

			// entityKind Parameter
			if(null != entityKind) {
				declaredParams.add("Integer entityKindParam");
				filters.add("entityKind == entityKindParam");
				parameters.put("entityKindParam", entityKind);
			}

			// fromDate Parameter
			if(null != fromDate) {
				declaredParams.add("java.util.Date fromDateParam");
				filters.add("checkinStarted >= fromDateParam");
				parameters.put("fromDateParam", fromDate);
			}

			// toDate Parameter
			if(null != toDate) {
				declaredParams.add("java.util.Date toDateParam");
				filters.add("checkinStarted <= toDateParam");
				parameters.put("toDateParam", toDate);
			}

			// entityId Parameter
			if(null != checkinType) {
				declaredParams.add("Integer checkinTypeParam");
				filters.add("checkinType == checkinTypeParam");
				parameters.put("checkinTypeParam", checkinType);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			query.deletePersistentAll(parameters);
			
			return;
			
		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			pm.close();
		}

	}

	@Override
	public Map<Integer, Integer> getRepetitions(List<String> entityIds, Integer entityKind, Integer checkinType, Date fromDate, Date toDate) throws ASException {
		
		int MAX = 10;
		
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		// Creates the basic result map
		Map<Integer, Integer> results = CollectionFactory.createMap();
		for( int i = 2; i <= (MAX + 1); i++ )
			results.put(i, 0);
		
		String result1 = "result" + UUID.randomUUID();
		
		try{
			
			// Obtains DB Connection
			JDOConnection jdoConn = pm.getDataStoreConnection();
			DB db = (DB)jdoConn.getNativeConnection();

			// Set one, generate first filter
			BasicDBObject query = new BasicDBObject("$and", Arrays.asList(
					new BasicDBObject("entityId", new BasicDBObject("$in", entityIds.toArray(new String[entityIds.size()]))),
					new BasicDBObject("entityKind", entityKind),
					new BasicDBObject("checkinType", checkinType),
					new BasicDBObject("$and", Arrays.asList(
							new BasicDBObject("checkinStarted", new BasicDBObject("$gt", fromDate)),
							new BasicDBObject("checkinStarted", new BasicDBObject("$lt", toDate))
							))
					));
			String map = "function() { emit( this.mac, {recs:1} );}";
			String reduce = "function(key, vals) { var recs = 0; for( var i = 0; i < vals.length; i++ ) { recs += vals[i].recs; } return recs; }";
			
			MapReduceCommand command = new MapReduceCommand(db.getCollection("APDVisit"), map, reduce,
				     result1, MapReduceCommand.OutputType.REPLACE, query);

			MapReduceOutput mpo = db.getCollection("APDVisit").mapReduce(command);
			
			// Step 2 generate results
			query = new BasicDBObject("value", new BasicDBObject("$gt", 1));
			map = "function() { emit( this.value, {recs:1} ) }";
			reduce = "function(key, vals) { var ret = {recs: 0}; for( var i = 0; i < vals.length; i++ ) { ret.recs += vals[i].recs; } return ret; }";
			
			command = new MapReduceCommand(db.getCollection(result1), map, reduce,
				     null, MapReduceCommand.OutputType.INLINE, query);
			
			mpo = db.getCollection(result1).mapReduce(command);

			// Drops the temporary result collection
			db.getCollection(result1).drop();
			
			// Step 3 iterate over results
			for( DBObject dbo : mpo.results()) {
				try {
					Integer key = ((Double)dbo.get("_id")).intValue();
					Integer plus = ((Double)((DBObject)dbo.get("value")).get("recs")).intValue();
					if( key > 1 ) {
						if( key > MAX ) key = MAX + 1;
						Integer val = results.get(key);
						val = val + plus;
						results.put(key, val);
					}
				} catch( Exception e ) {
					// Nothing to do here
				}
			}

			jdoConn.close();
			
			return results;
			
		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			pm.close();
		}

	}
		
}
