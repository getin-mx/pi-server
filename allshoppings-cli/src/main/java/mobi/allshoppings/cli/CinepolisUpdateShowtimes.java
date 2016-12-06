package mobi.allshoppings.cli;

import joptsimple.OptionParser;
import mobi.allshoppings.cinepolis.services.UpdateShowtimesService;
import mobi.allshoppings.exception.ASException;


public class CinepolisUpdateShowtimes extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws ASException {
		
		UpdateShowtimesService service = new UpdateShowtimesService();
		service.doUpdate();
		System.exit(0);

	}
}
