package mobi.allshoppings.dao;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.DashboardConfiguration;

public interface DashboardConfigurationDAO extends GenericDAO<DashboardConfiguration> {

	Key createKey(DashboardConfiguration seed) throws ASException;
	String getIdentifierUsingEntityIdAndEntityKind(String entityId, Integer entityKind) throws ASException;
	DashboardConfiguration getUsingEntityIdAndEntityKind(String entityId, Integer entityKind, boolean detachable) throws ASException;
	
}
