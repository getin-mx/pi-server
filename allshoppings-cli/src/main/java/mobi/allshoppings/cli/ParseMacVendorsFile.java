package mobi.allshoppings.cli;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;


public class ParseMacVendorsFile extends AbstractCLI {

	private static final Logger log = Logger.getLogger(ParseMacVendorsFile.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "file", "Input file. Can be a local file or a URL" ).withRequiredArg().ofType( String.class );
		parser.accepts( "outfile", "Output file" ).withRequiredArg().ofType( String.class );
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

			String filename = null;
			String outfile = null;
			
			try {
				if( options.has("help")) usage(parser);
				if( options.has("file")) 
					filename = (String)options.valueOf("file");
				else 
					usage(parser);
				
				if( options.has("outfile"))
					outfile = (String)options.valueOf("outfile");
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Parsing Mac Addresses from " + filename);
			if( outfile == null )
				helper.updateMacVendors(filename);
			else
				helper.macVendorFileParser(filename, outfile);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
