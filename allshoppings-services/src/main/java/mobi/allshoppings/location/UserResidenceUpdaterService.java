package mobi.allshoppings.location;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.util.StringUtils;

import com.google.gson.Gson;

import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumperHelperImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.geocoding.impl.GeoCodingHelperGMapsImpl;
import mobi.allshoppings.model.Address;
import mobi.allshoppings.model.DeviceLocationHistory;
import mobi.allshoppings.tools.CollectionFactory;


public class UserResidenceUpdaterService {

	private static final Logger log = Logger.getLogger(UserResidenceUpdaterService.class.getName());
	private static final String BASE_NAME = "UserResidence";
	private static final int MIN_HOUR = 2;
	private static final int MAX_HOUR = 5;
	private static final int GEOHASH_PRESITION = 8;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	protected GeoCodingHelper geocoder = new GeoCodingHelperGMapsImpl();
	protected Gson gson = new Gson();

	public Set<String> updateResidencePhase1(String baseDir, Date fromDate, Date toDate) throws ASException, IOException {

		Set<String> ret = CollectionFactory.createSet();
		DumperHelper<DeviceLocationHistory> dumper = new DumperHelperImpl<DeviceLocationHistory>(baseDir, DeviceLocationHistory.class);

		long totals = 0;
		long count = 0;
		long initTime = new Date().getTime();
		
		DeviceLocationHistory obj = null;
		Calendar cal = Calendar.getInstance();
		UserResidenceData residenceData = null;
		
		Iterator<DeviceLocationHistory> i = dumper.iterator(fromDate, toDate);
		while(i.hasNext()) {

			obj = i.next();
			if( obj != null && obj.getLat() != null && obj.getLon() != null && obj.getCreationDateTime() != null ) {
				totals++;
				
				if( totals % 1000 == 0 ) 
					log.log(Level.INFO, "Processing for date " + obj.getCreationDateTime());
				
				cal.setTime(obj.getCreationDateTime());
				int hour = cal.get(Calendar.HOUR_OF_DAY);
				int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
				
				// Checks for records that are taken in a week day night (from Sunday to Thursday)
				// And between the range of 2AM and 5AM
				if( dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.FRIDAY) {
					if( hour >= MIN_HOUR && hour <= MAX_HOUR ) {
						
						count++;
						ret.add(obj.getDeviceUUID());
						
						// Candidate Record Found
						residenceData = retrieveResidenceDataList(baseDir, obj.getDeviceUUID());
						if( residenceData == null ) {
							residenceData = new UserResidenceData(obj.getDeviceUUID(), obj.getUserId());
						}
						
						if(!StringUtils.hasText(obj.getGeohash())) obj.setGeohash(geocoder.encodeGeohash(obj.getLat(), obj.getLon()));
						residenceData.addMetric(obj.getCreationDateTime(), obj.getGeohash(), GEOHASH_PRESITION);
						dump(baseDir, residenceData);
						
					}
				}

			}
		}

		long endTime = new Date().getTime();
		log.log(Level.INFO, totals + " elements calculated and " + count + " processed in " + (endTime - initTime) + "ms");

		return ret;
	}
	
	public void updateResidencePhase2(String baseDir, Set<String> deviceSet, Date fromDate, Date toDate) throws ASException, IOException {

		long totals = 0;
		long initTime = new Date().getTime();

		Calendar cal = Calendar.getInstance();
		UserResidenceData residenceData = null;
		String deviceUUID = null;
		
		// Prepares the date list to query
		List<String> dateList = CollectionFactory.createList();
		cal.setTime(fromDate);
		while( cal.getTime().before(toDate)) {
			String dat = sdf.format(cal.getTime());
			dateList.add(dat);
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		Iterator<String> i = deviceSet.iterator();
		while(i.hasNext()) {

			deviceUUID = i.next();
			residenceData = retrieveResidenceDataList(baseDir, deviceUUID);
			totals++;
			
			Map<String, Integer> residenceGeoHashes = CollectionFactory.createMap();
			int deviceCount = 0;
			for( String date : dateList ) {
				Map<String, Integer> tmp = residenceData.getResidenceData().get(date);
				if( tmp != null ) {
					Iterator<String> x = tmp.keySet().iterator();
					while(x.hasNext()) {
						String key = x.next();
						Integer val = residenceGeoHashes.get(key);
						if( val == null ) val = 0;
						val += tmp.get(key);
						deviceCount += tmp.get(key);
						residenceGeoHashes.put(key, val);
					}
				}
			}
			
			log.log(Level.FINE, "Locations for device " + residenceData.getDeviceUUID() + ":");
			String probableHash = "";
			int probableVal = 0;
			Iterator<String> x = residenceGeoHashes.keySet().iterator();
			while(x.hasNext()) {
				String key = x.next();
				Integer val = residenceGeoHashes.get(key);
				int percent = val * 100 / deviceCount;
				if( percent >= 33 && percent > probableVal) {
					probableHash = key;
					probableVal = percent;
				}
				log.log(Level.FINE, "\t" + key + ": " + percent + "%");
			}
			if(StringUtils.hasText(probableHash)) {
				GeoPoint gp = geocoder.decodeGeohash(probableHash);
				log.log(Level.FINE, "\tMost Probable Location: " + probableHash + ": "+ probableVal + "% lat:" + gp.getLat() + ", " + gp.getLon() );
			} else {
				log.log(Level.FINE, "\tMost Probable Location: UNDEFINED");
			}
			
			try {
				if( StringUtils.hasText(probableHash)) {
					residenceData.setProbableHash(probableHash);
					residenceData.setAddress(geocoder.getAddressUsingGeohash(probableHash));
					System.out.println("\tMost Probable Location: " + probableHash + ": "+ probableVal + "% " + residenceData.getAddress());
				} else {
					residenceData.setProbableHash(null);
					residenceData.setAddress(null);
				}
			} catch( Exception e ) {
				residenceData.setAddress(null);
			}

			dump(baseDir, residenceData);
			
		}

		long endTime = new Date().getTime();
		log.log(Level.INFO, totals + " elements calculated in " + (endTime - initTime) + "ms");
		
	}

	public void dump(String baseDir, UserResidenceData obj) throws IOException {

		File f = new File(resolveDumpFileName(baseDir, obj.getDeviceUUID()));
		String jsonRep = gson.toJson(obj);
		dump(jsonRep, f);
		
	}
	
	public static void dump(String jsonRep, File file) throws IOException {
		synchronized(file.getAbsolutePath()) {

			FileOutputStream fos ;
			File dir = file.getParentFile();
			if( !dir.exists() ) dir.mkdirs();
			fos = new FileOutputStream(file, false);

			try {
				fos.write(jsonRep.getBytes());
				fos.write("\n".getBytes());
				fos.flush();
				fos.close();
			} catch( Throwable t ) {
				throw t;
			} finally {
				fos.flush();
				fos.close();
			}
		}
	}
	
	public String resolveDumpFileName(String baseDir, String deviceUUID) {

		StringBuffer sb = new StringBuffer();
		sb.append(baseDir);
		if(!baseDir.endsWith(File.separator)) sb.append(File.separator);
		sb.append(BASE_NAME).append(File.separator);
		sb.append(deviceUUID).append(".json");

		return sb.toString();
	}

	public UserResidenceData retrieveResidenceDataList(String baseDir, String deviceUUID) throws IOException {

		File f = new File(resolveDumpFileName(baseDir, deviceUUID));
		if( f.exists() && f.canRead()) {
			BufferedReader br = null;
			try{ 
				br = new BufferedReader(new FileReader(f));
				for(String line; (line = br.readLine()) != null; ) {
					try {
						UserResidenceData element = gson.fromJson(line, UserResidenceData.class);
						return element;
					} catch( Exception e ) {
						log.log(Level.SEVERE, e.getMessage(), e);
					}
				}
			} finally {
				br.close();
			}
		}
		return null;
	}

	class UserResidenceData {
		private String deviceUUID;
		private String userId;
		private Map<String, Map<String, Integer>> residenceData;
		private String probableHash;
		private Address address;
		
		public UserResidenceData(String deviceUUID, String userId) {
			this.deviceUUID = deviceUUID;
			this.userId = userId;
			this.residenceData = CollectionFactory.createMap();
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
		 * @return the userId
		 */
		public String getUserId() {
			return userId;
		}

		/**
		 * @param userId the userId to set
		 */
		public void setUserId(String userId) {
			this.userId = userId;
		}

		/**
		 * @return the residenceData
		 */
		public Map<String, Map<String, Integer>> getResidenceData() {
			return residenceData;
		}

		/**
		 * @param residenceData the residenceData to set
		 */
		public void setResidenceData(Map<String, Map<String, Integer>> residenceData) {
			this.residenceData = residenceData;
		}

		public void addMetric(Date forDate, String geohash, int presition) {
			String truncatedDate = sdf.format(forDate);
			Map<String, Integer> data = residenceData.get(truncatedDate);
			if( data == null ) data = CollectionFactory.createMap();
			Integer value = data.get(geohash.substring(0, presition));
			if( value == null ) value = 0;
			value++;
			data.put(geohash.substring(0, presition), value);
			residenceData.put(truncatedDate, data);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((deviceUUID == null) ? 0 : deviceUUID.hashCode());
			result = prime * result
					+ ((userId == null) ? 0 : userId.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			UserResidenceData other = (UserResidenceData) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (deviceUUID == null) {
				if (other.deviceUUID != null)
					return false;
			} else if (!deviceUUID.equals(other.deviceUUID))
				return false;
			if (userId == null) {
				if (other.userId != null)
					return false;
			} else if (!userId.equals(other.userId))
				return false;
			return true;
		}

		private UserResidenceUpdaterService getOuterType() {
			return UserResidenceUpdaterService.this;
		}

		/**
		 * @return the probableHash
		 */
		public String getProbableHash() {
			return probableHash;
		}

		/**
		 * @param probableHash the probableHash to set
		 */
		public void setProbableHash(String probableHash) {
			this.probableHash = probableHash;
		}

		/**
		 * @return the address
		 */
		public Address getAddress() {
			return address;
		}

		/**
		 * @param address the address to set
		 */
		public void setAddress(Address address) {
			this.address = address;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "UserResidenceData [deviceUUID=" + deviceUUID + ", userId="
					+ userId + ", residenceData=" + residenceData
					+ ", probableHash=" + probableHash + ", address=" + address
					+ "]";
		}

	}
}
