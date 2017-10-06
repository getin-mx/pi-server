package mobi.allshoppings.exporter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.datastore.JDOConnection;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import mobi.allshoppings.dao.ExternalGeoDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.spi.DAOJDOPersistentManagerFactory;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.model.DeviceLocationHistory;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.ExternalGeo;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.PersistentCacheFSImpl;
import mobi.allshoppings.tx.PersistenceProvider;

public class ExternalGeoImporter {

	private static final Logger log = Logger.getLogger(ExternalGeoImporter.class.getName());
	
	private static final int DAY_IN_MILLIS = 86400000;

	@Autowired
	private ExternalGeoDAO dao;

	@Autowired
	private GeoCodingHelper geocoder;
	
	@Autowired
	private StoreDAO storeDao;
	
	@Autowired
	private ExternalGeoDAO externalGeoDao;
	
	@Autowired
	private SystemConfiguration systemConfiguration;
	
	private DumperHelper<DeviceLocationHistory> dump;

	public void importFromGpsRecords(List<String> entityId, Integer entityKind) throws ASException {

		SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfPeriod = new SimpleDateFormat("yyyy-MM");
		
		String period = sdfPeriod.format(new Date());
		
		// Connections cache
		PersistentCacheFSImpl<ExternalGeo> cache = new PersistentCacheFSImpl<ExternalGeo>(
				ExternalGeo.class, systemConfiguration.getCacheMaxInMemElements(),
				systemConfiguration.getCachePageSize(), systemConfiguration.getCacheTempDir());

		// Devices cache
		PersistentCacheFSImpl<HashSet<String>> cacheDevices = new PersistentCacheFSImpl<HashSet<String>>(
				HashSet.class, systemConfiguration.getCacheMaxInMemElements(),
				systemConfiguration.getCachePageSize(), systemConfiguration.getCacheTempDir());

		try {
			
			Map<String, Map<Integer,HashSet<String>>> devices = CollectionFactory.createMap();
			List<String> entityIds = CollectionFactory.createList();
			
			if( entityKind != null && entityKind.equals(EntityKind.KIND_BRAND)) {
				for( String eid : entityId) {
					List<Store> stores = storeDao.getUsingBrandAndStatus(eid, StatusHelper.statusActive(), null);
					for(Store store : stores ) {
						entityIds.add(store.getIdentifier());
					}
				}
				entityKind = EntityKind.KIND_STORE;
			} else {
				if(entityId != null && !entityId.isEmpty())
					entityIds.addAll(entityId);
			}

			// First delete previous data
			log.log(Level.INFO, "Deleting previous data...");
			dao.deleteUsingEntityIdAndPeriod((PersistenceProvider)null, entityIds, entityKind, null, period);

			// Obtains a list of all the matched mac addresses
			log.log(Level.INFO, "Reading Matches...");
			
			PersistenceManager pm;
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
			pm.currentTransaction().begin();

			JDOConnection jdoConn = pm.getDataStoreConnection();
			DB db = (DB)jdoConn.getNativeConnection();

			DBCursor c1 = db.getCollection("APDeviceMacMatch").find();
			c1.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			Iterator<DBObject> i = c1.iterator();

			log.log(Level.INFO, "Processing " + c1.size() + " matches...");
			long count = 0;
			
			// Fetches the device list according to APDeviceMacMatch
			while(i.hasNext()) {
				DBObject dbo = i.next();
				if( dbo.containsField("deviceUUID")) {
					String deviceUUID = (String)dbo.get("deviceUUID");
					String mEntityId = (String)dbo.get("entityId");
					Integer type = (Integer)dbo.get("type");
					
					// The true format is DeviceUUID, (Peasant/Visitor), Stores in which is (Peasant/visitor)
					
					if( entityIds == null || entityIds.contains(mEntityId)) {
						Map<Integer, HashSet<String>> devices2 = devices.get(deviceUUID);
						if( devices2 == null ) devices2 = CollectionFactory.createMap();
						HashSet<String> devices3 = devices2.get(type);
						if( devices3 == null ) devices3 = new HashSet<String>();
						devices3.add(mEntityId);
						devices2.put(type, devices3);
						devices.put(deviceUUID, devices2);
					}
				}
				
				count++;
				if(count % 5000 == 0 ) {
					log.log(Level.INFO, "Processing APDeviceMacMatch Record " + count + " of " + c1.size());
				}
			}

			log.log(Level.INFO, devices.size() + " obtained records from APDeviceMacMatch...");

			jdoConn.close();
			pm.currentTransaction().commit();
			pm.close();

			// Now obtains the physical data information for the dump
			dump = new DumpFactory<DeviceLocationHistory>().build(null, DeviceLocationHistory.class);

			count = 0;
			long processed = 0;

			// Loops only with the selected date range
			Date workDate = sdf.parse(systemConfiguration.getExternalGeoFromDate());
			Date workFrom = sdf.parse(systemConfiguration.getExternalGeoFromDate());
			Date workTo = sdf.parse(systemConfiguration.getExternalGeoToDate());
			Date toDate = sdf.parse(systemConfiguration.getExternalGeoToDate());
			while(workDate.before(toDate) || workDate.equals(toDate)) {
				workFrom = new Date(workDate.getTime());
				workTo = new Date(workFrom.getTime() + DAY_IN_MILLIS);

				Iterator<JSONObject> it = dump.jsonIterator(workFrom, workTo);

				while( it.hasNext() ) {

					try {
						count++;
						JSONObject json = it.next();
						// Filter for only use matched devices
						if( json != null && json.has("deviceUUID") && devices.containsKey(json.getString("deviceUUID"))) {

							String uuid = json.getString("deviceUUID");
							@SuppressWarnings("deprecation")
							Date d = new Date(json.getString("lastUpdate"));
							int hour = Integer.valueOf(sdfHour.format(d));

							Map<Integer, HashSet<String>> devices2 = devices.get(uuid);
							Iterator<Integer> itx = devices2.keySet().iterator();
							while( itx.hasNext()) {
								Integer type = itx.next();
								HashSet<String> ids = devices2.get(type);
								for( String id : ids ) {
									try {
										Integer myType = dao.getType(type, hour);
										String key = dao.getHash(new Float(json.getDouble("lat")), 
												new Float(json.getDouble("lon")), period, id, entityKind, myType);

										ExternalGeo val = null;
										try {
											cache.get(key);
										} catch( Exception e ){}
										if( val == null ) {
											GeoPoint gp = geocoder.decodeGeohash(geocoder.encodeGeohash(json.getDouble("lat"), json.getDouble("lon")).substring(0,7));
											val = new ExternalGeo();
											val.setConnections(0);
											val.setExternalReference("GPS");
											val.setType(myType);
											val.setLat(Double.valueOf(gp.getLat()).floatValue());
											val.setLon(Double.valueOf(gp.getLon()).floatValue());
											val.setEntityId(id);
											val.setEntityKind(entityKind);
											val.setPeriod(period);
											val.setKey(dao.createKey(val));
										}
										val.setConnections(val.getConnections() + 1);
										cache.put(key, val);

										HashSet<String> valDevices = cacheDevices.get(key);
										if( valDevices == null ) valDevices = new HashSet<String>();
										valDevices.add(uuid);
										cacheDevices.put(key, valDevices);

										processed++;
									} catch( Exception e ) {
										log.log(Level.SEVERE, e.getMessage(), e);
									}
								}
							}

						}

						if( count % 5000 == 0 )
							log.log(Level.INFO, json.getString("creationDateTime") + " - " + count + " records processed with " + processed + " results...");
					} catch(Exception e) {}
					

				}

				// Iterates to next day
				Calendar cal = Calendar.getInstance();
				do {
					workDate = new Date(workDate.getTime() + 86400000 /* 24 hours */);
					cal.setTime(workDate);
				} while((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY 
						|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY));
			}

			
			log.log(Level.INFO, "Writing " + cache.size() + " records in the database...");
			count = 0;
			Iterator<ExternalGeo> it = cache.iterator();
			while(it.hasNext()) {
				ExternalGeo obj = it.next();
				if( obj != null ) {
					String key = obj.getIdentifier();
					obj.setUserCount(cacheDevices.containsKey(key) ? cacheDevices.get(key).size() : 0);
					externalGeoDao.create(obj);
					count ++;
					if( count % 1000 == 0 ) {
						log.log(Level.INFO, "Writing record " + count + " of " + cache.size() + " in Database...");
					}
				}
			}
			
			log.log(Level.INFO, "Disposing cache with " + cache.size() + " elements...");
			cache.dispose();
			cache.clear();

			log.log(Level.INFO, "Disposing Device cache with " + cacheDevices.size() + " elements...");
			cacheDevices.dispose();
			cacheDevices.clear();
			dump.dispose();
			
			log.log(Level.INFO, count + " records processed with " + processed + " results...");
			log.log(Level.INFO, "Process finished!");
			
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		
	}

	/**
	 * @return the dao
	 */
	public ExternalGeoDAO getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(ExternalGeoDAO dao) {
		this.dao = dao;
	}

}