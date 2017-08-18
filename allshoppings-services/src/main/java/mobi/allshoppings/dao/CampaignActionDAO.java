package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.CampaignAction;
import mobi.allshoppings.tools.Range;

public interface CampaignActionDAO extends GenericDAO<CampaignAction> {

	Key createKey() throws ASException;
	List<CampaignAction> getUsingShoppingAndStatus(String shoppingId, List<Integer> status, String order) throws ASException;
	List<CampaignAction> getUsingBrandAndStatus(String brandId, List<Integer> status, String order) throws ASException;
	List<CampaignAction> getUsingStoreAndStatus(String storeId, List<Integer> status, String order) throws ASException;
	List<CampaignAction> getUsingAppAndBrandAndStatusAndRange(String appId, String brandId, List<Integer> status, Range range, String order, Boolean detachable) throws ASException;

}
