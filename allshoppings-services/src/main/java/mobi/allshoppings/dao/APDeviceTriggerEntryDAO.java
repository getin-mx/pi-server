package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDeviceTriggerEntry;

public interface APDeviceTriggerEntryDAO extends GenericDAO<APDeviceTriggerEntry> {

	Key createKey() throws ASException;
	List<APDeviceTriggerEntry> getUsingCoincidence(String hostname, String mac) throws ASException;
	List<APDeviceTriggerEntry> getUsingGenerics(String hostname) throws ASException;

}
