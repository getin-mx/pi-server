package mobi.allshoppings.dump.impl;

import org.springframework.util.StringUtils;

import mobi.allshoppings.dump.DumperPlugin;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.geocoding.WifiSpotService;
import mobi.allshoppings.geocoding.impl.WifiSpotServiceImpl;
import mobi.allshoppings.model.DeviceWifiLocationHistory;
import mobi.allshoppings.model.interfaces.ModelKey;

public class DeviceWifiLocationHistoryDumperPlugin implements DumperPlugin<ModelKey> {

	private WifiSpotService service;

	public DeviceWifiLocationHistoryDumperPlugin() {
		service = new WifiSpotServiceImpl();
	}

	@Override
	public boolean isAvailableFor(ModelKey element) {
		if( element instanceof DeviceWifiLocationHistory ) return true;
		return false;
	}

	@Override
	public void preDump(ModelKey element) throws ASException {
		if(!StringUtils.hasText(((DeviceWifiLocationHistory)element).getWifiSpotId())) {
			service.calculateWifiSpot(((DeviceWifiLocationHistory)element));
		}
	}

	@Override
	public void postDump(ModelKey element) throws ASException {
		// Nothing to do here
	}

	@Override
	public String toJson(ModelKey element, String jsonRep) throws ASException {
		// Nothing to do here
		return jsonRep;
	}

}
