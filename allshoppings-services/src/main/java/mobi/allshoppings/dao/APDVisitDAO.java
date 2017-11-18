package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;
import java.util.Map;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.tools.Range;

@Deprecated
public interface APDVisitDAO extends GenericDAO<APDVisit> {

	Key createKey(APDVisit obj) throws ASException;

	List<APDVisit> getUsingEntityIdAndEntityKindAndDate(String entityId, Integer entityKind, Date fromDate, Date toDate,
			Integer checkinType, Range range, String order, Map<String, String> attributes, boolean detachable) throws ASException;

	void deleteUsingEntityIdAndEntityKindAndDate(String entityId, Integer entityKind, Date fromDate, Date toDate, Integer checkinType)
			throws ASException;

	List<APDVisit> getUsingStoresAndDate(List<String> stores, Date fromDate, Date toDate, Range range,
			boolean detachable) throws ASException;

	Map<Integer, Integer> getRepetitions(List<String> entityIds, Integer entityKind, Integer checkinType, Date fromDate,
			Date toDate) throws ASException;

	List<APDVisit> getUsingAPHE(String identifier, boolean detachable) throws ASException;
	
	Map<Integer, Integer> countUsingAPHE(String identifier) throws ASException;
}
