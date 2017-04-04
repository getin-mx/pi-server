package mobi.allshoppings.dao;


import java.util.List;
import java.util.Map;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDMAEmployee;
import mobi.allshoppings.tools.Range;

public interface APDMAEmployeeDAO extends GenericDAO<APDMAEmployee> {

	Key createKey() throws ASException;
	List<APDMAEmployee> getUsingEntityIdAndRange(String entityId, Integer entityKind, Range range, String order, Map<String, String> attributes, boolean detachable) throws ASException;
	List<APDMAEmployee> getUsingEntityIdandMac(String entityId, Integer entityKind, String mac) throws ASException;

}
