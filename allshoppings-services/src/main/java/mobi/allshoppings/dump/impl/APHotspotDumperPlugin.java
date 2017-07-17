package mobi.allshoppings.dump.impl;

import org.json.JSONObject;

import mobi.allshoppings.dump.DumperPlugin;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.model.interfaces.ModelKey;

public class APHotspotDumperPlugin implements DumperPlugin<ModelKey> {
	
	public APHotspotDumperPlugin() {
	}

	@Override
	public boolean isAvailableFor(ModelKey element) {
		if( element instanceof APHotspot ) return true;
		return false;
	}

	@Override
	public void preDump(ModelKey element) throws ASException {
		// Nothing to do here
	}

	@Override
	public void postDump(ModelKey element) throws ASException {
		// Nothing to do here
	}

	@Override
	public String toJson(ModelKey element, String jsonRep) throws ASException {
		JSONObject json = new JSONObject(jsonRep);
		json.remove("firstSeen");
		json.remove("lastSeen");
		json.remove("count");
		
		return json.toString();
	}

}
