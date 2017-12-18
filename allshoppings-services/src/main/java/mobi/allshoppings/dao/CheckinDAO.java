package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Checkin;

public interface CheckinDAO extends GenericDAO<Checkin	> {

	Key createKey() throws ASException;
	Checkin getUnfinishedCheckinByEntityAndKindAndType(String deviceUUID, String entityId,
			byte entityKind, byte checkinType) throws ASException;
	Checkin getUnfinishedCheckinByEntityAndKindAndType(String deviceUUID, String entityId,
			byte entityKind, byte checkinType, long closeLimitMillis) throws ASException;
	Checkin getUnfinishedCheckinByEntityAndKindAndType(String deviceUUID, String entityId,
			byte entityKind, byte checkinType, long closeLimitMillis, Date forDate) throws ASException;
	Checkin getCheckinByEntityAndKindAndType(String deviceUUID, String entityId, byte entityKind,
			byte checkinType, Date forDate) throws ASException;
	long getUserCheckinCount(String userId) throws ASException;
	long getEntityCheckinCount(String entityId, byte entityKind) throws ASException;
	
	List<Checkin> getUsingEntityKindAndPossibleFakeAndDates(byte entityKind, Boolean possibleFake,
			Date fromDate, Date toDate) throws ASException;
	List<Checkin> getUsingEntityIdAndEntityKindAndPossibleFakeAndDates(String entityId,
			byte entityKind, Boolean possibleFake, Date fromDate, Date toDate) throws ASException;
}
