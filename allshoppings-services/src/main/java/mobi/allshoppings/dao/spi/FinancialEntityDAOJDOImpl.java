package mobi.allshoppings.dao.spi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.auth.UserInfo;
import mobi.allshoppings.dao.FinancialEntityDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.FinancialEntity;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity;
import mobi.allshoppings.model.tools.CountryHelper;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CustomDatatableFilter;
import mobi.allshoppings.tools.Range;

public class FinancialEntityDAOJDOImpl extends GenericDAOJDO<FinancialEntity> implements FinancialEntityDAO {
	private static final Logger log = Logger.getLogger(FinancialEntityDAOJDOImpl.class.getName());

	public FinancialEntityDAOJDOImpl() {
		super(FinancialEntity.class);
	}

	/**
	 * Creates a new unique key for the financial entity, using its name as seed
	 * for the key
	 */
	@Override
	public Key createKey(String feName, String country) throws ASException {

		try {
			if(feName == null || feName.equals("")){
				throw ASExceptionHelper.notAcceptedException();
			}
			String code = CountryHelper.getCountryCode(country);
			String name = StringUtils.hasText(code) ? feName + "_" + code : feName;

			PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

			String sKey = keyHelper.resolveKey(name);
			int seq = 0;
			while(doesEntityExist(pm, clazz, (Key)keyHelper.obtainKey(FinancialEntity.class, sKey)) == true){
				sKey = keyHelper.resolveKey(name + "_" + seq);
				seq++;
			}

			return (Key)keyHelper.obtainKey(FinancialEntity.class, sKey);
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.notAcceptedException();
		}
	}

	/**
	 * Get a list of financial entity instances based on its View Location and
	 * Status.
	 * 
	 * @param vl
	 *            The ViewLocation object used as a filter. @see
	 *            mobi.allshoppings.model.tools.ViewLocation
	 * @param status
	 *            A list of integer representation of object statuses
	 */
	@Override 
	public List<FinancialEntity> getUsingViewLocationAndStatus(ViewLocation vl, List<Integer> status) throws ASException {
		return getUsingCountryAndStatus(vl.getCountry(), status);
	}
	
	/**
	 * Get a list of financial entity instances based on its physical country
	 * and Status.
	 * 
	 * @param country
	 *            The country object used as a filter
	 * @param status
	 *            A list of integer representation of object statuses
	 */
	@Override
	public List<FinancialEntity> getUsingCountryAndStatus(String country, List<Integer> status) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<FinancialEntity> ret = CollectionFactory.createList();

		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(FinancialEntity.class);	

			if(StringUtils.hasText(country)) {
				declaredParams.add("String countryParam");
				filters.add("country == countryParam");
				parameters.put("countryParam", country);
			}

			if( status != null && status.size() > 0 ) {
				filters.add(toListFilterCriteria("status", status, false));
			}

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			query.setOrdering("name asc");
			
			@SuppressWarnings("unchecked")
			List<FinancialEntity> result = (List<FinancialEntity>)query.executeWithMap(parameters);

			for(FinancialEntity res : result) {
				ret.add(pm.detachCopy(res));
			}
			
			return ret;

		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}
	}

	/**
	 * Get a list of financial entity instances from the UserEntityCache based
	 * in its status, and limited by a range.
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
	public List<FinancialEntity> getUsingStatusAndRangeInCache(List<Integer> status, Range range, User user, int returnType, String order) throws ASException {
		return getUsingIdsAndStatusAndRangeInCache(null, status, range, user, returnType, order);
	}
	
	/**
	 * Get a list of financial entity instances from the UserEntityCache based
	 * in its status, and limited by a range.
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
	public List<FinancialEntity> getUsingIdsAndStatusAndRangeInCache(Collection<String> ids, List<Integer> status, Range range, User user, int returnType, String order) throws ASException {
		
		List<String> idList = CollectionFactory.createList();
		idList.addAll(ids);
		return getUsingIdList(idList);

	}

	/**
	 * @see mobi.allshoppings.dao.spi.GenericDAOJDO#buildCustomFilter(UserInfo)
	 */
	@Override
	public CustomDatatableFilter buildCustomFilter(final UserInfo userInfo) {
		if( userInfo != null && userInfo.getRole() == UserSecurity.Role.ADMIN)
			return null;
		else {
			return super.buildCustomFilter(userInfo);
		}
	}

	/**
	 * @see mobi.allshoppings.dao.spi.GenericDAOJDO#safeAndInLimits(mobi.allshoppings.model.interfaces.ModelKey,
	 *      UserInfo)
	 */
	@Override
	public boolean safeAndInLimits(FinancialEntity obj, UserInfo userInfo) {
		if( userInfo != null && userInfo.getRole() == UserSecurity.Role.ADMIN)
			return true;
		
		return super.safeAndInLimits(obj, userInfo);
	}
}
