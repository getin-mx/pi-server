package mx.getin.dao;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.GenericDAO;
import mobi.allshoppings.exception.ASException;
import mx.getin.model.APDReport;

/**
 * Describes a DAO for the model APDReport
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 1.0, december 2017
 * @since Mark III, december 2017
 */
public interface APDReportDAO extends GenericDAO<APDReport> {

	/**
	 * Builds a key for the persistent data
	 * @param identifier - The Id of the APDReport
	 * @return Key - The key for the APDReport
	 * @throws ASException - If something goes wrong
	 */
	Key createKey(String identifier) throws ASException;
	
}
