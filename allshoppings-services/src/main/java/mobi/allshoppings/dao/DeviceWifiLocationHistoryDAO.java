package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.DeviceWifiLocationHistory;

public interface DeviceWifiLocationHistoryDAO extends GenericDAO<DeviceWifiLocationHistory> {
	
	Key createKey() throws ASException;
	List<DeviceWifiLocationHistory> getUsingDeviceUID(String deviceUid) throws ASException;

}
