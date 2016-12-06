package mobi.allshoppings.bz.spi;


import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bz.APDeviceSignalBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.APDeviceSignalDAO;
import mobi.allshoppings.dao.APHotspotDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDeviceSignal;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.model.User;

/**
 *
 */
public class APDeviceSignalBzServiceJSONImpl extends RestBaseServerResource implements APDeviceSignalBzService {

	private static final Logger log = Logger.getLogger(APDeviceSignalBzServiceJSONImpl.class.getName());

	@Autowired
	private APHotspotDAO aphDao;

	@Autowired
	private APDeviceSignalDAO apdsDao;

	/**
	 * Obtains information about a brand
	 * 
	 * @return A JSON representation of the selected fields for a brand
	 */
	@Override
	public String retrieve() {

		BzFields bzFields = BzFields.getModelBzFields(APHotspot.class);
		
		long start = markStart();
		JSONObject returnValue;
		try {
			// get parameters
			String hostname = this.obtainStringValue("hostname", "");
			String mac = this.obtainStringValue("mac", "");

			// get level, if not defined use default value
			String level = this.obtainStringValue(LEVEL, BzFields.LEVEL_LIST);

			// Retrieve the selected object
			long millisPre = new Date().getTime();
			APHotspot obj = aphDao.getLastUsingHostnameAndMac(hostname, mac);
			long diff = new Date().getTime() - millisPre;

			// Logs the result
			log.info("APHotspot found in " + diff + " millis");
			returnValue = this.getJSONRepresentationFromObject(obj, this.obtainOutputFields(bzFields, level));
			
			// track action
			trackerHelper.enqueue((User)null, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.APDeviceSignalBzService"),
					null, null);

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

	@Override
	public String put(final JsonRepresentation entity) {

		JSONObject returnValue = null;
		BzFields bzFields = BzFields.getModelBzFields(APDeviceSignal.class);

		long start = markStart();
		try {
			JSONObject obj = entity != null ? entity.getJsonObject() : new JSONObject();
			String hostname = obj.getString("hostname");
			String mac = obj.getString("mac");
			String deviceUUID = obj.getString("deviceUUID");
			Boolean insideStore = obj.getBoolean("insideStore");
			Integer distance = obj.getInt("distance");

			APHotspot aph = null;
			Date markDate = new Date();
			boolean done = false;
			boolean timeout = false;

			while(!done) {
				try {
					aph = aphDao.getLastUsingHostnameAndMac(hostname, mac);
				} catch( ASException e ) {}
				if( aph != null && aph.getLastSeen().after(markDate)) {
					done = true;
					timeout = false;
				} else {
					Thread.sleep(1000);
					long time = new Date().getTime() - markDate.getTime();
					if( time > 60000 ) {
						done = true;
						timeout = true;
					}
				}
			}

			if( timeout ) throw ASExceptionHelper.defaultException("Timeout Exceeded", null);

			APDeviceSignal apds = new APDeviceSignal();
			apds.setHostname(hostname);
			apds.setDeviceUUID(deviceUUID);
			apds.setMac(mac);
			apds.setInsideStore(insideStore);
			apds.setRssi(aph.getSignalDB());
			apds.setDistance(distance);
			apds.setKey(apdsDao.createKey());
			apdsDao.create(apds);

			returnValue = getJSONRepresentationFromObject(apds, this.obtainOutputFields(bzFields, BzFields.LEVEL_ALL));

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
			returnValue = getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e));
		} finally {
			markEnd(start);
		}

		return returnValue.toString();
	}


}
