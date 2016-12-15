package mobi.allshoppings.dao.spi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Cursor;
import com.inodes.datanucleus.model.JDOCursorHelper;
import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.UserEntityCacheDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserEntityCache;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tools.SystemStatusService;

public class UserEntityCacheDAOJDOImpl extends GenericDAOJDO<UserEntityCache> implements UserEntityCacheDAO {

	private static final Logger log = Logger.getLogger(UserEntityCacheDAOJDOImpl.class.getName());
	
	private KeyHelper keyHelper = new KeyHelperGaeImpl();
	
	@Autowired
	private SystemStatusService service;
	
	public UserEntityCacheDAOJDOImpl() {
		super(UserEntityCache.class);
		if( service == null ) service = new SystemStatusService();
	}

	@Override
	public UserEntityCache getUsingKindAndListName(String name, Integer entityKind, boolean forceCheck) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Key key = createKey(createUserIdUsingListName(name), entityKind, UserEntityCache.TYPE_NORMAL_SORT);
			try {
				UserEntityCache ret = get(key.getName(), true);
				if( forceCheck && needsUpdate(ret, entityKind)) throw new JDOObjectNotFoundException();
				return ret;
			} catch( Exception e ) {
				UserEntityCache ret = new UserEntityCache(createUserIdUsingListName(name), entityKind, UserEntityCache.TYPE_NORMAL_SORT, UserEntityCache.DEFAULT_CACHE_DURATION);
				ret.setKey(createKey(createUserIdUsingListName(name), entityKind, UserEntityCache.TYPE_NORMAL_SORT));
				return ret;
			}

		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally  {
			pm.close();
		}
	}

	@Override
	public UserEntityCache getUsingKindAndFavorite(User user, Integer entityKind, int returnType, boolean forceCheck) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Key key = createKey(user.getIdentifier(), entityKind, returnType);
			try {
				UserEntityCache ret = get(key.getName(), false);
				if( forceCheck && needsUpdate(ret, user)) throw new JDOObjectNotFoundException();
				return ret;
			} catch( Exception e ) {
				UserEntityCache ret = new UserEntityCache(user.getIdentifier(), entityKind, returnType, UserEntityCache.DEFAULT_CACHE_DURATION);
				ret.setKey(createKey(user.getIdentifier(), entityKind, returnType));
				return ret;
			}

		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally  {
			pm.close();
		}
	}

	@Override
	public UserEntityCache getUsingKindAndViewLocation(ViewLocation vl, Integer entityKind, int returnType, boolean forceCheck) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try {

			Key key = createKey(vl, entityKind, returnType);
			try {
				UserEntityCache ret = get(key.getName(), false);	
				if( forceCheck && needsUpdate(ret, vl)) throw new JDOObjectNotFoundException();
				return ret;
			} catch( Exception e ) {
				UserEntityCache ret = new UserEntityCache(createUserIdUsingViewLocation(vl), entityKind, returnType, UserEntityCache.DEFAULT_CACHE_DURATION);
				ret.setKey(createKey(vl, entityKind, returnType));
				return ret;
			}

		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally  {
			pm.close();
		}
	}
	
	private String createUserIdUsingListName(String name) {
		return "List_" + name;
	}

	@Override
	public boolean needsUpdate(UserEntityCache uec, User user) throws ASException {

		if( uec.getLastUpdate() == null ) {
			log.log(Level.INFO, uec + " needs to be updated for uec.getLastUpdate()");
			return true;
		}
		if( uec.getExpiresOn() == null || uec.getExpiresOn().before(new Date())) {
			log.log(Level.INFO, uec + " needs to be updated for uec.getExpiresOn()");
			return true;
		}
		if( (user.getStatusModificationDateTime() != null && user.getStatusModificationDateTime().after(uec.getLastUpdate()))) {
			log.log(Level.INFO, uec + " needs to be updated for uec.getStatusModificationDateTime()");
			return true;
		}
		if( service.getLastUpdate(EntityKind.KIND_FINANCIAL_ENTITY).after(uec.getLastUpdate())) {
			log.log(Level.INFO, uec + " needs to be updated for uec.getLastUpdate() on KIND_FINANCIAL_ENTITY");
			return true;		
		}
		if( service.getLastUpdate(EntityKind.KIND_SHOPPING).after(uec.getLastUpdate())) {
			log.log(Level.INFO, uec + " needs to be updated for uec.getLastUpdate() on KIND_SHOPPING");
			return true;
		}
		if( service.getLastUpdate(EntityKind.KIND_BRAND).after(uec.getLastUpdate())) {
			log.log(Level.INFO, uec + " needs to be updated for uec.getLastUpdate() on KIND_BRAND");
			return true;
		}
		if( service.getLastUpdate(EntityKind.KIND_OFFER).after(uec.getLastUpdate())) {
			log.log(Level.INFO, uec + " needs to be updated for uec.getLastUpdate() on KIND_OFFER");
			return true;
		}
		if( service.getLastUpdate(EntityKind.KIND_STORE).after(uec.getLastUpdate())) {
			log.log(Level.INFO, uec + " needs to be updated for uec.getLastUpdate() on KIND_STORE");
			return true;
		}
		
		return false;
	}

	@Override
	public boolean needsUpdate(UserEntityCache uec, ViewLocation vl) throws ASException {

		if( uec.getLastUpdate() == null ) return true;
		if( uec.getExpiresOn() == null || uec.getExpiresOn().before(new Date())) return true;

		// We will use a 10 seconds adjustment to avoid collisions. For example, we add a new Store, 
		// and immediately a task is submitted with the generation of the geo entities associated.
		// We hope that task will finish more or less after 10 seconds, so we adjust the time
		Date correctedDate = new Date(uec.getLastUpdate().getTime() - 10000);
		
		if( service.getLastUpdate(EntityKind.KIND_FINANCIAL_ENTITY).after(correctedDate)) return true;		
		if( service.getLastUpdate(EntityKind.KIND_SHOPPING).after(correctedDate)) return true;
		if( service.getLastUpdate(EntityKind.KIND_BRAND).after(correctedDate)) return true;
		if( service.getLastUpdate(EntityKind.KIND_OFFER).after(correctedDate)) return true;
		if( service.getLastUpdate(EntityKind.KIND_STORE).after(correctedDate)) return true;

		// if we have no elements in the cache... probably we need to update
		if( uec.getEntities().size() == 0 ) return true;
		
		return false;
	}

	@Override
	public boolean needsUpdate(UserEntityCache uec, Integer entityKind) throws ASException {

		if( uec.getLastUpdate() == null ) return true;
		if( uec.getExpiresOn() == null || uec.getExpiresOn().before(new Date())) return true;
		if( service.getLastUpdate(entityKind).after(uec.getLastUpdate())) return true;		
		
		return false;
	}

	public String createUserIdUsingViewLocation(ViewLocation vl) {
		return "Generic_" + vl.getCountry();
	}
	
	@Override
	public Key createKey(ViewLocation vl, Integer entityKind, int returnType) throws ASException {
		try {
			if(vl == null || !StringUtils.hasText(vl.getCountry()) ){
				throw ASExceptionHelper.invalidArgumentsException("viewLocation cannot be null");
			}
			
			if( entityKind == null ) {
				throw ASExceptionHelper.invalidArgumentsException("kind cannot be null");
			}
			
			String sKey = keyHelper.resolveKey(createUserIdUsingViewLocation(vl) + "_" + entityKind + "_" + returnType);
			return (Key)keyHelper.obtainKey(UserEntityCache.class, sKey);

		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.notAcceptedException();
		}
	}
	
	@Override
	public Key createKey(String userId, Integer entityKind, int returnType) throws ASException {
		try {
			if(!StringUtils.hasText(userId) || entityKind == null ){
				throw ASExceptionHelper.notAcceptedException();
			}
			
			String sKey = keyHelper.resolveKey(userId + "_" + entityKind + "_" + returnType);
			return (Key)keyHelper.obtainKey(UserEntityCache.class, sKey);

		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.notAcceptedException();
		}
	}

	@Override
	public int needsUpdate(User user) throws ASException {

		List<Integer> entityKinds = CollectionFactory.createList();
		entityKinds.add(EntityKind.KIND_BRAND);
		entityKinds.add(EntityKind.KIND_FINANCIAL_ENTITY);
		entityKinds.add(EntityKind.KIND_SHOPPING);
		entityKinds.add(EntityKind.KIND_OFFER);
		
		for( int kind : entityKinds ) {
			Key k = createKey(user.getIdentifier(), kind, UserEntityCache.TYPE_FAVORITES_ONLY);
			try {
				UserEntityCache uec = get(k.getName(), false);
				if( uec == null || needsUpdate(uec, user)) return kind;
			} catch( ASException e ) {
				if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
					log.log(Level.SEVERE, e.getMessage(), e);
					throw e;
				} else {
					return kind;
				}
			}
		}
		
		return EntityKind.NONE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserEntityCache> getEvicted(Date limit, Range range)
			throws ASException {
	
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<UserEntityCache> ret = new ArrayList<UserEntityCache>();

		try {
			Query query = pm.newQuery(UserEntityCache.class);
			query.setFilter("lastUpdate < parmLimit");
			query.declareParameters("java.util.Date parmLimit");
			if( range != null ) {
				if( StringUtils.hasText(range.getCursor())) {
					// Query q = the same query that produced the cursor
					// String cursorString = the string from storage
					Cursor cursor = Cursor.fromWebSafeString(range.getCursor());
					Map<String, Object> extensionMap = new HashMap<String, Object>();
					extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
					query.setExtensions(extensionMap);
					query.setRange(0, (range.getTo() - range.getFrom()));
				} else {
					query.setRange(range.getFrom(), range.getTo());
				}
			}
			
			Map<String, Object> parameters = CollectionFactory.createMap();
			parameters.put("parmLimit", limit);

			List<UserEntityCache> elems = (List<UserEntityCache>)query.executeWithMap(parameters); 

			// Store the cursorString
			if( range != null ) {
				Cursor cursor = JDOCursorHelper.getCursor(elems);
				range.setCursor(cursor.toWebSafeString());
			}

			if (elems != null) {
				// force to read
				for (UserEntityCache obj : elems) {
					ret.add(obj);
				}
			}

		}catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}		
		return ret;

	}

	@Override
	public long countEvicted(Date limit) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Query query = pm.newQuery(UserEntityCache.class);
			query.setFilter("lastUpdate < parmLimit");
			query.declareParameters("java.util.Date parmLimit");

			Map<String, Object> parameters = CollectionFactory.createMap();
			parameters.put("parmLimit", limit);

			query.setResult("count(this)");
			Long results = Long.parseLong(query.executeWithMap(parameters).toString());
			return (long)results;
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
	    } finally  {
			pm.close();
	    }	
	}
}
