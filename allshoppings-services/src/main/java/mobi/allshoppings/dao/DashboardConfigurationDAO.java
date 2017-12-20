package mobi.allshoppings.dao;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.DashboardConfiguration;

public interface DashboardConfigurationDAO extends GenericDAO<DashboardConfiguration> {

	Key createKey(DashboardConfiguration seed) throws ASException;
	String getIdentifierUsingEntityIdAndEntityKind(String entityId, byte entityKind) throws ASException;
	DashboardConfiguration getUsingEntityIdAndEntityKind(String entityId, byte entityKind,
			boolean detachable) throws ASException;
	
}
