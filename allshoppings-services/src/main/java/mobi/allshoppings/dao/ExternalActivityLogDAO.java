package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.ExternalActivityLog;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public interface ExternalActivityLogDAO extends GenericDAO<ExternalActivityLog> {

	Key createKey() throws ASException;
	List<ExternalActivityLog> getUsingDatesAndCampaignSpecial(Date fromDate, Date toDate, String campaignSpecialId, Range range, String order) throws ASException;
	List<ExternalActivityLog> getUsingDatesAndCampaignSpecial(PersistenceProvider pp, Date fromDate, Date toDate, String campaignSpecialId, Range range, String order, boolean detachable) throws ASException;
	
}
