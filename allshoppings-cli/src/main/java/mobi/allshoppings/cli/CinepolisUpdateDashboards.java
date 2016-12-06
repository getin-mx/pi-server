package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.cinepolis.services.DashboardMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;


public class CinepolisUpdateDashboards extends AbstractCLI {

	private static final Logger log = Logger.getLogger(CinepolisUpdateDashboards.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "fromDate", "Date From" ).withRequiredArg().ofType( String.class );
		parser.accepts( "toDate", "Date To" ).withRequiredArg().ofType( String.class );
		parser.accepts( "outDir", "Output Directory (for example, /tmp/dump)").withRequiredArg().ofType( String.class );
		parser.accepts( "phase", "Phase: 1 = Dashboards, 2 = Location").withRequiredArg().ofType( Integer.class );
		return parser;
	}

	public static void main(String args[]) throws ASException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			long TWENTY_FOUR_HOURS = 86400000;

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String sFromDate = null;
			String sToDate = null;
			Date fromDate = null;
			Date toDate = null;
			String sOutDir = null;
			int phase = 0;
			
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

				if( options.has("outDir")) sOutDir = (String)options.valueOf("outDir");
				else sOutDir = "/usr/local/allshoppings/dump";

				if( options.has("phase")) phase = (Integer)options.valueOf("phase");
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Generating cinepolis dashboards from " + fromDate + " to " + toDate);
			DashboardMapperService service = new DashboardMapperService();
			while(fromDate.before(toDate) || fromDate.equals(toDate)) {
				if( phase == 0 ) {
					service.createDashboardDataForDay(sOutDir, fromDate);
				} else if( phase == 1 ) {
					service.createTicketPerformanceDashboardForDay(fromDate);
					service.createPromoPerformanceDashboardForDay(fromDate);
				} else {
					service.createCheckinPerformanceDashboardForDay(fromDate);
					service.createHeatmapDashboardForDay(sOutDir, fromDate);
				}
				
				fromDate = new Date(fromDate.getTime() + TWENTY_FOUR_HOURS);
			}
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
