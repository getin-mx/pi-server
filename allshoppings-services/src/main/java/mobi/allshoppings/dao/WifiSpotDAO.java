package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.WifiSpot;

public interface WifiSpotDAO extends GenericDAO<WifiSpot> {

	Key createKey(WifiSpot obj) throws ASException;
	List<WifiSpot> getUsingFloorMapId(String floorMapId) throws ASException;
	String getNextSequence() throws ASException;
}
