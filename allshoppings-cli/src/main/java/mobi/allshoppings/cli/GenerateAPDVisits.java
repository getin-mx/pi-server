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
import mobi.allshoppings.apdevice.APDVisitHelper;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.tools.CollectionFactory;


public class GenerateAPDVisits extends AbstractCLI {

	private static final Logger log = Logger.getLogger(GenerateAPDVisits.class.getName());
	public static final long TWENTY_FOUR_HOURS = 86400000;
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "fromDate", "Date From" ).withRequiredArg().ofType( String.class );
		parser.accepts( "toDate", "Date To" ).withRequiredArg().ofType( String.class );
		parser.accepts( "brandIds", "List of comma separated brands").withRequiredArg().ofType( String.class );
		parser.accepts( "storeIds", "List of comma separated stores (superseeds brandIds)").withRequiredArg().ofType( String.class );
		parser.accepts( "shoppingIds", "List of comma separated shoppings (superseeds brandIds and storeIds)").withRequiredArg().ofType( String.class );
		parser.accepts( "onlyEmployees", "Only process employees").withRequiredArg().ofType( Boolean.class );
		parser.accepts( "onlyDashboards", "Only process dashboards with preexisting APDVisit data").withRequiredArg().ofType( Boolean.class );
		parser.accepts( "updateDashboards", "Update dashboards with preexisting APDVisit data").withRequiredArg().ofType( Boolean.class );
		parser.accepts( "deletePreviousRecors", "Delete previus dashboards").withRequiredArg().ofType( Boolean.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			APDVisitHelper helper = (APDVisitHelper)getApplicationContext().getBean("apdvisit.helper");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			boolean onlyEmployees = false;
			boolean onlyDashboards = false;
			boolean updateDashboards = false;
			boolean deletePreviousRecors = false;
			String sFromDate = null;
			String sToDate = null;
			Date fromDate = null;
			Date toDate = null;
			String brandIds = null;
			String storeIds = null;
			String shoppingIds = null;
			List<String> brands = CollectionFactory.createList();
			List<String> stores = CollectionFactory.createList();
			List<String> shoppings = CollectionFactory.createList();
			
			try {
				if( options.has("fromDate")) sFromDate = (String)options.valueOf("fromDate");
				if( options.has("toDate")) sToDate = (String)options.valueOf("toDate");
				
				if( StringUtils.hasText(sFromDate)) {
					fromDate = sdf.parse(sFromDate);
				} else {
					fromDate = sdf.parse(sdf.format(new Date(new Date().getTime() - 86400000 /* 24 hours */)));
				}
				
				if( StringUtils.hasText(sToDate)) {
					toDate = sdf.parse(sToDate);
				} else {
					toDate = new Date(fromDate.getTime() + 86400000 /* 24 hours */);
				}

				if(options.has("brandIds")) {
					brandIds = (String)options.valueOf("brandIds");
					String tmp[] = brandIds.split(",");
					for( String s : tmp ) {
						if(!brands.contains(s.trim()))
							brands.add(s.trim());
					}
				}

				if(options.has("storeIds")) {
					storeIds = (String)options.valueOf("storeIds");
					String tmp[] = storeIds.split(",");
					for( String s : tmp ) {
						if(!stores.contains(s.trim()))
							stores.add(s.trim());
					}
				}

				if(options.has("shoppingIds")) {
					shoppingIds = (String)options.valueOf("shoppingIds");
					String tmp[] = shoppingIds.split(",");
					for( String s : tmp ) {
						if(!shoppings.contains(s.trim()))
							shoppings.add(s.trim());
					}
				}
				
				if(options.has("onlyEmployees")) {
					onlyEmployees = (Boolean)options.valueOf("onlyEmployees");
				}

				if(options.has("onlyDashboards")) {
					onlyDashboards = (Boolean)options.valueOf("onlyDashboards");
				}
				
				if(options.has("updateDashboards")) {
					updateDashboards = (Boolean)options.valueOf("updateDashboards");
				}
				
				if(options.has("deletePreviousRecors")) {
					deletePreviousRecors = (Boolean)options.valueOf("deletePreviousRecors");
				}

			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Generating APDVisits");
			
			if( shoppings.isEmpty() )
				helper.generateAPDVisits(brands, stores, fromDate, toDate, deletePreviousRecors, updateDashboards, onlyEmployees, onlyDashboards);
			else
				helper.generateAPDVisits(shoppings, fromDate, toDate, deletePreviousRecors, updateDashboards, onlyEmployees, onlyDashboards);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
