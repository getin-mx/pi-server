package mobi.allshoppings.dump;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.interfaces.ModelKey;

public interface DumperPlugin<T extends ModelKey> {

	/**
	 * Is this plugin available for the received element?
	 * 
	 * @param element
	 *            Element to check
	 * @return True if it is available, false if not
	 */
	boolean isAvailableFor(ModelKey element);
		
	/**
	 * Pre dump process
	 * 
	 * @param element
	 *            The element to process
	 * @throws ASException
	 */
	void preDump(T element) throws ASException;

	/**
	 * Post dump process
	 * 
	 * @param element
	 *            The element to process
	 * @throws ASException
	 */
	void postDump(T element) throws ASException;

	/**
	 * Conversion process
	 * 
	 * @param element
	 *            The element to process
	 * @param jsonRep
	 *            The previous JSON Representation
	 * @return A formatted JSON Representation of the object
	 * @throws ASException
	 */
	String toJson(T element, String jsonRep) throws ASException;
}
