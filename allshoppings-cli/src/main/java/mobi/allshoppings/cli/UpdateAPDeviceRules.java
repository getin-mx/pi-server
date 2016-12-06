package mobi.allshoppings.cli;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;


public class UpdateAPDeviceRules extends AbstractCLI {

	private static final Logger log = Logger.getLogger(UpdateAPDeviceRules.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "hostname", "APDevice Hostname list, separated by comma (for example ashs-0016,ashs-0017)" ).withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {

			System.out.println("Don't mess with it!!!! Get Out!!!");
			System.exit(-1);
			
			APDeviceHelper helper = (APDeviceHelper)getApplicationContext().getBean("apdevice.helper");
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String hostname = null;
			List<String> hostnames = null;
			
			try {
				if( options.has("help")) usage(parser);
				if( options.has("hostname")) 
					hostname = (String)options.valueOf("hostname");
				
				if( hostname != null ) {
					hostnames = Arrays.asList(hostname.split(","));
				}
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Updating Hostnames Rules");
			helper.importParametersFromLegacy(hostnames);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
