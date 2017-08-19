package mobi.allshoppings.dao;


import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Campaign;

public interface CampaignDAO extends GenericDAO<Campaign> {

	Key createKey() throws ASException;

}
