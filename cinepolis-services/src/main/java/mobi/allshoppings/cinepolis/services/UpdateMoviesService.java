package mobi.allshoppings.cinepolis.services;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.client.RestTemplate;

import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.dao.MovieDAO;
import mobi.allshoppings.dao.spi.CinemaDAOJDOImpl;
import mobi.allshoppings.dao.spi.MovieDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Cinema;
import mobi.allshoppings.model.Movie;
import mobi.allshoppings.model.embedded.CinemaEmbedd;
import mobi.allshoppings.model.interfaces.StatusAware;

public class UpdateMoviesService {

	private static final Logger log = Logger.getLogger(UpdateMoviesService.class.getName());
	
	/**
	 * Base key
	 */
	private static final String BASE_ID = "cinepolis_mx";

	/**
	 * URL where the images are
	 */
	private static final String BANNER_URL = "http://www.cinepolis.com/_MOVIL/Android/cartel/";
	
	/**
	 * DAOs 
	 */
	private CinemaDAO cinemaDao = new CinemaDAOJDOImpl();
	private MovieDAO movieDao = new MovieDAOJDOImpl();
	
	/**
	 * Updates the movies list
	 */
	public void doUpdate() throws ASException {

		log.log(Level.INFO, "Starting to update movies for Cinepolis");

		Date now = new Date();
		Object[] temp;
		RestTemplate restTemplate = new RestTemplate();
		
		List<Integer> statuses = Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED});
		List<Cinema> cinemas = cinemaDao.getUsingBrandAndStatusAndRange(BASE_ID, statuses, null, null);
		
		for( Cinema cinema : cinemas ) {
			log.log(Level.INFO, "Starting to update movies for Cinepolis cinema " + cinema.getName());
			temp = restTemplate.getForObject("http://api.cinepolis.com.mx/Consumo.svc/json/ObtenerPeliculasCartelera?idsComplejos=" 
					+ cinema.getInternalIdentifier() + "&idPais=" + countryToInt(cinema.getAddress().getCountry()), Object[].class);
			
			// For each result, tries to update a cinema object
			for( Object obj : temp ) {
				@SuppressWarnings("unchecked")
				Map<String,?> rcvMovie = (Map<String,?>)obj;
				
				String identifier = BASE_ID + "_" + String.valueOf(rcvMovie.get("Id"));
				
				Movie movie;
				boolean newObject = false;
				
				try {
					movie = movieDao.get(identifier, true);
				} catch( Exception e ) {
					movie = new Movie();
					movie.setKey(movieDao.createKey(identifier));
					movie.setBrandId(BASE_ID);
					newObject = true;
				}
				
				movie.setInternalIdentifier(String.valueOf(rcvMovie.get("Id")));
				movie.setActors(String.valueOf(rcvMovie.get("Actores")));
				movie.setAvatarId(BANNER_URL + String.valueOf(rcvMovie.get("Cartel")));
				movie.setLenght(String.valueOf(rcvMovie.get("Duracion")));
				movie.setName(String.valueOf(rcvMovie.get("Titulo")));
				movie.setOriginalName(String.valueOf(rcvMovie.get("TituloOriginal")));
				movie.setRate(String.valueOf(rcvMovie.get("Clasificacion")));
				movie.setSinopsis(String.valueOf(rcvMovie.get("Sinopsis")).trim());
				movie.setMovieGender(String.valueOf(rcvMovie.get("Genero")));
				movie.setDirector(String.valueOf(rcvMovie.get("Director")));
				movie.setStatus(StatusAware.STATUS_ENABLED);

				List<CinemaEmbedd> cList = movie.getCinemas();
				CinemaEmbedd cEmbedd = new CinemaEmbedd(cinema.getIdentifier(), cinema.getName(), cinema.getInternalIdentifier());
				if( !cList.contains(cEmbedd)) cList.add(cEmbedd);

				log.log(Level.INFO, "Updating movie " + movie.getName());

				if( newObject ) 
					movieDao.create(movie);
				else
					movieDao.update(movie);
			}
		}

		log.log(Level.INFO, "Disabling old movies");
		movieDao.disableOldMovies(now);
		
		log.log(Level.INFO, "Update movies for Cinepolis finished");

	}
	
	/**
	 * Internal country conversion method only valid for Cinepolis
	 * 
	 * @param code
	 *            AllShoppings country code
	 * @return A Integer representing the Cinepolis country code
	 */
	private int countryToInt(String country) {
		if (country.equalsIgnoreCase("Mexico"))
			return 1;
		return 1;
	}

}
