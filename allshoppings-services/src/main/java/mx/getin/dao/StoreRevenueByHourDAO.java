package mx.getin.dao;

import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.GenericDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.tools.Range;
import mx.getin.model.StoreRevenueByHour;

/**
 * Describes a DAO for the Store Revenue By Hour model.
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 1.0, january 2018
 * @since Mark III
 */
public interface StoreRevenueByHourDAO extends GenericDAO<StoreRevenueByHour> {

	/**
	 * Creates a DB key for data.
	 * @return Key - the key to use within the DB.
	 * @throws ASException - If something goes wrong
	 */
	Key createKey() throws ASException;
	
	/**
	 * Retrieves Store Revenue By Hour using a Store ID, and Date.
	 * @param storeId - The store whose Tickets By Hour are desired.
	 * @param date - The date to fetch tickets from.
	 * @param fromHour - The initial hour to fetch.
	 * @param toHour - The final hour to fetch (exclusive).
	 * @param range - ???
	 * @param order - The order for the tickets.
	 * @param detachable - ???
	 * @return List&lt;StoreRevenueByHour&gt; - A list with the results of the query.
	 * @throws ASException - If not found or else.
	 */
	List<StoreRevenueByHour> getUsingStoreIdAndDateAndRange(String storeId, String date, String fromHour,
			String toHour, Range range, String order, boolean detachable) throws ASException;
	
	/**
	 * Retrieves Store Revenue By Hour using the belonging Store ID, the date of the sales and an specific
	 * sale hour.
	 * @param storeId - The Store which Revenue By Hour is desired.
	 * @param date - The date to fetch tickets from.
	 * @param hour - The hour to fetch tickets from.
	 * @param detachable - ???
	 * @return List&lt;StoreRevenueByHour&gt; - A list with the results of the query.
	 * @throws ASException - If not found or else.
	 */
	StoreRevenueByHour getUsingStoreIdAndDateAndHour(String storeId, String date, String hour,
			boolean detachable) throws ASException;
	
}//Store Revenue By Hour DAO
