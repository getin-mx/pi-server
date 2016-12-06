package mobi.allshoppings.dao.spi;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import mobi.allshoppings.dao.CampaignSpecialDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.CampaignSpecial;
import mobi.allshoppings.tools.CollectionFactory;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

public class CampaignSpecialDAOJDOImpl extends GenericDAOJDO<CampaignSpecial> implements CampaignSpecialDAO {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(CampaignSpecialDAOJDOImpl.class.getName());

	public CampaignSpecialDAOJDOImpl() {
		super(CampaignSpecial.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createNumericUniqueKey(CampaignSpecial.class);
	}
	
	/**
	 * Get a list of campaign specials instances using its shoppingId and status
	 * 
	 * @param shoppingId
	 *            The Identifier of the shopping that the store belongs to
	 * @param status
	 *            A list of statuses to filter. If null, ignores the status
	 *            property
	 * @param order
	 *            The property that will be used to order the obtained dataset
	 */
	@Override
	public List<CampaignSpecial> getUsingShoppingAndStatus(String shoppingId, List<Integer> status, String order) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<CampaignSpecial> ret = CollectionFactory.createList();

		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(CampaignSpecial.class);

			// Shopping parameters
			if( StringUtils.hasText(shoppingId)) {
				declaredParams.add("String shoppingIdParam");
				filters.add("shoppings.contains(shoppingIdParam)");
				parameters.put("shoppingIdParam", shoppingId);
			}

			// Status parameters
			if( status != null && status.size() > 0 ) {
				filters.add(toListFilterCriteria("status", status, false));
			}

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			if(StringUtils.hasText(order)) query.setOrdering(order);
			
			// Executes the query
			@SuppressWarnings("unchecked")
			List<CampaignSpecial> objs = (List<CampaignSpecial>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (CampaignSpecial obj : objs) {
					ret.add(obj);
				}
			}
			
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}
		
		return ret;
	}
	
	/**
	 * Get a list of campaign specials instances using its brandId and status
	 * 
	 * @param brandId
	 *            The Identifier of the brand that the store belongs to
	 * @param status
	 *            A list of statuses to filter. If null, ignores the status
	 *            property
	 * @param order
	 *            The property that will be used to order the obtained dataset
	 */
	@Override
	public List<CampaignSpecial> getUsingBrandAndStatus(String brandId, List<Integer> status, String order) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<CampaignSpecial> ret = CollectionFactory.createList();

		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(CampaignSpecial.class);

			// Shopping parameters
			if( StringUtils.hasText(brandId)) {
				declaredParams.add("String brandIdParam");
				filters.add("brands.contains(brandIdParam)");
				parameters.put("brandIdParam", brandId);
			}

			// Status parameters
			if( status != null && status.size() > 0 ) {
				filters.add(toListFilterCriteria("status", status, false));
			}

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			if(StringUtils.hasText(order)) query.setOrdering(order);
			
			// Executes the query
			@SuppressWarnings("unchecked")
			List<CampaignSpecial> objs = (List<CampaignSpecial>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (CampaignSpecial obj : objs) {
					ret.add(obj);
				}
			}
			
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}
		
		return ret;
	}

	/**
	 * Get a list of campaign specials instances using its storeId and status
	 * 
	 * @param storeId
	 *            The Identifier of the selected store
	 * @param status
	 *            A list of statuses to filter. If null, ignores the status
	 *            property
	 * @param order
	 *            The property that will be used to order the obtained dataset
	 */
	@Override
	public List<CampaignSpecial> getUsingStoreAndStatus(String storeId, List<Integer> status, String order) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<CampaignSpecial> ret = CollectionFactory.createList();

		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(CampaignSpecial.class);

			// Shopping parameters
			if( StringUtils.hasText(storeId)) {
				declaredParams.add("String storeIdParam");
				filters.add("stores.contains(storeIdParam)");
				parameters.put("storeIdParam", storeId);
			}

			// Status parameters
			if( status != null && status.size() > 0 ) {
				filters.add(toListFilterCriteria("status", status, false));
			}

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			if(StringUtils.hasText(order)) query.setOrdering(order);
			
			// Executes the query
			@SuppressWarnings("unchecked")
			List<CampaignSpecial> objs = (List<CampaignSpecial>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (CampaignSpecial obj : objs) {
					ret.add(obj);
				}
			}
			
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}
		
		return ret;
	}
}
