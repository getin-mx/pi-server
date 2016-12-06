package mobi.allshoppings.cli;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;


public class ExecuteAPDeviceCommand extends AbstractCLI {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ExecuteAPDeviceCommand.class.getName());
	private static OptionSpec<String> cmdArgs;
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "hostname", "APDevice Hostname (for example ashs-0016)" ).withRequiredArg().ofType( String.class );
		parser.accepts( "out", "Output file" ).withRequiredArg().ofType( String.class );
		cmdArgs = parser.nonOptions();
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		int exitStatus = 0;
		try {
			
			APDeviceHelper helper = (APDeviceHelper)getApplicationContext().getBean("apdevice.helper");
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String hostname = null;
			String command = "";
			String out = null;
			
			try {
				if( options.has("help")) usage(parser);
				if( options.has("hostname")) 
					hostname = (String)options.valueOf("hostname");
				else 
					usage(parser);
				if( options.has("out")) 
					out = (String)options.valueOf("out");

				for(String s : options.valuesOf(cmdArgs)) {
					command += s + " ";
				}
				command.trim();
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			StringBuffer stdout = new StringBuffer();
			StringBuffer stderr = new StringBuffer();
			exitStatus = helper.executeCommandOnAPDevice(hostname, command, stdout, stderr);

			if( out == null ) {
				System.out.println(stdout.toString());
				System.err.println(stderr.toString());
			} else {
				File f = new File(out);
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(stdout.toString().getBytes());
				fos.write(stderr.toString().getBytes());
				fos.close();
			}
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(exitStatus);
	}
	
}
