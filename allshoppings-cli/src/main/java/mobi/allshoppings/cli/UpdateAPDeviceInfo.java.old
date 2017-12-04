package mobi.allshoppings.cli;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.interfaces.StatusAware;


public class UpdateAPDeviceInfo extends AbstractCLI {

	private static final Logger log = Logger.getLogger(UpdateAPDeviceInfo.class.getName());
	private static final long ONE_HOUR = 3600000;
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "hostname", "APDevice Hostname (for example ashs-0016)" ).withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			
			APDeviceHelper helper = (APDeviceHelper)getApplicationContext().getBean("apdevice.helper");
			APDeviceDAO dao = (APDeviceDAO)getApplicationContext().getBean("apdevice.dao.ref");
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String hostname = null;
			
			try {
				if( options.has("help")) usage(parser);
				if( options.has("hostname")) 
					hostname = (String)options.valueOf("hostname");
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			if( hostname != null ) {
				log.log(Level.INFO, "Updating info for hostname " + hostname);
				helper.updateAPDeviceInfo(hostname);
			} else {
				Date oneHour = new Date(new Date().getTime() - ONE_HOUR);
				List<APDevice> list = dao.getAllAndOrder("hostname", true);
				for(APDevice device : list ) {
					try {
						if (device.getStatus() != null 
								&& !device.getStatus().equals(StatusAware.STATUS_DISABLED)
								&& device.getLastRecordDate() != null && device.getLastRecordDate().after(oneHour)) {
							log.log(Level.INFO, "Updating info for hostname " + device.getIdentifier());
							helper.updateAPDeviceInfo(device.getIdentifier());
						}
					} catch( Exception e ) {
						log.log(Level.ERROR, e.getMessage(), e);
					}
				}
			}
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
