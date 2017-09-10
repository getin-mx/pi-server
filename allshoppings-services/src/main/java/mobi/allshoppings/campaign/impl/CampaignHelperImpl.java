package mobi.allshoppings.campaign.impl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.campaign.CampaignHelper;
import mobi.allshoppings.coupon.CouponHelper;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.CampaignActivityDAO;
import mobi.allshoppings.dao.CampaignActionDAO;
import mobi.allshoppings.dao.CheckinDAO;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.NotificationLogDAO;
import mobi.allshoppings.dao.OfferTypeDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.CampaignAction;
import mobi.allshoppings.model.Checkin;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.NotificationLog;
import mobi.allshoppings.model.Offer;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.push.PushMessageHelper;

public class CampaignHelperImpl implements CampaignHelper {

	private static final Logger log = Logger.getLogger(CampaignHelperImpl.class.getName());
	
	@Autowired
	private UserDAO userDao;
	@Autowired
	private CheckinDAO checkinDao;
	@Autowired
	private CampaignActionDAO campaignActionDao;
	@Autowired
	private CampaignActivityDAO campaignActivityDao;
	@Autowired
	private ShoppingDAO shoppingDao;
	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private BrandDAO brandDao;
	@Autowired
	private DeviceInfoDAO deviceInfoDao;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private NotificationLogDAO notificationLogDao;
	@Autowired
	private OfferTypeDAO offerTypeDao;
	@Autowired
	private PushMessageHelper pushHelper;
	@Autowired
	private CouponHelper couponHelper;

	@Override
	public Offer campaignActivityToOffer(CampaignActivity activity) throws ASException {
		CampaignAction special = campaignActionDao.get(activity.getCampaignActionId(), true);
		Offer offer = new Offer();
		
		offer.addShopping(activity.getShoppingId());
		offer.addBrand(activity.getBrandId());
		offer.addAvailableFinancialEntity(activity.getFinancialEntityId());
		
		offer.getAreaId().addAll(special.getAreaId());
		offer.setAvatarId(special.getAvatarId());
		offer.setCountry(special.getCountry());
		offer.setDescription(buildDescription(special, activity, true));
		offer.setExclusive(true);
		offer.setName(special.getName());
		offer.setOfferType(offerTypeDao.get(special.getOfferTypeId(), true));
		offer.setPolicies(special.getPolicies());
		offer.setValidFrom(new Date(new Date().getTime() + (long)(special.getTimezone() * 3600000)));
		offer.setValidTo(new Date(new Date().getTime() + (long)(special.getTimezone() * 3600000) + special.getSpan()));

		return offer;
	}
	
	@Override
	public CampaignAction getCampaignActionForCheckin(User user, Checkin checkin, Date date ) throws ASException {

		// The checkin kind is for a Shopping
		if( checkin.getEntityKind() == EntityKind.KIND_SHOPPING) {

			// Now I get the Shopping from the Checkin, and the list of active
			// campaign specials that the shopping has
			Shopping shopping = shoppingDao.get(checkin.getEntityId());
			List<CampaignAction> specialsList = campaignActionDao
					.getUsingShoppingAndStatus(
							shopping.getIdentifier(),
							null, null);

			// For each Special, Check requirements
			for( CampaignAction s : specialsList ) {
				log.log(Level.INFO, "Special found in getCampaignActionForCheckin: " + s);
				// Check Availability
				if( hasAvailabilityForDate(s, date)) {
					log.log(Level.INFO, "This Special has availability for date: " + s);
					if(!hasSpecialBeenUsedForUserAndDate(s, user, date)) {
						log.log(Level.INFO, "This Special has not been used for date: " + date + ", " + s);
						// Bingo! we have a candidate!
						return s;
					}
				} else {
					log.log(Level.INFO, "Special not available for " + date);
				}
			}
		}

		// The checkin kind is for a Brand or Street Store
		if( checkin.getEntityKind() == EntityKind.KIND_STORE) {

			// Now I get the Store and then the Brand from the Checkin, and the list of active
			// campaign specials that the shopping has
			Store store = storeDao.get(checkin.getEntityId());
			Brand brand = brandDao.get(store.getBrandId());
			List<CampaignAction> specialsList = campaignActionDao
					.getUsingBrandAndStatus(
							brand.getIdentifier(),
							null, null);

			// For each Special, Check requirements
			for( CampaignAction s : specialsList ) {
				log.log(Level.INFO, "Special found in getCampaignActionForCheckin: " + s);
				if( s.getShoppings().size() == 0 ) {
					// Check Availability
					if( hasAvailabilityForDate(s, date)) {
						log.log(Level.INFO, "This Special has availability for date: " + s);
						if(!hasSpecialBeenUsedForUserAndDate(s, user, date)) {
							log.log(Level.INFO, "This Special has not been used for date: " + date + ", " + s);
							// Bingo! we have a candidate!
							return s;
						}
					} else {
						log.log(Level.INFO, "Special not available for " + date);
					}
				} else {
					log.log(Level.INFO, "Special is attached to a mall... quitting");
				}
			}
		}

		return null;

	}
	
	@SuppressWarnings({"deprecation"})
	@Override
	public CampaignActivity createActivity(User user, Checkin checkin, Date date) throws ASException {

		CampaignActivity activity = null;
		
		CampaignAction special = getCampaignActionForCheckin(user, checkin, date);
		log.log(Level.INFO, "Special found: " + special);
		if( special != null ) {
			activity = new CampaignActivity();
			
			String shoppingId = null;
			Iterator<String> i = special.getShoppings().iterator();
			if(i.hasNext()) shoppingId = i.next();
			
			String brandId = null;
			i = special.getBrands().iterator();
			if(i.hasNext()) brandId = i.next();
			
			String financialEntityId = null;
			i = special.getAvailableFinancialEntities().iterator();
			if(i.hasNext()) financialEntityId = i.next();

			if( checkin.getEntityKind() == EntityKind.KIND_SHOPPING ) {
				activity.setShoppingId(checkin.getEntityId());
				activity.setBrandId(brandId);
				activity.setFinancialEntityId(financialEntityId);
			} else if( checkin.getEntityKind() == EntityKind.KIND_BRAND ) {
				activity.setShoppingId(shoppingId);
				activity.setBrandId(checkin.getEntityId());
				activity.setFinancialEntityId(financialEntityId);
			} else if( checkin.getEntityKind() == EntityKind.KIND_SHOPPING ) {
				activity.setShoppingId(shoppingId);
				activity.setBrandId(brandId);
				activity.setFinancialEntityId(checkin.getEntityId());
			}

			activity.setDeviceUUID(checkin.getDeviceUUID());
			activity.setCheckinId(checkin.getIdentifier());
			activity.setCampaignId(special.getCampaignId());
			activity.setCampaignActionId(special.getIdentifier());
			activity.setUserId(user.getIdentifier());
			activity.setPromotionType(special.getPromotionType());
			activity.setLimitDateTime(special.getSpan() > 0 ? DateUtils.add(DateUtils.add(
					activity.getCreationDateTime(), Calendar.MILLISECOND,
					special.getSpan().intValue()), Calendar.MILLISECOND, 
					(int)(special.getTimezone() * 3600000)) : activity
					.getCreationDateTime());
			couponHelper.createCouponCode(activity, null);
			
			activity.setKey(campaignActivityDao.createKey());
			activity.setCustomUrl(assingCustomUrl(special, activity));
			campaignActivityDao.create(activity);
			
		}

		return activity;
	}

	/**
	 * Assigns a custom URL
	 * 
	 * @param activity
	 *            the campaign activity
	 * @param special
	 *            the campaign special
	 * @return The custom URL, or null / blanks if there is none
	 */
	@Override
	public String assingCustomUrl(CampaignAction special, CampaignActivity activity) {
		String customUrl = special.getCustomUrl();
		if( StringUtils.hasText(customUrl)) {
			customUrl = customUrl.replace("{identifier}", activity.getKey().getName());
			if(!customUrl.startsWith("http") && customUrl.startsWith("/")) {
				customUrl = systemConfiguration.getPushMessageResponseUrl() + customUrl; 
			}
		}
		
		return customUrl;

	}
	
	@SuppressWarnings("deprecation")
	@Override
	public CampaignActivity createActivity(User user, DeviceInfo device, CampaignAction special ) throws ASException {
		CampaignActivity activity = null;
		
		if( special != null ) {
			activity = new CampaignActivity();
			
			String shoppingId = null;
			Iterator<String> i = special.getShoppings().iterator();
			if(i.hasNext()) shoppingId = i.next();
			
			String brandId = null;
			i = special.getBrands().iterator();
			if(i.hasNext()) brandId = i.next();
			
			String financialEntityId = null;
			i = special.getAvailableFinancialEntities().iterator();
			if(i.hasNext()) financialEntityId = i.next();

			activity.setShoppingId(shoppingId);
			activity.setBrandId(brandId);
			activity.setFinancialEntityId(financialEntityId);

			activity.setCheckinId(null);
			activity.setDeviceUUID(device != null ? device.getIdentifier() : null);
			activity.setCampaignId(special.getCampaignId());
			activity.setCampaignActionId(special.getIdentifier());
			activity.setUserId(user.getIdentifier());
			activity.setPromotionType(special.getPromotionType());
			activity.setLimitDateTime(special.getSpan() > 0 ? DateUtils.add(DateUtils.add(
					activity.getCreationDateTime(), Calendar.MILLISECOND,
					special.getSpan().intValue()), Calendar.MILLISECOND, 
					(int)(special.getTimezone() * 3600000)) : activity
					.getCreationDateTime());
			couponHelper.createCouponCode(activity, null);
			
			activity.setKey(campaignActivityDao.createKey());
			activity.setCustomUrl(assingCustomUrl(special, activity));
			campaignActivityDao.create(activity);
			
		}

		return activity;
	}
	
	@Override
	public void sendCampaignActivity(CampaignActivity activity) throws ASException {
		Checkin checkin = null;
		DeviceInfo device = null;

		try { checkin = checkinDao.get(activity.getCheckinId()); } catch( Exception e ) {}
		
		User user = userDao.get(activity.getUserId(), true);
		CampaignAction special = campaignActionDao.get(activity.getCampaignActionId(), true);
		if( checkin != null) {
			device = deviceInfoDao.get(checkin.getDeviceUUID(), true);
		}
		sendCampaignActivity(user, device, checkin, special, activity, true, true);
	}

	@Override
	public void sendCampaignActivity(User user, DeviceInfo device, Checkin checkin, CampaignActivity activity, 
			boolean sendMail, boolean sendPush) throws ASException {
		CampaignAction special = campaignActionDao.get(activity.getCampaignActionId(), true);
		sendCampaignActivity(user, device, checkin, special, activity, true, true);
	}
	
	@Override
	public void sendCampaignActivity(User user, DeviceInfo device, Checkin checkin, CampaignAction special,
			CampaignActivity activity, boolean sendMail, boolean sendPush) throws ASException {
	
		if( sendPush ) {
			try {
				String title = systemConfiguration.getPushMessageOfferTitle();
				String text = special.getName();
				
				String action = "coupon/" + activity.getIdentifier();

				// Create the notification Log
				NotificationLog nl = new NotificationLog();
				nl.setKey(notificationLogDao.createKey());
				nl.setTitle(text);
				nl.setUserId(user.getIdentifier());
				nl.setEntityId(activity.getIdentifier());
				nl.setEntityKind(EntityKind.KIND_OFFER);
				nl.setNotifyDate(null);
				nl.setAvatarId(special.getAvatarId());
				nl.setData(buildDescription(special, activity, false));
				nl.setStatus(StatusAware.STATUS_NEW);
				notificationLogDao.create(nl);

				// Finds devices for the user
				List<DeviceInfo> userDevices = null;
				if( device == null ) {
					userDevices = deviceInfoDao.getUsingUser(user.getIdentifier());
				} else {
					userDevices = Arrays.asList(new DeviceInfo[] {device});
				}
				
				// Send the message
				pushHelper.sendMessage(user, title, text, action, userDevices, true);

				// And now, set the succeeded notify date
				nl.setNotifyDate(new Date());
				notificationLogDao.update(nl);

			} catch( ASException e ) {
				if(e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_PUSH_MESSAGE) {
					log.log(Level.SEVERE, e.getMessage(), e);
				} else if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ){
					// Silently fail
				} else {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			} catch( Exception e ) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	private String buildDescription(CampaignAction special, CampaignActivity activity, boolean includeInstructions) {
		String desc = special.getDescription();
		if( includeInstructions ) desc = desc + "<br/><br/><b>" + buildInstructions(special, activity) + "</b>";
		return desc;
	}
	
	@Override
	public String buildInstructions(CampaignAction special, CampaignActivity activity ) {
		String desc = special.getInstructions();
		desc = StringUtils.replace(desc, "${code}", activity.getCouponCode());
		desc = StringUtils.replace(
				desc,
				"${limit}",
				(new SimpleDateFormat("dd/MM/yyyy").format(activity.getLimitDateTime()))
						+ " a las "
						+ new SimpleDateFormat("HH:mm").format(activity.getLimitDateTime()));
		return desc;
	}
	
	@Override
	public boolean hasAvailabilityForDate(CampaignAction campaignAction, Date date ) throws ASException {
		if(campaignActivityDao.hasAvailabilityForDate(campaignAction, date)) {
			long timeDiff = (long)(campaignAction.getTimezone() * 3600000);
			Date today = DateUtils.truncate(new Date(date.getTime() + timeDiff), Calendar.DATE);
			Date todayWithTime = new Date(date.getTime() + timeDiff);
			
			log.log(Level.INFO, "today is " + today);
			log.log(Level.INFO, "todayWithTime is " + todayWithTime);
			
			//TODO: Reorganize this same logic in Offers
			if ((DateUtils.truncate(campaignAction.getValidFrom(),	Calendar.DATE).before(today) 
					|| DateUtils.truncate(campaignAction.getValidFrom(), Calendar.DATE).equals(today))
					&& 
					(DateUtils.truncate(campaignAction.getValidTo(), Calendar.DATE).after(today) 
					|| DateUtils.truncate(campaignAction.getValidTo(), Calendar.DATE).equals(today))) {
				try {
					// Gets day of week
					Calendar c = Calendar.getInstance();
					c.setTime(today);
					int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
					String dayCode = null;
					switch(dayOfWeek) {
					case Calendar.MONDAY:
						dayCode = "0";
						break;
					case Calendar.TUESDAY:
						dayCode = "1";
						break;
					case Calendar.WEDNESDAY:
						dayCode = "2";
						break;
					case Calendar.THURSDAY:
						dayCode = "3";
						break;
					case Calendar.FRIDAY:
						dayCode = "4";
						break;
					case Calendar.SATURDAY:
						dayCode = "5";
						break;
					case Calendar.SUNDAY:
						dayCode = "6";
						break;
					}

					// First we do a day check
					if( dayCode == null ) {
						log.log(Level.INFO, "dayCode is null");
						return false;
					}
					if(!campaignAction.getNotifyDays().contains(dayCode)) {
						log.log(Level.INFO, "campaignAction has not a notify day in daycode " + dayCode);
						return false;
					}

					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
					String hourCode = sdf.format(todayWithTime);
					if (campaignAction.getNotifyFromHour().compareTo(hourCode) <= 0
							&& campaignAction.getNotifyToHour().compareTo(hourCode) >= 0) {
						return true;
					} else {
						log.log(Level.INFO,
								"OK for this day... but not in hour... "
										+ hourCode + ", fromHour "
										+ campaignAction.getNotifyFromHour()
										+ ", toHour "
										+ campaignAction.getNotifyToHour());
						return false;
					}
					
				} catch( Throwable t ) {
					log.log(Level.SEVERE, t.getMessage(), t);
					return false;
				}
				
			} else {
				log.log(Level.INFO, "Out of date");
				return false;
			}
		} else {
			log.log(Level.INFO, "Getting out from campaignActivityDao.hasAvailabilityForDate(campaignAction, date)");
			return false;
		}
	}
	
	@Override
	public boolean hasSpecialBeenUsedForUserAndDate(CampaignAction campaignAction, User user, Date date) throws ASException {
		return campaignActivityDao.hasSpecialBeenUsedForUserAndDate(campaignAction, user, date);
	}

	@Override
	public void setNonDisplayableForUser(String userId) {
		try {
			List<Integer> statuses = Arrays.asList(new Integer[] {
					CampaignActivity.REDEEM_STATUS_DELIVERED,
					CampaignActivity.REDEEM_STATUS_ACCEPTED,
					CampaignActivity.REDEEM_STATUS_REDEEMED });

			List<CampaignActivity> list = campaignActivityDao.getUsingUserAndRedeemStatusAndRange(null, userId, statuses, true, null, null, true);
			for( CampaignActivity obj : list ) {
				try {
					if( obj.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_DELIVERED)) {
						obj.setRedeemStatus(CampaignActivity.REDEEM_STATUS_EXPIRED);
						obj.setStatusChangeDateTime(new Date());
					}
					obj.setDisplayable(false);
					campaignActivityDao.update(obj);
				} catch( ASException e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		} catch( ASException e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
