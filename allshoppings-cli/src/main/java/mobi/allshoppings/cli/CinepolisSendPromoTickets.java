package mobi.allshoppings.cli;

import java.util.Arrays;
import java.util.Date;

import joptsimple.OptionParser;
import mobi.allshoppings.cinepolis.services.SendPromoTicketsService;


public class CinepolisSendPromoTickets extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws Exception {

		SendPromoTicketsService service = new SendPromoTicketsService();
		service.doProcess(
				new Date(),
				"http://api.allshoppings.mobi/app/externalActivityTrigger?authToken="
						+ "2A2C192578F597CEE6BC3EE8A26B9F7DB0DBA4B0CD8930FDC3D2BB0E7D1B8CC9",
						3600000 /* 1 hour */,
						null, null, Arrays.asList(new String[] { "cinepolis_mx_339", "cinepolis_mx_449", "cinepolis_mx_479" }), 
						false, false, null, false);

		System.exit(0);

	}
}
