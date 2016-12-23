package mobi.allshoppings.exporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.dao.APDeviceMacMatchDAO;
import mobi.allshoppings.dao.ExternalGeoDAO;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumperHelperImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.model.APDeviceMacMatch;
import mobi.allshoppings.model.DeviceLocationHistory;
import mobi.allshoppings.model.ExternalGeo;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tx.PersistenceProvider;

public class ExternalGeoImporter {

	private static final Logger log = Logger.getLogger(ExternalGeoImporter.class.getName());

	@Autowired
	private ExternalGeoDAO dao;

	@Autowired
	private APDeviceMacMatchDAO apdmDao;
	
	@Autowired
	private GeoCodingHelper geocoder;
	
	private DumperHelper<DeviceLocationHistory> dump;

	public void importFromGpsRecords(String entityId, Integer entityKind, String period, String hostname, Date fromDate, 
			Date toDate, String baseDir, String fromHour, String toHour, boolean workDays, Integer type) throws ASException {
		
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		// Connections cache
		Map<GeoPoint, ExternalGeo> cache = CollectionFactory.createMap();
		
		// First delete previous data
		log.log(Level.INFO, "Deleting previous data...");
		dao.deleteUsingEntityIdAndPeriod((PersistenceProvider)null, entityId, entityKind, type, period);
		
		try {
			
			// Obtains a list of all the matched mac addresses
			log.log(Level.INFO, "Reading Matches...");
			List<APDeviceMacMatch> list = apdmDao.getUsingHostname(hostname);
			Map<String, Boolean> deviceUUIDs = CollectionFactory.createMap();
			for( APDeviceMacMatch obj : list )
				deviceUUIDs.put(obj.getDeviceUUID(), true);

			log.log(Level.INFO, deviceUUIDs.size() + " matches found for " + hostname + "...");

			// Now obtains the physical data information for the dump
			dump = new DumperHelperImpl<DeviceLocationHistory>(baseDir, DeviceLocationHistory.class);

			long count = 0;
			long processed = 0;

			// Loops only with the selected date range
			Date workDate = new Date(fromDate.getTime());
			Date workFrom = null;
			Date workTo = null;
			while(workDate.before(toDate) || workDate.equals(toDate)) {
				if( fromHour == null || toHour == null ) {
					workFrom = new Date(workDate.getTime());
					workTo = new Date(workFrom.getTime() + 86400000 /* 24 hours */);
				} else {
					workFrom = sdf.parse(sdfDate.format(workDate) + " " + fromHour);
					workTo = sdf.parse(sdfDate.format(workDate) + " " + toHour);
				}

				Iterator<JSONObject> i = dump.jsonIterator(workFrom, workTo);

				boolean hasEvents = false;
				while( i.hasNext() ) {

					count++;
					JSONObject json = i.next();
					// Filter for only use matched devices
					if( json != null && json.has("deviceUUID") && deviceUUIDs.containsKey(json.getString("deviceUUID"))) {

						try {
							// round the position
							GeoPoint gp = geocoder.decodeGeohash(geocoder.encodeGeohash(json.getDouble("lat"), json.getDouble("lon")).substring(0,7));
							// And insert it on the cache
							ExternalGeo val = cache.get(gp);
							if( val == null ) {
								val = new ExternalGeo();
								val.setConnections(0);
								val.setExternalReference("GPS");
								val.setType(ExternalGeo.TYPE_GPS);
								val.setLat(Double.valueOf(gp.getLat()).floatValue());
								val.setLon(Double.valueOf(gp.getLon()).floatValue());
								val.setEntityId(entityId);;
								val.setEntityKind(entityKind);;
								val.setPeriod(period);
								val.setKey(dao.createKey());
							}
							val.setConnections(val.getConnections() + 1);
							hasEvents = true;
							cache.put(gp, val);

							processed++;
						} catch( Exception e ) {
							log.log(Level.SEVERE, e.getMessage(), e);
						}

					}

					if( count % 1000 == 0 )
						log.log(Level.INFO, json.getString("creationDateTime") + " - " + count + " records processed with " + processed + " results...");

				}

				// Writes down the objects, just in case there where events
				if( hasEvents ) {
					log.log(Level.INFO, "Writing down " + cache.size() + " objects...");
					Iterator<GeoPoint> x = cache.keySet().iterator();
					while( x.hasNext() ) {
						GeoPoint gp = x.next();
						ExternalGeo obj = cache.get(gp);
						dao.createOrUpdate(obj);
					}
				}

				// Iterates to next day
				Calendar cal = Calendar.getInstance();
				do {
					workDate = new Date(workDate.getTime() + 86400000 /* 24 hours */);
					cal.setTime(workDate);
				} while( workDays 
						&& (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY 
						|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY));
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