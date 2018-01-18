package mobi.allshoppings.dao.spi;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.tools.CollectionFactory;

public class APDAssignationDAOJDOImpl extends GenericDAOJDO<APDAssignation> implements APDAssignationDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(APDAssignationDAOJDOImpl.class.getName());

	public APDAssignationDAOJDOImpl() {
		super(APDAssignation.class);
	}

	@Override
	public Key createKey(APDAssignation obj) throws ASException {
		return keyHelper.obtainKey(APDAssignation.class, String.valueOf(obj.hashCode()));
	}

	@Override
	public List<APDAssignation> getUsingEntityIdAndEntityKind(String entityId, byte entityKind) throws ASException {
		return getUsingEntityIdAndEntityKindAndDate(entityId, entityKind, null);
	}

	@Override
	public APDAssignation getOneUsingHostnameAndDate(String hostname, Date date) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(APDAssignation.class);

			if(hostname != null) {
				declaredParams.add("String hostnameParam");
				filters.add("hostname == hostnameParam");
				parameters.put("hostnameParam", hostname);
			}

			if( date != null ) {
				declaredParams.add("java.util.Date dateParam");
				filters.add("fromDate <= dateParam");
				filters.add("(toDate == null || toDate >= dateParam)");
				parameters.put("dateParam", date);
			}
			
			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			query.setOrdering("fromDate DESC");
			
			@SuppressWarnings("unchecked")
			List<APDAssignation> objs = (List<APDAssignation>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (APDAssignation obj : objs) {
					return pm.detachCopy(obj);
				}
			}
			
			throw ASExceptionHelper.notFoundException();
			
		} catch(Exception e) {
			if( e instanceof ASException ) {
				throw e;
			} else {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			}
	    } finally  {
	    	pm.close();
	    }

	}
	@Override
	public List<APDAssignation> getUsingHostnameAndDate(String hostname, Date date) throws ASException {

		List<APDAssignation> ret = CollectionFactory.createList();
		
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(APDAssignation.class);

			if(hostname != null) {
				declaredParams.add("String hostnameParam");
				filters.add("hostname == hostnameParam");
				parameters.put("hostnameParam", hostname);
			}

			if( date != null ) {
				declaredParams.add("java.util.Date dateParam");
				filters.add("fromDate <= dateParam");
				filters.add("(toDate == null || toDate >= dateParam)");
				parameters.put("dateParam", date);
			}
			
			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			query.setOrdering("fromDate DESC");
			
			@SuppressWarnings("unchecked")
			List<APDAssignation> objs = (List<APDAssignation>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (APDAssignation obj : objs) {
					ret.add(pm.detachCopy(obj));
				}
			}
			
			return ret;
			
		} catch(Exception e) {
			if( e instanceof ASException ) {
				throw e;
			} else {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			}
	    } finally  {
	    	pm.close();
	    }
	}

	@Override
	public List<APDAssignation> getUsingEntityIdAndEntityKindAndDate(String entityId, byte entityKind, Date date) throws ASException {

		List<APDAssignation> ret = CollectionFactory.createList();
		
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(APDAssignation.class);

			if(entityId != null) {
				declaredParams.add("String entityIdParam");
				filters.add("entityId == entityIdParam");
				parameters.put("entityIdParam", entityId);
			}

			if(entityKind >= 0) {
				declaredParams.add("Integer entityKindParam");
				filters.add("entityKind == entityKindParam");
				parameters.put("entityKindParam", entityKind);
			}
				
			if( date != null ) {
				declaredParams.add("java.util.Date dateParam");
				filters.add("fromDate <= dateParam");
				filters.add("(toDate == null || toDate >= dateParam)");
				parameters.put("dateParam", date);
			}
			
			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			query.setOrdering("fromDate DESC");
			
			@SuppressWarnings("unchecked")
			List<APDAssignation> objs = (List<APDAssignation>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (APDAssignation obj : objs) {
					ret.add(pm.detachCopy(obj));
				}
			}
			
			return ret;
			
		} catch(Exception e) {
			if( e instanceof ASException ) {
				throw e;
			} else {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			}
	    } finally  {
	    	pm.close();
	    }
	}

	@Override
	public List<String> getEntityIds(byte entityKind, Date forDate) throws ASException {
		
		List<String> ret = CollectionFactory.createList();
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// Hostname Parameter
			if(entityKind >= 0) {
				declaredParams.add("Integer entityKindParam");
				filters.add("entityKind == entityKindParam");
				parameters.put("entityKindParam", entityKind);
			}

			// toDate Parameter
			if( forDate != null ) {
				declaredParams.add("java.util.Date dateParam");
				filters.add("fromDate <= dateParam");
				filters.add("(toDate == null || toDate >= dateParam)");
				parameters.put("dateParam", forDate);
			}

			query.setResult("entityId");
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>)query.executeWithMap(parameters);
			for(String obj : list ) {
				if(!ret.contains(obj))
					ret.add(obj);
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

}
