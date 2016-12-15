package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.DeviceMessageLock;

public interface DeviceMessageLockDAO extends GenericDAO<DeviceMessageLock> {

	Key createKey(DeviceMessageLock obj) throws ASException;
	boolean hasActiveLocks(String userId, Date forDate) throws ASException;
	boolean deviceHasActiveLocks(String deviceUUID, Date forDate) throws ASException;
	boolean deviceHasActiveLocks(String deviceUUID, Date forDate, List<Integer> scope) throws ASException;
	List<DeviceMessageLock> getDeviceActiveLocks(String deviceUUID, Date forDate) throws ASException;
	List<DeviceMessageLock> getUsingDeviceAndScopeAndCampaign(String deviceUUID, Integer scope, String campaignActivityId) throws ASException;
}
