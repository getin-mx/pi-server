package mobi.allshoppings.location.test;

import java.io.File;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;
import mobi.allshoppings.location.LocationHistoryExporterService;

import org.junit.Test;

public class LocationHistoryExporterServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			File f1 = new File("/tmp/deviceLocation.json");
			File f2 = new File("/tmp/deviceInfo.json");
			
			LocationHistoryExporterService service = new LocationHistoryExporterService();
			service.exportLocationHistory(19.363334, -99.266480, 3500, sdf.parse("2015-06-01"), sdf.parse("2015-06-15"), f1, f2, 100, true, null);

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0002() {
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			File f1 = new File("/tmp/deviceLocation.json");
			File f2 = new File("/tmp/deviceInfo.json");
			
			LocationHistoryExporterService service = new LocationHistoryExporterService();
			service.exportLocationHistory(19.363334, -99.266480, 3500, sdf.parse("2015-06-01"), sdf.parse("2015-06-15"), f1, f2, 100, true, f2);

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
