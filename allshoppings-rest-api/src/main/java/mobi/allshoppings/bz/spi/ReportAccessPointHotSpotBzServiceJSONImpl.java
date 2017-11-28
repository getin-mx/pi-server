package mobi.allshoppings.bz.spi;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.apdevice.APHHelper;
import mobi.allshoppings.bz.ReportAccessPointHotSpotBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.APHotspotDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APHotspot;

/**
 * Receives and stores data from antennas.
 */
public class ReportAccessPointHotSpotBzServiceJSONImpl extends RestBaseServerResource
		implements ReportAccessPointHotSpotBzService {

	private static final Logger log = Logger.getLogger(
			ReportAccessPointHotSpotBzServiceJSONImpl.class.getName());
	
	private final Calendar CALENDAR = Calendar.getInstance();

	@Autowired
	private APDeviceDAO apdDao;
	@Autowired
	private APHotspotDAO dao;
	@Autowired
	private APHHelper aphHelper;

	@Override
	public String post(final JsonRepresentation entity) {

		long start = markStart();
		try {
			final JSONObject obj = entity.getJsonObject();
			String hostname = obj.getString("hostname");

			JSONArray data = obj.getJSONArray("data");

			log.log(Level.INFO, "Reporting " + data.length() + " AP Members from "
					+ hostname);

			// Sets the device last data
			APDevice device = null;
			try {
				device = apdDao.get(hostname, true);
				device.completeDefaults();
				device.setLastRecordDate(new Date());
				device.setLastRecordCount(data.length());
				apdDao.update(device);
			} catch( ASException e ) {
				if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE )
					throw e;
				
				device = new APDevice();
				device.setHostname(hostname);
				device.setKey(apdDao.createKey(hostname));
				device.setLastRecordDate(new Date());
				device.setLastRecordCount(data.length());
				apdDao.create(device);
			}

			for( int i = 0; i < data.length(); i++ ) {

				try {
					JSONObject ele = (JSONObject)data.get(i);

					APHotspot aphotspot = new APHotspot();
					aphotspot.setHostname(hostname);
					aphotspot.setFirstSeen(restoreDate(
							ele.getLong("firstSeen") * 1000));
					aphotspot.setLastSeen(restoreDate(ele.getLong("lastSeen") * 1000));
					aphotspot.setMac(ele.getString("mac").toLowerCase());
					aphotspot.setSignalDB(ele.getInt("signalDB"));
					aphotspot.setCount(ele.getInt("count"));
					aphotspot.setKey(dao.createKey());

					if(aphHelper.isValidMacAddress(aphotspot.getMac()) && 
							aphotspot.getSignalDB() < 0) {
						dao.create(aphotspot);
					}
					
				} catch( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
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
	
	private Date restoreDate(long time) {
		CALENDAR.clear();
		CALENDAR.setTimeInMillis(time);
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		int yearsDiff = Math.abs(CALENDAR.get(Calendar.YEAR)
				-now.get(Calendar.YEAR));
		int monthsDiff = Math.abs(CALENDAR.get(Calendar.MONTH)
				-now.get(Calendar.MONTH));
		int daysDiff = Math.abs(CALENDAR.get(Calendar.DATE)
				-now.get(Calendar.DATE));
		if(yearsDiff > 1 || (yearsDiff != 0 &&
				(now.get(Calendar.MONTH) > 0 ||
				now.get(Calendar.DATE) > 1
				|| now.get(Calendar.HOUR_OF_DAY) > 2)))
			CALENDAR.set(Calendar.YEAR, now.get(Calendar.YEAR));
		if(monthsDiff > 1 || (monthsDiff != 0 &&
				(now.get(Calendar.DATE) > 1 ||
				now.get(Calendar.HOUR_OF_DAY) > 2)))
			CALENDAR.set(Calendar.MONTH, now.get(Calendar.MONTH));
		if(daysDiff > 1 || (daysDiff != 0 &&
				now.get(Calendar.HOUR_OF_DAY) > 2))
			CALENDAR.set(Calendar.DATE, now.get(Calendar.DATE));
		return CALENDAR.getTime();
	}
	
}
