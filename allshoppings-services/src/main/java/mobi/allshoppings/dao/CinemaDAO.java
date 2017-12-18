package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Cinema;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

@Deprecated
public interface CinemaDAO extends GenericDAO<Cinema> {

	Key createKey(String identifier) throws ASException;

	List<Cinema> getUsingBrandAndRange(String brandId, Range range, String order) throws ASException;
	List<Cinema> getUsingBrandAndRange(PersistenceProvider pp, String brandId, Range range, String order, boolean detachable) throws ASException;
	List<Cinema> getUsingBrandAndStatusAndRange(String brandId, List<Integer> statuses, Range range, String order) throws ASException;
	List<Cinema> getUsingBrandAndStatusAndRange(PersistenceProvider pp, String brandId, List<Integer> statuses, Range range, String order, boolean detachable) throws ASException;
	
}
