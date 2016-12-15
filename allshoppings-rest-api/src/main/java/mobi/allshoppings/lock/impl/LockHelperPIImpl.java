package mobi.allshoppings.lock.impl;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import mobi.allshoppings.coupon.CouponHelper;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.DeviceMessageLockDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.lock.LockHelper;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.DeviceMessageLock;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.SystemConfiguration;

public class LockHelperPIImpl implements LockHelper {

	private static final Logger log = Logger.getLogger(LockHelperPIImpl.class.getName());

	@Autowired
	private DeviceMessageLockDAO dao;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private DeviceInfoDAO deviceInfoDao;
	@Autowired
	private CouponHelper couponHelper;

	private static RestTemplate restTemplate;
	private static HttpHeaders requestHeaders;
	
	static {
		requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().clear();
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
	}
	
	@Override
	public void deviceMessageLock(String deviceId, Integer scope, String campaignActivityId,
			Date fromDate, long duration, String subEntityId, Integer subEntityKind) throws ASException {

		DeviceMessageLock lock = new DeviceMessageLock();

		lock.setDeviceId(deviceId);
		lock.setFromDate(fromDate);
		lock.setScope(scope);
		lock.setCampaignActivityId(campaignActivityId);
		
		// checks for scope
		if( scope == null ) {
			scope = DeviceMessageLock.SCOPE_GLOBAL;
		}
		
		// checks for application lock
		try {
			DeviceInfo deviceInfo = deviceInfoDao.get(lock.getDeviceId());
			lock.setUserId(deviceInfo.getUserId());
			if( StringUtils.hasText(deviceInfo.getAppId()) && !systemConfiguration.getDefaultBehavioursApps().contains(deviceInfo.getAppId())) {
				lock.setEntityId(deviceInfo.getAppId());
				lock.setEntityKind(EntityKind.KIND_USER);
			}

			// Sub entity id and kind. For example, it could be the cinema ID
			lock.setSubEntityKind(subEntityKind);
			if( StringUtils.hasText(deviceInfo.getAppId()) && !systemConfiguration.getDefaultBehavioursApps().contains(deviceInfo.getAppId())) {
				lock.setSubEntityId(deviceInfo.getAppId() + "_" + subEntityId);
			} else {
				lock.setSubEntityId(subEntityId);
			}
		} catch( Exception e ) {}
		
		//checks for lock duration
		if( lock.getFromDate() == null ) {
			lock.setFromDate(new Date());
		}
		if( lock.getToDate() == null && duration == 0 ) {
			lock.setToDate(new Date(lock.getFromDate().getTime() + systemConfiguration.getDefaultMessageLockTimeMillis()));
		}
		if( lock.getToDate() == null && duration > 0 ) {
			lock.setToDate(new Date(lock.getFromDate().getTime() + duration));
		}

		//checks if there is a lock on a particular activity
		if( StringUtils.hasText(lock.getCampaignActivityId())) {
			try {
				couponHelper.ruleReject(lock.getCampaignActivityId());
			} catch( ASException e ) {
				if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
					log.log(Level.INFO, "Campaign activity " + lock.getCampaignActivityId() + " not found!");
				} else if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_FORBIDDEN_CODE ) {
					log.log(Level.INFO, "Campaign activity " + lock.getCampaignActivityId() + " already locked!");
				} else {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}

		//persist the object into the datastore
		lock.setKey(dao.createKey(lock));
		dao.create(lock);

	}

	@Override
	public void clearLocks(String deviceId, Integer scope, String campaignActivityId) throws ASException {
		List<DeviceMessageLock> list = dao.getUsingDeviceAndScopeAndCampaign(deviceId, scope, campaignActivityId);
		for( DeviceMessageLock lock : list ) {
			dao.delete(lock);
		}
	}
	
}
