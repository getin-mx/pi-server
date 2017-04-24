package mobi.allshoppings.util.test;

import java.text.DecimalFormat;
import java.util.Iterator;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.tools.PersistentCache;

public class PersistentCacheTest extends TestCase {

	@Test
	public void test0001() {
		try {
			DecimalFormat df = new DecimalFormat("000000000000000");
			PersistentCache<String, String> cache = new PersistentCache<String,String>(String.class);
			
			for( int i = 0; i < 100000; i++ ) {
				cache.put(df.format(i), "Value " + df.format(i));
			}
			
			for( int i = 0; i < 10000; i++ ) {
				cache.put(df.format(i), "Value " + df.format(i));
			}
			
			Iterator<String> i = cache.iterator();
			while(i.hasNext()) {
				System.out.println(i.next());
			}
			
			cache.dispose();
			
		} catch(Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
