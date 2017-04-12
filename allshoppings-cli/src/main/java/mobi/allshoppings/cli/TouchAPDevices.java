package mobi.allshoppings.cli;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.ExternalAPHotspotDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.CollectionFactory;


public class TouchAPDevices extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TouchAPDevices.class.getName());

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
			ExternalAPHotspotDAO eaphDao = (ExternalAPHotspotDAO)getApplicationContext().getBean("externalaphotspot.dao.ref");
			APDeviceHelper apdeviceHelper = (APDeviceHelper)getApplicationContext().getBean("apdevice.helper");
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");
			APDAssignationDAO apdaDao = (APDAssignationDAO)getApplicationContext().getBean("apdassignation.dao.ref");
			
			log.log(Level.INFO, "Scanning assignations....");

			// Antenas de Astrid
			List<Store> stores = CollectionFactory.createList();
			List<String> brands  = Arrays.asList("grupochomarc_mx","chomarc_mx","sportium_mx","modatelas_mx","saavedra_mx","delicafe_mx","flormar_pa","roku_mx","blulagoon_mx");
			for( String brand : brands ) {
				List<Store> tmp = storeDao.getUsingBrandAndStatus(brand, StatusHelper.statusActive(), null);
				for( Store store : tmp ) {
					stores.add(store);
				}
			}
			List<APDAssignation> assigs = CollectionFactory.createList();
			for(Store store : stores ) {
				List<APDAssignation> tmp = apdaDao.getUsingEntityIdAndEntityKindAndDate(store.getIdentifier(), EntityKind.KIND_STORE, new Date());
				for(APDAssignation assig : tmp ) {
					assigs.add(assig);
				}
			}
			List<String> astrid = CollectionFactory.createList();
			for(APDAssignation assig : assigs) {
				astrid.add(assig.getHostname());
			}
			
			// Antenas de outlet deportes
			stores = storeDao.getUsingBrandAndStatus("outletdeportes_mx", StatusHelper.statusActive(), null);
			assigs = CollectionFactory.createList();
			for(Store store : stores ) {
				List<APDAssignation> tmp = apdaDao.getUsingEntityIdAndEntityKindAndDate(store.getIdentifier(), EntityKind.KIND_STORE, new Date());
				for(APDAssignation assig : tmp ) {
					assigs.add(assig);
				}
			}
			List<String> outlet = CollectionFactory.createList();
			for(APDAssignation assig : assigs) {
				outlet.add(assig.getHostname());
			}
			
			// Antenas de club casablanca
			stores = storeDao.getUsingBrandAndStatus("clubcasablanca_mx", StatusHelper.statusActive(), null);
			assigs = CollectionFactory.createList();
			for(Store store : stores ) {
				List<APDAssignation> tmp = apdaDao.getUsingEntityIdAndEntityKindAndDate(store.getIdentifier(), EntityKind.KIND_STORE, new Date());
				for(APDAssignation assig : tmp ) {
					assigs.add(assig);
				}
			}
			List<String> casablanca = CollectionFactory.createList();
			for(APDAssignation assig : assigs) {
				casablanca.add(assig.getHostname());
			}
			
			// Antenas de club casablanca
			stores = storeDao.getUsingBrandAndStatus("agasys_mx", StatusHelper.statusActive(), null);
			assigs = CollectionFactory.createList();
			for(Store store : stores ) {
				List<APDAssignation> tmp = apdaDao.getUsingEntityIdAndEntityKindAndDate(store.getIdentifier(), EntityKind.KIND_STORE, new Date());
				for(APDAssignation assig : tmp ) {
					assigs.add(assig);
				}
			}
			List<String> agasys = CollectionFactory.createList();
			for(APDAssignation assig : assigs) {
				agasys.add(assig.getHostname());
			}
			

			log.log(Level.INFO, "Touching apdevices....");
			List<APDevice> list = apdeviceDao.getAll(true);
			for( APDevice obj : list ) {
				log.log(Level.INFO, "Touching " + obj.getIdentifier() + "...");
				obj.completeDefaults();
				if(StringUtils.hasText(obj.getDescription()))
					obj.setDescription(obj.getDescription().replaceAll("_", " "));
				else
					obj.setDescription(null);
				
				if( null == obj.getStatus() ) 
					obj.setStatus(StatusAware.STATUS_ENABLED);
				
				if( null == obj.getReportStatus() )
					obj.setReportStatus(APDevice.REPORT_STATUS_NOT_REPORTED);
				
				if( obj.getReportMailList() != null && obj.getReportMailList().size() > 0  ) {
					List<String> mails = CollectionFactory.createList();
					mails.addAll(Arrays.asList("matias@getin.mx","anabell@getin.mx","francisco@getin.mx","daniel@getin.mx","ingrid@getin.mx"));
					if(astrid.contains(obj.getHostname())) {
						mails.add("astrid@getin.mx");
					} else {
						mails.add("mariajose@getin.mx");
					}
					
					// custom mails
					if(obj.getHostname().equals("ashs-0091") || obj.getHostname().equals("ashs-0112"))
						mails.add("emmabotanicus@hotmail.com");
					
					if(obj.getHostname().equals("ashs-0091") || obj.getHostname().equals("ashs-0112"))
						mails.add("emmabotanicus@hotmail.com");

					if(outlet.contains(obj.getHostname())) {
						mails.add("cymerikayedra@gmail.com");
						mails.add("cymauditoria@live.com.mx");
					} 

					if(casablanca.contains(obj.getHostname())) {
						mails.add("ggonzalez@clubcasablanca.mx");
					} 

					if(agasys.contains(obj.getHostname())) {
						mails.clear();
					} 

					obj.setReportMailList(mails);
				}
				
				apdeviceDao.update(obj);
				apdeviceHelper.updateAssignationsUsingAPDevice(obj.getHostname());
			}
			
			
			// External Antennas
			List<String> externalHostnames = eaphDao.getExternalHostnames();
			for( String hostname : externalHostnames ) {
				APDevice obj = null;
				try {
					obj = apdeviceDao.get(hostname);
				} catch( Exception e ) {
					obj = new APDevice();
					obj.setHostname(hostname);
					obj.setExternal(true);
					obj.setKey(apdeviceDao.createKey(hostname));
					apdeviceDao.create(obj);
				} 

				log.log(Level.INFO, "Touching " + obj.getIdentifier() + "...");

				obj.setVisitGapThreshold(180L);
				if( null == obj.getStatus() ) 
					obj.setStatus(StatusAware.STATUS_ENABLED);

				if( null == obj.getReportStatus() )
					obj.setReportStatus(APDevice.REPORT_STATUS_NOT_REPORTED);
				
				apdeviceDao.update(obj);
				apdeviceHelper.updateAssignationsUsingAPDevice(obj.getHostname());

			}
			
			list = apdeviceDao.getAll();
			List<ModelKey> index = CollectionFactory.createList();
			for( APDevice obj : list ) {
				index.add(obj);
			}
			apdeviceDao.getIndexHelper().indexObject(index);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
