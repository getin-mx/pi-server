package mobi.allshoppings.dao;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.MacVendor;

public interface MacVendorDAO extends GenericDAO<MacVendor> {

	Key createKey(String seed) throws ASException;

}
