package mobi.allshoppings.model.tools;

public interface CacheHelper {

	Object get(String key);
	void put(String key, Object value);
	void remove(String key);
	boolean contains(String key);
	
}
