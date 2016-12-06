package mobi.allshoppings.cinepolis.vista.loyalty.test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.vista.loyalty.VistaLoyaltyService;
import mobi.allshoppings.dao.VoucherDAO;
import mobi.allshoppings.dao.spi.VoucherDAOJDOImpl;
import mobi.allshoppings.model.Voucher;

import org.junit.Test;

public class VistaLoyaltyServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {
			VistaLoyaltyService vs = VistaLoyaltyService.getInstance();
//			Map<String, String> resp = vs.activate("700540086182", "FCRELECFFCL20140700002", "315");
			Map<String, String> resp = vs.activate("700050512142", "PAYPALFPP20150106001", "142");
			System.out.println(resp);

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0002() {
		try {
			VoucherDAO dao = new VoucherDAOJDOImpl();
			VistaLoyaltyService vs = VistaLoyaltyService.getInstance();
//			Map<String, String> resp = vs.delete("700540086182", "FCRELECFFCL20140700002", "315");
//			Map<String, String> resp = vs.delete("700050512142", "PAYPALFPP20150106001", "142");

//			Map<String, String> resp = vs.getStatus("701190003031", "FASCRPFOLCRP20150607329", "456");
//			Map<String, String> resp = vs.getStatus("701180237243", "FASBGTFOLBGT20150633070", "457");
//			Map<String, String> resp = vs.getStatus("701180000377", "FASBGTFOLBGT20150615089", "457");

			List<Voucher> list = dao.getUsingStatusAndBrandAndType(Arrays.asList(new Integer[] {Voucher.STATUS_OFFERED}), "cinepolis_mx", Arrays.asList(new String[] {"70118", "70119"})); 
			for( Voucher obj : list ) {
				Map<String, String> resp = vs.getStatus(obj.getCode(), obj.getSubcode1(), obj.getSubcode2());
				System.out.println(obj.getCode() + " - " + resp.get("DisplayRecognition") + " - " + resp.get("QtyToTake") + " - " + resp.get("QtyEarned"));
			}
			
//			System.out.println(resp);

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0003() {
		try {
			VoucherDAO dao = new VoucherDAOJDOImpl();
			VistaLoyaltyService vs = VistaLoyaltyService.getInstance();

			Voucher obj; 
			Map<String, String> resp;

			obj = dao.get("701190052111"); 
			resp = vs.getStatus(obj.getCode(), obj.getSubcode1(), obj.getSubcode2());
//			System.out.println(obj.getCode() + " - " + resp.get("DisplayRecognition") + " - " + resp.get("QtyToTake") + " - " + resp.get("QtyEarned"));
			
			System.out.println("\n\n\n\n" + resp + "\n\n\n\n\n\n");

			obj = dao.get("701190052269"); 
			resp = vs.getStatus(obj.getCode(), obj.getSubcode1(), obj.getSubcode2());
//			System.out.println(obj.getCode() + " - " + resp.get("DisplayRecognition") + " - " + resp.get("QtyToTake") + " - " + resp.get("QtyEarned"));
			
			System.out.println("\n\n\n\n" + resp + "\n\n\n\n\n\n");

			obj = dao.get("701190052244"); 
			resp = vs.getStatus(obj.getCode(), obj.getSubcode1(), obj.getSubcode2());
//			System.out.println(obj.getCode() + " - " + resp.get("DisplayRecognition") + " - " + resp.get("QtyToTake") + " - " + resp.get("QtyEarned"));
			
			System.out.println("\n\n\n\n" + resp + "\n\n\n\n\n\n");

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
