package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.CampaignAction;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public interface CampaignActivityDAO extends GenericDAO<CampaignActivity> {

	Key createKey() throws ASException;
	boolean hasAvailabilityForDate(CampaignAction campaignAction, Date date) throws ASException;
	long countDeliveredUsignCampaignAction(CampaignAction campaignAction) throws ASException;
	long countDeliveredUsignCampaignActionAndUserAndDate(CampaignAction campaignAction, User user, Date date) throws ASException;
	boolean hasSpecialBeenUsedForUserAndDate(CampaignAction campaignAction, User user, Date date) throws ASException;
	
	List<CampaignActivity> getUsingUserAndRedeemStatusAndRange(String userId, List<Integer> redeemStatus, Range range, String order) throws ASException;
	List<CampaignActivity> getUsingUserAndRedeemStatusAndRange(PersistenceProvider pp, String userId, List<Integer> redeemStatus, boolean onlyDisplayableItems, Range range, String order, boolean detachable) throws ASException;
	
	CampaignActivity getUsingCouponCode(PersistenceProvider pp, String couponCode, boolean detachable) throws ASException;
	CampaignActivity getUsingCouponCode(String couponCode) throws ASException;
	CampaignActivity getLast() throws ASException;
	
	List<CampaignActivity> getUsingDatesAndCampaignAction(Date fromDate, Date toDate, String campaignActionId, Range range, String order) throws ASException;
	List<CampaignActivity> getUsingDatesAndCampaignAction(PersistenceProvider pp, Date fromDate, Date toDate, String campaignActionId, Range range, String order, boolean detachable) throws ASException;

	
}
