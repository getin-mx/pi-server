package mobi.allshoppings.cli;

import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import com.inodes.util.CollectionFactory;


public class UpdateAPDevice extends AbstractCLI {

	private static final Logger log = Logger.getLogger(UpdateAPDevice.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "hostname", "APDevice Hostname (for example ashs-0016)" ).withRequiredArg().ofType( String.class );
		parser.accepts( "description", "APDevice Description (for example 'Chilim Balam Santa Fe')" ).withRequiredArg().ofType( String.class );
		parser.accepts( "reportable", "If the device needs to report his status (true, false)" ).withRequiredArg().ofType( Boolean.class );
		parser.accepts( "mails", "A comma separated list of reportees by email" ).withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			
			APDeviceHelper helper = (APDeviceHelper)getApplicationContext().getBean("apdevice.helper");
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String hostname = null;
			String description = null;
			Boolean reportable = false;
			String mails = null;
			List<String> emails = CollectionFactory.createList();
			
			try {
				if( options.has("help")) usage(parser);
				if( options.has("hostname")) 
					hostname = (String)options.valueOf("hostname");
				else 
					usage(parser);
				
				if( options.has("description")) description = ((String)options.valueOf("description")).replaceAll("_", " ");
				if( options.has("reportable")) reportable = (Boolean)options.valueOf("reportable");
				if( options.has("mails")) mails = (String)options.valueOf("mails");
				if( StringUtils.hasText(mails) ) {
					String[] list = mails.split(",");
					for( String o : list ) {
						emails.add(o.trim());
					}
				}

			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Updating hostname " + hostname);
			helper.updateDeviceData(hostname, description, reportable, emails);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
