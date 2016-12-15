package mobi.allshoppings.dao;


import java.util.Collection;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public interface ShoppingDAO extends GenericDAO<Shopping> {

	List<Shopping> getByViewLocationAndStatus(ViewLocation vl, List<Integer> status, String order) throws ASException;
	List<Shopping> getByViewLocationAndStatus(ViewLocation vl, List<Integer> status, String order, boolean detachable) throws ASException;
	List<Shopping> getByViewLocationAndStatus(PersistenceProvider pp, ViewLocation vl, List<Integer> status, String order) throws ASException;
	List<Shopping> getByViewLocationAndStatus(PersistenceProvider pp, ViewLocation vl, List<Integer> status, String order, boolean detachable) throws ASException;

	List<Shopping> getUsingStatusAndRangeInCache(List<Integer> status, Range range, User user, int returnType, String order) throws ASException;
	List<Shopping> getUsingIdsAndStatusAndRangeInCache(Collection<String> ids, List<Integer> status, Range range, User user, int returnType, String order) throws ASException;

	Key createKey(String shoppingName) throws ASException;

}
