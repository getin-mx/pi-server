package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.APDeviceMacMatchDAO;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dump.impl.DumperHelperImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDeviceMacMatch;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.DeviceLocationHistory;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CollectionUtils;


public class APDeviceMacSaavedra extends AbstractCLI {

	private static final Logger log = Logger.getLogger(APDeviceMacSaavedra.class.getName());
	public static final long TWENTY_FOUR_HOURS = 86400000;
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "hostname", "APHostname").withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");
			APDeviceMacMatchDAO apdmmDao = (APDeviceMacMatchDAO)getApplicationContext().getBean("apdevicemacmatch.dao.ref");
			DeviceInfoDAO diDao = (DeviceInfoDAO)getApplicationContext().getBean("deviceinfo.dao.ref");
			GeoCodingHelper geocoder = (GeoCodingHelper)getApplicationContext().getBean("geocoding.helper");
			APDAssignationDAO apdaDao = (APDAssignationDAO)getApplicationContext().getBean("apdassignation.dao.ref");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, List<String>> geos = CollectionFactory.createMap();
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
//			OptionSet options = parser.parse(args);

			Map<String, String> hashes = CollectionFactory.createMap();
			List<Store> stores = storeDao.getUsingBrandAndStatus("saavedra_mx", StatusHelper.statusActive(), null);
			for(Store store : stores ) {
				String hash =geocoder.encodeGeohash(store.getAddress().getLatitude(), store.getAddress().getLongitude()).substring(0,5);
				List<APDAssignation> ass = apdaDao.getUsingEntityIdAndEntityKind(store.getIdentifier(), EntityKind.KIND_STORE);
				if( ass.size() > 0 ) {
					hashes.put(hash, ass.get(0).getHostname());
					if( !geos.containsKey(ass.get(0).getHostname())) {
						geos.put(ass.get(0).getHostname(), new ArrayList<String>());
					}
				}
			}

			// Now obtains the physical data information for the dump
			DumperHelperImpl<DeviceLocationHistory> dump = new DumperHelperImpl<DeviceLocationHistory>("/usr/local/allshoppings/dump", DeviceLocationHistory.class);
			Iterator<JSONObject> i = dump.jsonIterator(sdf.parse("2016-01-01"), sdf.parse("2017-01-01"));
			while(i.hasNext()) {
				JSONObject json = i.next();
				if( json.has("geohash")) {
					String gh = json.getString("geohash").substring(0,5);
					if( hashes.containsKey(gh)) {
						List<String> macs = geos.get(hashes.get(gh));
						if( !macs.contains(json.getString("deviceUUID"))) {
							macs.add(json.getString("deviceUUID"));
							geos.put(hashes.get(gh), macs);
						}
					}
				}
			}

			List<APDeviceMacMatch> list = CollectionFactory.createList();
			Iterator<String> x = hashes.keySet().iterator();
			while(x.hasNext()) {
				String key = x.next();
				String hostname = hashes.get(key);
				List<String> macs = geos.get(hostname);
				if( macs != null ) {
					for( String mac : macs ) {
						try {
							List<DeviceInfo> l2 = Arrays.asList(diDao.get(mac, false));
							if(!CollectionUtils.isEmpty(l2)) {
								for( DeviceInfo di : l2 ) {
									APDeviceMacMatch obj = new APDeviceMacMatch();
									obj.setDeviceUUID(di.getDeviceUUID());
									obj.setHostname(hostname);
									obj.setMac(mac);
									obj.setKey(apdmmDao.createKey());
									list.add(obj);
								}
							}
						} catch( Exception e ) {
							// nothing to do 
						}
					}
				}
				
			}
			
			log.log(Level.INFO, "Saving data with " + list.size() + " elements...");
			apdmmDao.createOrUpdate(null, list, true);
			
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
