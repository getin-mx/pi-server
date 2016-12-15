package mobi.allshoppings.push.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLHandshakeException;

import org.json.JSONObject;
import org.springframework.util.StringUtils;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.PushMessageLog;
import mobi.allshoppings.model.User;
import mobi.allshoppings.push.PushMessageSender;

public class PushMessageSenderAPNImpl extends AbstractPushMessageSender implements PushMessageSender {

	private static Logger log = Logger.getLogger(PushMessageSenderAPNImpl.class.getName());

	@Override
	public void sendMessage(User user, String title, String text, String action,
			DeviceInfo device, boolean doEncode) throws ASException {

		if (user == null ||
				user.getReceivePushMessages() != null
				&& user.getReceivePushMessages() == true
				&& user.getGeoFenceEnabled() != null
				&& user.getGeoFenceEnabled() == true 
				&& device != null ) {

			if(!StringUtils.hasText(action)) action = "notification";

			JSONObject extras = new JSONObject();
			extras.put("message", text);
			extras.put("title", title);
			extras.put("action", action);
			
			PushMessageLog pmlog = getPushMessageHelper().buildPushMessageLog(device, user, PushMessageLog.TYPE_MESSAGE, null, extras);
			String replyAddress = getPushMessageHelper().buildPushMessageReplyAddress(pmlog);
			
			ApnsService service = buildApnsForDevice(device);
			String payload = APNS.newPayload().alertBody(text).badge(1).sound("default").customField("action", action)
					.customField("source", "AllShoppings").customField("replyTo", replyAddress).shrinkBody().build();
			// This second payload is to make the message report a response to the same replyURL
			String replyPayload = APNS.newPayload().forNewsstand().customField("replyTo", replyAddress).build();
			if( systemConfiguration.getUseFakeMessages() == true ) {
				log.log(Level.INFO, "Mock message sent");
			} else {
				try {
					service.push(device.getMessagingToken(), payload);
					service.push(device.getMessagingToken(), replyPayload);

					// track action
					getTrackerHelper().enqueue( user, null,	null, null, 
							"Push Message: " + title, 
							null, null);

				} catch( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
					
					pmlog.setStatus(PushMessageLog.STATUS_ERROR);
					getPushMessageLogDao().delete(pmlog.getIdentifier());
					getPushMessageLogDao().create(pmlog);

				}
			}

		}
	}
	
	@Override
	public void requestLocation(DeviceInfo device) throws ASException {

		if (device != null) {
			User user = null;
			try {
				user = getUserDao().get(device.getUserId(), true);
			} catch( Exception e ) {}

			if (user == null ||
					user.getReceivePushMessages() != null
					&& user.getReceivePushMessages() == true
					&& user.getGeoFenceEnabled() != null
					&& user.getGeoFenceEnabled() == true
					&& device.getMessagingToken() != null) {

				PushMessageLog pmlog = getPushMessageHelper().buildPushMessageLog(device, user, PushMessageLog.TYPE_LOCATION, null, null);
				String replyAddress = getPushMessageHelper().buildPushMessageReplyAddress(pmlog);

				ApnsService service = buildApnsForDevice(device);
				String payload = APNS.newPayload().forNewsstand().customField("replyTo", replyAddress).build();
				try {
					service.push(device.getMessagingToken(), payload);
				} catch( Exception e ) {
					if( e instanceof SSLHandshakeException ) {
						log.log(Level.INFO, "SSLHandshakeException trying to send request location push message to " + user.getIdentifier(), e);
					} else {
						log.log(Level.SEVERE, "Errors occured trying to send request location push message to " + user.getIdentifier(), e);

						pmlog.setStatus(PushMessageLog.STATUS_ERROR);
						getPushMessageLogDao().delete(pmlog.getIdentifier());
						getPushMessageLogDao().create(pmlog);

						throw ASExceptionHelper.defaultException(e.getMessage(), e);
					}
				}
			}
		}
	}

	private String getCertificatePassword(DeviceInfo device) {
		if(device.getMessagingSandbox() != null && device.getMessagingSandbox() == true ) {
			return StringUtils.hasText(device.getAppId()) 
					? systemConfiguration.getAPNSandboxCertPassword().get(device.getAppId()) 
							: systemConfiguration.getAPNSandboxCertPassword().get("default");
		} else {
			return StringUtils.hasText(device.getAppId()) 
					? systemConfiguration.getAPNCertPassword().get(device.getAppId()) 
							: systemConfiguration.getAPNCertPassword().get("default");
		}
	}

	private String getApnsKey(DeviceInfo device) {
		if(device.getMessagingSandbox() != null && device.getMessagingSandbox() == true ) {
			return systemConfiguration.getAPNAppsCertificatePath() + device.getAppId() + "_development.p12";
		} else {
			return systemConfiguration.getAPNAppsCertificatePath() + device.getAppId() + "_production.p12";
		}
	}

	private ApnsService buildApnsForDevice(DeviceInfo device) {
		if(device.getMessagingSandbox() != null && device.getMessagingSandbox() == true ) {
			return APNS.newService()
					.withCert(getApnsKey(device), getCertificatePassword(device))
					.withNoErrorDetection()
					.withSandboxDestination()
					.build();
		} else {
			return APNS.newService()
					.withCert(getApnsKey(device), getCertificatePassword(device))
					.withNoErrorDetection()
					.withProductionDestination()
					.build();
		}
	}

}
