package mobi.allshoppings.dao.spi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.ObjectState;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Cursor;
import com.inodes.datanucleus.model.JDOCursorHelper;
import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.auth.UserInfo;
import mobi.allshoppings.dao.GenericDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.UserSecurity;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.interfaces.ViewLocationAware;
import mobi.allshoppings.model.tools.ASScoredDocument;
import mobi.allshoppings.model.tools.ASSearchField;
import mobi.allshoppings.model.tools.CacheHelper;
import mobi.allshoppings.model.tools.IndexHelper;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.model.tools.MultiLang;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CustomDatatableFilter;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public class GenericDAOJDO<T extends ModelKey> implements GenericDAO<T> {

	protected KeyHelper keyHelper = new KeyHelperGaeImpl();
	@Autowired
	protected IndexHelper indexHelper;
	@Autowired
	protected CacheHelper cacheHelper;

	private T objectExample;

	Class<T> clazz;
	Logger log;

	/**
	 * Generic Constructor
	 * 
	 * @param clazz
	 */
	public GenericDAOJDO(Class<T> clazz) {
		super();
		this.clazz = clazz;
		this.log = Logger.getLogger(clazz.getName());
	}

	/**
	 * Obtains the class affected by this DAO
	 */
	@Override
	public Class<T> getAffectedClass() {
		return clazz;
	}

	/**
	 * @return the indexHelper
	 */
	public IndexHelper getIndexHelper() {
		return indexHelper;
	}

	/**
	 * Gets an entity using its identifier
	 * 
	 * @param indentifier
	 *            The entity Identifier
	 */
	@Override
	public T get(String identifier) throws ASException {
		return this.get(identifier, false);
	}

	/**
	 * Gets an entity using its identifier
	 * 
	 * @param identifier
	 *            The entity Identifier
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public T get(String identifier, Boolean detachable) throws ASException {
		return get(null, identifier, detachable);
	}

	/**
	 * Gets an entity using its identifier
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the lookup
	 *            transaction. If this value is null, then a new Persistence
	 *            Manager will be created
	 * @param identifier
	 *            The entity Identifier
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public T get(PersistenceProvider pp, String identifier, Boolean detachable) throws ASException {

		if (identifier == null || identifier.equals("null")) {
			log.info("not accepted:id null");
			throw ASExceptionHelper.notAcceptedException();
		}

		@SuppressWarnings("unchecked")
		T bsx = cacheHelper != null ? (T)cacheHelper.get(getCacheKey(clazz, identifier)) : null;
		if( null != bsx ) return bsx;

		PersistenceManager pm;
		if( null == pp ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}
		try {
			Key key = keyHelper.<Key>obtainKey(clazz, identifier);
			T bs = pm.getObjectById(clazz, key);
			if(cacheHelper != null) cacheHelper.put(getCacheKey(clazz, identifier), bs);

			if (detachable == true) {
				return pm.detachCopy(bs);
			}

			return bs;

		} catch(Exception e) {
			if (!(e instanceof JDOObjectNotFoundException)) {
				log.log(Level.SEVERE, "exception catched", e);
			}
			throw ASExceptionHelper.notFoundException(clazz.getName() + " " + identifier);
		} finally  {
			if( null == pp ) pm.close();
		}
	}

	/**
	 * Deletes an entity using it's Identifier as key
	 * 
	 * @param identifier
	 *            The Identifier of the entity that will be deleted
	 */
	@Override
	public void delete(String identifier) throws ASException {
		delete(null, identifier);
	}

	/**
	 * Deletes an entity using it's Identifier as key
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param identifier
	 *            The Identifier of the entity that will be deleted
	 */
	@Override
	public void delete(PersistenceProvider pp, String identifier) throws ASException {

		if (identifier == null) {
			log.info("not accepted:id null");
			throw ASExceptionHelper.notAcceptedException();
		}

		PersistenceManager pm;
		if( null == pp ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}

		try {
			Key key = keyHelper.<Key>obtainKey(clazz, identifier);
			T bs = pm.getObjectById(clazz, key);
			pm.currentTransaction().begin();
			pm.makePersistent(bs);
			pm.deletePersistent(bs);
			pm.currentTransaction().commit();
			if( cacheHelper != null ) cacheHelper.remove(getCacheKey(clazz, identifier));

		} catch(Exception e) {
			if (!(e instanceof JDOObjectNotFoundException)) {
				log.log(Level.SEVERE, "exception catched", e);
			}
			throw ASExceptionHelper.notFoundException();
		} finally  {
			if( pp == null ) pm.close();
		}
	}

	/**
	 * Deletes all entities of a specified class
	 * 
	 * @throws ASException
	 */
	@Override
	public void deleteAll() throws ASException {
		deleteAll(null);
	}

	/**
	 * Deletes all entities of a specified class
	 * 
	 * @param pp
	 *            The persistence provider to use
	 * @throws ASException
	 */
	@Override
	public void deleteAll(PersistenceProvider pp) throws ASException {
		PersistenceManager pm;
		if( null == pp ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}

		try {
			Query q = pm.newQuery(clazz);
			q.deletePersistentAll();

		} catch(Exception e) {
			if (!(e instanceof JDOObjectNotFoundException)) {
				log.log(Level.SEVERE, "exception catched", e);
			}
		} finally  {
			if( pp == null ) pm.close();
		}
	}

	/**
	 * Deletes an entity from the datastore
	 * 
	 * @param obj
	 *            The Identifier of the entity that will be deleted
	 */
	@Override
	public void delete(T obj) throws ASException {
		delete( null, obj );
	}	

	/**
	 * Deletes an entity from the datastore
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param obj
	 *            The entity that is going to be deleted from the datastore
	 */
	@Override
	public void delete(PersistenceProvider pp, T obj) throws ASException {

		PersistenceManager pm;
		if( pp == null ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
			pm.currentTransaction().begin();
		} else {
			pm = pp.get();
		}

		try {
			pm.deletePersistent(obj);
			if( pp == null) pm.currentTransaction().commit();
			if( cacheHelper != null ) cacheHelper.remove(getCacheKey(clazz, keyHelper.obtainIdentifierFromKey(obj.getKey())));
		} catch(Exception e) {
			if(pp == null && pm.currentTransaction().isActive()){
				pm.currentTransaction().rollback();
			}
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			if( pp == null ) pm.close();
		}

	}

	/**
	 * Writes a new entity to the datastore
	 * 
	 * @param obj
	 *            The Entity to be written
	 */
	@Override
	public void create(T obj) throws ASException {
		create( null, obj, true );
	}

	/**
	 * Writes a new entity to the datastore
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param obj
	 *            The Entity to be written
	 */
	@Override
	public void create(PersistenceProvider pp, T obj, boolean performPreStore) throws ASException {
		if (obj == null) {
			throw ASExceptionHelper.notAcceptedException();
		}
		PersistenceManager pm;
		if (null == pp) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}

		try {
			if (pp == null)
				pm.currentTransaction().begin();

			if (doesEntityExist(pm, clazz, obj.getKey())) {
				log.info(clazz.getSimpleName() + " already exists");
				throw ASExceptionHelper.alreadyExistsException();
			}

			if( performPreStore )
				obj.preStore();

			pm.makePersistent(obj);

			if (pp == null)
				pm.currentTransaction().commit();

			obj.getLastUpdate();

			if( cacheHelper != null ) cacheHelper.put(getCacheKey(clazz, keyHelper.obtainIdentifierFromKey(obj.getKey())), obj);

		} catch (ASException ASException) {
			if (pp == null && pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			throw ASException;
		} catch (Exception e) {
			if (pp == null && pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			if (null == pp) {
				pm.close();
			}
		}
	}

	/**
	 * Updates an entity that previously existed in the datastore
	 * 
	 * @param obj
	 *            The Entity to be updated
	 */
	@Override
	public void update(T obj) throws ASException {
		update( null, obj, true );
	}

	/**
	 * Updates an entity that previously existed in the datastore
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param obj
	 *            The Entity to be updated
	 */
	@Override
	public void update(PersistenceProvider pp, T obj, boolean performPreStore) throws ASException {
		if(obj == null){
			throw ASExceptionHelper.notAcceptedException();
		}
		PersistenceManager pm;
		if( pp == null ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}

		try {
			if (pp == null)
				pm.currentTransaction().begin();

			if(doesEntityExist(pm, clazz, obj.getKey()) == false){
				throw ASExceptionHelper.notFoundException();
			}

			if( performPreStore )
				obj.preStore();

			// This is a form to check if there is a cross context bug
			// In a cross context scenario, the state of the object is marked as transient
			// because a class loader problem. So if it is the case, the simpler solution
			// is to delete the previous object and create a new one with the same key
			ObjectState objs = JDOHelper.getObjectState(obj);
			if(objs.equals(ObjectState.TRANSIENT)) {
				delete(obj.getIdentifier());
			}

			enqueuePersistence(pm, obj);

			if (pp == null)
				pm.currentTransaction().commit();

			obj.getLastUpdate();

			if( cacheHelper != null ) cacheHelper.put(getCacheKey(clazz, keyHelper.obtainIdentifierFromKey(obj.getKey())), obj);
		}catch(ASException ASException){
			if(pp == null && pm.currentTransaction().isActive()){
				pm.currentTransaction().rollback();
			}
			throw ASException;
		}catch(Exception e){
			if(pp == null && pm.currentTransaction().isActive()){
				pm.currentTransaction().rollback();
			}
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}finally{
			if( pp == null ) pm.close();
		}	
	}
	
	@Override
	public void update(List<T> obj) throws ASException {
		update(null, obj, true);
	}

	@Override
	public void update(PersistenceProvider pp, List<T> obj, boolean performPreStore) throws ASException {
		if(obj == null){
			throw ASExceptionHelper.notAcceptedException();
		}
		PersistenceManager pm;
		if( pp == null ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}

		try {
			if( pp == null && !pm.currentTransaction().isActive()) pm.currentTransaction().begin();

			for(T o : obj) {
				if(doesEntityExist(pm, clazz, o.getKey()) == false){
					throw ASExceptionHelper.notFoundException();
				}

				if( performPreStore )
					o.preStore();
			}
			
			pm.makePersistentAll(obj);

			if (pp == null)
				pm.currentTransaction().commit();

			if( cacheHelper != null )
				for(T o : obj )
					cacheHelper.put(getCacheKey(clazz, keyHelper.obtainIdentifierFromKey(o.getKey())), o);
			
		}catch(Exception e){
			if(pp == null && pm.currentTransaction().isActive()){
				pm.currentTransaction().rollback();
			}
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}finally{
			if( pp == null ) pm.close();
		}
	}

	/**
	 * Creates or Updates an entity on the datastore, no matters if the entity
	 * previously existed or not in the datastore
	 * 
	 * @param obj
	 *            The Entity to be updated
	 */
	@Override
	public void createOrUpdate(T obj) throws ASException {
		createOrUpdate( null, obj, true );
	}

	/**
	 * Creates or Updates an entity on the datastore, no matters if the entity
	 * previously existed or not in the datastore
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param obj
	 *            The Entity to be updated
	 */
	@Override
	public void createOrUpdate(PersistenceProvider pp, T obj, boolean performPreStore) throws ASException {
		if(obj == null){
			throw ASExceptionHelper.notAcceptedException();
		}
		PersistenceManager pm;
		if( pp == null ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}

		try {
			if( pp == null && !pm.currentTransaction().isActive()) pm.currentTransaction().begin();

			if(doesEntityExist(pm, clazz, obj.getKey())) {
				// This is a form to check if there is a cross context bug
				// In a cross context scenario, the state of the object is marked as transient
				// because a class loader problem. So if it is the case, the simpler solution
				// is to delete the previous object and create a new one with the same key
				ObjectState objs = JDOHelper.getObjectState(obj);
				if(objs.equals(ObjectState.TRANSIENT)) {
					try {
						delete(obj.getIdentifier());
					} catch( Exception e ) {}
				}
			}

			if( performPreStore )
				obj.preStore();

			enqueuePersistence(pm, obj);

			if (pp == null)
				pm.currentTransaction().commit();

			obj.getLastUpdate();

			if( cacheHelper != null ) cacheHelper.put(getCacheKey(clazz, keyHelper.obtainIdentifierFromKey(obj.getKey())), obj);
		}catch(Exception e){
			if(pp == null && pm.currentTransaction().isActive()){
				pm.currentTransaction().rollback();
			}
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}finally{
			if( pp == null ) pm.close();
		}	
	}

	/**
	 * Creates or Updates an entity on the datastore, no matters if the entity
	 * previously existed or not in the datastore
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param obj
	 *            The Entity to be updated
	 */
	@Override
	public void createOrUpdate(PersistenceProvider pp, List<T> obj, boolean performPreStore) throws ASException {
		if(obj == null){
			throw ASExceptionHelper.notAcceptedException();
		}
		PersistenceManager pm;
		if( pp == null ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}

		try {
			if( pp == null && !pm.currentTransaction().isActive()) pm.currentTransaction().begin();

			if( performPreStore )
				for(T o : obj )
					o.preStore();

			try {
				pm.deletePersistentAll(obj);
			} catch( Exception e ) {}
			pm.makePersistentAll(obj);

			if (pp == null)
				pm.currentTransaction().commit();

			if( cacheHelper != null )
				for(T o : obj )
					cacheHelper.put(getCacheKey(clazz, keyHelper.obtainIdentifierFromKey(o.getKey())), o);
			
		}catch(Exception e){
			if(pp == null && pm.currentTransaction().isActive()){
				pm.currentTransaction().rollback();
			}
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}finally{
			if( pp == null ) pm.close();
		}	
	}

	/**
	 * Converts a String array of columns in a sort command line used by the
	 * internal JDO engine
	 * 
	 * @param columnSort
	 *            List of columns
	 * @param direction
	 *            Direction (ASC | DESC)
	 * @return A String with the fully functional command line
	 */
	protected String buildQuerySort(String[] columnSort, String direction) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < columnSort.length; i++ ) {
			sb.append(columnSort[i]).append(" ").append(direction);
			if( i < columnSort.length - 1 ) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}

	/**
	 * Builds a 'matches' filter for a query. This is similar to a 'like' query
	 * in SQL. <br>
	 * Remember that in the Google Datastore we can only use one 'matches'
	 * filter in every query... so be careful with this. It is better to use an
	 * index than a matches filter if you want to use a complex search.
	 * 
	 * @param columnSort
	 *            The columns list that will be used in the full query. The only
	 *            field used in this method is the first element of this list
	 * @param search
	 *            The value that will be searched
	 * @return The 'matches' query in a String form to be inserted in a JDO
	 *         query filter (or aggregated to any other condition if needed)
	 */
	protected String buildQueryFilter(String columnSort[], String search) {
		StringBuffer sb = new StringBuffer();
		sb.append(columnSort[0]).append(".matches('").append(search.trim().toUpperCase()).append(".*')");
		return sb.toString();
	}

	/**
	 * Builds a String equality filter for a query. <br>
	 * You can use as many equality filters as you wish with the Google
	 * Datastore, so use it as needed.
	 * 
	 * @param keyName
	 *            The entity property to be filtered
	 * @param keyValue
	 *            The value which will be used to filter
	 * @return A filter part in a String form to be inserted in a JDO query
	 *         filter (or aggregated to any other condition if needed)
	 */
	protected String buildQueryFilter(String keyName, String keyValue) {
		StringBuffer sb = new StringBuffer();
		sb.append(keyName).append(" == \'").append(keyValue).append("\'");
		return sb.toString();
	}

	/**
	 * Builds a query filter using the combination of a 'matches' filter and an
	 * equality filter
	 * 
	 * @see #buildQueryFilter(String[], String)
	 * @see #buildQueryFilter(String, String)
	 * 
	 * @param columnSort
	 *            The columns list that will be used in the full query. The only
	 *            field used in this method is the first element of this list
	 * @param search
	 *            The value that will be searched
	 * @param keyName
	 *            The entity property to be filtered
	 * @param keyValue
	 *            The value which will be used to filter
	 * @return A filter part in a String form to be inserted in a JDO query
	 *         filter (or aggregated to any other condition if needed)
	 */
	protected String buildQueryFilter(String columnSort[], String search, String keyName, String keyValue) {
		StringBuffer sb = new StringBuffer();
		sb.append(buildQueryFilter(columnSort, search));
		if( sb.length() > 0 ) {
			sb.append(" && ");
		}
		sb.append(buildQueryFilter(keyName, keyValue));
		return sb.toString();
	}

	/**
	 * Gets a list of entities using the js datatables rules. <br>
	 * Only shows batches of data, as the front end used in js datatables
	 * controls the data pagination <br>
	 * This is only used to present information in the front end... not used in
	 * real transactions.
	 * 
	 * @param columnSort
	 *            A String Array with the columns name.
	 * @param sortDirection
	 *            The direction that will be used to sort the information
	 * @param searchFields
	 *            A String Array with the fields used in searching
	 * @param search
	 *            A search query to filter information
	 * @param first
	 *            The first element to be selected in this batch
	 * @param last
	 *            The last element to be selected in this batch
	 * @param userInfo
	 *            Logged user information. This is used in selecting
	 *            authorization filters (which data the user can see and which
	 *            data can't), and location filters (as in the ViewLocation
	 *            object)
	 */
	@Override
	public List<T> getForTable(String[] columnSort, String sortDirection, String[] searchFields, String search, long first, long last, UserInfo userInfo) throws ASException {
		if( StringUtils.hasText(search)) {
			return getForTableSearch(columnSort, sortDirection, searchFields, search, first, last, userInfo);
		} else {
			return getForTableNoSearch(columnSort, sortDirection, searchFields, first, last, userInfo);
		}
	}

	/**
	 * Gets a list of entities using the js datatables rules. <br>
	 * Only shows batches of data, as the front end used in js datatables
	 * controls the data pagination <br>
	 * This is only used to present information in the front end... not used in
	 * real transactions.<br>
	 * This method is invoked only in case that a final user has entered a
	 * search query, so it will need to call the DocumentIndex API instead of a
	 * simple datastore query
	 * 
	 * @param columnSort
	 *            A String Array with the columns name.
	 * @param sortDirection
	 *            The direction that will be used to sort the information
	 * @param searchFields
	 *            A String Array with the fields used in searching
	 * @param search
	 *            A search query to filter information
	 * @param first
	 *            The first element to be selected in this batch
	 * @param last
	 *            The last element to be selected in this batch
	 * @param userInfo
	 *            Logged user information. This is used in selecting
	 *            authorization filters (which data the user can see and which
	 *            data can't), and location filters (as in the ViewLocation
	 *            object)
	 */
	private List<T> getForTableSearch( String[] columnSort, String sortDirection, String[] searchFields, String search, long first, long last, UserInfo userInfo) throws ASException {
		List<T> returnedObjs = new ArrayList<T>();

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			List<T> l = getUsingIndex(search, null, null, null, null, null, null);
			for( long i = first; i < last && i < l.size(); i++ ) {
				if( l.get((int)i) != null )  {
					if( safeAndInLimits(l.get((int)i), userInfo))
						returnedObjs.add(l.get((int)i));
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


	/**
	 * Gets a list of entities using the js datatables rules. <br>
	 * Only shows batches of data, as the front end used in js datatables
	 * controls the data pagination <br>
	 * This is only used to present information in the front end... not used in
	 * real transactions.<br>
	 * This method is invoked only in case that a final user didn't entered a
	 * search query, so it will need to call a simple datastore query, because
	 * it is not needed to use complex search APIs in here
	 * 
	 * @param columnSort
	 *            A String Array with the columns name.
	 * @param sortDirection
	 *            The direction that will be used to sort the information
	 * @param searchFields
	 *            A String Array with the fields used in searching
	 * @param search
	 *            A search query to filter information
	 * @param first
	 *            The first element to be selected in this batch
	 * @param last
	 *            The last element to be selected in this batch
	 * @param userInfo
	 *            Logged user information. This is used in selecting
	 *            authorization filters (which data the user can see and which
	 *            data can't), and location filters (as in the ViewLocation
	 *            object)
	 */
	@SuppressWarnings("unchecked")
	private List<T> getForTableNoSearch(String[] columnSort, String sortDirection, String[] searchFields, long first, long last, UserInfo userInfo) throws ASException {
		List<T> returnedObjs = new ArrayList<T>();

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Query query = pm.newQuery(clazz);
			if(columnSort.length > 0) query.setOrdering(sanitizeOrder(buildQuerySort(columnSort, sortDirection)));
			query.setRange(first, last);
			Map<String, Object> parameters = CollectionFactory.createMap();
			CustomDatatableFilter customFilter = buildCustomFilter(userInfo);
			if( customFilter != null ) {
				customFilter.delegateFilter(query, parameters);
			}

			List<T> objs = (List<T>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (T o : objs) {
					returnedObjs.add(o);
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


	/**
	 * Gets a list of detail entities using the js datatables rules. <br>
	 * It needs the key name and value of the primary filter, as in the previous
	 * row it describes, for it is a detail elements grabber.<br>
	 * Only shows batches of data, as the front end used in js datatables
	 * controls the data pagination <br>
	 * This is only used to present information in the front end... not used in
	 * real transactions.<br>
	 * This method is invoked only in case that a final user didn't entered a
	 * search query, so it will need to call a simple datastore query, because
	 * it is not needed to use complex search APIs in here
	 * 
	 * @param keyName
	 *            The property name that will be used to filter data
	 * @param keyValue
	 *            The property value that will be used to filter data
	 * @param columnSort
	 *            A String Array with the columns name.
	 * @param sortType
	 *            The direction that will be used to sort the information
	 * @param searchFields
	 *            A String Array with the fields used in searching
	 * @param search
	 *            A search query to filter information
	 * @param first
	 *            The first element to be selected in this batch
	 * @param last
	 *            The last element to be selected in this batch
	 * @param userInfo
	 *            Logged user information. This is used in selecting
	 *            authorization filters (which data the user can see and which
	 *            data can't), and location filters (as in the ViewLocation
	 *            object)
	 */
	@Override
	public List<T> getForTableWidthKey(String keyName, String keyValue, String[] columnSort,
			String sortType, String[] searchFields, String search, long first,
			long last, UserInfo userInfo) throws ASException {
		return getForTable(columnSort, sortType, searchFields, search, first, last, userInfo);
	}

	/**
	 * Counts details instances available of an entity, filtering by a certain
	 * key and value.<br>
	 * This method is used to count filtered detail elements (as it uses a key
	 * name and value of its header instance) in a js datatable representation
	 * 
	 * @param keyName
	 *            The property name that will be used to filter data
	 * @param keyValue
	 *            The property value that will be used to filter data
	 * @param userInfo
	 *            Logged user information. This is used in selecting
	 *            authorization filters (which data the user can see and which
	 *            data can't), and location filters (as in the ViewLocation
	 *            object)
	 */
	@Override
	public long count(String keyName, String keyValue, UserInfo userInfo) throws ASException {
		return count(userInfo);
	}


	/**
	 * Gets all the instances of this entity.<br>
	 * This method is not recommended for large entity groups, because the
	 * overhead can literally tear down an AppEngine instance (As in TimeOut
	 * Exception and stuff like that). <br>
	 * Only use it on your own responsability
	 */
	@Override
	public List<T> getAll() throws ASException {
		return getAllAndOrder(null, null);
	}

	/**
	 * Gets all the instances of this entity.<br>
	 * This method is not recommended for large entity groups, because the
	 * overhead can literally tear down an AppEngine instance (As in TimeOut
	 * Exception and stuff like that). <br>
	 * Only use it on your own responsability
	 * 
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public List<T> getAll(boolean detachable) throws ASException {
		return getAllAndOrder(null, null, detachable);
	}

	/**
	 * Gets all the instances of this entity.<br>
	 * This method is not recommended for large entity groups, because the
	 * overhead can literally tear down an AppEngine instance (As in TimeOut
	 * Exception and stuff like that). <br>
	 * Only use it on your own responsability
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 */
	@Override
	public List<T> getAll(PersistenceProvider pp) throws ASException {
		return getAllAndOrder(pp, null);
	}

	/**
	 * Gets all the instances of this entity.<br>
	 * This method is not recommended for large entity groups, because the
	 * overhead can literally tear down an AppEngine instance (As in TimeOut
	 * Exception and stuff like that). <br>
	 * Only use it on your own responsability
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public List<T> getAll(PersistenceProvider pp, boolean detachable) throws ASException {
		return getAllAndOrder(pp, null, detachable);
	}

	/**
	 * Gets all the instances of this entity, and order the results based on a
	 * particular property.<br>
	 * This method is not recommended for large entity groups, because the
	 * overhead can literally tear down an AppEngine instance (As in TimeOut
	 * Exception and stuff like that). <br>
	 * Only use it on your own responsability
	 * 
	 * @param order
	 *            The property that will be used to order the dataset
	 * 
	 */
	@Override
	public List<T> getAllAndOrder(String order) throws ASException {
		return getAllAndOrder(null, order, false);
	}

	/**
	 * Gets all the instances of this entity, and order the results based on a
	 * particular property.<br>
	 * This method is not recommended for large entity groups, because the
	 * overhead can literally tear down an AppEngine instance (As in TimeOut
	 * Exception and stuff like that). <br>
	 * Only use it on your own responsability
	 * 
	 * @param order
	 *            The property that will be used to order the dataset
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 */
	@Override
	public List<T> getAllAndOrder(PersistenceProvider pp, String order) throws ASException {
		return getAllAndOrder(null, order, false);
	}

	/**
	 * Gets all the instances of this entity, and order the results based on a
	 * particular property.<br>
	 * This method is not recommended for large entity groups, because the
	 * overhead can literally tear down an AppEngine instance (As in TimeOut
	 * Exception and stuff like that). <br>
	 * Only use it on your own responsability
	 * 
	 * @param order
	 *            The property that will be used to order the dataset
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public List<T> getAllAndOrder(String order, boolean detachable) throws ASException {
		return getAllAndOrder(null, order, detachable);
	}

	/**
	 * Gets all the instances of this entity, and order the results based on a
	 * particular property.<br>
	 * This method is not recommended for large entity groups, because the
	 * overhead can literally tear down an AppEngine instance (As in TimeOut
	 * Exception and stuff like that). <br>
	 * Only use it on your own responsability
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param order
	 *            The property that will be used to order the dataset
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public List<T> getAllAndOrder(PersistenceProvider pp, String order, boolean detachable) throws ASException {
		return getUsingStatusAndRange(pp, null, null, order, null, detachable);
	}

	/**
	 * Get a list of instances of this entity according to a selected range.<br>
	 * This is mostly preferred against {@link #getAll()}
	 * 
	 * @param status
	 *            A list of status to select between the results
	 * @param range
	 *            The range object that bounds the query limits
	 * @param order
	 *            Property to use as order. If null, sets default order
	 */
	@Override
	public List<T> getUsingStatusAndRange(List<Integer> status, Range range, String order) throws ASException {
		return getUsingStatusAndRange(null, status, range, order, null, true);
	}

	/**
	 * Get a list of instances of this entity according to a selected range.<br>
	 * This is mostly preferred against {@link #getAll()}
	 * 
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
	public List<T> getUsingStatusAndRange(List<Integer> status, Range range, String order, Map<String, String> attributes, boolean detachable) throws ASException {
		return getUsingStatusAndRange(null, status, range, order, attributes, detachable);
	}

	/**
	 * Get a list of instances of this entity according to a selected range.<br>
	 * This is mostly preferred against {@link #getAll()}
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param status
	 *            A list of status to select between the results
	 * @param range
	 *            The range object that bounds the query limits
	 * @param order
	 *            Property to use as order. If null, sets default order
	 */
	@Override
	public List<T> getUsingStatusAndRange(PersistenceProvider pp, List<Integer> status, Range range, String order) throws ASException {
		return getUsingStatusAndRange(pp, status, range, order, null, true);
	}

	/**
	 * Get a list of instances of this entity according to a selected range.<br>
	 * This is mostly preferred against {@link #getAll()}
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
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
	public List<T> getUsingStatusAndRange(PersistenceProvider pp, List<Integer> status, Range range, String order, Map<String, String> attributes, boolean detachable) throws ASException {
		return getUsingLastUpdateStatusAndRange(pp, null, true, status, range, order, attributes, detachable);
	}

	/**
	 * Get a list of instances of this entity according to a selected range.<br>
	 * This is mostly preferred against {@link #getAll()}
	 * 
	 * @param status
	 *            A list of status to select between the results
	 * @param range
	 *            The range object that bounds the query limits
	 * @param country
	 *            Country to search instances for
	 * @param order
	 *            Property to use as order. If null, sets default order
	 * @param attributes
	 *            Attributes list to add additional information
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public List<T> getUsingStatusAndRangeAndCountry(List<Integer> status, Range range, String country, String order, Map<String, String> attributes, boolean detachable) throws ASException {
		return getUsingLastUpdateStatusAndRange(null, null, true, status, range, order, attributes, detachable);
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
	public List<T> getUsingLastUpdateStatusAndRange(PersistenceProvider pp, Date lastUpdate, boolean afterLastUpdateDate, List<Integer> status, Range range, String order, Map<String, String> attributes, boolean detachable) throws ASException {

		List<T> returnedObjs = new ArrayList<T>();

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

			if( isThisClassSafeForStatusFiltering() && status != null && status.size() > 0 ) {
				filters.add(toListFilterCriteria("status", status, false));
			}

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
			List<T> objs = (List<T>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (T obj : objs) {
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

	/**
	 * Sanitizes an order query
	 * 
	 * @param order
	 *            The order query
	 * @return The sanitized order query
	 */
	public String sanitizeOrder(String order) {
		String[] parts = order.split(" ");
		Field[] fields = clazz.getDeclaredFields();
		StringBuffer sb = new StringBuffer();
		for( String part : parts ) {
			boolean found = false;
			for( Field field : fields ) {
				if( field.getName().equals(part) && field.getType().equals(MultiLang.class)) {
					sb.append(part).append(".values ");
					found = true;
					break;
				}
			}
			
			if(!found) {
				sb.append(part).append(" ");
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Gets a list of instances of this entity, based in a 'matches' query for
	 * the identifier and a View Location attribute.<br>
	 * This is commonly used in js datatable filters for the frontend.
	 * 
	 * @param pm
	 *            The Persistence Manager that will be used in this transaction
	 * @param q
	 *            The query value, used to make the filter
	 * @param vl
	 *            The view location element that will be used to localize
	 *            elements according its physical location
	 * @param status
	 *            A list of valid status to filter the results. If it is null,
	 *            then this parameter is ignored
	 * @return A list with the instances that matches the query
	 * @throws ASException
	 */
	protected List<T> getUsingLike(PersistenceManager pm, String q, ViewLocation vl, List<Integer> status) throws ASException {
		List<T> ret = new ArrayList<T>();

		try {
			Query query = pm.newQuery(clazz);

			// Parameter Declaration
			List<String> filters = CollectionFactory.createList();

			// Status filter
			filters.add(new StringBuffer().append("uIdentifier.matches('").append(q.replaceAll(" ", "_").toUpperCase()).append(".*')").toString());
			if( isThisClassSafeForStatusFiltering() && status != null && status.size() > 0 ) {
				filters.add(toListFilterCriteria("status", status, false));
			}

			// Set Filters And Ranges
			query.setFilter(toWellParametrizedFilter(filters));
			query.setRange(0, 25);

			// Set Ordering
			query.setOrdering("uIdentifier asc");

			// Executes the query
			@SuppressWarnings("unchecked")
			List<T> objs = (List<T>)query.execute();
			if (objs != null) {
				// force to read
				for (T obj : objs) {
					if( obj instanceof ViewLocationAware && vl != null ) {
						if( ((ViewLocationAware)obj).isAvailableFor(vl)) {
							ret.add(obj);
						}
					} else {
						ret.add(obj);
					}
				}
			}

		} catch (Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			pm.close();
		}

		return ret;
	}

	/**
	 * Get a list of instances from this entity that matches a Document Index
	 * search
	 * 
	 * @param q
	 *            The query that will be used to search the Document Index
	 *            database
	 * @param viewLocation
	 *            View Location from which this find is made
	 * @param status
	 *            A list of valid status to filter the results. If it is null,
	 *            then this parameter is ignored
	 */
	@Override
	public List<T> getUsingIndex(String q, ViewLocation viewLocation, List<Integer> status, Range range, Map<String, String> additionalFields, String order, String lang) throws ASException {
		return getUsingIndex(clazz.getName(), q, viewLocation, status, range, additionalFields, order, lang);
	}

	/**
	 * Get a list of instances from this entity that matches a Document Index
	 * search
	 * 
	 * @param indexName
	 *            The Document Index Name on which the search will be executed
	 * @param q
	 *            The query that will be used to search the Document Index
	 *            database
	 * @param viewLocation
	 *            View Location from which this find is made
	 * @param status
	 *            A list of valid status to filter the results. If it is null,
	 *            then this parameter is ignored
	 */
	@Override
	public List<T> getUsingIndex(String indexName, String q, ViewLocation viewLocation, List<Integer> status, Range range, Map<String, String> additionalFields, String order, String lang) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<T> ret = new ArrayList<T>();

		try {
			if( indexHelper != null ) {
				List<ASScoredDocument> tmp = indexHelper.query(indexName, q, additionalFields);
				if(tmp.isEmpty()) throw ASExceptionHelper.notFoundException(indexName +" " +q);
				Iterator<ASScoredDocument> i = tmp.iterator();
				while(i.hasNext()) {
					ASScoredDocument d = i.next();
					Iterator<ASSearchField> x = d.getFields("id").iterator();
					while(x.hasNext()) {
						ASSearchField f = x.next();
						try {
							T obj = get(f.getText(), true);
							if(!ret.contains(obj)) {
								if( obj instanceof ViewLocationAware && viewLocation != null ) {
									if( ((ViewLocationAware)obj).isAvailableFor(viewLocation)) {
										addToListConditionallyByStatus(ret, obj, status);
									}
								} else {
									addToListConditionallyByStatus(ret, obj, status);
								}
							}
						} catch( ASException e ) {
							return indexFallback(q);
						}
					}
				}
				
				// Ordering Stuff
				if( StringUtils.hasText(order)) {
					String orderParm = order.split(",")[0];
					String orderParts[] = orderParm.split(" ");
					String field = orderParts[0];
					boolean asc = true;
					if( orderParts.length > 1 && orderParts[1].equalsIgnoreCase("desc"))
						asc = false;
					
					Collections.sort(ret, new GenericModelKeyComparator(clazz, field, asc, lang));
				}

				// And limit stuff
				if( range != null ) {
					List<T> temp = CollectionFactory.createList();
					temp.addAll(ret);
					ret.clear();
					for( int j = range.getFrom(); j < range.getTo() && j < temp.size(); j++ ) {
						ret.add(temp.get(j));
					}
				}
			}

		} catch (Exception e) {
			if( e instanceof ASException ) {
				return indexFallback(q);
			} else { 
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			}
		} finally {
			pm.close();
		}

		return ret;

	}

	/**
	 * Conditionally add a result on a result list based in its status and the
	 * valid status list
	 * 
	 * @param list
	 *            The list to be manipulated
	 * @param obj
	 *            The object to check
	 * @param status
	 *            The status list to validate the status from
	 */
	protected void addToListConditionallyByStatus(List<T> list, T obj, List<Integer> status) {
		if(isThisClassSafeForStatusFiltering() && status != null && status.size() > 0 ) {
			if(status.contains(((StatusAware)obj).getStatus())) 
				if(!list.contains(obj)) list.add(obj);
		} else {
			if(!list.contains(obj)) list.add(obj);
		}
	}

	/**
	 * Counts how many instances matches the user criteria. <br>
	 * This is only used by js datatables query. <br>
	 * Warning: As the same with #getAll(), this will produce a lot of overhead
	 * to the AppEngine datastore, who is not so friendly with 'count'
	 * operations... so use it at your own risk!
	 * 
	 * @param userInfo
	 *            Logged user information. This is used in selecting
	 *            authorization filters (which data the user can see and which
	 *            data can't), and location filters (as in the ViewLocation
	 *            object)
	 */
	@Override
	public long count(UserInfo userInfo) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Query query = pm.newQuery(clazz);
			Map<String, Object> parameters = CollectionFactory.createMap();
			CustomDatatableFilter customFilter = buildCustomFilter(userInfo);
			if( customFilter != null ) {
				customFilter.delegateFilter(query, parameters);
			}
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

	/**
	 * Counts how many instances matches the user criteria. <br>
	 * Warning: As the same with #getAll(), this will produce a lot of overhead
	 * to the AppEngine datastore, who is not so friendly with 'count'
	 * operations... so use it at your own risk!
	 * 
	 * @param status
	 *            A status list to filter the query
	 */
	@Override
	public long count(List<Integer> status) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			if( isThisClassSafeForStatusFiltering() && status != null && status.size() > 0 ) {
				filters.add(toListFilterCriteria("status", status, false));
			}

			// Setting query parameters
			query.setFilter(toWellParametrizedFilter(filters));
			query.setResult("count(this)");

			Long results = Long.parseLong(query.execute().toString());
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

	/**
	 * Counts how many instances matches the user criteria. <br>
	 * Warning: As the same with #getAll(), this will produce a lot of overhead
	 * to the AppEngine datastore, who is not so friendly with 'count'
	 * operations... so use it at your own risk!
	 */
	@Override
	public long count() throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try{
			Query query = pm.newQuery(clazz);
			query.setResult("count(this)");
			Long results = Long.parseLong(query.execute().toString());
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

	/**
	 * Builds a well formed parameter list ready to be used in
	 * qeury.declareParameters(), based in a List of string property elements.
	 * 
	 * @param parms
	 *            The List of String properties to transform
	 * @returns A well formed parameter list
	 */
	@Override
	public String toParameterList(List<String> parms) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		Iterator<String> i = parms.iterator();
		while(i.hasNext()) {
			if( !first ) {
				sb.append(", ");
			} else {
				first = false;
			}
			sb.append(i.next());
		}
		return sb.toString();
	}

	/**
	 * Transforms a list into a concatenated OR select criteria. <br>
	 * Useful for selecting over lists as status or idLists.
	 * 
	 * @param key
	 *            The property key to filter
	 * @param values
	 *            A list of values to filter
	 * @param needColons
	 *            Set it to true if the kind of value your are filtering
	 *            requires a colon, for example as it will be needed with a
	 *            String list of values. Set it to false in it's not needed, for
	 *            example in a list of Integer values
	 * @return a Query filter part ready to be used in a query.setFilter()
	 *         operation, or to be aggregated with other criteria
	 */
	public String toListFilterCriteria(String key, List<?> values, boolean needColons) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;

		sb.append("(");
		for( Object value : values ) {
			if( !first ) {
				sb.append(" || ");
			} else {
				first = false;
			}

			sb.append(key).append(" == ");
			if( needColons ) sb.append("'");
			sb.append(value.toString());
			if( needColons ) sb.append("'");
		}
		sb.append(")");

		return sb.toString();
	}

	/**
	 * Builds a well formed filter ready to be used in
	 * qeury.setFilter(), based in a Map of properties and values to filter.
	 * 
	 * @param map
	 *            A map with property:value form
	 * @returns A well formed filter
	 */
	@Override
	public String toWellParametrizedFilter(Map<String, Object> map ) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		Iterator<String> i = map.keySet().iterator();
		while(i.hasNext()) {
			if( !first ) {
				sb.append(" && ");
			} else {
				first = false;
			}

			String param = i.next();
			String paramName = param.substring(0, param.length() - 5); 
			sb.append(paramName).append(" == ").append(param);
		}
		return sb.toString();
	}

	/**
	 * Builds a well formed filter ready to be used in
	 * qeury.setFilter(), based in a List of properties and values to filter.
	 * 
	 * @param filters
	 *            A List with all the well formed query conditions
	 * @returns A well formed filter
	 */
	@Override
	public String toWellParametrizedFilter(List<String> filters) {
		if( filters == null || filters.size() == 0 ) return null;

		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for(String filter : filters ) {
			if( !first ) {
				sb.append(" && ");
			} else {
				first = false;
			}

			sb.append(filter);
		}

		return sb.toString();
	}

	/**
	 * Get a list of instances of this entity according to a selected range.<br>
	 * This is mostly preferred against {@link #getAll()}
	 * 
	 * @param range
	 *            The range object that bounds the query limits
	 */
	@Override
	public List<T> getUsingRange(Range range) throws ASException {
		return getUsingRange(range, true);
	}

	/**
	 * Get a list of instances of this entity according to a selected range.<br>
	 * This is mostly preferred against {@link #getAll()}
	 * 
	 * @param range
	 *            The range object that bounds the query limits
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public List<T> getUsingRange(Range range, boolean detachable) throws ASException {
		return getUsingRange(null, range, true);
	}

	/**
	 * Get a list of instances of this entity according to a selected range.<br>
	 * This is mostly preferred against {@link #getAll()}
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param range
	 *            The range object that bounds the query limits
	 */
	@Override
	public List<T> getUsingRange(PersistenceProvider pp, Range range) throws ASException {
		return getUsingRange(pp, range, true);
	}

	/**
	 * Get a list of instances of this entity according to a selected range.<br>
	 * This is mostly preferred against {@link #getAll()}
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param range
	 *            The range object that bounds the query limits
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public List<T> getUsingRange(PersistenceProvider pp, Range range, boolean detachable) throws ASException {
		return getUsingStatusAndRange(pp, null, range, null, null, detachable);
	}

	/**
	 * Get a list of instances of this entity using a list of identifiers as key
	 * 
	 * @param idList
	 *            The Identifiers list that will be used to select the instances
	 *            that will be returned
	 */
	@Override
	public List<T> getUsingIdList(List<String> idList) throws ASException {
		return getUsingIdList(idList, true);
	}

	/**
	 * Get a list of instances of this entity using a list of identifiers as key
	 * 
	 * @param idList
	 *            The Identifiers list that will be used to select the instances
	 *            that will be returned
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public List<T> getUsingIdList(List<String> idList, boolean detachable)
			throws ASException {
		return getUsingIdList(null, idList, detachable);
	}

	/**
	 * Get a list of instances of this entity using a list of identifiers as key
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param idList
	 *            The Identifiers list that will be used to select the instances
	 *            that will be returned
	 */
	@Override
	public List<T> getUsingIdList(PersistenceProvider pp, List<String> idList)
			throws ASException {
		return getUsingIdList(pp, idList, true);
	}

	/**
	 * Get a list of instances of this entity using a list of identifiers as key
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param idList
	 *            The Identifiers list that will be used to select the instances
	 *            that will be returned
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public List<T> getUsingIdList(PersistenceProvider pp, List<String> idList,
			boolean detachable) throws ASException {
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get()
				.getPersistenceManager();
		List<T> ret = CollectionFactory.createList();

		try {
			// If we got here with no elements... there are no elements to get... so bye!
			if( idList == null || idList.size() == 0 ) return ret;
			for( String id : idList ) {
				T obj = null;
				try {
					obj = get(id, detachable);
				} catch(ASException e1) {
					if( ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE != e1.getErrorCode()) {
						throw e1;
					}
				}
				if( obj != null ) ret.add(obj);
			}

		} catch (Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally {
			pm.close();
		}

		return ret;
	}

	/**
	 * Builds a cache key for an entity, based in its class and Identifier
	 * 
	 * @param clazz
	 *            The class that the object belongs to
	 * @param identifier
	 *            The object Identifier
	 * @return A String that will be used as a cache key for this object
	 *         representation
	 */
	public String getCacheKey(Class<?> clazz, String identifier) {
		return clazz.getName() + "(" + identifier + ")";
	}

	/**
	 * Builds a custom filter callback for a list of instances of this entity. <br>
	 * This method is mainly used to filter data for js datatables according the
	 * users permissions and physical location.
	 * 
	 * @param userInfo
	 *            Logged user information. This is used in selecting
	 *            authorization filters (which data the user can see and which
	 *            data can't), and location filters (as in the ViewLocation
	 *            object)
	 */
	@Override
	public CustomDatatableFilter buildCustomFilter(UserInfo userInfo) {
		if(userInfo != null && userInfo.getRole() == UserSecurity.Role.ADMIN) 
			return null;

		if( userInfo != null && (userInfo.getRole() == UserSecurity.Role.COUNTRY_ADMIN || userInfo.getRole() == UserSecurity.Role.READ_ONLY)) { 
			final List<String> availableCountries = userInfo.getAvailableCountries();
			try {
				final T placebo = clazz.newInstance();
				return new CustomDatatableFilter() {
					@Override
					public void delegateFilter(Query query, Map<String, Object> parameters) {
						if( placebo instanceof ViewLocationAware ) {
							if( placebo.getClass().getName().endsWith("Shopping")) {
								parameters.put("paramVL", availableCountries);
								query.declareParameters("java.util.List paramVL");
								query.setFilter("paramVL.contains(address.country)");
							} else if( placebo.getClass().getName().endsWith("Store")) {
								parameters.put("paramVL", availableCountries);
								query.declareParameters("java.util.List paramVL");
								query.setFilter("paramVL.contains(viewLocation.country)");
							} else {
								parameters.put("paramVL", availableCountries);
								query.declareParameters("java.util.List paramVL");
								query.setFilter("paramVL.contains(country)");
							}
						}
					}
				};
			} catch (InstantiationException | IllegalAccessException e) {
			}
		}

		return null;
	}

	/**
	 * Validates if an instance is safe and within limits of authorization for a
	 * specific user.<br>
	 * Basically, this method is the police that says if you are allowed to see
	 * an instance of not
	 * 
	 * @param obj
	 *            The object to inspect
	 * @param userInfo
	 *            Logged user information. This is used in selecting
	 *            authorization filters (which data the user can see and which
	 *            data cant), and location filters (as in the ViewLocation
	 *            object)
	 */
	@Override
	public boolean safeAndInLimits(T obj, UserInfo userInfo) {

		if(obj instanceof ViewLocationAware ) {
			List<String> availableCountries = CollectionFactory.createList();
			if( userInfo != null && userInfo.getRole() == UserSecurity.Role.ADMIN) 
				return true;
			if( userInfo != null )
				availableCountries = userInfo.getAvailableCountries();
			if( availableCountries == null ) return false;

			for( String s : availableCountries ) {
				ViewLocation vl = new ViewLocation();
				vl.setCountry(s);
				if( ((ViewLocationAware)obj).isAvailableFor(vl)) {
					return true;
				}
			}
		} else {
			return true;
		}

		return false;
	}

	/**
	 * Returns if the working class of this DAO is safe to be filtered by
	 * status. Generally, it means that the class has inheritance from
	 * StatusAware.
	 * 
	 * @return True if this class is safe to filter by status, false if not
	 */
	public boolean isThisClassSafeForStatusFiltering() {
		return ( getObjectExample() instanceof StatusAware); 
	}

	/**
	 * Assigns an example of an object instance of this reference class to test
	 * for 'instanceof' methods, such as in
	 * {@link #isThisClassSafeForStatusFiltering()}
	 * 
	 * @return An example of this kind of object
	 */
	public T getObjectExample() {
		if( objectExample == null ) {
			try {
				objectExample = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				log.log(Level.WARNING, e.getMessage(), e);
			}
		}
		return objectExample;
	}

	/**
	 * Checks if an objects exists in the current datastore
	 * 
	 * @param pm
	 *            The persistence manager to work with during the check
	 *            transaction. If this parameter is null, then the lookup is
	 *            just made in memcache
	 * @param clazz
	 *            The object class
	 * @param key
	 *            The object key to look for
	 * @return True if the object already exists in the datastore, false if not
	 * @throws Exception
	 */
	public Boolean doesEntityExist(PersistenceManager pm, Class<?> clazz, Key key) throws Exception {
		Boolean exists = false;
		try {
			Object obj = null;

			if( pm == null ) {
				if(cacheHelper != null) obj = cacheHelper.get(getCacheKey(clazz, key.getName()));
			} else {
				obj = pm.getObjectById(clazz,key);
			}

			if (obj != null) {
				exists = true;
			}

		}catch(JDOObjectNotFoundException e){
			// its normal; looks for a record, but cant find it
			// someone needs a little help with his english, don't ya?
			return false;
		}catch(Exception e){
			log.log(Level.SEVERE, "exception catched", e);
			throw e;
		}
		return exists;
	}

	/**
	 * Artifact to make sure we are avoiding Contention Problems
	 * 
	 * @param pm
	 *            The PersistenceManager to use
	 * @param obj
	 *            The object to be stored
	 */
	protected void enqueuePersistence(PersistenceManager pm, T obj) {
		pm.makePersistent(obj);
	}

	@Override
	public List<T> indexFallback(String id) {
		try {
			List<T> res = getUsingIdList(Arrays.asList(id.split(",")));
			for(ModelKey obj : res) indexHelper.indexObject(obj);
			return res;
		} catch (ASException e) {
			log.log(Level.WARNING, "Cant find non-indexed object\n" +e.getMessage(), e);
			return CollectionFactory.createList();
		}
	}

}
