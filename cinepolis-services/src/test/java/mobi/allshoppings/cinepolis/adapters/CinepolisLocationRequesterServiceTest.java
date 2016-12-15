package mobi.allshoppings.cinepolis.adapters;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.services.CinepolisLocationRequesterService;

public class CinepolisLocationRequesterServiceTest extends TestCase {

	private final static long ONE_HOUR = 3600000;
	private final static long FIVE_HOURS = ONE_HOUR * 5;
	
	@Test
	public void test0001() {
		try {

			CinepolisLocationRequesterService service = new CinepolisLocationRequesterService();
			service.requestLocation(null, "http://api.allshoppings.mobi/app/requestDeviceLocation?authToken=" +
					"2A2C192578F597CEE6BC3EE8A26B9F7DB0DBA4B0CD8930FDC3D2BB0E7D1B8CC9", 3000, FIVE_HOURS, 20, true, 6000000);
			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
