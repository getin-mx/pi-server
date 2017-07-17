package mobi.allshoppings.dump;

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
	void checkIntegrity() throws ASException;
}
