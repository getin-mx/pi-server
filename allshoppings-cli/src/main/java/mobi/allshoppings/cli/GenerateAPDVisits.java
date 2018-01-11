package mobi.allshoppings.cli;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.apdevice.APDVisitHelper;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.tools.CollectionFactory;
import mx.getin.Constants;

public class GenerateAPDVisits extends AbstractCLI {

	private static final Logger log = Logger.getLogger(GenerateAPDVisits.class.getName());
	private static final String BRAND_IDS_PARAM = "brandIds";
	private static final String STORE_IDS_PARAM = "storeIds";
	private static final String ONLY_EMPLOYEES_PARAM = "onlyEmployees";
	private static final String ONLY_DASHBOARDS_PARAM = "onlyDashboards";
	private static final String UPDATE_DASHBOARDS_PARAM = "updateDashboards";
	private static final String DAILY_PROCESS_PARAM = "isDailyProcess";
	private static final String START_HOUR_PARAM = "startHour";
	private static final String END_HOUR_PARAM = "endHour";
	private static final String IGNORED_BRANDS_PARAM = "ignoredBrands";
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts(Constants.FROM_DATE_PARAM, "Date From" ).withRequiredArg().ofType( String.class );
		parser.accepts(Constants.TO_DATE_PARAM, "Date To" ).withRequiredArg().ofType( String.class );
		parser.accepts(BRAND_IDS_PARAM, "List of comma separated brands").withRequiredArg().ofType( String.class );
		parser.accepts(STORE_IDS_PARAM, "List of comma separated stores (superseeds brandIds)").withRequiredArg()
				.ofType( String.class );
		parser.accepts(ONLY_EMPLOYEES_PARAM, "Only process employees").withRequiredArg().ofType( Boolean.class );
		parser.accepts(ONLY_DASHBOARDS_PARAM, "Only process dashboards with preexisting APDVisit data")
				.withRequiredArg().ofType( Boolean.class );
		parser.accepts(UPDATE_DASHBOARDS_PARAM, "Update dashboards with preexisting APDVisit data").withRequiredArg()
				.ofType( Boolean.class );
		parser.accepts(Constants.DELETE_PREVIOUS_RECORDS_PARAM, "Delete previus dashboards")
				.withRequiredArg().ofType( Boolean.class );
		parser.accepts(DAILY_PROCESS_PARAM, "Whether the process is for a never before processed date (true - "
				+ "common daily process) or is a reprocess (false). Default is false (reprocess)")
				.withRequiredArg().ofType(Boolean.class);
		parser.accepts(START_HOUR_PARAM, "The initial hour to build visits in the format HH:MM. Before this "
				+ "time, visits will be downloaded from a previous process.").withRequiredArg()
				.ofType(String.class);
		parser.accepts(END_HOUR_PARAM, "The final hour to build visits in the format HH:MM. This is the limit "
				+ "time to generate visits for every given day").withOptionalArg().ofType(String.class);
		parser.accepts(IGNORED_BRANDS_PARAM, "Comma separated list of brands which stores wont be processed")
				.withRequiredArg().ofType(String.class);
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
			sdf.setTimeZone(TimeZone.getTimeZone(Constants.GMT_TIMEZONE_ID));
			APDVisitHelper helper = (APDVisitHelper)getApplicationContext().getBean("apdvisit.helper");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			boolean onlyEmployees = false;
			boolean onlyDashboards = false;
			boolean updateDashboards = false;
			boolean deletePreviousRecors = false;
			boolean isDailyProcess = false;
			String sFromDate = null;
			String sToDate = null;
			Date fromDate = null;
			Date toDate = null;
			String brandIds = null;
			String storeIds = null;
			String shoppingIds = null;
			byte startHour = -1;
			byte endHour = -1;
			List<String> brands = CollectionFactory.createList();
			List<String> stores = CollectionFactory.createList();
			List<String> ignoredBrands = CollectionFactory.createList();
			
			try {
				if( options.has(Constants.FROM_DATE_PARAM)) sFromDate =
						(String)options.valueOf(Constants.FROM_DATE_PARAM);
				if( options.has(Constants.TO_DATE_PARAM)) sToDate =
						(String)options.valueOf(Constants.TO_DATE_PARAM);
				
				fromDate = StringUtils.hasText(sFromDate) ? sdf.parse(sFromDate) :
					sdf.parse(sdf.format(System.currentTimeMillis() - Constants.DAY_IN_MILLIS));
				
				if(options.has(START_HOUR_PARAM)) startHour = Byte.valueOf(
						options.valueOf(START_HOUR_PARAM).toString().substring(0, 2));
				if(options.has(START_HOUR_PARAM)) endHour = Byte.valueOf(
						options.valueOf(END_HOUR_PARAM).toString().substring(0, 2));
				if(startHour >= 0 && endHour < 0) endHour = (byte)(startHour +1);
				if(endHour > 0 && startHour < 0) startHour = (byte)(endHour -1);
				if(endHour == 0 && startHour < 0) throw ASExceptionHelper.invalidArgumentsException();
				
				toDate = StringUtils.hasText(sToDate) ? sdf.parse(sToDate) :
					new Date(fromDate.getTime() + Constants.DAY_IN_MILLIS);

				if(options.has(BRAND_IDS_PARAM)) {
					brandIds = (String)options.valueOf(BRAND_IDS_PARAM);
					String tmp[] = brandIds.split(",");
					for( String s : tmp ) {
						if(!brands.contains(s.trim()))
							brands.add(s.trim());
					}
				}

				if(options.has(STORE_IDS_PARAM)) {
					storeIds = (String)options.valueOf(STORE_IDS_PARAM);
					String tmp[] = storeIds.split(",");
					for( String s : tmp ) {
						if(!stores.contains(s.trim()))
							stores.add(s.trim());
					}
				}

				if(options.has(IGNORED_BRANDS_PARAM)) {
					shoppingIds = (String)options.valueOf(IGNORED_BRANDS_PARAM);
					String tmp[] = shoppingIds.split(",");
					for( String s : tmp ) {
						if(!ignoredBrands.contains(s.trim()))
							ignoredBrands.add(s.trim());
					}
				}
				
				if(options.has(ONLY_EMPLOYEES_PARAM)) {
					onlyEmployees = (Boolean)options.valueOf(ONLY_EMPLOYEES_PARAM);
				}

				if(options.has(ONLY_DASHBOARDS_PARAM)) {
					onlyDashboards = (Boolean)options.valueOf(ONLY_DASHBOARDS_PARAM);
				}
				
				if(options.has(UPDATE_DASHBOARDS_PARAM)) {
					updateDashboards = (Boolean)options.valueOf(UPDATE_DASHBOARDS_PARAM);
				}
				
				if(options.has(Constants.DELETE_PREVIOUS_RECORDS_PARAM)) {
					deletePreviousRecors = (Boolean)options.valueOf(Constants.DELETE_PREVIOUS_RECORDS_PARAM);
				}
				
				isDailyProcess = (options.has(DAILY_PROCESS_PARAM) && (Boolean)options.valueOf(DAILY_PROCESS_PARAM))
						|| (sFromDate == null && sToDate == null);

			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Generating APDVisits");
			log.log(Level.INFO, "This process PID is: " +ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
			
			helper.generateAPDVisits(brands, stores, ignoredBrands, fromDate, toDate, deletePreviousRecors,
					updateDashboards, onlyEmployees, onlyDashboards, isDailyProcess, startHour, endHour);
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
