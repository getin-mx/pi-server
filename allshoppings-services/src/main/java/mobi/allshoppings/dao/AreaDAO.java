package mobi.allshoppings.dao;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Area;

import com.inodes.datanucleus.model.Key;

public interface AreaDAO extends GenericDAO<Area> {

	Key createKey() throws ASException;

}
