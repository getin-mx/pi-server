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
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.CollectionFactory;
import mx.getin.Constants;

/**
 * <p>"Paints" data in the dashboards genrating DashboardIndicatorDatas and other indicator (like heatmaps).</p>
 * <p>This CLI uses a special parameter <b>phases</b>, which describes what kind of indicators should be constructed:<ol>
 * <li>APDevices - </li>
 * </ol></p>
 * @author Matias Hapanowicz
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 2.0, december 2017
 * @since Allshoppings
 */
public class UpdateDashboards extends AbstractCLI {

	private static final Logger log = Logger.getLogger(UpdateDashboards.class.getName());
	
	private static final String PHASES_PARAM = "phases";
	private static final String ENTITY_IDS_PARAM = "entityIds";
	private static final String BRAND_IDS_PARAM = "brandIds";
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts(Constants.FROM_DATE_PARAM, "Date From" ).withRequiredArg().ofType( String.class );
		parser.accepts(Constants.TO_DATE_PARAM, "Date To" ).withRequiredArg().ofType( String.class );
		parser.accepts(Constants.OUT_DIR_PARAM, "Output Directory (for example, /tmp/dump)").withRequiredArg()
				.ofType(String.class);
		parser.accepts(PHASES_PARAM, "Phases to process, separated by comma. 0: APDevices, 1: Wifi HeatMaps,"
				+ "2: APDevice HeatMaps, 3: FloorMap Tracking, 4: APDVisits, 5: External APDevice Heatmaps")
						.withRequiredArg().ofType( String.class );
		parser.accepts(ENTITY_IDS_PARAM, "Comma separated entity Ids to process (just for phase 0)").withRequiredArg()
				.ofType( String.class );
		parser.accepts(BRAND_IDS_PARAM, "Comma separated entity IDs to process (just for phase 4)").withRequiredArg()
				.ofType(String.class);
		parser.accepts(Constants.DELETE_PREVIOUS_RECORDS_PARAM, "Delete previus dashboards")
				.withRequiredArg().ofType( Boolean.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static void main(String args[]) throws ASException {
		try {
			DashboardAPDeviceMapperService mapper = (DashboardAPDeviceMapperService)
					getApplicationContext().getBean("dashboard.apdevice.mapper");

			SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);

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
			boolean deletePreviousRecords = false;
			
			try {
				if( options.has(Constants.FROM_DATE_PARAM)) sFromDate = (String)options.valueOf(Constants.FROM_DATE_PARAM);
				if( options.has(Constants.TO_DATE_PARAM)) sToDate = (String)options.valueOf(Constants.TO_DATE_PARAM);
				
				fromDate = StringUtils.hasText(sFromDate) ? sdf.parse(sFromDate) :
					sdf.parse(sdf.format(System.currentTimeMillis() - Constants.TWELVE_HOURS_IN_MILLIS));
				
				toDate = StringUtils.hasText(sToDate) ? sdf.parse(sToDate) :
					new Date(fromDate.getTime() + Constants.DAY_IN_MILLIS);
				
				if( options.has(Constants.OUT_DIR_PARAM)) sOutDir = (String)options.valueOf(Constants.OUT_DIR_PARAM);
				else sOutDir = "/usr/local/allshoppings/dump";

				if( options.has(PHASES_PARAM)) {
					sPhases = (String)options.valueOf(PHASES_PARAM);
					String[] parts = sPhases.split(",");
					for( String part : parts ) phases.add(Integer.valueOf(part));
				}
				
				if( options.has(ENTITY_IDS_PARAM)) {
					sEntityIds = (String)options.valueOf(ENTITY_IDS_PARAM);
					String[] parts = sEntityIds.split(",");
					for( String part : parts ) entityIds.add(part.trim());
				} if(options.has(BRAND_IDS_PARAM)) {
					StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");
					for(Store s : storeDao.getUsingBrandAndStatus(options.valueOf(BRAND_IDS_PARAM).toString(),
							StatusHelper.statusActive(), null)) entityIds.add(s.getIdentifier());
				}
				
				if(options.has(Constants.DELETE_PREVIOUS_RECORDS_PARAM)) {
					deletePreviousRecords = (Boolean)options.valueOf(Constants.DELETE_PREVIOUS_RECORDS_PARAM);
				}
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Generating dashboards from " + fromDate + " to " + toDate);
			mapper.createDashboardDataForDays(sOutDir, fromDate, toDate, entityIds, phases, deletePreviousRecords);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
