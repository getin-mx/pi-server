package mobi.allshoppings.dao;


import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDevice;

public interface APDeviceDAO extends GenericDAO<APDevice> {

	Key createKey(String identifier) throws ASException;

}
