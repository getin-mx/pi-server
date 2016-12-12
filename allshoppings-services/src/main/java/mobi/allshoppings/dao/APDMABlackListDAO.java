package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDMABlackList;
import mobi.allshoppings.tools.Range;

public interface APDMABlackListDAO extends GenericDAO<APDMABlackList> {

	Key createKey(APDMABlackList seed) throws ASException;
	List<APDMABlackList> getUsingEntityIdAndRange(String entityId, Integer entityKind, Range range, String order, boolean detachable) throws ASException;

}
