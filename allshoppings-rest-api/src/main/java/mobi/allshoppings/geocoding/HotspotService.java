package mobi.allshoppings.geocoding;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.DeviceWifiLocationHistory;
import mobi.allshoppings.model.adapter.DeviceLocationAdapter;
import mobi.allshoppings.model.adapter.HotSpotAdapter;

public interface HotspotService {

	void addWithKnownLocation(DeviceLocationAdapter adapter, HotSpotAdapter nearest) throws ASException;
	void add(String deviceUUID, Double lat, Double lon) throws ASException;
	void calculateWifiSpot(String deviceWifiLocationId) throws ASException;
	void calculateWifiSpot(DeviceWifiLocationHistory deviceWifiLocation) throws ASException;
	
}
