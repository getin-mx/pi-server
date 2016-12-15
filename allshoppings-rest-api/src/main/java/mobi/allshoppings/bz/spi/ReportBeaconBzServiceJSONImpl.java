package mobi.allshoppings.bz.spi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bz.ReportBeaconBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.BeaconHotspotDAO;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.lock.LockHelper;
import mobi.allshoppings.model.BeaconHotspot;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.DeviceMessageLock;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;

/**
 *
 */
public class ReportBeaconBzServiceJSONImpl extends RestBaseServerResource implements ReportBeaconBzService {

	private static final Logger log = Logger.getLogger(ReportBeaconBzServiceJSONImpl.class.getName());
	private BzFields bzFields = BzFields.getBzFields(getClass());

	@Autowired
	private BeaconHotspotDAO dao;
	@Autowired
	private DeviceInfoDAO deviceInfoDao;
	@Autowired
	private LockHelper lockHelper;
	@Autowired
	private SystemConfiguration systemConfiguration;
	
	@Override
	public String post(final JsonRepresentation entity) {

		long start = markStart();
		try {
			final JSONObject obj = entity.getJsonObject();
			this.getUserFromToken();

			//check mandatory fields
			log.info("check mandatory fields");
			if (!hasDefaultParameters(obj, bzFields.DEFAULT_FIELDS.toArray(EMPTY_STRING_ARRAY))) {
				throw ASExceptionHelper.invalidArgumentsException(INVALID_DEFAULT_FIELDS);
			}

			//creates the lock object
			BeaconHotspot bh = new BeaconHotspot();
			setPropertiesFromJSONObject(obj, bh, bzFields.READONLY_FIELDS);
			try {
				DeviceInfo deviceInfo = deviceInfoDao.get(bh.getDeviceUUID());
				bh.setUserId(deviceInfo.getUserId());
			} catch( Exception e ) {}
			bh.setKey(dao.createKey());

			dao.create(bh);

			// This is a quick and dirty implementation
			// FIXME: Add subEntityId and subEntityKind
			lockHelper.deviceMessageLock(bh.getDeviceUUID(),
					DeviceMessageLock.SCOPE_PROMOTIONS, null, null,
					systemConfiguration.getDefaultProximityLock(), null, null);
			
			// track action
			trackerHelper.enqueue( (User)null, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.ReportBeacon.put"), 
					null, null);

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
