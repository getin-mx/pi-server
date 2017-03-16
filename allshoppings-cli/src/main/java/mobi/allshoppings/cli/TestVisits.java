package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.apdevice.APDVisitHelper;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.APHEntryDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.tools.CollectionFactory;


public class TestVisits extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TestVisits.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "aphe", "List of comma separated aphe").withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			APHEntryDAO dao = (APHEntryDAO)getApplicationContext().getBean("aphentry.dao.ref");
			APDeviceDAO apdDao = (APDeviceDAO)getApplicationContext().getBean("apdevice.dao.ref");
			APDAssignationDAO apdaDao = (APDAssignationDAO)getApplicationContext().getBean("apdassignation.dao.ref");
			APDVisitHelper helper = (APDVisitHelper)getApplicationContext().getBean("apdvisit.helper");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);
			
			List<String> aphes = null;
			
			try {
				if( options.has("aphe")) aphes = Arrays.asList(((String)options.valueOf("aphe")).split(","));
				else usage(parser);
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			// Obtains the entries
			List<APHEntry> entries = dao.getUsingIdList(aphes);
			
			// Obtains the APD Cache
			Map<String, APDevice> apdCache = CollectionFactory.createMap();
			for( APHEntry entry : entries ) { 
				APDevice device = apdDao.get(entry.getHostname());
				apdCache.put(entry.getHostname(), device);
			}

			// Obtains the Assignment Cache
			Map<String, APDAssignation> assignmentsCache = CollectionFactory.createMap();
			for( APHEntry entry : entries ) { 
				List<APDAssignation> assigs = apdaDao.getUsingHostnameAndDate(entry.getHostname(), sdf.parse(entry.getDate()));
				assignmentsCache.put(assigs.get(0).getHostname(), assigs.get(0));
			}

			// Executes the operation
			List<APDVisit> visitList = helper.aphEntryToVisits(entries, apdCache, assignmentsCache,
					new ArrayList<String>(), new ArrayList<String>());
			
			for(APDVisit o : visitList) {
				log.log(Level.INFO, o.toString());
			}
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
