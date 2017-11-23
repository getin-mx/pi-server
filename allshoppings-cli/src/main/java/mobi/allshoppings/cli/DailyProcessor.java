package mobi.allshoppings.cli;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.inodes.util.CollectionFactory;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DailyProcessConfiguration;

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
	
	private static final Logger LOG = Logger.getLogger(DailyProcessor.class.getName());
	
	private static final String GENERATE_APHE_PARAM = "generateAPHE";
	private static final String FROM_DATE_PARAM = "fromDate";
	private static final String TO_DATE_PARAM = "toDate";
	private static final String SERVERS_CONF_PARAM = "serversLoadFile";
	private static final String DEFAULT_CONF_FILE = Paths.get("usr", "local",
			"allshoppings", "etc", "dailyConf.json").toString();
	public static final int TWENTY_FOUR_HOURS = 86400000;
	
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
		parser.accepts(SERVERS_CONF_PARAM, "File in JSON format which describes how "
				+ "the load will be balanced across productive servers. Defaults to "
				+ DEFAULT_CONF_FILE).withOptionalArg().ofType(String.class);
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
	public static void main(String[] args) throws ASException {
		OptionSet options = parser.parse(args);
		boolean generateAPHE = options.has(GENERATE_APHE_PARAM) &&
				(Boolean)options.valueOf(GENERATE_APHE_PARAM);
		Date fromDate = retrieveDate(options, FROM_DATE_PARAM);
		Date toDate = retrieveDate(options, TO_DATE_PARAM);
		
		boolean fallback = !options.has(SERVERS_CONF_PARAM);
		DailyProcessConfiguration conf;
		Gson gson = new Gson();
		JsonReader reader = null;
		if(!fallback) {
			try {
				reader = new JsonReader(new FileReader(
						options.valueOf(SERVERS_CONF_PARAM).toString()));
			} catch(IOException e) {
				LOG.log(Level.WARNING, "Could not open given configuration file ", e);
				fallback = true;
			}
		} if(fallback) {
			try {
				reader = new JsonReader(new FileReader(DEFAULT_CONF_FILE));
			} catch(IOException e) {
				throw ASExceptionHelper.notFoundException(e.getMessage());
			}
		}
		conf = gson.fromJson(reader, DailyProcessConfiguration.class);
		try {
			reader.close();
		} catch(IOException e) {
			LOG.log(Level.WARNING, "Could not close GSON reader", e);
		}
		List<Float> netLoads = CollectionFactory.createList();
		float totalLoad = 0;
		float uniformPerc = -1;
		for(int i = 0; i < conf.getServerList().length; i++) {
			if(i < conf.getServersLoad().length) {
				float f = conf.getServersLoad()[i];
				if(f < 0 || f > 1) throw ASExceptionHelper.defaultException("Server "
						+ "loads must be a number in the closed range [0,1]; and must "
						+ "sum 1 or less in total. These loads represent the (minimum)"
						+ " percentage of all antennas to process by each server",
						null);
				totalLoad += f; 
				if(totalLoad > 1) throw ASExceptionHelper.defaultException("Server "
						+ "loads must sum 1 or less. If less, a uniform load will be "
						+ "given to the last servers on the list", null);
				netLoads.add(f);
			} else {
				if(uniformPerc < 0) {
					uniformPerc = (1 -totalLoad)
							/(conf.getServerList().length -i);
					if(uniformPerc <= 0) throw ASExceptionHelper.defaultException(
							"Cannot guess a uniform percentage for all servers",
							null);
				}
				
			} 
			
		}
		LOG.log(Level.INFO, "Distributing daily process on servers and loads: "
				+ " using conf. File " + " for dates ");
		if(generateAPHE) LOG.log(Level.INFO, "Each server will generate their "
				+ "corresponding APHEntries first");
		// TODO separate antennas
		// TODO ssh command
	}//main
	
	// helper static methods
	
	private static Date retrieveDate(OptionSet options, String param) throws ASException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		if(options.has(param)) {
			try {
				date = sdf.parse(options.valueOf(param).toString());
			} catch(ParseException e) {
				throw ASExceptionHelper.invalidArgumentsException(param);
			}
		} if(date == null) {
			try {
				date = sdf.parse(sdf.format(new Date(
						System.currentTimeMillis() -TWENTY_FOUR_HOURS)));
			} catch(ParseException e) {
				throw ASExceptionHelper.invalidArgumentsException(param);
			}
		}
		return date;
	}
	
}//DailyProcessor
