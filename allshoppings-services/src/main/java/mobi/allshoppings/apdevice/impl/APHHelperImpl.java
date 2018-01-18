package mobi.allshoppings.apdevice.impl;

import static mx.getin.Constants.DAY_IN_MILLIS;
import static mx.getin.Constants.MINUTE_TO_TWENTY_SECONDS_SLOT;
import static mx.getin.Constants.SLOT_NUMBER_IN_DAY;
import static mx.getin.Constants.sdf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.apdevice.APHHelper;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.model.ExternalAPHotspot;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.PersistentCacheFSImpl;
import mx.getin.model.APDCalibration;

public class APHHelperImpl implements APHHelper {

	private static Calendar CALENDAR = Calendar.getInstance();
	
	private static final Logger log = Logger.getLogger(APHHelperImpl.class.getName());
	private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	
	private static final Pattern VALID_MAC_ADDRESS_REGEX =
			Pattern.compile("^([0-9A-Fa-f]{2}[\\.:-]){5}([0-9A-Fa-f]{2})$");
	
	private PersistentCacheFSImpl<APHEntry> cache;

	@Autowired
	private SystemConfiguration systemConfiguration;
	
	private boolean cacheBuilt = false;
	private boolean scanInDevices = true;
	private boolean useCache = true;
	private TimeZone gmt;
	protected KeyHelper keyHelper = new KeyHelperGaeImpl();
	

	private final Map<String, Integer> AUXILIAR_INTERPOLATION_MAP =
			CollectionFactory.createMap();

	/**
	 * Standard Constructor
	 */
	public APHHelperImpl() {
		gmt = TimeZone.getTimeZone("GMT");
		sdf.setTimeZone(gmt);
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
		return mac + ":" + hostname + ":" + date;
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
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws NoSuchAlgorithmException 
	 */
	public APHEntry getFromCache(String hostname, String mac, String date) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
		String hash = getHash(hostname, mac, date);
		if( cache == null ) useCache = false;
		APHEntry ret = useCache ? cache.get(hash) : null;
		if( null == ret ) {
			DumperHelper<APHEntry> dumper = new DumpFactory<APHEntry>().build(null, APHEntry.class);
			try {
				if(cacheBuilt == true && useCache) throw ASExceptionHelper.notFoundException();
				dumper.setFilter(hostname);
				Date dest = sdf.parse(date);
				CALENDAR.setTime(dest);
				CALENDAR.add(Calendar.DATE, 1);
				CALENDAR.add(Calendar.MILLISECOND, -1);
				return dumper.iterator(dest, CALENDAR.getTime()).next();
			} catch( ASException e ) {
				ret = new APHEntry(hostname, mac, date);
				ret.setKey(keyHelper.createStringUniqueKey(APHEntry.class));
			} catch(ParseException e) {
				log.log(Level.SEVERE, "Cannot parse date " +date, e);
			} finally {
				dumper.dispose();
			}
		}
		return ret;
	}
	
	/*
	public APHEntry getFromCacheDumper(String hostname, String mac, String date) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
		String hash = getHash(hostname, mac, date);
		if( cache == null ) useCache = false;
		APHEntry ret = useCache ? cache.get(hash) : null;
		if( null == ret ) {
			try {
				if( cacheBuilt == true && useCache ) 
					throw ASExceptionHelper.notFoundException();
				
				ret = apheDao.get(hash, true);
			} catch( ASException e ) {
				ret = new APHEntry(hostname, mac, date);
				try {
					String hashKey = ret.getMac() + ":" + ret.getHostname() + ":" + ret.getDate();
					ret.setKey((Key) keyHelper.obtainKey(APHEntry.class, hashKey));
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

	/*
	 * Obtains an APHEntry representation from cache
	 * @param obj
	 * @return
	 *
	public APHEntry getFromCache(APHotspot obj) {
		String hash = getHash(obj);
		if( cache == null ) useCache = false;
		APHEntry ret = null;
		if( useCache ) {
			try {
				ret = cache.get(hash);
			} catch( Exception e ) {}
		}
		if( null == ret ) {
			try {
				if( cacheBuilt == true && useCache ) 
					throw ASExceptionHelper.notFoundException();
				
				ret = apheDao.get(hash, true);
			} catch( ASException e ) {
				ret = apHotpostToaphEntry(obj);
				try {
					ret.setKey(apheDao.createKey(ret));
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

	/*
	 * Obtains an APHEntry representation from cache
	 * @param obj
	 * @return
	 *
	public APHEntry getFromCache(ExternalAPHotspot obj) {
		String hash = getHash(obj);
		if( cache == null ) useCache = false;
		APHEntry ret = null;
		if( useCache ) {
			try {
				ret = cache.get(hash);
			} catch( Exception e ) {
				log.log(Level.WARNING, e.getMessage(), e);
			}
		}
		if( null == ret ) {
			try {
				if( cacheBuilt == true && useCache ) 
					throw ASExceptionHelper.notFoundException();
				
				ret = apheDao.get(hash, true);
			} catch( ASException e ) {
				ret = apHotpostToaphEntry(obj);
				try {
					ret.setKey(apheDao.createKey(ret));
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
	}*/

	/**
	 * Inserts an APHEntry representation in the cache
	 * @param obj
	 * @return
	 */
	public APHEntry putInCache(APHEntry obj) {
		if( useCache ) {
			String hash = getHash(obj);
			try {
				cache.put( hash, obj );
			} catch( Exception e ) {
				log.log(Level.WARNING, e.getMessage(), e);
			}
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
		CALENDAR.clear();
		CALENDAR.setTime(forDate);
		CALENDAR.setTimeZone(gmt);
		int secondsOfDay = CALENDAR.get(Calendar.SECOND)
				+CALENDAR.get(Calendar.MINUTE) *60
				+CALENDAR.get(Calendar.HOUR_OF_DAY) *60 *60;
		int frame = secondsOfDay /20;
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
	public APHEntry setFramedRSSI(JSONObject aph) {
		try {
			CALENDAR.clear();
			CALENDAR.setTimeInMillis(aph.getLong("creationDateTime"));
			String date = sdf.format(CALENDAR.getTimeInMillis());
			APHEntry aphe = getFromCache(aph.getString("hostname"),
					aph.getString("mac"), date);
			return putInCache(setFramedRSSI(aphe, CALENDAR.getTime(),
					aph.getInt("signalDB")));
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
	
	@Override
	public APHEntry setFramedRSSI(JSONObject aph, Date forceDate) {
		try {
			CALENDAR.clear();
			CALENDAR.setTime(forceDate);
			Calendar mins = Calendar.getInstance();
			mins.setTimeInMillis(aph.getLong("creationDateTime"));//FIXME doesn't work
			CALENDAR.add(Calendar.SECOND, mins.get(Calendar.HOUR) *60 *60 +mins.get(Calendar.MINUTE) *60
					+mins.get(Calendar.SECOND));
			String date = sdf.format(CALENDAR.getTimeInMillis());
			APHEntry aphe = getFromCache(aph.getString("hostname"),
					aph.getString("mac"), date);
			return putInCache(setFramedRSSI(aphe, CALENDAR.getTime(),
					aph.getInt("signalDB")));
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	/*
	 * Sets the correct RSSI in a placed frame Frames are divided in the seconds
	 * of the day / 20
	 * 
	 * @param aph
	 *            The APHotspot from which the RSSI will be taken from
	 * @return The modified APHEntry
	 *
	@Override
	public APHEntry setFramedRSSI(APHotspot aph) {
		APHEntry aphe = getFromCache(aph);
		return putInCache(setFramedRSSI(aphe, aph.getCreationDateTime(), aph.getSignalDB()));
	}*/

	/*
	 * Sets the correct RSSI in a placed frame Frames are divided in the seconds
	 * of the day / 20
	 * 
	 * @param aph
	 *            The APHotspot from which the RSSI will be taken from
	 * @return The modified APHEntry
	 *
	@Override
	public APHEntry setFramedRSSI(ExternalAPHotspot aph) {
		APHEntry aphe = getFromCache(aph);
		
		return putInCache(repeatFramedRSSI(aphe, aph.getFirstSeen(), aph.getLastSeen(), aph.getSignalDB()));
	}*/
	
	/**
	 * Pre build the APHEntries cache
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param apdevices
	 * @throws ASException
	 */
	@Override
	public void buildCache(Date fromDate, Date toDate, List<String> hostnames) throws ASException {
		cache.clear();

		if( hostnames.size() == 1 ) {
			for( String key : hostnames ) { 
				DumperHelper<APHEntry> apheDumper = new DumpFactory<APHEntry>().build(null, APHEntry.class);
				apheDumper.setFilter(key);
				Iterator<APHEntry> i = apheDumper.iterator(fromDate, toDate);
				while(i.hasNext()) {
					APHEntry obj = i.next();
					try {
						cache.put(getHash(obj), obj);
					} catch( Exception e ) {
						log.log(Level.WARNING, e.getMessage(), e);
					}
				}
				apheDumper.dispose();
			}
		} else {
			DumperHelper<APHEntry> apheDumper = new DumpFactory<APHEntry>().build(null, APHEntry.class);
			Iterator<APHEntry> i = apheDumper.iterator(fromDate, toDate);
			while(i.hasNext()) {
				APHEntry obj = i.next();
				try {
					cache.put(getHash(obj), obj);
				} catch( Exception e ) {
					log.log(Level.WARNING, e.getMessage(), e);
				}
			}
			apheDumper.dispose();
		}

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

	/*
	 * Artificiate a group of APHEntries
	 * 
	 * @param apdevices
	 *            Devices to match
	 * @param fromDate
	 *            From which date
	 * @param toDate
	 *            To wich date
	 * @throws ASException
	 *
	@Override
	public void artificiateRSSI(Map<String, APDevice> apdevices, Date fromDate, Date toDate) throws ASException {
		
		Date d1 = new Date(fromDate.getTime());
		Date d2 = new Date(d1.getTime() + DAY_IN_MILLIS);
		while( d1.before(toDate)) {
			Iterator<String> i = apdevices.keySet().iterator();
			while(i.hasNext()) {
				String hostname = i.next();
				long counter = 0;
				log.log(Level.INFO, "Processing " + hostname + " for date " + d1);
				List<APHEntry> list = apheDao.getUsingHostnameAndDates(Arrays.asList( new String[] {hostname}), d1, d2, null, true);
				for( APHEntry obj : list ) {
					artificiateRSSI(obj, apdevices.get(hostname));
					apheDao.update(obj);
					counter++;
				}
				log.log(Level.INFO, counter + " elements processed for " + hostname + " for date " + d1);
			}
			d1 = new Date(d2.getTime());
			d2 = new Date(d1.getTime() + 86400000);
		}
	}*/

	/**
	 * Calculates an artificial RSSI in the spaces between gaps
	 * 
	 * @param obj
	 *            The APHEntry object to analyze
	 * @param apd - The Device asociated to the APHEntry
	 * @return List - The time slots for all reported and artificial RSSI
	 * @throws ASException
	 */
	@Override
	public List<Integer> artificiateRSSI(APHEntry obj, APDCalibration apd) throws ASException {
		// Obtains the APDevice definition for reference
		int maxDistance = 30 *MINUTE_TO_TWENTY_SECONDS_SLOT;

		if( null != apd && apd.getVisitGapThreshold() > 0)
			maxDistance = apd.getVisitGapThreshold() *MINUTE_TO_TWENTY_SECONDS_SLOT;

		// Clears all Artificial generated RSSI
		obj.getArtificialRssi().clear();
		List<Integer> resSlots = timeslotToList(obj.getRssi());
		
		for( int i = 0; i < resSlots.size() - 1; i++ ) {
			int distance = resSlots.get(i+1) - resSlots.get(i) - 1;
			if( distance == 0 ) {
				// No distance between elements
				String key = String.valueOf(resSlots.get(i));
				obj.getArtificialRssi().put(key, obj.getRssi().get(key));
			} else if( distance < maxDistance ) {
				// Distance between Ranges
				int initial = resSlots.get(i);
				int initialValue = obj.getRssi().get(String.valueOf(initial));
				int last = resSlots.get(i+1);
				int lastValue = obj.getRssi().get(String.valueOf(last));
				obj.getArtificialRssi().put(String.valueOf(initial), initialValue);
				obj.getArtificialRssi().put(String.valueOf(last), lastValue);
				AUXILIAR_INTERPOLATION_MAP.clear();
				interpolate(initial, initialValue, last, lastValue);
				obj.getArtificialRssi().putAll(AUXILIAR_INTERPOLATION_MAP);
			}
		}
		
		// copies the last key
		if( resSlots.size() > 0 ) {
			String key = String.valueOf(resSlots.get(resSlots.size() -1));
			obj.getArtificialRssi().put(key, obj.getRssi().get(key));
		}
		
		return resSlots;
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
	public void interpolate(int initial, int initialValue, int last, int lastValue ) {
		int distance = last - initial - 1;
		if(distance <= 0) return;
		if( distance % 2 != 0 ) {
			// odd distance
			int ele = (int)(initial + (((distance - 1) / 2) + 1));
			int value = (int)((initialValue + lastValue) / 2);
			AUXILIAR_INTERPOLATION_MAP.put(String.valueOf(ele), value);
			interpolate(initial, initialValue, ele, value);
			interpolate(ele, value, last, lastValue);
		} else {
			// even distance
			int ele = (int)(initial + (distance / 2));
			int value = (int)((initialValue + lastValue) / 2);
			AUXILIAR_INTERPOLATION_MAP.put(String.valueOf(ele), value);
			AUXILIAR_INTERPOLATION_MAP.put(String.valueOf(ele+1), value);

			interpolate(initial, initialValue, ele, value);
			interpolate(ele+1, value, last, lastValue);

		}
	}
	
	@Override
	public boolean isValidMacAddress(String mac) {

		Matcher m = VALID_MAC_ADDRESS_REGEX.matcher(mac);
		return m.find();
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
	public void generateAPHEntriesFromDump(Date fromDate, Date toDate,
			List<String> hostnames, boolean buildCache,
			Map<String, List<APHEntry>> prevDayMem, boolean lastDate, Date forceDate)
					throws ASException {

		DumperHelper<APHotspot> dumpHelper;

		if( cache == null )
			cache = new PersistentCacheFSImpl<APHEntry>(APHEntry.class,
					systemConfiguration.getCacheMaxInMemElements(),
					systemConfiguration.getCachePageSize(),
					systemConfiguration.getCacheTempDir());
		cache.clear();

		// Pre build cache
		if( buildCache ) {
			log.log(Level.INFO, "Building Cache");
			buildCache(fromDate, new Date(toDate.getTime() - DAY_IN_MILLIS), null);
		} else {
			setCacheBuilt(true);
		}

		// Gets the input data
		long totals = 0;
		log.log(Level.INFO, "Processing Dump Records");
		dumpHelper = new DumpFactory<APHotspot>().build(null, APHotspot.class);

		List<String> options;
		if( hostnames == null || hostnames.size() == 0 ) {
			options = dumpHelper.getMultipleNameOptions(fromDate);
		} else if( hostnames.size() == 1 ) {
			options = CollectionFactory.createList();
			options.add(hostnames.get(0));
		} else {
			options = CollectionFactory.createList();
			options.addAll(hostnames);
		}

		DumperHelper<APHEntry> apheDumper = new DumpFactory<APHEntry>().build(
				null, APHEntry.class); 
		
		for( String hostname : options ) {

			log.log(Level.INFO, "Processing " + hostname + " for date " + fromDate
					+ "...");

			dumpHelper = new DumpFactory<APHotspot>().build(null, APHotspot.class);
			dumpHelper.setFilter(hostname);
			Date xdate = new Date(toDate.getTime() -1);
			Iterator<String> i = dumpHelper.stringIterator(fromDate, xdate);
			JSONObject json;
			while( i.hasNext() ) {
				json = new JSONObject(i.next());
				totals++;
				if(isValidMacAddress(json.getString("mac"))) {
					if(forceDate != null) setFramedRSSI(json, forceDate);
					else setFramedRSSI(json);
				} if( totals % 10000 == 0 ) log.log(Level.INFO, "Processing for date "
						+ new Date(json.getLong("creationDateTime")) + " with "
								+ cache.size() + " records so far (" + cache.getHits()
								+ "/" + cache.getMisses() + "/" + cache.getStores() 
								+ "/" + cache.getLoads() + ")...");
			}

			log.log(Level.INFO, "Disposing APHotspot dumper");
			dumpHelper.dispose();

			// Write to the database, only if it was not written yet!
			log.log(Level.INFO, "Writing Database with " + cache.size() + " objects");
			Iterator<APHEntry> x = cache.iterator();
			while(x.hasNext()) {
				APHEntry aphe = x.next();
				aphe.setKey(keyHelper.createStringUniqueKey(APHEntry.class));
				apheDumper.dump(aphe);
			}
			
			log.log(Level.INFO, "Disposing cache");
			cache.dispose();
			
			apheDumper.flush();
		}
		
		log.log(Level.INFO, "Disposing APHE dumper");
		apheDumper.dispose();

		log.log(Level.INFO, "Process Ended");

	}

	/*
	 * Generates the APHEntries tables from external APH
	 * 
	 * @param fromDate
	 *            Initial date
	 * @param toDate
	 *            Final date
	 * @param apdevices
	 *            Devices to match
	 *
	@Override
	public void generateAPHEntriesFromExternalAPH(Date fromDate, Date toDate, List<String> hostnames, boolean buildCache) throws ASException {

		DumperHelper<ExternalAPHotspot> dumpHelper;

		if( cache == null )
			cache = new PersistentCacheFSImpl<APHEntry>(APHEntry.class, systemConfiguration.getCacheMaxInMemElements(),
					systemConfiguration.getCachePageSize(), systemConfiguration.getCacheTempDir());

		// Pre build cache
		if( buildCache ) {
			log.log(Level.INFO, "Building Cache");
			buildCache(fromDate, new Date(toDate.getTime() - DAY_IN_MILLIS), null);
		} else {
			setCacheBuilt(true);
		}
		
		// Gets the input data
		long totals = 0;
		log.log(Level.INFO, "Processing Dump Records");
		dumpHelper = new DumpFactory<ExternalAPHotspot>().build(null, ExternalAPHotspot.class);

		List<String> options;
		if( hostnames == null || hostnames.size() == 0 ) {
			options = dumpHelper.getMultipleNameOptions(fromDate);
		} else if( hostnames.size() == 1 ) {
			options = CollectionFactory.createList();
			options.add(hostnames.get(0));
		} else {
			options = CollectionFactory.createList();
			options.addAll(hostnames);
		}

		DumperHelper<APHEntry> apheDumper = new DumpFactory<APHEntry>().build(null, APHEntry.class); 

		for( String hostname : options ) {

			log.log(Level.INFO, "Processing " + hostname + " for date " + fromDate
					+ "...");

			dumpHelper = new DumpFactory<ExternalAPHotspot>().build(null, ExternalAPHotspot.class);
			dumpHelper.setFilter(hostname);
			Date xdate = new Date(toDate.getTime() - HOUR_IN_MILLIS);
			Iterator<ExternalAPHotspot> i = dumpHelper.iterator(fromDate, xdate);
			while( i.hasNext() ) {
				ExternalAPHotspot obj = i.next();
				if( totals % 1000 == 0 ) 
					log.log(Level.INFO, "Processing for date " + obj.getCreationDateTime() + " with " + cache.size() + " records so far (" + cache.getHits() + "/" + cache.getMisses() + "/" + cache.getStores() + "/" + cache.getLoads() + ")...");

				if( obj.getLastSeen() == null )
					obj.setLastSeen(new Date(obj.getFirstSeen().getTime() + HOUR_IN_MILLIS));

				if(isValidMacAddress(obj.getMac()))
					setFramedRSSI(obj);

				totals++;
			}

			log.log(Level.INFO, "Disposing ExternalAPHotspot dumper");
			dumpHelper.dispose();

			// Write to the database, only if it was not written yet!
			log.log(Level.INFO, "Writing Database with " + cache.size() + " objects");
			Iterator<APHEntry> x = cache.iterator();
			while(x.hasNext()) {
				APHEntry aphe = x.next();
				//if( aphe.getDataCount() > 2 ) {
					aphe.setKey(apheDao.createKey(aphe));
					apheDumper.dump(aphe);
				//}
			}
			log.log(Level.INFO, "Disposing cache");
			cache.dispose();

			apheDumper.flush();
		}
		
		log.log(Level.INFO, "Disposing APHE dumper");
		apheDumper.dispose();

		log.log(Level.INFO, "Process Ended");

	}*/

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
	 * Converts a timeslot offset to seconds offset
	 * 
	 * @param t
	 *            The timeslot offset
	 * @return The seconds offset
	 */
	@Override
	public int slotToSeconds(int t) {
		return (t * 20);
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
	public Date slotToDate(APHEntry source, int t, TimeZone tz)
			throws ParseException {
		CALENDAR.clear();
		CALENDAR.setTimeZone(gmt);
		sdf2.setTimeZone(tz);
		CALENDAR.setTime(sdf2.parse(source.getDate()));
		if(t < 0) {
			t += SLOT_NUMBER_IN_DAY;
			CALENDAR.add(Calendar.DATE, -1);
			source.setShiftDay(APHEntry.PREVIOUS);
		} else if(t >= SLOT_NUMBER_IN_DAY) {
			t -= SLOT_NUMBER_IN_DAY;
			CALENDAR.add(Calendar.DATE, 1);
			source.setShiftDay(APHEntry.NEXT);
		} else source.setShiftDay(APHEntry.NO_SHIFT);
		CALENDAR.add(Calendar.SECOND, slotToSeconds(t));
		return CALENDAR.getTime();
	}

	@Override
	public void generateAPDVAnalysis(Date forDate, String entityId, Integer entityKind) throws ASException {
		// TODO Auto-generated method stub
		
	}
}
