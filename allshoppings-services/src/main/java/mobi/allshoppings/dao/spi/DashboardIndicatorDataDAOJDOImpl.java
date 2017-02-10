package mobi.allshoppings.dao.spi;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

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
		return keyHelper.obtainKey(DashboardIndicatorData.class, String.valueOf(obj.hashCode()));
	}

	@Override
	public List<DashboardIndicatorData> getUsingFilters(String entityId,
			Integer entityKind, String elementId, String elementSubId,
			String shoppingId, String subentityId, String periodType,
			String fromStringDate, String toStringDate, String movieId,
			String voucherType, Integer dayOfWeek, Integer timeZone, String order, 
			String country, String province, String city)
			throws ASException {

		return getUsingFilters(entityId, entityKind, Arrays.asList(new String[] {elementId}), elementSubId, shoppingId, subentityId, 
				periodType, fromStringDate, toStringDate, movieId, voucherType, dayOfWeek, timeZone, order, country, province, city);
	}

	@Override
	public List<DashboardIndicatorData> getUsingFilters(String entityId,
			Integer entityKind, List<String> elementId, String elementSubId,
			String shoppingId, String subentityId, String periodType,
			String fromStringDate, String toStringDate, String movieId,
			String voucherType, Integer dayOfWeek, Integer timeZone, String order, 
			String country, String province, String city)
			throws ASException {
		return getUsingFilters(entityId, entityKind, elementId, Arrays.asList(new String[] {elementSubId}), shoppingId, subentityId, 
				periodType, fromStringDate, toStringDate, movieId, voucherType, dayOfWeek, timeZone, order, country, province, city);
	}
	
	@Override
	public List<DashboardIndicatorData> getUsingFilters(String entityId,
			Integer entityKind, List<String> elementId, List<String> elementSubId,
			String shoppingId, String subentityId, String periodType,
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

			if(StringUtils.hasText(entityId)) {
				declaredParams.add("String entityIdParm");
				filters.add("entityId == entityIdParm");
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
			
			if(StringUtils.hasText(subentityId)) {
				declaredParams.add("String subentityIdParm");
				filters.add("subentityId == subentityIdParm");
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
			List<String> elementId, Date fromDate, Date toDate)
			throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		if( elementId != null && elementId.size() == 1 && elementId.get(0) == null ) {
			elementId = CollectionFactory.createList();
		}
		
		try {
			Query query = pm.newQuery(DashboardIndicatorData.class);

			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			if(StringUtils.hasText(subentityId)) {
				declaredParams.add("String subentityIdParm");
				filters.add("subentityId == subentityIdParm");
				parameters.put("subentityIdParm", subentityId);
			}
			
			if(!CollectionUtils.isEmpty(elementId)) {
				declaredParams.add("java.util.List elementIdParm");
				filters.add("elementIdParm.contains(elementId)");
				parameters.put("elementIdParm", elementId);
			}

			if(null != fromDate) {
				declaredParams.add("String fromStringDateParm");
				filters.add("stringDate >= fromStringDateParm");
				parameters.put("fromStringDateParm", sdf.format(fromDate));
			}
			
			if(null != toDate) {
				declaredParams.add("String toStringDateParm");
				filters.add("stringDate <= toStringDateParm");
				parameters.put("toStringDateParm", sdf.format(toDate));
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			query.deletePersistentAll(parameters);

			return;
			
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}		

	}
}
