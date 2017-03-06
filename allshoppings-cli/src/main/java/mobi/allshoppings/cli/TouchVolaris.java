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


public class TouchVolaris extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TouchVolaris.class.getName());

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
			Date fromDate = sdf.parse("2017-02-01"); 
			String AICM = "a9f9d78e-d5f6-42b5-97be-2a84aca5165d";
			List<Store> stores = CollectionFactory.createList();
			stores.addAll(storeDao.getUsingBrandAndStatus("volaris_mx", null, null));
			Store aicm = storeDao.get(AICM);
			stores.remove(aicm);
			
			for( Store store : stores ) {
				List<APDAssignation> assigs = apdaDao.getUsingEntityIdAndEntityKind(store.getIdentifier(), EntityKind.KIND_STORE);
				for( APDAssignation assig : assigs ) {
					List<APDAssignation> assigs2 = apdaDao.getUsingHostnameAndDate(assig.getHostname(), new Date());
					boolean done = false;
					for( APDAssignation assig2 : assigs2 ) {
						if( assig2.getEntityId().equals(AICM))
							done = true;
					}
					
					if(!done) {
						APDAssignation assig2 = new APDAssignation();
						assig2.setEntityId(AICM);
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
			List<APDAssignation> assigs = apdaDao.getUsingEntityIdAndEntityKind(AICM, EntityKind.KIND_STORE);
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
