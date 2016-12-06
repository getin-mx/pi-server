package mobi.allshoppings.cinepolis.services;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.spi.CinemaDAOJDOImpl;
import mobi.allshoppings.dao.spi.ShoppingDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.geocoding.impl.GeoCodingHelperGMapsImpl;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;

public class UpdateCinemasService {

	/**
	 * GeoCoders
	 */
	protected GeoCodingHelper geocoder = new GeoCodingHelperGMapsImpl();
	/**
	 * DAOs 
	 */
	protected CinemaDAO cinemaDao = new CinemaDAOJDOImpl();
	protected ShoppingDAO shoppingDao = new ShoppingDAOJDOImpl();

	public UpdateCinemasService() {
		super();
	}

	public String getShoppingId(GeoCodingHelper geocoder, Map<String, GeoPoint> shoppingMap, double lat, double lon) {
				Iterator<String> i = shoppingMap.keySet().iterator();
				String closest = null;
				int closestDistance = 0;
				while(i.hasNext()) {
					String key = i.next();
					GeoPoint gp = shoppingMap.get(key);
					int distance = geocoder.calculateDistance(lat, lon, gp.getLat(), gp.getLon());
					if( distance < 300 ) {
						if( closest == null ) {
							closest = key;
							closestDistance = distance;
						} else {
							if( distance < closestDistance ) {
								closest = key;
								closestDistance = distance;
							}
						}
					}
				}
				
				return closest;
			}

	public Map<String, GeoPoint> getShoppingMap(ShoppingDAO dao) throws ASException {
		Map<String, GeoPoint> ret = CollectionFactory.createMap();
		List<Shopping> shoppings = dao.getAll();
		for( Shopping shopping : shoppings ) {
			if(shopping.getStatus() == StatusAware.STATUS_ENABLED) {
				GeoPoint geo = new GeoPoint(shopping.getAddress().getLatitude(), shopping.getAddress().getLongitude(), "");
				ret.put(shopping.getIdentifier(), geo);
			}
		}
		return ret;
	}

}