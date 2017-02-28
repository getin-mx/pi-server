package mobi.allshoppings.dao;

import java.util.List;
import java.util.Map;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.AuditLog;
import mobi.allshoppings.tools.Range;

public interface AuditLogDAO extends GenericDAO<AuditLog> {

	Key createKey() throws ASException;
	List<AuditLog> getUsingUserAndTypeAndRange(String userId, Integer eventType, Range range, String order, Map<String, String> attributes, boolean detachable) throws ASException;
}
