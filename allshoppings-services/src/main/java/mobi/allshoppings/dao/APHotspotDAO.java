package mobi.allshoppings.dao;


import java.util.Date;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APHotspot;

@Deprecated
public interface APHotspotDAO extends GenericDAO<APHotspot> {

	Key createKey() throws ASException;
	long countUsingHostnameAndDates(String hostname, Date fromDate, Date toDate) throws ASException;
	APHotspot getLastUsingHostnameAndMac(String hostname, String mac) throws ASException;
	APHotspot getPreviousUsingHostnameAndMac(String hostname, String mac) throws ASException;
	
}
