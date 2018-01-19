package mobi.allshoppings.model.test;

import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.dao.spi.CinemaDAOJDOImpl;
import mobi.allshoppings.model.Cinema;
import mobi.allshoppings.model.embedded.FormatPrice;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;

public class FormatPriceTest extends TestCase {

	CinemaDAO cinemaDao = new CinemaDAOJDOImpl();

	@Test
	public void test0001() {

		Cinema obj;
		List<FormatPrice> alt;
		
		try {
			// Espacio las Americas
			obj = cinemaDao.get("cinepolis_mx_339", true);
			System.out.println("Previous Format Prices for " + obj.getName() + ": " + obj.getPrices());
			alt = CollectionFactory.createList();
			alt.add(new FormatPrice("2D", 40));
			alt.add(new FormatPrice("3D", 48));
			obj.setPrices(alt);
			System.out.println("Previous Format Prices for " + obj.getName() + ": " + obj.getPrices());
			obj.setStatus(StatusAware.STATUS_ENABLED);
			cinemaDao.update(obj);
			
			// Town Center el Rosario
			obj = cinemaDao.get("cinepolis_mx_479", true);
			System.out.println("Previous Format Prices for " + obj.getName() + ": " + obj.getPrices());
			alt = CollectionFactory.createList();
			alt.add(new FormatPrice("2D", 32));
			alt.add(new FormatPrice("3D", 35));
			obj.setPrices(alt);
			System.out.println("Previous Format Prices for " + obj.getName() + ": " + obj.getPrices());
			obj.setStatus(StatusAware.STATUS_ENABLED);
			cinemaDao.update(obj);
			
			// La Gran Plaza
			obj = cinemaDao.get("cinepolis_mx_59", true);
			System.out.println("Previous Format Prices for " + obj.getName() + ": " + obj.getPrices());
			alt = CollectionFactory.createList();
			alt.add(new FormatPrice("2D", 43));
			alt.add(new FormatPrice("3D", 50));
			alt.add(new FormatPrice("4DX2D", 86));
			alt.add(new FormatPrice("4DX3D", 107));
			obj.setPrices(alt);
			System.out.println("Previous Format Prices for " + obj.getName() + ": " + obj.getPrices());
			obj.setStatus(StatusAware.STATUS_ENABLED);
			cinemaDao.update(obj);
			
			// Galerias Valle Oriente
			obj = cinemaDao.get("cinepolis_mx_166", true);
			System.out.println("Previous Format Prices for " + obj.getName() + ": " + obj.getPrices());
			alt = CollectionFactory.createList();
			alt.add(new FormatPrice("2D", 43));
			alt.add(new FormatPrice("3D", 50));
			alt.add(new FormatPrice("IMAX2D", 51));
			alt.add(new FormatPrice("IMAX3D", 61));
			obj.setPrices(alt);
			System.out.println("Previous Format Prices for " + obj.getName() + ": " + obj.getPrices());
			obj.setStatus(StatusAware.STATUS_ENABLED);
			cinemaDao.update(obj);
			
			// Paseo Interlomas
			obj = cinemaDao.get("cinepolis_mx_449", true);
			System.out.println("Previous Format Prices for " + obj.getName() + ": " + obj.getPrices());
			alt = CollectionFactory.createList();
			alt.add(new FormatPrice("2D", 44));
			alt.add(new FormatPrice("3D", 50));
			alt.add(new FormatPrice("4DX2D", 86));
			alt.add(new FormatPrice("4DX3D", 107));
			obj.setPrices(alt);
			System.out.println("Previous Format Prices for " + obj.getName() + ": " + obj.getPrices());
			obj.setStatus(StatusAware.STATUS_ENABLED);
			cinemaDao.update(obj);
			
			// Paseo Interlomas
			obj = cinemaDao.get("cinepolis_mx_96", true);
			System.out.println("Previous Format Prices for " + obj.getName() + ": " + obj.getPrices());
			alt = CollectionFactory.createList();
			alt.add(new FormatPrice("2D", 34));
			alt.add(new FormatPrice("3D", 42));
			obj.setPrices(alt);
			System.out.println("Previous Format Prices for " + obj.getName() + ": " + obj.getPrices());
			obj.setStatus(StatusAware.STATUS_ENABLED);
			cinemaDao.update(obj);
			
		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
