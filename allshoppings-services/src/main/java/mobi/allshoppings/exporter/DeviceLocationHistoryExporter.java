package mobi.allshoppings.exporter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.spi.DeviceInfoDAOJDOImpl;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumperHelperImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.impl.GeoCodingHelperGMapsImpl;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.DeviceLocationHistory;
import mobi.allshoppings.model.DeviceWifiLocationHistory;

import com.google.gson.Gson;

/**
 * This class testing dump or export information features 
 * to export information about one location or geofence we should
 * process 3 files or entities:
 * 
 * DeviceLocationHistory: This entity contains the information of the geofences where the device has passed.
 * DeviceInfo: This entity contains all the details of the device (model, OS, version, branch)
 * DeviceWifiLocationHistory: This entity contains the indoor geofences delimited by the wifi connections available on the indoor location.
 * 
 * 
 * */
public class DeviceLocationHistoryExporter {
	
	private static final Logger log = Logger.getLogger(DeviceLocationHistoryExporter.class.getName());

	private static final String BASE_DIR_FILES_TO_PROCESS = "/usr/local/allshoppings/dump";
	private static final SimpleDateFormat SDF_DATETIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");
	private static final Gson GSON = new Gson();
	//Coordenadas de Espacio Las Americas.
	private static final double BASE_LAT = 19.68905016993142;
	private static final double BASE_LON = -101.15796727519182;
	private static final Integer CHECKIN_DISTANCE = 120; //en metros
	
	private static final String BASE_EXPORT_PATH = BASE_DIR_FILES_TO_PROCESS + "/descifra/2015/08/";
	private static final String DLH_OUTPUT_FILE_NAME = "_deviceLocationHistory.json";
	private static final String DWLH_OUTPUT_FILE_NAME = "_deviceWifiLocationHistory.json";
	private static final String DI_OUTPUT_FILE_NAME = "deviceInfo.json";
	
	//List to keep the ID's  were find into the exported file to use later to recovery detailed info about the device. 
	private static final List<String> ID_DEVICES_INTO_FENCE = new ArrayList<String>();
	
	private static final GeoCodingHelper geocoder = new GeoCodingHelperGMapsImpl();
	private static final DeviceInfoDAO diDao = new DeviceInfoDAOJDOImpl();


	public static void main(String[] args) {
		DeviceLocationHistoryExporter dhle = new DeviceLocationHistoryExporter();
		dhle.exportInfo();
	}
	
	public void exportInfo() {

		log.log(Level.INFO, "Starting export process");
		DumperHelper<DeviceLocationHistory> dlhDumper = new DumperHelperImpl<DeviceLocationHistory>(BASE_DIR_FILES_TO_PROCESS, DeviceLocationHistory.class);			

		Date fromDate = new Date(0);
		Date toDate = new Date(0);;
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();			
		try {
			fromDate = SDF_DATETIME.parse("2015-08-01 00:00:00");		
			startDate.setTime(fromDate);			
			toDate = SDF_DATETIME.parse("2015-08-30 23:59:59");
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		File diOutputF = new File(BASE_EXPORT_PATH + DI_OUTPUT_FILE_NAME);
		//I will processing day by day to get less heavy files 
		while(startDate.getTime().before(toDate)) {
			//String geohashToFind = geocoder.encodeGeohash(BASE_LAT, BASE_LON);				
			
			endDate.setTime(startDate.getTime());
			endDate.add(Calendar.DATE, 1);
			Iterator<DeviceLocationHistory> iteDLH = dlhDumper.iterator(startDate.getTime(), endDate.getTime());
			String prefixFromToDate = SDF_DATE.format(startDate.getTime()) +"_to_"+ SDF_DATE.format(endDate.getTime());
			
			log.log(Level.INFO, "Processing info from " + startDate.getTime() + " to " + endDate.getTime());
			log.log(Level.INFO, "Iterating dlh Objects");				
			
			while(iteDLH.hasNext()) { 
				DeviceLocationHistory dlhObject = iteDLH.next();
				// If the object doesn't have latitude and longitude we move to next.
				if( dlhObject != null && dlhObject.getLat() != null && dlhObject.getLon() != null ) {
					
					// to compare fast we set the geohash into the object 
					if( dlhObject.getGeohash() == null ) 
						dlhObject.setGeohash(geocoder.encodeGeohash(dlhObject.getLat(), dlhObject.getLon()));
					
					Integer distanceBetweenCoord = geocoder.calculateDistance(BASE_LAT, BASE_LON, dlhObject.getLat(), dlhObject.getLon());

					//Saves into a List all the ID's of the devices that are into the geofence area.
					if(distanceBetweenCoord <= CHECKIN_DISTANCE && !ID_DEVICES_INTO_FENCE.contains(dlhObject.getDeviceUUID())) {						
						ID_DEVICES_INTO_FENCE.add(dlhObject.getDeviceUUID());
						// Obtain the detailed info of the device ID
						log.log(Level.INFO, "Getting deviceInfo");
						DeviceInfo di = null;
						try {
							di = diDao.get(dlhObject.getDeviceUUID(), true);
						} catch (ASException e) {
							log.log(Level.WARNING, "Error getting detailed info for deviceUUID:" + dlhObject.getDeviceUUID());
							e.printStackTrace();
						}
						if(null != di) {
							try {
								DumperHelperImpl.dump(GSON.toJson(new DeviceInfoAdapter(di)), diOutputF);
							} catch (IOException e) {
								log.log(Level.SEVERE, "Error dumping detailed info for deviceUUID:" + dlhObject.getDeviceUUID());
								e.printStackTrace();
							}					
						}
						log.log(Level.INFO, "END: getting & dump deviceInfo");
					}
					
				}
			}
			
			log.log(Level.INFO, "END: Iterating dlh Objects");

			File dhlOutputF = new File(BASE_EXPORT_PATH + prefixFromToDate + DLH_OUTPUT_FILE_NAME);
			//Loads iterator of DeviceLocationHistory again
			iteDLH = dlhDumper.iterator(startDate.getTime(), endDate.getTime());
			//Next step I have to get all the information about a device on DeviceLocationHistory and writes into a file.
			log.log(Level.INFO, "Getting deviceLocationHistory");
			while(iteDLH.hasNext()) { 
				DeviceLocationHistory dlhObject = iteDLH.next();
				if(ID_DEVICES_INTO_FENCE.contains(dlhObject.getDeviceUUID())) {
					try {
						DumperHelperImpl.dump(GSON.toJson(dlhObject), dhlOutputF);
					} catch (IOException e) {
						log.log(Level.SEVERE, "Error dumping DeviceHistoryLocation for deviceUUID:" + dlhObject.getDeviceUUID());
						e.printStackTrace();
					}					
				}
			}
			log.log(Level.INFO, "END: Getting deviceLocationHistory");
			
			//Now we have to process all the indoor info
			DumperHelper<DeviceWifiLocationHistory> dwlhDumper = new DumperHelperImpl<DeviceWifiLocationHistory>(BASE_DIR_FILES_TO_PROCESS, DeviceWifiLocationHistory.class);
			Iterator<DeviceWifiLocationHistory> iteDWLH = dwlhDumper.iterator(startDate.getTime(), endDate.getTime());
			//Finally I have to get all the information about indoor location and writes into a file.
			File dwlhOutputF = new File(BASE_EXPORT_PATH + prefixFromToDate + DWLH_OUTPUT_FILE_NAME);			
			log.log(Level.INFO, "Iterating dwlh Objects");
			while(iteDWLH.hasNext()) {
				DeviceWifiLocationHistory dwlhObject = iteDWLH.next();
				if(ID_DEVICES_INTO_FENCE.contains(dwlhObject.getDeviceUUID()) && (null != dwlhObject.getWifiSpotId())) {
					try {
						DumperHelperImpl.dump(GSON.toJson(dwlhObject), dwlhOutputF);
					} catch (IOException e) {
						log.log(Level.SEVERE, "Error dumping detailed info for deviceUUID:" + dwlhObject.getDeviceUUID());
						e.printStackTrace();
					}
					
				}
			}
			log.log(Level.INFO, "END: Iterating dwlh Objects");
			log.log(Level.INFO, "END: Processing info from " + startDate.getTime() + " to " + endDate.getTime());
			
			startDate.add(Calendar.DATE, 1);
		}
	
		System.exit(0);
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