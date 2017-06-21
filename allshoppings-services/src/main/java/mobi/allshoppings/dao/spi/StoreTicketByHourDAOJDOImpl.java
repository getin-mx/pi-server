package mobi.allshoppings.dao.spi;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.StoreTicketByHourDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.StoreTicketByHour;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

public class StoreTicketByHourDAOJDOImpl extends GenericDAOJDO<StoreTicketByHour> implements StoreTicketByHourDAO {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(StoreTicketByHourDAOJDOImpl.class.getName());

	public StoreTicketByHourDAOJDOImpl() {
		super(StoreTicketByHour.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(StoreTicketByHour.class);
	}

	@Override
	public List<StoreTicketByHour> getUsingStoreIdAndDateAndRange(String storeId, String date, String fromHour, String toHour, Range range, String order, boolean detachable) throws ASException {
		List<StoreTicketByHour> returnedObjs = CollectionFactory.createList();

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
			List<StoreTicketByHour> objs = parameters.size() > 0 ? (List<StoreTicketByHour>)query.executeWithMap(parameters) : (List<StoreTicketByHour>)query.execute();
			if (objs != null) {
				// force to read
				for (StoreTicketByHour obj : objs) {
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
	public StoreTicketByHour getUsingStoreIdAndDateAndHour(String storeId, String date, String hour, boolean detachable) throws ASException {

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
			List<StoreTicketByHour> objs = parameters.size() > 0 ? (List<StoreTicketByHour>)query.executeWithMap(parameters) : (List<StoreTicketByHour>)query.execute();
			if (objs != null) {
				// force to read
				for (StoreTicketByHour obj : objs) {
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
