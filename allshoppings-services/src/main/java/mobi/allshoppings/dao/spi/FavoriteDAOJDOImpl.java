package mobi.allshoppings.dao.spi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import mobi.allshoppings.dao.FavoriteDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Favorite;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

public class FavoriteDAOJDOImpl extends GenericDAOJDO<Favorite> implements FavoriteDAO {

	public FavoriteDAOJDOImpl() {
		super(Favorite.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(Favorite.class);
	}

	public List<Favorite> getUsingUserAndKindAndRange(User user, Integer entityKind, Range range, String order) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<Favorite> ret = CollectionFactory.createList();
		
		try {
			Query query = pm.newQuery(Favorite.class);

			// Parameter Declaration
			List<String> parametersDeclaration = CollectionFactory.createList();
			Map<String, Object> parameters = new HashMap<String, Object>();
			
			if(null != user) {
				parametersDeclaration.add("String userIdParam");
				parameters.put("userIdParam", user.getIdentifier());
			}
			if(entityKind >= 0 ) {
				parametersDeclaration.add("Integer entityKindParam");
				parameters.put("entityKindParam", entityKind);
			}
			
			query.declareParameters(toParameterList(parametersDeclaration));

			// Set Filters And Ranges
			query.setFilter(toWellParametrizedFilter(parameters));
			if( range != null ) query.setRange(range.getFrom(), range.getTo());
			
			// Set Order
			if( StringUtils.hasText(order)) {
				query.setOrdering(order);
			}
			
			// Executes the query
			@SuppressWarnings("unchecked")
			List<Favorite> objs = (List<Favorite>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Favorite obj : objs) {
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
	
	public List<String> getIdsUsingUserAndKind(User user, Integer entityKind) throws ASException {
		
		List<String> ret = CollectionFactory.createList();
		
		List<Favorite> objs = getUsingUserAndKindAndRange(user, entityKind, null, null);
		for( Favorite obj : objs ) {
			ret.add(obj.getEntityId());
		}
		
		return ret;
	}
	
	
	@Override
	public Favorite getUsingUserAndEntityAndKind(User user, String entityId, Integer entityKind, boolean detached) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		Favorite ret = null;
		
		try {
			Query query = pm.newQuery(Favorite.class);

			// Parameter Declaration
			List<String> parametersDeclaration = CollectionFactory.createList();
			Map<String, Object> parameters = new HashMap<String, Object>();
			
			if(null != user) {
				parametersDeclaration.add("String userIdParam");
				parameters.put("userIdParam", user.getIdentifier());
			}
			if(StringUtils.hasText(entityId)) {
				parametersDeclaration.add("String entityIdParam");
				parameters.put("entityIdParam", entityId);
			}
			if(entityKind >= 0 ) {
				parametersDeclaration.add("Integer entityKindParam");
				parameters.put("entityKindParam", entityKind);
			}
			
			query.declareParameters(toParameterList(parametersDeclaration));

			// Set Filters And Ranges
			query.setFilter(toWellParametrizedFilter(parameters));
			query.setRange( 0, 1 );
			
			// Executes the query
			@SuppressWarnings("unchecked")
			List<Favorite> objs = (List<Favorite>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Favorite obj : objs) {
					if( detached ) {
						ret = pm.detachCopy(obj);
					} else {
						ret = obj;
					}
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
	public long getUserFavoriteCount(String userId) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();

			Query query = pm.newQuery(Favorite.class);

			declaredParams.add("String userIdParam");
			parameters.put("userIdParam", userId);

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(parameters));
			query.setResult("count(this)");

			Long results = Long.parseLong(query.executeWithMap(parameters).toString());
			return (long)results;
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
	public long getEntityFavoriteCount(String entityId, Integer entityKind) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();

			Query query = pm.newQuery(Favorite.class);

			declaredParams.add("String entityIdParam");
			declaredParams.add("Integer entityKindParam");
			parameters.put("entityIdParam", entityId);
			parameters.put("entityKindParam", entityKind);

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(parameters));
			query.setResult("count(this)");

			Long results = Long.parseLong(query.executeWithMap(parameters).toString());
			return (long)results;
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
