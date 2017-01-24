package mobi.allshoppings.dao.spi;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.datastore.JDOConnection;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;
import com.mongodb.DB;

import mobi.allshoppings.dao.ExternalAPHotspotDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.ExternalAPHotspot;
import mobi.allshoppings.tools.CollectionFactory;

public class ExternalAPHotspotDAOJDOImpl extends GenericDAOJDO<ExternalAPHotspot> implements ExternalAPHotspotDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ExternalAPHotspotDAOJDOImpl.class.getName());

	public ExternalAPHotspotDAOJDOImpl() {
		super(ExternalAPHotspot.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(ExternalAPHotspot.class);
	}

	@Override
	public long countUsingHostnameAndDates(String hostname, Date fromDate, Date toDate) throws ASException {
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// Hostname Parameter
			if(StringUtils.hasText(hostname)) {
				declaredParams.add("String hostnameParam");
				filters.add("hostname == hostnameParam");
				parameters.put("hostnameParam", hostname);
			}
			
			// From Date Parameter
			if( null != fromDate ) {
				declaredParams.add("java.util.Date fromDateParam");
				filters.add("firstSeen >= fromDateParam");
				parameters.put("fromDateParam", fromDate);
			}

			// To Date Parameter
			if( null != toDate ) {
				declaredParams.add("java.util.Date toDateParam");
				filters.add("firstSeen <= toDateParam");
				parameters.put("toDateParam", toDate);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			query.setResult("count(this)");
			Long results = Long.parseLong(query.executeWithMap(parameters).toString());
			return (long)results;

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
	public List<ExternalAPHotspot> getUsingHostnameAndDates(String hostname, Date fromDate, Date toDate) throws ASException {
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<ExternalAPHotspot> ret = CollectionFactory.createList();

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// Hostname Parameter
			if(StringUtils.hasText(hostname)) {
				declaredParams.add("String hostnameParam");
				filters.add("hostname == hostnameParam");
				parameters.put("hostnameParam", hostname);
			}
			
			// From Date Parameter
			if( null != fromDate ) {
				declaredParams.add("java.util.Date fromDateParam");
				filters.add("firstSeen >= fromDateParam");
				parameters.put("fromDateParam", fromDate);
			}

			// To Date Parameter
			if( null != toDate ) {
				declaredParams.add("java.util.Date toDateParam");
				filters.add("firstSeen <= toDateParam");
				parameters.put("toDateParam", toDate);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			@SuppressWarnings("unchecked")
			List<ExternalAPHotspot> objs = (List<ExternalAPHotspot>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (ExternalAPHotspot obj : objs) {
					ret.add(obj);
				}
			}
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
	public ExternalAPHotspot getLastUsingHostnameAndMac(String hostname, String mac) throws ASException {

		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// Hostname Parameter
			if(StringUtils.hasText(hostname)) {
				declaredParams.add("String hostnameParam");
				filters.add("hostname == hostnameParam");
				parameters.put("hostnameParam", hostname);
			}

			// Hostname Parameter
			if(StringUtils.hasText(hostname)) {
				declaredParams.add("String macParam");
				filters.add("mac == macParam");
				parameters.put("macParam", mac);
			}
			
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			query.setOrdering("creationDateTime DESC");
			query.setRange(0,1);
			
			@SuppressWarnings("unchecked")
			List<ExternalAPHotspot> list = (List<ExternalAPHotspot>)query.executeWithMap(parameters);
			if( list.size() > 0 ) {
				return pm.detachCopy(list.get(0));
			}
			
			throw ASExceptionHelper.notFoundException();
			
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
	public ExternalAPHotspot getPreviousUsingHostnameAndMac(String hostname, String mac) throws ASException {

		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// Hostname Parameter
			if(StringUtils.hasText(hostname)) {
				declaredParams.add("String hostnameParam");
				filters.add("hostname == hostnameParam");
				parameters.put("hostnameParam", hostname);
			}

			// Hostname Parameter
			if(StringUtils.hasText(hostname)) {
				declaredParams.add("String macParam");
				filters.add("mac == macParam");
				parameters.put("macParam", mac);
			}
			
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			query.setOrdering("creationDateTime DESC");
			query.setRange(0,2);
			
			@SuppressWarnings("unchecked")
			List<ExternalAPHotspot> list = (List<ExternalAPHotspot>)query.executeWithMap(parameters);
			if( list.size() > 1 ) {
				return pm.detachCopy(list.get(1));
			}
			
			throw ASExceptionHelper.notFoundException();
			
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
	public List<String> getExternalHostnames() throws ASException {
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try{
			
			// Obtains DB Connection
			JDOConnection jdoConn = pm.getDataStoreConnection();
			DB db = (DB)jdoConn.getNativeConnection();
			
			@SuppressWarnings("unchecked")
			List<String> results = db.getCollection("ExternalAPHotspot").distinct("hostname");

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
