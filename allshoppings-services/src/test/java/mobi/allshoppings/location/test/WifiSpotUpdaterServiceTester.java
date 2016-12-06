package mobi.allshoppings.location.test;

import java.text.SimpleDateFormat;

import junit.framework.TestCase;
import mobi.allshoppings.location.WifiSpotUpdaterService;

import org.junit.Test;

public class WifiSpotUpdaterServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH");
			
			WifiSpotUpdaterService service = new WifiSpotUpdaterService();
			service.updateWifiSpots("/tmp/dump", sdf.parse("2015-06-20:16"), sdf.parse("2015-09-01:00"));

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
