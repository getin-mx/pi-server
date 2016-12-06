package mobi.allshoppings.dao;

import java.util.Collection;
import java.util.List;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.FinancialEntity;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.tools.Range;

import com.inodes.datanucleus.model.Key;

public interface FinancialEntityDAO extends GenericDAO<FinancialEntity> {

	Key createKey(String name, String country) throws ASException;

	List<FinancialEntity> getUsingCountryAndStatus(String country, List<Integer> status) throws ASException;
	List<FinancialEntity> getUsingViewLocationAndStatus(ViewLocation vl, List<Integer> status) throws ASException;

	List<FinancialEntity> getUsingStatusAndRangeInCache(List<Integer> status, Range range, User user, int returnType, String order) throws ASException;
	List<FinancialEntity> getUsingIdsAndStatusAndRangeInCache(Collection<String> ids, List<Integer> status, Range range, User user, int returnType, String order) throws ASException;

}
