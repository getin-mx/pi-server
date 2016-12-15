package mobi.allshoppings.cli;

import java.util.Arrays;
import java.util.Date;

import joptsimple.OptionParser;
import mobi.allshoppings.cinepolis.services.SendMovieTicketsService;


public class CinepolisSendShowTickets2 extends AbstractCLI {

	private static final long TWENTY_MINUTES = 1200000;
	private static final long SEVEN_MINUTES = 420000;

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws Exception {

		SendMovieTicketsService service = new SendMovieTicketsService();
		service.doProcess(
				new Date(),
				TWENTY_MINUTES,
				"http://api.allshoppings.mobi/app/externalActivityTrigger?authToken="
						+ "2A2C192578F597CEE6BC3EE8A26B9F7DB0DBA4B0CD8930FDC3D2BB0E7D1B8CC9",
				SEVEN_MINUTES, null, null, true,
				Arrays.asList(new String[] { "cinepolis_mx_96" }), false,
				false, null, false, true);
		System.exit(0);

	}
}
