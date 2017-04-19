package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.StoreRevenue;
import mobi.allshoppings.tools.Range;

public interface StoreRevenueDAO extends GenericDAO<StoreRevenue> {

	Key createKey() throws ASException;
	List<StoreRevenue> getUsingStoreIdAndDatesAndRange(String storeId, String fromDate, String toDate, Range range, String order, boolean detachable) throws ASException;
	StoreRevenue getUsingStoreIdAndDate(String storeId, String date, boolean detachable) throws ASException;
}
