package mobi.allshoppings.dump;

import java.util.Date;

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
	 * @return The fully qualified file name for this object
	 */
	String resolveDumpFileName(String baseDir, String baseName, Date forDate, ModelKey element);
}