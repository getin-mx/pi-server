package mobi.allshoppings.dump.impl;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.util.StringUtils;

import mobi.allshoppings.dump.CloudFileManager;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.model.DeviceWifiLocationHistory;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.ApplicationContextProvider;

public class DumpFactory<T extends ModelKey> {
	
	private static final Logger log = Logger.getLogger(DumpFactory.class.getName());
	
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
		
		boolean tmpdir = false;
		
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
			tmpdir = true;
		}

		SystemConfiguration systemConfiguration = (SystemConfiguration) ApplicationContextProvider
				.getApplicationContext().getBean("system.configuration");
		
		// Constructs the basic dumper
		DumperHelper<T> dumper = new DumperHelperImpl<T>(baseDir, entity);
		dumper.setTmpDir(tmpdir);
		
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

		// For APHEntry
		if(entity.equals(APHEntry.class)) {
			dumper.registerFileNameResolver(new APHEntryFileNameResolver());
			dumper.setTimeFrame(DumperHelperImpl.TIMEFRAME_ONE_DAY);
		}

		// Registers the Cloud File Manager for Walrus
//		CloudFileManager s3cfm = new S3CloudFileManager(baseDir, systemConfiguration);
		CloudFileManager s3cfm = new XS3CloudFileManager(baseDir, systemConfiguration);
		String bucket = systemConfiguration.getS3Buckets().get(entity.getSimpleName());
		if(!StringUtils.hasText(bucket)) bucket = systemConfiguration.getS3Buckets().get("default");
		
		s3cfm.setBucket(bucket);
		dumper.registerCloudFileManager(s3cfm);
		try {
			s3cfm.startPrefetch();
		} catch (ASException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		
		// Now returns the builded result
		return dumper;
		
	}
	
}
