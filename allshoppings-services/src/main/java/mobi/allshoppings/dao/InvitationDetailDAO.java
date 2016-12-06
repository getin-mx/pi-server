package mobi.allshoppings.dao;


import java.util.List;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.InvitationDetail;

import com.inodes.datanucleus.model.Key;

public interface InvitationDetailDAO extends GenericDAO<InvitationDetail> {

	Key createKey() throws ASException;
	List<InvitationDetail> getUsingInvitedIdAndStatus(String invitedId, Integer status, String source) throws ASException;
	List<InvitationDetail> getUsingReferralCodeAndStatus(String referralCode, Integer status, String source) throws ASException;
}
