package mobi.allshoppings.coupon.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Text;

import mobi.allshoppings.campaign.CampaignHelper;
import mobi.allshoppings.coupon.CouponHelper;
import mobi.allshoppings.dao.CampaignActivityDAO;
import mobi.allshoppings.dao.CampaignActionDAO;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.DeviceMessageLockDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.CampaignAction;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.DeviceMessageLock;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.adapter.CouponAdapter;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;
import mobi.allshoppings.push.PushMessageHelper;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Hash;
import mobi.allshoppings.tools.I18NUtils;
import mobi.allshoppings.tracker.TrackerHelper;

public class CouponHelperImpl implements CouponHelper {

	private static final Logger log = Logger.getLogger(CouponHelperImpl.class.getName());

	@Autowired
	private CampaignActivityDAO caDao;
	@Autowired
	private CampaignActionDAO csDao;
	@Autowired
	private UserDAO userDao;

	@Autowired
	private DeviceMessageLockDAO dmlDao;
	@Autowired
	private DeviceInfoDAO deviceInfoDao;
	
	@Autowired
	private CampaignHelper campaignHelper;
	@Autowired
	private PushMessageHelper pushMessageHelper;
	@Autowired
	private TrackerHelper trackerHelper;
//	@Autowired
//	private PointsService pointsService;

	/**
	 * Obtains a coupon adapter using the campaign activity Identifier
	 * 
	 * @param campaignActivityIdentifier
	 *            The Campaign Activity ID to use
	 * @param user
	 *            The user to assign the coupon to
	 * @param includeShoppings
	 *            Include shoppings information in the coupon
	 * @param includeBrands
	 *            Include brands information in the coupon
	 * @param includeFinancialEntities
	 *            Include financial entities information in the coupon
	 * @return A well formed coupon adapter
	 * @throws ASException
	 */
	@Override
	public CouponAdapter getCouponUsingActivityId(String campaignActivityIdentifier, User user, boolean includeExpiredStatus,  boolean includeShoppings, boolean includeBrands, boolean includeFinancialEntities) throws ASException {

		try {
			CampaignActivity ca = caDao.get(campaignActivityIdentifier);

			Map<String,Object> options = CollectionFactory.createMap();
			options.put(CouponAdapter.OPTIONS_CSDAO, csDao);
			options.put(CouponAdapter.OPTIONS_CHELPER, campaignHelper);
			options.put(CouponAdapter.OPTIONS_REQUESTER, user);
			options.put(CouponAdapter.OPTIONS_INCLUDE_EXPIRED_STATUS, includeExpiredStatus);

			options = addCaches(options, includeShoppings, includeBrands, includeFinancialEntities);

			// Obtains the coupon object
			CouponAdapter coupon = new GenericAdapterImpl<CouponAdapter>().adapt(ca, user.getIdentifier(), CouponAdapter.class, null, options);
			
			return coupon;
			
		} catch( ASException e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}

	}

	/**
	 * Returns a coupon code for a specific campaign activity. If the campaign
	 * activity already had a coupon code, then returns that code. Else, returns
	 * a new code
	 * 
	 * @param ca
	 *            The specified campaign activity
	 * @return A Fully functional coupon code
	 */
	@Override
	public String createCouponCode(CampaignActivity ca, String stringParams) {
		JSONObject params = new JSONObject(stringParams);
		if(!StringUtils.hasText(ca.getCouponCode())) {
			ca.setCouponCode(Hash.generateAuthCode());
			
			//FIXME: Hardcoded Cinepolis code here!!!!
			if(StringUtils.hasText(ca.getDeviceUUID())) {
				try {
					DeviceInfo device = deviceInfoDao.get(ca.getDeviceUUID(), true);
					if( device.getAppId().equals("cinepolis_mx")) {

						JSONObject extras = new JSONObject(ca.getExtras() != null ? ca.getExtras().getValue() : "");
						JSONArray coupons = extras.has("suggestedCoupons") ? extras.getJSONArray("suggestedCoupons") : null;

						if( coupons != null ) {

							ca.setCouponCode(coupons.getString(0));
							
							if( params != null && params.has("couponCount") && params.getInt("couponCount") >= 2)
								extras.put("extraCoupon1", coupons.getString(1));

							if( params != null && params.has("couponCount") && params.getInt("couponCount") >= 3)
								extras.put("extraCoupon2", coupons.getString(2));

							if( params != null && params.has("couponCount"))
								extras.put("couponCount", params.getInt("couponCount"));

							ca.setExtras(new Text(extras.toString()));
						
						} else {
						
							ca.setCouponCode("000000000001");
							
							if( params != null && params.has("couponCount") && params.getInt("couponCount") >= 2)
								extras.put("extraCoupon1", "000000000002");

							if( params != null && params.has("couponCount") && params.getInt("couponCount") >= 3)
								extras.put("extraCoupon2", "000000000003");

							if( params != null && params.has("couponCount"))
								extras.put("couponCount", params.getInt("couponCount"));

							ca.setExtras(new Text(extras.toString()));
						}
					}
				} catch( ASException | JSONException e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
		return ca.getCouponCode();
	}
	
	/**
	 * Obtains a coupon adapter using a previously generated coupon code
	 * 
	 * @param couponCode
	 *            The coupon code to search for
	 * @param includeShoppings
	 *            Include shoppings information in the coupon
	 * @param includeBrands
	 *            Include brands information in the coupon
	 * @param includeFinancialEntities
	 *            Include financial entities information in the coupon
	 * @return A well formed coupon adapter
	 * @throws ASException
	 */
	@Override
	public CouponAdapter getCouponByCode(String couponCode, boolean includeExpiredStatus, boolean includeShoppings, boolean includeBrands, boolean includeFinancialEntities) throws ASException {

		try {
			CampaignActivity ca = caDao.getUsingCouponCode(couponCode);
			User user = userDao.get(ca.getUserId());

			Map<String,Object> options = CollectionFactory.createMap();
			options.put(CouponAdapter.OPTIONS_CSDAO, csDao);
			options.put(CouponAdapter.OPTIONS_CHELPER, campaignHelper);
			options.put(CouponAdapter.OPTIONS_REQUESTER, user);
			options.put(CouponAdapter.OPTIONS_INCLUDE_EXPIRED_STATUS, includeExpiredStatus);

			options = addCaches(options, includeShoppings, includeBrands, includeFinancialEntities);
			
			// Obtains the coupon object
			CouponAdapter coupon = new GenericAdapterImpl<CouponAdapter>().adapt(ca, user.getIdentifier(), CouponAdapter.class, null, options);

			return coupon;

		} catch( ASException e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}

	}

	/**
	 * Notifies a user about a successful redemption
	 * 
	 * @param couponCode
	 *            Redeemed Coupon Code
	 * @throws ASException
	 */
	@Override
	public void notifyRedemption(String couponCode) throws ASException {

		log.log(Level.INFO, "Trying to notify redemption for coupon code: " + couponCode + "...");
		try {
			CouponAdapter coupon = getCouponByCode(couponCode, false, false, false, false);
			User user = userDao.get(coupon.getUserId());

//			long points = pointsService.calculatePointsForAction(user.getIdentifier(), PointsService.ACTION_COUPON_REDEEMED,
//					EntityKind.KIND_CAMPAIGN_ACTIVITY, coupon.getIdentifier(), null);
					
			log.log(Level.INFO, "User is " + user.getIdentifier());

			Map<String, String> replacementValues = new HashMap<String, String>();
			replacementValues.put("name", coupon.getName());
			replacementValues.put("couponCode", coupon.getCouponCode());
//			replacementValues.put("points", String.valueOf(points));
			String message = I18NUtils.getI18NMessage("es_AR", "app.coupon.redeemMessage", replacementValues);

			pushMessageHelper.sendNotification(user, "AllShoppings",
					coupon.getAvatarId(), message, null, null,
					coupon.getIdentifier(), EntityKind.KIND_CAMPAIGN_ACTIVITY);

			log.log(Level.INFO, "Enqueueing log for redemption for coupon code: " + couponCode + "...");
			// track action
			trackerHelper.enqueue( user, null,
					null, "/app/notifyRedemption/" + coupon.getCouponCode(),
					I18NUtils.getI18NMessage("es_AR", "service.CouponRedemption"), 
					null, null);

			// Send information to the challenge objective listener
//			challengeService.triggerAchievements(user.getIdentifier(),
//					ChallengeObjective.TYPE_COUPON_REDEMPTION, null,
//					null, new Date(), 1L);

		} catch( Exception e1 ) {
			log.log(Level.SEVERE, e1.getMessage(), e1);
		}

	}

	/**
	 * Rejects a coupon by rule using its activity id
	 * 
	 * @param campaignActivityId
	 *            Activity ID to reject
	 * @throws ASException
	 */
	@Override
	public void ruleReject(String campaignActivityId) throws ASException {
		CampaignActivity ca = caDao.get(campaignActivityId);
		
		if( ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_DELIVERED)) { 
			
			ca.setRedeemStatus(CampaignActivity.REDEEM_STATUS_RULE_REJECTED);
			ca.setStatusChangeDateTime(new Date());
			
			caDao.update(ca);

		} else {
			throw ASExceptionHelper.forbiddenException();
		}
	}
	
	/**
	 * Redeems a coupon
	 * 
	 * @param couponCode
	 *            The coupon code to redeem
	 * @param redeemDate
	 *            Optional: Date/Time in which the coupon is redeemed
	 * @throws ASException
	 */
	@Override
	public void redeem(String couponCode, Date redeemDate) throws ASException {

		CampaignActivity ca = caDao.getUsingCouponCode(couponCode);
		
		if( ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_ACCEPTED) 
				|| ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_DELIVERED)) {
			
			ca.setRedeemStatus(CampaignActivity.REDEEM_STATUS_REDEEMED);
			ca.setRedeemDateTime(redeemDate == null ? new Date() : redeemDate);
			
			caDao.update(ca);

//			pointsService.enqueue(ca.getUserId(), PointsService.ACTION_COUPON_REDEEMED,
//					EntityKind.KIND_CAMPAIGN_ACTIVITY, ca.getIdentifier(), null);

		} else {
			throw ASExceptionHelper.forbiddenException();
		}
		
	}
	
	/**
	 * Add caches to the options used in the coupon adapter generator
	 * 
	 * @param options
	 *            The original options map
	 * @param includeShoppings
	 *            Include shoppings information in the coupon
	 * @param includeBrands
	 *            Include brands information in the coupon
	 * @param includeFinancialEntities
	 *            Include financial entities information in the coupon
	 * @return The options map with the selected caches appedend
	 * @throws ASException
	 */
	private Map<String, Object> addCaches(Map<String, Object> options, boolean includeShoppings, boolean includeBrands, boolean includeFinancialEntities) throws ASException {

		if( options == null ) options = new HashMap<String, Object>();
//		User user = (User)options.get(CouponAdapter.OPTIONS_REQUESTER);
//
//		if( includeShoppings ) {
//			options.put(CouponAdapter.OPTIONS_SDAO, shoppingDao);
//			if( user != null ) {
//				UserEntityCache uecShopping = uecService.get(user, EntityKind.KIND_SHOPPING, UserEntityCache.TYPE_FAVORITES_ONLY);
//				options.put(CouponAdapter.OPTIONS_UECS, uecShopping);
//			}
//		}
//
//		if( includeBrands ) {
//			options.put(CouponAdapter.OPTIONS_BDAO, brandDao);
//			if( user != null ) {
//				UserEntityCache uecBrand = uecService.get(user, EntityKind.KIND_BRAND, UserEntityCache.TYPE_FAVORITES_ONLY);
//				options.put(CouponAdapter.OPTIONS_UECB, uecBrand);
//			}
//		}
//
//		if( includeFinancialEntities ) {
//			options.put(CouponAdapter.OPTIONS_FEDAO, financialEntityDao);
//			if( user != null ) {
//				UserEntityCache uecFinancialEntity = uecService.get(user, EntityKind.KIND_FINANCIAL_ENTITY, UserEntityCache.TYPE_FAVORITES_ONLY);
//				options.put(CouponAdapter.OPTIONS_UECFE, uecFinancialEntity);
//			}
//		}

		return options;
	}

	/**
	 * Checks if the coupon is available for a determinate user
	 * 
	 * @param userId
	 *            The user identifier to check
	 * @return True if the user can receive coupons, false if not
	 * @throws ASException
	 */
	@Override
	public boolean hasAvailability(String userId) throws ASException {
		User user = userDao.get(userId, true);
		
		// No messaging enabled means no coupons received
		if( !user.getGeoFenceEnabled() || !user.getReceivePushMessages() ) 
			return false;
		
		// if has an active lock, means false
		if( dmlDao.hasActiveLocks(userId, new Date()))
			return false;
		
		// No surprises till now... means it's OK
		return true;
	}

	/**
	 * Checks if the coupon is available for a determinate device
	 * 
	 * @param deviceUUID
	 *            The user identifier to check
	 * @return True if the user can receive coupons, false if not
	 * @throws ASException
	 */
	@Override
	public boolean deviceHasAvailability(String deviceUUID) throws ASException {
		// if has an active lock, means false
		if( dmlDao.deviceHasActiveLocks(deviceUUID, new Date()))
			return false;
		
		// No surprises till now... means it's OK
		return true;
	}

	/**
	 * Checks if the coupon is available for a determinate device
	 * 
	 * @param deviceUUID
	 *            The user identifier to check
	 * @return True if the user can receive coupons, false if not
	 * @throws ASException
	 */
	@Override
	public boolean deviceHasAvailability(String deviceUUID, String campaignSpecialId) throws ASException {

		try {
			DeviceInfo di = deviceInfoDao.get(deviceUUID, true);
			if(StringUtils.hasText(di.getUserId())) {
				User user = userDao.get(di.getUserId(), true);
				if(!user.getGeoFenceEnabled() || !user.getReceivePushMessages()) return false;
			}
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		 
		List<DeviceMessageLock> locks = dmlDao.getDeviceActiveLocks(deviceUUID, new Date());
		for( DeviceMessageLock lock : locks ) {
			if(lock.getScope() != 2 ) return false;
			if(StringUtils.hasText(lock.getCampaignActivityId())) {
				CampaignActivity ca = caDao.get(lock.getCampaignActivityId(), true);
				CampaignAction cs = csDao.get(ca.getCampaignSpecialId(), true);
				if( cs.getIdentifier().equals(campaignSpecialId)) return false;
			}
		}
		
		// No surprises till now... means it's OK
		return true;
	}

	/**
	 * Checks if the coupon is available for a determinate device
	 * 
	 * @param deviceUUID
	 *            The user identifier to check
	 * @return True if the user can receive coupons, false if not
	 * @throws ASException
	 */
	@Override
	public boolean deviceHasActiveCoupons(String deviceUUID) throws ASException {
		DeviceInfo di = deviceInfoDao.get(deviceUUID, true);
		if(StringUtils.hasText(di.getUserId())) {
			Date now = new Date();
			List<Integer> redeemStatus = Arrays.asList(new Integer[] {CampaignActivity.REDEEM_STATUS_DELIVERED, CampaignActivity.REDEEM_STATUS_ACCEPTED});
			List<CampaignActivity> list = caDao.getUsingUserAndRedeemStatusAndRange(di.getUserId(), redeemStatus, null, null);
			for( CampaignActivity obj : list ) {
				if( obj.getLimitDateTime().after(now)) return true;
			}
		}
		return false;
	}
}
