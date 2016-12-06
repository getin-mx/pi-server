package mobi.allshoppings.bz.spi;

import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.bz.DeviceWifiLocationBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.DeviceWifiLocationHistoryDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.HotspotService;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.DeviceWifiLocationHistory;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.task.QueueTaskHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import com.inodes.datanucleus.model.Text;

/**
 *
 */
public class DeviceWifiLocationBzServiceJSONImpl
extends RestBaseServerResource
implements DeviceWifiLocationBzService {

	private static final Logger log = Logger.getLogger(DeviceWifiLocationBzServiceJSONImpl.class.getName());

	@Autowired
	private DeviceWifiLocationHistoryDAO dao;
	@Autowired
	private DeviceInfoDAO deviceInfoDao;
	@Autowired
	private HotspotService hotspotService;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private QueueTaskHelper queueHelper;

	private BzFields bzFields = BzFields.getBzFields(getClass());

	@Override
	public String post(final JsonRepresentation entity) {

		long start = markStart();
		try {
			final JSONObject obj = entity.getJsonObject();

			//check mandatory fields
			log.info("check mandatory fields");
			if (!hasDefaultParameters(obj, bzFields.DEFAULT_FIELDS.toArray(EMPTY_STRING_ARRAY))) {
				throw ASExceptionHelper.invalidArgumentsException(INVALID_DEFAULT_FIELDS);
			}

			DeviceWifiLocationHistory device = new DeviceWifiLocationHistory();
			device.setKey(dao.createKey());
            setPropertiesFromJSONObject(obj, device, bzFields.READONLY_FIELDS);
            device.setWifiData(new Text(obj.get("values").toString()));
            if( device.getDeviceUUID() != null ) {
            	try {
            		DeviceInfo di = deviceInfoDao.get(device.getDeviceUUID(), false);
            		device.setUserId(di.getUserId());
            	} catch( ASException e1 ) {
            		log.log(Level.FINE, "Device Information not found for " + device.getDeviceUUID(), e1);
            	}
            }

            // Try to calculate the location within a mall
            try {
            	hotspotService.calculateWifiSpot(device);
            } catch( Exception e ) {
            	log.log(Level.SEVERE, e.getMessage(), e);
            }

            // Save the object
            if( systemConfiguration.isEnqueueHistoryReplicableObjects() ) {
            	queueHelper.enqueueTransientInReplica(device);
            } else {
            	dao.create(device);
            }
            
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
