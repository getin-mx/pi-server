package mobi.allshoppings.cinepolis.dataservice;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.services.VistaDataService;
import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.dao.MovieDAO;
import mobi.allshoppings.dao.ShowtimeDAO;
import mobi.allshoppings.dao.spi.CinemaDAOJDOImpl;
import mobi.allshoppings.dao.spi.MovieDAOJDOImpl;
import mobi.allshoppings.dao.spi.ShowtimeDAOJDOImpl;
import mobi.allshoppings.model.Cinema;
import mobi.allshoppings.model.Movie;
import mobi.allshoppings.model.Showtime;
import mobi.allshoppings.model.interfaces.StatusAware;

public class VistaDataServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {
			VistaDataService vs = VistaDataService.getInstance();
			Map<String, Object> resp = vs.getShowtimeAttributes("295", "43797");
			System.out.println(resp);

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0002() {
		try {

			CinemaDAO cinemaDao = new CinemaDAOJDOImpl();
			MovieDAO movieDao = new MovieDAOJDOImpl();
			ShowtimeDAO showtimeDao = new ShowtimeDAOJDOImpl();

			VistaDataService vs = VistaDataService.getInstance();

			List<String> cinemaIds = Arrays.asList(new String[]{"cinepolis_mx_339","cinepolis_mx_340"});
			List<Integer> statuses = Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED});
			List<Cinema> cinemas = cinemaDao.getUsingIdList(cinemaIds, true);

			for( Cinema cinema : cinemas ) {
				List<Showtime> candidateShows = showtimeDao.getUsingCinemaAndDateAndStatusAndRange(cinema.getIdentifier(), new Date(), statuses, null, "showTime");
				for( Showtime showtime : candidateShows ) {
					Movie movie = movieDao.get(showtime.getMovie().getIdentifier(), true);
					try {
						Map<String, Object> resp = vs.getShowtimeAttributes(cinema.getAlternateIdentifier(), showtime.getInternalIdentifier());
						System.out.println("Cinema: " + cinema.getName()
								+ ", Movie: " + movie.getName() + ", Day: "
								+ showtime.getShowDate() + ", Hour: "
								+ showtime.getShowTime() + ", Screen: "
								+ resp.get("screen") + ", availableSeats: "
								+ resp.get("availableSeats"));
					} catch( Exception e ) {
						e.printStackTrace();
					}
				}
			}

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
