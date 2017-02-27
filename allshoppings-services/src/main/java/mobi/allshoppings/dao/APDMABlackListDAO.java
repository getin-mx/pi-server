package mobi.allshoppings.dao;


import java.util.List;
import java.util.Map;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDMABlackList;
import mobi.allshoppings.tools.Range;

public interface APDMABlackListDAO extends GenericDAO<APDMABlackList> {

	Key createKey() throws ASException;
	List<APDMABlackList> getUsingEntityIdAndRange(String entityId, Integer entityKind, Range range, String order, Map<String, String> attributes, boolean detachable) throws ASException;

}
