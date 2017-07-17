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
//		Date cdt = element.getCreationDateTime();
//		Date lupd = element.getLastUpdate();
//		
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(cdt);
//		cal.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
//		int offset = (cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)) / (60 * 1000);
//		cal.setTime(new Date());
//		cal.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
//		int currOffset = (cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)) / (60 * 1000);
//		
//		System.out.println("Date           : " + cdt);
//		cdt.setTime(cdt.getTime() - (offset * 60 * 1000));
//		System.out.println("Original offset: " + offset);
//		System.out.println("Current  offset: " + currOffset);
//		System.out.println("New Date       : " + cdt);

		// Nothing to do here
	}

	@Override
	public void postDump(ModelKey element) throws ASException {
		// Nothing to do here
	}

	@SuppressWarnings("deprecation")
	@Override
	public String toJson(ModelKey element, String jsonRep) throws ASException {
		JSONObject json = new JSONObject(jsonRep);
		json.remove("firstSeen");
		json.remove("lastSeen");
		json.remove("count");
		json.put("creationDateTime", new Date(json.getString("creationDateTime")).getTime());
		json.put("lastUpdate", new Date(json.getString("lastUpdate")).getTime());
		
		return json.toString();
	}

}
