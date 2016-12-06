package mobi.allshoppings.bz.spi;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.bz.ClearMessageLockBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.lock.LockHelper;
import mobi.allshoppings.model.DeviceMessageLock;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 */
public class ClearMessageLockBzServiceJSONImpl
extends RestBaseServerResource
implements ClearMessageLockBzService {

	private static final Logger log = Logger.getLogger(ClearMessageLockBzServiceJSONImpl.class.getName());

	@Autowired
	private LockHelper lockHelper;

	private BzFields bzFields = BzFields.getBzFields(getClass());

	@Override
	public String post(final JsonRepresentation entity) {

		long start = markStart();
		try {

			String userId = obtainUserIdentifier(false);
			final JSONObject obj = entity.getJsonObject();

			//check mandatory fields
			log.info("check mandatory fields");
			if (!hasDefaultParameters(obj, bzFields.DEFAULT_FIELDS.toArray(EMPTY_STRING_ARRAY))) {
				throw ASExceptionHelper.invalidArgumentsException(INVALID_DEFAULT_FIELDS);
			}
			
			//drop the lock object
			DeviceMessageLockAdapter lock = new DeviceMessageLockAdapter();
			setPropertiesFromJSONObject(obj, lock, bzFields.READONLY_FIELDS);
			if( lock.getDeviceId() == null ) lock.setDeviceId(obj.getString("deviceUUID"));

			// calls the lock procedure
			lockHelper.clearLocks(lock.getDeviceId(), lock.getScope(), lock.getCampaignActivityId());

			// track action
			trackerHelper.enqueue( userId, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.ClearLockBzService.put"), 
					null, null);

			log.info("clear device message lock end");
			return generateJSONOkResponse().toString();

		} catch (JSONException | IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} catch (ASException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(e).toString();
		} finally {
			markEnd(start);
		}

	}

	// Device message lock adapter
	class DeviceMessageLockAdapter extends DeviceMessageLock {
		private static final long serialVersionUID = 5689490650021534905L;
	}
}
