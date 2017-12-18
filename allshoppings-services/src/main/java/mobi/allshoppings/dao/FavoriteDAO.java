package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Favorite;
import mobi.allshoppings.model.User;

public interface FavoriteDAO extends GenericDAO<Favorite> {

	Key createKey() throws ASException;
	Favorite getUsingUserAndEntityAndKind(User user, String entityId, byte entityKind, boolean detach) throws ASException;
	List<String> getIdsUsingUserAndKind(User user, byte entityKind) throws ASException;
	long getUserFavoriteCount(String userId) throws ASException;
	long getEntityFavoriteCount(String entityId, byte entityKind) throws ASException;
}
