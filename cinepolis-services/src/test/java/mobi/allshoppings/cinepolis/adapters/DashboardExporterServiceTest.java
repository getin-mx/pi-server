package mobi.allshoppings.cinepolis.adapters;

import java.io.File;
import java.text.SimpleDateFormat;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.services.DashboardExporterService;

public class DashboardExporterServiceTest extends TestCase {

	@Test
	public void test0001() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			DashboardExporterService service = new DashboardExporterService();
			service.doExport("ticket_performance", null, sdf.parse("2015-06-26"), sdf.parse("2015-07-05"), new File("/tmp/export.csv"));
			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
