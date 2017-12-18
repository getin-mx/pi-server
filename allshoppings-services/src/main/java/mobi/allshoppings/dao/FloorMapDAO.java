package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.FloorMap;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.Range;

public interface FloorMapDAO extends GenericDAO<FloorMap> {

	Key createKey(FloorMap obj) throws ASException;
	List<FloorMap> getUsingStatusAndShoppingId(byte status, String shoppingId) throws ASException;
	List<FloorMap> getUsingStatusAndUserAndRange(byte status, User user, Range range) throws ASException;
	
}
