package mobi.allshoppings.bz.spi;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bz.DeviceMessageLockBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.DeviceMessageLockDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.lock.LockHelper;
import mobi.allshoppings.model.DeviceMessageLock;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.adapter.IGenericAdapter;


/**
 *
 */
public class DeviceMessageLockBzServiceJSONImpl
extends RestBaseServerResource
implements DeviceMessageLockBzService {

	private static final Logger log = Logger.getLogger(DeviceMessageLockBzServiceJSONImpl.class.getName());

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private LockHelper lockHelper;
	@Autowired
	private DeviceMessageLockDAO dao;

	private BzFields bzFields = BzFields.getBzFields(getClass());

    @Override
    public String retrieve() {
    	
    	long start = markStart();
		JSONObject returnValue = null;
		try {
			List<DeviceMessageLock> list = new ArrayList<DeviceMessageLock>();
			
			// validate authToken
			User user = this.getUserFromToken();

			// get level, if not defined use default value
			String level = this.obtainStringValue(LEVEL, BzFields.LEVEL_LIST);
			
			// Get Device UUID
			String deviceUUID = this.obtainStringValue(DEVICE_UUID, null);
			
			// retrieve all locks
			long millisPre = new Date().getTime();

			// and the coupon list
			list = dao.getUsingDeviceAndScopeAndCampaign(deviceUUID, (byte) -1, null); 
			long diff = new Date().getTime() - millisPre;

			// Logs the result
			log.info("Number of locks found [" + list.size() + "] in " + diff + " millis");
			returnValue = this.getJSONRepresentationFromArrayOfObjects(	list, this.obtainOutputFields(bzFields, level));
			
			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.DeviceMessageLockBzService.get"),
					deviceUUID, null);

		} catch (ASException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			returnValue = this.getJSONRepresentationFromException(e);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			returnValue = this.getJSONRepresentationFromException(e);
		} finally {
			markEnd(start);
		}
		return returnValue.toString();
    }

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
			
			//creates the lock object
			DeviceMessageLockAdapter lock = new DeviceMessageLockAdapter();
			setPropertiesFromJSONObject(obj, lock, bzFields.READONLY_FIELDS);
			if( obj.has("duration")) lock.setDuration(obj.getLong("duration"));
			if( obj.has("ticketsDate")) {
				try {
					String sTicketDate = obj.getString("ticketsDate");
					String parts[] = sTicketDate.split(" ");
					lock.setFromDate(sdf.parse(parts[0]));
				} catch( Exception e ) {
					log.log(Level.SEVERE, "Bad Format Date: " + e.getMessage(), e);
				}
			}
			if( lock.getDeviceId() == null ) lock.setDeviceId(obj.getString("deviceUUID"));

			// calls the lock procedure
			lockHelper.deviceMessageLock(lock.getDeviceId(), lock.getScope(), lock.getCampaignActivityId(), lock.getFromDate(), 
					lock.getDuration(), lock.getSubEntityId(), lock.getSubEntityKind());

			// track action
			trackerHelper.enqueue( userId, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.DeviceMessageLockBzService.put"), 
					null, null);

			log.info("device message lock end");
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
	public class DeviceMessageLockAdapter extends DeviceMessageLock implements IGenericAdapter {
		private static final long serialVersionUID = 5689490650021534905L;
		private long duration = 0;

		/**
		 * @return the duration
		 */
		public long getDuration() {
			return duration;
		}

		/**
		 * @param duration the duration to set
		 */
		public void setDuration(long duration) {
			this.duration = duration;
		}
		
		/**
		 * @param duration the duration to set
		 */
		public void setDuration(String duration) {
			this.duration = Long.parseLong(duration);
		}
	}
}
