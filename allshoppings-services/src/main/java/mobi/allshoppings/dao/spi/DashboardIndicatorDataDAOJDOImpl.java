package mobi.allshoppings.dao.spi;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.datastore.JDOConnection;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;

import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CollectionUtils;

public class DashboardIndicatorDataDAOJDOImpl extends GenericDAOJDO<DashboardIndicatorData> implements DashboardIndicatorDataDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(DashboardIndicatorDataDAOJDOImpl.class.getName());

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public DashboardIndicatorDataDAOJDOImpl() {
		super(DashboardIndicatorData.class);
	}

	@Override
	public Key createKey(DashboardIndicatorData obj) throws ASException {
		return keyHelper.obtainKey(DashboardIndicatorData.class,
				String.valueOf(obj.hashCode()) +"-" +UUID.randomUUID());
	}

	@Override
	public List<DashboardIndicatorData> getUsingFilters(String entityId,
			Integer entityKind, String elementId, String elementSubId,
			String shoppingId, String subentityId, String periodType,
			String fromStringDate, String toStringDate, String movieId,
			String voucherType, Integer dayOfWeek, Integer timeZone, String order, 
			String country, String province, String city)
			throws ASException {

		return getUsingFilters(entityId, entityKind,
				StringUtils.hasText(elementId) ? Arrays.asList(new String[] { elementId }) : null, elementSubId,
				shoppingId, subentityId, periodType, fromStringDate, toStringDate, movieId, voucherType, dayOfWeek,
				timeZone, order, country, province, city);
	}

	@Override
	public List<DashboardIndicatorData> getUsingFilters(String entityId,
			Integer entityKind, List<String> elementId, String elementSubId,
			String shoppingId, String subentityId, String periodType,
			String fromStringDate, String toStringDate, String movieId,
			String voucherType, Integer dayOfWeek, Integer timeZone, String order, 
			String country, String province, String city)
			throws ASException {
		return getUsingFilters(entityId, entityKind, elementId,
				StringUtils.hasText(elementSubId) ? Arrays.asList(new String[] { elementSubId }) : null, shoppingId,
				subentityId, periodType, fromStringDate, toStringDate, movieId, voucherType, dayOfWeek, timeZone, order,
				country, province, city);
	}

	@Override
	public List<DashboardIndicatorData> getUsingFilters(String entityId,
			Integer entityKind, List<String> elementId, List<String> elementSubId,
			String shoppingId, String subentityId, String periodType,
			String fromStringDate, String toStringDate, String movieId,
			String voucherType, Integer dayOfWeek, Integer timeZone, String order, 
			String country, String province, String city)
			throws ASException {
		List<String> entityIds;
		if(entityId == null) entityIds = CollectionFactory.createList();
		else entityIds = Arrays.asList(entityId);
		return getUsingFilters(entityIds, entityKind, elementId, elementSubId, shoppingId,
				StringUtils.hasText(subentityId) ? Arrays.asList(subentityId) : null, periodType, fromStringDate,
				toStringDate, movieId, voucherType, dayOfWeek, timeZone, order, country, province, city);
	}
	
	@Override
	public List<DashboardIndicatorData> getUsingFilters(List<String> entityId,
			Integer entityKind, List<String> elementId, List<String> elementSubId,
			String shoppingId, List<String> subentityId, String periodType,
			String fromStringDate, String toStringDate, String movieId,
			String voucherType, Integer dayOfWeek, Integer timeZone, String order, 
			String country, String province, String city)
			throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<DashboardIndicatorData> returnedObjs = CollectionFactory.createList();

		if( elementId != null && elementId.size() == 1 && elementId.get(0) == null ) {
			elementId = CollectionFactory.createList();
		}
		
		try {
			Query query = pm.newQuery(DashboardIndicatorData.class);

			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			if(!CollectionUtils.isEmpty(entityId)) {
				declaredParams.add("java.util.List entityIdParm");
				filters.add("entityIdParm.contains(entityId)");
				parameters.put("entityIdParm", entityId);
			}

			if(entityKind != null ) {
				declaredParams.add("Integer entityKindParm");
				filters.add("entityKind == entityKindParm");
				parameters.put("entityKindParm", entityKind);
			}

			if(!CollectionUtils.isEmpty(elementId)) {
				declaredParams.add("java.util.List elementIdParm");
				filters.add("elementIdParm.contains(elementId)");
				parameters.put("elementIdParm", elementId);
			}

			if(!CollectionUtils.isEmpty(elementSubId)) {
				declaredParams.add("java.util.List elementSubIdParm");
				filters.add("elementSubIdParm.contains(elementSubId)");
				parameters.put("elementSubIdParm", elementSubId);
			}

			if(StringUtils.hasText(shoppingId)) {
				declaredParams.add("String shoppingIdParm");
				filters.add("shoppingId == shoppingIdParm");
				parameters.put("shoppingIdParm", shoppingId);
			}
			
			if(!CollectionUtils.isEmpty(subentityId)) {
				declaredParams.add("java.util.List subentityIdParm");
				filters.add("subentityIdParm.contains(subentityId)");
				parameters.put("subentityIdParm", subentityId);
			}

			if(StringUtils.hasText(periodType)) {
				declaredParams.add("String periodTypeParm");
				filters.add("periodType == periodTypeParm");
				parameters.put("periodTypeParm", periodType);
			}

			if(StringUtils.hasText(movieId)) {
				declaredParams.add("String movieIdParm");
				filters.add("movieId == movieIdParm");
				parameters.put("movieIdParm", movieId);
			}

			if(StringUtils.hasText(voucherType)) {
				declaredParams.add("String voucherTypeParm");
				filters.add("voucherType == voucherTypeParm");
				parameters.put("voucherTypeParm", voucherType);
			}

			if(StringUtils.hasText(country)) {
				declaredParams.add("String countryParm");
				filters.add("country == countryParm");
				parameters.put("countryParm", country);
			}

			if(StringUtils.hasText(province)) {
				declaredParams.add("String provinceParm");
				filters.add("province == provinceParm");
				parameters.put("provinceParm", province);
			}

			if(StringUtils.hasText(city)) {
				declaredParams.add("String cityParm");
				filters.add("city == cityParm");
				parameters.put("cityParm", city);
			}

			if(dayOfWeek != null && dayOfWeek > 0  ) {
				declaredParams.add("Integer dayOfWeekParm");
				filters.add("dayOfWeek == dayOfWeekParm");
				parameters.put("dayOfWeekParm", dayOfWeek);
			}

			if(timeZone != null && timeZone > 0 ) {
				declaredParams.add("Integer timeZoneParm");
				filters.add("timeZone == timeZoneParm");
				parameters.put("timeZoneParm", timeZone);
			}

			if(StringUtils.hasText(fromStringDate)) {
				declaredParams.add("String fromStringDateParm");
				filters.add("stringDate >= fromStringDateParm");
				parameters.put("fromStringDateParm", fromStringDate);
			}
			
			if(StringUtils.hasText(toStringDate)) {
				declaredParams.add("String toStringDateParm");
				filters.add("stringDate <= toStringDateParm");
				parameters.put("toStringDateParm", toStringDate);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			// Sets order as required
			if( StringUtils.hasText(order)) query.setOrdering(order);

			@SuppressWarnings("unchecked")
			List<DashboardIndicatorData> objs = parameters.size() > 0 ? (List<DashboardIndicatorData>)query.executeWithMap(parameters) : (List<DashboardIndicatorData>)query.execute();
			if (objs != null) {
				// force to read
				for (DashboardIndicatorData obj : objs) {
					returnedObjs.add(pm.detachCopy(obj));
				}
			}

			return returnedObjs;

		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}		

	}

	@Override
	public void deleteUsingSubentityIdAndElementIdAndDate(String subentityId,
			List<String> elementId, Date fromDate, Date toDate, TimeZone tz)
			throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		if( elementId != null && elementId.size() == 1 && elementId.get(0) == null ) {
			elementId = CollectionFactory.createList();
		}
		
		try {
			
			// Obtains DB Connection
			JDOConnection jdoConn = pm.getDataStoreConnection();
			DB db = (DB)jdoConn.getNativeConnection();
			
			if(tz != null) sdf.setTimeZone(tz);
			else sdf.setTimeZone(TimeZone.getDefault());

			// Set one, generate first filter
			List<BasicDBObject> parts = CollectionFactory.createList();
			if( StringUtils.hasText(subentityId))
				parts.add(new BasicDBObject("subentityId", subentityId));
			if(null != elementId && elementId.size() > 0 )
				parts.add(new BasicDBObject("elementId", new BasicDBObject("$in", elementId)));
			if(null != fromDate && null == toDate ) {
				String fromDateString = sdf.format(fromDate);
				parts.add(new BasicDBObject("stringDate", new BasicDBObject("$gte", fromDateString)));
			}
			if(null == fromDate && null != toDate ) {
				String toDateString = sdf.format(toDate);
				parts.add(new BasicDBObject("stringDate", new BasicDBObject("$lte", toDateString)));
			}
			if(null != fromDate && null != toDate ) {
				String fromDateString = sdf.format(fromDate);
				String toDateString = sdf.format(toDate);
				parts.add(new BasicDBObject("$and", Arrays.asList(
						new BasicDBObject("stringDate", new BasicDBObject("$gte", fromDateString)),
						new BasicDBObject("stringDate", new BasicDBObject("$lte", toDateString))
						)));
			}
			BasicDBObject query = new BasicDBObject("$and", parts);

			db.getCollection("DashboardIndicatorData").remove(query);
			jdoConn.close();
			
			pm.evictAll(true, DashboardIndicatorData.class);
			
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}		

	}
}
