package mobi.allshoppings.dump.impl;

import java.io.File;
import java.util.UUID;

import org.springframework.util.StringUtils;

import mobi.allshoppings.dump.CloudFileManager;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.model.DeviceWifiLocationHistory;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.ApplicationContextProvider;

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
		
		// Gets the TMP Directory
		if( baseDir == null ) {
			UUID uuid = UUID.randomUUID();
			File tmpDir = new File("/tmp/dump-" + uuid.toString());
			try {
				tmpDir.mkdirs();
			} catch( Exception e ) {
				tmpDir = new File("/tmp");
			}
			baseDir = tmpDir.getAbsolutePath();
		}

		SystemConfiguration systemConfiguration = (SystemConfiguration) ApplicationContextProvider
				.getApplicationContext().getBean("system.configuration");
		
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

		// Registers the Cloud File Manager for Walrus
		CloudFileManager s3cfm = new S3CloudFileManager(baseDir, systemConfiguration);
		String bucket = systemConfiguration.getS3Buckets().get(entity.getSimpleName());
		if(!StringUtils.hasText(bucket)) bucket = systemConfiguration.getS3Buckets().get("default");
		
		s3cfm.setBucket(bucket);
		dumper.registerCloudFileManager(s3cfm);
		
		// Now returns the builded result
		return dumper;
		
	}
	
}
