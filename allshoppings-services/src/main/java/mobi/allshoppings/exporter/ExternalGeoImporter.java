package mobi.allshoppings.exporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import mobi.allshoppings.dump.impl.DumperHelperImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.DeviceLocationHistory;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.ExternalGeo;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tx.PersistenceProvider;

public class ExternalGeoImporter {

	private static final Logger log = Logger.getLogger(ExternalGeoImporter.class.getName());

	@Autowired
	private ExternalGeoDAO dao;

	@Autowired
	private GeoCodingHelper geocoder;
	
	@Autowired
	private StoreDAO storeDao;
	
	private DumperHelper<DeviceLocationHistory> dump;

	public void importFromGpsRecords(List<String> entityId, Integer entityKind, String baseDir) throws ASException {

		SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfPeriod = new SimpleDateFormat("yyyy-MM");
		
		String period = sdfPeriod.format(new Date());
		
		// Connections cache
		Map<String, Map<GeoPoint, ExternalGeo>> cache = CollectionFactory.createMap();
		
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
			if( entityIds.isEmpty() ) {
				dao.deleteUsingEntityIdAndPeriod((PersistenceProvider)null, null, entityKind, null, period);
			} else {
				for( String eid : entityIds ) {
					dao.deleteUsingEntityIdAndPeriod((PersistenceProvider)null, eid, entityKind, null, period);
				}
			}

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
			// Fetches the visit list
			while(i.hasNext()) {
				DBObject dbo = i.next();
				if( dbo.containsField("deviceUUID")) {
					String deviceUUID = (String)dbo.get("deviceUUID");
					String mEntityId = (String)dbo.get("entityId");
					Integer type = (Integer)dbo.get("type");
					
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
			dump = new DumperHelperImpl<DeviceLocationHistory>(baseDir, DeviceLocationHistory.class);

			count = 0;
			long processed = 0;

			// Loops only with the selected date range
			Date workDate = sdf.parse("2016-01-01");
			Date workFrom = sdf.parse("2016-01-01");
			Date workTo = sdf.parse("2017-01-01");
			Date toDate = sdf.parse("2017-01-01");
			while(workDate.before(toDate) || workDate.equals(toDate)) {
				workFrom = new Date(workDate.getTime());
				workTo = new Date(workFrom.getTime() + 86400000 /* 24 hours */);

				Iterator<JSONObject> it = dump.jsonIterator(workFrom, workTo);

				while( it.hasNext() ) {

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
									// round the position
									GeoPoint gp = geocoder.decodeGeohash(geocoder.encodeGeohash(json.getDouble("lat"), json.getDouble("lon")).substring(0,7));
									// And insert it on the cache
									Map<GeoPoint, ExternalGeo> cache2 = cache.get(uuid);
									if( cache2 == null ) cache2 = CollectionFactory.createMap();
									
									ExternalGeo val = cache2.get(gp);
									if( val == null ) {
										val = new ExternalGeo();
										val.setConnections(0);
										val.setExternalReference("GPS");
										if( type == APDVisit.CHECKIN_PEASANT ) {
											if( hour >= 2 && hour <= 5 ) {
												val.setType(ExternalGeo.TYPE_GPS_HOME_PEASANT);
											} else {
												val.setType(ExternalGeo.TYPE_GPS_WORK_PEASANT);
											}
										} else {
											if( hour >= 2 && hour <= 5 ) {
												val.setType(ExternalGeo.TYPE_GPS_HOME);
											} else {
												val.setType(ExternalGeo.TYPE_GPS_WORK);
											}
										}
										val.setLat(Double.valueOf(gp.getLat()).floatValue());
										val.setLon(Double.valueOf(gp.getLon()).floatValue());
										val.setEntityId(id);
										val.setEntityKind(entityKind);
										val.setPeriod(period);
										val.setKey(dao.createKey());
									}
									val.setConnections(val.getConnections() + 1);
									cache2.put(gp, val);

									cache.put(uuid, cache2);
									processed++;
								} catch( Exception e ) {
									log.log(Level.SEVERE, e.getMessage(), e);
								}
							}
						}

					}

					if( count % 5000 == 0 )
						log.log(Level.INFO, json.getString("creationDateTime") + " - " + count + " records processed with " + processed + " results...");

				}

				// Iterates to next day
				Calendar cal = Calendar.getInstance();
				do {
					workDate = new Date(workDate.getTime() + 86400000 /* 24 hours */);
					cal.setTime(workDate);
				} while((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY 
						|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY));
			}

			// Writes down the objects, just in case there where events
			log.log(Level.INFO, "Writing down " + cache.size() + " objects...");
			Iterator<String> x = cache.keySet().iterator();
			while( x.hasNext() ) {
				String eid = x.next();
				Map<GeoPoint, ExternalGeo> cache2 = cache.get(eid);
				List<ExternalGeo> values = new ArrayList<ExternalGeo>(cache2.values());
				Collections.sort(values, new Comparator<ExternalGeo>() {
					@Override
					public int compare(ExternalGeo o1, ExternalGeo o2) {
						return o2.getConnections().compareTo(o1.getConnections());
					}
				});

				int cnt = 0;
				Iterator<ExternalGeo> y = values.iterator();
				while(y.hasNext()) {
					ExternalGeo obj = y.next();
					dao.createOrUpdate(obj);
					cnt++;
					if(cnt > 500 ) break;
				}
			}

			log.log(Level.INFO, count + " records processed with " + processed + " results...");
			log.log(Level.INFO, "Process finished!");
			
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		
	}
	
	public void importInfo(String venue, String period, String fileName ) throws ASException {

		// First delete previous data
		dao.deleteUsingVenueAndPeriod(null, venue, period);

		try {
			// Opens input file
			File file = new File(fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			// "probe","conexiones","lat","lon"
			// "..izzi-WiFi.._1",156,19.38262558,-99.17722321
			// ....

			int rec = 0;
			long start = new Date().getTime();
			String line = br.readLine();
			while(line != null) {

				if( rec > 0 ) {
					String[] parts = line.split(",");
					if( parts.length == 4) {
						try {
							String externalReference = parts[0].replaceAll("\"", "");
							int connections = Integer.parseInt(parts[1]);
							float lat = Float.parseFloat(parts[2]);
							float lon = Float.parseFloat(parts[3]);
							
							ExternalGeo obj = new ExternalGeo();
							obj.setVenue(venue);
							obj.setPeriod(period);
							obj.setExternalReference(externalReference);
							obj.setConnections(connections);
							obj.setType(ExternalGeo.TYPE_WIFI);
							obj.setLat(lat);
							obj.setLon(lon);
							
							obj.setKey(dao.createKey());
							dao.create(obj);
							
						} catch( Exception e ) {
							log.log(Level.WARNING, "Invalid record: " + line);
						}
					}
				}
				rec++;
				line = br.readLine();
			}
			
			br.close();

			long end = new Date().getTime();
			
			log.log(Level.INFO, rec + " records processed in " + (end - start) + "ms" );
			
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