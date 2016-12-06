package mobi.allshoppings.coupon.impl;

import java.util.Date;
import java.util.logging.Logger;

import mobi.allshoppings.coupon.CouponHelper;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.adapter.CouponAdapter;

public class CouponHelperNullImpl implements CouponHelper {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(CouponHelperNullImpl.class.getName());

	@Override
	public CouponAdapter getCouponUsingActivityId(String specialId, User user,
			boolean includeExpiredStatus, boolean includeShoppings,
			boolean includeBrands, boolean includeFinancialEntities)
			throws ASException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CouponAdapter getCouponByCode(String couponCode,
			boolean includeExpiredStatus, boolean includeShoppings,
			boolean includeBrands, boolean includeFinancialEntities)
			throws ASException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createCouponCode(CampaignActivity ca, String params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void redeem(String couponCode, Date redeemDate) throws ASException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ruleReject(String campaignActivityId) throws ASException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyRedemption(String couponCode) throws ASException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasAvailability(String userId) throws ASException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deviceHasAvailability(String deviceUUID) throws ASException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deviceHasAvailability(String deviceUUID,
			String campaignSpecialId) throws ASException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deviceHasActiveCoupons(String deviceUUID) throws ASException {
		// TODO Auto-generated method stub
		return false;
	}

}
