package mobi.allshoppings.location.test;

import java.text.SimpleDateFormat;
import java.util.Set;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.impl.GeoCodingHelperGMapsImpl;
import mobi.allshoppings.location.UserResidenceUpdaterService;

public class UserResidenceUpdaterServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH");
			
			UserResidenceUpdaterService service = new UserResidenceUpdaterService();
			Set<String> devices = service.updateResidencePhase1("/tmp/dump", sdf.parse("2015-05-01:16"), sdf.parse("2015-09-01:00"));
			service.updateResidencePhase2("/tmp/dump", devices, sdf.parse("2015-05-01:16"), sdf.parse("2015-09-01:00"));

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0002() {
		try {

			GeoCodingHelper geocoder = new GeoCodingHelperGMapsImpl();
			System.out.println(geocoder.getAddressUsingGeohash("9mucsq7q"));
			System.out.println(geocoder.getAddressUsingGeohash("9g8t5vc2"));

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	
}
