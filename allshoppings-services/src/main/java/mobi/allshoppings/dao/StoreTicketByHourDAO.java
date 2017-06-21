package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.StoreTicketByHour;
import mobi.allshoppings.tools.Range;

public interface StoreTicketByHourDAO extends GenericDAO<StoreTicketByHour> {

	Key createKey() throws ASException;
	List<StoreTicketByHour> getUsingStoreIdAndDateAndRange(String storeId, String date, String fromHour, String toHour, Range range, String order, boolean detachable) throws ASException;
	StoreTicketByHour getUsingStoreIdAndDateAndHour(String storeId, String date, String hour, boolean detachable) throws ASException;
}
