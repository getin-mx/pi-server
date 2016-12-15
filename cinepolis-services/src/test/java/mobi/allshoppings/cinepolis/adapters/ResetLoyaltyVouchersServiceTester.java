package mobi.allshoppings.cinepolis.adapters;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.services.ResetLoyaltyVouchersService;
import mobi.allshoppings.cinepolis.vista.voucher.VistaVoucherService;
import mobi.allshoppings.dao.VoucherDAO;
import mobi.allshoppings.dao.spi.VoucherDAOJDOImpl;
import mobi.allshoppings.model.Voucher;

public class ResetLoyaltyVouchersServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {
			// 2 hours
			long TWO_HOURS = 7200000;
			
			ResetLoyaltyVouchersService service = new ResetLoyaltyVouchersService();
			service.doProcess(TWO_HOURS);
			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0002() {
		List<String> types = Arrays.asList(new String[] {
				"2D", "3D"
		});
		
		VoucherDAO dao = new VoucherDAOJDOImpl();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		ResetVistaVouchersService rvvs = new ResetVistaVouchersService();
		
		try {
			Date checkDate = sdf.parse("2015-08-03");
			Date limitDate = sdf.parse("2015-08-06");
			List<Voucher> vouchers = dao.getUsingDatesAndType(checkDate, limitDate, types, null, "code");
			VistaVoucherService vs = VistaVoucherService.getInstance();
			
			for( Voucher voucher : vouchers ) {
				if(voucher.getAssignationDate() != null && voucher.getAssignationDate().after(checkDate) && voucher.getAssignationDate().before(limitDate) && types.contains(voucher.getType())) {
					Map<String, String> resp = vs.getStatus(voucher.getCode());
					System.out.println("voucher: " + voucher.getCode() + ", status: " + resp.get("VOUCHERSTATUS") + ", internal status: " + voucher.getStatus());
					if(!resp.get("VOUCHERSTATUS").equals("R")) {
						System.out.println("Rejecting coupon " + voucher.getCode());
						voucher.setStatus(Voucher.STATUS_AVAILABLE);
						voucher.setAssignationDate(null);
						voucher.setAssignationMember(null);
						voucher.setDeviceUUID(null);
						dao.update(voucher);
						
//						rvvs.resetVoucher(voucher.getCode());
					}
				}
			}

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
		
	}
}
