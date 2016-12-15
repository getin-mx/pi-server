package mobi.allshoppings.cinepolis.adapters;

import java.text.SimpleDateFormat;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.services.CinepolisCheckinUpdaterService;

public class CinepolisCheckinUpdaterServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			CinepolisCheckinUpdaterService service = new CinepolisCheckinUpdaterService();
			service.updateCheckins("/tmp/dump", sdf.parse("2015-05-01"), sdf.parse("2015-06-01"));

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
