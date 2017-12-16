package mx.getin.dao;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.GenericDAO;
import mobi.allshoppings.exception.ASException;
import mx.getin.model.APDCalibration;

/**
 * Describes a DAO for the model APDCalibration
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 1.0, december 2017
 * @since Mark III, december 2017
 */
public interface APDCalibrationDAO extends GenericDAO<APDCalibration> {

	/**
	 * Build a key for the persistent data
	 * @param identifier - The ID of the persistent data
	 * @return Key - The key for the APDCalibration
	 * @throws ASException - If something goes wrong
	 */
	Key createKey(String identifier) throws ASException;
	
}
