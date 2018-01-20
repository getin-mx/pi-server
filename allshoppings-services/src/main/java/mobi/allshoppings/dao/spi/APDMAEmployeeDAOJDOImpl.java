package mobi.allshoppings.dao.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.APDMAEmployeeDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDMAEmployee;
import mobi.allshoppings.tools.Range;

public class APDMAEmployeeDAOJDOImpl extends GenericDAOJDO<APDMAEmployee> implements APDMAEmployeeDAO {

	public APDMAEmployeeDAOJDOImpl() {
		super(APDMAEmployee.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(clazz);
	}

	@Override
	public List<APDMAEmployee> getUsingEntityIdAndRange(String entityId, byte entityKind, Range range, String order, Map<String, String> attributes, boolean detachable) throws ASException {
		List<APDMAEmployee> returnedObjs = new ArrayList<>();

		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try{
			Map<String, Object> parameters = new HashMap<>();
			List<String> declaredParams = new ArrayList<>();
			List<String> filters = new ArrayList<>();

			Query query = pm.newQuery(clazz);

			// Entity id parameter
			if( StringUtils.hasText(entityId) ) {
				declaredParams.add("String entityIdParam");
				filters.add("entityId == entityIdParam");
				parameters.put("entityIdParam", entityId);
			}
			
			// Entity Kind parameter
			if( entityKind >= 0) {
				declaredParams.add("Integer entityKindParam");
				filters.add("entityKind == entityKindParam");
				parameters.put("entityKindParam", entityKind);
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

			// Adds a cursor to the ranged query
			if( range != null ) 
				query.setRange(range.getFrom(), range.getTo());

			// Sets order as required
			if( StringUtils.hasText(order)) query.setOrdering(order);

			@SuppressWarnings("unchecked")
			List<APDMAEmployee> objs = parameters.size() > 0 ? (List<APDMAEmployee>)query.executeWithMap(parameters) : (List<APDMAEmployee>)query.execute();
			if (objs != null) {
				// force to read
				for (APDMAEmployee obj : objs) {
					if( detachable )
						returnedObjs.add(pm.detachCopy(obj));
					else
						returnedObjs.add(obj);
				}
			}

		} finally  {
			pm.close();
		}

		return returnedObjs;

	}

	@Override
	public List<APDMAEmployee> getUsingEntityIdandMac(String entityId, byte entityKind, String mac) throws ASException {
		List<APDMAEmployee> returnedObjs = new ArrayList<>();

		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try{
			Map<String, Object> parameters = new HashMap<>();
			List<String> declaredParams = new ArrayList<>();
			List<String> filters = new ArrayList<>();

			Query query = pm.newQuery(clazz);

			// Entity id parameter
			if( StringUtils.hasText(entityId) ) {
				declaredParams.add("String entityIdParam");
				filters.add("entityId == entityIdParam");
				parameters.put("entityIdParam", entityId);
			}
			
			// Entity Kind parameter
			if( entityKind >= 0) {
				declaredParams.add("Integer entityKindParam");
				filters.add("entityKind == entityKindParam");
				parameters.put("entityKindParam", entityKind);
			}

			// Mac parameter
			if( StringUtils.hasText(mac)) {
				declaredParams.add("String macParam");
				filters.add("mac == macParam");
				parameters.put("macParam", mac);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			@SuppressWarnings("unchecked")
			List<APDMAEmployee> objs = parameters.size() > 0 ? (List<APDMAEmployee>)query.executeWithMap(parameters) : (List<APDMAEmployee>)query.execute();
			if (objs != null) {
				// force to read
				for (APDMAEmployee obj : objs) {
					returnedObjs.add(pm.detachCopy(obj));
				}
			}

		} finally  {
			pm.close();
		}

		return returnedObjs;
	}

	
}
