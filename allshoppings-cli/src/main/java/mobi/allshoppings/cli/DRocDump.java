	package mobi.allshoppings.cli;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.FloorMapDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.WifiSpotDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.FloorMap;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.WifiSpot;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;


public class DRocDump extends AbstractCLI {

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
			ShoppingDAO shoppingDao = (ShoppingDAO)getApplicationContext().getBean("shopping.dao.ref");
			FloorMapDAO floormapDao = (FloorMapDAO)getApplicationContext().getBean("floormap.dao.ref");
			APDeviceDAO apdeviceDao = (APDeviceDAO)getApplicationContext().getBean("apdevice.dao.ref");
			WifiSpotDAO wifispotDao = (WifiSpotDAO)getApplicationContext().getBean("wifispot.dao.ref");
			APDAssignationDAO apdaDao = (APDAssignationDAO)getApplicationContext().getBean("apdassignation.dao.ref");

			Shopping shopping = shoppingDao.get("mundoe", true);
			List<FloorMap> floormaps = floormapDao.getUsingStatusAndShoppingId(StatusAware.STATUS_ENABLED, shopping.getIdentifier());
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
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
