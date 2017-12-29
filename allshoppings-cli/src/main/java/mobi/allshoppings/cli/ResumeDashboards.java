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
import mobi.allshoppings.dashboards.DashboardResumeMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
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
public class ResumeDashboards extends AbstractCLI {

	private static final Logger log = Logger.getLogger(ResumeDashboards.class.getName());
	
	private static final String ENTITY_IDS_PARAM = "entityIds";
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts(Constants.FROM_DATE_PARAM, "Date From" ).withRequiredArg().ofType( String.class );
		parser.accepts(Constants.TO_DATE_PARAM, "Date To" ).withRequiredArg().ofType( String.class );
		parser.accepts(ENTITY_IDS_PARAM, "Comma separated entity Ids to process (just for phase 0)").withRequiredArg()
				.ofType( String.class );
		parser.accepts(Constants.DELETE_PREVIOUS_RECORDS_PARAM, "Delete previus dashboards")
				.withRequiredArg().ofType( Boolean.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static void main(String args[]) throws ASException {
		try {
			DashboardResumeMapperService mapper = (DashboardResumeMapperService)
					getApplicationContext().getBean("dashboard.resume.mapper");

			SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String sFromDate = null;
			String sToDate = null;
			Date fromDate = null;
			Date toDate = null;
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
				
				if( options.has(ENTITY_IDS_PARAM)) {
					sEntityIds = (String)options.valueOf(ENTITY_IDS_PARAM);
					String[] parts = sEntityIds.split(",");
					for( String part : parts ) entityIds.add(part.trim());
				}
				
				if(options.has(Constants.DELETE_PREVIOUS_RECORDS_PARAM)) {
					deletePreviousRecords = (Boolean)options.valueOf(Constants.DELETE_PREVIOUS_RECORDS_PARAM);
				}
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Generating dashboards from " + fromDate + " to " + toDate);
			mapper.resumeDashboardDataForDays(entityIds, fromDate, toDate, deletePreviousRecords);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
