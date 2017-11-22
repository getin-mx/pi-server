package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Using a configurations file, this program executes the
 * GenerateAPHE and GenerateAPDVisits CLIs in as much servers
 * as intended via SSH. Servers load may be unbalanced; if
 * servers capacity is different or other reasons.
 * 
 * For naming; we'll call "main server" to the one which
 * launches this program, and "slave servers" to the servers
 * which will start the GenerateAPHE and GenerateAPDVisists
 * CLIs whithin the main server.
 * 
 * Using the Proccess database's table, the main server will
 * know when slaves have finished. This feature will be used
 * to launch the GenerateAPH and GenerateAPDVisits CLIs
 * for each day given, waiting for every server to finish,
 * so more days can be processed.
 * 
 * @author <a href="mailto:ignacio@getin.mx">Ignacio
 * "Nachintoch" Castillo</a>
 * @version 1.0, november 2017
 * @since Mark III, GetIn-Allshoppings
 *
 */
public class DailyProcessor extends AbstractCLI {

	// class attributes
	
	private static final Logger log = Logger.getLogger(DailyProcessor.class.getName());
	
	private static final String GENERATE_APHE_PARAM = "generateAPHE";
	private static final String FROM_DATE_PARAM = "fromDate";
	private static final String TO_DATE_PARAM = "toDate";
	
	// static methods
	
	/**
	 * Builds the option parser.
	 * @param base - The source optiions; if any.
	 * @return OptionParser - The CLI's option parser.
	 * @since Daily Processor 1.0, november 2017
	 */
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts(GENERATE_APHE_PARAM, "If the GenerateAPHE CLI should be "
				+ "executed before GenerateAPDVisits").withOptionalArg()
				.ofType(String.class);
		parser.accepts(FROM_DATE_PARAM, "The date from which visits should be "
				+ "processed.").withOptionalArg().ofType(String.class);
		parser.accepts(TO_DATE_PARAM, "Limit date to process").withOptionalArg()
				.ofType(String.class);
		return parser;
	}//buildOptionParser
	
	// main method
	
	/**
	 * Test the validity of the arguments and then launches the remote process.
	 * Then the program sleeps for five minutes until a server finishes; if there
	 * are pending operations, they are sended to the remote. This repeats until
	 * no more pending tasks remain.
	 * @param args - Those are parsed using the OptionsParser.
	 * @since Daily Process 1.0, november 2017
	 */
	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		OptionSet options = parser.parse(args);
		
	}//main
	
}//DailyProcessor
