package mobi.allshoppings.dao;


import java.util.List;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.FloorMap;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.Range;

import com.inodes.datanucleus.model.Key;

public interface FloorMapDAO extends GenericDAO<FloorMap> {

	Key createKey(FloorMap obj) throws ASException;
	List<FloorMap> getUsingStatusAndShoppingId(Integer status, String shoppingId) throws ASException;
	List<FloorMap> getUsingStatusAndUserAndRange(Integer status, User user, Range range) throws ASException;
	
}
