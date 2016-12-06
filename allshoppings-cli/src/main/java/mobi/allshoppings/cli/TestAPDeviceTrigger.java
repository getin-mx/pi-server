package mobi.allshoppings.cli;

import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.apdevice.impl.APDeviceTriggerStartVisitImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;


public class TestAPDeviceTrigger extends AbstractCLI {

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
//			OptionSet options = parser.parse(args);
//
//			String deviceUUID = null;
			
			try {
//				if( options.has("deviceUUID")) deviceUUID = (String)options.valueOf("deviceUUID");
//				else usage(parser);

			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			APDeviceTriggerStartVisitImpl trigger = new APDeviceTriggerStartVisitImpl();
			JSONObject metadata = new JSONObject();
			metadata.put("timeLimit", 0);
			metadata.put("message", "Bienvenido a Casa Mat!");
			trigger.execute("ashs-9005", "28:27:bf:d2:d9:c8", -70, metadata.toString());
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
