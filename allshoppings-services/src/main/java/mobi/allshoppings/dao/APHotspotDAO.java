package mobi.allshoppings.dao;


import java.util.Date;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APHotspot;

import com.inodes.datanucleus.model.Key;

public interface APHotspotDAO extends GenericDAO<APHotspot> {

	Key createKey() throws ASException;
	long countUsingHostnameAndDates(String hostname, Date fromDate, Date toDate) throws ASException;
	APHotspot getLastUsingHostnameAndMac(String hostname, String mac) throws ASException;
	APHotspot getPreviousUsingHostnameAndMac(String hostname, String mac) throws ASException;
	
}
