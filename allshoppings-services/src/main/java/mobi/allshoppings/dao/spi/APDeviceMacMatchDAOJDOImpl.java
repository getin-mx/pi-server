package mobi.allshoppings.dao.spi;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.APDeviceMacMatchDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDeviceMacMatch;
import mobi.allshoppings.tools.CollectionFactory;

public class APDeviceMacMatchDAOJDOImpl extends GenericDAOJDO<APDeviceMacMatch> implements APDeviceMacMatchDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(APDeviceMacMatchDAOJDOImpl.class.getName());

	public APDeviceMacMatchDAOJDOImpl() {
		super(APDeviceMacMatch.class);
	}

	@Override
	public Key createKey() throws ASException {
		String seed = UUID.randomUUID().toString() + new Random().nextInt((int)System.nanoTime());
		return keyHelper.createStringUniqueKey(clazz, seed);
	}
	
	@Override
	public List<APDeviceMacMatch> getUsingHostname(String hostname) throws ASException {

		List<APDeviceMacMatch> ret = CollectionFactory.createList();
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// Hostname Parameter
			if(StringUtils.hasText(hostname)) {
				declaredParams.add("String hostnameParam");
				filters.add("hostname == hostnameParam");
				parameters.put("hostnameParam", hostname);
			}
			
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			@SuppressWarnings("unchecked")
			List<APDeviceMacMatch> list = (List<APDeviceMacMatch>)query.executeWithMap(parameters);
			for(APDeviceMacMatch obj : list ) {
				ret.add(pm.detachCopy(obj));
			}

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

	@Override
	public void deleteUsingHostname(String hostname) throws ASException {

		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// Hostname Parameter
			if(StringUtils.hasText(hostname)) {
				declaredParams.add("String hostnameParam");
				filters.add("hostname == hostnameParam");
				parameters.put("hostnameParam", hostname);
			}
			
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			query.deletePersistentAll(parameters);
			
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
