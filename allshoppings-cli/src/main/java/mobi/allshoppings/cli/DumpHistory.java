package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.interfaces.ModelKey;


public class DumpHistory extends AbstractCLI {

	private static final Logger log = Logger.getLogger(DumpHistory.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "fromDate", "Date From" ).withRequiredArg().ofType( String.class );
		parser.accepts( "toDate", "Date To" ).withRequiredArg().ofType( String.class );
		parser.accepts( "entity", "Entity to dump (for example, DeviceLocationHistory)")
				.withRequiredArg().ofType( String.class );
		parser.accepts( "collection", "DB Collection to dump (for example, DeviceLocationHistory)")
				.withRequiredArg().ofType( String.class );
		parser.accepts( "deleteAfterDump", "Do I have to delete the entity from the DB after dump?")
				.withRequiredArg().ofType( Boolean.class );
		parser.accepts( "renameCollection", "Do I have to rename the collection before run?")
				.withRequiredArg().ofType( Boolean.class );
		return parser;
	}

	@SuppressWarnings("unchecked")
	public static void main(String args[]) throws ASException {
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

			String sEntity = null;
			String sCollection = null;
			boolean deleteAfterDump = true;
			boolean renameCollection = false;
			
			Class<ModelKey> entity = null;
			
			try {
				if( options.has("help")) usage(parser);
				if( options.has("fromDate")) sFromDate = (String)options.valueOf("fromDate");
				if( options.has("toDate")) sToDate = (String)options.valueOf("toDate");
				if( options.has("deleteAfterDump")) deleteAfterDump = (boolean)options.valueOf("deleteAfterDump");
				if( options.has("usePlugins")) deleteAfterDump = (boolean)options.valueOf("usePlugins");
				if( options.has("renameCollection")) renameCollection =
						(boolean)options.valueOf("renameCollection");
				
				if( options.has("entity")) sEntity = (String)options.valueOf("entity");
				else usage(parser);
				entity = (Class<ModelKey>)Class.forName("mobi.allshoppings.model." + sEntity);
				
				if( options.has("collection")) sCollection = (String)options.valueOf("collection");
				else sCollection = new String(sEntity);

				fromDate = StringUtils.hasText(sFromDate) ? sdf.parse(sFromDate) :
					new Date(0L);
				toDate = StringUtils.hasText(sToDate) ? sdf.parse(sToDate) :
					new Date();
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Starting dump for entity " + entity.getName()
					+ " from " + fromDate + " to " + toDate);
			DumperHelper<ModelKey> dumper = new DumpFactory<ModelKey>().build(null, entity);
			
			dumper.dumpModelKey(sCollection, fromDate, toDate, deleteAfterDump, renameCollection);
			
			dumper.dispose();
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
