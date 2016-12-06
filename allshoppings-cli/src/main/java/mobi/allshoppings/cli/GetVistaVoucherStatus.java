package mobi.allshoppings.cli;

import java.util.Map;

import joptsimple.OptionParser;
import mobi.allshoppings.cinepolis.vista.voucher.VistaVoucherService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;


public class GetVistaVoucherStatus extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws ASException {

		try {
			VistaVoucherService vs = VistaVoucherService.getInstance();
			Map<String, String> response = vs.getStatus(args[0]);
			System.out.println(response);
			System.exit(0);
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}

	}
}
