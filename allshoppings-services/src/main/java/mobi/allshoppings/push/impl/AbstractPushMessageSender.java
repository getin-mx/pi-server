package mobi.allshoppings.push.impl;

import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.PushMessageLogDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.push.PushMessageHelper;
import mobi.allshoppings.tracker.TrackerHelper;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractPushMessageSender {

	@Autowired
	protected TrackerHelper trackerHelper;
	@Autowired
	protected SystemConfiguration systemConfiguration;
	@Autowired
	protected UserDAO userDao;
	@Autowired
	protected DeviceInfoDAO deviceInfoDao;
	@Autowired
	protected PushMessageLogDAO pushMessageLogDao;
	@Autowired
	protected PushMessageHelper pushMessageHelper;
	
	/**
	 * @return the trackerHelper
	 */
	public TrackerHelper getTrackerHelper() {
		return trackerHelper;
	}
	
	/**
	 * @param trackerHelper the trackerHelper to set
	 */
	public void setTrackerHelper(TrackerHelper trackerHelper) {
		this.trackerHelper = trackerHelper;
	}
	
	/**
	 * @return the systemConfiguration
	 */
	public SystemConfiguration getSystemConfiguration() {
		return systemConfiguration;
	}
	
	/**
	 * @param systemConfiguration the systemConfiguration to set
	 */
	public void setSystemConfiguration(SystemConfiguration systemConfiguration) {
		this.systemConfiguration = systemConfiguration;
	}
	
	/**
	 * @return the userDao
	 */
	public UserDAO getUserDao() {
		return userDao;
	}
	
	/**
	 * @param userDao the userDao to set
	 */
	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
	}

	/**
	 * @return the deviceInfoDao
	 */
	public DeviceInfoDAO getDeviceInfoDao() {
		return deviceInfoDao;
	}

	/**
	 * @param deviceInfoDao the deviceInfoDao to set
	 */
	public void setDeviceInfoDao(DeviceInfoDAO deviceInfoDao) {
		this.deviceInfoDao = deviceInfoDao;
	}

	/**
	 * @return the pushMessageHelper
	 */
	public PushMessageHelper getPushMessageHelper() {
		return pushMessageHelper;
	}

	/**
	 * @param pushMessageHelper the pushMessageHelper to set
	 */
	public void setPushMessageHelper(PushMessageHelper pushMessageHelper) {
		this.pushMessageHelper = pushMessageHelper;
	}

	/**
	 * @return the pushMessageLogDao
	 */
	public PushMessageLogDAO getPushMessageLogDao() {
		return pushMessageLogDao;
	}

	/**
	 * @param pushMessageLogDao the pushMessageLogDao to set
	 */
	public void setPushMessageLogDao(PushMessageLogDAO pushMessageLogDao) {
		this.pushMessageLogDao = pushMessageLogDao;
	}

}
