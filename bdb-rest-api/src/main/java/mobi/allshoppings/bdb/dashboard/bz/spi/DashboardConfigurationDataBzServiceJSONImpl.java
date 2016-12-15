package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.DashboardConfigurationDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardConfiguration;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.User;


/**
 *
 */
public class DashboardConfigurationDataBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(DashboardConfigurationDataBzServiceJSONImpl.class.getName());

	@Autowired
	private DashboardConfigurationDAO dao;

	/**
	 * Obtains a list of FloorMap points
	 * 
	 * @return A JSON representation of the selected fields for a FloorMap
	 */
	@Override
	public String retrieve()
	{
		long start = markStart();
		JSONObject returnValue = null;
		try {
			// obtain the id and validates the auth token
			@SuppressWarnings("unused")
			User user = getUserFromToken();
			long diff = 0;
			
			long millisPre = new Date().getTime();

			String entityId = obtainStringValue("entityId", null);
			Integer entityKind = obtainIntegerValue("entityKind", EntityKind.KIND_BRAND);

			DashboardConfiguration obj = new DashboardConfiguration(entityId, entityKind);
			try {
				obj = dao.getUsingEntityIdAndEntityKind(entityId, entityKind, true);
			} catch( Exception e ) {}
			
			diff = new Date().getTime() - millisPre;

			// Logs the result
			log.info("DashboardConfiguration built in " + diff + " millis");

			returnValue = this.getJSONRepresentationFromObject(obj,
					this.obtainOutputFields(DashboardConfiguration.class));
			
			return returnValue.toString();

		} catch (ASException e) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE || 
					e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE) {
				log.log(Level.INFO, e.getMessage());
			} else {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			return getJSONRepresentationFromException(e).toString();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} finally {
			markEnd(start);
		}
	}
	
}
