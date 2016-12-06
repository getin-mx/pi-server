package mobi.allshoppings.dao;


import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDeviceSignal;

public interface APDeviceSignalDAO extends GenericDAO<APDeviceSignal> {

	Key createKey() throws ASException;

}
