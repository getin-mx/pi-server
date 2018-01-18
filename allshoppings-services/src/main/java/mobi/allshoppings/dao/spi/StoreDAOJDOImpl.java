package mobi.allshoppings.dao.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.auth.UserInfo;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CustomDatatableFilter;
import mobi.allshoppings.tx.PersistenceProvider;

public class StoreDAOJDOImpl extends GenericDAOJDO<Store> implements StoreDAO {

	@Autowired
	private GeoCodingHelper geocoder;
	
	public StoreDAOJDOImpl() {
		super(Store.class);
	}

	/**
	 * Creates a new unique key for the store, based in the shopping Id and the
	 * store name
	 */
	@Override
	public Key createKey(String shoppingId, String brandId) throws ASException {
		return keyHelper.createNumericUniqueKey(clazz);
	}

	@Override
	public Key createKey(String brandId) throws ASException {
		return keyHelper.createNumericUniqueKey(clazz);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(clazz);
	}

	/**
	 * @see mobi.allshoppings.dao.spi.GenericDAOJDO#delete(PersistenceProvider,
	 *      String) The difference with its parent method, is that this method
	 *      removes the associated GeoEntity to perform a clean up
	 */
	@Override
	public void delete(PersistenceProvider pp, String identifier) throws ASException {
		super.delete(pp, identifier);
		geocoder.removeGeoEntity(identifier, EntityKind.KIND_STORE);
	}

	/**
	 * Get a list of store instances using its shoppingId and status
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
	public List<Store> getUsingShoppingAndStatus(String shoppingId, List<Byte> status, String order) throws ASException {
		return getUsingBrandAndShoppingAndUserAndStatus(null, shoppingId, null, status, order);
	}

	/**
	 * Get a list of store instances using its brandId, a user (to know the
	 * selected view location) and status
	 * 
	 * @param user
	 *            A user instance, used to know the physical location to show
	 * @param brandId
	 *            The Identifier of the brand that the store belongs to
	 * @param status A list of statuses to filter. If null, ignores the status
	 *         property
	 * @param order The property that will be used to order the obtained dataset
	 */
	@Override
	public List<Store> getUsingUserAndBrandAndStatus(User user, String brandId, List<Byte> status, String order) throws ASException {
		return getUsingBrandAndShoppingAndUserAndStatus(brandId, null, user, status, order);
	}

	/**
	 * Get a list of store instances using a combination of its brandId,
	 * shoppingId and status
	 * 
	 * @param brandId
	 *            The Identifier of the brand that the store belongs to
	 * @param shoppingId
	 *            The Identifier of the shopping that the store belongs to
	 * @param status
	 *            A list of statuses to filter. If null, ignores the status
	 *            property
	 * @param order
	 *            The property that will be used to order the obtained dataset
	 */
	@Override
	public List<Store> getUsingBrandAndShoppingAndStatus(String brandId,
			String shoppingId, List<Byte> status, String order) throws ASException {
		return getUsingBrandAndShoppingAndUserAndStatus(brandId, shoppingId, null, status, order);
	}

	/**
	 * Get a list of store instances using a combination of its brandId and
	 * status
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
	public List<Store> getUsingBrandAndStatus(String brandId, List<Byte> status, String order) throws ASException {
		return getUsingBrandAndShoppingAndUserAndStatus(brandId, null, null, status, order);
	}

	/**
	 * Get a list of store instances using a combination of its shoppingId,
	 * brandId, a user (to know the current view location) and status
	 * 
	 * @param brandId
	 *            The Identifier of the brand that the store belongs to
	 * @param shoppingId
	 *            The Identifier of the shopping that the store belongs to
	 * @param user
	 *            A user instance, used to know the physical location to show
	 * @param status
	 *            A list of statuses to filter. If null, ignores the status
	 *            property
	 * @param order
	 *            The property that will be used to order the obtained dataset
	 */
	private List<Store> getUsingBrandAndShoppingAndUserAndStatus(String brandId,
			String shoppingId, User user, List<Byte> status, String order) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<Store> ret = new ArrayList<Store>();

		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(Store.class);

			// Brand parameters
			if( StringUtils.hasText(brandId)) {
				declaredParams.add("String brandIdParam");
				filters.add("brandId == brandIdParam");
				parameters.put("brandIdParam", brandId);
			}

			// Shopping parameters
			if( StringUtils.hasText(shoppingId)) {
				declaredParams.add("String shoppingIdParam");
				filters.add("shoppingId == shoppingIdParam");
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
			List<Store> objs = (List<Store>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Store obj : objs) {
					ret.add(pm.detachCopy(obj));
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
	 * Get a store instance using an external ID interface
	 * 
	 * @param externalId
	 *            The external Identifier of the store 
	 */
	public Store getUsingExternalId(String externalId) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(Store.class);

			// External ID parameters
			declaredParams.add("String externalIdParam");
			filters.add("externalId == externalIdParam");
			parameters.put("externalIdParam", externalId);

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			// Executes the query
			@SuppressWarnings("unchecked")
			List<Store> objs = (List<Store>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Store obj : objs) {
					return (pm.detachCopy(obj));
				}
			}
			
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}

		throw ASExceptionHelper.notFoundException();
	}

	/**
	 * Get a list of street stores using Brand Identifier and a Status List
	 * 
	 * @param brandId
	 *            The brand Identifier to find
	 * @param status
	 *            The status list to select
	 * @param The
	 *            property that will be used to order the obtained dataset
	 * @return
	 * @throws ASException
	 */
	public List<Store> getStreetUsingBrandAndStatus(String brandId, List<Byte> status, String order) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<Store> ret = new ArrayList<Store>();

		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(Store.class);

			// Brand parameters
			if( StringUtils.hasText(brandId)) {
				declaredParams.add("String brandIdParam");
				filters.add("brandId == brandIdParam");
				parameters.put("brandIdParam", brandId);
			}

			// Status parameters
			if( status != null && status.size() > 0 ) {
				filters.add(toListFilterCriteria("status", status, false));
			}

			// Filter objects that are not inside malls
			filters.add("address.latitude != 0");
			
			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			if(StringUtils.hasText(order)) query.setOrdering(order);
			
			// Executes the query
			@SuppressWarnings("unchecked")
			List<Store> objs = (List<Store>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Store obj : objs) {
					ret.add(pm.detachCopy(obj));
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
	 * Get a list of store instances in its status, and limited by a range.
	 * 
	 * @param status
	 *            A list of Integer statuses to be used as a filter
	 * @param range
	 *            A range object to bound the query with. @see Range
	 */
	@Override
	public List<Store> getUsingStatus(List<Byte> status) throws ASException {
		return getUsingIdsAndStatus(null, status);
	}
	
	/**
	 * Get a list of store instances in its status, and limited by a range.
	 * 
	 * @param ids
	 *            A list of identifiers to be returned
	 * @param status
	 *            A list of Integer statuses to be used as a filter
	 * @param range
	 *            A range object to bound the query with. @see Range
	 */
	@Override
	public List<Store> getUsingIdsAndStatus(Collection<String> ids, List<Byte> status) throws ASException {
		// TODO unused method, remove or fix to get by ID as in GenericDAO and filted with status
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<Store> ret = CollectionFactory.createList();
		List<Key> findIn = CollectionFactory.createList();

		if( ids != null && ids.size() == 0 ) return ret;
		
		try {
			if( ids != null && ids.size() > 0 ) {
				for( String id : ids ) {
					findIn.add((Key)keyHelper.obtainKey(Store.class, id));
				}
			}
			
			Query query = pm.newQuery(Store.class);
			int count = null == status ? 0 : status.size();

			// Filter declaration
			StringBuffer filter = new StringBuffer();
			
			// Status filter
			if (count > 1) {
				filter.append("(");
				for (int idx = 0; idx < count; idx++) {
					filter.append("status == ").append(status.get(idx));
					if (idx+1 < count) {
						filter.append(" || ");
					}
				}
			} else {
				for (int idx = 0; idx < count; idx++) filter.append("status == ").append(status.get(idx));
			}

			Map<String, Object> parameters = CollectionFactory.createMap();
			if( findIn.size() > 0 ) {
				if( filter.length() > 0 ) filter.append(" && ");
				filter.append("_id.contains(key)");
				query.declareParameters("java.util.List _id");
				parameters.put("_id", findIn);
			}
			
			// Set Filters And Ranges
			if( !filter.toString().trim().equals("")) query.setFilter(filter.toString());
			
			// Executes the query
			@SuppressWarnings("unchecked")
			List<Store> objs = (List<Store>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Store obj : objs) {
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
	 * @see mobi.allshoppings.dao.spi.GenericDAOJDO#buildCustomFilter(UserInfo)
	 */
	@Override
	public CustomDatatableFilter buildCustomFilter(final UserInfo userInfo) {
		if( userInfo != null && userInfo.getRole() == UserSecurity.Role.ADMIN)
			return null;

		if( userInfo != null && userInfo.getRole() == UserSecurity.Role.SHOPPING) {
			return new CustomDatatableFilter() {
				@Override
				public void delegateFilter(Query query, Map<String, Object> parameters) {
					parameters.put("keysParam", userInfo.getShoppings());
					query.declareParameters("java.util.List keysParam");
					query.setFilter("keysParam.contains(shoppingId)");
				}
			};
		} else if( userInfo != null && userInfo.getRole() == UserSecurity.Role.BRAND) {
			return new CustomDatatableFilter() {
				@Override
				public void delegateFilter(Query query, Map<String, Object> parameters) {
					parameters.put("keysParam", userInfo.getBrands());
					query.declareParameters("java.util.List keysParam");
					query.setFilter("keysParam.contains(brandId)");
				}
			};
		} else {
			return super.buildCustomFilter(userInfo);
		}
	}

	/**
	 * @see mobi.allshoppings.dao.spi.GenericDAOJDO#safeAndInLimits(mobi.allshoppings.model.interfaces.ModelKey,
	 *      UserInfo)
	 */
	@Override
	public boolean safeAndInLimits(Store obj, UserInfo userInfo) {
		if( userInfo != null && userInfo.getRole() == UserSecurity.Role.ADMIN)
			return true;
		
		if( userInfo != null && userInfo.getRole() == UserSecurity.Role.SHOPPING) {
			return userInfo.getShoppings().contains(obj.getShoppingId());
		}

		if( userInfo != null && userInfo.getRole() == UserSecurity.Role.BRAND) {
			return userInfo.getBrands().contains(obj.getBrandId());
		}

		return super.safeAndInLimits(obj, userInfo);
	}

	/**
	 * Get a list of instances of this entity using a list of identifiers as key
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param idList
	 *            The Identifiers list that will be used to select the instances
	 *            that will be returned
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public List<String> getBrandIdsUsingIdList(PersistenceProvider pp, List<String> idList,
			boolean detachable) throws ASException {
		
		List<String> ret = CollectionFactory.createList();
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			List<String> ids = CollectionFactory.createList();
			for( String id : idList ) {
				if(!ids.contains(id.toUpperCase()))
					ids.add(id.toUpperCase());
			}
			
			Query query = pm.newQuery(clazz);

			// idList Parameter
			if(!CollectionUtils.isEmpty(idList)) {
				declaredParams.add("java.util.List idListParam");
				filters.add("idListParam.contains(uIdentifier)");
				parameters.put("idListParam", ids);
			}

			query.setResult("brandId");
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>)query.executeWithMap(parameters);
			for(String obj : list ) {
				if(!ret.contains(obj))
					ret.add(obj);
			}
			
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
}
