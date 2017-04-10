package mobi.allshoppings.cli;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.DashboardConfigurationDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardConfiguration;
import mobi.allshoppings.model.EntityKind;


public class DashboardConfigurationDump extends AbstractCLI {

	private static final Logger log = Logger.getLogger(DashboardConfigurationDump.class.getName());

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws ASException {
		try {
			DashboardConfigurationDAO dcDao = (DashboardConfigurationDAO)getApplicationContext().getBean("dashboardconfiguration.dao.ref");

			log.log(Level.INFO, "Dumping Dashboard Configuration....");

			DashboardConfiguration dc = null;
			boolean forUpdate = true;

			// Flormar
			try {
				dc = dcDao.getUsingEntityIdAndEntityKind("flormar_pa", EntityKind.KIND_BRAND, true);
				forUpdate = true;
			} catch( Exception e ) {
				dc = new DashboardConfiguration("flormar_pa",EntityKind.KIND_BRAND);
				dc.setKey(dcDao.createKey(dc));
				forUpdate = false;
			}
			dc.setTimezone("SERVER");
			if( forUpdate ) 
				dcDao.update(dc);
			else
				dcDao.create(dc);

			try {
				dc = dcDao.getUsingEntityIdAndEntityKind("flormar_cr", EntityKind.KIND_BRAND, true);
				forUpdate = true;
			} catch( Exception e ) {
				dc = new DashboardConfiguration("flormar_cr",EntityKind.KIND_BRAND);
				dc.setKey(dcDao.createKey(dc));
				forUpdate = false;
			}
			dc.setTimezone("-04:00");
			if( forUpdate ) 
				dcDao.update(dc);
			else
				dcDao.create(dc);

			// Sportium
			try {
				dc = dcDao.getUsingEntityIdAndEntityKind("sportium_mx", EntityKind.KIND_BRAND, true);
				forUpdate = true;
			} catch( Exception e ) {
				dc = new DashboardConfiguration("sportium_mx",EntityKind.KIND_BRAND);
				dc.setKey(dcDao.createKey(dc));
				forUpdate = false;
			}
			dc.setStoreLabel("Club");
			if( forUpdate ) 
				dcDao.update(dc);
			else
				dcDao.create(dc);

			// Club Casablanca
			try {
				dc = dcDao.getUsingEntityIdAndEntityKind("clubcasablanca_mx", EntityKind.KIND_BRAND, true);
				forUpdate = true;
			} catch( Exception e ) {
				dc = new DashboardConfiguration("clubcasablanca_mx",EntityKind.KIND_BRAND);
				dc.setKey(dcDao.createKey(dc));
				forUpdate = false;
			}
			dc.setStoreLabel("Salon");
			if( forUpdate ) 
				dcDao.update(dc);
			else
				dcDao.create(dc);

			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
