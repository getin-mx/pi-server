package mobi.allshoppings.bz.spi;

import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.bz.DeviceMessageBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.push.PushMessageHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 */
public class DeviceMessageBzServiceJSONImpl
extends RestBaseServerResource
implements DeviceMessageBzService {

	private static final Logger log = Logger.getLogger(DeviceMessageBzServiceJSONImpl.class.getName());
	
	@Autowired
	DeviceInfoDAO deviceInfoDao;
	@Autowired
	PushMessageHelper pushHelper;

    @Override
	public String post(final JsonRepresentation entity) {

		long start = markStart();
		try {

			log.info("device message start");
			final JSONObject obj = entity.getJsonObject();
			
			DeviceInfo device = deviceInfoDao.get(obj.getString("deviceUUID"));
			pushHelper.sendMessage(obj.getString("title"), obj.getString("message"), obj.getString("url"), device);

			log.info("device message end");
			return generateJSONOkResponse().toString();

		} catch (JSONException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} catch (ASException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(e).toString();
		} finally {
			markEnd(start);
		}

	}
}
