package mobi.allshoppings.dao;

import java.util.List;
import java.util.Map;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.InnerZone;
import mobi.allshoppings.tools.Range;

public interface InnerZoneDAO extends GenericDAO<InnerZone> {

	Key createKey() throws ASException;
	List<InnerZone> getUsingEntityIdAndRange(String entityId, Integer entityKind, Range range, String order, Map<String, String> attributes, boolean detachable) throws ASException;
	
}
