package mobi.allshoppings.dao;


import java.util.List;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Favorite;
import mobi.allshoppings.model.User;

import com.inodes.datanucleus.model.Key;

public interface FavoriteDAO extends GenericDAO<Favorite> {

	Key createKey() throws ASException;
	Favorite getUsingUserAndEntityAndKind(User user, String entityId, Integer entityKind, boolean detach) throws ASException;
	List<String> getIdsUsingUserAndKind(User user, Integer entityKind) throws ASException;
	long getUserFavoriteCount(String userId) throws ASException;
	long getEntityFavoriteCount(String entityId, Integer entityKind) throws ASException;
}
