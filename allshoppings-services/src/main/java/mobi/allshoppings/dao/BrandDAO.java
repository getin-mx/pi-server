package mobi.allshoppings.dao;


import java.util.Collection;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public interface BrandDAO extends GenericDAO<Brand> {

	List<Brand> getByViewLocationAndStatus(ViewLocation vl, List<Integer> status, String order) throws ASException;
	List<Brand> getByViewLocationAndStatus(ViewLocation vl, List<Integer> status, String order, boolean detachable) throws ASException;
	List<Brand> getByViewLocationAndStatus(PersistenceProvider pp, ViewLocation vl, List<Integer> status, String order) throws ASException;
	List<Brand> getByViewLocationAndStatus(PersistenceProvider pp, ViewLocation vl, List<Integer> status, String order, boolean detachable) throws ASException;

	List<Brand> getUsingStatusAndRangeInCache(List<Integer> status, Range range, User user, int returnType) throws ASException;
	List<Brand> getUsingIdsAndStatusAndRangeInCache(Collection<String> ids, List<Integer> status, Range range, User user, int returnType) throws ASException;

	Key createKey(String brandName, String country) throws ASException;

}
