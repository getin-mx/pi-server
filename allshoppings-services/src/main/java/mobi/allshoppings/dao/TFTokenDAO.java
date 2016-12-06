package mobi.allshoppings.dao;


import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.TFToken;
import mobi.allshoppings.tx.PersistenceProvider;

import com.inodes.datanucleus.model.Key;

public interface TFTokenDAO extends GenericDAO<TFToken> {

	TFToken getLastUsingUser(String userId) throws ASException;
	TFToken getLastUsingUser(PersistenceProvider pp, String userId, boolean detachable) throws ASException;
	
	Key createKey(String tfToken) throws ASException;

}
