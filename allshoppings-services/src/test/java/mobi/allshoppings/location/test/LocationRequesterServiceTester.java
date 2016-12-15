package mobi.allshoppings.location.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.location.LocationRequesterService;

public class LocationRequesterServiceTester extends TestCase {

	public final static List<String> testDevices = Arrays.asList(new String[] {
			"309C51B1-B6B4-4529-A3C8-95996ED990A3", // iPhone 4s (Fer)
			"8ea3fc131a6c44de@cinepolis_mx", //  iPhone 4 testing
			"083B37E5-36B0-4688-904B-1DDD9797E21A", //  iPhone 6+ testing
			"8ea3fc131a6c44de@cinepolis_mx", //  Samsumg G4 testing
			"38b172b6ca65b513@cinepolis_mx", //  Moto G2 (Testing Kari)
			"776AE2D6-840A-45BF-A1D0-3873B54D6D72", //  iPhone 4s (Mat)
			"c254507c3690abd9@cinepolis_mx", //  Samsung G1 (Mat)
			"4d36f854b29fcdb8@cinepolis_mx" //  Samsung G3 mini (Max)
	});

	@Test
	public void test0001() {
		try {

			LocationRequesterService service = new LocationRequesterService();
			service.requestLocation("http://127.0.0.1:8080/app/requestDeviceLocation?authToken=" +
					"2A2C192578F597CEE6BC3EE8A26B9F7DB0DBA4B0CD8930FDC3D2BB0E7D1B8CC9", 100, null);

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0002() {
		try {

			LocationRequesterService service = new LocationRequesterService();
			service.requestLocation("http://api.allshoppings.mobi/app/requestDeviceLocation?authToken=" +
					"2A2C192578F597CEE6BC3EE8A26B9F7DB0DBA4B0CD8930FDC3D2BB0E7D1B8CC9", 30, testDevices);

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}
	
}
