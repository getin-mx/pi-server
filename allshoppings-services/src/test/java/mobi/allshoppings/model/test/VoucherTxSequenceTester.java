package mobi.allshoppings.model.test;

import junit.framework.TestCase;
import mobi.allshoppings.dao.VoucherDAO;
import mobi.allshoppings.dao.spi.VoucherDAOJDOImpl;
import mobi.allshoppings.model.Voucher;

import org.junit.Test;

public class VoucherTxSequenceTester extends TestCase {

	VoucherDAO voucherDao = new VoucherDAOJDOImpl();

	@Test
	public void test0001() {

		try {
			Long n = voucherDao.getNextSequence();
			System.out.println(n);
		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void test0002() {
		try {
			Voucher obj = voucherDao.getNextAvailable("2D");
			System.out.println(obj);
		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
