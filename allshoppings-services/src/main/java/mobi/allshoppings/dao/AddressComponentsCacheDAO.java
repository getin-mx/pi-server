package mobi.allshoppings.dao;


import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.AddressComponentsCache;

import com.inodes.datanucleus.model.Key;

public interface AddressComponentsCacheDAO extends GenericDAO<AddressComponentsCache> {

	Key createKey(String geohash) throws ASException;

}
