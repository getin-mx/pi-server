package mobi.allshoppings.dao.spi;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.APDMAEmployeeDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDMAEmployee;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

public class APDMAEmployeeDAOJDOImpl extends GenericDAOJDO<APDMAEmployee> implements APDMAEmployeeDAO {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(APDMAEmployeeDAOJDOImpl.class.getName());
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public APDMAEmployeeDAOJDOImpl() {
		super(APDMAEmployee.class);
	}

	@Override
	public Key createKey(APDMAEmployee seed) throws ASException {
		if(!StringUtils.hasText(seed.getMac()) || !StringUtils.hasText(seed.getEntityId())){
			throw ASExceptionHelper.notAcceptedException();
		}
		return (Key)keyHelper.obtainKey(APDMAEmployee.class, keyHelper.resolveKey(seed.getEntityId() 
				+ ":" + seed.getEntityKind() + ":" + seed.getMac() + sdf.format(seed.getFromDate())));
	}

	@Override
	public List<APDMAEmployee> getUsingEntityIdAndRange(String entityId, Integer entityKind, Range range, String order, boolean detachable) throws ASException {
		List<APDMAEmployee> returnedObjs = CollectionFactory.createList();

		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// Store id parameter
			if( StringUtils.hasText(entityId) ) {
				declaredParams.add("String entityIdParam");
				filters.add("entityId == entityIdParam");
				parameters.put("entityIdParam", entityId);
			}
			
			// Date parameter
			if( entityKind != null ) {
				declaredParams.add("Integer entityKindParam");
				filters.add("entityKind >= entityKindParam");
				parameters.put("entityKindParam", entityKind);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

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

		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			pm.close();
		}

		return returnedObjs;

	}

}
