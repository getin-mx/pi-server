package mx.getin.dao.spi;

import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.spi.DAOJDOPersistentManagerFactory;
import mobi.allshoppings.dao.spi.GenericDAOJDO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;
import mx.getin.dao.StoreRevenueByHourDAO;
import mx.getin.model.StoreRevenueByHour;

/**
 * Implements the DAO to retrieve Store Revenue By Hour from the DB.
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 1.0, january 2018
 * @since Mark III, january 2017
 */
public class StoreRevenueByHourDAOJDOImpl extends GenericDAOJDO<StoreRevenueByHour> implements
		StoreRevenueByHourDAO {

	public StoreRevenueByHourDAOJDOImpl() {
		super(StoreRevenueByHour.class);
	}//constructor
	
	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(StoreRevenueByHour.class);
	}

	@Override
	public List<StoreRevenueByHour> getUsingStoreIdAndDateAndRange(String storeId, String date, String fromHour, String toHour, Range range, String order, boolean detachable) throws ASException {
		List<StoreRevenueByHour> returnedObjs = CollectionFactory.createList();

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

			if( fromHour != null ) {
				declaredParams.add("String fromHourParam");
				filters.add("hour >= fromHourParam");
				parameters.put("fromHourParam", fromHour);
			}

			if( toHour != null ) {
				declaredParams.add("String toHourParam");
				filters.add("hour <= toHourParam");
				parameters.put("toHourParam", toHour);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			// Adds a cursor to the ranged query
			if( range != null ) 
				query.setRange(range.getFrom(), range.getTo());

			// Sets order as required
			if( StringUtils.hasText(order)) query.setOrdering(order);

			@SuppressWarnings("unchecked")
			List<StoreRevenueByHour> objs = (List<StoreRevenueByHour>) (parameters.size() > 0 ?
					query.executeWithMap(parameters) : query.execute());
			if (objs != null) {
				// force to read
				for (StoreRevenueByHour obj : objs) {
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
	public StoreRevenueByHour getUsingStoreIdAndDateAndHour(String storeId, String date, String hour,
			boolean detachable) throws ASException {

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

			// Date parameter
			if( date != null ) {
				declaredParams.add("String hourParam");
				filters.add("hour == hourParam");
				parameters.put("hourParam", hour);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			@SuppressWarnings("unchecked")
			List<StoreRevenueByHour> objs = (List<StoreRevenueByHour>) (parameters.size() > 0 ?
					query.executeWithMap(parameters) : query.execute());
			if (objs != null) {
				// force to read
				for (StoreRevenueByHour obj : objs) {
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
