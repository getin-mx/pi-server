package com.inodes.datanucleus.cache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Newer versions of datanucleus deprecated and removed the classes SoftValueMap &amp; ReferenceValueMap.
 * So in order to use it, a custom implementation based on the original is included here.
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 1.0, december 2017
 * @since Mark III, december 2017
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ValueMap implements Map, Cloneable {

	/**
	 * Wrapped map for the cache.
	 * @since Value Map 1.0, december 2017
	 */
	private HashMap map = new HashMap();
	private ReferenceQueue reaped = new ReferenceQueue();
	
	@Override
	public Object clone() {
		reap();
		ValueMap vm = null;
		try {
			vm = (ValueMap) super.clone();
		} catch(CloneNotSupportedException e) {}
		vm.map = (HashMap) map.clone();
		vm.map.clear();
		vm.reaped = new ReferenceQueue();
		vm.putAll(entrySet());
		return vm;
	}
	
	@Override
	public int size() {
		reap();
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		reap();
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		reap();
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		reap();
		if(value == null) return false;
		for(Object data : map.values()) {
			Reference ref = (Reference) data;
			return value.equals(ref);
		}
		return false;
	}

	@Override
	public Object get(Object key) {
		reap();
		Reference ref = (Reference) map.get(key);
		return ref == null ? null : ref.get();
	}

	@Override
	public Object put(Object key, Object value) {
		reap();
		Reference oldRef = (Reference) map.put(key, newValueReference(key, value, reaped));
		return oldRef != null ? oldRef.get() : null;
	}

	@Override
	public Object remove(Object key) {
		reap();
		return map.remove(key);
	}

	@Override
	public void putAll(Map m) {
		putAll(m.entrySet());
	}

	@Override
	public void clear() {
		reap();
		map.clear();
	}

	@Override
	public Set keySet() {
		reap();
		return map.keySet();
	}

	@Override
	public Collection values() {
		Collection c = map.values();
		ArrayList l = new ArrayList(c.size());
		for(Object data: c) {
			Reference ref = (Reference) data;
			Object obj = ref.get();
			if(obj != null) l.add(obj);
		}
		return Collections.unmodifiableList(l);
	}
	
		@Override
	public Set entrySet() {
		reap();
		Set s = map.entrySet();
		HashMap m = new HashMap(s.size());
		for(Object obj : map.entrySet()) {
			Map.Entry entry = (Map.Entry) obj;
			Reference ref = (Reference) entry.getValue();
			Object other = ref.get();
			if(other != null) m.put(entry.getKey(), obj);
		}
		return Collections.unmodifiableSet(m.entrySet());
	}
		
	@Override
	public int hashCode() {
		reap();
		return map.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		reap();
		return map.equals(o);
	}
	
	public void reap() {
		ValueReference ref;
		while((ref = (ValueReference) reaped.poll()) != null) {
			map.remove(ref.getKey());
		}
	}
	
	/**
	 * Returns a new reference to the given object to be inserted in this map.
	 * @param key - The key to insert.
	 * @param value - The value to insert.
	 * @param queue - The queue with which to register the new reference.
	 * @return ValueReference - the new reference.
	 * @since Mark III, december 2017
	 */
	protected ValueReference newValueReference(Object key, Object value, ReferenceQueue queue) {
		return new SoftValueReference(key, value, queue);
	}//newValueReference

	/**
	 * Adds all contents of a set.
	 * @param entrySet - The set to add into the cache.
	 * @since Mark III, december 2017
	 */
	private void putAll(Set entrySet) {
		for(Object data : entrySet) {
			Map.Entry entry = (Map.Entry) data;
			put(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * References must implement this interface to provide the corresponding map key.
	 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
	 * @version 1.0, december 2017
	 * @since Mark III, dcember 2017
	 */
	public interface ValueReference {
		
		Object getKey();
		
	}//Value Reference
	
	/**
	 * A soft value reference.
	 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
	 * @version 1.0, december 2017
	 * @since Mark III, december 2017
	 */
	private static class SoftValueReference extends SoftReference implements ValueReference {
		
		private final Object KEY;
		
		/**
		 * Builds the soft value reference.
		 * @param key - The key.
		 * @param value - The payload-
		 * @param q - The queue with which to register the new reference.
		 */
		public SoftValueReference(Object key, Object value, ReferenceQueue q) {
			super(value, q);
			KEY = key;
		}//constructor
		
		@Override
		public Object getKey() {
			return KEY;
		}//getKey
		
	}//Soft Value Reference
	
}//value map class
