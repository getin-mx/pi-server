package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.tools.CollectionFactory;


public class UpdateDashboards extends AbstractCLI {

	private static final Logger log = Logger.getLogger(UpdateDashboards.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "fromDate", "Date From" ).withRequiredArg().ofType( String.class );
		parser.accepts( "toDate", "Date To" ).withRequiredArg().ofType( String.class );
		parser.accepts( "outDir", "Output Directory (for example, /tmp/dump)").withRequiredArg().ofType( String.class );
		parser.accepts( "phases", "Phases to process, separated by comma. 0: APDevices, 1: Wifi HeatMaps, 2: APDevice HeatMaps, 3: FloorMap Tracking, 4: APDVisits, 5: External APDevice Heatmaps").withRequiredArg().ofType( String.class );
		parser.accepts( "entityIds", "Comma separated entity Ids to process (just for phase 0)").withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static void main(String args[]) throws ASException {
		try {
			DashboardAPDeviceMapperService mapper = (DashboardAPDeviceMapperService)getApplicationContext().getBean("dashboard.apdevice.mapper");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String sFromDate = null;
			String sToDate = null;
			Date fromDate = null;
			Date toDate = null;
			String sOutDir = null;
			String sPhases = null;
			List<Integer> phases = CollectionFactory.createList();
			String sEntityIds = null;
			List<String> entityIds = CollectionFactory.createList();
			
			try {
				if( options.has("fromDate")) sFromDate = (String)options.valueOf("fromDate");
				if( options.has("toDate")) sToDate = (String)options.valueOf("toDate");
				
				if( StringUtils.hasText(sFromDate)) {
					fromDate = sdf.parse(sFromDate);
				} else {
					fromDate = sdf.parse(sdf.format(new Date(new Date().getTime() - 86400000 /* 12 hours */)));
				}
				
				if( StringUtils.hasText(sToDate)) {
					toDate = sdf.parse(sToDate);
				} else {
					toDate = new Date(fromDate.getTime() + 86400000);
				}

				if( options.has("outDir")) sOutDir = (String)options.valueOf("outDir");
				else sOutDir = "/usr/local/allshoppings/dump";

				if( options.has("phases")) {
					sPhases = (String)options.valueOf("phases");
					String[] parts = sPhases.split(",");
					for( String part : parts ) {
						phases.add(Integer.valueOf(part));
					}
				}
				
				if( options.has("entityIds")) {
					sEntityIds = (String)options.valueOf("entityIds");
					String[] parts = sEntityIds.split(",");
					for( String part : parts ) {
						entityIds.add(part.trim());
					}
				}
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Generating dashboards from " + fromDate + " to " + toDate);
			mapper.createDashboardDataForDays(sOutDir, fromDate, toDate, entityIds, phases);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
