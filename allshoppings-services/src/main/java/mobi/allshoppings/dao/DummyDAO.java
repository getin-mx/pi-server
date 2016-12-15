package mobi.allshoppings.dao;


import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Dummy;

public interface DummyDAO extends GenericDAO<Dummy> {

	Key createKey() throws ASException;

}
