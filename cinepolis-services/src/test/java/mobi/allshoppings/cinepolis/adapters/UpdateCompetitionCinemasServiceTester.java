package mobi.allshoppings.cinepolis.adapters;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.services.UpdateCompetitionCinemasService;

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
