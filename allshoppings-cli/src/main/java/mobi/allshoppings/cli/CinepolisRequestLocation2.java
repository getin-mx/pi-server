package mobi.allshoppings.cli;

import java.util.Arrays;

import joptsimple.OptionParser;
import mobi.allshoppings.cinepolis.services.CinepolisLocationRequesterService;


public class CinepolisRequestLocation2 extends AbstractCLI {

	private final static long ONE_HOUR = 3600000;
	private final static long FIVE_HOURS = ONE_HOUR * 5;
	private final static long TWENTYFIVE_MINS = 1500000; 

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws Exception {

		CinepolisLocationRequesterService service = new CinepolisLocationRequesterService();
		service.requestLocation(
				Arrays.asList(new String[] { "cinepolis_mx_96" }), 
				"http://api.allshoppings.mobi/app/requestDeviceLocation?authToken=" +
				"2A2C192578F597CEE6BC3EE8A26B9F7DB0DBA4B0CD8930FDC3D2BB0E7D1B8CC9", 3000, FIVE_HOURS, 20, true, TWENTYFIVE_MINS);

		System.exit(0);

	}
}
