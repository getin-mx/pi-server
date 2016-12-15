package mobi.allshoppings.cli;

import java.util.Arrays;

import joptsimple.OptionParser;
import mobi.allshoppings.cinepolis.services.CinepolisLocationRequesterService;


public class CinepolisRequestLocation extends AbstractCLI {

	private final static long ONE_HOUR = 3600000;
	private final static long ONE_DAY = ONE_HOUR * 24;
	private final static long THIRTY_MINUTES = 1800000;

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws Exception {

		CinepolisLocationRequesterService service = new CinepolisLocationRequesterService();
		service.requestLocation(
				Arrays.asList(new String[] { "cinepolis_mx_96", "cinepolis_mx_339", "cinepolis_mx_479", "cinepolis_mx_449" }), 
				"http://api.allshoppings.mobi/app/requestDeviceLocation?authToken=" +
				"2A2C192578F597CEE6BC3EE8A26B9F7DB0DBA4B0CD8930FDC3D2BB0E7D1B8CC9", 3000, ONE_DAY, 20, true, THIRTY_MINUTES);

		System.exit(0);

	}
}
