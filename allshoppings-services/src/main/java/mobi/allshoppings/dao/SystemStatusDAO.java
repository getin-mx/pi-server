package mobi.allshoppings.dao;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.SystemStatus;

import com.inodes.datanucleus.model.Key;

public interface SystemStatusDAO extends GenericDAO<SystemStatus> {

	Key createKey(String identifier) throws ASException;

}
