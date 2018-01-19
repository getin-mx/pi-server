package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.FloorMapJourneyDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.FloorMapJourney;
import mobi.allshoppings.tools.Range;


/**
 *
 */
public class FloorMapJourneyDataBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(FloorMapJourneyDataBzServiceJSONImpl.class.getName());

//	@Autowired
//	private FloorMapJourneyHelper helper;
	@Autowired
	private FloorMapJourneyDAO dao;
	

	/**
	 * Obtains a Dashboard report prepared to form a User Journey graph
	 * 
	 * @return A JSON representation of the selected graph
	 */
	@Override
	public String retrieve()
	{
		JSONObject returnValue = null;
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			obtainUserIdentifier(true);

			String floorMapId = obtainStringValue("floormapId", null);
			String mac = obtainStringValue("mac", null);
			String fromDate = obtainStringValue("fromStringDate", null);
			String toDate = obtainStringValue("toStringDate", null);
			byte fromRange = obtainByteValue("fromRange", (byte) -1);
			byte toRange = obtainByteValue("toRange", (byte) -1);

			if( fromRange < 0) 
				fromRange = 0;
			
			if( toRange < 0)
				toRange = 20;
			
			Range range = new Range(fromRange,toRange);
			
			List<FloorMapJourney> list = dao.getUsingFloorMapAndMacAndDate(floorMapId, mac, fromDate, toDate, range, "wordLength DESC");
			
			
			returnValue = this.getJSONRepresentationFromArrayOfObjects(
					list, this.obtainOutputFields(FloorMapJourney.class));

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
