package mobi.allshoppings.dao.spi;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Cursor;
import com.inodes.datanucleus.model.JDOCursorHelper;
import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.DeviceLocationDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.model.DeviceLocation;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

public class DeviceLocationDAOJDOImpl extends GenericDAOJDO<DeviceLocation> implements DeviceLocationDAO {
	private static final Logger log = Logger.getLogger(DeviceLocationDAOJDOImpl.class.getName());

	@Autowired
	private GeoCodingHelper geocoder;
	
	public DeviceLocationDAOJDOImpl() {
		super(DeviceLocation.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(DeviceLocation.class);
	}

	@Override
	public DeviceLocation getLastUsingUserId(String userId) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		DeviceLocation ret = null;

		try {
			// Device Validation
			if(!StringUtils.hasText(userId)) {
				throw ASExceptionHelper.notAcceptedException();
			}

			Map<String, Object> parameters = CollectionFactory.createMap();
			Query query = pm.newQuery(DeviceLocation.class);
			query.declareParameters("String userIdParam");
			query.setFilter("userId == userIdParam");
			query.setOrdering("lastUpdate desc");
			parameters.put("userIdParam", userId);
			
			@SuppressWarnings("unchecked")
			List<DeviceLocation> result = (List<DeviceLocation>)query.executeWithMap(parameters);

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

	public List<DeviceLocation> getAllDelayed(Date from, Date limit) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<DeviceLocation> ret = CollectionFactory.createList();

		try {

			if(from == null || limit == null) {
				throw ASExceptionHelper.notAcceptedException();
			}

			Map<String, Object> parameters = CollectionFactory.createMap();
			Query query = pm.newQuery(DeviceLocation.class);	
			query.declareParameters("java.util.Date fromParam, java.util.Date limitParam");
			query.setFilter("lastUpdate < fromParam && lastUpdate > limitParam");
			parameters.put("fromParam", from);
			parameters.put("limitParam", limit);
			
			@SuppressWarnings("unchecked")
			List<DeviceLocation> result = (List<DeviceLocation>)query.executeWithMap(parameters);

			for( DeviceLocation o : result ) {
				if(StringUtils.hasText(o.getUserId()))
					ret.add(pm.detachCopy(o));
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
	public List<DeviceLocation> getOrphan(Range range) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<DeviceLocation> ret = CollectionFactory.createList();

		try {

			Query query = pm.newQuery(DeviceLocation.class);	
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
			List<DeviceLocation> results = (List<DeviceLocation>)query.execute();

			// Store the cursorString
			if( range != null ) {
				Cursor cursor = JDOCursorHelper.getCursor(results);
				range.setCursor(cursor.toWebSafeString());
			}

			for( DeviceLocation o : results ) {
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
	public long countOrphan() throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Query query = pm.newQuery(DeviceLocation.class);
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

	public List<DeviceLocation> getAllUpdated(Date from) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<DeviceLocation> ret = CollectionFactory.createList();

		try {

			if(from == null) {
				throw ASExceptionHelper.notAcceptedException();
			}

			Map<String, Object> parameters = CollectionFactory.createMap();
			Query query = pm.newQuery(DeviceLocation.class);	
			query.declareParameters("java.util.Date fromParam");
			query.setFilter("lastUpdate > fromParam");
			parameters.put("fromParam", from);
			
			@SuppressWarnings("unchecked")
			List<DeviceLocation> result = (List<DeviceLocation>)query.executeWithMap(parameters);

			for( DeviceLocation o : result ) {
				if(StringUtils.hasText(o.getUserId()))
					ret.add(pm.detachCopy(o));
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
	public List<DeviceLocation> getUsingUserIdentifierAndLastUpdate(
			String userIdentifier, Date lastUpdate) throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<DeviceLocation> ret = CollectionFactory.createList();

		try {

			if(lastUpdate == null) lastUpdate = new Date(0);
			Map<String, Object> parameters = CollectionFactory.createMap();
			Query query = pm.newQuery(DeviceLocation.class);	
			query.declareParameters("java.util.Date fromParam, String userIdParam");
			query.setFilter("userId == userIdParam && lastUpdate > fromParam");
			parameters.put("fromParam", lastUpdate);
			parameters.put("userIdParam", userIdentifier);
			
			@SuppressWarnings("unchecked")
			List<DeviceLocation> result = (List<DeviceLocation>)query.executeWithMap(parameters);

			for( DeviceLocation o : result ) {
				if(StringUtils.hasText(o.getUserId()))
					ret.add(pm.detachCopy(o));
			}

			return ret;

		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}		
	}

	/**
	 * @return the geocoder
	 */
	public GeoCodingHelper getGeocoder() {
		return geocoder;
	}

	/**
	 * @param geocoder the geocoder to set
	 */
	public void setGeocoder(GeoCodingHelper geocoder) {
		this.geocoder = geocoder;
	}

	@Override
	public 	List<DeviceLocation> getByProximity(GeoPoint geo, Integer presition, Integer limitInMeters, String appId, Date lastUpdate, boolean detachable) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<DeviceLocation> ret = CollectionFactory.createList();
		String[] boxes = geocoder.getAdjacentPoints(geo, geo.getGeohash().length() - (presition  + 1));

		try {
			for( int i = 0; i < boxes.length; i++ ) {
				if( StringUtils.hasLength(boxes[i])) {
					Query query = pm.newQuery(DeviceLocation.class);

					// Filter declaration
					Map<String, Object> params = CollectionFactory.createMap();
					StringBuffer filter = new StringBuffer();
					filter.append("geohash").append(" > '").append(boxes[i]).append("' && geohash < '").append(getLimit(boxes[i])).append("'");
					if( lastUpdate != null ) {
						query.declareParameters("java.util.Date lastUpdateParam");
						filter.append(" && lastUpdate > lastUpdateParam");
						params.put("lastUpdateParam", lastUpdate);
					}
					
					query.setFilter(filter.toString());

					// Executes the query
					@SuppressWarnings("unchecked")
					List<DeviceLocation> objs = (List<DeviceLocation>)query.executeWithMap(params);
					if (objs != null) {
						// force to read
						for (DeviceLocation obj : objs) {
							if( lastUpdate == null || obj.getLastUpdate().after(lastUpdate)) {
								if (geocoder == null
										|| limitInMeters == null
										|| geocoder.calculateDistance(
												geo.getLat(), geo.getLon(),
												obj.getLat(), geo.getLon()) <= limitInMeters) {
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
				}
			}

		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}

		return ret;
	}

	public String getLimit(String source) {
		byte[] bytes = source.getBytes();
		bytes[bytes.length -1] = (byte)(((int)bytes[bytes.length -1]) + 1);
		return new String(bytes);
	}
	
	@Override
	public long countLastUpdate(Date lastUpdate, boolean after) throws ASException {
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// From Date Parameter
			if( null != lastUpdate ) {
				declaredParams.add("java.util.Date lastUpdateParam");
				if( after ) {
					filters.add("lastUpdate >= lastUpdateParam");
				} else {
					filters.add("lastUpdate <= lastUpdateParam");
				}
				parameters.put("lastUpdateParam", lastUpdate);
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
}
