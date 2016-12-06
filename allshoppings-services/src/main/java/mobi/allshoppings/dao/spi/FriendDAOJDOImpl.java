package mobi.allshoppings.dao.spi;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import mobi.allshoppings.dao.FriendDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Friend;
import mobi.allshoppings.tools.CollectionFactory;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

public class FriendDAOJDOImpl extends GenericDAOJDO<Friend> implements FriendDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(FriendDAOJDOImpl.class.getName());

	public FriendDAOJDOImpl() {
		super(Friend.class);
	}

	@Override
	public Key createKey(Friend obj) throws ASException {
		return keyHelper.obtainKey(Friend.class, String.valueOf(obj.hashCode()));
	}

	@Override
	public long getUserFriendCount(String userId) throws ASException {
		return getUserFriendCountUsingStatus(userId, null);
	}

	@Override
	public long getUserFriendCountUsingStatus(String userId, Integer status) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();

			Query query = pm.newQuery(Friend.class);

			if(StringUtils.hasText(userId)) {
				declaredParams.add("String userId1Param");
				parameters.put("userId1Param", userId);
			}

			if(status != null ) {
				declaredParams.add("Integer statusParam");
				parameters.put("statusParam", status);
			}

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
