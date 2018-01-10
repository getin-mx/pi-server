package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.tools.CollectionFactory;

public class DumpAPHHostpotFromMordor extends AbstractCLI {

private static final Logger log = Logger.getLogger(DumpHistory.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "fromDate", "Date From" ).withRequiredArg().ofType( String.class );
		parser.accepts( "toDate", "Date To" ).withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void main(String args[]) throws ASException {
		DumperHelper<APHotspot> dumpHelper;
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			TimeZone tz = TimeZone.getTimeZone("GMT");
			sdf.setTimeZone(tz);
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String sFromDate = null;
			String sToDate = null;
			Date fromDate = null;
			Date toDate = null;

			try {
				if( options.has("help")) usage(parser);
				if( options.has("fromDate")) sFromDate = (String)options.valueOf("fromDate");
				if( options.has("toDate")) sToDate = (String)options.valueOf("toDate");

				if( StringUtils.hasText(sFromDate)) {
					fromDate = sdf.parse(sFromDate);
				} else {
					fromDate = new Date(0L);
				}
				
				if( StringUtils.hasText(sToDate)) {
					toDate = sdf.parse(sToDate);
				} else {
					toDate = new Date();
				}
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			// Gets the input data
			long totals = 0;
			log.log(Level.INFO, "Processing Dump Records");
			dumpHelper = new DumpFactory<APHotspot>().build(null, APHotspot.class, false);
			
			List<String> optionss = CollectionFactory.createList();
			optionss = dumpHelper.getMultipleNameOptions(fromDate);
			
			for( String hostname : optionss ){
				log.log(Level.INFO, "Processing " + hostname + " for date " + fromDate + "...");
				
				dumpHelper = new DumpFactory<APHotspot>().build(null, APHotspot.class, false);
				dumpHelper.setFilter(hostname);
				Iterator<String> i = dumpHelper.stringIterator(fromDate, toDate, false);
				JSONObject json;
				while( i.hasNext() ) {
					json = new JSONObject(i.next());
					if( totals % 1000 == 0 ) 
						log.log(Level.INFO, "Processing for date " + new Date(json.getLong("creationDateTime")) + " with "+totals+" ...");

					json.getString("mac");
					
					totals++;
				}
			}

			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}

}
