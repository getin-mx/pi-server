package mobi.allshoppings.cinepolis.vista.voucher.test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.vista.voucher.VistaVoucherService;
import mobi.allshoppings.dao.VoucherDAO;
import mobi.allshoppings.dao.spi.VoucherDAOJDOImpl;
import mobi.allshoppings.model.Voucher;
import mobi.allshoppings.tools.Range;

public class VistaVoucherServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {
			VistaVoucherService vs = VistaVoucherService.getInstance();
			Map<String, String> resp = vs.sellAndCommit("890013027358", 1);
			System.out.println(resp);

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0002() {
		try {
			VistaVoucherService vs = VistaVoucherService.getInstance();
			Map<String, String> resp = vs.sellAndCommit("890017535343", 1 );
			System.out.println(resp);

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0003() {
		try {
			VistaVoucherService vs = VistaVoucherService.getInstance();
			Map<String, String> resp = vs.refundAndCommit("890017535343", 1);
			System.out.println(resp);

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0004() {
//		String vouchers[] = new String[] {
//				"890010004215"
//		};
		
		
		List<String> types = Arrays.asList(new String[] {
				"2D", "3D"
		});
		
		VoucherDAO dao = new VoucherDAOJDOImpl();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date checkDate = sdf.parse("2015-09-08");
			Date limitDate = sdf.parse("2015-09-09");
			List<Voucher> vouchers = dao.getUsingDatesAndType(checkDate, limitDate, types, null, "code");
			VistaVoucherService vs = VistaVoucherService.getInstance();
			
			for( Voucher voucher : vouchers ) {
				if(voucher.getAssignationDate() != null && voucher.getAssignationDate().after(checkDate) && voucher.getAssignationDate().before(limitDate) && types.contains(voucher.getType())) {
					Map<String, String> resp = vs.getStatus(voucher.getCode());
					System.out.println(resp);
					System.out.println("voucher: " + voucher.getCode() + ", status: " + resp.get("VOUCHERSTATUS") + ", internal status: " + voucher.getStatus() + ", assignation date: " + voucher.getAssignationDate());
					if( resp.get("VOUCHERSTATUS").equals("R")) {
						System.out.println("Rejecting coupon " + voucher.getCode());
					}
				}
			}

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
		
	}
	
	@Test
	public void test0005() {
		try {
			VistaVoucherService vs = VistaVoucherService.getInstance();
			Map<String, String> resp = vs.activate("820050096500", 1);
			System.out.println(resp);

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}
	
	@Test
	public void test0006() {
		// folios de produccion
		String vouchers[] = new String[] {
				"890010043758",
				"890020954181"
		};
		
		try {
			VistaVoucherService vs = VistaVoucherService.getInstance();
			for( String voucher : vouchers ) {
				Map<String, String> resp = vs.getStatus(voucher);
				System.out.println("voucher: " + voucher + ", status: " + resp.get("VOUCHERSTATUS"));
				System.out.println(resp);
			}

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
		
	}
	
	@Test
	public void test0007() {

		try {
			VoucherDAO dao = new VoucherDAOJDOImpl();
			Range range = new Range(0, 300);
			List<Voucher> list = dao.getUsingRange(range);
			
			VistaVoucherService vs = VistaVoucherService.getInstance();
			for( Voucher voucher : list ) {
//				long txId = dao.getNextSequence();
//				Map<String, String> resp = vs.refundAndCommit(voucher.getCode(), txId);
				Map<String, String> resp = vs.getStatus(voucher.getCode());
				System.out.println("voucher: " + voucher + ", status: " + resp.get("VOUCHERSTATUS"));
				System.out.println(resp);
			}

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
		
	}
	
	@Test
	public void test0008() {
		try {
			VistaVoucherService vs = VistaVoucherService.getInstance();
			List<Voucher> list = vs.get(3, "2D", "device1", "show1");
			System.out.println(list);
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0009() {
		// folios de produccion
		String vouchers[] = new String[] {
				"890017535343", // dudoso
				"890017717201", // dudoso
				"890010006800", // canjeado
				"890010006800", 
				"890018431433"	// disponible
		};
		
		try {
			VistaVoucherService vs = VistaVoucherService.getInstance();
			for( String voucher : vouchers ) {
				
				Map<String, String> resp = vs.getStatus(voucher);
				System.out.println("voucher: " + voucher + ", status: " + resp.get("VOUCHERSTATUS"));
				System.out.println(resp);
			}

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
		
	}
}

