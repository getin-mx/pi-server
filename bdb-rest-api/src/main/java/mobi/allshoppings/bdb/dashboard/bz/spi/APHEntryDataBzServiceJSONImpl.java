package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.apdevice.APHHelper;
import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class APHEntryDataBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(APHEntryDataBzServiceJSONImpl.class.getName());
	
	@Autowired
	private APHHelper aphHelper;

	/**
	 * Obtains a Dashboard report prepared to form a APHEntry graph
	 * 
	 * @return A JSON representation of the selected graph
	 */
	@Override
	public String retrieve()
	{
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			obtainUserIdentifier(true);

			String hostnameList = obtainStringValue("hostnames", null);
			String mac = obtainStringValue("mac", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			Boolean original = obtainBooleanValue("original", false);
//			String toStringDate = obtainStringValue("toStringDate", null);

			int fromHour = 0;
			int toHour = 4320;
			try { 
				String fromStringHour = obtainStringValue("fromStringHour", null);
				fromHour = aphHelper.stringToOffsetTime(fromStringHour); 
			} catch( Exception e ) {}
			try { 
				String toStringHour = obtainStringValue("toStringHour", null);
				toHour = aphHelper.stringToOffsetTime(toStringHour); 
			} catch( Exception e ) {}
			
			String[] hostnames = hostnameList.split(",");
			List<APHEntry> entries = CollectionFactory.createList();
			for( String hostname : hostnames ) {
				entries.add(aphHelper.getFromCache(hostname, mac, fromStringDate));
			}

			JSONArray series = new JSONArray();
			for( APHEntry entry : entries ) {
				JSONObject serie = new JSONObject();
				JSONArray data = new JSONArray();
				Map<String, Integer> candidate = (entry.getArtificialRssi().size() > 0 && !original) ? entry.getArtificialRssi() : entry.getRssi();
				for(int i = fromHour; i < toHour; i++) {
					if( candidate.containsKey(String.valueOf(i))) {
						data.put(candidate.get(String.valueOf(i)));
					} else {
						data.put((Integer)null);
					}
				}
				serie.put("data", data);
				serie.put("name", entry.getHostname());
				serie.put("type", "spline");
				series.put(serie);
			}
			
			JSONArray categories = new JSONArray();
			for(int i = fromHour; i < toHour; i++) {
				categories.put(aphHelper.slotToTime(i));
			}
			
			// Returns the final value
			JSONObject ret = new JSONObject();
			ret.put("series", series);
			ret.put("categories", categories);
			return ret.toString();
			
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
