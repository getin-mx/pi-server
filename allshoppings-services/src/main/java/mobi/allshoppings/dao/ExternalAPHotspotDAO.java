package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;
import java.util.Map;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.ExternalAPHotspot;

@Deprecated
public interface ExternalAPHotspotDAO extends GenericDAO<ExternalAPHotspot> {

	Key createKey() throws ASException;
	long countUsingHostnameAndDates(String hostname, Date fromDate, Date toDate) throws ASException;
	ExternalAPHotspot getLastUsingHostnameAndMac(String hostname, String mac) throws ASException;
	ExternalAPHotspot getPreviousUsingHostnameAndMac(String hostname, String mac) throws ASException;
	List<ExternalAPHotspot> getUsingHostnameAndDates(String hostname, Date fromDate, Date toDate) throws ASException;
	List<ExternalAPHotspot> getUsingHostnameAndDates(List<String> hostname, Date fromDate, Date toDate) throws ASException;
	List<String> getExternalHostnames() throws ASException;
	Date getLastEntryDate(List<String> hostname) throws ASException;
	void updateLastEntryDate(Map<String, Date> map) throws ASException;
}
