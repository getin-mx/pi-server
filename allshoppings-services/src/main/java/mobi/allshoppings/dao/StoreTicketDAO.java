package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.StoreTicket;
import mobi.allshoppings.tools.Range;

public interface StoreTicketDAO extends GenericDAO<StoreTicket> {

	Key createKey() throws ASException;
	List<StoreTicket> getUsingStoreIdAndDatesAndRange(String storeId, String fromDate, String toDate, Range range, String order, boolean detachable) throws ASException;
	StoreTicket getUsingStoreIdAndDate(String storeId, String date, boolean detachable) throws ASException;

}
