package mobi.allshoppings.location;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.util.StringUtils;

import com.google.common.io.Files;

import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.geocoding.WifiSpotService;
import mobi.allshoppings.geocoding.impl.WifiSpotServiceImpl;
import mobi.allshoppings.model.DeviceWifiLocationHistory;


public class WifiSpotUpdaterService {

	private static final Logger log = Logger.getLogger(WifiSpotUpdaterService.class.getName());
	
	public void updateWifiSpots(String baseDir, Date fromDate, Date toDate) throws ASException, IOException {
		
		WifiSpotService service = new WifiSpotServiceImpl();
		File tmpDir = File.createTempFile("dump", "WifiSpotService");
		tmpDir.delete();
		tmpDir.mkdirs();
		DumperHelper<DeviceWifiLocationHistory> dumper = new DumpFactory<DeviceWifiLocationHistory>().build(baseDir, DeviceWifiLocationHistory.class);
		DumperHelper<DeviceWifiLocationHistory> tmpDumper = new DumpFactory<DeviceWifiLocationHistory>().build(tmpDir.getAbsolutePath(), DeviceWifiLocationHistory.class);
		Date curDate = new Date(fromDate.getTime());

		long totals = 0;
		long initTime = new Date().getTime();
		
		while(curDate.before(toDate)) {
			
			long initListTime = new Date().getTime();
			List<DeviceWifiLocationHistory> list = dumper.retrieveModelKeyList(curDate, curDate);
			long endListTime = new Date().getTime();
			
			if( list.size() > 0 ) {
				File f = new File(dumper.resolveDumpFileName(curDate, null));
				log.log(Level.INFO, "Resolving Wifi Locations for Date " + curDate + " in File " + f + " with " + list.size() + " records in " + (endListTime - initListTime) + "ms");
			}

			int count = 0;
			// dry run
			for( DeviceWifiLocationHistory element : list ) {
				if(!StringUtils.hasText(element.getWifiSpotId())) {
					service.calculateWifiSpot(element);
					if(StringUtils.hasText(element.getWifiSpotId())) {
						count++;
						break;
					}
				}
			}
			
			// real run
			if( count > 0 ) {
				count = 0;
				for( DeviceWifiLocationHistory element : list ) {
					if(!StringUtils.hasText(element.getWifiSpotId())) {
						service.calculateWifiSpot(element);
						if(StringUtils.hasText(element.getWifiSpotId())) {
							count++;
							totals++;
						}
					}
					tmpDumper.dump(element);
				}
			}
			
			if( count > 0 ) {
				log.log(Level.INFO, count + " elements calculated for this file");
				File f1 = new File(dumper.resolveDumpFileName(curDate, null));
				File f2 = new File(tmpDumper.resolveDumpFileName(curDate, null));
				Files.copy(f2, f1);
				f2.delete();
			}
			curDate = new Date(curDate.getTime() + 3600000);
		}
		
		long endTime = new Date().getTime();
		log.log(Level.INFO, totals + " elements calculated in " + (endTime - initTime) + "ms for this process");
		
	}
	
}
