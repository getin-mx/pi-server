package mobi.allshoppings.cli;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.FloorMapDAO;
import mobi.allshoppings.dao.WifiSpotDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.FloorMap;
import mobi.allshoppings.model.WifiSpot;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.tools.CollectionFactory;


public class TouchVolarisMap extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TouchVolarisMap.class.getName());

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
			APDAssignationDAO apdaDao = (APDAssignationDAO)getApplicationContext().getBean("apdassignation.dao.ref");
			FloorMapDAO fmDao = (FloorMapDAO)getApplicationContext().getBean("floormap.dao.ref");
			WifiSpotDAO wsDao = (WifiSpotDAO)getApplicationContext().getBean("wifispot.dao.ref");
			KeyHelper keyHelper = (KeyHelper)getApplicationContext().getBean("key.helper");
			
			log.log(Level.INFO, "Scanning assignations....");

			String AICM = "a9f9d78e-d5f6-42b5-97be-2a84aca5165d";
			String AICMFM = "volarisaicmnacional";

			// Finds the map
			FloorMap fm = null;
			try {
				fm = fmDao.get(AICMFM, true);
				fm.setStatus(StatusAware.STATUS_ENABLED);
				fm.setMapWidth(1356);
				fm.setScreenWidth(1356);
				fm.setMapHeight(816);
				fm.setScreenHeight(816);
				fmDao.update(fm);
			} catch( Exception e ) {
				fm = new FloorMap();
				fm.setFloor("AICM Nacional");
				fm.setImageId("volarisaicmnacional.png");
				fm.setShoppingId(AICM);
				fm.setKey((Key)keyHelper.obtainKey(FloorMap.class, AICMFM));
				fm.setCorrected(false);
				fm.setStatus(StatusAware.STATUS_ENABLED);
				fmDao.create(fm);
			}
			
			// Finds the hostnames
			List<APDAssignation> assigs = apdaDao.getUsingEntityIdAndEntityKindAndDate(AICM, EntityKind.KIND_STORE, new Date());
			List<String> hostnames = CollectionFactory.createList();
			for( APDAssignation assig : assigs ) {
				if(!hostnames.contains(assig.getHostname()))
					hostnames.add(assig.getHostname());
			}
			
			// Finds the wifi spots
			List<WifiSpot> wifiSpots = wsDao.getUsingFloorMapId(AICMFM);
			for(WifiSpot ws : wifiSpots ) {
				if( StringUtils.hasText(ws.getApDevice()) && hostnames.contains(ws.getApDevice()))
					hostnames.remove(ws.getApDevice());
			}
			int x = 10;
			int y = 10;
			for( String hostname : hostnames ) {
				WifiSpot ws = new WifiSpot();
				ws.setApDevice(hostname);
				ws.setCalculusStrategy(0);
				ws.setFloorMapId(AICMFM);
				ws.setMeasures(0);
				ws.setRecordStrategy(0);
				ws.setShoppingId(AICM);
				ws.setX(x);
				ws.setY(y);
				ws.setKey(wsDao.createKey(ws));
				wsDao.create(ws);
			}

			// Finds the wifi spots
			wifiSpots = wsDao.getUsingFloorMapId(AICMFM);
			x = 10;
			y = 10;
			for( int i = wifiSpots.size(); i < 46; i++ ) {
				WifiSpot ws = new WifiSpot();
				ws.setCalculusStrategy(0);
				ws.setFloorMapId(AICMFM);
				ws.setMeasures(0);
				ws.setRecordStrategy(0);
				ws.setShoppingId(AICM);
				ws.setX(x);
				ws.setY(y);
				ws.setKey(wsDao.createKey(ws));
				wsDao.create(ws);
				y+= 10;
			}
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
