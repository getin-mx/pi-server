package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.CampaignSpecial;

public interface CampaignSpecialDAO extends GenericDAO<CampaignSpecial> {

	Key createKey() throws ASException;
	List<CampaignSpecial> getUsingShoppingAndStatus(String shoppingId, List<Integer> status, String order) throws ASException;
	List<CampaignSpecial> getUsingBrandAndStatus(String brandId, List<Integer> status, String order) throws ASException;
	List<CampaignSpecial> getUsingStoreAndStatus(String storeId, List<Integer> status, String order) throws ASException;
	
}
