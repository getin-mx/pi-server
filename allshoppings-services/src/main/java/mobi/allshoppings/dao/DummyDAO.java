package mobi.allshoppings.dao;


import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Dummy;

import com.inodes.datanucleus.model.Key;

public interface DummyDAO extends GenericDAO<Dummy> {

	Key createKey() throws ASException;

}
