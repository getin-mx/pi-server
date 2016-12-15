package mobi.allshoppings.dao;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Service;

public interface ServiceDAO extends GenericDAO<Service> {

	Key createKey() throws ASException;

}
