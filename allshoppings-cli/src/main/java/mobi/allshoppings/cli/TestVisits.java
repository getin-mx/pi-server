package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.apdevice.APDVisitHelper;
import mobi.allshoppings.apdevice.APHHelper;
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
			APHHelper aphHelper = (APHHelper)getApplicationContext().getBean("aphentry.helper");

			Map<String, APDevice> apdCache = CollectionFactory.createMap();
			Map<String, APDAssignation> assignmentsCache = CollectionFactory.createMap();
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			@SuppressWarnings("unused")
			OptionSet options = parser.parse(args);

			APHEntry entry = dao.get("10:3b:59:5b:b1:e7:gihs-0243:2017-01-17");

			APDevice device = apdDao.get(entry.getHostname());
			
			// Test modifying rules
			device.setVisitGapThreshold(180L);
			aphHelper.artificiateRSSI(entry, device);
			
			List<APDAssignation> assigs = apdaDao.getUsingHostnameAndDate(device.getHostname(), sdf.parse(entry.getDate()));
			apdCache.put(device.getHostname(), device);
			assignmentsCache.put(assigs.get(0).getHostname(), assigs.get(0));

			List<APDVisit> visitList = helper.aphEntryToVisits(entry, apdCache, assignmentsCache,
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
