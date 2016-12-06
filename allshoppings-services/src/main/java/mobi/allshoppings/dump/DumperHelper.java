package mobi.allshoppings.dump;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.interfaces.ModelKey;

public interface DumperHelper<T extends ModelKey> {

	/**
	 * Registers a new plugin
	 * 
	 * @param plugin
	 *            The plugin to register
	 */
	void registerPlugin(DumperPlugin<ModelKey> plugin);

	/**
	 * Unregisters a registered plugin
	 * 
	 * @param plugin
	 *            The plugin to unregister
	 */
	void unregisterPlugin(DumperPlugin<ModelKey> plugin);
	
	/**
	 * Applies all the plugins registered for an object before dumping the
	 * object
	 * 
	 * @param obj
	 *            The object to be affected by the plugins
	 * @throws ASException
	 */
	void applyPreDumpPlugins(T obj) throws ASException;
	
	/**
	 * Applies all the plugins registered for an object after dumping the
	 * object
	 * 
	 * @param obj
	 *            The object to be affected by the plugins
	 * @throws ASException
	 */
	void applyPostDumpPlugins(T obj) throws ASException;
	
	/**
	 * Dumps a ModelKey database to a set of files
	 * 
	 * @param fromDate
	 *            From which date
	 * @param toDate
	 *            To which date
	 * @param deleteAfterDump
	 *            Do I have to delete the object after successful dump?
	 * @throws ASException
	 */
	public void dumpModelKey(String collection, Date fromDate, Date toDate, boolean deleteAfterDump, boolean moveCollectionBeforeDump) throws ASException;

	/**
	 * Fakes a Dump batch.
	 * 
	 * @param fromDate
	 *            From which date
	 * @param toDate
	 *            To which date
	 * @throws ASException
	 */
	public void fakeModelKey(Date fromDate, Date toDate) throws ASException;


	/**
	 * Dumps a single ModelKey object
	 * 
	 * @param baseDir
	 *            The base directory to write
	 * @param obj
	 *            DeviceLocationHistory Object to dump
	 * @throws IOException
	 */
	public void dump(T obj) throws IOException;

	/**
	 * Resolves the file name for a dump file based in its date and time
	 * 
	 * @param forDate
	 *            The date and time to calculate the name from
	 * @return A fully formed file name for the dump file
	 */
	public String resolveDumpFileName(Date forDate);

	/**
	 * Gets an iterator with all the saved entities in a date range
	 * 
	 * @param fromDate
	 *            Date and time from which to retrieve entities
	 * @param toDate
	 *            Date and time to which to retrieve entities
	 * @return An iterator with all the selected records
	 */
	public Iterator<T> iterator(Date fromDate, Date toDate);

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
	public Iterator<String> stringIterator(Date fromDate, Date toDate);

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
	public Iterator<JSONObject> jsonIterator(Date fromDate, Date toDate);

	/**
	 * Gets a list with all the saved entities in a date rante
	 * 
	 * @param fromDate
	 *            Date and time from which to retrieve entities
	 * @param toDate
	 *            Date and time to which to retrieve entities
	 * @return A list with all the selected records
	 * @throws IOException
	 */
	public List<T> retrieveModelKeyList(Date fromDate, Date toDate) throws IOException;

}