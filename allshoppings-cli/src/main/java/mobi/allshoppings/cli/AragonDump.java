package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
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


public class AragonDump extends AbstractCLI {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final String SHOPPING_ENTITY = "plazaaragon";
	private static final String SHOPPING_DESCRIPTION = "Plaza Aragon";
	private static final String IMAGE_ENTITY = "63237877-d539-4833-a8d7-3a6e83ebcaf1.png";
	private static final String ASSIGNATION_DATE = "2017-08-01";

	
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
			APDeviceDAO apdDao = (APDeviceDAO)getApplicationContext().getBean("apdevice.dao.ref");
			KeyHelper keyHelper = (KeyHelper)getApplicationContext().getBean("key.helper");
			
			ShoppingDAO shoppingDao = (ShoppingDAO)getApplicationContext().getBean("shopping.dao.ref");
			FloorMapDAO floormapDao = (FloorMapDAO)getApplicationContext().getBean("floormap.dao.ref");
			WifiSpotDAO wifispotDao = (WifiSpotDAO)getApplicationContext().getBean("wifispot.dao.ref");

			// Hardcoded APs
			List<String> externalDevices = Arrays.asList(
					"droc-802aa81027b5", "droc-802aa8c09b09", "droc-802aa8c09b06",
					"droc-802aa8c0998c", "droc-802aa8c09b51", "droc-802aa8c09ba3", "droc-802aa8c09b53",
					"droc-802aa8c09ab2", "droc-f09fc229a3f3", "droc-24a43c6ed297", "droc-24a43c6ed336",
					"droc-802aa8c0ad88", "droc-f09fc2299fd7", "droc-802aa8c09b52", "droc-802aa8c099f7",
					"droc-0418d62a8e05", "droc-0418d62a8dad", "droc-0418d62a8e9b", "droc-24a43c6ed337",
					"droc-f09fc22990f3");			

			
			Map<String, String> aps = CollectionFactory.createMap();
			aps.put("AP1-1", "droc-802aa81027b5");
			aps.put("AP1-3", "droc-802aa8c09b09");
			aps.put("AP1-5", "droc-802aa8c09b06");
			aps.put("AP2-1", "droc-802aa8c0998c");
			aps.put("AP2-2", "droc-802aa8c09b51");
			aps.put("AP2-4", "droc-802aa8c09ba3");
			aps.put("AP3-3", "droc-802aa8c09b53");
			aps.put("AP4-1", "droc-802aa8c09ab2");
			aps.put("AP4-3", "droc-f09fc229a3f3");
			aps.put("AP4-4", "droc-24a43c6ed297");
			aps.put("AP5-1", "droc-24a43c6ed336");
			aps.put("AP5-3", "droc-802aa8c0ad88");
			aps.put("AP5-4", "droc-f09fc2299fd7");
			aps.put("AP6-2", "droc-802aa8c09b52");
			aps.put("AP6-3", "droc-802aa8c099f7");
			aps.put("AP6-5", "droc-0418d62a8e05");
			aps.put("AP7-3", "droc-0418d62a8dad");
			aps.put("AP7-5", "droc-0418d62a8e9b");
			aps.put("AP7A-6", "droc-24a43c6ed337");
			aps.put("AP8-2", "droc-f09fc22990f3");

			// Step 1: Finds a matching Shopping
			Shopping shopping = null;
			try {
				shopping = shoppingDao.get(SHOPPING_ENTITY, true);
			} catch( Exception e ) {
				shopping = new Shopping();
				shopping.setName(SHOPPING_DESCRIPTION);
				shopping.setStatus(StatusAware.STATUS_ENABLED);
				shopping.getAddress().setCountry("Mexico");;
				shopping.setKey((Key)keyHelper.createStringUniqueKey(Shopping.class, SHOPPING_ENTITY));
				shoppingDao.create(shopping);
			}

			// Step 2: Create APDevices and Assignations
			for( String hostname : externalDevices ) {
				try {
					apdDao.get(hostname);
				} catch( Exception e ) {
					APDevice apd = new APDevice();
					apd.setHostname(hostname);
					apd.setDescription(SHOPPING_DESCRIPTION);
					apd.setExternal(true);
					apd.completeDefaults();
					apd.setKey(apdDao.createKey(hostname));
					apdDao.create(apd);
				}

				List<APDAssignation> assigs = apdaDao.getUsingHostnameAndDate(hostname, new Date());
				boolean found = false;
				for(APDAssignation assig : assigs ) {
					if( assig.getEntityId().equals(SHOPPING_ENTITY))
						found = true;
				}

				if( !found ) {
					APDAssignation assig = new APDAssignation();
					assig.setEntityId(SHOPPING_ENTITY);
					assig.setEntityKind(EntityKind.KIND_SHOPPING);
					assig.setHostname(hostname);
					assig.setFromDate(sdf.parse(ASSIGNATION_DATE));
					assig.setKey(apdaDao.createKey(assig));
					apdaDao.create(assig);
				}
			}
			
			// Step 3: Get Floormaps for the entity
			List<FloorMap> floormaps = floormapDao.getUsingStatusAndShoppingId(StatusAware.STATUS_ENABLED, shopping.getIdentifier());
			if( CollectionUtils.isEmpty(floormaps)) {
				
				FloorMap fm = new FloorMap();
				fm.setCorrected(false);
				fm.setFloor("Plaza Aragon");
				fm.setImageId(IMAGE_ENTITY);
				fm.setMapWidth(1559);
				fm.setMapHeight(1213);
				fm.setScreenWidth(1559);
				fm.setScreenHeight(1213);
				fm.setMarginTop(0);
				fm.setStatus(StatusAware.STATUS_ENABLED);
				fm.setShoppingId(SHOPPING_ENTITY);
				fm.setKey((Key)keyHelper.createStringUniqueKey(FloorMap.class, SHOPPING_ENTITY));
				floormapDao.create(fm);

				floormaps = floormapDao.getUsingStatusAndShoppingId(StatusAware.STATUS_ENABLED, shopping.getIdentifier());
				
			}
			
			// Step 4: Create Wifi Spots (points in the map)
			for(FloorMap floormap : floormaps ) {
				List<WifiSpot> wifiSpots = wifispotDao.getUsingFloorMapId(floormap.getIdentifier());
				Map<String, WifiSpot> wsCache = CollectionFactory.createMap();
				
				for( WifiSpot ws : wifiSpots ) {
					wsCache.put(ws.getApDevice(), ws);
				}
				
				int x = 10;
				int y = 10;
				
				for(String device : externalDevices) {
					if(!wsCache.containsKey(device)) {
						WifiSpot obj = new WifiSpot();
						obj.setApDevice(device);
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
			
			// Step 5: Create Inner Zones
			List<InnerZone> list = innerZoneDao.getUsingEntityIdAndRange(SHOPPING_ENTITY, EntityKind.KIND_SHOPPING, null, null, null, true);
			Map<String, InnerZone> names = CollectionFactory.createMap();
			for( InnerZone zone : list ) 
				names.put(zone.getName(), zone);
			
			InnerZone zone;
			InnerZone subZone;
			Map<String, InnerZone> subNames;
			
			zone = findZone1(names, "AP1-1 Walmart", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP1-1"), aps);
			zone = findZone1(names, "AP1-3 Movistar Tienda", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP1-3"), aps);
			zone = findZone1(names, "AP1-5 LizMinelli", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP1-5"), aps);
			zone = findZone1(names, "AP2-1 Cinepolis Entrada", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP2-1"), aps);
			zone = findZone1(names, "AP2-2 Movistar Atencion", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP2-2"), aps);
			zone = findZone1(names, "AP2-4 Turin", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP2-4"), aps);
			zone = findZone1(names, "AP3-3 Cecina", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP3-3"), aps);
			zone = findZone1(names, "AP4-1 La Joya", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP4-1"), aps);
			zone = findZone1(names, "AP4-3 Bancomer", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP4-3"), aps);
			zone = findZone1(names, "AP4-4 Moyo", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP4-4"), aps);
			zone = findZone1(names, "AP5-1 Monte de Piedad", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP5-1"), aps);
			zone = findZone1(names, "AP5-3 New Balance", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP5-3"), aps);
			zone = findZone1(names, "AP5-4 Stylo", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP5-4"), aps);
			zone = findZone1(names, "AP6-2 Kurian", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP6-2"), aps);
			zone = findZone1(names, "AP6-3 Shirushi", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP6-3"), aps);
			zone = findZone1(names, "AP6-5 Bayon", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP6-5"), aps);
			zone = findZone1(names, "AP7-3 Suhui", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP7-3"), aps);
			zone = findZone1(names, "AP7-5 Las Alitas", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP7-5"), aps);
			zone = findZone1(names, "AP7A-6 Salida Sams", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP7A-6"), aps);
			zone = findZone1(names, "AP8-2 India Palace", innerZoneDao, apdaDao, apdDao, Arrays.asList("AP8-2"), aps);

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
			zone.setEntityId(SHOPPING_ENTITY);
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
