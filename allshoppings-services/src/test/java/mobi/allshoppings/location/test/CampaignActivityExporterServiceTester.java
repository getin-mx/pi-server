package mobi.allshoppings.location.test;

import java.io.File;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;
import mobi.allshoppings.location.CampaignActivityExporterService;

import org.junit.Test;

public class CampaignActivityExporterServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			CampaignActivityExporterService service = new CampaignActivityExporterService();
			service.exportCampaignActivities("cinepolis_mx", sdf.parse("2015-06-26"), sdf.parse("2015-09-01"), new File("/tmp/export.csv"));

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
