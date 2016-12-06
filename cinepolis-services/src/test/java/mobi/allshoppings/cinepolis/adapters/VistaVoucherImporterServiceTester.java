package mobi.allshoppings.cinepolis.adapters;

import java.io.File;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.services.VistaVoucherImporterService;
import mobi.allshoppings.model.Voucher;

import org.junit.Test;

public class VistaVoucherImporterServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {
			VistaVoucherImporterService vs = new VistaVoucherImporterService();
			File dir = new File("/home/mhapanowicz/workspace-aspi/pi-server/cinepolis-services/target/classes/vouchers/");
			File files[] = dir.listFiles();
			for( File file : files ) {
				String fileName = file.getName();
				String parts[]  = fileName.split("_");
				String parts2[] = parts[2].split("\\(");
				String type = parts2[0];
				vs.doImport(file, "cinepolis_mx", type, Voucher.STATUS_AVAILABLE);
			}
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
