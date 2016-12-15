package mobi.allshoppings.dao;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.SystemStatus;

public interface SystemStatusDAO extends GenericDAO<SystemStatus> {

	Key createKey(String identifier) throws ASException;

}
