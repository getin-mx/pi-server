package mobi.allshoppings.dao;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Service;

import com.inodes.datanucleus.model.Key;

public interface ServiceDAO extends GenericDAO<Service> {

	Key createKey() throws ASException;

}
