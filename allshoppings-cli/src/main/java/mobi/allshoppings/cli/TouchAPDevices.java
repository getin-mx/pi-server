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
			
			// Antenas de sally beauty
			stores = storeDao.getUsingBrandAndStatus("sallybeauty_mx", StatusHelper.statusActive(), null);
			assigs = CollectionFactory.createList();
			for(Store store : stores ) {
				List<APDAssignation> tmp = apdaDao.getUsingEntityIdAndEntityKindAndDate(store.getIdentifier(), EntityKind.KIND_STORE, new Date());
				for(APDAssignation assig : tmp ) {
					assigs.add(assig);
				}
			}
			List<String> sally = CollectionFactory.createList();
			for(APDAssignation assig : assigs) {
				sally.add(assig.getHostname());
			}

			// Antenas de Modatelas
			stores = storeDao.getUsingBrandAndStatus("modatelas_mx", StatusHelper.statusActive(), null);
			assigs = CollectionFactory.createList();
			for(Store store : stores ) {
				List<APDAssignation> tmp = apdaDao.getUsingEntityIdAndEntityKindAndDate(store.getIdentifier(), EntityKind.KIND_STORE, new Date());
				for(APDAssignation assig : tmp ) {
					assigs.add(assig);
				}
			}
			List<String> modatelas = CollectionFactory.createList();
			for(APDAssignation assig : assigs) {
				modatelas.add(assig.getHostname());
			}

			// Antenas de Prada
			stores = storeDao.getUsingBrandAndStatus("prada_mx", StatusHelper.statusActive(), null);
			assigs = CollectionFactory.createList();
			for(Store store : stores ) {
				List<APDAssignation> tmp = apdaDao.getUsingEntityIdAndEntityKindAndDate(store.getIdentifier(), EntityKind.KIND_STORE, new Date());
				for(APDAssignation assig : tmp ) {
					assigs.add(assig);
				}
			}
			List<String> prada = CollectionFactory.createList();
			for(APDAssignation assig : assigs) {
				prada.add(assig.getHostname());
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

			// Antenas de flormar
			List<String> flormar = Arrays.asList("ashs-0064","ashs-0095","ashs-0096","ashs-0097","gihs-0343","gihs-0344","gihs-0345","gihs-0346","gihs-0347");
			List<String> retailUnited = Arrays.asList("gihs-0340","gihs-0339","gihs-0341","gihs-0354");

			// Antenas de Squalo
			List<String> squalo1 = Arrays.asList("ashs-0122");
			List<String> squalo2 = Arrays.asList("ashs-0123");
			List<String> squalo3 = Arrays.asList("ashs-0124");
			List<String> squalo4 = Arrays.asList("ashs-0125");
			List<String> squalo5 = Arrays.asList("ashs-0126");
			
			// Antenas de Universo de Fragancias
			List<String> universo = Arrays.asList("gihs-0333");
			
			// Antenas de Agasys
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
					mails.addAll(Arrays.asList("matias@getin.mx","anabell@getin.mx","francisco@getin.mx","luis@getin.mx","ingrid@getin.mx","eduardo@getin.mx"));
					if(astrid.contains(obj.getHostname())) {
						mails.add("astrid@getin.mx");
					}
					
					// custom mails
					if(obj.getHostname().equals("ashs-0091") || obj.getHostname().equals("ashs-0112"))
						mails.add("emmabotanicus@hotmail.com");
					
					if(prada.contains(obj.getHostname())) {
						mails.add("maguirre@prada.mx");
						mails.add("truiz@prada.mx");
					} 

					if(outlet.contains(obj.getHostname())) {
						mails.add("cymerikayedra@gmail.com");
						mails.add("cymauditoria@live.com.mx");
					} 

					if(sally.contains(obj.getHostname())) {
						mails.add("PPerezCantu@sallybeauty.com");
					} 

					if(casablanca.contains(obj.getHostname())) {
						mails.add("gio_292@hotmail.com");
					} 

					if(flormar.contains(obj.getHostname())) {
						mails.add("nezrin.saker@demodazl.com");
						mails.add("sistemasru@retailunitedsa.com");
						mails.add("valerie.paredes@demodazl.com");
						mails.add("yamilly.bonilla@retailunitedsa.com");
					}
					
					if(retailUnited.contains(obj.getHostname())) {
						mails.add("nezrin.saker@demodazl.com");
						mails.add("sistemasru@retailunitedsa.com");
						mails.add("richard.pang@retailunitedsa.com");
					}
					
					if( squalo1.contains(obj.getHostname())) {
						mails.add("guadalajara.patria@squalo.com");
					}

					if( squalo2.contains(obj.getHostname())) {
						mails.add("vallarta.galerias@squalo.com");
					}

					if( squalo3.contains(obj.getHostname())) {
						mails.add("guadalajara.sol@squalo.com");
					}

					if( squalo4.contains(obj.getHostname())) {
						mails.add("playa.uno@squalo.com");
					}

					if( squalo5.contains(obj.getHostname())) {
						mails.add("monterrey.valleoriente@squalo.com");
					}
					
					if(agasys.contains(obj.getHostname())) {
						mails.clear();
					}

					if( universo.contains(obj.getHostname())) {
						mails.add("gloriagilhr@yahoo.com");
					}
					
					if( modatelas.contains(obj.getHostname())) {
						mails.add("yaragon@modatelas.com.mx");
					}
					
					if( obj.getHostname().equals("gihs-0156")) {
						mails.add("atizapan62a@adolfodominguez.mx");
					}

					if( obj.getHostname().equals("gihs-0155")) {
						mails.add("atizapan70a@adolfodominguez.mx");
					}
					 
					if( obj.getHostname().equals("gihs-0176")) {
						mails.add("cabos61a@adolfodominguez.mx");
					}
					
					if( obj.getHostname().equals("gihs-0153")) {
						mails.add("lomasverdes33b@adolfodominguez.mx");
					}

					if( obj.getHostname().equals("gihs-0145")) {
						mails.add("anatole32b@adolfodominguez.mx");
					}
					 
					if( obj.getHostname().equals("gihs-0146")) {
						mails.add("duraznos83a@adolfodominguez.mx");
					}

					if( obj.getHostname().equals("gihs-0154")) {
						mails.add("castelar84a@adolfodominguez.mx");
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
