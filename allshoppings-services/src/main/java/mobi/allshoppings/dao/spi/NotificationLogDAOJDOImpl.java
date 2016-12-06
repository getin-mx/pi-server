package mobi.allshoppings.dao.spi;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import mobi.allshoppings.auth.UserInfo;
import mobi.allshoppings.dao.NotificationLogDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.NotificationLog;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

public class NotificationLogDAOJDOImpl extends GenericDAOJDO<NotificationLog> implements NotificationLogDAO {
	private static final Logger log = Logger.getLogger(NotificationLogDAOJDOImpl.class.getName());

	public NotificationLogDAOJDOImpl() {
		super(NotificationLog.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(NotificationLog.class);
	}

	@Override
	public NotificationLog getLastNotificationFor(User user, String entityId,
			Integer entityKind) throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		NotificationLog ret = null;

		try {

			if(!StringUtils.hasText(entityId) || null == user ) {
				throw ASExceptionHelper.notAcceptedException();
			}

			Map<String, Object> parameters = CollectionFactory.createMap();
			Query query = pm.newQuery(NotificationLog.class);
			query.declareParameters("String userIdParm, String entityIdParm, Integer entityKindParm");
			query.setFilter("userId == userIdParm && entityId == entityIdParm && entityKind == entityKindParm");
			query.setOrdering("notifyDate desc");
			parameters.put("userIdParm", user.getIdentifier());
			parameters.put("entityIdParm", entityId);
			parameters.put("entityKindParm", entityKind);
			query.setRange(0, 1);
			
			@SuppressWarnings("unchecked")
			List<NotificationLog> result = (List<NotificationLog>)query.executeWithMap(parameters);

			if (result.size() > 0) {
				ret = pm.detachCopy(result.get(0));
			} else {
				throw ASExceptionHelper.notFoundException();
			}
			
			return ret;

		} catch(ASException e) {
			if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
				log.log(Level.WARNING, "exception catched", e);
			}
			throw e;
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}		

	}

	@Override
	public List<NotificationLog> getUsingStatusAndUserAndRange(List<Integer> status, User user, Range range, String order) throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<NotificationLog> ret = CollectionFactory.createList();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);
			
			if( status != null && status.size() > 0 ) {
				filters.add(toListFilterCriteria("status", status, false));
			}

			if( user != null ) {
				declaredParams.add("String userIdParam");
				filters.add("userId == userIdParam");
				parameters.put("userIdParam", user.getIdentifier());
			}
			
			if( range != null ) {
				query.setRange(range.getFrom(), range.getTo());
			}

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			if( StringUtils.hasText(order)) query.setOrdering(order);

			@SuppressWarnings("unchecked")
			List<NotificationLog> objs = (List<NotificationLog>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (NotificationLog o : objs) {
					ret.add(pm.detachCopy(o));
				}
			}
			
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
	    } finally  {
			pm.close();
	    }	
		
		return ret;
	}


	@Override
	public List<NotificationLog> getForTableWidthKey(String keyName, String keyValue, String[] columnSort,
			String sortDirection, String[] searchFields, String search, long first,
			long last, UserInfo userInfo) throws ASException {
		List<NotificationLog> returnedObjs = CollectionFactory.createList();
		
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Query query = pm.newQuery(NotificationLog.class);
			if( columnSort.length > 0 ) {
				query.setOrdering(buildQuerySort(columnSort, sortDirection));
			}
			query.setRange(first, last);
			
			if( search != null && !search.equals("")) {
				query.setFilter(buildQueryFilter(columnSort, search, keyName, keyValue));
			} else {
				query.setFilter(buildQueryFilter(keyName, keyValue));
			}

			@SuppressWarnings("unchecked")
			List<NotificationLog> objs = (List<NotificationLog>)query.execute();
			if (objs != null) {
				// force to read
				for (NotificationLog o : objs) {
					returnedObjs.add(o);
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
	public long count(String keyName, String keyValue, UserInfo userInfo) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Query query = pm.newQuery(clazz);
			query.setFilter(buildQueryFilter(keyName, keyValue));
			query.setResult("count(this)");
			Long results = Long.parseLong(query.execute().toString());
			return (long)results;
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
	    } finally  {
			pm.close();
	    }	
	}
}
