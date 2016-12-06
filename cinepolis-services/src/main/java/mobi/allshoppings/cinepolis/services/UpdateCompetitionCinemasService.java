package mobi.allshoppings.cinepolis.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.model.Cinema;
import mobi.allshoppings.model.interfaces.StatusAware;

import org.springframework.web.client.RestTemplate;

public class UpdateCompetitionCinemasService extends UpdateCinemasService {

	/**
	 * Available Countries List 
	 */
	public final static List<Integer> COUNTRIES = Arrays.asList(new Integer[] {1}); 

	/**
	 * Base key
	 */
	private static final String BASE_ID = "cinemex_mx";

	/**
	 * Updates the cinemas list
	 */
	public void doUpdate() throws ASException {

		Object[] temp;
		List<?> statesArray;
		Map<String, GeoPoint> shoppingMap = getShoppingMap(shoppingDao);
		
		// Update States
		RestTemplate restTemplate = new RestTemplate();
		temp = restTemplate.getForObject("http://cinemex.com/rest/states/", Object[].class);
		statesArray = Arrays.asList(temp);

		// For each City, update cinemas
		for( int i = 0; i < statesArray.size(); i++ ) {
			@SuppressWarnings("unchecked")
			Map<String, ?> state = (Map<String, ?>)statesArray.get(i);
			List<?> areasArray = Arrays.asList(state.get("areas"));
			for( int j = 0; j < areasArray.size(); j++ ) {
				@SuppressWarnings("unchecked")
				Map<String, ?> area = (Map<String, ?>)((List<?>)areasArray.get(0)).get(j);

				temp = restTemplate.getForObject("http://cinemex.com/rest/cinemas/area/" + area.get("id"), Object[].class);
				if( temp != null ) {

					// For each result, tries to update a cinema object
					for( Object obj : temp ) {
						@SuppressWarnings("unchecked")
						Map<String, ?> rcvCinema = (Map<String, ?>)obj;					
						String identifier = BASE_ID + "_" + rcvCinema.get("id");

						boolean newObject = false;
						Cinema cinema;
						try {
							cinema = cinemaDao.get(identifier, true);
						} catch( ASException e ) {
							cinema = new Cinema();
							cinema.setKey(cinemaDao.createKey(identifier));
							cinema.setBrandId(BASE_ID);
							cinema.setStatus(StatusAware.STATUS_DISABLED);
							newObject = true;
						}

						@SuppressWarnings("unchecked")
						Map<String, ?> info = (Map<String, ?>)rcvCinema.get("info");
						@SuppressWarnings("unchecked")
						Map<String, ?> myArea = (Map<String, ?>)rcvCinema.get("area");
						
						cinema.setAlternateIdentifier(String.valueOf(rcvCinema.get("id")));
						cinema.setInternalIdentifier(String.valueOf(rcvCinema.get("id")));
						cinema.setName(String.valueOf(rcvCinema.get("name")));

						cinema.getAddress().setLatitude(Double.valueOf(String.valueOf(rcvCinema.get("lat"))));
						cinema.getAddress().setLongitude(Double.valueOf(String.valueOf(rcvCinema.get("lng"))));
						cinema.getAddress().setStreetName(String.valueOf(info.get("address")));
						cinema.getAddress().setCountry("Mexico");
						cinema.getAddress().setProvince((String)state.get("name"));
						cinema.getAddress().setCity((String)myArea.get("name"));

						cinema.setShoppingId(getShoppingId(geocoder, shoppingMap, cinema.getAddress().getLatitude(), cinema.getAddress().getLongitude()));
						
						if( cinema.getRadius() == null ) cinema.setRadius(200D);

						if( newObject ) 
							cinemaDao.create(cinema);
						else
							cinemaDao.update(cinema);

					}
				}
			}
		}
	}
}
