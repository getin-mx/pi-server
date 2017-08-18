package mobi.allshoppings.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.spi.GenericDAOJDO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;
import mobi.allshoppings.tx.PersistenceProvider;
import mobi.allshoppings.tx.TransactionType;
import mobi.allshoppings.tx.spi.PersistenceProviderJDOImpl;

public class PersistentCacheJDOImpl <V extends ModelKey> {

	private static final Logger log = Logger.getLogger(PersistentCacheJDOImpl.class.getName());
	
	private static final int DEFAULT_LIMIT = 1000;
	private static final int DEFAULT_PAGE_SIZE = 100;

	private KeyHelper keyHelper = new KeyHelperGaeImpl();
	private Map<String,V> map;
	private Map<String,Long> lastUsed;
	private int inMemLimit;
	private int pageSize;
	private Map<String, Integer> keys;
	private GenericDAOJDO<V> dao;
	private int lastPage;
	private int hits;
	private int misses;
	private int stores;
	private int loads;
	private Class<V> valueClazz;
	
	public PersistentCacheJDOImpl(Class<V> valueClazz) {
		this(valueClazz, DEFAULT_LIMIT, DEFAULT_PAGE_SIZE, null);
	}

	public PersistentCacheJDOImpl(Class<V> valueClazz, int inMemLimit, int pageSize, String tempDir) {
		super();
		
		this.inMemLimit = inMemLimit;
		this.pageSize = pageSize;
		this.hits = 0;
		this.misses = 0;
		this.stores = 0;
		this.loads = 0;
		this.dao = new GenericDAOJDO<V>(valueClazz);

		map = CollectionFactory.createMap();
		lastUsed = CollectionFactory.createMap();
		keys = CollectionFactory.createMap();
		this.valueClazz = valueClazz;				
		
	}

	public void clear() {
		map.clear();
		lastUsed.clear();
		keys.clear();
		lastPage = 0;
		hits = 0;
		misses = 0;
		stores = 0;
		loads = 0;
	}
	
	public int size() {
		return keys.size();
	}
	
	public int inMemSize() {
		return map.size();
	}
	
	public boolean containsKey(String key) {
		return keys.containsKey(key);
	}

	public void remove(String key) throws NoSuchAlgorithmException, IOException {
		V obj = get(key);
		try {
			dao.delete(obj);
			keys.remove(key);
			lastUsed.remove(key);
			map.remove(key);
		} catch( ASException e ) {
			throw new IOException(e);
		}
	}
	
	public void put(String key, V value) throws NoSuchAlgorithmException, IOException {
		map.put(key, value);
		lastUsed.put(key, System.currentTimeMillis());
		keys.put(key, 0);

		if(map.size() > inMemLimit) {
			storePage();
			storePage();
		}
	}
	
	public V get(String key) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
		if( map.containsKey(key)) {
			hits++;
			return map.get(key);
		} else {
			if( keys.containsKey(key)) {
				int pageNumber = keys.get(key);
				if( pageNumber > 0 ) {
					misses++;
					loadFromPage(pageNumber, key);
					return map.get(key);
				} else {
					hits++;
					return null;
				}
			} else {
				hits++;
				return null;
			}
		}
	}

	public Iterator<V> iterator() {
		return (Iterator<V>) new PersistentCacheIterator();
	}
	
	public void storePage() throws IOException, NoSuchAlgorithmException {

		long start = System.currentTimeMillis();
		List<Long> uses = CollectionFactory.createList();
		uses.addAll(lastUsed.values());
		
		java.util.Collections.sort(uses);

		int index = pageSize;
		if( uses.size() -1 < pageSize )
			index = uses.size() -1;

		lastPage++;

		long limit = uses.get(index);

		List<String> toRemove = CollectionFactory.createList();
		Iterator<String> inMemLimits = lastUsed.keySet().iterator();
		PersistenceProvider pp = new PersistenceProviderJDOImpl(TransactionType.SIMPLE);
		PersistenceManager pm = pp.get();
		pm.currentTransaction().begin();

		while(inMemLimits.hasNext()) {
			String key = inMemLimits.next();
			if( lastUsed.get(key) <= limit ) {
				if( map.containsKey(key)) {
					V value = map.get(key);
					try {
						Key k = keyHelper.<Key>obtainKey(valueClazz, key);
						V bs = null;
						try {
							bs = pm.getObjectById(valueClazz, k);
						} catch( JDOObjectNotFoundException e1) {}
						if( bs != null ) {
							cloneObject(value, bs);
							pm.makePersistent(bs);
//							value = pm.detachCopy(bs);
						} else {
							pm.makePersistent(value);
//							value = pm.detachCopy(value);
						}
						keys.put(key, lastPage);
						toRemove.add(key);
					} catch( Exception e ) {
						log.log(Level.WARNING, e.getMessage(), e);
					}
				}
			}
		}

		pm.currentTransaction().commit();
		pm.close();

		for(String key : toRemove ) {
			map.remove(key);
			lastUsed.remove(key);
		}

		long end = System.currentTimeMillis();
		log.log(Level.FINE, "Page " + lastPage + " stored in " + (end-start) + "ms with " + map.size() + " records in mem");
		
		stores++;
	}
	
	private void cloneObject(V from, V to ) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> properties = PropertyUtils.describe(from);
			Iterator<String> it = properties.keySet().iterator();
			while( it.hasNext()) {
				String property = it.next();
				if(!property.startsWith("jdo")) {
					Object fieldValue = properties.get(properties);
					BeanUtils.setProperty(to, property, fieldValue);
				}
			}
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public void loadPage(int pageNumber) throws NoSuchAlgorithmException, IOException {

		long start = System.currentTimeMillis();
		if( inMemSize() + pageSize >= inMemLimit ) 
			storePage();

		Iterator<String> i = keys.keySet().iterator();
		while(i.hasNext()) {
			String key = i.next();
			int page = keys.get(key);
			if( page == pageNumber ) {
				try {
					map.put(key, dao.get(key,true));
					keys.put(key, 0);
					lastUsed.put(key, System.currentTimeMillis());
				} catch( ASException e ) {
					log.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
		
		long end = System.currentTimeMillis();
		log.log(Level.FINE, "Page " + pageNumber + " loaded in " + (end-start) + "ms with " + map.size() + " records in mem");
		loads++;
	}

	public void loadFromPage(int pageNumber, String key) throws NoSuchAlgorithmException, IOException {

		long start = System.currentTimeMillis();
		if( inMemSize() + pageSize >= inMemLimit ) 
			storePage();

		try {
			map.put(key, dao.get(key, true));
			keys.put(key, 0);
			lastUsed.put(key, System.currentTimeMillis());
		} catch( ASException e ) {
			throw new FileNotFoundException(key);
		}

		long end = System.currentTimeMillis();
		log.log(Level.FINE, "Page " + pageNumber + " loaded in " + (end-start) + "ms with " + map.size() + " records in mem");

		loads++;
	}

	public void dispose() {
		PersistenceProvider pp = new PersistenceProviderJDOImpl(TransactionType.SIMPLE);
		PersistenceManager pm = pp.get();
		pm.currentTransaction().begin();

		int count = 0;
		Iterator<String> i = map.keySet().iterator();
		while(i.hasNext()) {
			String key = i.next();
			V obj = map.get(key);
			try {
				Key k = keyHelper.<Key>obtainKey(valueClazz, key);
				V bs = null;
				try {
					bs = pm.getObjectById(valueClazz, k);
				} catch( JDOObjectNotFoundException e1) {}
				if( bs != null ) {
					cloneObject(obj, bs);
					pm.makePersistent(bs);
//					obj = pm.detachCopy(bs);
				} else {
					pm.makePersistent(obj);
//					obj = pm.detachCopy(obj);
				}
				if( count % pageSize == 0 ) {
					log.log(Level.WARNING, "Flushing " + count + " of " + map.size() + "...");
					pm.flush();
				}
				count++;
			} catch( Exception e ) {
				if(pp == null && pm.currentTransaction().isActive()){
					pm.currentTransaction().rollback();
				}
				log.log(Level.WARNING, e.getMessage(), e);
			}
		}

		((PersistenceManager)pp.get()).currentTransaction().commit();
		((PersistenceManager)pp.get()).close();

		clear();
		System.gc();

	}

	/**
	 * @return the hits
	 */
	public int getHits() {
		return hits;
	}

	/**
	 * @return the misses
	 */
	public int getMisses() {
		return misses;
	}

	
	/**
	 * @return the stores
	 */
	public int getStores() {
		return stores;
	}

	/**
	 * @return the loads
	 */
	public int getLoads() {
		return loads;
	}

	public class PersistentCacheIterator implements Iterator<V> {
		
		private Iterator<String> iterator;
		private int currentPage = 0;
		
		public PersistentCacheIterator() {
			super();
			Map<Integer, List<String>> pages = CollectionFactory.createMap();
			for( String key : keys.keySet()) {
				Integer subKey = keys.get(key);
				List<String> subValue = pages.get(subKey);
				if( subValue == null )
					subValue = CollectionFactory.createList();
				subValue.add(key);
				pages.put(subKey, subValue);
			}
			
			List<Integer> l = CollectionFactory.createList();
			l.addAll(pages.keySet());
			java.util.Collections.sort(l);
			
			List<String> l2 = CollectionFactory.createList();
			for( Integer i : l )
				l2.addAll(pages.get(i));
			
			iterator = l2.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public V next() {
			String key = iterator.next();
			if( keys.get(key) != currentPage ) {
				currentPage = keys.get(key);
				try {
					loadPage(currentPage);
				} catch( Exception e ) {}
			}
			try {
				return get(key);
			} catch (NoSuchAlgorithmException | IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public void remove() {
			// Not Implemented
		}
		
	}
}
