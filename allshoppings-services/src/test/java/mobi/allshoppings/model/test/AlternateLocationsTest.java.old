package mobi.allshoppings.model.test;

import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.dao.spi.CinemaDAOJDOImpl;
import mobi.allshoppings.model.Cinema;
import mobi.allshoppings.model.embedded.AlternateLocation;
import mobi.allshoppings.tools.CollectionFactory;

public class AlternateLocationsTest extends TestCase {

	CinemaDAO cinemaDao = new CinemaDAOJDOImpl();

	@Test
	public void test0001() {

		try {
			Cinema obj = cinemaDao.get("cinepolis_mx_339", true);
			System.out.println("Alternate locations for " + obj.getName() + ": " + obj.getAlternateLocations());
			List<AlternateLocation> alt = CollectionFactory.createList();
			alt.add(new AlternateLocation("clusterTIM", 19.72552908467457, -101.12032055854797));
//			alt.add(new AlternateLocation("Corporativo Cinepolis", 19.715121447389738, -101.11916184425354));
//			alt.add(new AlternateLocation("Samara", 19.36770527305636, -99.25832780157464));
			obj.setAlternateLocations(alt);
			
			cinemaDao.update(obj);
			
		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
