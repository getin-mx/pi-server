package mobi.allshoppings.dao.spi;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.TFTokenDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.TFToken;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tx.PersistenceProvider;

public class TFTokenDAOJDOImpl extends GenericDAOJDO<TFToken> implements TFTokenDAO {

	private static final Logger log = Logger.getLogger(TFTokenDAOJDOImpl.class.getName());

	@Autowired
	private KeyHelper keyHelper;

	public TFTokenDAOJDOImpl() {
		super(TFToken.class);
	}

	/**
	 * Creates a new unique key for the shopping, based in the shopping name
	 * 
	 * @param shoppingName
	 *            The name of the entity used as seed to the key
	 */
	@Override
	public Key createKey(String tfToken) throws ASException {

		try {
			if(!StringUtils.hasText(tfToken)){
				throw ASExceptionHelper.notAcceptedException();
			}
			return (Key)keyHelper.obtainKey(TFToken.class, tfToken);
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.notAcceptedException();
		}
	}

	/**
	 * Gets the las tfToken obtained by a user 
	 * 
	 * @param userId
	 *            The user identifier to check for
	 */
	@Override
	public TFToken getLastUsingUser(String userId) throws ASException {
		return getLastUsingUser(null, userId, true);
	}

	/**
	 * Gets the las tfToken obtained by a user 
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param userId
	 *            The user identifier to check for
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public TFToken getLastUsingUser(PersistenceProvider pp, String userId, boolean detachable) throws ASException {

		PersistenceManager pm;
		if( pp == null ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}
		
		if( !StringUtils.hasText(userId)) throw ASExceptionHelper.notAcceptedException();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();
			
			Query query = pm.newQuery(clazz);
			declaredParams.add("String userIdParam");
			filters.add("userId == userIdParam");
			parameters.put("userIdParam", userId);
			
			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			query.setOrdering("creationDateTime DESC");
			query.setRange(0, 1);
			
			@SuppressWarnings("unchecked")
			List<TFToken> list = (List<TFToken>)query.executeWithMap(parameters);
			if( list.size() > 0 ) {
				if( detachable ) {
					return pm.detachCopy(list.get(0));
				} else {
					return list.get(0);
				}
			} else {
				throw ASExceptionHelper.notFoundException();
			}
			
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
	    } finally  {
	    	if( null == pp ) pm.close();
	    }
	}

}
