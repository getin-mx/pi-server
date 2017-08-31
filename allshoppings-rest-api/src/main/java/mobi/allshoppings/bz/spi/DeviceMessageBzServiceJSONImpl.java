package mobi.allshoppings.bz.spi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.DeviceMessageBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.User;
import mobi.allshoppings.push.PushMessageHelper;


/**
 *
 */
public class DeviceMessageBzServiceJSONImpl
extends RestBaseServerResource
implements DeviceMessageBzService {

	private static final Logger log = Logger.getLogger(DeviceMessageBzServiceJSONImpl.class.getName());

	private static final String DEFAULT_APP = "amazing_mx";
	
	@Autowired
	DeviceInfoDAO deviceInfoDao;
	@Autowired
	UserDAO userDao;
	@Autowired
	PushMessageHelper pushHelper;

    @Override
	public String post(final JsonRepresentation entity) {

		long start = markStart();
		try {

			log.info("device message start");
			final JSONObject obj = entity.getJsonObject();

			if( obj.has("deviceUUID") && StringUtils.hasText(obj.getString("deviceUUID"))) {
				DeviceInfo device = deviceInfoDao.get(obj.getString("deviceUUID"));
				pushHelper.sendMessage(obj.getString("title"), obj.getString("message"), obj.getString("url"), device);
			} else if( obj.has("userId") && StringUtils.hasText(obj.getString("userId"))) {
				User user = userDao.get(obj.getString("userId"));
				pushHelper.sendMessage(DEFAULT_APP, user, obj.getString("title"), obj.getString("message"), obj.getString("url"));
			} else {
				pushHelper.sendBulkMessage(DEFAULT_APP, obj.getString("title"), obj.getString("message"), obj.getString("url"));
			}
			
			
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
