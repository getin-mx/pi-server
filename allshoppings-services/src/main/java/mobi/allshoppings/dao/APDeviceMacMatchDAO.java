package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDeviceMacMatch;

public interface APDeviceMacMatchDAO extends GenericDAO<APDeviceMacMatch> {

	Key createKey() throws ASException;
	List<APDeviceMacMatch> getUsingHostname(String hostname) throws ASException;

}
