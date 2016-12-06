package mobi.allshoppings.cli;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;


public class GetAPDeviceFile extends AbstractCLI {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(GetAPDeviceFile.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "hostname", "APDevice Hostname (for example ashs-0016)" ).withRequiredArg().ofType( String.class );
		parser.accepts( "file", "File to get (for example, /etc/hosts)" ).withRequiredArg().ofType( String.class );
		parser.accepts( "out", "Output file" ).withRequiredArg().ofType( String.class );
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
			String filename = null;
			String out = null;
			
			try {
				if( options.has("help")) usage(parser);
				if( options.has("hostname")) 
					hostname = (String)options.valueOf("hostname");
				else 
					usage(parser);
				if( options.has("file")) 
					filename = (String)options.valueOf("file");
				else 
					usage(parser);
				if( options.has("out")) 
					out = (String)options.valueOf("out");
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			if( out == null ) {
				System.out.println(new String(helper.getFileFromAPDevice(hostname, filename)));
			} else {
				File f = new File(out);
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(helper.getFileFromAPDevice(hostname, filename));
				fos.close();
			}
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
