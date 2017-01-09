package mobi.allshoppings.dao.spi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

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
	public List<APDVisit> getUsingEntityIdAndEntityKindAndDate(String entityId, Integer entityKind, Date fromDate, Date toDate, Range range, boolean detachable) throws ASException {

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
				parameters.put("entityKindParam", entityId);
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
	public void deleteUsingEntityIdAndEntityKindAndDate(String entityId, Integer entityKind, Date fromDate, Date toDate) throws ASException {

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
}
