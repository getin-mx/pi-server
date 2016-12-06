package mobi.allshoppings.cli;

import joptsimple.OptionParser;
import mobi.allshoppings.location.LocationRequesterService;

public class StartLocationRequester extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) {
		
		LocationRequesterService service = new LocationRequesterService();
		while(true) {
			try {
				Thread.sleep(5000);
				// prints the service status
				service.requestLocation("http://api.allshoppings.mobi/app/requestDeviceLocation?authToken=" +
					"2A2C192578F597CEE6BC3EE8A26B9F7DB0DBA4B0CD8930FDC3D2BB0E7D1B8CC9", 30, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
