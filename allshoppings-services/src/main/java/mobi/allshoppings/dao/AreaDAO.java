package mobi.allshoppings.dao;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Area;

public interface AreaDAO extends GenericDAO<Area> {

	Key createKey() throws ASException;

}
