package mobi.allshoppings.dao.spi;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.EmployeeLogDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.EmployeeLog;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

public class EmployeeLogDAOJDOImpl extends GenericDAOJDO<EmployeeLog> implements EmployeeLogDAO {

	public EmployeeLogDAOJDOImpl() {
		super(EmployeeLog.class);
	}

	@Override
	public Key createKey(EmployeeLog obj) throws ASException {
		return keyHelper.createStringUniqueKey(EmployeeLog.class);
	}

	@Override
	public List<EmployeeLog> getUsingEntityIdAndEntityKindAndDate(String entityId, Integer entityKind, Date fromDate,
			Date toDate, Range range, String order, Map<String, String> attributes, boolean detachable)
			throws ASException {

		List<EmployeeLog> ret = CollectionFactory.createList();
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// entityId Parameter
			if(StringUtils.hasText(entityId)) {
				declaredParams.add("String entityIdParam");
				filters.add("entityId == entityIdParam");
				parameters.put("entityIdParam", entityId);
			}

			// entityKind Parameter
			if(null != entityKind) {
				declaredParams.add("Integer entityKindParam");
				filters.add("entityKind == entityKindParam");
				parameters.put("entityKindParam", entityKind);
			}

			// fromDate Parameter
			if(null != fromDate) {
				declaredParams.add("java.util.Date fromDateParam");
				filters.add("checkinStarted >= fromDateParam");
				parameters.put("fromDateParam", fromDate);
			}

			// toDate Parameter
			if(null != toDate) {
				declaredParams.add("java.util.Date toDateParam");
				filters.add("checkinStarted < toDateParam");
				parameters.put("toDateParam", toDate);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			// Do a counting of records
			if( attributes != null ) {
				query.setResult("count(this)");
				Long count = Long.parseLong(query.executeWithMap(parameters).toString());
				attributes.put("recordCount", String.valueOf(count));
				query.setResult(null);
			}
			
			if( StringUtils.hasText(order))
				query.setOrdering(order);
			
			if( range != null ) 
				query.setRange(range.getFrom(), range.getTo());

			@SuppressWarnings("unchecked")
			List<EmployeeLog> list = (List<EmployeeLog>)query.executeWithMap(parameters);

			for(EmployeeLog obj : list ) {
				if(detachable) {
					ret.add(pm.detachCopy(obj));
				} else {
					ret.add(obj);
				}
			}
			
			query.closeAll();
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
