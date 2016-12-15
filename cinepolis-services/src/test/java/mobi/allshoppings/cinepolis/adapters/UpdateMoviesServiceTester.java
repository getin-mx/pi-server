package mobi.allshoppings.cinepolis.adapters;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.services.UpdateMoviesService;

public class UpdateMoviesServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {

			UpdateMoviesService service = new UpdateMoviesService();
			service.doUpdate();
			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
