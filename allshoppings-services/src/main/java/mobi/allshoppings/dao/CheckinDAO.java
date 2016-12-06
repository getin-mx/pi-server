package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Checkin;

import com.inodes.datanucleus.model.Key;

public interface CheckinDAO extends GenericDAO<Checkin	> {

	Key createKey() throws ASException;
	Checkin getUnfinishedCheckinByEntityAndKindAndType(String deviceUUID, String entityId, Integer entityKind, Integer checkinType) throws ASException;
	Checkin getUnfinishedCheckinByEntityAndKindAndType(String deviceUUID, String entityId, Integer entityKind, Integer checkinType, Long closeLimitMillis) throws ASException;
	Checkin getUnfinishedCheckinByEntityAndKindAndType(String deviceUUID, String entityId, Integer entityKind, Integer checkinType, Long closeLimitMillis, Date forDate) throws ASException;
	Checkin getCheckinByEntityAndKindAndType(String deviceUUID, String entityId, Integer entityKind, Integer checkinType, Date forDate) throws ASException;
	long getUserCheckinCount(String userId) throws ASException;
	long getEntityCheckinCount(String entityId, Integer entityKind) throws ASException;
	
	List<Checkin> getUsingEntityKindAndPossibleFakeAndDates(Integer entityKind, Boolean possibleFake, Date fromDate, Date toDate) throws ASException;
	List<Checkin> getUsingEntityIdAndEntityKindAndPossibleFakeAndDates(String entityId, Integer entityKind, Boolean possibleFake, Date fromDate, Date toDate) throws ASException;
}
