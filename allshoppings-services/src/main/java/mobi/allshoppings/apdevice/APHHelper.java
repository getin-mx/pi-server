package mobi.allshoppings.apdevice;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.model.APHotspot;

public interface APHHelper {

	// Utilities
	String getHash(String hostname, String mac, String date);
	String getHash(String hostname, String mac, Date date);
	String getHash(APHotspot obj);
	String getHash(APHEntry obj);
	APHEntry apHotpostToaphEntry(APHotspot obj);
	APHEntry getFromCache(APHotspot obj);
	APHEntry getFromCache(String hostname, String mac, String date);
	int stringToOffsetTime(String t) throws Exception;
	String slotToTime(int t);
	Date slotToDate(String date, int t) throws ParseException;
	
	// Getters and setter
	boolean isScanInDevices();
	void setScanInDevices(boolean scanInDevices);
	public boolean isUseCache();
	public void setUseCache(boolean useCache);

	// Helper methods
	void buildCache(Date fromDate, Date toDate, List<String> apdevices) throws ASException;
	APHEntry setFramedRSSI(APHEntry aphe, Date forDate, Integer rssi);
	APHEntry setFramedRSSI(APHotspot aph);
	List<Integer> timeslotToList(Map<String, ?> slots);
	void artificiateRSSI(APHEntry obj) throws ASException;
	void artificiateRSSI(List<String> apdevices, Date fromDate, Date toDate) throws ASException;
	void generateAPHEntriesFromDump(String baseDir, Date fromDate, Date toDate, List<String> apdevices, boolean buildCache) throws ASException;

}
