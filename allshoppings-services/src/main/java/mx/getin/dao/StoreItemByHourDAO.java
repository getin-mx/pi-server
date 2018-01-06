package mx.getin.dao;

import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.GenericDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.tools.Range;
import mx.getin.model.StoreItemByHour;

/**
 * Describes a DAO for the Store Item By Hour model.
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 1.0, january 2018
 * @since Mark III
 */
public interface StoreItemByHourDAO extends GenericDAO<StoreItemByHour> {

	/**
	 * Creates a DB key for data.
	 * @return Key - the key to use within the DB.
	 * @throws ASException - If something goes wrong
	 */
	Key createKey() throws ASException;
	
	/**
	 * Retrieves Store Items By Hour using a Store ID, and Date.
	 * @param storeId - The store whose Items By Hour are desired.
	 * @param date - The date to fetch items from.
	 * @param fromHour - The initial hour to fetch.
	 * @param toHour - The final hour to fetch (exclusive).
	 * @param range - ???
	 * @param order - The order for the items.
	 * @param detachable - ???
	 * @return List&lt;StoreItemByHour&gt; - A list with the results of the query.
	 * @throws ASException - If not found or else.
	 */
	List<StoreItemByHour> getUsingStoreIdAndDateAndRange(String storeId, String date, String fromHour,
			String toHour, Range range, String order, boolean detachable) throws ASException;
	
	/**
	 * Retreives Store Items By Hour using the belonging Store ID, the date of the sales and an specific
	 * sale hour.
	 * @param storeId - The Store whose Items By Hour are desired.
	 * @param date - The date to fetch items from.
	 * @param hour - The hour to fetch items from.
	 * @param detachable - ???
	 * @return List&lt;StoreItemByHour&gt; - A list with the results of the query.
	 * @throws ASException - If not found or else.
	 */
	StoreItemByHour getUsingStoreIdAndDateAndHour(String storeId, String date, String hour,
			boolean detachable) throws ASException;
	
}//Store Item By Hour DAO
