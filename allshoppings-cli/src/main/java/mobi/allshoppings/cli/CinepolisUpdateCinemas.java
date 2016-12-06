package mobi.allshoppings.cli;

import joptsimple.OptionParser;
import mobi.allshoppings.cinepolis.services.UpdateCinepolisCinemasService;
import mobi.allshoppings.exception.ASException;


public class CinepolisUpdateCinemas extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws ASException {
		
		UpdateCinepolisCinemasService service = new UpdateCinepolisCinemasService();
		service.doUpdate();
		System.exit(0);

	}
}
