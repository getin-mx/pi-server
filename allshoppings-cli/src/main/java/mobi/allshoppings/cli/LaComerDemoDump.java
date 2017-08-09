package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.inodes.datanucleus.model.Key;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.FloorMapDAO;
import mobi.allshoppings.dao.InnerZoneDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.WifiSpotDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.FloorMap;
import mobi.allshoppings.model.InnerZone;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.WifiSpot;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.tools.CollectionFactory;

public class LaComerDemoDump extends AbstractCLI {
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static final String MAIN_ENTITY = "lacomerdemo";
	
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
			
			ShoppingDAO shoppingDao = (ShoppingDAO)getApplicationContext().getBean("shopping.dao.ref");
			FloorMapDAO floormapDao = (FloorMapDAO)getApplicationContext().getBean("floormap.dao.ref");
			WifiSpotDAO wifispotDao = (WifiSpotDAO)getApplicationContext().getBean("wifispot.dao.ref");
			KeyHelper keyHelper = (KeyHelper)getApplicationContext().getBean("key.helper");
					
			@SuppressWarnings("unused")
			List<String> hostnames = Arrays.asList(
					"droc-802aa810270b",
					"droc-802aa8198591",
					"droc-802aa85058aa",
					"droc-802aa819882f",
					"droc-802aa8198d81",
					"droc-802aa81028b4",
					"droc-802aa810273b",
					"droc-802aa81027f9",
					"droc-802aa8102789",
					"droc-802aa810277c",
					"droc-802aa81984a4",
					"droc-802aa810278b",
					"droc-802aa81026de",
					"droc-802aa8102849",
					"droc-44d9e7204f6f",
					"droc-802aa8102798",
					"droc-802aa8102766",
					"droc-802aa8198596",
					"droc-802aa8198589",
					"droc-802aa81028b7",
					"droc-802aa81026da",
					"droc-802aa810285a"
					);
					
			
			Shopping shopping = shoppingDao.get(MAIN_ENTITY, true);
			List<FloorMap> floormaps = floormapDao.getUsingStatusAndShoppingId(StatusAware.STATUS_ENABLED, shopping.getIdentifier());
			if( floormaps.size() == 0 ) {
				FloorMap floormap = new FloorMap();
				floormap.setCorrected(false);
				floormap.setFloor("Floor");
				floormap.setImageId("b5de2027-48c2-4c66-9a43-ee3e68fba490.jpg");
				floormap.setShoppingId(MAIN_ENTITY);
				floormap.setStatus(StatusAware.STATUS_ENABLED);
				floormap.setMapHeight(740);
				floormap.setMapWidth(995);
				floormap.setKey((Key)keyHelper.createStringUniqueKey(FloorMap.class, "lacomerdemo_pb"));
				floormapDao.create(floormap);
				floormaps = floormapDao.getUsingStatusAndShoppingId(StatusAware.STATUS_ENABLED, shopping.getIdentifier());
			}

			List<APDAssignation> assigs = apdaDao.getUsingEntityIdAndEntityKindAndDate(shopping.getIdentifier(), EntityKind.KIND_SHOPPING, new Date());
			List<APDevice> devices = CollectionFactory.createList();
			for(APDAssignation assig : assigs) {
				APDevice device = apdeviceDao.get(assig.getHostname(), true);
				if(!devices.contains(device))
					devices.add(device);
			}
			
			for(FloorMap floormap : floormaps ) {
				List<WifiSpot> wifiSpots = wifispotDao.getUsingFloorMapId(floormap.getIdentifier());
				Map<String, WifiSpot> wsCache = CollectionFactory.createMap();
				
				for( WifiSpot ws : wifiSpots ) {
					wsCache.put(ws.getApDevice(), ws);
				}
				
				int x = 10;
				int y = 10;
				
				for(APDevice device : devices) {
					if(!wsCache.containsKey(device.getIdentifier())) {
						WifiSpot obj = new WifiSpot();
						obj.setApDevice(device.getHostname());
						obj.setFloorMapId(floormap.getIdentifier());
						obj.setShoppingId(shopping.getIdentifier());
						obj.setX(x);
						obj.setY(y);
						y+= 10;
						obj.setKey(wifispotDao.createKey(obj));
						wifispotDao.create(obj);
						wsCache.put(obj.getApDevice(), obj);
					}
				}
			}
			
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
			
			
			List<InnerZone> list = innerZoneDao.getUsingEntityIdAndRange(MAIN_ENTITY, EntityKind.KIND_SHOPPING, null, null, null, true);
			Map<String, InnerZone> names = CollectionFactory.createMap();
			for( InnerZone zone : list ) 
				names.put(zone.getName(), zone);
			
			InnerZone zone;
			InnerZone subZone;
			Map<String, InnerZone> subNames;
			
			zone = findZone1(names, "Zone A", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP02","AP03"), aps);
			subNames = getSubZones(zone.getIdentifier(), innerZoneDao);
			subZone = findZone2(zone, subNames, "Meat", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP02"), aps);
			subZone = findZone2(zone, subNames, "Grocery", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP03"), aps);
			
			zone = findZone1(names, "Zone B", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP04","AP05","AP06"), aps);
			subNames = getSubZones(zone.getIdentifier(), innerZoneDao);
			subZone = findZone2(zone, subNames, "Infant", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP04"), aps);
			subZone = findZone2(zone, subNames, "Shoes", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP05"), aps);
			subZone = findZone2(zone, subNames, "Electronics", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP06"), aps);

			zone = findZone1(names, "Zone C", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP07","AP08","AP26"), aps);
			subNames = getSubZones(zone.getIdentifier(), innerZoneDao);
			subZone = findZone2(zone, subNames, "Girls' wear", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP07"), aps);
			subZone = findZone2(zone, subNames, "Men's wear", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP08"), aps);
			subZone = findZone2(zone, subNames, "Stationary", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP26"), aps);

			zone = findZone1(names, "Zone D", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP10","AP11","AP27"), aps);
			subNames = getSubZones(zone.getIdentifier(), innerZoneDao);
			subZone = findZone2(zone, subNames, "Fornitures", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP10"), aps);
			subZone = findZone2(zone, subNames, "Domestics", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP11"), aps);
			subZone = findZone2(zone, subNames, "Warehouse", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP27"), aps);

			zone = findZone1(names, "Zone E", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP34","AP35","AP28"), aps);
			subNames = getSubZones(zone.getIdentifier(), innerZoneDao);
			subZone = findZone2(zone, subNames, "Sporting Goods", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP34"), aps);
			subZone = findZone2(zone, subNames, "Toys", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP35"), aps);
			subZone = findZone2(zone, subNames, "Automotive", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP28"), aps);

			zone = findZone1(names, "Zone D", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP31","AP32","AP33","AP41"), aps);
			subNames = getSubZones(zone.getIdentifier(), innerZoneDao);
			subZone = findZone2(zone, subNames, "Pharmacy", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP31"), aps);
			subZone = findZone2(zone, subNames, "Health and beauty", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP32"), aps);
			subZone = findZone2(zone, subNames, "Cosmetics", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP33"), aps);
			subZone = findZone2(zone, subNames, "Pets", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP41"), aps);

			zone = findZone1(names, "Checkout", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP42"), aps);
			subNames = getSubZones(zone.getIdentifier(), innerZoneDao);
			subZone = findZone2(zone, subNames, "Checkout", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP42"), aps);
			
			zone = findZone1(names, "Outer Zone", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP21","AP22","AP23"), aps);
			subNames = getSubZones(zone.getIdentifier(), innerZoneDao);
			subZone = findZone2(zone, subNames, "Customer Service", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP21"), aps);
			subZone = findZone2(zone, subNames, "McDonalds", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP22"), aps);
			subZone = findZone2(zone, subNames, "Entry", innerZoneDao, apdaDao, apdeviceDao, Arrays.asList("AP23"), aps);

			System.out.println(subZone);
			
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
			zone.setEntityId(MAIN_ENTITY);
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
