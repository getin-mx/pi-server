package mobi.allshoppings.apdevice;

import java.util.Date;
import java.util.List;
import java.util.Map;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.MacVendor;

public interface APDeviceHelper {

	void updateDeviceData(String identifier, String description, boolean enableAlerts, List<String> alertMails) throws ASException;
	void reportDownDevices() throws ASException;
	void calculateUptime(Date fromDate, Date toDate, List<String> apdevices) throws ASException;

	// Utilities
	void updateAPDeviceInfo(String identifier) throws ASException;
	void updateAPDeviceStatus(String identifier) throws ASException;
	byte[] getFileFromAPDevice(String identifier, String fileName) throws ASException;
	byte[] getFileFromAPDevice(APDevice apdevice, String fileName) throws ASException;
	int executeCommandOnAPDevice(String identifier, String command, StringBuffer stdout, StringBuffer stderr) throws ASException;
	int executeCommandOnAPDevice(APDevice apdevice, String command, StringBuffer stdout, StringBuffer stderr) throws ASException;
	void restartAPDevice(String identifier) throws ASException;
	void restartAPDevice(APDevice apdevice) throws ASException;
	void tryRestartAPDevices() throws ASException;
	APDevice geoIp(APDevice apd) throws ASException;
	void updateAssignationsUsingAPDevice(String hostname) throws ASException;
	void unassignUsingAPDevice(String hostname) throws ASException;
	
	// Mac Addresses Utilites
	String getDevicePlatform(String mac, Map<String, MacVendor> cache);
	void updateMacVendors(String filename) throws ASException;
	List<MacVendor> macVendorFileParser(String filename, String outfile) throws ASException;
}
