package mobi.allshoppings.cli;

import java.util.Arrays;
import java.util.Date;

import joptsimple.OptionParser;

import mobi.allshoppings.cinepolis.services.SendMovieTicketsService;


public class CinepolisSendShowTickets extends AbstractCLI {

	private static final long FIFTEEN_MINUTES = 900000;
	private static final long THIRTY_MINUTES = 1800000;

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws Exception {

		SendMovieTicketsService service = new SendMovieTicketsService();
		service.doProcess(
				new Date(),
				THIRTY_MINUTES,
				"http://api.allshoppings.mobi/app/externalActivityTrigger?authToken="
						+ "2A2C192578F597CEE6BC3EE8A26B9F7DB0DBA4B0CD8930FDC3D2BB0E7D1B8CC9",
				FIFTEEN_MINUTES, null, null, true,
				Arrays.asList(new String[] { "cinepolis_mx_96", "cinepolis_mx_339", "cinepolis_mx_479", "cinepolis_mx_449" }), false,
				false, null, false, true);
		
		System.exit(0);

	}
}
