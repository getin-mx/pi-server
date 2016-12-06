package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APUptime;

public interface APUptimeDAO extends GenericDAO<APUptime> {

	Key createKey(String hostname, Date date) throws ASException;
	APUptime getUsingHostnameAndDate(String hostname, Date date) throws ASException;
	List<APUptime> getUsingHostnameAndDates(String hostname, Date fromDate, Date toDate) throws ASException;
}
