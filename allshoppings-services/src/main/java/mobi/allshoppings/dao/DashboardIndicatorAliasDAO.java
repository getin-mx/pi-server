package mobi.allshoppings.dao;


import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.DashboardIndicatorAlias;

import com.inodes.datanucleus.model.Key;

public interface DashboardIndicatorAliasDAO extends GenericDAO<DashboardIndicatorAlias> {

	Key createKey(DashboardIndicatorAlias obj) throws ASException;
	DashboardIndicatorAlias getUsingFilters(String entityId, Integer entityKind, String elementId, String elementSubId) throws ASException;
}
