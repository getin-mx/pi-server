package mobi.allshoppings.dao;


import java.util.List;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDeviceMacMatch;

import com.inodes.datanucleus.model.Key;

public interface APDeviceMacMatchDAO extends GenericDAO<APDeviceMacMatch> {

	Key createKey() throws ASException;
	List<APDeviceMacMatch> getUsingHostname(String hostname) throws ASException;

}
