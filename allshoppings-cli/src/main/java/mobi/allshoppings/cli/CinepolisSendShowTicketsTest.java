package mobi.allshoppings.cli;

import java.util.Arrays;
import java.util.Date;

import joptsimple.OptionParser;
import mobi.allshoppings.cinepolis.services.SendMovieTicketsService;


public class CinepolisSendShowTicketsTest extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws Exception {

		SendMovieTicketsService service = new SendMovieTicketsService();
		service.doProcess(
				new Date(),
				// 360000, /* 6 minutes */
				86400000, /* 24 hours */
				"http://localhost:8081/appv2/externalActivityTrigger?authToken="
//				"http://api.allshoppings.mobi/app/externalActivityTrigger?authToken="
						+ "2A2C192578F597CEE6BC3EE8A26B9F7DB0DBA4B0CD8930FDC3D2BB0E7D1B8CC9",
				600000 /* 10 minutes */, TestDevices.testDevices, TestDevices.testDevices, false,
				Arrays.asList(new String[] { "cinepolis_mx_339" }), true,
				true, null, true, true);
		
		System.exit(0);

	}
}
