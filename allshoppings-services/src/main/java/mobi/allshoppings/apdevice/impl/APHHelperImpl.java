package mobi.allshoppings.apdevice.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.apdevice.APHHelper;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.APHEntryDAO;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.ExternalAPHotspotDAO;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumperHelperImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.ExternalAPHotspot;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CollectionUtils;

public class APHHelperImpl implements APHHelper {

	private static final Logger log = Logger.getLogger(APHHelperImpl.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Gson gson = new Gson();
	private static final DecimalFormat df = new DecimalFormat("00");
	private static final long ONE_HOUR = 3600000;
	
	private Map<String, APHEntry> cache;

	@Autowired
	private APDeviceDAO dao;
	@Autowired
	private APHEntryDAO apheDao;
	@Autowired
	private APDeviceHelper apdHelper;
	@Autowired
	private DeviceInfoDAO diDao;
	@Autowired
	private ExternalAPHotspotDAO eaphDao;
	
	private DumperHelper<APHotspot> dumpHelper;
	private boolean cacheBuilt = false;
	private boolean scanInDevices = true;
	private boolean useCache = true;
	private TimeZone tz;

	/**
	 * Standard Constructor
	 */
	public APHHelperImpl() {
		cache = CollectionFactory.createMap();
		tz = TimeZone.getDefault();
	}
	
	/**
	 * Gets the internal hash code for a given combination of hostname, mac and
	 * date
	 * 
	 * @param hostname
	 * @param mac
	 * @param date
	 * @return
	 */
	@Override
	public String getHash(String hostname, String mac, String date) {
		APHEntry obj = new APHEntry(hostname, mac, date);
		return obj.getMac() + ":" + obj.getHostname() + ":" + obj.getDate();
	}

	/**
	 * Gets the internal hash code for a given combination of hostname, mac and
	 * date
	 * 
	 * @param hostname
	 * @param mac
	 * @param date
	 * @return
	 */
	@Override
	public String getHash(String hostname, String mac, Date date) {
		return getHash(hostname, mac, sdf.format(date));
	}
	
	/**
	 * Gets the internal hash code based in an APHotspot
	 * @param obj
	 * @return
	 */
	@Override
	public String getHash(APHotspot obj) {
		return getHash(obj.getHostname(), obj.getMac(), obj.getCreationDateTime()); 
	}

	/**
	 * Gets the internal hash code based in an APHotspot
	 * @param obj
	 * @return
	 */
	@Override
	public String getHash(ExternalAPHotspot obj) {
		return getHash(obj.getHostname(), obj.getMac(), obj.getFirstSeen()); 
	}

	
	/**
	 * Gets the internal hash code based in an APHEntry
	 * @param obj
	 * @return
	 */
	@Override
	public String getHash(APHEntry obj) {
		return getHash(obj.getHostname(), obj.getMac(), obj.getDate()); 
	}

	/**
	 * Converts an APHotspot into a APHEntry representation
	 * @param obj
	 * @return
	 */
	@Override
	public APHEntry apHotpostToaphEntry(APHotspot obj) {
		return new APHEntry(obj.getHostname(), obj.getMac(), sdf.format(obj.getCreationDateTime()));
	}

	/**
	 * Converts an APHotspot into a APHEntry representation
	 * @param obj
	 * @return
	 */
	@Override
	public APHEntry apHotpostToaphEntry(ExternalAPHotspot obj) {
		return new APHEntry(obj.getHostname(), obj.getMac(), sdf.format(obj.getFirstSeen()));
	}

	/**
	 * Obtains an APHEntry representation from cache
	 * @param obj
	 * @return
	 */
	public APHEntry getFromCache(String hostname, String mac, String date) {
		String hash = getHash(hostname, mac, date);
		APHEntry ret = useCache ? cache.get(hash) : null;
		if( null == ret ) {
			try {
				if( cacheBuilt == true && useCache ) 
					throw ASExceptionHelper.notFoundException();
				
				ret = apheDao.get(hash, true);
			} catch( ASException e ) {
				ret = new APHEntry(hostname, mac, date);
				try {
					if( scanInDevices ) {
						List<DeviceInfo> di = diDao.getUsingMAC(mac);
						if( di.size() > 0 ) {
							ret.setDevicePlatform(di.get(0).getDevicePlatform());
						} else {
							throw ASExceptionHelper.notFoundException();
						}
					} else {
						throw ASExceptionHelper.notFoundException();
					}
				} catch( ASException e1 ) {
					ret.setDevicePlatform(apdHelper.getDevicePlatform(mac, null));
				}
			}
		}
		return ret;
	}

	/**
	 * Obtains an APHEntry representation from cache
	 * @param obj
	 * @return
	 */
	public APHEntry getFromCache(APHotspot obj) {
		String hash = getHash(obj);
		APHEntry ret = null;
		if( useCache ) {
			ret = cache.get(hash);
		}
		if( null == ret ) {
			try {
				if( cacheBuilt == true && useCache ) 
					throw ASExceptionHelper.notFoundException();
				
				ret = apheDao.get(hash, true);
			} catch( ASException e ) {
				ret = apHotpostToaphEntry(obj);
				try {
					if( scanInDevices ) {
						List<DeviceInfo> di = diDao.getUsingMAC(obj.getMac());
						if( di.size() > 0 ) {
							ret.setDevicePlatform(di.get(0).getDevicePlatform());
						} else {
							throw ASExceptionHelper.notFoundException();
						}
					} else {
						throw ASExceptionHelper.notFoundException();
					}
				} catch( ASException e1 ) {
					ret.setDevicePlatform(apdHelper.getDevicePlatform(obj.getMac(), null));
				}
			}
		}
		return ret;
	}

	/**
	 * Obtains an APHEntry representation from cache
	 * @param obj
	 * @return
	 */
	public APHEntry getFromCache(ExternalAPHotspot obj) {
		String hash = getHash(obj);
		APHEntry ret = null;
		if( useCache ) {
			ret = cache.get(hash);
		}
		if( null == ret ) {
			try {
				if( cacheBuilt == true && useCache ) 
					throw ASExceptionHelper.notFoundException();
				
				ret = apheDao.get(hash, true);
			} catch( ASException e ) {
				ret = apHotpostToaphEntry(obj);
				try {
					if( scanInDevices ) {
						List<DeviceInfo> di = diDao.getUsingMAC(obj.getMac());
						if( di.size() > 0 ) {
							ret.setDevicePlatform(di.get(0).getDevicePlatform());
						} else {
							throw ASExceptionHelper.notFoundException();
						}
					} else {
						throw ASExceptionHelper.notFoundException();
					}
				} catch( ASException e1 ) {
					ret.setDevicePlatform(apdHelper.getDevicePlatform(obj.getMac(), null));
				}
			}
		}
		return ret;
	}

	/**
	 * Inserts an APHEntry representation in the cache
	 * @param obj
	 * @return
	 */
	public APHEntry putInCache(APHEntry obj) {
		if( useCache ) {
			String hash = getHash(obj);
			cache.put( hash, obj );
		}
		return obj;
	}
	
	/**
	 * Sets the correct RSSI in a placed frame Frames are divided in the seconds
	 * of the day / 20
	 * 
	 * @param aphe
	 *            The referred APHEntry
	 * @param forDate
	 *            The date to frame
	 * @param rssi
	 *            The RSSI to set into the frame
	 * @return The modified APHEntry
	 */
	@Override
	public APHEntry setFramedRSSI(APHEntry aphe, Date forDate, Integer rssi) {
		if( rssi == null || rssi.equals(0)) return aphe;
		long offset = tz.getOffset(forDate.getTime());
		long secondsOfDay = (long)(((forDate.getTime() + offset ) % 86400000) / 1000);
		int frame = (int)Math.round((secondsOfDay / 20));
		aphe.getRssi().put(String.valueOf(frame), rssi);
		aphe.setDataCount(aphe.getRssi().size());
		if( null == aphe.getMinRssi() || rssi < aphe.getMinRssi() )
			aphe.setMinRssi(rssi);
		if( null == aphe.getMaxRssi() || rssi > aphe.getMaxRssi() )
			aphe.setMaxRssi(rssi);
		return aphe;
	}

	/**
	 * Sets the correct RSSI in a placed frame Frames are divided in the seconds
	 * of the day / 20
	 * 
	 * @param aphe
	 *            The referred APHEntry
	 * @param fromDate
	 *            The date to frame
	 * @param rssi
	 *            The RSSI to set into the frame
	 * @return The modified APHEntry
	 */
	@Override
	public APHEntry repeatFramedRSSI(APHEntry aphe, Date fromDate, Date toDate, Integer rssi) {
		Date forDate = new Date(fromDate.getTime());
		while( forDate.before(toDate) || forDate.equals(toDate)) {
			setFramedRSSI(aphe, forDate, rssi);
			forDate = new Date(forDate.getTime() + 20000);
		}
		return aphe;
	}

	/**
	 * Sets the correct RSSI in a placed frame Frames are divided in the seconds
	 * of the day / 20
	 * 
	 * @param aph
	 *            The APHotspot from which the RSSI will be taken from
	 * @return The modified APHEntry
	 */
	@Override
	public APHEntry setFramedRSSI(APHotspot aph) {
		APHEntry aphe = getFromCache(aph);
		return putInCache(setFramedRSSI(aphe, aph.getCreationDateTime(), aph.getSignalDB()));
	}
	
	/**
	 * Sets the correct RSSI in a placed frame Frames are divided in the seconds
	 * of the day / 20
	 * 
	 * @param aph
	 *            The APHotspot from which the RSSI will be taken from
	 * @return The modified APHEntry
	 */
	@Override
	public APHEntry setFramedRSSI(ExternalAPHotspot aph) {
		APHEntry aphe = getFromCache(aph);
		
		return putInCache(repeatFramedRSSI(aphe, aph.getFirstSeen(), aph.getLastSeen(), aph.getSignalDB()));
	}
	
	/**
	 * Pre build the APHEntries cache
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param apdevices
	 * @throws ASException
	 */
	@Override
	public void buildCache(Date fromDate, Date toDate, List<String> apdevices) throws ASException {
		
		cache.clear();
		
		List<APHEntry> list = apheDao.getUsingHostnameAndDates(apdevices, fromDate, toDate, null, true);
		for( APHEntry obj : list ) 
			cache.put(getHash(obj), obj);
		
		cacheBuilt = true;
	}
	
	/**
	 * Converts a timeslot HashMap into a sorted array
	 * 
	 * @param slots
	 *            The timeslot HashMap to convert
	 * @return A sorted array
	 */
	@Override
	public List<Integer> timeslotToList(Map<String, ?> slots) {
		List<Integer> ret = CollectionFactory.createList();
		
		Iterator<String> i = slots.keySet().iterator();
		while( i.hasNext() ) {
			String key = i.next();
			ret.add(Integer.parseInt(key));
		}
		
		Collections.sort(ret);
		return ret;
	}

	/**
	 * Artificiate a group of APHEntries
	 * 
	 * @param apdevices
	 *            Devices to match
	 * @param fromDate
	 *            From which date
	 * @param toDate
	 *            To wich date
	 * @throws ASException
	 */
	@Override
	public void artificiateRSSI(List<String> apdevices, Date fromDate, Date toDate) throws ASException {
		
		// Populates the apdevices list if empty
		if(CollectionUtils.isEmpty(apdevices)) {
			apdevices = CollectionFactory.createList();
			List<APDevice> list = dao.getAll(true);
			for( APDevice obj : list ) {
				apdevices.add(obj.getHostname());
			}
		}

		Date d1 = new Date(fromDate.getTime());
		Date d2 = new Date(d1.getTime() + 86400000);
		while( d1.before(toDate)) {
			for( String hostname : apdevices ) {
				long counter = 0;
				log.log(Level.INFO, "Processing " + hostname + " for date " + d1);
				List<APHEntry> list = apheDao.getUsingHostnameAndDates(Arrays.asList( new String[] {hostname}), d1, d2, null, true);
				for( APHEntry obj : list ) {
					artificiateRSSI(obj);
					apheDao.update(obj);
					counter++;
				}
				log.log(Level.INFO, counter + " elements processed for " + hostname + " for date " + d1);
			}
			d1 = new Date(d2.getTime());
			d2 = new Date(d1.getTime() + 86400000);
		}
	}

	/**
	 * Calculates an artificial RSSI in the spaces between gaps
	 * 
	 * @param obj
	 *            The APHEntry object to analyze
	 * @throws ASException
	 */
	@Override
	public void artificiateRSSI(APHEntry obj) throws ASException {
		
		// Obtains the APDevice definition for reference
		APDevice apd = null;

		try {
			apd = dao.get(obj.getHostname(), true);
		} catch( Exception e ) {
		}

		artificiateRSSI(obj, apd);
	}
	
	/**
	 * Calculates an artificial RSSI in the spaces between gaps
	 * 
	 * @param obj
	 *            The APHEntry object to analyze
	 * @throws ASException
	 */
	@Override
	public void artificiateRSSI(APHEntry obj, APDevice apd) throws ASException {
		
		// Obtains the APDevice definition for reference
		int maxDistance = 0;

		if( null != apd ) {
			maxDistance = (int)(apd.getVisitGapThreshold() != null ? apd.getVisitGapThreshold() * 3 : 30);
		} else {
			maxDistance = 30;
		}

		// Clears all Artificial generated RSSI
		obj.getArtificialRssi().clear();
		List<Integer> elements = timeslotToList(obj.getRssi());
		
		for( int i = 0; i < elements.size() - 1; i++ ) {
			
			int distance = elements.get(i+1) - elements.get(i) - 1;

			// No distance between elements
			if( distance == 0 ) {
				String key = String.valueOf(elements.get(i));
				obj.getArtificialRssi().put(key, obj.getRssi().get(key));
			} 
			
			// Distance between Ranges
			else if( distance < maxDistance ) {
				
				int initial = elements.get(i);
				int initialValue = obj.getRssi().get(String.valueOf(initial));
				int last = elements.get(i+1);
				int lastValue = obj.getRssi().get(String.valueOf(last));
				obj.getArtificialRssi().put(String.valueOf(initial), initialValue);
				obj.getArtificialRssi().put(String.valueOf(last), lastValue);
				
				obj.getArtificialRssi().putAll(interpolate(initial, initialValue, last, lastValue));
				
			}
			
		}
		
		// copies the last key
		if( elements.size() > 0 ) {
			String key = String.valueOf(elements.get(elements.size() -1));
			obj.getArtificialRssi().put(key, obj.getRssi().get(key));
		}
		
		
	}

	/**
	 * Creates a basic Linear Interpolation for the RSSI values between readings
	 * 
	 * @param initial
	 *            Initial timeslot index
	 * @param initialValue
	 *            Initial value
	 * @param last
	 *            Last timeslot index
	 * 	@param lastValue
	 *            Last Value
	 * @return a Map with the interpolated intermediate values
	 */
	public Map<String, Integer> interpolate( int initial, int initialValue, int last, int lastValue ) {
		Map<String, Integer> ret = CollectionFactory.createMap();
		int distance = last - initial - 1;
		
		if( distance > 0 ) {

			// odd distance
			if( distance % 2 != 0 ) {
				
				int ele = (int)(initial + (((distance - 1) / 2) + 1));
				int value = (int)((initialValue + lastValue) / 2);
				ret.put(String.valueOf(ele), value);

				ret.putAll(interpolate(initial, initialValue, ele, value));
				ret.putAll(interpolate(ele, value, last, lastValue));
				
			// even distance
			} else {
				int ele = (int)(initial + (distance / 2));
				int value = (int)((initialValue + lastValue) / 2);
				ret.put(String.valueOf(ele), value);
				ret.put(String.valueOf(ele+1), value);

				ret.putAll(interpolate(initial, initialValue, ele, value));
				ret.putAll(interpolate(ele+1, value, last, lastValue));

			}
		}
		
		return ret;
	}
	
	/**
	 * Generates the APHEntries tables from dump
	 * 
	 * @param baseDir
	 *            The directory where the dump files reside
	 * @param fromDate
	 *            Initial date
	 * @param toDate
	 *            Final date
	 * @param apdevices
	 *            Devices to match
	 * @throws ASException
	 */
	@Override
	public void generateAPHEntriesFromDump(String baseDir, Date fromDate, Date toDate, List<String> apdevices, boolean buildCache) throws ASException {

		// Populates the apdevices list if empty
		if(CollectionUtils.isEmpty(apdevices)) {
			apdevices = CollectionFactory.createList();
			List<APDevice> list = dao.getAll(true);
			for( APDevice obj : list ) {
				apdevices.add(obj.getHostname());
			}
		}

		// Pre build cache
		if( buildCache ) {
			log.log(Level.INFO, "Building Cache");
			buildCache(fromDate, new Date(toDate.getTime() - 86400000), apdevices);
		}
		
		// Gets the input data
		long totals = 0;
		log.log(Level.INFO, "Processing Dump Records");
		dumpHelper = new DumperHelperImpl<APHotspot>(baseDir, APHotspot.class);
		Iterator<String> i = dumpHelper.stringIterator(fromDate, toDate);
		while( i.hasNext() ) {
			String s = i.next();
			JSONObject json = new JSONObject(s);
			if( totals % 1000 == 0 ) 
				log.log(Level.INFO, "Processing for date " + json.getString("creationDateTime"));

			if(!json.getString("mac").startsWith("broad"))
				if( apdevices.contains(json.getString("hostname"))) 
					setFramedRSSI((APHotspot)gson.fromJson(s, APHotspot.class));

			totals++;
		}

		// Write to the database
		log.log(Level.INFO, "Writing Database");
		Iterator<String> x = cache.keySet().iterator();
		while(x.hasNext()) {
			String key = x.next();
			APHEntry aphe = cache.get(key);
			artificiateRSSI(aphe);
			if( aphe.getDataCount() > 0 ) {
				aphe.setKey(apheDao.createKey(aphe));
				try {
					apheDao.createOrUpdate(aphe);
				} catch( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
		
		log.log(Level.INFO, "Process Ended");

	}

	/**
	 * Generates the APHEntries tables from external APH
	 * 
	 * @param fromDate
	 *            Initial date
	 * @param toDate
	 *            Final date
	 * @param apdevices
	 *            Devices to match
	 */
	@Override
	public void generateAPHEntriesFromExternalAPH(Date fromDate, Date toDate, List<String> apdevices, boolean buildCache) throws ASException {

		// Populates the apdevices list if empty
		if(CollectionUtils.isEmpty(apdevices)) {
			apdevices = CollectionFactory.createList();
			List<APDevice> list = dao.getAll(true);
			for( APDevice obj : list ) {
				apdevices.add(obj.getHostname());
			}
		}

		// Pre build cache
		if( buildCache ) {
			log.log(Level.INFO, "Building Cache");
			buildCache(fromDate, new Date(toDate.getTime() - 86400000), apdevices);
		}
		
		// Gets the input data
		long totals = 0;
		log.log(Level.INFO, "Processing External AP Records from " + fromDate + " to " + toDate);
		List<ExternalAPHotspot> list = eaphDao.getUsingHostnameAndDates(null, fromDate, toDate);
		for( ExternalAPHotspot obj : list ) {
			if( totals % 1000 == 0 ) 
				log.log(Level.INFO, "Processing records " + totals + " of " + list.size());
			
			totals++;
			
			if( obj.getLastSeen() == null )
				obj.setLastSeen(new Date(obj.getFirstSeen().getTime() + ONE_HOUR));
			setFramedRSSI(obj);
		}
		
		// Write to the database
		log.log(Level.INFO, "Writing Database with " + cache.keySet().size() + " objects");
		Iterator<String> x = cache.keySet().iterator();
		while(x.hasNext()) {
			String key = x.next();
			APHEntry aphe = cache.get(key);
			artificiateRSSI(aphe);
			if( aphe.getDataCount() > 0 ) {
				aphe.setKey(apheDao.createKey(aphe));
				try {
					apheDao.createOrUpdate(aphe);
				} catch( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
		
		log.log(Level.INFO, "Process Ended");

	}

	/**
	 * @return the cacheBuilt
	 */
	public boolean isCacheBuilt() {
		return cacheBuilt;
	}

	/**
	 * @param cacheBuilt the cacheBuilt to set
	 */
	public void setCacheBuilt(boolean cacheBuilt) {
		this.cacheBuilt = cacheBuilt;
	}

	/**
	 * @return the scanInDevices
	 */
	@Override
	public boolean isScanInDevices() {
		return scanInDevices;
	}

	/**
	 * @param scanInDevices the scanInDevices to set
	 */
	@Override
	public void setScanInDevices(boolean scanInDevices) {
		this.scanInDevices = scanInDevices;
	}

	/**
	 * @return the useCache
	 */
	public boolean isUseCache() {
		return useCache;
	}

	/**
	 * @param useCache the useCache to set
	 */
	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	/**
	 * Converts a String time to a TimeSlot offset
	 * 
	 * @param t
	 *            The string time
	 * @return A number representing the timeslot offset
	 * @throws Exception
	 */
	@Override
	public int stringToOffsetTime(String t) throws Exception {
		String parts[] = t.split(":");
		long val = (Integer.valueOf(parts[0]) * 3600) 
				+ (Integer.valueOf(parts[1]) * 60) 
				+ (Integer.valueOf(parts[2]));
		int ret = (int)(val / 20);
		if( ret > 4320 ) ret = 4320;
		return ret;
	}
	
	/**
	 * Converts a timeslot offset to a String time
	 * 
	 * @param t
	 *            The timeslot offset
	 * @return a Fully formed String representing the time
	 */
	@Override
	public String slotToTime(int t) {
		long val = t * 20;
		int hour = (int) (val / 3600);
		val = val % 3600;
		int min = (int) (val / 60);
		int sec = (int)(val % 60);
		
		StringBuffer sb = new StringBuffer();
		sb.append(df.format(hour))
			.append(":")
			.append(df.format(min))
			.append(":")
			.append(df.format(sec));
		
		return sb.toString();
	}
		
	/**
	 * Converts a timeslot offset to a Date
	 * 
	 * @param date
	 *            The string date
	 * @param t
	 *            The timeslot offset
	 * @return a Fully formed Date
	 * @throws ParseException
	 */
	@Override
	public Date slotToDate(String date, int t) throws ParseException {
		String f = date + " " + slotToTime(t);
		return sdf2.parse(f);
	}
}
