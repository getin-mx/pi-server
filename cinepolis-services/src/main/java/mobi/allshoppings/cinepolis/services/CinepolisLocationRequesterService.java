package mobi.allshoppings.cinepolis.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.dao.ShowtimeDAO;
import mobi.allshoppings.dao.spi.CinemaDAOJDOImpl;
import mobi.allshoppings.dao.spi.ShowtimeDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.geocoding.impl.GeoCodingHelperGMapsImpl;
import mobi.allshoppings.location.LocationRequesterService;
import mobi.allshoppings.model.Cinema;
import mobi.allshoppings.model.Showtime;
import mobi.allshoppings.model.embedded.AlternateLocation;
import mobi.allshoppings.model.interfaces.StatusAware;

import com.inodes.util.CollectionFactory;

public class CinepolisLocationRequesterService {

	private static final Logger log = Logger.getLogger(CinepolisLocationRequesterService.class.getName());

	/**
	 * Base key
	 */
	private static final String BASE_ID = "cinepolis_mx";

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private CinemaDAO cinemaDao = new CinemaDAOJDOImpl();
	private GeoCodingHelper geocoder = new GeoCodingHelperGMapsImpl();
	private ShowtimeDAO showtimeDao = new ShowtimeDAOJDOImpl();

	public void requestLocation(List<String> cinemaIds, String serviceUrl, int distance, long locationUpdateLimit, int batchSize, boolean checkShows, long showTimeOffset) throws ASException {

		LocationRequesterService service = new LocationRequesterService();
		List<Integer> statuses = Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED});
		List<Cinema> cinemas = cinemaIds == null ? cinemaDao.getUsingBrandAndStatusAndRange(BASE_ID, statuses, null, null) : cinemaDao.getUsingIdList(cinemaIds, true);
		List<GeoPoint> points = CollectionFactory.createList();

		Date now = new Date();
		Date limitDateTime = new Date(now.getTime() + showTimeOffset);

		for( Cinema cinema : cinemas ) {

			log.log(Level.INFO, "Starting to send movie tickets for Cinepolis cinema " + cinema.getName());
			List<Showtime> candidateShows = showtimeDao.getUsingCinemaAndDateAndStatusAndRange(cinema.getIdentifier(), new Date(), null, null, "showTime");
			List<Showtime> finalShows = CollectionFactory.createList();

			if( checkShows ) {
				// Select shows between time range
				StringBuffer sb = new StringBuffer();
				for(Showtime show : candidateShows ) {
					try {
						sb.append(show.getShowTime()).append(" ");
						Date showDateTime = sdf.parse(show.getShowDate() + " " + show.getShowTime() + " CDT");
						if( showDateTime.before(limitDateTime) && now.before(showDateTime)) {
							log.log(Level.INFO, "Show " + show + " is between valid ranges ");
							finalShows.add(show);
						}
					} catch (ParseException e) {
						log.log(Level.SEVERE, e.getMessage(), e);
					}
				}

				log.log(Level.INFO, finalShows.size() + " shows found");
				log.log(Level.INFO, "Recorded show times where " + sb.toString());
			}
			
			if( !checkShows || finalShows.size() > 0 ) {
				points.add(new GeoPoint(cinema.getAddress().getLatitude(), cinema
						.getAddress().getLongitude(), geocoder
						.encodeGeohash(cinema.getAddress().getLatitude(),
								cinema.getAddress().getLongitude())));
				for( AlternateLocation location : cinema.getAlternateLocations() ) {
					points.add(new GeoPoint(location.getLatitude(), location
							.getLongitude(), geocoder
							.encodeGeohash(location.getLatitude(),
									location.getLongitude())));
				}
			}
		}

		service.requestLocation(serviceUrl, points, distance, locationUpdateLimit, batchSize, BASE_ID);

	}
}
