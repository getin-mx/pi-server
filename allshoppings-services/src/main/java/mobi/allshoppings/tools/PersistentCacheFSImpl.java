package mobi.allshoppings.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

public class PersistentCacheFSImpl <V extends Object> {

	private static final Logger log = Logger.getLogger(PersistentCacheFSImpl.class.getName());
	
	private static final int DEFAULT_LIMIT = 1000;
	private static final int DEFAULT_PAGE_SIZE = 100;
	
	private Map<String,V> map;
	private Map<String,Long> lastUsed;
	private String tempDir;
	private int inMemLimit;
	private int pageSize;
	private Gson gson;
	private Class<?> valueClazz;
	private Map<String, Integer> keys;
	private int lastPage;
	private int hits;
	private int misses;
	private int stores;
	private int loads;
		
	public PersistentCacheFSImpl(Class<?> valueClazz) {
		this(valueClazz, DEFAULT_LIMIT, DEFAULT_PAGE_SIZE, null);
	}

	public PersistentCacheFSImpl(Class<?> valueClazz, int inMemLimit, int pageSize, String tempDir) {
		super();
		
		this.valueClazz = valueClazz;
		this.inMemLimit = inMemLimit;
		this.pageSize = pageSize;
		this.hits = 0;
		this.misses = 0;
		this.stores = 0;
		this.loads = 0;
				
		this.tempDir = tempDir;
		if( this.tempDir == null ) 
			this.tempDir = "/tmp/";

		StringBuffer sb = new StringBuffer(this.tempDir);
		if(!sb.toString().endsWith(File.separator))
			sb.append(File.separator);
		sb.append("PersistentCache").append(File.separator);
		sb.append(getProcessId("jvm")).append(File.separator);
		sb.append(Thread.currentThread().getId()).append(File.separator);
		this.tempDir = sb.toString();
		
		File f = new File(this.tempDir);
		if(!f.exists())
			f.mkdirs();
		
		map = CollectionFactory.createMap();
		lastUsed = CollectionFactory.createMap();
		keys = CollectionFactory.createMap();
		
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
		StringBuffer pageSB = new StringBuffer();
		Iterator<String> inMemLimits = lastUsed.keySet().iterator();
		while(inMemLimits.hasNext()) {
			String key = inMemLimits.next();
			if( lastUsed.get(key) <= limit ) {
				if( map.containsKey(key)) {
					V value = map.get(key);
					String jsonValue = serialize(value);
					pageSB.append(key).append(";;").append(jsonValue).append("\n");
					keys.put(key, lastPage);
					toRemove.add(key);
				}
			}
		}

		File f = new File(tempDir + lastPage);
		if( f.exists() ) f.delete();
		if(!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(pageSB.toString().getBytes());
		fos.flush();
		fos.close();

		for(String key : toRemove ) {
			map.remove(key);
			lastUsed.remove(key);
		}

		long end = System.currentTimeMillis();
		log.log(Level.FINE, "Page " + lastPage + " stored in " + (end-start) + "ms with " + map.size() + " records in mem");
		
		stores++;
	}
	
	public void loadPage(int pageNumber) throws NoSuchAlgorithmException, IOException {

		long start = System.currentTimeMillis();
		if( inMemSize() + pageSize >= inMemLimit ) 
			storePage();
		
		Map<String, String> page = CollectionFactory.createMap();
		File f = new File(tempDir + pageNumber);
		if( f.exists() && f.canRead()) {
			try(BufferedReader br = new BufferedReader(new FileReader(f))) {
			    for(String line; (line = br.readLine()) != null ;) {
			    	String[] parts = line.split(";;");
			    	if( parts.length > 1 ) {
			    		page.put(parts[0], parts[1]);
			    	}
			    }
			    br.close();
				f.delete();
			}
		} else {
			throw new FileNotFoundException(f.getAbsolutePath());
		}

		for(String key : keys.keySet() ) {
			if( keys.get(key) == pageNumber ) {
				String jsonObject = page.get(key);
				if( jsonObject != null ) {
					@SuppressWarnings("unchecked")
					V element = (V) gson.fromJson(jsonObject, valueClazz);
					map.put(key, element);
					keys.put(key, 0);
			    	lastUsed.put(key, System.currentTimeMillis());
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
		
		File f = new File(tempDir + pageNumber);
		if( f.exists() && f.canRead()) {
			try(BufferedReader br = new BufferedReader(new FileReader(f))) {
				for(String line; (line = br.readLine()) != null ;) {
					if( line.startsWith(key + ";;")) {
						String[] parts = line.split(";;");
						@SuppressWarnings("unchecked")
						V element = (V) gson.fromJson(parts[1], valueClazz);
						map.put(key, element);
						keys.put(key, 0);
						lastUsed.put(key, System.currentTimeMillis());
						break;
					}
				}
				br.close();
			}
		} else {
			throw new FileNotFoundException(f.getAbsolutePath());
		}
		
		long end = System.currentTimeMillis();
		log.log(Level.FINE, "Page " + pageNumber + " loaded in " + (end-start) + "ms with " + map.size() + " records in mem");

		loads++;
	}

	public void dispose() {
		deleteFolder(new File(tempDir));
		clear();
	}

	private void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}

	private String serialize(V obj) {
		if(gson == null)
			gson = GsonFactory.getInstance();
		
		return gson.toJson(obj);
	}
	
	private String getProcessId(final String fallback) {
	    // Note: may fail in some JVM implementations
	    // therefore fallback has to be provided

	    // something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
	    final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
	    final int index = jvmName.indexOf('@');

	    if (index < 1) {
	        // part before '@' empty (index = 0) / '@' not found (index = -1)
	        return fallback;
	    }

	    try {
	        return Long.toString(Long.parseLong(jvmName.substring(0, index)));
	    } catch (NumberFormatException e) {
	        // ignore
	    }
	    return fallback;
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
