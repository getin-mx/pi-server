package mobi.allshoppings.bz.spi;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.bz.RequestDeviceLocationBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.push.PushMessageHelper;
import mobi.allshoppings.tools.CollectionFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class RequestDeviceLocationBzServiceJSONImpl
extends RestBaseServerResource
implements RequestDeviceLocationBzService {

	private static final Logger log = Logger.getLogger(RequestDeviceLocationBzServiceJSONImpl.class.getName());

	@Autowired
	private PushMessageHelper pushHelper;


	@Override
	public String post(final JsonRepresentation entity) {

		long start = markStart();
		try {
			final JSONObject obj = entity.getJsonObject();
			obtainUserIdentifier(false);

			// Obtain Device List
			JSONArray arr = obj.getJSONArray("deviceList");
			List<String> devices = CollectionFactory.createList();
			for( int i = 0; i < arr.length(); i++ ) {
				String deviceUUID = arr.getString(i);
				devices.add(deviceUUID);
			}
			pushHelper.requestLocation(devices);
			
			return generateJSONOkResponse().toString();

		} catch (JSONException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(e).toString();
		} finally {
			markEnd(start);
		}
	}

}
