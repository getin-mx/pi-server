package mobi.allshoppings.dao;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Process;

public interface ProcessDAO extends GenericDAO<Process> {

	Key createKey() throws ASException;

}
