package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Offer;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

import com.inodes.datanucleus.model.Key;

public interface OfferDAO extends GenericDAO<Offer> {

	List<Offer> getUsingStatusAndRangeInCache(List<Integer> status, Range range, User user, int returnType) throws ASException;
	List<Offer> getUsingKindAndRangeInCache(String entityId, Integer entityKind, Range range, User user, int returnType) throws ASException;

	List<Offer> getActiveUsingBrandAndRange(String brandId, Range range) throws ASException;
	List<Offer> getActiveUsingShoppingAndRange(String shoppingId, Range range) throws ASException;

	List<Offer> getByViewLocationAndDate(ViewLocation vl, Date queryDate, String order) throws ASException;
	List<Offer> getByViewLocationAndDate(ViewLocation vl, Date queryDate, String order, boolean detachable) throws ASException;
	List<Offer> getByViewLocationAndDate(PersistenceProvider pp, ViewLocation vl, Date queryDate, String order) throws ASException;
	List<Offer> getByViewLocationAndDate(PersistenceProvider pp, ViewLocation vl, Date queryDate, String order, boolean detachable) throws ASException;

	List<Offer> getToExpire(Range range) throws ASException;
	long countToExpire() throws ASException;

	Key createKey() throws ASException;

}
