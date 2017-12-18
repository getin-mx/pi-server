package mobi.allshoppings.dao;


import java.util.Collection;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tx.PersistenceProvider;

public interface StoreDAO extends GenericDAO<Store> {
	
	Key createKey(String shoppingId, String brandId) throws ASException;
	Key createKey(String brandId) throws ASException;
	Key createKey() throws ASException;

	List<Store> getUsingShoppingAndStatus(String shoppingId, List<Byte> status, String order) throws ASException;
	List<Store> getUsingUserAndBrandAndStatus(User user, String brandId, List<Byte> status, String order) throws ASException;
	List<Store> getUsingBrandAndStatus(String brandId, List<Byte> status, String order) throws ASException;
	List<Store> getUsingBrandAndShoppingAndStatus(String brandId, String shoppingId, List<Byte> status,
			String order) throws ASException;
	List<Store> getStreetUsingBrandAndStatus(String brandId, List<Byte> status, String order) throws ASException;
	Store getUsingExternalId(String externalId) throws ASException;
	
	List<Store> getUsingStatus(List<Byte> status) throws ASException;
	List<Store> getUsingIdsAndStatus(Collection<String> ids, List<Byte> status) throws ASException;
	List<String> getBrandIdsUsingIdList(PersistenceProvider pp, List<String> idList, boolean detachable) throws ASException;
}
