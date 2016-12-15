package mobi.allshoppings.cinepolis.adapters;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.services.UpdateCinepolisCinemasService;

public class UpdateCinemasServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {

			UpdateCinepolisCinemasService service = new UpdateCinepolisCinemasService();
			service.doUpdate();
			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
