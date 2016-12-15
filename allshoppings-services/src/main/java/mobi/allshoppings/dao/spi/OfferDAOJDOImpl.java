package mobi.allshoppings.dao.spi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Cursor;
import com.inodes.datanucleus.model.JDOCursorHelper;
import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.auth.UserInfo;
import mobi.allshoppings.dao.OfferDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Offer;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserEntityCache;
import mobi.allshoppings.model.UserSecurity;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.model.tools.impl.OfferCreationComparator;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CustomDatatableFilter;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;
import mobi.allshoppings.uec.UserEntityCacheBzService;

public class OfferDAOJDOImpl extends GenericDAOJDO<Offer> implements OfferDAO {

	@Autowired
	private UserEntityCacheBzService uecService;
	@Autowired
	private GeoCodingHelper geocoder;

	public OfferDAOJDOImpl() {
		super(Offer.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createNumericUniqueKey(Offer.class);
	}

	@Override
	public List<Offer> getUsingStatusAndRangeInCache(List<Integer> status, Range range, User user, int returnType) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<Offer> ret = CollectionFactory.createList();
		List<String> findIn = CollectionFactory.createList();
		
		try {
			UserEntityCache uec = uecService.get(user, EntityKind.KIND_OFFER, returnType);
			if( uec == null || uec.getEntities() == null ) {
				throw ASExceptionHelper.invalidArgumentsException("uec");
			}
			
			
			// Now we set the cached entities in the find list
			if( range != null ) {
				for( int i = range.getFrom(); i < range.getTo() && i < uec.getEntities().size(); i++ ) {
					findIn.add(((Key)keyHelper.obtainKey(Offer.class, uec.getEntities().get(i))).getName().toUpperCase());
				}
			} else {
				for( String id : uec.getEntities() ) {
					findIn.add(((Key)keyHelper.obtainKey(Shopping.class, id)).getName().toUpperCase());
				}
			}

			// If we got here with no elements... there are no elements to get... so bye!
			if( findIn.size() == 0 ) return ret;

			Query query = pm.newQuery(Offer.class);
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
				query.declareParameters("java.util.List keysParam");
				filter.append("keysParam.contains(uIdentifier)");
				parameters.put("keysParam", findIn);
			}
			
			// Set Filters And Ranges
			if( !filter.toString().trim().equals("")) query.setFilter(filter.toString());
			
			// Executes the query
			@SuppressWarnings("unchecked")
			List<Offer> objs = (List<Offer>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Offer obj : objs) {
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

	@Override
	public List<Offer> getActiveUsingBrandAndRange(String brandId, Range range) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<Offer> ret = CollectionFactory.createList();

		// Filter declaration
		Map<String, Object> parameters = CollectionFactory.createMap();
		List<String> declaredParams = CollectionFactory.createList();
		List<String> filters = CollectionFactory.createList();
		
		Query query = pm.newQuery(clazz);
		
		// Not expired
		declaredParams.add("Boolean expiredParam");
		filters.add("expired == expiredParam");
		parameters.put("expiredParam", false);

		if( StringUtils.hasText(brandId)) {
			declaredParams.add("String brandIdParam");
			filters.add("brands.contains(brandIdParam)");
			parameters.put("brandIdParam", brandId);
		}

		// Set query filters
		query.declareParameters(toParameterList(declaredParams));
		query.setFilter(toWellParametrizedFilter(filters));
		
		// And set ranges
		if( range != null ) {
			query.setRange(range.getFrom(), range.getTo());
		}
		
		try {
			// Executes the query
			@SuppressWarnings("unchecked")
			List<Offer> objs = (List<Offer>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Offer obj : objs) {
					ret.add(pm.detachCopy(obj));
				}
			}
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally  {
			pm.close();
		}
		
		return ret;

	}

	@Override
	public List<Offer> getActiveUsingShoppingAndRange(String shoppingId, Range range) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<Offer> ret = CollectionFactory.createList();

		// Filter declaration
		Map<String, Object> parameters = CollectionFactory.createMap();
		List<String> declaredParams = CollectionFactory.createList();
		List<String> filters = CollectionFactory.createList();
		
		Query query = pm.newQuery(clazz);

		// Not expired
		declaredParams.add("Boolean expiredParam");
		filters.add("expired == expiredParam");
		parameters.put("expiredParam", false);
		
		if( StringUtils.hasText(shoppingId)) {
			declaredParams.add("String shoppingIdParam");
			filters.add("shoppings.contains(shoppingIdParam)");
			parameters.put("shoppingIdParam", shoppingId);
		}

		// Set query filters
		query.declareParameters(toParameterList(declaredParams));
		query.setFilter(toWellParametrizedFilter(filters));
		
		// And set ranges
		if( range != null ) {
			query.setRange(range.getFrom(), range.getTo());
		}
		
		try {
			// Executes the query
			@SuppressWarnings("unchecked")
			List<Offer> objs = (List<Offer>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Offer obj : objs) {
					ret.add(pm.detachCopy(obj));
				}
			}
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally  {
			pm.close();
		}
		
		return ret;

	}

	public List<String> getOfferIdsUsingRelatedKind(String entityId, int entityKind, Date queryDate) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<String> ret = CollectionFactory.createList();

		Query query = pm.newQuery(Offer.class);

		// Filter declaration
		StringBuffer filter = new StringBuffer();
		StringBuffer declares = new StringBuffer();
		Map<String, Object> parameters = CollectionFactory.createMap();

		declares.append("String idParam");
		declares.append(", java.util.Date dateParam");

		filter.append("validTo >= dateParam");
		if( EntityKind.KIND_BRAND == entityKind ) {
			filter.append("&& brands.contains(idParam)");
		} else if( EntityKind.KIND_SHOPPING == entityKind ) {
			filter.append("&& shoppings.contains(idParam)");
		} else if( EntityKind.KIND_STORE == entityKind ) {
			filter.append("&& stores.contains(idParam)");
		} else if( EntityKind.KIND_FINANCIAL_ENTITY == entityKind ) {
			filter.append("&& availableFinancialEntities.contains(idParam)");
		} else {
			return ret;
		}

		parameters.put("dateParam", queryDate );
		parameters.put("idParam", entityId);
		query.setFilter(filter.toString());
		query.declareParameters(declares.toString());
		
		try {
			// Executes the query
			@SuppressWarnings("unchecked")
			List<Offer> objs = (List<Offer>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Offer obj : objs) {
					ret.add(obj.getIdentifier());
				}
			}
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally  {
			pm.close();
		}
		
		return ret;

	}
	
	@Override
	public List<Offer> getUsingKindAndRangeInCache(String entityId, Integer entityKind, Range range, User user, int returnType) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<Offer> ret = CollectionFactory.createList();
		
		try {
			// Caches goes first
			UserEntityCache uec = uecService.get(user, EntityKind.KIND_OFFER, returnType);
			
			// Now I get the offers existent in a particular entity and kind
			List<String> offersByKind = getOfferIdsUsingRelatedKind(entityId, entityKind, new Date());
			
			// and we match the two lists (because I want to preserve the order of the uec list)
			List<String> tmpList = CollectionFactory.createList();
			for( String key : uec.getEntities()) {
				if( offersByKind.contains(key) ) tmpList.add(key);
			}
			
			// match according ranges;
			List<String> findIn = CollectionFactory.createList();
			if( range != null ) {
				for( int i = range.getFrom(); i < range.getTo() && i < tmpList.size(); i++ ) {
					findIn.add(((Key)keyHelper.obtainKey(Offer.class, tmpList.get(i))).getName().toUpperCase());
				}
			} else {
				for( String key : tmpList ) {
					findIn.add(((Key)keyHelper.obtainKey(Offer.class, key)).getName().toUpperCase());
				}
			}
			
			// If we got here with no elements... there are no elements to get... so bye!
			if( findIn.size() == 0 ) return ret;

			Query query = pm.newQuery(Offer.class);

			// Filter declaration
			StringBuffer filter = new StringBuffer();
			
			Map<String, Object> parameters = CollectionFactory.createMap();
			if( findIn.size() > 0 ) {
				if( filter.length() > 0 ) filter.append(" && ");
				query.declareParameters("java.util.List keysParam");
				filter.append("keysParam.contains(uIdentifier)");
				parameters.put("keysParam", findIn);
			}
			
			// Set Filters And Ranges
			if( !filter.toString().trim().equals("")) query.setFilter(filter.toString());
			
			// Executes the query
			@SuppressWarnings("unchecked")
			List<Offer> objs = (List<Offer>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Offer obj : objs) {
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
	 * @see mobi.allshoppings.dao.spi.GenericDAOJDO#delete(PersistenceProvider,
	 *      String) The difference with its parent method, is that this method
	 *      removes the associated GeoEntity to perform a clean up
	 */
	@Override
	public void delete(PersistenceProvider pp, String identifier) throws ASException {
		super.delete(pp, identifier);
		geocoder.removeGeoEntity(identifier, EntityKind.KIND_OFFER);
	}

	@Override
	public List<Offer> getByViewLocationAndDate(ViewLocation vl, Date queryDate, String order) throws ASException {
		return getByViewLocationAndDate(null, vl, queryDate, order, true);
	}

	@Override
	public List<Offer> getByViewLocationAndDate(PersistenceProvider pp, ViewLocation vl, Date queryDate, String order) throws ASException {
		return getByViewLocationAndDate(null, vl, queryDate, order, true);
	}

	@Override
	public List<Offer> getByViewLocationAndDate(ViewLocation vl, Date queryDate, String order, boolean detachable) throws ASException {
		return getByViewLocationAndDate(null, vl, queryDate, order, detachable);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Offer> getByViewLocationAndDate(PersistenceProvider pp, ViewLocation vl, Date queryDate, String order, boolean detachable) throws ASException {

		// If there's no view location defined... asume to request it all
		if(null == vl || !StringUtils.hasText(vl.getCountry())) return getAllAndOrder(pp, order, detachable);
		
		List<Offer> returnedObjs = CollectionFactory.createList();
		
		PersistenceManager pm;
		if( pp == null ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}
		
		try {
			Query query = pm.newQuery(clazz);
			
			Map<String, Object> parameters = CollectionFactory.createMap();
			StringBuffer filter = new StringBuffer();
			StringBuffer declare = new StringBuffer();
			
			filter.append("country == countryParam");
			declare.append("String countryParam");
			parameters.put("countryParam", vl.getCountry());
			
			if( null != queryDate ) {
				if( filter.length() > 0 ) filter.append(" && "); 
				filter.append("validTo >= dateParam");
				if( declare.length() > 0 ) declare.append(", ");
				declare.append("java.util.Date dateParam");
				parameters.put("dateParam", DateUtils.truncate(queryDate, Calendar.DATE));
			}

			query.declareParameters(declare.toString());
			query.setFilter(filter.toString());
			
			List<Offer> objs = (List<Offer>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Offer obj : objs) {
					if( detachable )
						returnedObjs.add(pm.detachCopy(obj));
					else
						returnedObjs.add(obj);
				}
			}
			
			if( StringUtils.hasLength(order) && order.startsWith("creationDate")) {
				Collections.sort(returnedObjs, new OfferCreationComparator());
			}
			
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
	    } finally  {
	    	if( null == pp ) pm.close();
	    }
		
		return returnedObjs;
	}

	@Override
	public List<Offer> getToExpire(Range range) throws ASException {
		List<Offer> returnedObjs = new ArrayList<Offer>();
		
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{
			Date d = DateUtils.truncate(new Date(), Calendar.DATE);
			Map<String, Object> parameters = CollectionFactory.createMap();
			Query query = pm.newQuery(Offer.class);
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
			query.declareParameters("java.util.Date dateParm");
			query.setFilter("expired == false && validTo < dateParm");
			parameters.put("dateParm", d);
			
			@SuppressWarnings("unchecked")
			List<Offer> objs = (List<Offer>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Offer obj : objs) {
					returnedObjs.add(pm.detachCopy(obj));
				}
			}
			
			// Store the cursorString
			Cursor cursor = JDOCursorHelper.getCursor(objs);
			range.setCursor(cursor.toWebSafeString());
			
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
	    } finally  {
	    	pm.close();
	    }
		
		return returnedObjs;
	}

	@Override
	public long countToExpire() throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Date d = DateUtils.truncate(new Date(), Calendar.DATE);
			Map<String, Object> parameters = CollectionFactory.createMap();
			Query query = pm.newQuery(Offer.class);
			query.declareParameters("java.util.Date dateParm");
			query.setFilter("expired == false && validTo < dateParm");
			parameters.put("dateParm", d);
			query.setResult("count(this)");
			Long results = Long.parseLong(query.executeWithMap(parameters).toString());
			return (long)results;
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
	    } finally  {
			pm.close();
	    }	
	}
	
	@Override
	public CustomDatatableFilter buildCustomFilter(final UserInfo userInfo) {
		if( userInfo != null && userInfo.getRole() == UserSecurity.Role.ADMIN)
			if (userInfo.getSessionParameters().containsKey(Offer.FILTER_ACTIVE_ONLY)
					&& (Boolean) userInfo.getSessionParameters().get(Offer.FILTER_ACTIVE_ONLY)) {

				return new CustomDatatableFilter() {
					@Override
					public void delegateFilter(Query query, Map<String, Object> parameters) {
						query.setFilter("expired == false");
					}
				};
			} else {
				return null;
			}

		if (userInfo.getSessionParameters().containsKey(Offer.FILTER_ACTIVE_ONLY)
				&& (Boolean) userInfo.getSessionParameters().get(Offer.FILTER_ACTIVE_ONLY)) {

			final List<String> availableCountries = userInfo.getAvailableCountries();

			return new CustomDatatableFilter() {
				@Override
				public void delegateFilter(Query query, Map<String, Object> parameters) {
					if( availableCountries != null ) {
						parameters.put("paramVL", availableCountries);
						query.declareParameters("java.util.List paramVL");
						query.setFilter("expired == false && paramVL.contains(country)");
					} else {
						query.setFilter("expired == false");
					}
				}
			};
		} else {
			return super.buildCustomFilter(userInfo);
		}
	}

	@Override
	public boolean safeAndInLimits(Offer obj, UserInfo userInfo) {
		if (userInfo.getSessionParameters().containsKey(Offer.FILTER_ACTIVE_ONLY)
				&& (Boolean) userInfo.getSessionParameters().get(Offer.FILTER_ACTIVE_ONLY)) {
			if( obj.isExpired() ) return false;
		}

		if( userInfo != null && userInfo.getRole() == UserSecurity.Role.ADMIN)
			return true;
		
		return super.safeAndInLimits(obj, userInfo);
	}

}
