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


public class DRocDump extends AbstractCLI {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
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
			
//			ShoppingDAO shoppingDao = (ShoppingDAO)getApplicationContext().getBean("shopping.dao.ref");
//			FloorMapDAO floormapDao = (FloorMapDAO)getApplicationContext().getBean("floormap.dao.ref");
//			APDeviceDAO apdeviceDao = (APDeviceDAO)getApplicationContext().getBean("apdevice.dao.ref");
//			WifiSpotDAO wifispotDao = (WifiSpotDAO)getApplicationContext().getBean("wifispot.dao.ref");
//			APDAssignationDAO apdaDao = (APDAssignationDAO)getApplicationContext().getBean("apdassignation.dao.ref");
//
//			Shopping shopping = shoppingDao.get("mundoe", true);
//			List<FloorMap> floormaps = floormapDao.getUsingStatusAndShoppingId(StatusAware.STATUS_ENABLED, shopping.getIdentifier());
//			List<APDAssignation> assigs = apdaDao.getUsingEntityIdAndEntityKindAndDate(shopping.getIdentifier(), EntityKind.KIND_SHOPPING, new Date());
//			List<APDevice> devices = CollectionFactory.createList();
//			for(APDAssignation assig : assigs) {
//				APDevice device = apdeviceDao.get(assig.getHostname(), true);
//				if(!devices.contains(device))
//					devices.add(device);
//			}
//			
//			for(FloorMap floormap : floormaps ) {
//				List<WifiSpot> wifiSpots = wifispotDao.getUsingFloorMapId(floormap.getIdentifier());
//				Map<String, WifiSpot> wsCache = CollectionFactory.createMap();
//				
//				for( WifiSpot ws : wifiSpots ) {
//					wsCache.put(ws.getApDevice(), ws);
//				}
//				
//				int x = 10;
//				int y = 10;
//				
//				for(APDevice device : devices) {
//					if(!wsCache.containsKey(device.getIdentifier())) {
//						WifiSpot obj = new WifiSpot();
//						obj.setApDevice(device.getHostname());
//						obj.setFloorMapId(floormap.getIdentifier());
//						obj.setShoppingId(shopping.getIdentifier());
//						obj.setX(x);
//						obj.setY(y);
//						y+= 10;
//						obj.setKey(wifispotDao.createKey(obj));
//						wifispotDao.create(obj);
//						wsCache.put(obj.getApDevice(), obj);
//					}
//				}
//			}
			
			Map<String, String> aps = CollectionFactory.createMap();
			aps.put("AP02", "droc-802aa810270b");
			aps.put("AP03", "droc-802aa8198591");
			aps.put("AP04", "droc-802aa85058aa");
			aps.put("AP05", "droc-802aa819882f");
			aps.put("AP06", "droc-802aa8198d81");
			aps.put("AP07", "droc-802aa81028b4");
			aps.put("AP08", "droc-802aa810273b");
			aps.put("AP10", "droc-802aa81027f9");
			aps.put("AP11", "droc-802aa8102789");
			aps.put("AP21", "droc-802aa810277c");
			aps.put("AP22", "droc-802aa81984a4");
			aps.put("AP23", "droc-802aa810278b");
			aps.put("AP26", "droc-802aa81026de");
			aps.put("AP27", "droc-802aa8102849");
			aps.put("AP28", "droc-44d9e7204f6f");
			aps.put("AP31", "droc-802aa8102798");
			aps.put("AP32", "droc-802aa8102766");
			aps.put("AP33", "droc-802aa8198596");
			aps.put("AP34", "droc-802aa8198589");
			aps.put("AP35", "droc-802aa81028b7");
			aps.put("AP41", "droc-802aa81026da");
			aps.put("AP42", "droc-802aa810285a");
			
			
			List<InnerZone> list = innerZoneDao.getUsingEntityIdAndRange("mundoe", EntityKind.KIND_SHOPPING, null, null, null, true);
			Map<String, InnerZone> names = CollectionFactory.createMap();
			for( InnerZone zone : list ) 
				names.put(zone.getName(), zone);
			
			InnerZone zone;
			@SuppressWarnings("unused")
			InnerZone subZone;
			Map<String, InnerZone> subNames;
			
			zone = findZone1(names, "Zona A: Area de Comidas", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP01","AP02","AP03"), aps);
			subNames = getSubZones(zone.getIdentifier(), innerZoneDao);
			subZone = findZone2(zone, subNames, "AP01 ZA Comida (salida - plafon)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP01"), aps);
			subZone = findZone2(zone, subNames, "AP02 ZA Comida (Recorcholis)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP02"), aps);
			subZone = findZone2(zone, subNames, "AP03 ZA Comida (Burger King)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP03"), aps);
			
			zone = findZone1(names, "Zona B: Pista de Hielo", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP04","AP05","AP06"), aps);
			subNames = getSubZones(zone.getIdentifier(), innerZoneDao);
			subZone = findZone2(zone, subNames, "AP04 ZB Pista (Santa Clara PAL RIV)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP04"), aps);
			subZone = findZone2(zone, subNames, "AP05 ZB Pista (Pull & Bear)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP05"), aps);
			subZone = findZone2(zone, subNames, "AP06 ZB Pista (Recorcholis)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP06"), aps);

			zone = findZone1(names, "Zona C: Pasillo Restaurantes", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP07","AP08","AP26"), aps);
			subNames = getSubZones(zone.getIdentifier(), innerZoneDao);
			subZone = findZone2(zone, subNames, "AP07 ZC Pista (Sfera)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP07"), aps);
			subZone = findZone2(zone, subNames, "AP08 ZC Pasillo Rest (Sfera)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP08"), aps);
			subZone = findZone2(zone, subNames, "AP26 ZC Pasillo (Forever 21)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP26"), aps);

			zone = findZone1(names, "Zona D: Atrium H&M", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP10","AP11","AP27","AP28"), aps);
			subNames = getSubZones(zone.getIdentifier(), innerZoneDao);
			subZone = findZone2(zone, subNames, "AP10 ZD Entrada (Dentimex)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP10"), aps);
			subZone = findZone2(zone, subNames, "AP11 ZD Entrada (H&M)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP11"), aps);
			subZone = findZone2(zone, subNames, "AP27 ZD Pasillo (American Eagle)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP27"), aps);
			subZone = findZone2(zone, subNames, "AP28 ZD Entrada (Julio) (AP temporal externa)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP28"), aps);

			zone = findZone1(names, "Zona E: Atrium BestBuy", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP34","AP35","AP36"), aps);
			subNames = getSubZones(zone.getIdentifier(), innerZoneDao);
			subZone = findZone2(zone, subNames, "AP34 ZE Pasillo (Bufalo Wild)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP34"), aps);
			subZone = findZone2(zone, subNames, "AP35 ZE Atrium Bestbuy (Bizzarro)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP35"), aps);
			subZone = findZone2(zone, subNames, "AP36 ZE Atrium Bestbuy (otro lado)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP36"), aps);

			zone = findZone1(names, "Zona F: Atrium Zara", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP31","AP22","AP33"), aps);
			subNames = getSubZones(zone.getIdentifier(), innerZoneDao);
			subZone = findZone2(zone, subNames, "AP31 ZF Atrium Zara (Ozone)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP31"), aps);
			subZone = findZone2(zone, subNames, "AP32 ZF Atrium Zara (arriba Zara)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP32"), aps);
			subZone = findZone2(zone, subNames, "AP33 ZF Pasillo (Cerveceria)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP33"), aps);

			zone = findZone1(names, "Zona G: Atrium Cinemex", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP41","AP42"), aps);
			subNames = getSubZones(zone.getIdentifier(), innerZoneDao);
			subZone = findZone2(zone, subNames, "AP41 ZG Obelisco (Beer Factory)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP41"), aps);
			subZone = findZone2(zone, subNames, "AP42 ZG Obelisco (Moyo)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP42"), aps);

			zone = findZone1(names, "Zona H: Atrium Starbucks", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP21","AP22","AP23"), aps);
			subNames = getSubZones(zone.getIdentifier(), innerZoneDao);
			subZone = findZone2(zone, subNames, "AP21 ZH Pasillo (TAF)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP21"), aps);
			subZone = findZone2(zone, subNames, "AP22 ZH Columna (La Chilanguita)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP22"), aps);
			subZone = findZone2(zone, subNames, "AP23 ZH Columna (Starbucks)", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP23"), aps);

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
					assig.setFromDate(sdf.parse("2016-12-17"));
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
			zone.setEntityId("mundoe");
			zone.setEntityKind(EntityKind.KIND_SHOPPING);
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
					assig.setFromDate(sdf.parse("2016-12-17"));
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
