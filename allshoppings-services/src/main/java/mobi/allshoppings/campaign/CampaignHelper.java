package mobi.allshoppings.campaign;

import java.util.Date;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.CampaignAction;
import mobi.allshoppings.model.Checkin;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.Offer;
import mobi.allshoppings.model.User;

public interface CampaignHelper {

	CampaignAction getCampaignActionForCheckin(User user, Checkin checkin, Date date ) throws ASException;
	boolean hasAvailabilityForDate(CampaignAction campaignAction, Date date ) throws ASException;
	boolean hasSpecialBeenUsedForUserAndDate(CampaignAction campaignAction, User user, Date date) throws ASException;
	CampaignActivity createActivity(User user, Checkin checkin, Date date ) throws ASException;
	CampaignActivity createActivity(User user, DeviceInfo device, CampaignAction special ) throws ASException;
	void sendCampaignActivity(CampaignActivity activity ) throws ASException;
	void sendCampaignActivity(User user, DeviceInfo device, Checkin checkin, CampaignAction special, CampaignActivity activity, boolean sendMail, boolean sendPush) throws ASException;
	void sendCampaignActivity(User user, DeviceInfo device, Checkin checkin, CampaignActivity activity, boolean sendMail, boolean sendPush) throws ASException;
	Offer campaignActivityToOffer(CampaignActivity activity) throws ASException;
	String buildInstructions(CampaignAction special, CampaignActivity activity );
	String assingCustomUrl(CampaignAction special, CampaignActivity activity);
	void setNonDisplayableForUser(String userId);
	
}
