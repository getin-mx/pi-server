package mobi.allshoppings.dao;


import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.PushMessageLog;

public interface PushMessageLogDAO extends GenericDAO<PushMessageLog> {

	Key createKey() throws ASException;
	
}
