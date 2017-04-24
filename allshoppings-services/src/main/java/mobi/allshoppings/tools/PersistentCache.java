package mobi.allshoppings.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class PersistentCache <K extends Object, V extends Object> {
	
	private static final int DEFAULT_LIMIT = 1000;
	private static final int DEFAULT_BATCH_SIZE = 100;
	
	private Map<K,V> map;
	private Map<K,Long> lastUsed;
	private String tempDir;
	private int inMemLimit;
	private int batchSize;
	private MessageDigest md5;
	private Gson gson;
	private Class<?> clazz;
	private HashSet<K> keys;
	
	public PersistentCache(Class<?> clazz) {
		this(clazz, DEFAULT_LIMIT, DEFAULT_BATCH_SIZE, null);
	}

	public PersistentCache(Class<?> clazz, int inMemLimit, int batchSize, String tempDir) {
		super();
		
		this.clazz = clazz;
		this.inMemLimit = inMemLimit;
		this.batchSize = batchSize;
				
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
		keys = new HashSet<K>();
	}
	
	public void put(K key, V value) throws NoSuchAlgorithmException, IOException {
		map.put(key, value);
		lastUsed.put(key, System.currentTimeMillis());
		keys.add(key);
		
		if(map.size() > inMemLimit)
			disposeBatch();
	}
	
	public V get(K key) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
		if( map.containsKey(key)) {
			return map.get(key);
		} else {
			
			String md5Key = md5Digest(key.toString());
			File f = new File(tempDir + md5Key);
			if( f.exists() && f.canRead()) {
				try(BufferedReader br = new BufferedReader(new FileReader(f))) {
				    for(String line; (line = br.readLine()) != null ;) {
				    	@SuppressWarnings("unchecked")
						V element = (V) gson.fromJson(line, clazz);
				    	map.put(key, element);
				    	lastUsed.put(key, System.currentTimeMillis());
						br.close();

				    	if(map.size() > inMemLimit)
							disposeBatch();
				    	
				    	return(element);
				    }
				}
			} else {
				throw new FileNotFoundException(f.getAbsolutePath());
			}
			
			
			return null;
		}
	}

	public Iterator<V> iterator() {
		return (Iterator<V>) new PersistentCacheIterator();
	}
	
	public void disposeBatch() throws IOException, NoSuchAlgorithmException {
		List<Long> uses = CollectionFactory.createList();
		Iterator<K> inMemKeys = map.keySet().iterator();
		while(inMemKeys.hasNext()) {
			K key = inMemKeys.next();
			uses.add(lastUsed.get(key));
		}
		
		java.util.Collections.sort(uses);
		
		int index = batchSize;
		if( uses.size() -1 < batchSize )
			index = uses.size() -1;
		
		long limit = uses.get(index);
		
		List<K> toRemove = CollectionFactory.createList();
		Iterator<K> inMemLimits = lastUsed.keySet().iterator();
		while(inMemLimits.hasNext()) {
			K key = inMemLimits.next();
			if( lastUsed.get(key) <= limit ) {
				if( map.containsKey(key)) {
					V value = map.get(key);
					String md5Key = md5Digest(key.toString());
					String jsonValue = serialize(value);

					File f = new File(tempDir + md5Key);
					if( f.exists() ) f.delete();

					FileOutputStream fos = new FileOutputStream(f);
					fos.write(jsonValue.getBytes());
					fos.flush();
					fos.close();
					
					toRemove.add(key);
				}
			}
		}
		
		for(K key : toRemove ) {
			map.remove(key);
			lastUsed.remove(key);
		}
		
	}

	public void dispose() {
		deleteFolder(new File(tempDir));
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
			gson = new Gson();
		
		return gson.toJson(obj);
	}
	
	private String md5Digest(String source) throws NoSuchAlgorithmException {

		if( md5 == null )
			md5 = MessageDigest.getInstance("MD5");

		md5.reset();
		md5.update(source.getBytes());
		byte[] res = md5.digest();

		BigInteger bigInt = new BigInteger(1,res);
		String hashtext = bigInt.toString(16);
		// Now we need to zero pad it if you actually want the full 32 chars.
		while(hashtext.length() < 32 ){
			hashtext = "0"+hashtext;
		}

		return hashtext;

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
	
	public class PersistentCacheIterator implements Iterator<V> {
		
		private Iterator<K> iterator;
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public PersistentCacheIterator() {
			super();
			List<K> l = CollectionFactory.createList();
			l.addAll(keys);
			java.util.Collections.sort((List<Comparable>)l);
			iterator = l.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public V next() {
			K key = iterator.next();
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
