package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.tools.Range;

public interface APDVisitDAO extends GenericDAO<APDVisit	> {

	Key createKey(APDVisit obj) throws ASException;
	List<APDVisit> getUsingEntityIdAndEntityKindAndDate(String entityId, Integer entityKind, Date fromDate, Date toDate, Range range, boolean detachable) throws ASException;
	void deleteUsingEntityIdAndEntityKindAndDate(String entityId, Integer entityKind, Date fromDate, Date toDate) throws ASException;
	List<APDVisit> getUsingStoresAndDate(List<String> stores, Date fromDate, Date toDate, Range range, boolean detachable) throws ASException;
}
