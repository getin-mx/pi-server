package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;
import java.util.Map;

import com.inodes.datanucleus.model.Email;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public interface UserDAO extends GenericDAO<User> {
	
	User getByAuthToken(String token) throws ASException;
	User getByEmail(Email email) throws ASException;
	User getByFacebookUserId(String facebookId) throws ASException;
	User getByFacebookUserIdOrEmail(String facebookId, Email email) throws ASException;
	List<User> getActiveUsers(Range range) throws ASException;
	long countActiveUsers() throws ASException;
	User getByEmail(String email) throws ASException;
	User getByRecoveryToken(String token) throws ASException;
	List<User> getByEmail(List<String> emails) throws ASException;
	List<User> getByFullName(List<String> fullnames) throws ASException;
	List<User> getByType(int type, Range range, String order) throws ASException;
	List<User> getMessageEnabledUsingStatusAndRange(List<Integer> status, Range range) throws ASException;
	long countMessageEnabledUsingStatus(List<Integer> status) throws ASException;
	List<User> getTopFifteenRanking(User me, boolean global, boolean friends) throws ASException;

	List<User> getUsingLastUpdateStatusAndRangeAndRole(PersistenceProvider pp, Date lastUpdate,
			boolean afterLastUpdateDate, List<Integer> status, Range range, String order, List<Integer> role,
			Map<String, String> attributes, boolean detachable) throws ASException;
	
	void updateWithoutChangingMail(User obj) throws ASException;

}
