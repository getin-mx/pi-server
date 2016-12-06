package mobi.allshoppings.dao;


import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.PushMessageLog;

import com.inodes.datanucleus.model.Key;

public interface PushMessageLogDAO extends GenericDAO<PushMessageLog> {

	Key createKey() throws ASException;
	
}
