package mobi.allshoppings.dao;

import java.util.Date;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserEntityCache;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.tools.Range;

public interface UserEntityCacheDAO extends GenericDAO<UserEntityCache> {
	
	UserEntityCache getUsingKindAndFavorite(User user, byte entityKind, int returnType, boolean forceCheck) throws ASException;
	UserEntityCache getUsingKindAndViewLocation(ViewLocation vl, byte entityKind, int returnType, boolean forceCheck) throws ASException;
	UserEntityCache getUsingKindAndListName(String name, byte entityKind, boolean forceCheck) throws ASException;
	Key createKey(ViewLocation vl, byte entityKind, int returnType) throws ASException;
	Key createKey(String userId, byte entityKind, int returnType) throws ASException;
	boolean needsUpdate(UserEntityCache uec, User user) throws ASException;
	boolean needsUpdate(UserEntityCache uec, ViewLocation vl) throws ASException;
	boolean needsUpdate(UserEntityCache uec, byte entityKind) throws ASException;
	int needsUpdate(User user) throws Exception;
	List<UserEntityCache> getEvicted(Date limit, Range range) throws ASException;
	long countEvicted(Date limit) throws ASException;
}
