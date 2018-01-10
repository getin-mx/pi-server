package mobi.allshoppings.dump;

import java.util.List;

import mobi.allshoppings.exception.ASException;

public interface CloudFileManager {

	/**
	 * Registers a File as touched for future update to the cloud
	 * 
	 * @param filename
	 *            The used file name
	 * @throws ASException
	 */
	void registerFileForUpdate(String filename) throws ASException;

	/**
	 * Registers a File as needed by the process
	 * 
	 * @param filename
	 *            The needed file name
	 * @throws ASException
	 */
	void registerFileForPrefetch(String filename) throws ASException;
	
	/**
	 * Registers a file as disposable for the next cleanup cycle
	 * 
	 * @param filename
	 *            The file name to dispose
	 */
	void registerFileAsDisposable(String filename);
	
	/**
	 * Starts a pre-fetch cycle
	 * 
	 * @throws ASException
	 */
	void startPrefetch(boolean notFoundExpected) throws ASException;

	/**
	 * Flushes the updated files to the Cloud
	 * 
	 * @throws ASException
	 */
	void flush() throws ASException;

	/**
	 * Triggers a cleanup for the local cached files
	 * 
	 * @throws ASException
	 */
	void forceCleanup() throws ASException;

	/**
	 * Checks local copy integrity
	 * 
	 * @throws ASException
	 */
	void checkAvailableSpace() throws ASException;
	
	/**
	 * Checks a local copy integrity
	 * 
	 * @param fileName
	 *            The file to check
	 * @param wait
	 *            True if the process can be locked while the file is
	 *            transferred, false if not
	 * @return Returns true if the copy is OK, false if not
	 * @throws ASException
	 */
	boolean checkLocalCopyIntegrity(String fileName, boolean wait, boolean notFoundExpected) throws ASException;

	/**
	 * Gets a cloud directory listing
	 * 
	 * @param directory
	 *            The directory to list
	 * @return The directory contents
	 * @throws ASException
	 */
	List<String> getDirectoryListing(String directory) throws ASException;

	/**
	 * Sets the remote bucket name
	 * 
	 * @param bucket
	 *            The selected bucket name
	 */
	void setBucket(String bucket);

	/**
	 * Retrieves the remote bucket name
	 * 
	 * @return The used bucket name
	 */
	String getBucket();

	/**
	 * Finalizes the object
	 */
	void dispose();
}
