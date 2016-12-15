package mobi.allshoppings.dao;


import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Friend;

public interface FriendDAO extends GenericDAO<Friend> {

	Key createKey(Friend obj) throws ASException;
	long getUserFriendCount(String userId) throws ASException;
	long getUserFriendCountUsingStatus(String userId, Integer status) throws ASException;
}
