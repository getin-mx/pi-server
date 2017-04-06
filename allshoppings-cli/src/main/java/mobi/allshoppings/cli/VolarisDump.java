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


public class VolarisDump extends AbstractCLI {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
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
			
			InnerZoneDAO innerZoneDao = (InnerZoneDAO)getApplicationContext().getBean("innerzone.dao.ref");
			APDAssignationDAO apdaDao = (APDAssignationDAO)getApplicationContext().getBean("apdassignation.dao.ref");
			APDeviceDAO apdeviceDao = (APDeviceDAO)getApplicationContext().getBean("apdevice.dao.ref");
			
			Map<String, String> aps = CollectionFactory.createMap();
			aps.put("gihs-0180", "gihs-0180");
			aps.put("gihs-0201", "gihs-0201");
			aps.put("gihs-0276", "gihs-0276");
			aps.put("gihs-0277", "gihs-0277");
			aps.put("gihs-0278", "gihs-0278");
			aps.put("gihs-0279", "gihs-0279");
			aps.put("gihs-0280", "gihs-0280");
			aps.put("gihs-0281", "gihs-0281");
			aps.put("gihs-0282", "gihs-0282");
			aps.put("gihs-0283", "gihs-0283");
			aps.put("gihs-0284", "gihs-0284");
			
			
			List<InnerZone> list = innerZoneDao.getUsingEntityIdAndRange("a9f9d78e-d5f6-42b5-97be-2a84aca5165d", EntityKind.KIND_STORE, null, null, null, true);
			Map<String, InnerZone> names = CollectionFactory.createMap();
			for( InnerZone zone : list ) 
				names.put(zone.getName(), zone);
			
			InnerZone zone;
			InnerZone subZone;
			Map<String, InnerZone> subNames;
			
			zone = findZone1(names, "Zona Maquinas", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("gihs-0281","gihs-0282","gihs-0278", "gihs-0277", "gihs-0276", "gihs-0280"), aps);
			zone = findZone1(names, "Zona Filas", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("gihs-0279"), aps);
			zone = findZone1(names, "Zona Servicios Especiales", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("gihs-0180"), aps);
			zone = findZone1(names, "Zona Checkin", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("gihs-0201", "gihs-0284", "gihs-0282"), aps);

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
					assig.setFromDate(sdf.parse("2017-01-02"));
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
			zone.setEntityId("a9f9d78e-d5f6-42b5-97be-2a84aca5165d");
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
					assig.setFromDate(sdf.parse("2017-01-02"));
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
