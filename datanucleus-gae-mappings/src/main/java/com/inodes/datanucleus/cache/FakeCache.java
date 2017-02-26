package com.inodes.datanucleus.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.datanucleus.cache.Level1Cache;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.util.SoftValueMap;

public class FakeCache implements Level1Cache {

	@SuppressWarnings("unchecked")
	private Map<Object, ObjectProvider> softCache = new SoftValueMap();

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean containsKey(Object key) {
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public ObjectProvider get(Object key) {
		return null;
	}

	@Override
	public ObjectProvider put(Object key, ObjectProvider value) {
		return softCache.put(key,value);        
	}

	@Override
	public ObjectProvider remove(Object key) {
		return softCache.remove(key);       
	}

	@Override
	public void putAll(Map<? extends Object, ? extends ObjectProvider> m) {
		
	}

	@Override
	public void clear() {
		softCache.clear();
	}

	@Override
	public Set<Object> keySet() {
		softCache.clear();
		return softCache.keySet();
	}

	@Override
	public Collection<ObjectProvider> values() {
		softCache.clear();
		return softCache.values();
	}

	@Override
	public Set<java.util.Map.Entry<Object, ObjectProvider>> entrySet() {
		softCache.clear();
		return softCache.entrySet();
	}

}
