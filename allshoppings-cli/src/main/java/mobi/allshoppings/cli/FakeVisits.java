package mobi.allshoppings.cli;

import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.apdevice.APDVisitHelper;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.EntityKind;


public class FakeVisits extends AbstractCLI {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(FakeVisits.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			
			APDVisitHelper vhelper = (APDVisitHelper)getApplicationContext().getBean("apdvisit.helper");
			DashboardAPDeviceMapperService mapper = (DashboardAPDeviceMapperService)getApplicationContext().getBean("dashboard.apdevice.mapper");
			DashboardIndicatorDataDAO didDao = (DashboardIndicatorDataDAO)getApplicationContext().getBean("dashboardindicatordata.dao.ref");
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			@SuppressWarnings("unused")
			OptionSet options = parser.parse(args);

			Date fromDate = new Date(1484499600000L);
			Date toDate = new Date(1484532000000L);
			
			vhelper.fakeVisitsWith("6b8056c1-9249-40a1-8c7a-780433e75fb5", "1471039822656", fromDate, toDate); // Toreo -> Chilim satelite			
			vhelper.fakeVisitsWith("6ca437b4-6e74-4ca2-a3fa-9c6da8480df5", "1471039822677", fromDate, toDate); // Santa Fe -> Chilim Santa fe
			vhelper.fakeVisitsWith("6f7df967-01db-46b7-a43f-defc15a08dc4", "a2a555fe-05db-448c-a008-873ac5aad1ab", fromDate, toDate); // coacalco -> sally metepec			
			vhelper.fakeVisitsWith("ef825453-5e5d-47bb-aab1-da06af02f282", "93f61d1e-956e-42ca-8003-8811c0b4b9dd", fromDate, toDate); // Tlane -> sally mega coyoacan

			try {
				mapper.buildCaches(false);
			} catch( Exception e ) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			}

			didDao.deleteUsingSubentityIdAndElementIdAndDate("6b8056c1-9249-40a1-8c7a-780433e75fb5",
					Arrays.asList(new String[] { "apd_visitor", "apd_permanence" }), fromDate, fromDate);
			didDao.deleteUsingSubentityIdAndElementIdAndDate("6ca437b4-6e74-4ca2-a3fa-9c6da8480df5",
					Arrays.asList(new String[] { "apd_visitor", "apd_permanence" }), fromDate, fromDate);
			didDao.deleteUsingSubentityIdAndElementIdAndDate("6f7df967-01db-46b7-a43f-defc15a08dc4",
					Arrays.asList(new String[] { "apd_visitor", "apd_permanence" }), fromDate, fromDate);
			didDao.deleteUsingSubentityIdAndElementIdAndDate("ef825453-5e5d-47bb-aab1-da06af02f282",
					Arrays.asList(new String[] { "apd_visitor", "apd_permanence" }), fromDate, fromDate);

			mapper.createAPDVisitPerformanceDashboardForDay(fromDate,
					Arrays.asList(new String[] { "6b8056c1-9249-40a1-8c7a-780433e75fb5" }), EntityKind.KIND_STORE, null);
			mapper.createAPDVisitPerformanceDashboardForDay(fromDate,
					Arrays.asList(new String[] { "6ca437b4-6e74-4ca2-a3fa-9c6da8480df5" }), EntityKind.KIND_STORE, null);
			mapper.createAPDVisitPerformanceDashboardForDay(fromDate,
					Arrays.asList(new String[] { "6f7df967-01db-46b7-a43f-defc15a08dc4" }), EntityKind.KIND_STORE, null);
			mapper.createAPDVisitPerformanceDashboardForDay(fromDate,
					Arrays.asList(new String[] { "ef825453-5e5d-47bb-aab1-da06af02f282" }), EntityKind.KIND_STORE, null);

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
