package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.ExternalAPHotspot;

public interface ExternalAPHotspotDAO extends GenericDAO<ExternalAPHotspot> {

	Key createKey() throws ASException;
	long countUsingHostnameAndDates(String hostname, Date fromDate, Date toDate) throws ASException;
	ExternalAPHotspot getLastUsingHostnameAndMac(String hostname, String mac) throws ASException;
	ExternalAPHotspot getPreviousUsingHostnameAndMac(String hostname, String mac) throws ASException;
	List<ExternalAPHotspot> getUsingHostnameAndDates(String hostname, Date fromDate, Date toDate) throws ASException;
	
}
