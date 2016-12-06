package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.tools.Range;

public interface DeviceInfoDAO extends GenericDAO<DeviceInfo> {

	DeviceLocationDAO getDeviceLocationDao();
	void setDeviceLocationDao(DeviceLocationDAO deviceLocationDao);
	
	DeviceInfo getUsingUserAndDeviceAndPlatform(String userId, String deviceName, String devicePlatform) throws ASException;
	DeviceInfo getUsingMessagingToken(String messagingToken) throws ASException;
	List<DeviceInfo> getUsingUser(String userId) throws ASException;
	List<DeviceInfo> getUsingKeyList(List<String> keys) throws ASException;
	List<DeviceInfo> getOrphan(Range range) throws ASException;
	List<DeviceInfo> getByProximity(GeoPoint geo, Integer presition, Integer limitInMeters, String appId, Date lastUpdate, boolean detachable) throws ASException;
	long countOrphan() throws ASException;
	void deleteByMessagingToken(String messagingToken) throws ASException;
	List<DeviceInfo> getUsingMAC(String mac) throws ASException;

	
}
