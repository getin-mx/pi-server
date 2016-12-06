package mobi.allshoppings.push;

import java.util.Date;
import java.util.List;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.PushMessageLog;
import mobi.allshoppings.model.User;

import org.json.JSONObject;

import com.inodes.datanucleus.model.Key;

public interface PushMessageHelper {

	void sendMessage(User user, String title, String message, String action, DeviceInfo device ) throws ASException;
	void sendMessage(User user, String title, String text, String action, List<DeviceInfo> devices, boolean doEncode) throws ASException;
	void sendMessage(String title, String message, String action, DeviceInfo device ) throws ASException;
	
	void requestLocationByDeviceList(List<DeviceInfo> devices) throws ASException;
	void requestLocation(DeviceInfo device) throws ASException;
	void requestLocation(String deviceId) throws ASException;
	void requestLocation(List<String> deviceIds) throws ASException;

	void markAsReceived(String logIdentifier, Date receivedDate) throws ASException;
	void markAsReceived(PushMessageLog log, Date receivedDate) throws ASException;

	void markAsOpened(String logIdentifier, Date openDate) throws ASException;
	void markAsOpened(PushMessageLog log, Date openDate) throws ASException;
	
	// Utilities
	String buildPushMessageReplyAddress(PushMessageLog log) throws ASException;
	PushMessageLog buildPushMessageLog(DeviceInfo device, User user, Integer messageType, Key owner, JSONObject extras) throws ASException;
	
	// Deprecated
	void sendNotification(User user, String title, String avatarId, String message, String action, DeviceInfo device, String entityId, Integer entityKind) throws ASException;
}
