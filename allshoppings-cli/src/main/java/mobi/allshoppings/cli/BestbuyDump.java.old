	package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.InnerZoneDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.InnerZone;
import mobi.allshoppings.tools.CollectionFactory;


public class BestbuyDump extends AbstractCLI {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final String STORE_ENTITY = "1491370940990";
	private static final String ASSIGNATION_DATE = "2017-02-20";
	
	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	@SuppressWarnings("unused")
	public static void main(String args[]) throws ASException {
		try {
			
			InnerZoneDAO innerZoneDao = (InnerZoneDAO)getApplicationContext().getBean("innerzone.dao.ref");
			APDAssignationDAO apdaDao = (APDAssignationDAO)getApplicationContext().getBean("apdassignation.dao.ref");
			APDeviceDAO apdeviceDao = (APDeviceDAO)getApplicationContext().getBean("apdevice.dao.ref");
			
			Map<String, String> aps = CollectionFactory.createMap();
			aps.put("gihs-0210", "gihs-0210");
			aps.put("gihs-0211", "gihs-0211");
			aps.put("gihs-0212", "gihs-0212");
			aps.put("gihs-0213", "gihs-0213");
			aps.put("gihs-0214", "gihs-0214");
			aps.put("gihs-0215", "gihs-0215");
			aps.put("gihs-0216", "gihs-0216");
			aps.put("gihs-0217", "gihs-0217");
			aps.put("gihs-0218", "gihs-0218");
			aps.put("gihs-0219", "gihs-0219");
			
			
			List<InnerZone> list = innerZoneDao.getUsingEntityIdAndRange(STORE_ENTITY, EntityKind.KIND_STORE, null, null, null, true);
			Map<String, InnerZone> names = CollectionFactory.createMap();
			for( InnerZone zone : list ) 
				names.put(zone.getName(), zone);
			
			InnerZone zone;
			InnerZone subZone;
			Map<String, InnerZone> subNames;
			
			zone = findZone1(names, "Servicio Al Cliente", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("gihs-0212"), aps);
			zone = findZone1(names, "Home & Entertainment", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("gihs-0213"), aps);
			zone = findZone1(names, "Home & Entertainment 2", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("gihs-0210", "gihs-0211"), aps);
			zone = findZone1(names, "Apliances", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("gihs-0214", "gihs-0216"), aps);
			zone = findZone1(names, "Mobile", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("gihs-0218"), aps);
			zone = findZone1(names, "IT", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("gihs-0215", "gihs-0217", "gihs-0219"), aps);

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}

	public static Map<String, InnerZone> getSubZones(String identifier, InnerZoneDAO dao) throws ASException {
		List<InnerZone> subList = dao.getUsingEntityIdAndRange(identifier, EntityKind.KIND_INNER_ZONE, null, null, null, true);
		Map<String, InnerZone> subNames = CollectionFactory.createMap();
		for( InnerZone subZone : subList ) 
			subNames.put(subZone.getName(), subZone);
		return subNames;
	}
	
	public static InnerZone findZone2(InnerZone master, Map<String, InnerZone> names, String name, InnerZoneDAO dao,
			APDAssignationDAO apdaDao, APDeviceDAO apdDao, List<String> l, Map<String, String> aps) throws ASException {
		InnerZone zone;

		if( !names.containsKey(name)) {
			zone = new InnerZone();
			zone.setName(name);
			zone.setEntityId(master.getIdentifier());
			zone.setEntityKind(EntityKind.KIND_INNER_ZONE);
			zone.setKey(dao.createKey());
			dao.create(zone);
			names.put(name, zone);
		} else {
			zone = names.get(name);
		}

		for( String ap : l ) {
			try {
				String hostname = aps.get(ap);
				apdDao.get(hostname);

				List<APDAssignation> assigs = apdaDao.getUsingHostnameAndDate(hostname, new Date());
				boolean found = false;
				for(APDAssignation assig : assigs ) {
					if( assig.getEntityId().equals(zone.getIdentifier()))
						found = true;
				}
				
				if( !found ) {
					APDAssignation assig = new APDAssignation();
					assig.setEntityId(zone.getIdentifier());
					assig.setEntityKind(EntityKind.KIND_INNER_ZONE);
					assig.setHostname(hostname);
					assig.setFromDate(sdf.parse(ASSIGNATION_DATE));
					assig.setKey(apdaDao.createKey(assig));
					apdaDao.create(assig);
				}
				
			} catch( Exception e ) {}
		}
		
		return zone;		

	}

	public static InnerZone findZone1(Map<String, InnerZone> names, String name, InnerZoneDAO dao,
			APDAssignationDAO apdaDao, APDeviceDAO apdDao, List<String> l, Map<String, String> aps) throws ASException {

		InnerZone zone;

		if( !names.containsKey(name)) {
			zone = new InnerZone();
			zone.setName(name);
			zone.setEntityId(STORE_ENTITY);
			zone.setEntityKind(EntityKind.KIND_STORE);
			zone.setKey(dao.createKey());
			dao.create(zone);
			names.put(name, zone);
		} else {
			zone = names.get(name);
		}

		for( String ap : l ) {
			try {
				String hostname = aps.get(ap);
				apdDao.get(hostname);

				List<APDAssignation> assigs = apdaDao.getUsingHostnameAndDate(hostname, new Date());
				boolean found = false;
				for(APDAssignation assig : assigs ) {
					if( assig.getEntityId().equals(zone.getIdentifier()))
						found = true;
				}
				
				if( !found ) {
					APDAssignation assig = new APDAssignation();
					assig.setEntityId(zone.getIdentifier());
					assig.setEntityKind(EntityKind.KIND_INNER_ZONE);
					assig.setHostname(hostname);
					assig.setFromDate(sdf.parse(ASSIGNATION_DATE));
					assig.setKey(apdaDao.createKey(assig));
					apdaDao.create(assig);
				}
				
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}
		
		return zone;
		
	}
}
