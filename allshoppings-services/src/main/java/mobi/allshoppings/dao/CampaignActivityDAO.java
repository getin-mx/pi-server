package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.CampaignSpecial;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

import com.inodes.datanucleus.model.Key;

public interface CampaignActivityDAO extends GenericDAO<CampaignActivity> {

	Key createKey() throws ASException;
	boolean hasAvailabilityForDate(CampaignSpecial campaignSpecial, Date date) throws ASException;
	long countDeliveredUsignCampaignSpecial(CampaignSpecial campaignSpecial) throws ASException;
	long countDeliveredUsignCampaignSpecialAndUserAndDate(CampaignSpecial campaignSpecial, User user, Date date) throws ASException;
	boolean hasSpecialBeenUsedForUserAndDate(CampaignSpecial campaignSpecial, User user, Date date) throws ASException;
	
	List<CampaignActivity> getUsingUserAndRedeemStatusAndRange(String userId, List<Integer> redeemStatus, Range range, String order) throws ASException;
	List<CampaignActivity> getUsingUserAndRedeemStatusAndRange(PersistenceProvider pp, String userId, List<Integer> redeemStatus, boolean onlyDisplayableItems, Range range, String order, boolean detachable) throws ASException;
	
	CampaignActivity getUsingCouponCode(PersistenceProvider pp, String couponCode, boolean detachable) throws ASException;
	CampaignActivity getUsingCouponCode(String couponCode) throws ASException;
	CampaignActivity getLast() throws ASException;
	
	List<CampaignActivity> getUsingDatesAndCampaignSpecial(Date fromDate, Date toDate, String campaignSpecialId, Range range, String order) throws ASException;
	List<CampaignActivity> getUsingDatesAndCampaignSpecial(PersistenceProvider pp, Date fromDate, Date toDate, String campaignSpecialId, Range range, String order, boolean detachable) throws ASException;

	
}
