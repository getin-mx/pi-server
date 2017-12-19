package mobi.allshoppings.cli;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Store;

public class FakeVisits extends AbstractCLI {
	
	private static final String FROM_DATE_PARAM = "fromDate";
	private static final String TO_DATE_PARAM = "toDate";
	private static final String STORE_IDS_PARAM = "storeIds";
	private static final String DEST_DATE_PARAM = "destDate";
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts(FROM_DATE_PARAM, "Date From" ).withRequiredArg()
				.ofType( String.class );
		parser.accepts(TO_DATE_PARAM, "Date To" ).withRequiredArg()
				.ofType( String.class );
		parser.accepts(DEST_DATE_PARAM, "Destination date to copy to")
				.withRequiredArg().ofType(String.class);
		parser.accepts(STORE_IDS_PARAM, "List of comma separated brands")
				.withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			
			APDVisitHelper vhelper = (APDVisitHelper)getApplicationContext().getBean("apdvisit.helper");
			DashboardAPDeviceMapperService mapper = (DashboardAPDeviceMapperService)getApplicationContext().getBean("dashboard.apdevice.mapper");
			StoreDAO storeDao = (StoreDAO)getApplicationContext()
					.getBean("store.dao.ref");
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			TimeZone tz = TimeZone.getTimeZone("GMT");
			sdf.setTimeZone(tz);
			
			Date fromDate;
			Date toDate;
			Date destDate;
			
			try {
				fromDate = sdf.parse(options.valueOf(FROM_DATE_PARAM).toString());
				toDate = sdf.parse(options.valueOf(TO_DATE_PARAM).toString());
				destDate = sdf.parse(options.valueOf(DEST_DATE_PARAM).toString());
			} catch(ParseException e) {
				Logger.getLogger(FakeVisits.class.getSimpleName()).log(Level.INFO,
						"Limit dates are required", e);
				usage(parser);
				return;
			}
			Object rawStores = options.valueOf(STORE_IDS_PARAM);
			List<Store> stores = rawStores != null &&
					StringUtils.hasText(rawStores.toString()) ?
							storeDao.getUsingIdList(
									Arrays.asList(rawStores.toString().split(","))) :
										storeDao.getAll();
			
			//List<APDVisit> aux;
			
			try {
				mapper.buildCaches(false);
			} catch( Exception e ) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			}
			
			for(Store store : stores) {
				vhelper.fakeVisitsWith(store, fromDate, toDate, destDate);
				/*if(aux != null && aux.size() > 0)
					mapper.createAPDVisitPerformanceDashboardForDay(destDate,
							Arrays.asList(new String[] {store.getIdentifier()}),
						EntityKind.KIND_STORE, aux);*/
			}

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
}
	
}
