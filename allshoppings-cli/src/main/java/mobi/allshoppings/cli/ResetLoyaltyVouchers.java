package mobi.allshoppings.cli;

import joptsimple.OptionParser;
import mobi.allshoppings.cinepolis.services.ResetLoyaltyVouchersService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;


public class ResetLoyaltyVouchers extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws ASException {

		try {
			// 0 seconds
			long CERO = 0;
			
			ResetLoyaltyVouchersService service = new ResetLoyaltyVouchersService();
			service.doProcess(CERO);
			System.exit(0);
			
		} catch( Throwable t ) {
			throw ASExceptionHelper.defaultException(t.getMessage(), t);
		}
		
	}

}
