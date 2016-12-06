package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APHEntry;

public interface APHEntryDAO extends GenericDAO<APHEntry> {

	Key createKey(APHEntry obj) throws ASException;
	List<APHEntry> getUsingHostnameAndDates(List<String> hostname, Date fromDate, Date toDate, boolean detachable) throws ASException;
	List<String> getMacsUsingHostnameAndDates(List<String> hostname, Date fromDate, Date toDate) throws ASException;
}
