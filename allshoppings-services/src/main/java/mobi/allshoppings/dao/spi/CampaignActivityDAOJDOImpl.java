package mobi.allshoppings.dao.spi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Cursor;
import com.inodes.datanucleus.model.JDOCursorHelper;
import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.auth.UserInfo;
import mobi.allshoppings.dao.CampaignActivityDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.CampaignSpecial;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public class CampaignActivityDAOJDOImpl extends GenericDAOJDO<CampaignActivity> implements CampaignActivityDAO {

	private static final Logger log = Logger.getLogger(CampaignActivityDAOJDOImpl.class.getName());

	public CampaignActivityDAOJDOImpl() {
		super(CampaignActivity.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createNumericUniqueKey(CampaignActivity.class);
	}

	@Override
	public boolean hasSpecialBeenUsedForUserAndDate(CampaignSpecial campaignSpecial, User user, Date date) throws ASException {
		long dailyCount = countDeliveredUsignCampaignSpecialAndUserAndDate(campaignSpecial, user, date);
		log.log(Level.INFO, "campaignSpecial is " + campaignSpecial.toString());
		log.log(Level.INFO, "user is " + user.toString());
		log.log(Level.INFO, "date is " + date);
		log.log(Level.INFO, "dailyCount is " + dailyCount);
		if( dailyCount > 0 ) return true;
		return false;
	}

	@Override
	public boolean hasAvailabilityForDate(CampaignSpecial campaignSpecial, Date date) throws ASException {

		long count = countDeliveredUsignCampaignSpecial(campaignSpecial);
		long dailyCount = countDeliveredUsignCampaignSpecialAndUserAndDate(campaignSpecial, null, date);
		if((campaignSpecial.getDailyQuantity() == 0 || dailyCount < campaignSpecial.getDailyQuantity()))
			if ((campaignSpecial.getQuantity() == 0 || count < campaignSpecial.getQuantity()))
				return true;
		
		return false;
		
	}

	@Override
	public long countDeliveredUsignCampaignSpecial(CampaignSpecial campaignSpecial) throws ASException {
		return countDeliveredUsignCampaignSpecialAndUserAndDate(campaignSpecial, null, null);
	}

	@Override
	public long countDeliveredUsignCampaignSpecialAndUserAndDate(CampaignSpecial campaignSpecial, User user, Date date) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(CampaignActivity.class);

			// Campaign Special Parameters
			if( campaignSpecial != null ) {
				declaredParams.add("String campaignSpecialIdParam");
				filters.add("campaignSpecialId == campaignSpecialIdParam");
				parameters.put("campaignSpecialIdParam", campaignSpecial.getIdentifier());
			}

			// Date parameters
			if( date != null ) {
				declaredParams.add("java.util.Date creationDateParam");
				filters.add("creationDate == creationDateParam");
				parameters.put("creationDateParam", DateUtils.truncate(date, Calendar.DATE));
			}

			// User parameters
			if( user != null ) {
				declaredParams.add("String userIdParam");
				filters.add("userId == userIdParam");
				parameters.put("userIdParam", user.getIdentifier());
			}
			
			// Setting query parameters
			log.log(Level.INFO, "Declared parameters: " + toParameterList(declaredParams));
			log.log(Level.INFO, "Filter: " + toWellParametrizedFilter(filters));
			log.log(Level.INFO, "Parameters: " + parameters.toString());
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			query.setResult("count(this)");
			Long results = Long.parseLong(query.executeWithMap(parameters).toString());
			log.log(Level.INFO, "result is " + results);
			
			return results;
			
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}
	}

	@Override
	public long count(String keyName, String keyValue, UserInfo userInfo) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Query query = pm.newQuery(clazz);
			query.setFilter(buildQueryFilter(keyName, keyValue));
			query.setResult("count(this)");
			Long results = Long.parseLong(query.execute().toString());
			return (long)results;
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
	    } finally  {
			pm.close();
	    }	
	}

	/**
	 * Get a list of instances of this entity according to a selected range.<br>
	 * This is mostly preferred against {@link #getAll()}
	 * 
	 * @param status
	 *            A list of status to select between the results
	 * @param range
	 *            The range object that bounds the query limits
	 * @param order
	 *            Property to use as order. If null, sets default order
	 */
	@Override
	public List<CampaignActivity> getUsingUserAndRedeemStatusAndRange(String userId, List<Integer> redeemStatus, Range range, String order) throws ASException {
		return getUsingUserAndRedeemStatusAndRange(null, userId, redeemStatus, false, range, order, true);
	}
	
	/**
	 * Get a list of instances of this entity according to a selected range.<br>
	 * This is mostly preferred against {@link #getAll()}
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param status
	 *            A list of status to select between the results
	 * @param range
	 *            The range object that bounds the query limits
	 * @param order
	 *            Property to use as order. If null, sets default order
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public List<CampaignActivity> getUsingUserAndRedeemStatusAndRange(
			PersistenceProvider pp, String userId, List<Integer> redeemStatus,
			boolean onlyDisplayableItems, Range range, String order,
			boolean detachable) throws ASException {

		List<CampaignActivity> returnedObjs = new ArrayList<CampaignActivity>();
		
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

			// User parameters
			if( userId != null ) {
				declaredParams.add("String userIdParam");
				filters.add("userId == userIdParam");
				parameters.put("userIdParam", userId);
			}

			// User parameters
			if( onlyDisplayableItems) {
				declaredParams.add("Boolean displayableParam");
				filters.add("displayable == displayableParam");
				parameters.put("displayableParam", true);
			}

			if( redeemStatus != null && redeemStatus.size() > 0 ) {
				StringBuffer sb  = new StringBuffer();
				boolean first = true;
				sb.append("(");
				for( Integer stat : redeemStatus ) {
					if(!first) sb.append(" || ");
					first = false;
					sb.append("redeemStatus == ").append(stat);
				}
				sb.append(")");
				filters.add(sb.toString());
			}
			
			query.setFilter(toWellParametrizedFilter(filters));
			query.declareParameters(toParameterList(declaredParams));
			
			// Adds a cursor to the ranged query
			if( range != null ) {
				if( StringUtils.hasText(range.getCursor())) {
					// Query q = the same query that produced the cursor
					// String cursorString = the string from storage
					Cursor cursor = Cursor.fromWebSafeString(range.getCursor());
					Map<String, Object> extensionMap = new HashMap<String, Object>();
					extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
					query.setExtensions(extensionMap);
					query.setRange(0, (range.getTo() - range.getFrom()));
				} else {
					query.setRange(range.getFrom(), range.getTo());
				}
			}

			// Sets order as required
			if( StringUtils.hasText(order)) query.setOrdering(order);
			
			@SuppressWarnings("unchecked")
			List<CampaignActivity> objs = (List<CampaignActivity>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (CampaignActivity obj : objs) {
					if( detachable )
						returnedObjs.add(pm.detachCopy(obj));
					else
						returnedObjs.add(obj);
				}
			}
			
			// Store the cursorString
			if( range != null ) {
				Cursor cursor = JDOCursorHelper.getCursor(objs);
				if( cursor != null)
					range.setCursor(cursor.toWebSafeString());
			}
			
		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
	    } finally  {
	    	if( null == pp ) pm.close();
	    }
		
		return returnedObjs;
	}

	/**
	 * Get a coupon using the generated coupon code
	 * 
	 * @param couponCode
	 *            The coupon code to find in the database 
	 */
	@Override
	public CampaignActivity getUsingCouponCode(String couponCode) throws ASException {
		return getUsingCouponCode(null, couponCode, true);
	}
	
	/**
	 * Get a coupon using the generated coupon code
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param couponCode
	 *            The coupon code to find in the database 
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public CampaignActivity getUsingCouponCode(PersistenceProvider pp, String couponCode, boolean detachable) throws ASException {

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

			// User parameters
			declaredParams.add("String couponCodeParam");
			filters.add("couponCode == couponCodeParam");
			parameters.put("couponCodeParam", couponCode);

			query.setFilter(toWellParametrizedFilter(filters));
			query.declareParameters(toParameterList(declaredParams));

			query.setRange(0,1);
			
			@SuppressWarnings("unchecked")
			List<CampaignActivity> objs = (List<CampaignActivity>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (CampaignActivity obj : objs) {
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
	    	if( null == pp ) pm.close();
	    }
	}


	/**
	 * Get last coupon. For testing proposes
	 */
	@Override
	public CampaignActivity getLast() throws ASException {

		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			
			Query query = pm.newQuery(clazz);
			query.setOrdering("creationDateTime DESC");
			query.setRange(0,1);
			
			@SuppressWarnings("unchecked")
			List<CampaignActivity> objs = (List<CampaignActivity>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (CampaignActivity obj : objs) {
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

	@Override
	public List<CampaignActivity> getForTableWidthKey(String keyName, String keyValue, String[] columnSort,
			String sortDirection, String[] searchFields, String search, long first,
			long last, UserInfo userInfo) throws ASException {
		List<CampaignActivity> returnedObjs = CollectionFactory.createList();
		
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Query query = pm.newQuery(CampaignActivity.class);
			if( columnSort.length > 0 ) {
				query.setOrdering(buildQuerySort(columnSort, sortDirection));
			}
			query.setRange(first, last);
			
			if( search != null && !search.equals("")) {
				query.setFilter(buildQueryFilter(columnSort, search, keyName, keyValue));
			} else {
				query.setFilter(buildQueryFilter(keyName, keyValue));
			}

			@SuppressWarnings("unchecked")
			List<CampaignActivity> objs = (List<CampaignActivity>)query.execute();
			if (objs != null) {
				// force to read
				for (CampaignActivity o : objs) {
					returnedObjs.add(o);
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
	public List<CampaignActivity> getUsingDatesAndCampaignSpecial(Date fromDate, Date toDate, String campaignSpecialId, Range range, String order) throws ASException {
		return getUsingDatesAndCampaignSpecial(null, fromDate, toDate, campaignSpecialId, range, null, true);
	}
	
	@Override
	public List<CampaignActivity> getUsingDatesAndCampaignSpecial(PersistenceProvider pp, Date fromDate, Date toDate, String campaignSpecialId, Range range, String order, boolean detachable) throws ASException {
		List<CampaignActivity> returnedObjs = new ArrayList<CampaignActivity>();

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

			// Campaign Special Parameter
			if( campaignSpecialId != null ) {
				declaredParams.add("String campaignSpecialIdParam");
				filters.add("campaignSpecialId == campaignSpecialIdParam");
				parameters.put("campaignSpecialIdParam", campaignSpecialId);
			}

			// From Date Parameter
			if( fromDate != null ) {
				declaredParams.add("java.util.Date fromDateParam");
				filters.add("creationDateTime >= fromDateParam");
				parameters.put("fromDateParam", fromDate);
			}

			// To Date Parameter
			if( fromDate != null ) {
				declaredParams.add("java.util.Date toDateParam");
				filters.add("creationDateTime <= toDateParam");
				parameters.put("toDateParam", toDate);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			// Adds a cursor to the ranged query
			if( range != null ) {
				if( StringUtils.hasText(range.getCursor())) {
					// Query q = the same query that produced the cursor
					// String cursorString = the string from storage
					Cursor cursor = Cursor.fromWebSafeString(range.getCursor());
					Map<String, Object> extensionMap = new HashMap<String, Object>();
					extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
					query.setExtensions(extensionMap);
					query.setRange(0, (range.getTo() - range.getFrom()));
				} else {
					query.setRange(range.getFrom(), range.getTo());
				}
			}

			// Sets order as required
			if( StringUtils.hasText(order)) query.setOrdering(order);

			@SuppressWarnings("unchecked")
			List<CampaignActivity> objs = parameters.size() > 0 ? (List<CampaignActivity>)query.executeWithMap(parameters) : (List<CampaignActivity>)query.execute();
			if (objs != null) {
				// force to read
				for (CampaignActivity obj : objs) {
					if( detachable )
						returnedObjs.add(pm.detachCopy(obj));
					else
						returnedObjs.add(obj);
				}
			}

			// Store the cursorString
			if( range != null ) {
				Cursor cursor = JDOCursorHelper.getCursor(objs);
				if( cursor != null )
					range.setCursor(cursor.toWebSafeString());
			}

		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			if( null == pp ) pm.close();
		}

		return returnedObjs;

	}
}
