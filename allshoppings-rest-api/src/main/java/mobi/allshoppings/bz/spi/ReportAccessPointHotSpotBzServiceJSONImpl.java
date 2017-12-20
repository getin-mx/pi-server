package mobi.allshoppings.bz.spi;

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
import mx.getin.dao.APDCalibrationDAO;
import mx.getin.dao.APDReportDAO;
import mx.getin.model.APDCalibration;
import mx.getin.model.APDReport;

/**
 * Receives and stores data from antennas.
 */
public class ReportAccessPointHotSpotBzServiceJSONImpl extends RestBaseServerResource
		implements ReportAccessPointHotSpotBzService {

	private static final Logger log = Logger.getLogger(
			ReportAccessPointHotSpotBzServiceJSONImpl.class.getName());
	
	@Autowired
	private APDReportDAO apdrDao;
	@Autowired
	private APHotspotDAO dao;
	@Autowired
	private APHHelper aphHelper;
	@Autowired
	private APDeviceDAO apdDao;
	@Autowired
	private APDCalibrationDAO apdcDao;

	@Override
	public String post(final JsonRepresentation entity) {

		long start = markStart();
		try {
			final JSONObject obj = entity.getJsonObject();
			String hostname = obj.getString("hostname");

			JSONArray data = obj.getJSONArray("data");

			log.log(Level.INFO, "Reporting " + data.length() + " AP Members from " + hostname);

			// Sets the device last data
			APDReport report = null;
			try {
				report = apdrDao.get(hostname, true);
				report.setLastRecordDate(new Date());
				report.setLastRecordCount(data.length());
				apdrDao.update(report);
			} catch( ASException e ) {
				if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE )
					throw e;
				APDevice device = new APDevice();
				device.setHostname(hostname);
				device.setKey(apdDao.createKey(hostname));
				apdDao.create(device);
				report = new APDReport();
				report.setLastRecordDate(new Date());
				report.setLastRecordCount(data.length());
				report.setHostname(hostname);
				report.setKey(apdrDao.createKey(hostname));
				APDCalibration cal = new APDCalibration();
				cal.setHostname(hostname);
				cal.setKey(apdcDao.createKey(hostname));
			}

			for( int i = 0; i < data.length(); i++ ) {

				try {
					JSONObject ele = (JSONObject)data.get(i);

					APHotspot aphotspot = new APHotspot();
					aphotspot.setHostname(hostname);
					aphotspot.setMac(ele.getString("mac").toLowerCase());
					aphotspot.setSignalDB((short) ele.getInt("signalDB"));
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
	
}
