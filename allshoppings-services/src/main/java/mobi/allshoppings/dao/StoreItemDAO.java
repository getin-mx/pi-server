package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.StoreItem;
import mobi.allshoppings.tools.Range;

public interface StoreItemDAO extends GenericDAO<StoreItem> {

	Key createKey() throws ASException;
	List<StoreItem> getUsingStoreIdAndDatesAndRange(String storeId, String fromDate, String toDate, Range range, String order, boolean detachable) throws ASException;
	StoreItem getUsingStoreIdAndDate(String storeId, String date, boolean detachable) throws ASException;
}
