package mobi.allshoppings.cli;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.push.PushMessageHelper;


public class SendTestMessage extends AbstractCLI {

	private static final Logger log = Logger.getLogger(SendTestMessage.class.getName());

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "deviceUUID", "deviceUUID" ).withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void main(String args[]) throws ASException {
		try {
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String deviceUUID = null;
			
			try {
				if( options.has("deviceUUID")) deviceUUID = (String)options.valueOf("deviceUUID");
				else usage(parser);

			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			DeviceInfoDAO deviceInfoDao = (DeviceInfoDAO)getApplicationContext().getBean("deviceinfo.dao.ref");
			PushMessageHelper pushHelper = (PushMessageHelper)getApplicationContext().getBean("push.message.helper");
			
			log.log(Level.INFO, "Sending test message to " + deviceUUID);
			DeviceInfo device = deviceInfoDao.get(deviceUUID);
			pushHelper.sendMessage("Hola Mundo", "Este es un mensaje", "http://www.google.com", device);

			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
