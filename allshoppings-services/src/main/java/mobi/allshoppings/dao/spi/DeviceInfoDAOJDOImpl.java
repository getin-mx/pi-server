package mobi.allshoppings.dao.spi;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import mobi.allshoppings.auth.UserInfo;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.DeviceLocationDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.DeviceLocation;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Cursor;
import com.inodes.datanucleus.model.JDOCursorHelper;

public class DeviceInfoDAOJDOImpl extends GenericDAOJDO<DeviceInfo> implements DeviceInfoDAO {
	private static final Logger log = Logger.getLogger(DeviceInfoDAOJDOImpl.class.getName());

	@Autowired
	private DeviceLocationDAO deviceLocationDao;
	
	public DeviceInfoDAOJDOImpl() {
		super(DeviceInfo.class);
	}

	@Override
	public DeviceInfo getUsingUserAndDeviceAndPlatform(String userId,
			String deviceName, String devicePlatform) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		DeviceInfo ret = null;

		try {

			if(!StringUtils.hasText(userId) || !StringUtils.hasText(deviceName) || !StringUtils.hasText(devicePlatform)) {
				throw ASExceptionHelper.notAcceptedException();
			}

			Map<String, Object> parameters = CollectionFactory.createMap();
			Query query = pm.newQuery(DeviceInfo.class);	
			query.declareParameters("String userIdParm, String deviceNameParam, String devicePlatformParam");
			query.setFilter("userId == userIdParm && deviceName == deviceNameParam && devicePlatform == devicePlatformParam");
			parameters.put("userIdParam", userId);
			parameters.put("deviceNameParam", deviceName);
			parameters.put("devicePlatformParam", devicePlatform);
			
			@SuppressWarnings("unchecked")
			List<DeviceInfo> result = (List<DeviceInfo>)query.executeWithMap(parameters);

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
	public long countOrphan() throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Query query = pm.newQuery(DeviceInfo.class);
			query.setFilter("userId == null");
			query.setResult("count(this)");
			Long results = Long.parseLong(query.execute().toString());
			return (long)results;
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
	    } finally  {
			pm.close();
	    }	
	}

	@Override
	public List<DeviceInfo> getOrphan(Range range) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<DeviceInfo> ret = CollectionFactory.createList();

		try {

			Query query = pm.newQuery(DeviceInfo.class);	
			query.setFilter("userId == null");
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
			
			@SuppressWarnings("unchecked")
			List<DeviceInfo> results = (List<DeviceInfo>)query.execute();

			// Store the cursorString
			if( range != null ) {
				Cursor cursor = JDOCursorHelper.getCursor(results);
				range.setCursor(cursor.toWebSafeString());
			}

			for( DeviceInfo o : results ) {
				ret.add(pm.detachCopy(o));
			}

			return ret;

		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}		
	}

	@Override
	public DeviceInfo getUsingMessagingToken(String messagingToken) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		DeviceInfo ret = null;

		try {

			if(!StringUtils.hasText(messagingToken)) {
				throw ASExceptionHelper.notAcceptedException();
			}

			Map<String, Object> parameters = CollectionFactory.createMap();
			Query query = pm.newQuery(DeviceInfo.class);
			query.declareParameters("String messagingTokenParm");
			query.setFilter("messagingToken == messagingTokenParm");
			parameters.put("messagingTokenParm", messagingToken);
			
			@SuppressWarnings("unchecked")
			List<DeviceInfo> result = (List<DeviceInfo>)query.executeWithMap(parameters);

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
	public List<DeviceInfo> getUsingUser(String userId) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<DeviceInfo> ret = CollectionFactory.createList();

		try {

			if(!StringUtils.hasText(userId)) {
				throw ASExceptionHelper.notAcceptedException();
			}

			Map<String, Object> parameters = CollectionFactory.createMap();
			Query query = pm.newQuery(DeviceInfo.class);	
			query.declareParameters("String userIdParm");
			query.setFilter("userId == userIdParm");
			query.setOrdering("lastUpdate desc");
			parameters.put("userIdParm", userId);
			
			@SuppressWarnings("unchecked")
			List<DeviceInfo> result = (List<DeviceInfo>)query.executeWithMap(parameters);

			if( result.size() == 0 ) {
				throw ASExceptionHelper.notFoundException();
			}

			for(DeviceInfo res : result) {
				ret.add(pm.detachCopy(res));
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
	public List<DeviceInfo> getUsingMAC(String mac) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<DeviceInfo> ret = CollectionFactory.createList();

		try {

			if(!StringUtils.hasText(mac)) {
				throw ASExceptionHelper.notAcceptedException();
			}

			Map<String, Object> parameters = CollectionFactory.createMap();
			Query query = pm.newQuery(DeviceInfo.class);	
			query.declareParameters("String macParm");
			query.setFilter("mac == macParm");
			parameters.put("macParm", mac.toUpperCase());
			
			@SuppressWarnings("unchecked")
			List<DeviceInfo> result = (List<DeviceInfo>)query.executeWithMap(parameters);

			if( result.size() == 0 ) {
				throw ASExceptionHelper.notFoundException();
			}

			for(DeviceInfo res : result) {
				ret.add(pm.detachCopy(res));
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
	public List<DeviceInfo> getUsingKeyList(List<String> keys) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<DeviceInfo> ret = CollectionFactory.createList();

		try {

			if(keys == null || keys.size() == 0) {
				throw ASExceptionHelper.notAcceptedException();
			}

			Map<String, Object> parameters = CollectionFactory.createMap();
			Query query = pm.newQuery(DeviceInfo.class);	
			query.declareParameters("java.util.List keysParam");
			query.setFilter("keysParam.contains(key)");
			parameters.put("keysParam", keys);
			
			@SuppressWarnings("unchecked")
			List<DeviceInfo> result = (List<DeviceInfo>)query.executeWithMap(parameters);

			for(DeviceInfo res : result) {
				ret.add(pm.detachCopy(res));
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
	public List<DeviceInfo> getForTableWidthKey(String keyName, String keyValue, String[] columnSort,
			String sortDirection, String[] searchFields, String search, long first,
			long last, UserInfo userInfo) throws ASException {
		List<DeviceInfo> returnedObjs = CollectionFactory.createList();
		
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Query query = pm.newQuery(DeviceInfo.class);
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
			List<DeviceInfo> objs = (List<DeviceInfo>)query.execute();
			if (objs != null) {
				// force to read
				for (DeviceInfo o : objs) {
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

	@Override
	public void deleteByMessagingToken(String messagingToken) throws ASException {
		delete(getUsingMessagingToken(messagingToken));
	}

	@Override
	public 	List<DeviceInfo> getByProximity(GeoPoint geo, Integer presition, Integer limitInMeters, String appId, Date lastUpdate, boolean detachable) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<DeviceLocation> locations = deviceLocationDao.getByProximity(geo, presition, limitInMeters, appId, lastUpdate, detachable);
		List<DeviceInfo> ret = CollectionFactory.createList();
		List<String> idList = CollectionFactory.createList();
		for( DeviceLocation obj : locations ) {
			idList.add(obj.getIdentifier());
		}
		
		List<DeviceInfo> list = getUsingIdList(idList, true);
		
		for( DeviceInfo obj : list ) {
			if( appId == null || (obj.getAppId() != null && obj.getAppId().equals(appId))) {
				if(!ret.contains(obj)) {
					if( detachable )
						ret.add(pm.detachCopy(obj));
					else 
						ret.add(obj);
				} 
			}
		}
		return ret;
	}

	/**
	 * @return the deviceLocationDao
	 */
	public DeviceLocationDAO getDeviceLocationDao() {
		return deviceLocationDao;
	}

	/**
	 * @param deviceLocationDao the deviceLocationDao to set
	 */
	public void setDeviceLocationDao(DeviceLocationDAO deviceLocationDao) {
		this.deviceLocationDao = deviceLocationDao;
	}

}
