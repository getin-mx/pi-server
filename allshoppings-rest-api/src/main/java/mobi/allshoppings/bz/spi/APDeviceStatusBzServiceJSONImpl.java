package mobi.allshoppings.bz.spi;


import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bz.APDeviceStatusBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

/**
 *
 */
public class APDeviceStatusBzServiceJSONImpl extends RestBaseServerResource implements APDeviceStatusBzService {

	private static final Logger log = Logger.getLogger(APDeviceStatusBzServiceJSONImpl.class.getName());

	@Autowired
	private APDeviceDAO apdDao;
	
	private BzFields bzFields = BzFields.getBzFields(getClass());

	/**
	 * Obtains information about a brand
	 * 
	 * @return A JSON representation of the selected fields for a brand
	 */
	@Override
	public String retrieve() {
		
		long start = markStart();
		JSONObject returnValue;
		try {
			List<APDevice> list = CollectionFactory.createList();

			// get level, if not defined use default value
			String level = this.obtainStringValue(LEVEL, BzFields.LEVEL_LIST);
			Range range = this.obtainRange();

			// Get Search query
			String q = this.obtainStringValue(Q, null);

			// Additional Search Fields
			Map<String, String> additionalFields = CollectionFactory.createMap();

			// retrieve all brands
			long millisPre = new Date().getTime();
			if( q != null && !q.trim().equals("")) {
				list = apdDao
						.getUsingIndex(APDevice.class.getName(), q, null,
								null, null, additionalFields, null, null);
			} else {
				list = apdDao.getUsingRange(range);
				Collections.sort(list, new APDeviceComparator());
			}
			long diff = new Date().getTime() - millisPre;

			// Logs the result
			log.info("Number of Access Point Devices found [" + list.size() + "] in " + diff + " millis");
			returnValue = this.getJSONRepresentationFromArrayOfObjects(list, this.obtainOutputFields(bzFields, level));
			
			// track action
			trackerHelper.enqueue((User)null, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.APDeviceStatusBzService"),
					q, null);

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
	
	public class APDeviceComparator implements Comparator<APDevice> {

		@Override
		public int compare(APDevice o1, APDevice o2) {
			return o1.getHostname().compareTo(o2.getHostname());
		}
	}

}
