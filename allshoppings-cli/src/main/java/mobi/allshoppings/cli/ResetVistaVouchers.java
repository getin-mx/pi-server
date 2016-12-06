package mobi.allshoppings.cli;

import joptsimple.OptionParser;
import mobi.allshoppings.cinepolis.services.ResetVistaVouchersService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;


public class ResetVistaVouchers extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws ASException {

		try {
			long ONE_HOUR_TEN = 4200000;
			
			ResetVistaVouchersService service = new ResetVistaVouchersService();
			service.doProcess(ONE_HOUR_TEN);
			System.exit(0);
			
		} catch( Throwable t ) {
			throw ASExceptionHelper.defaultException(t.getMessage(), t);
		}
		
	}

}
