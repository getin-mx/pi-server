package mobi.allshoppings.geocoding;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.DeviceWifiLocationHistory;

public interface WifiSpotService {

	void calculateWifiSpot(DeviceWifiLocationHistory deviceWifiLocation) throws ASException;
	
}
