package mobi.allshoppings.dao.spi;

import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.commons.collections.CollectionUtils;

import mobi.allshoppings.auth.UserInfo;
import mobi.allshoppings.dao.FloorMapDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.FloorMap;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CustomDatatableFilter;
import mobi.allshoppings.tools.Range;

import com.inodes.datanucleus.model.Key;

public class FloorMapDAOJDOImpl extends GenericDAOJDO<FloorMap> implements FloorMapDAO {

	public FloorMapDAOJDOImpl() {
		super(FloorMap.class);
	}

	/**
	 * Creates a new unique key for an object of this entity kind, using a
	 * Hash of the query that this object refers as the unique key
	 */
	@Override
	public Key createKey(FloorMap obj) throws ASException {
		return keyHelper.createStringUniqueKey(FloorMap.class);
	}

	@Override
	public List<FloorMap> getUsingStatusAndShoppingId(Integer status, String shoppingId) throws ASException {

		List<FloorMap> returnedObjs = CollectionFactory.createList();
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(clazz);
			declaredParams.add("String shoppingIdParam");
			filters.add("shoppingId == shoppingIdParam");
			parameters.put("shoppingIdParam", shoppingId);
			
			
			if( status != null ) {
				declaredParams.add("Integer statusParam");
				filters.add("status == statusParam");
				parameters.put("statusParam", status);
			}

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			@SuppressWarnings("unchecked")
			List<FloorMap> objs = (List<FloorMap>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (FloorMap obj : objs) {
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

	@Override
	public List<FloorMap> getUsingStatusAndUserAndRange(Integer status, User user, Range range) throws ASException {

		List<FloorMap> returnedObjs = CollectionFactory.createList();
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(clazz);
			if( user.getSecuritySettings().getRole() != Role.ADMIN) {
				if(CollectionUtils.isEmpty(user.getSecuritySettings().getShoppings())) return returnedObjs;
				
				declaredParams.add("java.util.List keyListParam");
				filters.add("keyListParam.contains(shoppingId)");
				parameters.put("keyListParam", user.getSecuritySettings().getShoppings());
			}
			
			if( status != null ) {
				declaredParams.add("Integer statusParam");
				filters.add("status == statusParam");
				parameters.put("statusParam", status);
			}

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			if( range != null ) {
				query.setRange(range.getFrom(), range.getTo());
			}
			
			@SuppressWarnings("unchecked")
			List<FloorMap> objs = (List<FloorMap>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (FloorMap obj : objs) {
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
		} else {
			return super.buildCustomFilter(userInfo);
		}
	}

	/**
	 * @see mobi.allshoppings.dao.spi.GenericDAOJDO#safeAndInLimits(mobi.allshoppings.model.interfaces.ModelKey,
	 *      UserInfo)
	 */
	@Override
	public boolean safeAndInLimits(FloorMap obj, UserInfo userInfo) {
		if( userInfo != null && userInfo.getRole() == UserSecurity.Role.ADMIN)
			return true;
		
		if( userInfo != null && userInfo.getRole() == UserSecurity.Role.SHOPPING) {
			return userInfo.getShoppings().contains(obj.getShoppingId());
		}
		
		return super.safeAndInLimits(obj, userInfo);
	}

}
