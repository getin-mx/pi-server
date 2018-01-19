package mobi.allshoppings.push.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.NotificationLogDAO;
import mobi.allshoppings.dao.PushMessageLogDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.NotificationLog;
import mobi.allshoppings.model.PushMessageLog;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.push.PushMessageHelper;
import mobi.allshoppings.push.PushMessageSender;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CollectionUtils;
import mobi.allshoppings.tracker.TrackerHelper;

public class PushMessageHelperImpl implements PushMessageHelper {

	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private UserDAO userDao;
	@Autowired
	private DeviceInfoDAO deviceInfoDao;
	@Autowired
	private NotificationLogDAO notificationLogDao;
	@Autowired
	protected TrackerHelper trackerHelper;
	@Autowired
	protected GeoCodingHelper geocoder;
	@Autowired
	protected PushMessageLogDAO pushMessageLogDao;


	private static final Logger log = Logger.getLogger(PushMessageHelperImpl.class.getName());

	private Map<String, PushMessageSender> senders;
	
	@Override
	public void sendMessage(String title, String text, String action, DeviceInfo device) throws ASException {

		User user = null;
		try {
			user = userDao.get(device.getUserId(), true);
		} catch( ASException e ) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE 
					|| e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTACCEPTED_CODE) {
				log.log(Level.INFO, "User " + device.getUserId() + " not found for push message");
			} else {
				throw e;
			}
		}
		sendMessage(user, title, text, action, device);
	
	}

	@Override
	public void sendBulkMessage(String appId, String title, String text, String action ) throws ASException {
		
		List<DeviceInfo> list = deviceInfoDao.getUsingAppId(appId);
		for( DeviceInfo dev : list ) {
			if( StringUtils.hasText(dev.getMessagingToken())) {
				sendMessage(title, text, action, dev);
			}
		}
		
	}
	
	
	@Override
	public void sendMessage(String appId, User user, String title, String text, String action) throws ASException {
		
		List<DeviceInfo> list = deviceInfoDao.getUsingUser(user.getIdentifier());
		for( DeviceInfo dev : list ) {
			if( StringUtils.hasText(dev.getMessagingToken())) {
				if( null != appId && appId.equals(dev.getAppId())) {
					sendMessage(user, title, text, action, dev);
				}
			}
		}
		
	}
	
	@Override
	public void sendMessage(User user, String title, String text, String action, DeviceInfo device) throws ASException {

		PushMessageSender sender = getSenderForPlatform(device.getDevicePlatform());
		sender.sendMessage(user, title, text, action, device, true);

	}

	@Override
	public void sendMessage(User user, String title, String text, String action,
			List<DeviceInfo> devices, boolean doEncode) throws ASException {

		if (user.getReceivePushMessages() != null
				&& user.getReceivePushMessages() == true
				&& user.getGeoFenceEnabled() != null
				&& user.getGeoFenceEnabled() == true 
				&& devices.size() > 0) {

			/*
			 * Separates the devices list into segments, according the device platform
			 */
			HashMap<String, List<DeviceInfo>> segments = new HashMap<String, List<DeviceInfo>>();
			for( DeviceInfo device : devices ) {
				List<DeviceInfo> segmentDevices = segments.get(device.getDevicePlatform());
				if( segmentDevices == null ) segmentDevices = new ArrayList<DeviceInfo>();
				segmentDevices.add(device);
				segments.put(device.getDevicePlatform(), segmentDevices);
			}
			
			/*
			 * Calls each message sender to deliver the push message
			 */
			for( String key : segments.keySet()) {
				PushMessageSender sender = getSenderForPlatform(key);
				if( sender != null ) {
					for( DeviceInfo device : segments.get(key)) {
						sender.sendMessage(user, title, text, action, device, doEncode);
					}
				}
			}
		}
	}

	@Override
	public void requestLocation(DeviceInfo device) throws ASException {
		requestLocationByDeviceList(Arrays.asList(new DeviceInfo[] {device}));
	}
	
	@Override
	public void requestLocationByDeviceList(List<DeviceInfo> devices) throws ASException {

		if( CollectionUtils.isEmpty(devices)) return;
		
		Map<PushMessageSender, List<DeviceInfo>> map = CollectionFactory.createMap();
		for( DeviceInfo device : devices ) {
			PushMessageSender sender = getSenderForPlatform(device.getDevicePlatform());
			if( sender != null ) {
				List<DeviceInfo> list = map.get(sender);
				if( list == null ) list = CollectionFactory.createList();
				list.add(device);
				map.put(sender, list);
			}
		}
		
		Iterator<PushMessageSender> i = map.keySet().iterator();
		while(i.hasNext()) {
			PushMessageSender sender = i.next();
			for( DeviceInfo device : map.get(sender)) {
				try {
					sender.requestLocation(device);
				} catch( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
	}

	public PushMessageSender getSenderForPlatform(String platform) {
		
		if(!StringUtils.hasLength(platform)) return null;
		
		if( senders == null ) senders = new HashMap<String, PushMessageSender>();
		PushMessageSender sender = senders.get(platform);
		
		if( sender == null ) {
			if( platform.equalsIgnoreCase("Android")) {
				sender = new PushMessageSenderGCMImpl();
			} else if( platform.equalsIgnoreCase("iOS")) {
				sender = new PushMessageSenderAPNImpl();
			}

			if( sender != null ) {
				sender.setSystemConfiguration(systemConfiguration);
				sender.setTrackerHelper(trackerHelper);
				sender.setUserDao(userDao);
				sender.setDeviceInfoDao(deviceInfoDao);
				sender.setPushMessageLogDao(pushMessageLogDao);
				sender.setPushMessageHelper(this);
				senders.put(platform, sender);
			}
		}
		
		return sender;
	}

	/* (non-Javadoc)
	 * @see mobi.allshoppings.push.PushMessageHelper#sendNotification(mobi.allshoppings.model.User, java.lang.String, java.lang.String, java.lang.String, java.lang.String, mobi.allshoppings.model.DeviceInfo)
	 */
	@Override
	public void sendNotification(User user, String title, String avatarId,
			String message, String action, DeviceInfo device, String entityId, byte entityKind)
			throws ASException {
		try {
			NotificationLog nl = new NotificationLog();
			nl.setKey(notificationLogDao.createKey());
			nl.setUserId(user.getIdentifier());
			nl.setDeviceUUID(device == null ? null : device.getDeviceUUID());
			nl.setEntityId(entityId);
			nl.setEntityKind(entityKind);
			nl.setTitle(title);
			nl.setData(message);
			nl.setAvatarId(avatarId);
			nl.setAction(action);
			nl.setNotifyDate(new Date());
			nl.setAction(action);
			nl.setStatus(StatusAware.STATUS_NEW);

			notificationLogDao.create(nl);
			nl = notificationLogDao.get(nl.getIdentifier(), true);

			List<DeviceInfo> devices;
			if( device == null ) {
				devices = deviceInfoDao.getUsingUser(user.getIdentifier());
			} else {
				devices = Arrays.asList(new DeviceInfo[] {device});
			}
			
			// Send the message
			sendMessage(user, title, message, action, devices, true);

			// And now, set the succeeded notify date
			nl.setNotifyDate(new Date());
			nl.setStatus(StatusAware.STATUS_REMOVED);
			notificationLogDao.update(nl);
		} catch( ASException e ) {
			if(e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_PUSH_MESSAGE) {
				log.log(Level.SEVERE, e.getMessage(), e);
			} else if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ){
				// Silently fail
			}
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	public void requestLocation(String deviceId) throws ASException {
		requestLocation(Arrays.asList(new String[] {deviceId}));
	}

	@Override
	public void requestLocation(List<String> deviceIds) throws ASException {
		List<DeviceInfo> devices = deviceInfoDao.getUsingIdList(deviceIds);
		requestLocationByDeviceList(devices);
	}

	/**
	 * Trims the length of a String
	 * 
	 * @param string
	 *            The String to trim
	 * @param maxLength
	 *            maximum string length
	 * @return The trimmed text
	 */
	protected static String setLength(String string, int maxLength) {
		if( !StringUtils.hasText(string) || string.length() <= maxLength ) return string;
		String str = string.substring(0, maxLength) + "...";
		return str;
	}

	/**
	 * Return the physical address to response to a Push Message Log for
	 * reception and ack
	 * 
	 * @param log
	 *            The physical push message log
	 * @return A fully qualified URL to reply
	 */
	public String buildPushMessageReplyAddress(PushMessageLog log) throws ASException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(systemConfiguration.getPushMessageResponseUrl()).append("/appv2/reportMessage?identifier=").append(log.getIdentifier());
			return sb.toString();
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	/**
	 * Builds a reply address to mark the message reception and ack
	 * 
	 * @param device
	 *            The affected device
	 * @param user
	 *            The affected user
	 * @param messageType
	 *            Message type
	 * @param owner
	 *            Key to the owner
	 * @param extras
	 *            Extras used in the message
	 * @return A fully formed push message log
	 * @throws ASException
	 */
	@Override
	public PushMessageLog buildPushMessageLog(DeviceInfo device, User user, int messageType, Key owner, JSONObject extras) throws ASException {
		
		PushMessageLog log = new PushMessageLog();
		log.setDeviceUUID(device.getIdentifier());
		log.setUserId(user != null ? user.getIdentifier() : device.getUserId());
		log.setType(messageType);
		log.setStatus(PushMessageLog.STATUS_SENDED);
		log.setCreationDateTime(new Date());
		log.setNotifiactionDateTime(new Date());
		log.setData(extras != null ? extras.toString() : null);
		log.setKey(pushMessageLogDao.createKey());
		
		pushMessageLogDao.create(log);
		
		return log;
	}
	
	@Override
	public void markAsReceived(PushMessageLog log, Date receivedDate) throws ASException {
		
		if( log == null ) throw ASExceptionHelper.invalidArgumentsException("log");
		if( receivedDate == null ) receivedDate = new Date();
		
		if( log.getStatus() == PushMessageLog.STATUS_OPENED ) return;
		if( log.getStatus() == PushMessageLog.STATUS_RECEIVED ) return;
		
		log.setReceptionDateTime(receivedDate);
		log.setStatus(PushMessageLog.STATUS_RECEIVED);
		pushMessageLogDao.update(log);
		
	}

	@Override
	public void markAsOpened(PushMessageLog log, Date readedDate) throws ASException {

		if( log == null ) throw ASExceptionHelper.invalidArgumentsException("log");
		if( readedDate == null ) readedDate = new Date();

		if( log.getStatus() == PushMessageLog.STATUS_OPENED ) return;

		if( log.getReceptionDateTime() == null ) log.setReceptionDateTime(readedDate);
		log.setOpenDateTime(readedDate);
		
		log.setStatus(PushMessageLog.STATUS_OPENED);
		pushMessageLogDao.update(log);

	}

	@Override
	public void markAsReceived(String logIdentifier, Date receivedDate) throws ASException {
		markAsReceived(pushMessageLogDao.get(logIdentifier, true), receivedDate);
	}

	@Override
	public void markAsOpened(String logIdentifier, Date readedDate) throws ASException {
		markAsOpened(pushMessageLogDao.get(logIdentifier, true), readedDate);
	}

}

