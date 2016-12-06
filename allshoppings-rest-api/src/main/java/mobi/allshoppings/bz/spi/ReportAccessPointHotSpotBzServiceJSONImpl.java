package mobi.allshoppings.bz.spi;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import mobi.allshoppings.apdevice.APDeviceHelper;
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
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.task.QueueTaskHelper;

/**
 *
 */
@SuppressWarnings("deprecation")
public class ReportAccessPointHotSpotBzServiceJSONImpl extends RestBaseServerResource implements ReportAccessPointHotSpotBzService {

	private static final Logger log = Logger.getLogger(ReportAccessPointHotSpotBzServiceJSONImpl.class.getName());
	private static final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	private APDeviceDAO apdDao;
	@Autowired
	private APHotspotDAO dao;
//	@Autowired
//	private DeviceInfoDAO diDao;
//	@Autowired
//	private LockHelper lockHelper;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private QueueTaskHelper queueHelper;
	@Autowired
	private APDeviceHelper apdHelper;
	@Autowired
	private APDeviceTriggerEntryDAO triggerDao;
	@Autowired
	private APHHelper aphHelper;
	@Autowired
	private APHEntryDAO apheDao;

	private static RestTemplate restTemplate;
	private static HttpHeaders requestHeaders;
	
	static {
		requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		requestHeaders.setAccept(Arrays.asList( new MediaType[] {MediaType.APPLICATION_JSON}));

		restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().clear();
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		
	    sdfTime.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Override
	public String post(final JsonRepresentation entity) {

		long start = markStart();
		try {
			final JSONObject obj = entity.getJsonObject();
			String hostname = obj.getString("hostname");

			JSONArray data = obj.getJSONArray("data");

			log.log(Level.INFO, "Reporting " + data.length() + " AP Members from " + hostname);
			log.log(Level.FINEST, obj.toString());

//			Date now = new Date();

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

					if(!aphotspot.getMac().startsWith("broadcast")) {

						if(systemConfiguration.isEnqueueHistoryReplicableObjects()) {
							queueHelper.enqueueTransientInReplica(aphotspot);
						} else {
							dao.create(aphotspot);
						}

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
			
			// Submits a check for the antennas
			final String deviceHostname = device.getHostname();
			if(device.getLastInfoUpdate() == null 
					|| ( new Date().getTime() - device.getLastInfoUpdate().getTime()) > 14400000) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							apdHelper.updateAPDeviceInfo(deviceHostname);
						} catch (ASException e) {
							log.log(Level.SEVERE, e.getMessage(), e);
						}
					}
				}).start();
			}
			
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

	
	// Replicate record on getin server
	public String replicateRecord(APHotspot rec) throws ASException {

		if( StringUtils.hasText(systemConfiguration.getGetinURI())) {

			try {

				JSONObject meta = new JSONObject();
				String record = "BM-0.1 " + sdfTime.format(rec.getCreationDateTime()) + " " + rec.getMac() + " RSSI:" + rec.getSignalDB();
				meta.put("record", record);
				JSONObject data = new JSONObject();
				data.put("type", "sensor");
				data.put("meta", meta);
				JSONObject obj = new JSONObject();
				obj.put("data", data);

				String url = systemConfiguration.getGetinURI() + "/api/sensors/" + rec.getHostname();
				log.log(Level.INFO, "Reporting to " + url + " with " + obj.toString());

				@SuppressWarnings("resource")
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				httppost.setHeader("Accept", "application/vnd.api+json");
				httppost.setHeader("Content-type", "application/vnd.api+json");
				httppost.setEntity(new StringEntity(obj.toString()));

				// Execute HTTP Post Request
				HttpResponse res = httpclient.execute(httppost);
				return res.getStatusLine().toString();

			} catch( Exception e ) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			}

		} else {
			return "Not Needed";
		}
	}
}
