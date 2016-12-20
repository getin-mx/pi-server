package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDMAEmployee;
import mobi.allshoppings.tools.Range;

public interface APDMAEmployeeDAO extends GenericDAO<APDMAEmployee> {

	Key createKey(APDMAEmployee seed) throws ASException;
	List<APDMAEmployee> getUsingEntityIdAndRange(String entityId, Integer entityKind, Range range, String order, boolean detachable) throws ASException;

}
