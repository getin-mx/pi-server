package mobi.allshoppings.tools;

import java.util.Collection;
import java.util.Iterator;

public class CollectionUtils {

	public static boolean isEmpty(Collection<?> collection) {
		if( collection == null || collection.size() == 0 ) return true;
		return false;
	}

	public static <T> T firstElement(Collection<T> collection) {
		if( isEmpty(collection)) return null;
		Iterator<T> i = collection.iterator();
		T obj = i.next();
		if( obj == null || obj.toString().equals("null")) return null;
		return obj;
	}

	public static boolean contains(Collection<?> collection, Object obj ) {
		if (isEmpty(collection)) return false;
		return collection.contains(obj);
	}
}
