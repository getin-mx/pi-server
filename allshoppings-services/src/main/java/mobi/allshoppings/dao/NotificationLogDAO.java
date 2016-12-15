package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.NotificationLog;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.Range;

public interface NotificationLogDAO extends GenericDAO<NotificationLog> {

	Key createKey() throws ASException;
	NotificationLog getLastNotificationFor(User user, String entityId, Integer entityKind) throws ASException;
	List<NotificationLog> getUsingStatusAndUserAndRange(List<Integer> status, User user, Range range, String order) throws ASException;
	
}
