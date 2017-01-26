package mobi.allshoppings.cli;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.datastore.JDOConnection;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.APDeviceMacMatchDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.spi.DAOJDOPersistentManagerFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.APDeviceMacMatch;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CollectionUtils;


public class APDeviceMacMatchDump extends AbstractCLI {

	private static final Logger log = Logger.getLogger(APDeviceMacMatchDump.class.getName());
	public static final long TWENTY_FOUR_HOURS = 86400000;
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "entityId", "Entity ID to Process").withRequiredArg().ofType( String.class );
		parser.accepts( "entityKind", "Entity Kind to Process").withRequiredArg().ofType( Integer.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			APDeviceMacMatchDAO apdmmDao = (APDeviceMacMatchDAO)getApplicationContext().getBean("apdevicemacmatch.dao.ref");
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			List<String> entityIds = CollectionFactory.createList();
			String entityId = null;
			Integer entityKind = EntityKind.KIND_STORE;
			
			try {
				if( options.has("entityId")) {
					entityId = (String)options.valueOf("entityId");
					entityIds.add(entityId);
				}

				if( options.has("entityKind")) 
					entityKind = (Integer)options.valueOf("entityKind");

				if(StringUtils.hasText(entityId) && entityKind.equals(EntityKind.KIND_BRAND)) {
					entityIds.clear();
					List<Store> stores = storeDao.getUsingBrandAndStatus(entityId, StatusHelper.statusActive(), null);
					for(Store store : stores) {
						entityIds.add(store.getIdentifier());
					}
					entityKind = EntityKind.KIND_STORE;
				}
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			
			log.log(Level.INFO, "Getting info from APDVisit...");
			Map<String,Map<Integer,HashSet<String>>> cache = CollectionFactory.createMap();
			
			PersistenceManager pm;
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
			pm.currentTransaction().begin();

			JDOConnection jdoConn = pm.getDataStoreConnection();
			DB db = (DB)jdoConn.getNativeConnection();

			// Phase 1
			// Read all recorded mac addresses

			log.log(Level.INFO, "Getting info from DeviceInfo...");
			
			Map<String,List<String>> devices = CollectionFactory.createMap();
			
			BasicDBObject fields = new BasicDBObject();
			fields.put("deviceUUID", 1);
			fields.put("mac", 1);
			DBCursor c1 = db.getCollection("DeviceInfo").find(new BasicDBObject(),fields);
			c1.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			Iterator<DBObject> i = c1.iterator();

			log.log(Level.INFO, "Processing " + c1.size() + " results...");
			long count = 0;
			// Fetches the visit list
			while(i.hasNext()) {
				DBObject dbo = i.next();
				if( dbo.containsField("deviceUUID") && dbo.containsField("mac")) {
					String identifier = (String)dbo.get("deviceUUID");
					String dmac = (String)dbo.get("mac");

					if(StringUtils.hasText(dmac)) {
						dmac = dmac.toLowerCase();
						if(!dmac.equals("00:00:00:00:00:00") && !dmac.equals("ff:ff:ff:ff:ff:ff")) {
							List<String> devs = devices.get(dmac);
							if( devs == null ) devs = CollectionFactory.createList();
							devs.add(identifier);
							devices.put(dmac, devs);
						}
					}
				}
				
				count++;
				if(count % 5000 == 0 ) {
					log.log(Level.INFO, "Processing APDVisit Record " + count + " of " + c1.size());
				}
				
//				if( count > 10000 ) break;
			}

			log.log(Level.INFO, devices.size() + " obtained records from DeviceInfo...");

			// Phase 2 
			// Read all mac addresses
			
			log.log(Level.INFO, "Processing Query for APDVisit...");
			
			fields = new BasicDBObject();
			fields.put("entityId", 1);
			fields.put("mac", 1);
			fields.put("checkinType", 1);
			c1 = db.getCollection("APDVisit").find(new BasicDBObject(),fields);
			c1.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			i = c1.iterator();

			log.log(Level.INFO, "Processing " + c1.size() + " results...");
			count = 0;
			// Fetches the visit list
			while(i.hasNext()) {
				DBObject dbo = i.next();
				if( dbo.containsField("entityId") && dbo.containsField("mac") && dbo.containsField("checkinType")) {
					String identifier = (String)dbo.get("entityId");
					String dmac = (String)dbo.get("mac");
					Integer checkinType = (Integer)dbo.get("checkinType");
					
					if( entityIds.isEmpty() || entityIds.contains(identifier)) {
						if(dmac != null ) {
							dmac = dmac.toLowerCase();
							if(devices.containsKey(dmac)) {
								Map<Integer, HashSet<String>> cache2 = cache.get(identifier);
								if( cache2 == null ) cache2 = CollectionFactory.createMap();
								HashSet<String> macs = cache2.get(checkinType);
								if( macs == null ) macs = new HashSet<String>();
								macs.add(dmac);
								cache2.put(checkinType, macs);
								cache.put(identifier, cache2);
							}
						}
					}
				}
				
				count++;
				if(count % 5000 == 0 ) {
					log.log(Level.INFO, "Processing APDVisit Record " + count + " of " + c1.size());
				}
				
//				if( count > 10000 ) break;
			}

			jdoConn.close();
			pm.currentTransaction().commit();
			pm.close();

			// phase 3
			// Separate visits from peasants
			
			log.log(Level.INFO, "Processing mac info ...");
			
			Iterator<String> it = cache.keySet().iterator();
			while(it.hasNext()) {
				String identifier = it.next();
				Map<Integer,HashSet<String>> cache2 = cache.get(identifier);
				HashSet<String> peasants = cache2.get(APDVisit.CHECKIN_PEASANT);
				HashSet<String> visits = cache2.get(APDVisit.CHECKIN_VISIT);
				HashSet<String> nPeasants = new HashSet<String>();
				HashSet<String> nVisits = new HashSet<String>();

				if( peasants != null ) {
					log.log(Level.INFO, "Processing " + peasants.size() + " peasants for entityId " + identifier);
					Iterator<String> i2 = peasants.iterator();
					while(i2.hasNext()) {
						String dmac = i2.next();
						if(devices.containsKey(dmac))
							nPeasants.add(dmac);
					}
					cache2.put(APDVisit.CHECKIN_PEASANT, nPeasants);
				}
				
				
				if( peasants != null && visits != null ) {
					log.log(Level.INFO, "Processing " + visits.size() + " visitors for entityId " + identifier);
					Iterator<String> i2 = visits.iterator();
					while(i2.hasNext()) {
						String dmac = i2.next();
						if(devices.containsKey(dmac))
							if(!nPeasants.contains(dmac))
								nVisits.add(dmac);
					}
					cache2.put(APDVisit.CHECKIN_VISIT, nVisits);
				}
			}

			// Phase 4 
			// Write
			it = cache.keySet().iterator();
			while(it.hasNext()) {
				String identifier = it.next();
				Map<Integer,HashSet<String>> cache2 = cache.get(identifier);
				HashSet<String> peasants = cache2.get(APDVisit.CHECKIN_PEASANT);
				HashSet<String> visits = cache2.get(APDVisit.CHECKIN_VISIT);

				if( peasants != null ) {
					log.log(Level.INFO, "Generating Peasants Mac List for entity id " + identifier + " with " + peasants.size() + " elements ...");
					List<APDeviceMacMatch> list = CollectionFactory.createList();
					for( String mac : peasants ) {
						try {
							List<String> l2 = devices.get(mac);
							if(!CollectionUtils.isEmpty(l2)) {
								for( String di : l2 ) {
									APDeviceMacMatch obj = new APDeviceMacMatch();
									obj.setDeviceUUID(di);
									obj.setHostname(null);
									obj.setMac(mac);
									obj.setEntityId(identifier);
									obj.setEntityKind(entityKind);
									obj.setType(APDVisit.CHECKIN_PEASANT);
									obj.setKey(apdmmDao.createKey(obj));
									list.add(obj);
								}
							}
						} catch( Exception e ) {
							// nothing to do 
						}
						
						try {
							log.log(Level.INFO, "Writing " + list.size() + " peasants for " + identifier + "...");
							apdmmDao.createOrUpdate(null, list, true);
						} catch( Exception e ) {
							log.log(Level.SEVERE, e.getMessage(), e);
						}
					}
				}

				if( visits != null ) {
					log.log(Level.INFO, "Generating Visitors Mac List for entity id " + identifier + " with " + visits.size() + " elements ...");
					List<APDeviceMacMatch> list = CollectionFactory.createList();
					for( String mac : visits ) {
						try {
							List<String> l2 = devices.get(mac);
							if(!CollectionUtils.isEmpty(l2)) {
								for( String di : l2 ) {
									APDeviceMacMatch obj = new APDeviceMacMatch();
									obj.setDeviceUUID(di);
									obj.setHostname(null);
									obj.setMac(mac);
									obj.setEntityId(identifier);
									obj.setEntityKind(entityKind);
									obj.setType(APDVisit.CHECKIN_VISIT);
									obj.setKey(apdmmDao.createKey(obj));
									list.add(obj);
								}
							}
						} catch( Exception e ) {
							// nothing to do 
						}
						
						try {
							log.log(Level.INFO, "Writing " + list.size() + " visitors for " + identifier + "...");
							apdmmDao.createOrUpdate(null, list, true);
						} catch( Exception e ) {
							log.log(Level.SEVERE, e.getMessage(), e);
						}
					}
				}
			}			
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
