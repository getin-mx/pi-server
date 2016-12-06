package mobi.allshoppings.campaign;

import java.util.Date;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.CampaignSpecial;
import mobi.allshoppings.model.Checkin;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.Offer;
import mobi.allshoppings.model.User;

public interface CampaignHelper {

	CampaignSpecial getCampaignSpecialForCheckin(User user, Checkin checkin, Date date ) throws ASException;
	boolean hasAvailabilityForDate(CampaignSpecial campaignSpecial, Date date ) throws ASException;
	boolean hasSpecialBeenUsedForUserAndDate(CampaignSpecial campaignSpecial, User user, Date date) throws ASException;
	CampaignActivity createActivity(User user, Checkin checkin, Date date ) throws ASException;
	CampaignActivity createActivity(User user, DeviceInfo device, CampaignSpecial special ) throws ASException;
	void sendCampaignActivity(CampaignActivity activity ) throws ASException;
	void sendCampaignActivity(User user, DeviceInfo device, Checkin checkin, CampaignSpecial special, CampaignActivity activity, boolean sendMail, boolean sendPush) throws ASException;
	void sendCampaignActivity(User user, DeviceInfo device, Checkin checkin, CampaignActivity activity, boolean sendMail, boolean sendPush) throws ASException;
	Offer campaignActivityToOffer(CampaignActivity activity) throws ASException;
	String buildInstructions(CampaignSpecial special, CampaignActivity activity );
	String assingCustomUrl(CampaignSpecial special, CampaignActivity activity);
	void setNonDisplayableForUser(String userId);
	
}
