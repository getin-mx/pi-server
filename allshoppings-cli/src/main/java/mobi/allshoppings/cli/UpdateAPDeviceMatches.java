package mobi.allshoppings.cli;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;


public class UpdateAPDeviceMatches extends AbstractCLI {

	private static final Logger log = Logger.getLogger(UpdateAPDeviceMatches.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			APDeviceHelper helper = (APDeviceHelper)getApplicationContext().getBean("apdevice.helper");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			@SuppressWarnings("unused")
			OptionSet options = parser.parse(args);
			
			log.log(Level.INFO, "Updating AP Deivce with Mac Addresses Matches");
			helper.updateAPDeviceMacMatch();
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
