package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.exporter.ExternalGeoImporter;


public class UpdateExternalGeoGPS extends AbstractCLI {

	private static final Logger log = Logger.getLogger(UpdateExternalGeoGPS.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "hostname", "APDevice Hostname (for example ashs-0016)" ).withRequiredArg().ofType( String.class );
		parser.accepts( "entityId", "Entity Id" ).withRequiredArg().ofType( String.class );
		parser.accepts( "entityKind", "Entity Kind" ).withRequiredArg().ofType( Integer.class );
		parser.accepts( "type", "ExternalGeo.type" ).withRequiredArg().ofType( Integer.class );
		parser.accepts( "period", "Period (for example 2016-04)" ).withRequiredArg().ofType( String.class );
		parser.accepts( "fromDate", "Date to start gps readings (for example 2016-06-01)" ).withRequiredArg().ofType( String.class );
		parser.accepts( "toDate", "Date to end gps readings (for example 2016-07-01)" ).withRequiredArg().ofType( String.class );
		parser.accepts( "fromHour", "Hour filter to start gps readings (for example 02:00)" ).withRequiredArg().ofType( String.class );
		parser.accepts( "toHour", "Hour filter to start gps readings (for example 05:00)" ).withRequiredArg().ofType( String.class );
		parser.accepts( "outDir", "Output Directory (for example, /tmp/dump)").withRequiredArg().ofType( String.class );
		parser.accepts( "workDays", "Boolean. Only Ẃork Days").withRequiredArg().ofType( Boolean.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			ExternalGeoImporter importer = (ExternalGeoImporter)getApplicationContext().getBean("external.geo.importer");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String hostname = null;
			String entityId = null;
			Integer entityKind = null;
			String period = null;
			String outDir = null;
			String sFromDate = null;
			String sToDate = null;
			String toHour = null;
			String fromHour = null;
			Integer type = null;
			boolean workDays = false;
			
			Date fromDate = null;
			Date toDate = null;
			
			try {
				if( options.has("help")) usage(parser);
				if( options.has("fromDate")) sFromDate = (String)options.valueOf("fromDate");
				if( options.has("toDate")) sToDate = (String)options.valueOf("toDate");

				hostname = (String)options.valueOf("hostname");
				entityId = (String)options.valueOf("entityId");
				entityKind = (Integer)options.valueOf("entityKind");
				type = (Integer)options.valueOf("type");
				period = (String)options.valueOf("period");
				outDir = (String)options.valueOf("outDir");
				
				if( options.has("fromHour") && options.has("toHour")) {
					fromHour = (String)options.valueOf("fromHour");
					toHour = (String)options.valueOf("toHour");
				}

				if( options.has("workDays")) {
					workDays = (Boolean)options.valueOf("workDays");
				}
				
				if( sFromDate != null ) {
					fromDate = sdf.parse(sFromDate);
				} else {
					fromDate = sdf.parse("2016-01-01");
				}
				
				if( sToDate != null ) {
					toDate = sdf.parse(sToDate);
				} else {
					toDate = sdf.parse("2030-01-01");
				}
				
			} catch( Exception e ) {
				usage(parser);
				System.exit(-1);
			}
			
			
			log.log(Level.INFO, "Updating External Geo with GPS References for " + hostname + " in period " + period + " and entityId " + entityId);
			importer.importFromGpsRecords(entityId, entityKind, period, hostname, fromDate, toDate, outDir, fromHour, toHour, workDays, type);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
