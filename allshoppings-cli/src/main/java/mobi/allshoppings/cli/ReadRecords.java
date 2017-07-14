package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.apdevice.APHHelper;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.dump.impl.DumperHelperImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.CollectionFactory;


public class ReadRecords extends AbstractCLI {

	private static final Logger log = Logger.getLogger(ReadRecords.class.getName());
	public static final long TWENTY_FOUR_HOURS = 86400000;
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "outDir", "Output Directory (for example, /tmp/dump)").withRequiredArg().ofType( String.class );
		parser.accepts( "fromDate", "Date From" ).withRequiredArg().ofType( String.class );
		parser.accepts( "toDate", "Date To" ).withRequiredArg().ofType( String.class );
		parser.accepts( "hostname", "APHostname").withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			APDeviceDAO apdDao = (APDeviceDAO)getApplicationContext().getBean("apdevice.dao.ref");
			APHHelper helper = (APHHelper)getApplicationContext().getBean("aphentry.helper");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String sFromDate = null;
			String sToDate = null;
			Date fromDate = null;
			Date toDate = null;
			String hostname = null;
			String sOutDir = null;
			
			try {
				if( options.has("fromDate")) sFromDate = (String)options.valueOf("fromDate");
				if( options.has("toDate")) sToDate = (String)options.valueOf("toDate");
				
				if( StringUtils.hasText(sFromDate)) {
					fromDate = sdf.parse(sFromDate);
				} else {
					fromDate = sdf.parse(sdf.format(new Date(new Date().getTime() - 86400000 /* 24 hours */)));
				}
				
				if( StringUtils.hasText(sToDate)) {
					toDate = sdf.parse(sToDate);
				} else {
					toDate = new Date(fromDate.getTime() + 86400000 /* 24 hours */);
				}
				
				if( options.has("hostname")) {
					hostname = (String)options.valueOf("hostname");
				}
				
				if( options.has("outDir")) sOutDir = (String)options.valueOf("outDir");
				else usage(parser);
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Generating APHEntries");
			Map<String,APDevice> apdevices = CollectionFactory.createMap();
			if(StringUtils.hasText(hostname)) {
				apdevices.put(hostname, apdDao.get(hostname, true));
			} else {
				List<APDevice> l = apdDao.getAll();
				for( APDevice dev : l )
					apdevices.put(dev.getHostname(), dev);
			}
			
			helper.setScanInDevices(false);

			DumperHelper<APHotspot> dumpHelper;
			
			Date ftoDate = new Date(fromDate.getTime());
			while( ftoDate.before(toDate)) {

				ftoDate = new Date(ftoDate.getTime() + TWENTY_FOUR_HOURS);
				
				long totals = 0;

				dumpHelper = new DumpFactory<APHotspot>().build(sOutDir, APHotspot.class);
				Iterator<String> i = dumpHelper.stringIterator(fromDate, toDate);
				while( i.hasNext() ) {
					String s = i.next();
					JSONObject json = new JSONObject(s);
					if( totals % 1000 == 0 ) 
						log.log(Level.INFO, "Processing for date " + json.getString("creationDateTime"));

					totals++;
				}
			}

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
