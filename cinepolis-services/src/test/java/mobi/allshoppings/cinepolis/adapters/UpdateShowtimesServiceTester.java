package mobi.allshoppings.cinepolis.adapters;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.services.UpdateShowtimesService;

public class UpdateShowtimesServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {

			UpdateShowtimesService service = new UpdateShowtimesService();
			service.doUpdate();
			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
