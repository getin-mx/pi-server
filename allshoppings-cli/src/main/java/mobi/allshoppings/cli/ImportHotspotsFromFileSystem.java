package mobi.allshoppings.cli;

import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;


public class ImportHotspotsFromFileSystem extends AbstractCLI {

	private static final Logger log = Logger.getLogger(ImportHotspotsFromFileSystem.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "input", "Input Directory" ).withRequiredArg().ofType( String.class );
		parser.accepts( "output", "Output Directory" ).withRequiredArg().ofType( String.class );
		parser.accepts( "fakeUnixTime", "First Unix Time (for faking porpouses)" ).withRequiredArg().ofType( Long.class );
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

			String input = null;
			String output = null;
			Long firstUnixTime = 0L;
			Date firstDate = null;
			
			try {
				if( options.has("help")) usage(parser);
				if( options.has("input")) input = (String)options.valueOf("input");
				if( options.has("output")) output = (String)options.valueOf("output");
				if( options.has("fakeUnixTime")) firstUnixTime = (Long)options.valueOf("fakeUnixTime");

				if(!StringUtils.hasText(input)) usage(parser);
				if(!StringUtils.hasText(output)) usage(parser);
				if( firstUnixTime > 0 ) 
					firstDate = new Date(firstUnixTime * 1000);
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Starting to import file system data from " + input + " with backup " + output);
			helper.importRecordsFromFileSystem(input, output, firstDate);			
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
