package mobi.allshoppings.dao.spi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.auth.UserInfo;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserEntityCache;
import mobi.allshoppings.model.UserSecurity;
import mobi.allshoppings.model.tools.CountryHelper;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CustomDatatableFilter;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;
import mobi.allshoppings.uec.UserEntityCacheBzService;

public class BrandDAOJDOImpl extends GenericDAOJDO<Brand> implements BrandDAO {

	private static final Logger log = Logger.getLogger(BrandDAOJDOImpl.class.getName());

	@Autowired
	private UserEntityCacheBzService uecService;

	public BrandDAOJDOImpl() {
		super(Brand.class);
	}

	/**
	 * Creates a new unique key for the brand, using the brand name as a seed
	 * for the key
	 */
	@Override
	public Key createKey(String brandName, String country) throws ASException {

		try {
			if(brandName == null || brandName.equals("")) {
				throw ASExceptionHelper.notAcceptedException();
			}
			String code = CountryHelper.getCountryCode(country);
			String name = StringUtils.hasText(code) ? brandName + "_" + code : brandName;
					
			PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

			String sKey = keyHelper.resolveKey(name);
			int seq = 0;
			while(doesEntityExist(pm, clazz, (Key)keyHelper.obtainKey(Brand.class, sKey)) == true){
				sKey = keyHelper.resolveKey(name + "_" + seq);
				seq++;
			}

			return (Key)keyHelper.obtainKey(Brand.class, sKey);
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.notAcceptedException();
		}
	}

	/**
	 * Get a list of brand instances from the UserEntityCache based in its
	 * status, and limited by a range.
	 * 
	 * @param status
	 *            A list of Integer statuses to be used as a filter
	 * @param range
	 *            A range object to bound the query with. @see Range
	 * @param user
	 *            The user for that the instances are retrieved
	 * @param returnType
	 *            The type of return requested. it can be any of
	 *            UserEntityCache.TYPE_BUNDLE,
	 *            UserEntityCache.TYPE_FAVORITES_FIRST,
	 *            UserEntityCache.TYPE_FAVORITES_ONLY or
	 *            UserEntityCache.TYPE_NORMAL_SORT
	 * @param order
	 *            The property that will be used as order parameter.
	 */
	@Override
	public List<Brand> getUsingStatusAndRangeInCache(List<Integer> status, Range range, User user, int returnType) throws ASException {
		return getUsingIdsAndStatusAndRangeInCache(null, status, range, user, returnType);
	}
	
	/**
	 * Get a list of brand instances from the UserEntityCache based in its
	 * status, and limited by a range.
	 * 
	 * @param ids
	 *            A list of identifiers to be returned
	 * @param status
	 *            A list of Integer statuses to be used as a filter
	 * @param range
	 *            A range object to bound the query with. @see Range
	 * @param user
	 *            The user for that the instances are retrieved
	 * @param returnType
	 *            The type of return requested. it can be any of
	 *            UserEntityCache.TYPE_BUNDLE,
	 *            UserEntityCache.TYPE_FAVORITES_FIRST,
	 *            UserEntityCache.TYPE_FAVORITES_ONLY or
	 *            UserEntityCache.TYPE_NORMAL_SORT
	 * @param order
	 *            The property that will be used as order parameter.
	 */
	@Override
	public List<Brand> getUsingIdsAndStatusAndRangeInCache(Collection<String> ids, List<Integer> status, Range range, User user, int returnType) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<Brand> ret = CollectionFactory.createList();
		List<String> findIn = CollectionFactory.createList();
		List<String> tmpList = CollectionFactory.createList();

		if( ids != null && ids.size() == 0 ) return ret;

		try {
			UserEntityCache uec = uecService.get(user, EntityKind.KIND_BRAND, returnType);
			log.log(Level.INFO, "User entity cache for BrandDAO retrieved");
			
			// If we have a id list restriction... let's apply it!
			if( ids != null && ids.size() > 0 ) {
				for( String id : uec.getEntities()) {
					if( ids.contains(id)) {
						tmpList.add(id);
					}
				}
			} else {
				tmpList = uec.getEntities();
			}
			
			// Now we set the cached entities in the find list
			if( range != null ) {
				for( int i = range.getFrom(); i < range.getTo() && i < tmpList.size(); i++ ) {
					findIn.add(((Key)keyHelper.obtainKey(Brand.class, tmpList.get(i))).getName().toUpperCase());
				}
			} else {
				for( String id : tmpList ) {
					findIn.add(((Key)keyHelper.obtainKey(Brand.class, id)).getName().toUpperCase());
				}
			}

			// If we got here with no elements... there are no elements to get... so bye!
			if( findIn.size() == 0 ) return ret;

			Query query = pm.newQuery(Brand.class);
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
				filter.append("keysParam.contains(uIdentifier)");
				query.declareParameters("java.util.List keysParam");
				parameters.put("keysParam", findIn);
			}
			
			// Set Filters And Ranges
			if( !filter.toString().trim().equals("")) query.setFilter(filter.toString());
			
			// Executes the query
			@SuppressWarnings("unchecked")
			List<Brand> objs = (List<Brand>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Brand obj : objs) {
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
	 * Get a list of brand instances based on its View Location and Status.
	 * 
	 * @param vl
	 *            The ViewLocation object used as a filter. @see
	 *            mobi.allshoppings.model.tools.ViewLocation
	 * @param status
	 *            A list of integer representation of object statuses
	 * @param order
	 *            The property that will be used as order parameter.
	 */
	@Override
	public List<Brand> getByViewLocationAndStatus(ViewLocation vl, List<Integer> status, String order) throws ASException {
		return getByViewLocationAndStatus(null, vl, status, order, true);
	}

	/**
	 * Get a list of brand instances based on its View Location and Status.
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param vl
	 *            The ViewLocation object used as a filter. @see
	 *            mobi.allshoppings.model.tools.ViewLocation
	 * @param status
	 *            A list of integer representation of object statuses
	 * @param order
	 *            The property that will be used as order parameter.
	 */
	@Override
	public List<Brand> getByViewLocationAndStatus(PersistenceProvider pp, ViewLocation vl, List<Integer> status, String order) throws ASException {
		return getByViewLocationAndStatus(pp, vl, status, order, true);
	}

	/**
	 * Get a list of brand instances based on its View Location and Status.
	 * 
	 * @param vl
	 *            The ViewLocation object used as a filter. @see
	 *            mobi.allshoppings.model.tools.ViewLocation
	 * @param status
	 *            A list of integer representation of object statuses
	 * @param order
	 *            The property that will be used as order parameter.
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public List<Brand> getByViewLocationAndStatus(ViewLocation vl, List<Integer> status, String order, boolean detachable) throws ASException {
		return getByViewLocationAndStatus(null, vl, status, order, detachable);
	}

	@Override
	public List<Brand> getUsingStatusAndRangeAndCountry(List<Integer> status, Range range, String country, String order, Map<String, String> attributes, boolean detachable) throws ASException {
		List<Brand> returnedObjs = CollectionFactory.createList();
		
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(clazz);
			if( StringUtils.hasText(country)) {
				declaredParams.add("String countryParam");
				filters.add("uCountry == countryParam");
				parameters.put("countryParam", country.toUpperCase());
			}
			
			if( status != null && status.size() > 0 ) {
				filters.add(toListFilterCriteria("status", status, false));
			}

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			// Do a counting of records
			if( attributes != null ) {
				query.setResult("count(this)");
				Long count = Long.parseLong(query.executeWithMap(parameters).toString());
				attributes.put("recordCount", String.valueOf(count));
				query.setResult(null);
			}

			// Set additional parameters
			if( StringUtils.hasText(order)) query.setOrdering(order);
			if( range != null )
				query.setRange(range.getFrom(), range.getTo());
			
			@SuppressWarnings("unchecked")
			List<Brand> objs = (List<Brand>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Brand obj : objs) {
					if( detachable )
						returnedObjs.add(pm.detachCopy(obj));
					else
						returnedObjs.add(obj);
				}
			}
			
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
	    } finally  {
	    	pm.close();
	    }
		
		return returnedObjs;
	}

	/**
	 * Get a list of brand instances based on its View Location and Status.
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param vl
	 *            The ViewLocation object used as a filter. @see
	 *            mobi.allshoppings.model.tools.ViewLocation
	 * @param status
	 *            A list of integer representation of object statuses
	 * @param order
	 *            The property that will be used as order parameter.
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public List<Brand> getByViewLocationAndStatus(PersistenceProvider pp, ViewLocation vl, List<Integer> status, String order, boolean detachable) throws ASException {

		List<Brand> returnedObjs = CollectionFactory.createList();
		
		PersistenceManager pm;
		if( pp == null ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(clazz);
			if( vl != null && StringUtils.hasText(vl.getCountry())) {
				declaredParams.add("String countryParam");
				filters.add("country == countryParam");
				parameters.put("countryParam", vl.getCountry());
			}
			
			if( status != null && status.size() > 0 ) {
				filters.add(toListFilterCriteria("status", status, false));
			}

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			if( StringUtils.hasText(order)) query.setOrdering(order);

			@SuppressWarnings("unchecked")
			List<Brand> objs = (List<Brand>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Brand obj : objs) {
					if( detachable )
						returnedObjs.add(pm.detachCopy(obj));
					else
						returnedObjs.add(obj);
				}
			}
			
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
	    } finally  {
	    	if( null == pp ) pm.close();
	    }
		
		return returnedObjs;
	}

	/**
	 * @see mobi.allshoppings.dao.spi.GenericDAOJDO#buildCustomFilter(UserInfo)
	 */
	@Override
	public CustomDatatableFilter buildCustomFilter(final UserInfo userInfo) {
		if( userInfo != null && userInfo.getRole() == UserSecurity.Role.ADMIN)
			return null;
		
		if( userInfo != null && userInfo.getRole() == UserSecurity.Role.BRAND) {
			return new CustomDatatableFilter() {
				@Override
				public void delegateFilter(Query query, Map<String, Object> parameters) {
					parameters.put("keysParam", userInfo.getBrands());
					query.declareParameters("java.util.List keysParam");
					query.setFilter("keysParam.contains(key)");
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
	public boolean safeAndInLimits(Brand obj, UserInfo userInfo) {
		if( userInfo != null && userInfo.getRole() == UserSecurity.Role.ADMIN)
			return true;
		
		if( userInfo != null && userInfo.getRole() == UserSecurity.Role.BRAND) {
			return userInfo.getBrands().contains(obj.getIdentifier());
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
	public List<Brand> getUsingIdList(PersistenceProvider pp, List<String> idList,
			boolean detachable) throws ASException {
		
		List<Brand> ret = CollectionFactory.createList();
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

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			@SuppressWarnings("unchecked")
			List<Brand> list = (List<Brand>)query.executeWithMap(parameters);
			for(Brand obj : list ) {
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
