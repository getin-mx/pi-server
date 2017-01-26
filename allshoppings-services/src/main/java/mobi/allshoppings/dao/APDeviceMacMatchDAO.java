package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDeviceMacMatch;

public interface APDeviceMacMatchDAO extends GenericDAO<APDeviceMacMatch> {

	Key createKey(APDeviceMacMatch seed) throws ASException;
	List<APDeviceMacMatch> getUsingHostname(String hostname) throws ASException;
	void deleteUsingHostname(String hostname) throws ASException;

}
