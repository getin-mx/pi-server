package mobi.allshoppings.cinepolis.adapters;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.services.UpdateCompetitionCinemasService;

import org.junit.Test;

public class UpdateCompetitionCinemasServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {

			UpdateCompetitionCinemasService service = new UpdateCompetitionCinemasService();
			service.doUpdate();
			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
