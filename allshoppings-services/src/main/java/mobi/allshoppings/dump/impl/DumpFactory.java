package mobi.allshoppings.dump.impl;

import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.model.DeviceWifiLocationHistory;
import mobi.allshoppings.model.interfaces.ModelKey;

public class DumpFactory<T extends ModelKey> {

	/**
	 * Factory for the DumperHelper
	 * 
	 * @param baseDir
	 *            Base Directory
	 * @param entity
	 *            Entity to work for
	 * @return A fully builded DumperHelper
	 */
	public DumperHelper<T> build(String baseDir, Class<T> entity) {
		
		// Constructs the basic dumper
		DumperHelper<T> dumper = new DumperHelperImpl<T>(baseDir, entity);
		
		// Configures the dumper according to the entity class
		// For DeviceWifiLocationHistory
		if(entity.equals(DeviceWifiLocationHistory.class)) {
			dumper.registerPlugin(new DeviceWifiLocationHistoryDumperPlugin());
		}
		
		// For APHotspot
		if(entity.equals(APHotspot.class)) {
			dumper.registerFileNameResolver(new APHotspotFileNameResolver());
			dumper.registerPlugin(new APHotspotDumperPlugin());
		}
		
		// Now returns the builded result
		return dumper;
		
	}
	
}
