package mobi.allshoppings.cinepolis.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.model.Cinema;
import mobi.allshoppings.model.interfaces.StatusAware;

public class UpdateCinepolisCinemasService extends UpdateCinemasService {

	/**
	 * Available Countries List 
	 */
	public final static List<Integer> COUNTRIES = Arrays.asList(new Integer[] {1}); 
	
	/**
	 * Base key
	 */
	private static final String BASE_ID = "cinepolis_mx";

	/**
	 * Updates the cinemas list
	 */
	public void doUpdate() throws ASException {
		
		Object[] temp;
		List<?> citiesArray;
		Map<String, GeoPoint> shoppingMap = getShoppingMap(shoppingDao);

		// Update Cities
		RestTemplate restTemplate = new RestTemplate();
		temp = restTemplate.getForObject("http://api.cinepolis.com.mx/Consumo.svc/json/ObtenerCiudades", Object[].class);
		citiesArray = Arrays.asList(temp);

		// For each City, update cinemas
		for( int i = 0; i < citiesArray.size(); i++ ) {
			@SuppressWarnings("unchecked")
			Map<String, ?> city = (Map<String, ?>)citiesArray.get(i);

			// Just use available countries
			if( COUNTRIES.contains((Integer)city.get("IdPais"))) {
				temp = restTemplate.getForObject("http://api.cinepolis.com.mx/Consumo.svc/json/ObtenerComplejos?idsComplejos=0&idsCiudades=" + city.get("Id"), Object[].class);
				if( temp != null ) {
					
					// For each result, tries to update a cinema object
					for( Object obj : temp ) {
						@SuppressWarnings("unchecked")
						Map<String, ?> rcvCinema = (Map<String, ?>)obj;					
						String identifier = BASE_ID + "_" + rcvCinema.get("Id");
						
						boolean newObject = false;
						Cinema cinema;
						try {
							cinema = cinemaDao.get(identifier, true);
						} catch( ASException e ) {
							cinema = new Cinema();
							cinema.setKey(cinemaDao.createKey(identifier));
							cinema.setBrandId("cinepolis_mx");
							cinema.setStatus(StatusAware.STATUS_DISABLED);
							newObject = true;
						}
						
						cinema.setAlternateIdentifier(String.valueOf(rcvCinema.get("IdVista")));
						cinema.setInternalIdentifier(String.valueOf(rcvCinema.get("Id")));
						cinema.setName(String.valueOf(rcvCinema.get("Nombre")));
						cinema.setCustomUrl(String.valueOf(rcvCinema.get("Url")));

						cinema.getAddress().setLatitude(Double.valueOf(String.valueOf(rcvCinema.get("Latitud"))));
						cinema.getAddress().setLongitude(Double.valueOf(String.valueOf(rcvCinema.get("Longitud"))));
						cinema.getAddress().setStreetName(String.valueOf(rcvCinema.get("Direccion")));
						cinema.getAddress().setCountry(countryToString(Integer.valueOf(String.valueOf(city.get("IdPais")))));
						cinema.getAddress().setProvince((String.valueOf(city.get("Estado"))));
						cinema.getAddress().setCity((String.valueOf(city.get("Nombre"))));
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
	
	/**
	 * Internal country conversion method only valid for Cinepolis
	 * 
	 * @param code
	 *            Cinepolis country code
	 * @return A String representing the allshoppings country code
	 */
	private String countryToString(int code) {
		switch (code) {
		case 1:
			return "Mexico";
		default:
			return "Mexico";
		}
	}

}
