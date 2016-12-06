package mobi.allshoppings.push;

import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.PushMessageLogDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tracker.TrackerHelper;

public interface PushMessageSender {

	void sendMessage(User user, String title, String text, String action, DeviceInfo device, boolean doEncode) throws ASException;
	void requestLocation(DeviceInfo device) throws ASException;

	void setTrackerHelper(TrackerHelper trackerHelper);
	void setSystemConfiguration(SystemConfiguration systemConfiguration);
	void setUserDao(UserDAO userDao);
	void setDeviceInfoDao(DeviceInfoDAO deviceInfoDao);
	void setPushMessageHelper(PushMessageHelper pushMessageHelper);
	void setPushMessageLogDao(PushMessageLogDAO pushMessageLogDao);

}
