package mobi.allshoppings.bz.spi;


import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.bz.CountryListBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.GeoEntityDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserEntityCache;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.uec.UserEntityCacheBzService;

import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 */
public class CountryListBzServiceJSONImpl extends RestBaseServerResource implements CountryListBzService {

	private static final Logger log = Logger.getLogger(CountryListBzServiceJSONImpl.class.getName());

	@Autowired
	private GeoCodingHelper geocoder;
	@Autowired
	private GeoEntityDAO geoDao;
	@Autowired
	private UserDAO userDao;
	@Autowired
	private UserEntityCacheBzService uecService;
	@Autowired
	private SystemConfiguration systemConfiguration;

	@Override
	public String retrieve(final JsonRepresentation entity) {

		long start = markStart();
		JSONObject returnValue = null;
		try {
			List<String> countries = CollectionFactory.createList();
			String current = null;
			final JSONObject obj = entity == null ? new JSONObject() : entity.getJsonObject();
			UserEntityCache uec = uecService.getCountryList();
			countries.addAll(uec.getEntities());

			try {
				if( obj.has("lat") ) {
					GeoPoint p1 = geocoder.getGeoPoint(
							obj.getDouble("lat"),
							obj.getDouble("lon"));

					Shopping nearest = geoDao.getNearestShopping(new GeoPoint(
							p1.getLat(), p1.getLon(), geocoder
							.encodeGeohash(p1.getLat(),
									p1.getLon())));
					if( nearest != null ) {
						current = nearest.getAddress().getCountry();
					}
				} else if( obj.has("user")){
					User u = userDao.get(obj.getString("user"), true);
					current = u.getViewLocation().getCountry();
				}
				if( current != null ) {
					countries.remove(current);
				}

			} catch( ASException e ) {
				if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE) {
					current = systemConfiguration.getDefaultCountry();
					countries.remove(current);
				}
			}

			long millisPre = new Date().getTime();
			long diff = new Date().getTime() - millisPre;

			// Logs the result
			log.info("Number of countries found [" + countries.size() + "] in " + diff + " millis");
			returnValue = new JSONObject();
			returnValue.put("countries", countries);
			returnValue.put("current", current);

		} catch (ASException e) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE || 
					e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE) {
				log.log(Level.INFO, e.getMessage());
			} else {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			returnValue = getJSONRepresentationFromException(e);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			returnValue = this.getJSONRepresentationFromException(e);
		} finally {
			markEnd(start);  
		}
		return returnValue.toString();
	}
}
