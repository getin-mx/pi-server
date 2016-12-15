package mobi.allshoppings.model.test;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.dao.spi.CinemaDAOJDOImpl;
import mobi.allshoppings.model.Cinema;
import mobi.allshoppings.model.interfaces.StatusAware;

public class CinemaTest extends TestCase {

	CinemaDAO cinemaDao = new CinemaDAOJDOImpl();

	@Test
	public void test0001() {

		Cinema obj = new Cinema();
		try {
			obj = cinemaDao.get("cinepolis_mx_96",true);
			obj.setStatus(StatusAware.STATUS_ENABLED);
			obj.setRadius(300D);
//			cinemaDao.update(obj);
			
		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
