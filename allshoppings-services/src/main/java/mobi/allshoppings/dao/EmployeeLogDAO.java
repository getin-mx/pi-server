package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;
import java.util.Map;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.EmployeeLog;
import mobi.allshoppings.tools.Range;

public interface EmployeeLogDAO extends GenericDAO<EmployeeLog> {

	Key createKey(EmployeeLog obj) throws ASException;

	List<EmployeeLog> getUsingEntityIdAndEntityKindAndDate(String entityId, Integer entityKind, Date fromDate, Date toDate,
			Range range, String order, Map<String, String> attributes, boolean detachable) throws ASException;

}
