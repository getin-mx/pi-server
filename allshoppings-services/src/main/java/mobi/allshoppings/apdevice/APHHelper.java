package mobi.allshoppings.apdevice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.model.ExternalAPHotspot;

public interface APHHelper {

	// Utilities
	boolean isValidMacAddress(String mac);
	String getHash(String hostname, String mac, String date);
	String getHash(String hostname, String mac, Date date);
	String getHash(APHotspot obj);
	String getHash(APHEntry obj);
	String getHash(ExternalAPHotspot obj);
	APHEntry apHotpostToaphEntry(APHotspot obj);
	APHEntry apHotpostToaphEntry(ExternalAPHotspot obj);
	APHEntry getFromCache(APHotspot obj);
	APHEntry getFromCache(String hostname, String mac, String date) throws NoSuchAlgorithmException, FileNotFoundException, IOException;
	int stringToOffsetTime(String t) throws Exception;
	String slotToTime(int t);
	Date slotToDate(String date, int t) throws ParseException;
	
	// Getters and setter
	boolean isScanInDevices();
	void setScanInDevices(boolean scanInDevices);
	public boolean isUseCache();
	public void setUseCache(boolean useCache);

	// Helper methods
	void buildCache(Date fromDate, Date toDate, Map<String, APDevice> apdevices) throws ASException;
	APHEntry repeatFramedRSSI(APHEntry aphe, Date fromDate, Date toDate, Integer rssi);
	APHEntry setFramedRSSI(APHEntry aphe, Date forDate, Integer rssi);
	APHEntry setFramedRSSI(JSONObject aph);
	APHEntry setFramedRSSI(APHotspot aph);
	APHEntry setFramedRSSI(ExternalAPHotspot aph);
	List<Integer> timeslotToList(Map<String, ?> slots);
	void artificiateRSSI(APHEntry obj, APDevice apd) throws ASException;
	void artificiateRSSI(Map<String, APDevice> apdevices, Date fromDate, Date toDate) throws ASException;
	void generateAPHEntriesFromDump(String baseDir, Date fromDate, Date toDate, Map<String, APDevice> apdevices, boolean buildCache) throws ASException;
	void generateAPHEntriesFromExternalAPH(Date fromDate, Date toDate, Map<String, APDevice> apdevices, boolean buildCache) throws ASException;

}
