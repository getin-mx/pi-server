package mobi.allshoppings.dao;


import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.AddressComponentsCache;

public interface AddressComponentsCacheDAO extends GenericDAO<AddressComponentsCache> {

	Key createKey(String geohash) throws ASException;

}
