package mobi.allshoppings.coupon;

import java.util.Date;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.adapter.CouponAdapter;

public interface CouponHelper {

	CouponAdapter getCouponUsingActivityId(String specialId, User user, boolean includeExpiredStatus, boolean includeShoppings, boolean includeBrands, boolean includeFinancialEntities) throws ASException;
	CouponAdapter getCouponByCode(String couponCode, boolean includeExpiredStatus, boolean includeShoppings, boolean includeBrands, boolean includeFinancialEntities) throws ASException;
	String createCouponCode(CampaignActivity ca, String params);
	void redeem(String couponCode, Date redeemDate) throws ASException;
	void ruleReject(String campaignActivityId) throws ASException;
	void notifyRedemption(String couponCode) throws ASException;
	boolean hasAvailability(String userId) throws ASException;
	boolean deviceHasAvailability(String deviceUUID) throws ASException;
	boolean deviceHasAvailability(String deviceUUID, String campaignSpecialId) throws ASException;
	boolean deviceHasActiveCoupons(String deviceUUID) throws ASException;

}
