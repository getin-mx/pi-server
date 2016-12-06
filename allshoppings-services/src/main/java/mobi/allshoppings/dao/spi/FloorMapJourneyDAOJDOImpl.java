package mobi.allshoppings.dao.spi;

import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.FloorMapJourneyDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.FloorMapJourney;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

public class FloorMapJourneyDAOJDOImpl extends GenericDAOJDO<FloorMapJourney> implements FloorMapJourneyDAO {

	public FloorMapJourneyDAOJDOImpl() {
		super(FloorMapJourney.class);
	}

	/**
	 * Creates a new unique key for an object of this entity kind, using a
	 * Hash of the query that this object refers as the unique key
	 */
	@Override
	public Key createKey(FloorMapJourney obj) throws ASException {
		String hashKey = obj.getMac() + ":" + obj.getFloorMapId() + ":" + obj.getDate();
		return keyHelper.obtainKey(FloorMapJourney.class, hashKey);
	}

	/**
	 * Returns a list of FloorMapJourneys according the following filters
	 * 
	 * @param floorMapId
	 *            The map to scan for
	 * @param mac
	 *            The mac address to scan for
	 * @param fromDate
	 *            Lower Date Limit (inclusive). Date format is yyyy-MM-dd
	 * @param toDate
	 *            Higher Date Limit (inclusive). Date format is yyyy-MM-dd
	 * @return A list of *detached* FloorMapJourneys
	 * @throws ASException
	 */
	@Override
	public List<FloorMapJourney> getUsingFloorMapAndMacAndDate(String floorMapId, String mac, String fromDate, String toDate, Range range, String order) throws ASException {

		List<FloorMapJourney> returnedObjs = CollectionFactory.createList();
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(clazz);
			
			// check for floorMap parameter
			if( StringUtils.hasText(floorMapId)) {
				declaredParams.add("String floorMapIdParam");
				filters.add("floorMapId == floorMapIdParam");
				parameters.put("floorMapIdParam", floorMapId);
			}
			
			// check for mac parameter
			if( StringUtils.hasText(mac)) {
				declaredParams.add("String macParam");
				filters.add("mac == macParam");
				parameters.put("macParam", mac);
			}

			// check for fromDate parameter
			if( StringUtils.hasText(fromDate)) {
				declaredParams.add("String fromDateParam");
				filters.add("date >= fromDateParam");
				parameters.put("fromDateParam", fromDate);
			}

			// check for toDate parameter
			if( StringUtils.hasText(fromDate)) {
				declaredParams.add("String toDateParam");
				filters.add("date <= toDateParam");
				parameters.put("toDateParam", toDate);
			}

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			if( null != range )
				query.setRange(range.getFrom(), range.getTo());

			if( StringUtils.hasText(order))
				query.setOrdering(order);
			
			@SuppressWarnings("unchecked")
			List<FloorMapJourney> objs = (List<FloorMapJourney>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (FloorMapJourney obj : objs) {
					returnedObjs.add(pm.detachCopy(obj));
				}
			}
			
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
	    } finally  {
	    	pm.close();
	    }
		
		return returnedObjs;

	}

}
