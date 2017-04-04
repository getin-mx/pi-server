package mobi.allshoppings.bz.spi;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.apdevice.APHHelper;
import mobi.allshoppings.apdevice.IAPDeviceTrigger;
import mobi.allshoppings.bz.ReportAccessPointHotSpotBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.APDeviceTriggerEntryDAO;
import mobi.allshoppings.dao.APHEntryDAO;
import mobi.allshoppings.dao.APHotspotDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APDeviceTriggerEntry;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.model.APHotspot;

/**
 *
 */
public class ReportAccessPointHotSpotBzServiceJSONImpl extends RestBaseServerResource implements ReportAccessPointHotSpotBzService {

	private static final Logger log = Logger.getLogger(ReportAccessPointHotSpotBzServiceJSONImpl.class.getName());

	@Autowired
	private APDeviceDAO apdDao;
	@Autowired
	private APHotspotDAO dao;
//	@Autowired
//	private APDeviceHelper apdHelper;
	@Autowired
	private APDeviceTriggerEntryDAO triggerDao;
	@Autowired
	private APHHelper aphHelper;
	@Autowired
	private APHEntryDAO apheDao;

	@Override
	public String post(final JsonRepresentation entity) {

		long start = markStart();
		try {
			final JSONObject obj = entity.getJsonObject();
			String hostname = obj.getString("hostname");

			JSONArray data = obj.getJSONArray("data");

			log.log(Level.INFO, "Reporting " + data.length() + " AP Members from " + hostname);
			log.log(Level.FINEST, obj.toString());

			for( int i = 0; i < data.length(); i++ ) {

				try {
					JSONObject ele = (JSONObject)data.get(i);

					APHotspot aphotspot = new APHotspot();
					aphotspot.setHostname(hostname);
					aphotspot.setFirstSeen(new Date(ele.getLong("firstSeen") * 1000));
					aphotspot.setLastSeen(new Date(ele.getLong("lastSeen") * 1000));
					aphotspot.setMac(ele.getString("mac").toLowerCase());
					aphotspot.setSignalDB(ele.getInt("signalDB"));
					aphotspot.setCount(ele.getInt("count"));
					aphotspot.setKey(dao.createKey());

					if(!aphotspot.getMac().startsWith("broadcast") && !aphotspot.getMac().equals("00:00:00:00:00:00") && !aphotspot.getMac().contains(" ") && aphotspot.getSignalDB() < 0) {
						dao.create(aphotspot);

						// Updates APHEntries
						try {
							aphHelper.setUseCache(false);
							APHEntry aphe = aphHelper.setFramedRSSI(aphotspot);
							aphHelper.artificiateRSSI(aphe);
							if( StringUtils.hasText(aphe.getIdentifier())) {
								apheDao.update(aphe);
							} else {
								aphe.setKey(apheDao.createKey(aphe));
								apheDao.create(aphe);
							}
						} catch( Exception e ) {
							log.log(Level.SEVERE, "Error updating APHEntries", e);
						}

						// Try to Execute Triggers
						try {
							List<APDeviceTriggerEntry> triggers = triggerDao.getUsingCoincidence(hostname, aphotspot.getMac());
							for(APDeviceTriggerEntry entry : triggers ) {
								try {
									IAPDeviceTrigger trigger = (IAPDeviceTrigger)Class.forName(entry.getTriggerClassName()).newInstance();
									trigger.execute(hostname, aphotspot.getMac(), aphotspot.getSignalDB(), entry.getTriggerMetadata());
								} catch( Throwable e ) {
									log.log(Level.SEVERE, "Error excecuting APDevice Triggers", e);
								}
							}
						} catch( ASException e ) {
							log.log(Level.SEVERE, "Error excecuting APDevice Triggers", e);
						}

						// Creates locks if needed
						// TODO: This should be moved to a trigger
//						try {
//							List<DeviceInfo> diList = diDao.getUsingMAC(aphotspot.getMac());
//							for(DeviceInfo di : diList ) {
//								log.log(Level.WARNING, "Adding lock for device " + di.getDeviceUUID() );
//								//FIXME: Add subEntityId and subEntityKind
//								lockHelper.deviceMessageLock(di.getDeviceUUID(), DeviceMessageLock.SCOPE_GLOBAL, null, 
//										now, systemConfiguration.getDefaultProximityLock(), null, null);
//							}
//						} catch ( ASException e ) {
//							if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE )
//								throw e;
//						}

//						try {
//							replicateRecord(aphotspot);
//						} catch( Exception e ) {
//							log.log(Level.WARNING, e.getMessage(), e);
//						}
					}
					
				} catch( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}

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
			
//			// Submits a check for the antennas
//			final String deviceHostname = device.getHostname();
//			if(device.getLastInfoUpdate() == null 
//					|| ( new Date().getTime() - device.getLastInfoUpdate().getTime()) > 14400000) {
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						try {
//							apdHelper.updateAPDeviceInfo(deviceHostname);
//						} catch (ASException e) {
//							log.log(Level.SEVERE, e.getMessage(), e);
//						}
//					}
//				}).start();
//			}
			
			// And sends the return message
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
