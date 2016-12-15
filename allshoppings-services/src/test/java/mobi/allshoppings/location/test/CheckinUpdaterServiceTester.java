package mobi.allshoppings.location.test;

import java.text.SimpleDateFormat;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.location.CheckinUpdaterService;

public class CheckinUpdaterServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			CheckinUpdaterService service = new CheckinUpdaterService();
			service.updateCheckins("/tmp/dump", sdf.parse("2015-05-01"), sdf.parse("2015-06-01"));

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
