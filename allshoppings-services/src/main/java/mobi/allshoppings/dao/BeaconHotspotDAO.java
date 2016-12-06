package mobi.allshoppings.dao;


import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.BeaconHotspot;

import com.inodes.datanucleus.model.Key;

public interface BeaconHotspotDAO extends GenericDAO<BeaconHotspot> {

	Key createKey() throws ASException;

}
