package mobi.allshoppings.cinepolis.adapters;

import java.io.File;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.services.VistaLoyaltyImporterService;
import mobi.allshoppings.model.Voucher;

public class VistaLoyaltyImporterServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {
			VistaLoyaltyImporterService vs = new VistaLoyaltyImporterService();
			File dir = new File("/home/mhapanowicz/workspace-aspi/pi-server/cinepolis-services/target/classes/loyalty/");
			File files[] = dir.listFiles();
			for( File file : files ) {
				String fileName = file.getName();
				String parts[]  = fileName.split("_");
				String type = parts[0];
				vs.doImport(file, "cinepolis_mx", type, Voucher.STATUS_AVAILABLE);
			}
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
