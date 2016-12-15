package mobi.allshoppings.cli;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.cinepolis.services.DashboardExporterService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;


public class CinepolisExportDashboards extends AbstractCLI {

	private static final Logger log = Logger.getLogger(CinepolisExportDashboards.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "fromDate", "Date From" ).withRequiredArg().ofType( String.class );
		parser.accepts( "toDate", "Date To" ).withRequiredArg().ofType( String.class );
		parser.accepts( "elementId", "Element ID (for example, ticket_performance)").withRequiredArg().ofType( String.class );
		parser.accepts( "subentityId", "Subentity ID (for example, cinepolis_mx_339").withRequiredArg().ofType( String.class );
		parser.accepts( "file", "Output file").withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void main(String args[]) throws ASException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String sFromDate = null;
			String sToDate = null;
			Date fromDate = null;
			Date toDate = null;
			String elementId = null;
			String sFile = null;
			File file = null;
			String subentityId = null;
			
			try {
				if( options.has("fromDate")) sFromDate = (String)options.valueOf("fromDate");
				if( options.has("toDate")) sToDate = (String)options.valueOf("toDate");
				
				if( StringUtils.hasText(sFromDate)) {
					fromDate = sdf.parse(sFromDate);
				} else {
					fromDate = sdf.parse(sdf.format(new Date(new Date().getTime() - 43200000 /* 12 hours */)));
				}
				
				if( StringUtils.hasText(sToDate)) {
					toDate = sdf.parse(sToDate);
				} else {
					toDate = new Date(fromDate.getTime());
				}
				
				if( options.has("subentityId")) {
					subentityId = (String)options.valueOf("subentityId");
				}
				
				// Required parameters
				elementId = (String)options.valueOf("elementId");
				sFile = (String)options.valueOf("file");
				file = new File(sFile);
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Exporting cinepolis dashboards from " + fromDate + " to " + toDate);
			DashboardExporterService service = new DashboardExporterService();
			service.doExport(elementId, subentityId, fromDate, toDate, file);
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
