package mobi.allshoppings.dao;


import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDevice;

import com.inodes.datanucleus.model.Key;

public interface APDeviceDAO extends GenericDAO<APDevice> {

	Key createKey(String identifier) throws ASException;

}
