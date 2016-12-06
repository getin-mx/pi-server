package mobi.allshoppings.tools;

import java.util.Set;

public class Collections {
	
	public static <T> Set<T> getSetFromAnArray(T[] array) {
		Set<T> ret = CollectionFactory.createSet();
		for (int idx = 0; idx < array.length; idx++) {
			ret.add(array[idx]);
		}
		return ret;
	}

}
