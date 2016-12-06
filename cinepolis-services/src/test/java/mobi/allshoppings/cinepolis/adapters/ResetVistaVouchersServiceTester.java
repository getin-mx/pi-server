package mobi.allshoppings.cinepolis.adapters;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.services.ResetVistaVouchersService;
import mobi.allshoppings.dao.VoucherDAO;
import mobi.allshoppings.dao.spi.VoucherDAOJDOImpl;
import mobi.allshoppings.model.Voucher;

import org.junit.Test;

public class ResetVistaVouchersServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {
			// 45 minutes
			long FOURTY_FIVE_MINUTES = 2700000;
			
			ResetVistaVouchersService service = new ResetVistaVouchersService();
			service.doProcess(FOURTY_FIVE_MINUTES);
			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0002() {
		try {
			ResetVistaVouchersService service = new ResetVistaVouchersService();
			service.resetVoucher("890010052495");
			service.resetVoucher("890010178561");
			service.resetVoucher("890010178608");
			service.resetVoucher("890015146540");
			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0003() {
		try {

			List<String> types = Arrays.asList(new String[] {
					"2D", "3D"
			});
			
			VoucherDAO dao = new VoucherDAOJDOImpl();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			try {
				Date checkDate = sdf.parse("2015-08-01");
				Date limitDate = sdf.parse("2015-09-09");
				List<Voucher> vouchers = dao.getUsingDatesAndType(checkDate, limitDate, types, null, "code");
				ResetVistaVouchersService rs = new ResetVistaVouchersService();
				for( Voucher voucher : vouchers ) {
					rs.confirmVoucher(voucher.getCode());
				}

			} catch( Throwable t ) {
				t.printStackTrace();
				fail(t.getMessage());
			}
			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
