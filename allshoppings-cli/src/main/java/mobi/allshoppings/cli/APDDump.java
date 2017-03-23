package mobi.allshoppings.cli;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.tools.StatusHelper;


public class APDDump extends AbstractCLI {

	private static final Logger log = Logger.getLogger(APDDump.class.getName());

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
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");
			APDAssignationDAO apdaDao = (APDAssignationDAO)getApplicationContext().getBean("apdassignation.dao.ref");
			APDeviceDAO apdDao = (APDeviceDAO)getApplicationContext().getBean("apdevice.dao.ref");

			log.log(Level.INFO, "Dumping Getin Data....");

			// Prada
			{
				log.log(Level.INFO, "Dumping Prada Data....");
				List<Store> list = storeDao.getUsingBrandAndStatus("prada_mx", StatusHelper.statusActive(), null);
				for( Store store : list ) {
					log.log(Level.INFO, "Dumping Prada Data for store " + store.getName() + "....");
					List<APDAssignation> apdaList = apdaDao.getUsingEntityIdAndEntityKind(store.getIdentifier(), EntityKind.KIND_STORE);
					for( APDAssignation apda : apdaList ) {
						log.log(Level.INFO, "Dumping Prada Data for ap device " + apda.getHostname() + "....");
						APDevice device = apdDao.get(apda.getHostname(), true);
						device.completeDefaults();
						if( device.getVisitTimeThreshold() != 3 ) {
							log.log(Level.INFO, "Updating 3 minutes rule for ap device " + apda.getHostname() + "....");
							device.setVisitTimeThreshold(3L);
							apdDao.update(device);
						}
					}
				}
			}

			// Prada
			{
				log.log(Level.INFO, "Dumping Volaris Data....");
				List<Store> list = storeDao.getUsingBrandAndStatus("volaris_mx", StatusHelper.statusActive(), null);
				for( Store store : list ) {
					log.log(Level.INFO, "Dumping Volaris Data for store " + store.getName() + "....");
					List<APDAssignation> apdaList = apdaDao.getUsingEntityIdAndEntityKind(store.getIdentifier(), EntityKind.KIND_STORE);
					for( APDAssignation apda : apdaList ) {
						log.log(Level.INFO, "Dumping Volaris Data for ap device " + apda.getHostname() + "....");
						APDevice device = apdDao.get(apda.getHostname(), true);
						device.completeDefaults();

						device.setVisitStartMon("04:00");
						device.setVisitStartTue("04:00");
						device.setVisitStartWed("04:00");
						device.setVisitStartThu("04:00");
						device.setVisitStartFri("04:00");
						device.setVisitStartSat("04:00");
						device.setVisitStartSun("04:00");

						device.setVisitEndMon("22:00");
						device.setVisitEndTue("22:00");
						device.setVisitEndWed("22:00");
						device.setVisitEndThu("22:00");
						device.setVisitEndFri("22:00");
						device.setVisitEndSat("22:00");
						device.setVisitEndSun("22:00");

						device.setMonitorStart("04:00");
						device.setMonitorEnd("22:00");
						
						device.setVisitMaxThreshold(90L);

						log.log(Level.INFO, "Updating rules for ap device " + apda.getHostname() + "....");
						apdDao.update(device);
					}
				}
			}

			// Casablanca
			{
				log.log(Level.INFO, "Dumping Casablanca Data....");
				List<Store> list = storeDao.getUsingBrandAndStatus("clubcasablanca_mx", StatusHelper.statusActive(), null);
				for( Store store : list ) {
					log.log(Level.INFO, "Dumping Casablanca Data for store " + store.getName() + "....");
					List<APDAssignation> apdaList = apdaDao.getUsingEntityIdAndEntityKind(store.getIdentifier(), EntityKind.KIND_STORE);
					for( APDAssignation apda : apdaList ) {
						log.log(Level.INFO, "Dumping Casablanca Data for ap device " + apda.getHostname() + "....");
						APDevice device = apdDao.get(apda.getHostname(), true);
						device.completeDefaults();
						
						device.setPeasantDecay(10L);
						device.setVisitDecay(10L);

						log.log(Level.INFO, "Updating rules for ap device " + apda.getHostname() + "....");
						apdDao.update(device);
					}
				}
			}

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
