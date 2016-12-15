package mobi.allshoppings.push.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.util.StringUtils;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.PushMessageLog;
import mobi.allshoppings.model.User;
import mobi.allshoppings.push.PushMessageSender;

public class PushMessageSenderGCMImpl extends AbstractPushMessageSender implements PushMessageSender {

	private static Logger log = Logger.getLogger(PushMessageSenderGCMImpl.class.getName());

	@Override
	public void sendMessage(User user, String title, String text, String action,
			DeviceInfo device, boolean doEncode) throws ASException {

		boolean messageSended = false;

		if (user == null 
				|| (user.getReceivePushMessages() != null
				&& user.getReceivePushMessages() == true
				&& user.getGeoFenceEnabled() != null
				&& user.getGeoFenceEnabled() == true 
				&& device != null)) {

			JSONObject extras = new JSONObject();
			Message.Builder mb = new Message.Builder();
			mb.collapseKey(getSystemConfiguration().getCollapseKey()).delayWhileIdle(false).dryRun(false).timeToLive(3000);

			if( StringUtils.hasText(text)) {
				try {
					extras.put("message", doEncode ? 
							URLEncoder.encode(PushMessageHelperImpl.setLength(text, 120), "UTF-8") : 
								PushMessageHelperImpl.setLength(text, 120));
				} catch (UnsupportedEncodingException e1) {
					extras.put("message", PushMessageHelperImpl.setLength(text, 120));
				}
			}

			if( StringUtils.hasText(title)) {
				try {
					extras.put("title", doEncode ? URLEncoder.encode(title, "UTF-8") : title );
				} catch (UnsupportedEncodingException e1) {
					extras.put("title", title);
				}
			}

			if( StringUtils.hasText(action)) { 
				extras.put("action", action);
			} else {
				extras.put("action", "notification");
			}
			
			PushMessageLog pmlog = getPushMessageHelper().buildPushMessageLog(device, user, PushMessageLog.TYPE_MESSAGE, null, extras);
			String replyAddress = getPushMessageHelper().buildPushMessageReplyAddress(pmlog);

			mb.addData("title", extras.getString("title"));
			mb.addData("message", extras.getString("message"));
			mb.addData("action", extras.getString("action"));
			mb.addData("replyTo", replyAddress);
			
			final Message message = mb.build();
			String target = device.getDeviceUUID();
			try {
				if(!getSystemConfiguration().getUseFakeMessages()) {
					if( device.getMessagingToken() != null ) {

						String senderId = StringUtils.hasText(device.getAppId()) 
								? systemConfiguration.getGCMSenders().get(device.getAppId()) 
										: systemConfiguration.getGCMSenders().get("default");
								Sender sender = new Sender(senderId);

								MulticastResult rs = sender.send(message, Arrays.asList(new String[] {device.getMessagingToken()}), getSystemConfiguration().getPushMessageRetries());
								if(rs.getFailure() > 0 ) {
									for(Result result : rs.getResults()) {
										if( result.getErrorCodeName() != null ) {

											Level l = Level.SEVERE;
											if( result.getErrorCodeName().trim().equalsIgnoreCase("NotRegistered")) {
												l = Level.INFO;
											}

											log.log(l,
													"Errors occured trying to send push message "
															+ title
															+ " to "
															+ user != null ? user.getIdentifier() : result.getCanonicalRegistrationId()
																	+ " with GCM id "
																	+ result.getCanonicalRegistrationId()
																	+ " - " 
																	+ result.getMessageId()
																	+ " with error code "
																	+ result.getErrorCodeName(),
																	ASExceptionHelper.defaultException(null, null));

											pmlog.setStatus(PushMessageLog.STATUS_ERROR);
											getPushMessageLogDao().delete(pmlog.getIdentifier());
											getPushMessageLogDao().create(pmlog);
											
											if( result.getErrorCodeName().equals("InvalidRegistration") || result.getErrorCodeName().equals("NotRegistered")) {
												try {
													if( getDeviceInfoDao() != null )
														getDeviceInfoDao().deleteByMessagingToken(device.getMessagingToken());
												} catch( ASException e ) {
													log.log(Level.SEVERE, e.getMessage(), e);
												}
											}
										}
									}
								} else {
									messageSended = true;
									log.log(Level.INFO, "Message " + title + " sent to " + target);
								}
					}
				} else {
					log.log(Level.INFO, "MOCK Message " + title + " sent to " + target);
				}
			} catch( IOException e ) {
				log.log(Level.SEVERE, "Errors occured trying to send push message " + title + " to " + target, e);
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			}

			// track action
			if( messageSended ) {
				getTrackerHelper().enqueue( user, null,	null, null, 
						"Push Message: " + message.getData().get("title"), 
						null, null);
			}
		}
	}

	@Override
	public void requestLocation(DeviceInfo device) throws ASException {

		boolean messageSended = false;

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

				Message.Builder mb = new Message.Builder();
				mb.collapseKey(getSystemConfiguration().getCollapseKey()).delayWhileIdle(false).dryRun(false).timeToLive(3000);

				JSONObject extras = new JSONObject();
				extras.put("requestLocation", "true");
				extras.put("url", getSystemConfiguration().getPushMessageResponseUrl());

				PushMessageLog pmlog = getPushMessageHelper().buildPushMessageLog(device, user, PushMessageLog.TYPE_LOCATION, null, null);
				String replyAddress = getPushMessageHelper().buildPushMessageReplyAddress(pmlog);

				mb.addData("replyTo", replyAddress);
				mb.addData("requestLocation", extras.getString("requestLocation"));
				mb.addData("url", extras.getString("url"));
				final Message message = mb.build();

				String target = device.getDeviceUUID();
				try {
					if( StringUtils.hasText(device.getMessagingToken())) {
						if(!getSystemConfiguration().getUseFakeMessages()) {

							String senderId = StringUtils.hasText(device.getAppId()) 
									? systemConfiguration.getGCMSenders().get(device.getAppId()) 
											: systemConfiguration.getGCMSenders().get("default");
									Sender sender = new Sender(senderId);

									MulticastResult rs = sender.send(message, Arrays.asList(new String[] {device.getMessagingToken()}), getSystemConfiguration().getPushMessageRetries());
									if(rs.getFailure() > 0 ) {
										for(Result result : rs.getResults()) {
											if( result.getErrorCodeName() != null ) {

												Level l = Level.SEVERE;
												if( result.getErrorCodeName().trim().equalsIgnoreCase("NotRegistered")) {
													l = Level.INFO;
												}

												log.log(l,
														"Errors occured trying to request location from"
																+ target
																+ " with GCM id "
																+ result.getCanonicalRegistrationId()
																+ " - " 
																+ result.getMessageId()
																+ " with error code "
																+ result.getErrorCodeName(),
																new RuntimeException());

												pmlog.setStatus(PushMessageLog.STATUS_ERROR);
												getPushMessageLogDao().delete(pmlog.getIdentifier());
												getPushMessageLogDao().create(pmlog);
												
												if( result.getErrorCodeName().equals("InvalidRegistration") || result.getErrorCodeName().equals("NotRegistered")) {
													try {
														if( getDeviceInfoDao() != null )
															getDeviceInfoDao().deleteByMessagingToken(device.getMessagingToken());
													} catch( ASException e ) {
														log.log(Level.SEVERE, e.getMessage(), e);
													}
												}
											}
										}
									} else {
										messageSended = true;
										log.log(Level.INFO, "Location requested from " + target);
									}
						} else {
							messageSended = true;
							log.log(Level.INFO, "MOCK Location requested from " + target);
						}
					}

				} catch( IOException e ) {
					log.log(Level.SEVERE, "Errors occured trying to request location from " + target, e);
				}

				// track action
				if( messageSended ) {
					getTrackerHelper().enqueue((User)null, null,	null, null, 
							"Location requested from device " + target, 
							null, null);
				}

			}
		}
	}

}
