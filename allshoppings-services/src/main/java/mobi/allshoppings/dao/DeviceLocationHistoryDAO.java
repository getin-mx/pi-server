package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.DeviceLocation;
import mobi.allshoppings.model.DeviceLocationHistory;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public interface DeviceLocationHistoryDAO extends GenericDAO<DeviceLocationHistory> {
	
	Key createKey() throws ASException;
	DeviceLocationHistory build(DeviceLocation deviceLocation) throws ASException;
	List<DeviceLocationHistory> getUsingDeviceUID(String deviceUid) throws ASException;
	long countUsingDates(Date fromDate, Date toDate) throws ASException;
	List<DeviceLocationHistory> getUsingDatesAndRange(Date fromDate, Date toDate, Range range) throws ASException;
	List<DeviceLocationHistory> getUsingDatesAndRange(PersistenceProvider pp, Date fromDate, Date toDate, Range range, String order, boolean detachable) throws ASException;
}
