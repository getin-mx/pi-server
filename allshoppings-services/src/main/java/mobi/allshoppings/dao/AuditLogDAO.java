package mobi.allshoppings.dao;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.AuditLog;

public interface AuditLogDAO extends GenericDAO<AuditLog> {

	Key createKey() throws ASException;

}
