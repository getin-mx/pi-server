package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.model.DeviceLocation;
import mobi.allshoppings.tools.Range;

public interface DeviceLocationDAO extends GenericDAO<DeviceLocation> {

	GeoCodingHelper getGeocoder();
	void setGeocoder(GeoCodingHelper geocoder);

	DeviceLocation getLastUsingUserId(String userId) throws ASException;
	List<DeviceLocation> getAllDelayed(Date from, Date limit) throws ASException;
	List<DeviceLocation> getAllUpdated(Date from) throws ASException;
	List<DeviceLocation> getOrphan(Range range) throws ASException;
	List<DeviceLocation> getUsingUserIdentifierAndLastUpdate(String userIdentifier, Date lastUpdate) throws ASException;
	List<DeviceLocation> getByProximity(GeoPoint geo, Integer presition, Integer limitInMeters, String appId, Date lastUpdate, boolean detachable) throws ASException;
	long countLastUpdate(Date lastUpdate, boolean after) throws ASException;
	long countOrphan() throws ASException;
	
	Key createKey() throws ASException;

}
