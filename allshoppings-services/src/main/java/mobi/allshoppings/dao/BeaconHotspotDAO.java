package mobi.allshoppings.dao;


import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.BeaconHotspot;

public interface BeaconHotspotDAO extends GenericDAO<BeaconHotspot> {

	Key createKey() throws ASException;

}
