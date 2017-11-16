package mobi.allshoppings.dump.impl;

import java.util.Date;

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
		return toJson(new JSONObject(jsonRep), jsonRep);
	}

	private boolean isNumber(String str) {
	    for (char c : str.toCharArray()) {
	        if (!Character.isDigit(c))
	            return false;
	    }
	    return true;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String toJson(JSONObject json, String jsonRep) throws ASException {
		json.remove("firstSeen");
		json.remove("lastSeen");
		json.remove("count");
		
		try {
			if(!isNumber(json.getString("creationDateTime"))) {
				json.put("creationDateTime", new Date(json.getString("creationDateTime")).getTime());
			}
		} catch( Exception e ) {}

		try {
			if(!isNumber(json.getString("lastUpdate"))) {
				json.put("lastUpdate", new Date(json.getString("lastUpdate")).getTime());
			}
		} catch( Exception e ) {}

		return json.toString();
	}
}
