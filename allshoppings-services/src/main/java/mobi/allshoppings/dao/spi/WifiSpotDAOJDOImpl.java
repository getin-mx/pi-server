package mobi.allshoppings.dao.spi;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.datastore.JDOConnection;

import com.inodes.datanucleus.model.Key;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;

import mobi.allshoppings.dao.WifiSpotDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.WifiSpot;
import mobi.allshoppings.tools.CollectionFactory;

public class WifiSpotDAOJDOImpl extends GenericDAOJDO<WifiSpot> implements WifiSpotDAO {

	public WifiSpotDAOJDOImpl() {
		super(WifiSpot.class);
	}

	/**
	 * Creates a new unique key for an object of this entity kind, using a
	 * Hash of the query that this object refers as the unique key
	 */
	@Override
	public Key createKey(WifiSpot obj) throws ASException {
		return keyHelper.createStringUniqueKey(WifiSpot.class);
	}

	@Override
	public List<WifiSpot> getUsingFloorMapId(String floorMapId) throws ASException {

		List<WifiSpot> returnedObjs = CollectionFactory.createList();
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(clazz);
			declaredParams.add("String floorMapIdParam");
			filters.add("floorMapId == floorMapIdParam");
			parameters.put("floorMapIdParam", floorMapId);
			
			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			@SuppressWarnings("unchecked")
			List<WifiSpot> objs = (List<WifiSpot>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (WifiSpot obj : objs) {
					returnedObjs.add(pm.detachCopy(obj));
				}
			}
			
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
	    } finally  {
	    	pm.close();
	    }
		
		return returnedObjs;

	}

	@Override
	public String getNextSequence() throws ASException {

		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try {

			pm.currentTransaction().begin();

			JDOConnection jdoConn = pm.getDataStoreConnection();
			DB db = (DB)jdoConn.getNativeConnection();

			final BasicDBObject query = new BasicDBObject("_id", "fmwordid");
			final BasicDBObject update = new BasicDBObject("$inc", new BasicDBObject("seq", 1));
			final DBObject modifiedDoc = db.getCollection("counters")
					.findAndModify(query, null, null, false, update, true,
							false);
			long seq = Double.valueOf(modifiedDoc.get("seq").toString()).longValue();
			jdoConn.close();
			pm.currentTransaction().commit();

			return Long.toHexString(seq);

		} catch(Exception e) {
			log.log(Level.SEVERE, "exception catched", e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally  {
			pm.close();
		}
	}

}
