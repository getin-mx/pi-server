package mobi.allshoppings.dao.spi;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.CheckinDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Checkin;
import mobi.allshoppings.tools.CollectionFactory;

public class CheckinDAOJDOImpl extends GenericDAOJDO<Checkin> implements CheckinDAO {

	private final static long THREE_HOURS = 10800000;
	private final static long TWENTYFOUR_HOURS = 86400000;
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(CheckinDAOJDOImpl.class.getName());

	public CheckinDAOJDOImpl() {
		super(Checkin.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(Checkin.class);
	}

	@Override
	public long getUserCheckinCount(String userId) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(Checkin.class);

			declaredParams.add("String userIdParam");
			filters.add("userId == userIdParam");
			parameters.put("userIdParam", userId);

			// Setting query parameters
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
	public long getEntityCheckinCount(String entityId, Integer entityKind) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(Checkin.class);

			declaredParams.add("String entityIdParam");
			declaredParams.add("Integer entityKindParam");
			filters.add("entityId == entityIdParam");
			filters.add("entityKind == entityKindParam");
			parameters.put("entityIdParam", entityId);
			parameters.put("entityKindParam", entityKind);

			// Setting query parameters
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
	public Checkin getUnfinishedCheckinByEntityAndKindAndType(String deviceUUID,
			String entityId, Integer entityKind, Integer checkinType) throws ASException {
		return getUnfinishedCheckinByEntityAndKindAndType(deviceUUID, entityId, entityKind, checkinType, null);
	}

	@Override
	public Checkin getUnfinishedCheckinByEntityAndKindAndType(String deviceUUID,
			String entityId, Integer entityKind, Integer checkinType, Long closeLimitMillis) throws ASException {
		return getUnfinishedCheckinByEntityAndKindAndType(deviceUUID, entityId, entityKind, checkinType, closeLimitMillis, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Checkin getUnfinishedCheckinByEntityAndKindAndType(String deviceUUID,
			String entityId, Integer entityKind, Integer checkinType, Long closeLimitMillis, Date forDate) throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			List<String> filtersWithFinish = CollectionFactory.createList();

			Query query = pm.newQuery(Checkin.class);
			
			declaredParams.add("String deviceUUIDParam");
			filters.add("deviceUUID == deviceUUIDParam");
			filtersWithFinish.add("deviceUUID == deviceUUIDParam");
			parameters.put("deviceUUIDParam", deviceUUID);
			
			filters.add("checkinFinished == null");
			
			if(StringUtils.hasText(entityId)) {
				declaredParams.add("String entityIdParam");
				filters.add("entityId == entityIdParam");
				filtersWithFinish.add("entityId == entityIdParam");
				parameters.put("entityIdParam", entityId);
			}
			
			if(entityKind != null) {
				declaredParams.add("Integer entityKindParam");
				filters.add("entityKind == entityKindParam");
				filtersWithFinish.add("entityKind == entityKindParam");
				parameters.put("entityKindParam", entityKind);
			}
			
			if(checkinType != null ) {
				declaredParams.add("Integer checkinTypeParam");
				filters.add("checkinType == checkinTypeParam");
				filtersWithFinish.add("checkinType == checkinTypeParam");
				parameters.put("checkinTypeParam", checkinType);
			}
	
			if( forDate != null ) {
				Date fromDate = new Date(forDate.getTime() - TWENTYFOUR_HOURS);
				declaredParams.add("java.util.Date checkinStartedParam");
				filters.add("checkinStarted >= checkinStartedParam");
				filtersWithFinish.add("checkinStarted >= checkinStartedParam");
				parameters.put("checkinStartedParam", fromDate);
				
				Date toDate = new Date(forDate.getTime() + TWENTYFOUR_HOURS);
				declaredParams.add("java.util.Date checkinStarted2Param");
				filters.add("checkinStarted <= checkinStarted2Param");
				filtersWithFinish.add("checkinStarted <= checkinStarted2Param");
				parameters.put("checkinStarted2Param", toDate);
			}
			
			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			query.setOrdering("checkinStarted DESC");
			
			if( closeLimitMillis != null && closeLimitMillis > 0 ) {
				// If we are here... We just have to try if there is a checkin recently closed, so we can reopen it if necesary
				query.setFilter(toWellParametrizedFilter(filtersWithFinish));
				List<Checkin> objs = (List<Checkin>)query.executeWithMap(parameters);
				if (objs != null) {
					// force to read
					Date limitDate = new Date(new Date().getTime() - closeLimitMillis);
					for (Checkin obj : objs) {
						if(obj.getCheckinFinished() != null && limitDate.before(obj.getCheckinFinished()))
							return pm.detachCopy(obj);
					}
				}
			}

			List<Checkin> objs = (List<Checkin>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Checkin obj : objs) {
					if( forDate != null ) {
						if(( obj.getCheckinStarted().equals(forDate) || obj.getCheckinStarted().before(forDate))) {
							return pm.detachCopy(obj);
						}
					} else {
						return pm.detachCopy(obj);
					}
				}
			}
			
			// If we get this far... no entity was found
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

	@SuppressWarnings("unchecked")
	@Override
	public Checkin getCheckinByEntityAndKindAndType(String deviceUUID,
			String entityId, Integer entityKind, Integer checkinType, Date forDate) throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			List<String> filtersWithFinish = CollectionFactory.createList();

			Query query = pm.newQuery(Checkin.class);
			
			declaredParams.add("String deviceUUIDParam");
			filters.add("deviceUUID == deviceUUIDParam");
			filtersWithFinish.add("deviceUUID == deviceUUIDParam");
			parameters.put("deviceUUIDParam", deviceUUID);
			
			if(StringUtils.hasText(entityId)) {
				declaredParams.add("String entityIdParam");
				filters.add("entityId == entityIdParam");
				filtersWithFinish.add("entityId == entityIdParam");
				parameters.put("entityIdParam", entityId);
			}
			
			if(entityKind != null) {
				declaredParams.add("Integer entityKindParam");
				filters.add("entityKind == entityKindParam");
				filtersWithFinish.add("entityKind == entityKindParam");
				parameters.put("entityKindParam", entityKind);
			}
			
			if(checkinType != null ) {
				declaredParams.add("Integer checkinTypeParam");
				filters.add("checkinType == checkinTypeParam");
				filtersWithFinish.add("checkinType == checkinTypeParam");
				parameters.put("checkinTypeParam", checkinType);
			}
	
			if( forDate != null ) {
				Date fromDate = new Date(forDate.getTime() - THREE_HOURS);
				declaredParams.add("java.util.Date checkinStartedParam");
				filters.add("checkinStarted >= checkinStartedParam");
				filtersWithFinish.add("checkinStarted >= checkinStartedParam");
				parameters.put("checkinStartedParam", fromDate);
				
				Date toDate = new Date(forDate.getTime() + THREE_HOURS);
				declaredParams.add("java.util.Date checkinStarted2Param");
				filters.add("checkinStarted <= checkinStarted2Param");
				filtersWithFinish.add("checkinStarted <= checkinStarted2Param");
				parameters.put("checkinStarted2Param", toDate);
			}
			
			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			query.setOrdering("checkinStarted DESC");
			
			List<Checkin> objs = (List<Checkin>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Checkin obj : objs) {
					if( forDate != null ) {
						if(( obj.getCheckinStarted().equals(forDate) || obj.getCheckinStarted().before(forDate))) {
							return pm.detachCopy(obj);
						}
					} else {
						return pm.detachCopy(obj);
					}
				}
			}

			// If we get this far... no entity was found
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
	public List<Checkin> getUsingEntityKindAndPossibleFakeAndDates(Integer entityKind, Boolean possibleFake, Date fromDate, Date toDate) throws ASException {
		return getUsingEntityIdAndEntityKindAndPossibleFakeAndDates(null, entityKind, possibleFake, fromDate, toDate);
	}
	
	@Override
	public List<Checkin> getUsingEntityIdAndEntityKindAndPossibleFakeAndDates(String entityId, Integer entityKind, Boolean possibleFake, Date fromDate, Date toDate) throws ASException {

		List<Checkin> ret = CollectionFactory.createList();
		
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(Checkin.class);

			if( possibleFake != null ) {
				declaredParams.add("Boolean possibleFakeParam");
				filters.add("possibleFake == possibleFakeParam");
				parameters.put("possibleFakeParam", possibleFake);
			}
						
			if(entityId != null) {
				declaredParams.add("String entityIdParam");
				filters.add("entityId == entityIdParam");
				parameters.put("entityIdParam", entityId);
			}

			if(entityKind != null) {
				declaredParams.add("Integer entityKindParam");
				filters.add("entityKind == entityKindParam");
				parameters.put("entityKindParam", entityKind);
			}
				
			if( fromDate != null ) {
				declaredParams.add("java.util.Date checkinStartedParam");
				filters.add("checkinStarted >= checkinStartedParam");
				parameters.put("checkinStartedParam", fromDate);
			}
			
			if( toDate != null ) {
				declaredParams.add("java.util.Date checkinStarted2Param");
				filters.add("checkinStarted <= checkinStarted2Param");
				parameters.put("checkinStarted2Param", toDate);
			}
			
			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			query.setOrdering("checkinStarted DESC");
			
			@SuppressWarnings("unchecked")
			List<Checkin> objs = (List<Checkin>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Checkin obj : objs) {
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
	
	
}
