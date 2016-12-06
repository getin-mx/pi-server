package mobi.allshoppings.dump;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONObject;

import mobi.allshoppings.model.APHotspot;

public interface APDumperHelper {

	/**
	 * Creates a new APDump batch from a Regular dump batch
	 * 
	 * @param baseDir
	 *            The base directory to read / write
	 * @param fromDate
	 *            The date and time from which the dump starts
	 * @param toDate
	 *            The date and time from which the dump ends
	 */
	void splitFromGeneralDump(String baseDir, Date fromDate, Date toDate);
	
	/**
	 * Dumps a single ModelKey object
	 * 
	 * @param baseDir
	 *            The base directory to write
	 * @param obj
	 *            DeviceLocationHistory Object to dump
	 * @throws IOException
	 */
	public void dump(APHotspot obj) throws IOException;

	/**
	 * Resolves the file name for a dump file based in its date and time
	 * 
	 * @param forDate
	 *            The date and time to calculate the name from
	 * @return A fully formed file name for the dump file
	 */
	public String resolveDumpFileName(String hostname, Date forDate);

	/**
	 * Gets an iterator with all the saved entities in a date range
	 * 
	 * @param fromDate
	 *            Date and time from which to retrieve entities
	 * @param toDate
	 *            Date and time to which to retrieve entities
	 * @return An iterator with all the selected records
	 */
	public Iterator<APHotspot> iterator(String hostname, Date fromDate, Date toDate);

	/**
	 * Gets an iterator with all the saved entities in a date range, in its
	 * String representation
	 * 
	 * @param fromDate
	 *            Date and time from which to retrieve entities
	 * @param toDate
	 *            Date and time to which to retrieve entities
	 * @return An iterator with all the selected records
	 */
	public Iterator<String> stringIterator(String hostname, Date fromDate, Date toDate);

	/**
	 * Gets an iterator with all the saved entities in a date range, in its
	 * JSON representation
	 * 
	 * @param fromDate
	 *            Date and time from which to retrieve entities
	 * @param toDate
	 *            Date and time to which to retrieve entities
	 * @return An iterator with all the selected records
	 */
	public Iterator<JSONObject> jsonIterator(String hostname, Date fromDate, Date toDate);

}