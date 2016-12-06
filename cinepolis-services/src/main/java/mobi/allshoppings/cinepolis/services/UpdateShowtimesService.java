package mobi.allshoppings.cinepolis.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.dao.MovieDAO;
import mobi.allshoppings.dao.ShowtimeDAO;
import mobi.allshoppings.dao.spi.MovieDAOJDOImpl;
import mobi.allshoppings.dao.spi.ShowtimeDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Movie;
import mobi.allshoppings.model.Showtime;
import mobi.allshoppings.model.embedded.CinemaEmbedd;
import mobi.allshoppings.model.embedded.MovieEmbedd;
import mobi.allshoppings.model.interfaces.StatusAware;

import org.springframework.web.client.RestTemplate;

public class UpdateShowtimesService {

	private static final Logger log = Logger.getLogger(UpdateShowtimesService.class.getName());

	/**
	 * Base key
	 */
	private static final String BASE_ID = "cinepolis_mx";

	/**
	 * Date Formatter
	 */
	private static final SimpleDateFormat sdfFrom = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat sdfTo = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * DAOs 
	 */
	private ShowtimeDAO showTimeDao = new ShowtimeDAOJDOImpl();
	private MovieDAO movieDao = new MovieDAOJDOImpl();

	/**
	 * Updates the cinemas list
	 */
	public void doUpdate() throws ASException {

		log.log(Level.INFO, "Starting to update showtimes for Cinepolis");

		Object[] temp;
		RestTemplate restTemplate = new RestTemplate();
		Date startTime = new Date(new Date().getTime() - 60000);

		List<Integer> status = Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED});
		List<Movie> movies = movieDao.getUsingBrandAndStatusAndRange(BASE_ID, status, null, null);

		String today = sdfTo.format(new Date());
		
		// For each Movie, update show times
		for( Movie movie : movies ) {

			log.log(Level.INFO, "Updating showtimes for movie " + movie.getName());

			for( CinemaEmbedd cinema : movie.getCinemas() ) {

				log.log(Level.INFO, "Updating showtimes for movie " + movie.getName() + " and cinema " + cinema.getName());

				temp = restTemplate
						.getForObject(
								"http://api.cinepolis.com.mx/Consumo.svc/json/ObtenerHorariosPelicula?idPelicula="
										+ movie.getInternalIdentifier()
										+ "&idsComplejos="
										+ cinema.getInternalIdentifier(),
								Object[].class);
				if( temp != null ) {

					// For each result, tries to update a cinema object
					for( Object obj : temp ) {
						@SuppressWarnings("unchecked")
						Map<String, ?> rcvShowTime = (Map<String, ?>)obj;
						for( Object obj1 : (ArrayList<?>)rcvShowTime.get("Formatos")) {

							@SuppressWarnings("unchecked")
							Map<String, ?> rcvFormatos = (Map<String, ?>)obj1;

							String formatName = (String)rcvFormatos.get("Nombre");

							for( Object obj2 : (ArrayList<?>)rcvFormatos.get("Horarios")) {

								try {
									@SuppressWarnings("unchecked")
									Map<String, ?> rcvHorarios = (Map<String, ?>)obj2;

									if( today.equals(sdfTo.format(sdfFrom.parse(((String)rcvHorarios.get("Fecha")))))) {

										String identifierShowTime = ((String)rcvHorarios.get("IdShowtime"));

										String identifier = cinema.getIdentifier() + "_" + identifierShowTime;

										boolean newObject = false;
										Showtime showTime;
										try {
											showTime = showTimeDao.get(identifier, true);
										} catch( ASException e ) {
											showTime = new Showtime();
											showTime.setKey(showTimeDao.createKey(identifier));
											showTime.setBrandId("cinepolis_mx");
											showTime.setStatus(StatusAware.STATUS_ENABLED);
											newObject = true;
										}

										showTime.setCinema(cinema);
										showTime.setMovie(new MovieEmbedd(movie.getIdentifier(), movie.getName(), movie.getInternalIdentifier()));
										showTime.setFormatName(formatName);
										showTime.setInternalIdentifier(identifierShowTime);
										showTime.setShowDate(sdfTo.format(sdfFrom.parse(((String)rcvHorarios.get("Fecha")))));
										showTime.setShowTime(((String)rcvHorarios.get("Hora")));

										if( newObject ) 
											showTimeDao.create(showTime);
										else
											showTimeDao.update(showTime);
									}
								} catch (java.text.ParseException e) {
									log.log(Level.SEVERE, e.getMessage(), e);
								}
							}
						}
					}
				}
			}
		}

		// Sleeps for 10 seconds
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}
		
		// clears old shows
		List<Integer> activeStatus = Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED});
		List<Showtime> oldShows = showTimeDao.getUsingLastUpdateStatusAndRange(null, startTime, false, activeStatus, null, null, null, true);
		log.log(Level.INFO, "Disabling old "  + oldShows.size() + " shows");
		for( Showtime show : oldShows ) {
			if(!show.getStatus().equals(StatusAware.STATUS_REMOVED)) {
				log.log(Level.INFO, "Disabling showtime " + show);
				show.setStatus(StatusAware.STATUS_REMOVED);
				showTimeDao.update(show);
			}
		}
		
		log.log(Level.INFO, "Showtimes updated for Cinepolis");

	}

}
