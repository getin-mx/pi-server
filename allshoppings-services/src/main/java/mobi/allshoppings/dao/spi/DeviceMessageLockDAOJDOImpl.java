package mobi.allshoppings.dao.spi;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.DeviceMessageLockDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DeviceMessageLock;
import mobi.allshoppings.tools.CollectionFactory;

public class DeviceMessageLockDAOJDOImpl extends GenericDAOJDO<DeviceMessageLock> implements DeviceMessageLockDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(DeviceMessageLockDAOJDOImpl.class.getName());

	public DeviceMessageLockDAOJDOImpl() {
		super(DeviceMessageLock.class);
	}

	@Override
	public Key createKey(DeviceMessageLock obj) throws ASException {
		return keyHelper.obtainKey(DeviceMessageLock.class, String.valueOf(obj.hashCode()));
	}

	@Override
	public boolean hasActiveLocks(String userId, Date forDate)
			throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(DeviceMessageLock.class);

			// Campaign Special Parameters
			if( userId != null ) {
				declaredParams.add("String userIdParam");
				filters.add("userId == userIdParam");
				parameters.put("userIdParam", userId);
			}

			// Date parameters
			if( forDate != null ) {
				declaredParams.add("java.util.Date toDateParam");
				filters.add("toDate > toDateParam");
				parameters.put("toDateParam", forDate);
			}

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			@SuppressWarnings("unchecked")
			List<DeviceMessageLock> objs = (List<DeviceMessageLock>)query.executeWithMap(parameters);
			for(DeviceMessageLock obj : objs ) {
				if( obj.getFromDate().before(forDate)) return true;
			}
			
			return false;
			
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}

	}

	@Override
	public boolean deviceHasActiveLocks(String deviceUUID, Date forDate)
			throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(DeviceMessageLock.class);

			// Campaign Special Parameters
			if( deviceUUID != null ) {
				declaredParams.add("String deviceUUIDParam");
				filters.add("deviceId == deviceUUIDParam");
				parameters.put("deviceUUIDParam", deviceUUID);
			}

			// Date parameters
			if( forDate != null ) {
				declaredParams.add("java.util.Date toDateParam");
				filters.add("toDate > toDateParam");
				parameters.put("toDateParam", forDate);
			}

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			@SuppressWarnings("unchecked")
			List<DeviceMessageLock> objs = (List<DeviceMessageLock>)query.executeWithMap(parameters);
			for(DeviceMessageLock obj : objs ) {
				if( obj.getFromDate().before(forDate)) return true;
			}
			
			return false;
			
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}

	}

	@Override
	public boolean deviceHasActiveLocks(String deviceUUID, Date forDate, List<Integer> scopes)
			throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(DeviceMessageLock.class);

			// Campaign Special Parameters
			if( deviceUUID != null ) {
				declaredParams.add("String deviceUUIDParam");
				filters.add("deviceId == deviceUUIDParam");
				parameters.put("deviceUUIDParam", deviceUUID);
			}

			// Date parameters
			if( forDate != null ) {
				declaredParams.add("java.util.Date toDateParam");
				filters.add("toDate > toDateParam");
				parameters.put("toDateParam", forDate);
			}

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			@SuppressWarnings("unchecked")
			List<DeviceMessageLock> objs = (List<DeviceMessageLock>)query.executeWithMap(parameters);
			for(DeviceMessageLock obj : objs ) {
				if( obj.getFromDate().before(forDate)) {
					if(scopes != null && scopes.size() > 0 ) {
						if(scopes.contains(obj.getScope())) {
							return true;
						}
					} else {
						return true;
					}
				}
			}
			
			return false;
			
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}

	}

	@Override
	public List<DeviceMessageLock> getDeviceActiveLocks(String deviceUUID, Date forDate) throws ASException {

		List<DeviceMessageLock> ret = CollectionFactory.createList();
		
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(DeviceMessageLock.class);

			// Campaign Special Parameters
			if( deviceUUID != null ) {
				declaredParams.add("String deviceUUIDParam");
				filters.add("deviceId == deviceUUIDParam");
				parameters.put("deviceUUIDParam", deviceUUID);
			}

			// Date parameters
			if( forDate != null ) {
				declaredParams.add("java.util.Date toDateParam");
				filters.add("toDate > toDateParam");
				parameters.put("toDateParam", forDate);
			}

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			@SuppressWarnings("unchecked")
			List<DeviceMessageLock> objs = (List<DeviceMessageLock>)query.executeWithMap(parameters);
			for(DeviceMessageLock obj : objs ) {
				if( obj.getFromDate().before(forDate)) ret.add(pm.detachCopy(obj));
			}
			
			return ret;
			
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}

	}

	@Override
	public List<DeviceMessageLock> getUsingDeviceAndScopeAndCampaign(String deviceUUID, byte scope,
			String campaignActivityId) throws ASException {

		List<DeviceMessageLock> ret = CollectionFactory.createList();
		
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(DeviceMessageLock.class);

			// Device Parameter
			if( deviceUUID != null ) {
				declaredParams.add("String deviceUUIDParam");
				filters.add("deviceId == deviceUUIDParam");
				parameters.put("deviceUUIDParam", deviceUUID);
			}

			// Scope Parameter
			if( scope != -1 ) {
				declaredParams.add("Integer scopeParam");
				filters.add("scope == scopeParam");
				parameters.put("scopeParam", new Integer(scope));
			}

			// Campaign Activity Parameter
			if( campaignActivityId != null ) {
				declaredParams.add("String campaignActivityIdParam");
				filters.add("campaignActivityId == campaignActivityIdParam");
				parameters.put("campaignActivityIdParam", campaignActivityId);
			}

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			@SuppressWarnings("unchecked")
			List<DeviceMessageLock> objs = (List<DeviceMessageLock>)query.executeWithMap(parameters);
			for(DeviceMessageLock obj : objs ) {
				ret.add(pm.detachCopy(obj));
			}
			
			return ret;
			
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}

	}

}
