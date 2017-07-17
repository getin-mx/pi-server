package mobi.allshoppings.dump;

import java.util.Date;
import java.util.List;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.interfaces.ModelKey;

public interface DumperFileNameResolver<T extends ModelKey> {

	/**
	 * Is this plugin available for the received element?
	 * 
	 * @param element
	 *            Element to check
	 * @return True if it is available, false if not
	 */
	boolean isAvailableFor(ModelKey element);

	/**
	 * Returns if the kind of element may have multiple files if a name
	 * discriminator was not set
	 * 
	 * @return True if if may have multiple files, false if not
	 */
	boolean mayHaveMultiple();
	
	/**
	 * Returns a list of optional files
	 * 
	 * @param baseDir
	 *            The base directory
	 * @param baseName
	 *            Base name for the class
	 * @param forDate
	 *            Date to scan
	 * @param cfm
	 *            A cloud File Manager to search online files
	 * @return A formed list of filename options
	 */
	List<String> getMultipleFileOptions(String baseDir, String baseName, Date forDate, CloudFileManager cfm ) throws ASException;
	
	/**
	 * Resolves a file name
	 * 
	 * @param baseDir
	 *            The base dump directory
	 * @param baseName
	 *            The base object name
	 * @param forDate
	 *            The discriminator date
	 * @param element
	 *            The particular element to use as discriminator
	 * @param filter
	 *            A particular filter content
	 * @return The fully qualified file name for this object
	 */
	String resolveDumpFileName(String baseDir, String baseName, Date forDate, ModelKey element, String filter);
}