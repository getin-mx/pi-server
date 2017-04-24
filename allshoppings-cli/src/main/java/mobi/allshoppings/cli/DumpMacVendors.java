package mobi.allshoppings.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.MacVendorDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.MacVendor;


public class DumpMacVendors extends AbstractCLI {

	private static final Logger log = Logger.getLogger(DumpMacVendors.class.getName());
	
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
			
			MacVendorDAO mvDao = (MacVendorDAO)getApplicationContext().getBean("macvendor.dao.ref");
			
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String filename = null;

			try {
				if( options.has("help")) usage(parser);
				if( options.has("file")) 
					filename = (String)options.valueOf("file");
				else 
					usage(parser);
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Parsing Mac Addresses to " + filename);
			File f = new File(filename);
			f.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(f);
			
			List<MacVendor> list = mvDao.getAll();
			for( MacVendor mv : list ) {
				if( mv.getCode().equals("Apple")) {
					fos.write((mv.getMac() + "\n").getBytes());
				}
			}
			
			fos.close();
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
