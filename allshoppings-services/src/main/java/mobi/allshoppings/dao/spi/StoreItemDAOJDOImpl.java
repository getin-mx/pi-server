package mobi.allshoppings.dao.spi;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.StoreItemDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.StoreItem;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

public class StoreItemDAOJDOImpl extends GenericDAOJDO<StoreItem> implements StoreItemDAO {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(StoreItemDAOJDOImpl.class.getName());

	public StoreItemDAOJDOImpl() {
		super(StoreItem.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(StoreItem.class);
	}

	@Override
	public List<StoreItem> getUsingStoreIdAndDatesAndRange(String storeId, String fromDate, String toDate, Range range, String order, boolean detachable) throws ASException {
		List<StoreItem> returnedObjs = CollectionFactory.createList();

		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// Store id parameter
			if( StringUtils.hasText(storeId) ) {
				declaredParams.add("String storeIdParam");
				filters.add("storeId == storeIdParam");
				parameters.put("storeIdParam", storeId);
			}
			
			// Date parameter
			if( fromDate != null ) {
				declaredParams.add("String fromDateParam");
				filters.add("date >= fromDateParam");
				parameters.put("fromDateParam", fromDate);
			}

			if( toDate != null ) {
				declaredParams.add("String toDateParam");
				filters.add("date <= toDateParam");
				parameters.put("toDateParam", toDate);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			// Adds a cursor to the ranged query
			if( range != null ) 
				query.setRange(range.getFrom(), range.getTo());

			// Sets order as required
			if( StringUtils.hasText(order)) query.setOrdering(order);

			@SuppressWarnings("unchecked")
			List<StoreItem> objs = parameters.size() > 0 ? (List<StoreItem>)query.executeWithMap(parameters) : (List<StoreItem>)query.execute();
			if (objs != null) {
				// force to read
				for (StoreItem obj : objs) {
					if( detachable )
						returnedObjs.add(pm.detachCopy(obj));
					else
						returnedObjs.add(obj);
				}
			}

		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			pm.close();
		}

		return returnedObjs;

	}

	
	@Override
	public StoreItem getUsingStoreIdAndDate(String storeId, String date, boolean detachable) throws ASException {

		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// Store id parameter
			if( StringUtils.hasText(storeId) ) {
				declaredParams.add("String storeIdParam");
				filters.add("storeId == storeIdParam");
				parameters.put("storeIdParam", storeId);
			}
			
			// Date parameter
			if( date != null ) {
				declaredParams.add("String dateParam");
				filters.add("date == dateParam");
				parameters.put("dateParam", date);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			@SuppressWarnings("unchecked")
			List<StoreItem> objs = parameters.size() > 0 ? (List<StoreItem>)query.executeWithMap(parameters) : (List<StoreItem>)query.execute();
			if (objs != null) {
				// force to read
				for (StoreItem obj : objs) {
					if( detachable )
						return pm.detachCopy(obj);
					else
						return obj;
				}
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
}
