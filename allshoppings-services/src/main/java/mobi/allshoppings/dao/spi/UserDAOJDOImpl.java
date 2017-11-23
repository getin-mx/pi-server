package mobi.allshoppings.dao.spi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Cursor;
import com.inodes.datanucleus.model.Email;
import com.inodes.datanucleus.model.JDOCursorHelper;

import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Friend;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public class UserDAOJDOImpl extends GenericDAOJDO<User> implements UserDAO {
	private static final Logger log = Logger.getLogger(UserDAOJDOImpl.class.getName());
	private static int MAX_SUBQUERIES = 100;

	public UserDAOJDOImpl() {
		super(User.class);
	}

	@Override
	public void create(User obj) throws ASException {

		if(obj == null){
			throw ASExceptionHelper.notAcceptedException();
		}
		
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			if(doesEntityExist(pm, clazz, obj.getKey()) == true){
				log.info(clazz.getSimpleName() + " already exists");
				throw ASExceptionHelper.alreadyExistsException();
			}

			if( obj.getContactInfo().getMail() != null && StringUtils.hasText(obj.getContactInfo().getMail().getEmail())) {
				Query query = pm.newQuery(User.class);
				query.declareParameters("String emailParam");
				query.setFilter("contactInfo.mail == emailParam");

				Map<String, Object> params = new HashMap<String, Object>();
				params.put("emailParam", obj.getContactInfo().getMail().getEmail());
				query.setRange(0, 2);

				@SuppressWarnings("unchecked")
				List<User> list = (List<User>)query.executeWithMap(params);
				if( list.size() > 0 ) {
					throw ASExceptionHelper.mailAlreadyExistsException();
				}
			}
			
			obj.preStore();
			pm.currentTransaction().begin();
			pm.makePersistent(obj);
			pm.currentTransaction().commit();
			obj.getLastUpdate();
			if( cacheHelper != null )
				cacheHelper.put(getCacheKey(clazz, keyHelper.obtainEncodedKey(obj.getKey())), obj);

		}catch(ASException ASException){
			if(pm.currentTransaction().isActive()){
				pm.currentTransaction().rollback();
			}
			throw ASException;
		}catch(Exception e){
			if(pm.currentTransaction().isActive()){
				pm.currentTransaction().rollback();
			}
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}finally{
			pm.close();
		}
	}

	@Override
	public void update(User obj) throws ASException {
		if(obj == null){
			throw ASExceptionHelper.notAcceptedException();
		}
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
 		try{
			pm.currentTransaction().begin();
			
			if(doesEntityExist(pm, clazz, obj.getKey()) == false) {
				throw ASExceptionHelper.notFoundException();
			}

			if( obj.getContactInfo().getMail() != null && StringUtils.hasText(obj.getContactInfo().getMail().getEmail())) {
				Query query = pm.newQuery(User.class);	
				query.declareParameters("String emailParam");
				query.setFilter("contactInfo.mail == emailParam");
				query.setRange(0, 2);

				Map<String, Object> params = new HashMap<String, Object>();
				params.put("emailParam", obj.getContactInfo().getMail().getEmail());

				@SuppressWarnings("unchecked")
				List<User> list = (List<User>)query.executeWithMap(params);
				for( User user : list ) {
					if(!user.getIdentifier().equals(obj.getIdentifier()))
						throw ASExceptionHelper.mailAlreadyExistsException();
				}
			}
			
 			obj.preStore();
 		 	boolean doc = pm.getDetachAllOnCommit();
 		 	pm.setDetachAllOnCommit(false);
			pm.makePersistent(obj);
			pm.currentTransaction().commit();
			obj.getLastUpdate();
			pm.flush();
		 	pm.setDetachAllOnCommit(doc);
			cacheHelper.put(getCacheKey(clazz, keyHelper.obtainIdentifierFromKey(obj.getKey())), obj);
			cacheHelper.put(obj.getSecuritySettings().getAuthToken(), obj);
			
		}catch(ASException ASException){
			if(pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			throw ASException;
		}catch(Exception e){
			if(pm.currentTransaction().isActive()){
				pm.currentTransaction().rollback();
			}
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}finally{
			pm.close();
		}	
	}

	/**
	 * Obtains a user information from its authentication token
	 */
	@Override
	public User getByAuthToken(String token) throws ASException {
		if (token == null) {
			throw ASExceptionHelper.notAcceptedException();
		}
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Query query = pm.newQuery(User.class);
			query.declareParameters("String token");

			query.setFilter("securitySettings.authToken == token");
			@SuppressWarnings("unchecked")
			List<User> result = (List<User>)query.execute(token);
			if (result.size() != 1) {
				throw ASExceptionHelper.tokenExpiredException();
			}
			User user = result.get(0);
			return pm.detachCopy(user);
		} catch(ASException ASException) {
			throw ASException;
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally  {
			pm.close();
		}
	}

	/**
	 * Obtains user information based on its email
	 */
	public User getByEmail(Email email) throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		User returnedUser = null;

		try {

			if(null == email || email.getEmail().equals("")){
				throw ASExceptionHelper.notAcceptedException();
			}

			Query query = pm.newQuery(User.class);	
			query.declareParameters("String email");
			query.setFilter("contactInfo.mail == email");

			@SuppressWarnings("unchecked")
			List<User> result = (List<User>)query.execute(email.getEmail());

			if (result.size() > 0) {
				returnedUser = pm.detachCopy(result.get(0));
			} else {
				query = pm.newQuery(User.class);	
				query.declareParameters("String email");
				query.setFilter("uEmail == email");

				@SuppressWarnings("unchecked")
				List<User> result2 = (List<User>)query.execute(email.getEmail().toUpperCase());
				if (result2.size() > 0) {
					returnedUser = pm.detachCopy(result2.get(0));
				}
			}

			return returnedUser;

		} catch(ASException e) {
			log.log(Level.WARNING, "exception catched", e);
			throw e;
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}		
	}

	public User getByFacebookUserId(String facebookId) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		User returnedUser = null;

		try {

			if(!StringUtils.hasText(facebookId)){
				throw ASExceptionHelper.notAcceptedException();
			}

			Query query = pm.newQuery(User.class);	
			query.declareParameters("String facebookIdParm");
			query.setFilter("contactInfo.facebookId == facebookIdParm");

			@SuppressWarnings("unchecked")
			List<User> result = (List<User>)query.execute(facebookId);

			if (result.size() > 0) {
				returnedUser = pm.detachCopy(result.get(0));
			}

			return returnedUser;

		} catch(ASException e) {
			log.log(Level.WARNING, "exception catched", e);
			throw e;
		} catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}		
	}
	
	/**
	 * Obtains user information based on its email
	 */
	public User getByFacebookUserIdOrEmail(String facebookId, Email email) throws ASException {
		User user = null;

		// Fist, I try to look up using facebookId
		try {
			user = getByFacebookUserId(facebookId);
		} catch( Exception e ) {
			// I assume it was not found
		}
		
		// If none was found, I try using email
		if( user == null ) {
			try {
				user = getByEmail(email);
			} catch( Exception e ) {
				// I assume it was not found
			}
		}

		// If I didn't found the user... just report it as an exception
		if( user == null ) {
			throw ASExceptionHelper.notFoundException(); 
		}
		return user;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getByEmail(List<String> emails) throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<User> returnedUsers = new ArrayList<User>();

		try {
			Query query = pm.newQuery(User.class);
			int count = emails.size();
			if(count > MAX_SUBQUERIES){
				throw ASExceptionHelper.notAcceptedException();
			}
			List<Email> queryEmails = new ArrayList<Email>();
			for (int i = 0; i < emails.size(); i++) {
				queryEmails.add(new Email(emails.get(i)));
			}

			query.setFilter("param.contains(mail)");
			query.declareParameters("java.util.Collection param");
			List<User> users = (List<User>)query.execute(queryEmails); 

			if (users != null) {
				// force to read
				for (User user : users) {
					returnedUsers.add(user);
				}
			}

		}catch(ASException e){
			log.log(Level.WARNING, "exception catched", e);
			throw e;
		}catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}		
		return returnedUsers;
	}

	@Override
	public User getByEmail(String email) throws ASException {
		return getByEmail(new Email(email));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getActiveUsers(Range range) throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<User> returnedUsers = new ArrayList<User>();

		try {
			Query query = pm.newQuery(User.class);
			query.setFilter("activityStatus == " + User.STATUS_ACTIVE);
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

			List<User> users = (List<User>)query.executeWithMap(parameters); 

			// Store the cursorString
			if( range != null ) {
				Cursor cursor = JDOCursorHelper.getCursor(users);
				range.setCursor(cursor.toWebSafeString());
			}
			
			if (users != null) {
				// force to read
				for (User user : users) {
					returnedUsers.add(user);
				}
			}

		}catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}		
		return returnedUsers;
	}

	/**
	 * Get a list of instances of this entity according to a selected range.<br>
	 * This is mostly preferred against {@link #getAll()}
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param lastUpdate
	 *            A reference date to bring the entities from
	 * @param afterLastUpdateDate
	 *            If lastUpdate is not null, it decides which rows to bring. If
	 *            this parameter is true, returns the entities which last update
	 *            is after to the reference date, but if this is false, returns
	 *            the rows which last update is before to the reference date
	 * @param status
	 *            A list of status to select between the results
	 * @param range
	 *            The range object that bounds the query limits
	 * @param order
	 *            Property to use as order. If null, sets default order
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public List<User> getUsingLastUpdateStatusAndRangeAndRole(PersistenceProvider pp, Date lastUpdate,
			boolean afterLastUpdateDate, List<Integer> status, Range range, String order, List<Integer> role,
			Map<String, String> attributes, boolean detachable) throws ASException {

		List<User> returnedObjs = new ArrayList<User>();

		PersistenceManager pm;
		if( pp == null ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			if( status != null && status.size() > 0 )
				filters.add(toListFilterCriteria("securitySettings.status", status, false));
			
			if( role != null && role.size() > 0 )
				filters.add(toListFilterCriteria("securitySettings.role", role, false));

			// Last update parameter
			if( lastUpdate != null ) {
				declaredParams.add("java.util.Date lastUpdateParam");
				filters.add(afterLastUpdateDate ? "lastUpdate >= lastUpdateParam" : "lastUpdate <= lastUpdateParam");
				parameters.put("lastUpdateParam", lastUpdate);
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

			// Sets order as required
			if( StringUtils.hasText(order)) query.setOrdering(sanitizeOrder(order));

			@SuppressWarnings("unchecked")
			List<User> objs = (List<User>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (User obj : objs) {
					if( detachable )
						returnedObjs.add(pm.detachCopy(obj));
					else
						returnedObjs.add(obj);
				}
			}

			// Store the cursorString
			if( range != null ) {
				Cursor cursor = JDOCursorHelper.getCursor(objs);
				if( cursor != null )
					range.setCursor(cursor.toWebSafeString());
			}

		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			if( null == pp ) pm.close();
		}

		return returnedObjs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getTopFifteenRanking(User me, boolean global, boolean friends) throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<User> returnedUsers = new ArrayList<User>();
		long max = 15;
		
		try {
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(User.class);
			
			if( friends ) {
				Query q2 = pm.newQuery(Friend.class);
				q2.setFilter("userId1 == '" + me.getIdentifier() + "'");
				List<Friend> friendList = (List<Friend>)q2.execute();
				List<String> ids = CollectionFactory.createList();
				ids.add(me.getIdentifier().toUpperCase());
				for(Friend f : friendList ) {
					ids.add(f.getUserId2().toUpperCase());
				}
				declaredParams.add("java.util.List ids");
				filters.add("ids.contains(uIdentifier)");
				parameters.put("ids", ids);
			} else if( !global ) {
				declaredParams.add("String vlCountryParam");
				filters.add("viewLocation.country == vlCountryParam");
				parameters.put("vlCountryParam", me.getViewLocation().getCountry());
			}
			
			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			query.setRange(0, max);
			query.setOrdering("points desc");
			List<User> users = (List<User>)query.executeWithMap(parameters); 

			boolean meFound = false;
			if (users != null) {
				// force to read
				for (User user : users) {
					if( !meFound && me.getPoints() >= user.getPoints()) {
						returnedUsers.add(me);
						meFound = true;
					}
					if(!me.getIdentifier().equals(user.getIdentifier())) {
						if( user.getPoints() != null && user.getPoints() > 0 ) { 
							returnedUsers.add(user);
						}
					}
				}
			}
			
			if(returnedUsers.size() > max ) returnedUsers.remove(max - 1);
			if( !meFound ) {
				if(returnedUsers.size() >= max ) returnedUsers.remove(max - 1);
				returnedUsers.add(me);
			}

		}catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}		
		return returnedUsers;
	}

	@Override
	public long countActiveUsers() throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Query query = pm.newQuery(User.class);
			query.setFilter("activityStatus == " + User.STATUS_ACTIVE);

			Map<String, Object> parameters = CollectionFactory.createMap();

			query.setResult("count(this)");
			Long results = Long.parseLong(query.executeWithMap(parameters).toString());
			return (long)results;
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
	    } finally  {
			pm.close();
	    }	
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getByFullName(List<String> fullnames)  throws ASException{
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<User> returnedUsers = new ArrayList<User>();

		try {
			Query query = pm.newQuery(User.class);
			int count = fullnames.size();
			if(count > MAX_SUBQUERIES){
				throw ASExceptionHelper.notAcceptedException();
			}

			query.setFilter("param.contains(fullName)");
			query.declareParameters("java.util.Collection param");
			List<User> users = (List<User>)query.execute(fullnames); 

			if (users != null) {
				// force to read
				for (User user : users) {
					returnedUsers.add(user);
				}
			}

		}catch(ASException e){
			log.log(Level.WARNING, "exception catched", e);
			throw e;
		}catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}		
		return returnedUsers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getByType(int type, Range range, String order) throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<User> returnedUsers = new ArrayList<User>();
		log.info("getByType entry :" + new Integer(type));
		try {
			Query query = pm.newQuery(User.class);

			query.declareParameters("int typeParam");
			query.setFilter("type == typeParam");
			query.setRange(range.getFrom(), range.getTo());
			query.setOrdering("name " + order);

			returnedUsers = (List<User>)query.execute(type);
			log.info("XXX: " + returnedUsers);
			if(returnedUsers == null){
				throw ASExceptionHelper.notFoundException();
			}
		} catch(ASException e){
			log.log(Level.WARNING, "AS exception catched", e);
			throw e;
		}catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}		
		return returnedUsers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getMessageEnabledUsingStatusAndRange(List<Integer> status, Range range)
			throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<User> returnedUsers = new ArrayList<User>();

		try {
			Query query = pm.newQuery(User.class);

			Map<String, Object> parameters = CollectionFactory.createMap();
			query.declareParameters("boolean receivePushMessagesParam, boolean geoFenceEnabledParam, java.util.Collection statusParam");
			query.setFilter("receivePushMessages == receivePushMessagesParam && geoFenceEnabled == geoFenceEnabledParam");
			parameters.put("receivePushMessagesParam", true);
			parameters.put("geoFenceEnabledParam", true);
			parameters.put("statusParam", status);
			query.setRange(range.getFrom(), range.getTo());
			query.setOrdering("key");

			returnedUsers = (List<User>)query.executeWithMap(parameters);
			if(returnedUsers == null || returnedUsers.size() == 0){
				throw ASExceptionHelper.notFoundException();
			}
		} catch(ASException e){
			log.log(Level.WARNING, "AS exception catched", e);
			throw e;
		}catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}		
		return returnedUsers;
	}

	@Override
	public long countMessageEnabledUsingStatus(List<Integer> status) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Query query = pm.newQuery(User.class);

			Map<String, Object> parameters = CollectionFactory.createMap();
			query.declareParameters("boolean receivePushMessagesParam, boolean geoFenceEnabledParam, java.util.ArrayList statusParam");
			query.setFilter("receivePushMessages == receivePushMessagesParam && geoFenceEnabled == geoFenceEnabledParam");
			parameters.put("receivePushMessagesParam", true);
			parameters.put("geoFenceEnabledParam", true);
			parameters.put("statusParam", status);
			query.setOrdering("key");

			query.setResult("count(this)");
			Long results = Long.parseLong(query.executeWithMap(parameters).toString());
			return (long)results;
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
	    } finally  {
			pm.close();
	    }	
	}

	@Override
	public User getByRecoveryToken(String token) throws ASException {
		if (!StringUtils.hasText(token)) {
			throw ASExceptionHelper.notAcceptedException();
		}
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Query query = pm.newQuery(User.class);
			query.declareParameters("String token");

			query.setFilter("securitySettings.recoveryToken == token");
			@SuppressWarnings("unchecked")
			List<User> result = (List<User>)query.execute(token);
			if (result.size() != 1) {
				throw ASExceptionHelper.tokenExpiredException();
			}
			User user = result.get(0);
			if( user.getSecuritySettings().getRecoveryTokenValidity().before(new Date())) {
				throw ASExceptionHelper.tokenExpiredException();
			}
			return pm.detachCopy(user);
		} catch(ASException ASException) {
			throw ASException;
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally  {
			pm.close();
		}
	}

	@Override
	public void updateWithoutChangingMail(User obj) throws ASException {
		super.update(obj);
	}

}
