package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Movie;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public interface MovieDAO extends GenericDAO<Movie> {

	Key createKey(String identifier) throws ASException;
	
	List<Movie> getUsingBrandAndStatusAndRange(String brand, List<Integer> status, Range range, String order) throws ASException;
	List<Movie> getUsingBrandAndStatusAndRange(PersistenceProvider pp, String brand, List<Integer> status, Range range, String order, boolean detachable) throws ASException;
	
	void disableOldMovies(Date referenceDate) throws ASException;
}
