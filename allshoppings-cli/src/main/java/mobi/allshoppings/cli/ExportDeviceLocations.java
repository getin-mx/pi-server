package mobi.allshoppings.cli;

import java.io.File;
import java.text.SimpleDateFormat;

import joptsimple.OptionParser;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.location.LocationHistoryExporterService;


public class ExportDeviceLocations extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws ASException {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			File f1 = new File("/tmp/deviceLocation.json");
			File f2 = new File("/tmp/deviceInfo.json");
			
			LocationHistoryExporterService service = new LocationHistoryExporterService();
			service.exportLocationHistory(19.440671, -99.121182, 20000, sdf.parse("2015-04-01"), sdf.parse("2015-07-30"), f1, f2, 1000, true, null);
			
			System.exit(0);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}

	}
}
