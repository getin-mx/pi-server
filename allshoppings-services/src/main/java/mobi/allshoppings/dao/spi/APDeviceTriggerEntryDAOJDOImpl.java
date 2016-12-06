package mobi.allshoppings.dao.spi;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.APDeviceTriggerEntryDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDeviceTriggerEntry;
import mobi.allshoppings.tools.CollectionFactory;

public class APDeviceTriggerEntryDAOJDOImpl extends GenericDAOJDO<APDeviceTriggerEntry> implements APDeviceTriggerEntryDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(APDeviceTriggerEntryDAOJDOImpl.class.getName());

	public APDeviceTriggerEntryDAOJDOImpl() {
		super(APDeviceTriggerEntry.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(APDeviceTriggerEntry.class);
	}

	@Override
	public List<APDeviceTriggerEntry> getUsingCoincidence(String hostname, String mac) throws ASException {

		List<APDeviceTriggerEntry> ret = CollectionFactory.createList();
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

			// MAC Parameter
			if(StringUtils.hasText(hostname)) {
				declaredParams.add("String macParam");
				filters.add("mac == macParam");
				parameters.put("macParam", mac);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			@SuppressWarnings("unchecked")
			List<APDeviceTriggerEntry> list = (List<APDeviceTriggerEntry>)query.executeWithMap(parameters);
			for(APDeviceTriggerEntry obj : list ) {
				ret.add(pm.detachCopy(obj));
			}

			if( ret.size() > 0 ) {
				return ret;
			} else {
				return getUsingGenerics(hostname);
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

	}

	@Override
	public List<APDeviceTriggerEntry> getUsingGenerics(String hostname) throws ASException {

		List<APDeviceTriggerEntry> ret = CollectionFactory.createList();
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
			List<APDeviceTriggerEntry> list = (List<APDeviceTriggerEntry>)query.executeWithMap(parameters);
			for(APDeviceTriggerEntry obj : list ) {
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
}
