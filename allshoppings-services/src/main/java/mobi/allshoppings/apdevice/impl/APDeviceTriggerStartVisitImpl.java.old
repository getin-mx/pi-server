package mobi.allshoppings.apdevice.impl;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.apdevice.IAPDeviceTrigger;
import mobi.allshoppings.dao.APHotspotDAO;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.push.PushMessageHelper;
import mobi.allshoppings.tools.ApplicationContextProvider;

public class APDeviceTriggerStartVisitImpl implements IAPDeviceTrigger {

	@Autowired
	private APHotspotDAO aphDao;
	@Autowired
	private DeviceInfoDAO diDao;
	@Autowired
	private PushMessageHelper pushHelper;
	
	public APDeviceTriggerStartVisitImpl() {
		aphDao = (APHotspotDAO)ApplicationContextProvider.getApplicationContext().getBean("aphotspot.dao.ref");
		diDao = (DeviceInfoDAO)ApplicationContextProvider.getApplicationContext().getBean("deviceinfo.dao.ref");
		pushHelper = (PushMessageHelper)ApplicationContextProvider.getApplicationContext().getBean("push.message.helper");
	}
	
	@Override
	public void execute(String hostname, String mac, Integer rssi, String smetadata) throws ASException {
		
		try {
			JSONObject metadata = new JSONObject(smetadata);
			
			APHotspot aph = aphDao.getPreviousUsingHostnameAndMac(hostname, mac);
			long timeLimit = metadata.has("timeLimit") ? metadata.getLong("timeLimit") : 1800000;
			if( new Date().getTime() - aph.getLastSeen().getTime() > timeLimit) {
				String title = metadata.has("title") ? metadata.getString("title") : "GetIn";
				String message = metadata.has("message") ? metadata.getString("message") : "Bienvenido a {{hostname}}";
				String app = metadata.has("app") ? metadata.getString("app") : "getin-apdevice-calibrator";
				message = message.replace("{{hostname}}", hostname);
				title = title.replace("{{hostname}}", hostname);

				List<DeviceInfo> devices = diDao.getUsingMAC(mac);
				for( DeviceInfo device : devices ) {
					if("*".equals(app) || device.getAppId().equals(app)) {
						pushHelper.sendMessage(title, message, null, device);
					}
				}
			}
			
		} catch( ASException e ) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE) {
				// Silently fail
			} else {
				throw e;
			}
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		
	}

}
