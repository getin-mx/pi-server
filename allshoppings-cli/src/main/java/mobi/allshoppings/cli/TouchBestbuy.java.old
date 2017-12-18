package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;


public class TouchBestbuy extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TouchBestbuy.class.getName());

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
			APDeviceDAO apdeviceDao = (APDeviceDAO)getApplicationContext().getBean("apdevice.dao.ref");
			APDeviceHelper apdeviceHelper = (APDeviceHelper)getApplicationContext().getBean("apdevice.helper");
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");
			APDAssignationDAO apdaDao = (APDAssignationDAO)getApplicationContext().getBean("apdassignation.dao.ref");
			
			log.log(Level.INFO, "Scanning assignations....");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date fromDate = sdf.parse("2017-02-20"); 
			String STAFE = "1491370940990";
			List<Store> stores = CollectionFactory.createList();
			stores.addAll(storeDao.getUsingBrandAndStatus("bestbuy_mx", null, null));
			Store stafe = storeDao.get(STAFE);
			stores.remove(stafe);
			
			for( Store store : stores ) {
				List<APDAssignation> assigs = apdaDao.getUsingEntityIdAndEntityKind(store.getIdentifier(), EntityKind.KIND_STORE);
				for( APDAssignation assig : assigs ) {
					List<APDAssignation> assigs2 = apdaDao.getUsingHostnameAndDate(assig.getHostname(), new Date());
					boolean done = false;
					for( APDAssignation assig2 : assigs2 ) {
						if( assig2.getEntityId().equals(STAFE))
							done = true;
					}
					
					APDevice dev = apdeviceDao.get(assig.getHostname(), true);
						dev.setVisitPowerThreshold(-80L);
						dev.setPeasantPowerThreshold(-90L);
						dev.setVisitTimeThreshold(3L);
						dev.setVisitMaxThreshold(180L);
						dev.setVisitGapThreshold(30L);
						dev.setVisitDecay(10L);
						dev.setVisitStartMon("11:00");
						dev.setVisitStartTue("11:00");
						dev.setVisitStartWed("11:00");
						dev.setVisitStartThu("11:00");
						dev.setVisitStartFri("11:00");
						dev.setVisitStartSat("11:00");
						dev.setVisitStartSun("11:00");

						dev.setVisitEndMon("21:00");
						dev.setVisitEndTue("21:00");
						dev.setVisitEndWed("21:00");
						dev.setVisitEndThu("21:00");
						dev.setVisitEndFri("21:00");
						dev.setVisitEndSat("21:00");
						dev.setVisitEndSun("21:00");
						
						dev.setMonitorStart("10:00");
						dev.setMonitorEnd("22:00");

						apdeviceDao.update(dev);
					
					if(!done) {
						APDAssignation assig2 = new APDAssignation();
						assig2.setEntityId(STAFE);
						assig2.setEntityKind(EntityKind.KIND_STORE);
						assig2.setHostname(assig.getHostname());
						assig2.setFromDate(fromDate);
						assig2.setKey(apdaDao.createKey(assig2));
						apdaDao.create(assig2);
					}
					
				}
				
				store.setStatus(StatusAware.STATUS_DISABLED);
				storeDao.update(store);
				
			}
				
			List<APDevice> list = CollectionFactory.createList();
			List<APDAssignation> assigs = apdaDao.getUsingEntityIdAndEntityKind(STAFE, EntityKind.KIND_STORE);
			for( APDAssignation assig : assigs ) {
				list.add(apdeviceDao.get(assig.getHostname(), true));
			}
			List<ModelKey> index = CollectionFactory.createList();
			for( APDevice obj : list ) {
				obj.setPeasantPowerThreshold(-80L);
				obj.setVisitPowerThreshold(-45L);
				obj.setVisitTimeThreshold(3L);
				apdeviceDao.update(obj);
				apdeviceHelper.updateAssignationsUsingAPDevice(obj.getHostname());
				index.add(obj);
			}
			apdeviceDao.getIndexHelper().indexObject(index);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
