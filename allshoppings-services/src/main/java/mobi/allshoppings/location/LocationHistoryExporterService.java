package mobi.allshoppings.location;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.datastore.JDOConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.inodes.datanucleus.model.Key;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.DeviceLocationHistoryDAO;
import mobi.allshoppings.dao.spi.DAOJDOPersistentManagerFactory;
import mobi.allshoppings.dao.spi.DeviceInfoDAOJDOImpl;
import mobi.allshoppings.dao.spi.DeviceLocationHistoryDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.impl.GeoCodingHelperGMapsImpl;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.DeviceLocationHistory;
import mobi.allshoppings.tools.CollectionFactory;

public class LocationHistoryExporterService {

	private static final Logger log = Logger.getLogger(LocationHistoryExporterService.class.getName());

	private DeviceInfoDAO diDao = new DeviceInfoDAOJDOImpl();
	private DeviceLocationHistoryDAO dlhDao = new DeviceLocationHistoryDAOJDOImpl();
	private GeoCodingHelper geocoder = new GeoCodingHelperGMapsImpl();

	public void exportLocationHistory(double lat, double lon, int distance, Date fromDate, Date toDate, File deviceLocationFile, File deviceInfoFile, int batchSize, boolean allActivity, File deviceInfoInputFile) throws ASException {

		List<String> devicesFound = CollectionFactory.createList(); 

		try {
			long totals = 0;
			if( deviceInfoInputFile == null ) {
				totals = dlhDao.countUsingDates(fromDate, toDate);
				log.log(Level.INFO, "Processing " + totals + " registers");
			}

			FileOutputStream fosDeviceLocation = new FileOutputStream(deviceLocationFile);
			FileOutputStream fosDeviceInfo  = null;
			if( deviceInfoInputFile == null ) {
				fosDeviceInfo = new FileOutputStream(deviceInfoFile);
			}

			long counter = 0;
			long processed = 0;
			long deviceCounter = 0;
			fosDeviceLocation.write("[".getBytes());
			if( deviceInfoInputFile == null ) {
				fosDeviceInfo.write("[".getBytes());
			}

			PersistenceManager pm;
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
			pm.currentTransaction().begin();

			JDOConnection jdoConn = pm.getDataStoreConnection();
			DB db = (DB)jdoConn.getNativeConnection();

			if(deviceInfoInputFile == null ) {
				final BasicDBObject query = new BasicDBObject("$and", Arrays.asList(new BasicDBObject("creationDateTime", new BasicDBObject("$gte", fromDate)), new BasicDBObject("creationDateTime", new BasicDBObject("$lte", toDate))));
				DBCursor c1 = db.getCollection("DeviceLocationHistory").find(query);
				c1.sort(new BasicDBObject("creationDateTime", 1));
				c1.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
				Iterator<DBObject> i = c1.iterator();

				// Fetches the device list
				while(i.hasNext()) {
					DBObject dbo = i.next();
					Key key = new Key(dbo.get("_id").toString());
					processed++;
					try {
						// Calculates distance to the target
						if( dbo.containsField("lat") && dbo.containsField("lon")) {
							long pointDistance = geocoder.calculateDistance(Double.parseDouble(dbo.get("lat").toString()), Double.parseDouble(dbo.get("lon").toString()), lat, lon);
							if( pointDistance <= distance ) {
								// Writes the DeviceLocation entry
								if( !allActivity ) {
									Gson gson = new Gson();
									String json = gson.toJson(new DeviceLocationAdapter(key
											.getName(), dbo.get("deviceUUID").toString(),
											Double.parseDouble(dbo.get("lat").toString()),
											Double.parseDouble(dbo.get("lon").toString()), dbo
											.get("geohash").toString(), (Date) dbo
											.get("creationDateTime")));
									if( counter > 0 ) {
										fosDeviceLocation.write(",".getBytes());
									}
									fosDeviceLocation.write(json.getBytes());
								}
								counter++;

								// Writes the deviceInfo entry, only if it was not added before
								if( !devicesFound.contains(dbo.get("deviceUUID").toString())) {
									try {
										DeviceInfo di = diDao.get(dbo.get("deviceUUID").toString(), true);
										devicesFound.add(dbo.get("deviceUUID").toString());
										Gson gson2 = new Gson();
										String json2 = gson2.toJson(new DeviceInfoAdapter(di));
										if( deviceCounter > 0 ) {
											fosDeviceInfo.write(",".getBytes());
										}
										fosDeviceInfo.write(json2.getBytes());
										deviceCounter++;
									} catch( Exception e ) {}
								}
							}
						}
					} catch( Exception e ) {
						log.log(Level.WARNING, e.getMessage(), e);
					}
					if( processed % batchSize == 0 )
						log.log(Level.INFO, "Processed " + processed + " of " + totals + " with " + counter + " results and " + deviceCounter + " devices...");
				}
			} else {
				FileInputStream fis = new FileInputStream(deviceInfoInputFile);
				byte[] data = new byte[(int) deviceInfoInputFile.length()];
				fis.read(data);
				fis.close();

				String str = new String(data, "UTF-8");
				JSONArray jsonArray = new JSONArray(str);
				for( int i = 0; i < jsonArray.length(); i++) {
					JSONObject obj = jsonArray.getJSONObject(i);
					devicesFound.add(obj.getString("identifier"));
				}
			}

			// And now fetches information about all the results
			if( allActivity ) {
				counter = 0;
				//Gets all DB information.
				DBCursor c2 = db.getCollection("DeviceLocationHistory").find();
				c2.sort(new BasicDBObject("creationDateTime", 1));
				c2.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
				Iterator<DBObject> i2 = c2.iterator();

				while(i2.hasNext()) {
					DBObject dbo = i2.next();
					Key key = new Key(dbo.get("_id").toString());
					if( dbo.containsField("lat") && dbo.containsField("lon") && dbo.containsField("lastUpdate")) {
						
						Date ref = (Date)dbo.get("lastUpdate");
						String uuid = (String)dbo.get("deviceUUID");
						if( ref.after(fromDate) && ref.before(toDate) && devicesFound.contains(uuid)) { 
						
							Gson gson = new Gson();
							if( dbo.containsField("deviceUUID") && dbo.containsField("lat") && dbo.containsField("lon") && dbo.containsField("geohash") && dbo.containsField("creationDateTime")) {
								String json = gson.toJson(new DeviceLocationAdapter(key
										.getName(), dbo.get("deviceUUID").toString(),
										Double.parseDouble(dbo.get("lat").toString()),
										Double.parseDouble(dbo.get("lon").toString()), dbo
										.get("geohash").toString(), (Date) dbo
										.get("creationDateTime")));
								if( counter > 0 ) {
									fosDeviceLocation.write(",".getBytes());
								}
								fosDeviceLocation.write(json.getBytes());
							counter++;
							}	
						}
						
						if( counter % batchSize == 0 )
							log.log(Level.INFO, "Processed " + processed + " of " + totals + " with " + counter + " results and " + deviceCounter + " devices...");
					}
				}
			}

			jdoConn.close();
			pm.currentTransaction().commit();
			pm.close();

			if( deviceInfoInputFile == null ) {
				fosDeviceInfo.write("]".getBytes());
				fosDeviceInfo.close();
			}

			fosDeviceLocation.write("]".getBytes());
			fosDeviceLocation.close();

			log.log(Level.INFO, "Process Finished! Processed " + totals + " registers with " + counter + " results!");

		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	class DeviceInfoAdapter {
		private String identifier;
		private String lang;
		private String deviceVersion;
		private String devicePlatform;
		private String deviceName;
		private Date creationDateTime;
		private Date lastUpdate;

		public DeviceInfoAdapter(DeviceInfo src) {
			this.identifier = src.getIdentifier();
			this.lang = src.getLang();
			this.deviceVersion = src.getDeviceVersion();
			this.devicePlatform = src.getDevicePlatform();
			this.deviceName = src.getDeviceName();
			this.creationDateTime = src.getCreationDateTime();
			this.lastUpdate = src.getLastUpdate();
		}

		/**
		 * @return the identifier
		 */
		public String getIdentifier() {
			return identifier;
		}

		/**
		 * @param identifier the identifier to set
		 */
		public void setIdentifier(String identifier) {
			this.identifier = identifier;
		}

		/**
		 * @return the lang
		 */
		public String getLang() {
			return lang;
		}

		/**
		 * @param lang the lang to set
		 */
		public void setLang(String lang) {
			this.lang = lang;
		}

		/**
		 * @return the deviceVersion
		 */
		public String getDeviceVersion() {
			return deviceVersion;
		}

		/**
		 * @param deviceVersion the deviceVersion to set
		 */
		public void setDeviceVersion(String deviceVersion) {
			this.deviceVersion = deviceVersion;
		}

		/**
		 * @return the devicePlatform
		 */
		public String getDevicePlatform() {
			return devicePlatform;
		}

		/**
		 * @param devicePlatform the devicePlatform to set
		 */
		public void setDevicePlatform(String devicePlatform) {
			this.devicePlatform = devicePlatform;
		}

		/**
		 * @return the deviceName
		 */
		public String getDeviceName() {
			return deviceName;
		}

		/**
		 * @param deviceName the deviceName to set
		 */
		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}

		/**
		 * @return the creationDateTime
		 */
		public Date getCreationDateTime() {
			return creationDateTime;
		}

		/**
		 * @param creationDateTime the creationDateTime to set
		 */
		public void setCreationDateTime(Date creationDateTime) {
			this.creationDateTime = creationDateTime;
		}

		/**
		 * @return the lastUpdate
		 */
		public Date getLastUpdate() {
			return lastUpdate;
		}

		/**
		 * @param lastUpdate the lastUpdate to set
		 */
		public void setLastUpdate(Date lastUpdate) {
			this.lastUpdate = lastUpdate;
		}

	}

	class DeviceLocationAdapter {
		private String identifier;
		private String deviceUUID;
		private double lat;
		private double lon;
		private String geohash;
		private Date creationDateTime;

		/**
		 * @param identifier
		 * @param deviceUUID
		 * @param lat
		 * @param lon
		 * @param geohash
		 * @param creationDateTime
		 */
		public DeviceLocationAdapter(String identifier, String deviceUUID,
				double lat, double lon, String geohash, Date creationDateTime) {
			super();
			this.identifier = identifier;
			this.deviceUUID = deviceUUID;
			this.lat = lat;
			this.lon = lon;
			this.geohash = geohash;
			this.creationDateTime = creationDateTime;
		}

		public DeviceLocationAdapter(DeviceLocationHistory src) {
			this.identifier = src.getIdentifier();
			this.deviceUUID = src.getDeviceUUID();
			this.lat = src.getLat();
			this.lon = src.getLon();
			this.geohash = src.getGeohash();
		}

		/**
		 * @return the identifier
		 */
		public String getIdentifier() {
			return identifier;
		}

		/**
		 * @param identifier the identifier to set
		 */
		public void setIdentifier(String identifier) {
			this.identifier = identifier;
		}

		/**
		 * @return the deviceUUID
		 */
		public String getDeviceUUID() {
			return deviceUUID;
		}

		/**
		 * @param deviceUUID the deviceUUID to set
		 */
		public void setDeviceUUID(String deviceUUID) {
			this.deviceUUID = deviceUUID;
		}

		/**
		 * @return the lat
		 */
		public double getLat() {
			return lat;
		}

		/**
		 * @param lat the lat to set
		 */
		public void setLat(double lat) {
			this.lat = lat;
		}

		/**
		 * @return the lon
		 */
		public double getLon() {
			return lon;
		}

		/**
		 * @param lon the lon to set
		 */
		public void setLon(double lon) {
			this.lon = lon;
		}

		/**
		 * @return the geohash
		 */
		public String getGeohash() {
			return geohash;
		}

		/**
		 * @param geohash the geohash to set
		 */
		public void setGeohash(String geohash) {
			this.geohash = geohash;
		}

		/**
		 * @return the creationDateTime
		 */
		public Date getCreationDateTime() {
			return creationDateTime;
		}

		/**
		 * @param creationDateTime the creationDateTime to set
		 */
		public void setCreationDateTime(Date creationDateTime) {
			this.creationDateTime = creationDateTime;
		}
	}
}
