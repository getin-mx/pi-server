package mobi.allshoppings.bz.spi;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Text;

import mobi.allshoppings.bz.CouponBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.coupon.CouponHelper;
import mobi.allshoppings.dao.CampaignActivityDAO;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.lock.LockHelper;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.DeviceMessageLock;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.adapter.CouponAdapter;
import mobi.allshoppings.ranking.PointsService;
import mobi.allshoppings.task.QueueTaskHelper;

/**
 *
 */
public class CouponBzServiceJSONImpl extends RestBaseServerResource implements CouponBzService {

	private static final Logger log = Logger.getLogger(CouponBzServiceJSONImpl.class.getName());

	@Autowired
	private CampaignActivityDAO caDao;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private QueueTaskHelper queueTaskHelper;
	@Autowired
	private PointsService pointsService;
	@Autowired
	private CouponHelper couponHelper;
	@Autowired
	private LockHelper lockHelper;
	@Autowired
	private DeviceInfoDAO deviceInfoDao;

	private final static String COUPION_ID = "couponId";

	private BzFields bzFields = BzFields.getBzFields(getClass());

	/**
	 * Obtains information about a shopping
	 * 
	 * @return A JSON representation of the selected fields for a shopping
	 */
	@Override
	public String retrieve() {
		long start = markStart();
		JSONObject returnValue;
		try {
			// validate authToken
			User user = this.getUserFromToken();

			// obtain the id
			final String couponId = obtainIdentifier(COUPION_ID);
			CouponAdapter coupon = couponHelper.getCouponUsingActivityId(couponId, user, false, true, true, true);

			queueTaskHelper.enqueueNotificationLogReceived(user.getIdentifier(), couponId, EntityKind.KIND_OFFER);

			// track action
			trackerHelper.enqueue( user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.CouponBzService") + coupon.getName(), 
					null, null);

			// Obtains the coupon entity
			String level = obtainStringValue(LEVEL, systemConfiguration.getDefaultLevel());
			coupon.setPoints(user.getPoints());

			// Obtains the user JSON representation
			final String[] fields = obtainOutputFields(bzFields, level);
			returnValue = getJSONRepresentationFromObject(coupon, fields);

		} catch (ASException e) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE || 
					e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE) {
				log.log(Level.INFO, e.getMessage());
			} else {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			returnValue = getJSONRepresentationFromException(e);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			returnValue = getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e));
		} finally {
			markEnd(start);
		}
		return returnValue.toString();
	}

	@Override
	public String post(final JsonRepresentation entity) {
		long start = markStart();
		String method = null;
		Integer status = null;
		try {
			JSONObject obj = entity != null ? entity.getJsonObject() : new JSONObject();
			try {
				if( obj.has("method")) {
					method = obj.getString("method");
					if( method.equals("post")) status = CampaignActivity.REDEEM_STATUS_ACCEPTED;
					if( method.equals("put")) status = CampaignActivity.REDEEM_STATUS_REDEEMED;
					if( method.equals("delete")) status = CampaignActivity.REDEEM_STATUS_REJECTED;
				} else {
					method = "post";
					status = CampaignActivity.REDEEM_STATUS_ACCEPTED;
				}
			} catch( Exception e ) {
				method = "post";
				status = CampaignActivity.REDEEM_STATUS_ACCEPTED;
			}
			return changeRedeemStatus(obj, status, method);
		} catch( JSONException e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally {
			markEnd(start);
		}
	}

	@Override
	public String put(final JsonRepresentation entity) {
		try {
			return changeRedeemStatus(entity.getJsonObject(), CampaignActivity.REDEEM_STATUS_REDEEMED, "put");
		} catch(Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}

	@Override
	public String delete(final JsonRepresentation entity) {
		try {
			return changeRedeemStatus(entity.getJsonObject(), CampaignActivity.REDEEM_STATUS_REJECTED, "delete");
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}

	public String changeRedeemStatus(final JSONObject obj, Integer status, String method) {
		long start = markStart();
		long points = 0;
		JSONObject returnValue = null;
		try {
			final User user = getUserFromToken();

			final String couponId = obtainIdentifier(COUPION_ID);
			CampaignActivity ca = caDao.get(couponId);

			Integer action = 0;
			switch( status ) {
			case 3:
				ca.setRedeemStatus(status);
				action = PointsService.ACTION_COUPON_REDEEMED;
				ca.setRedeemDateTime(new Date());
				break;
			case 2:
				ca.setRedeemStatus(status);
				action = PointsService.ACTION_COUPON_REJECTED;
				ca.setStatusChangeDateTime(new Date());
				if( obj.has("rejectionMotives")) {
					if( ca.getRejectionMotives() == null ) ca.setRejectionMotives(new ArrayList<String>());
					ca.getRejectionMotives().clear();
					String[] motives = obj.getString("rejectionMotives").split(",");
					for( String motive : motives ) {
						try {
							if(!ca.getRejectionMotives().contains(motive))
								ca.getRejectionMotives().add(motive);
						} catch( Exception e ) {}
					}
					if( ca.getRejectionMotives().size() > 1 || !ca.getRejectionMotives().contains(new Integer(3))) {
						// Add restraint for default time
						lockHelper.deviceMessageLock(ca.getDeviceUUID(),
								DeviceMessageLock.SCOPE_THIS_PROMOTION,
								ca.getIdentifier(), new Date(),
								systemConfiguration.getDefaultRejectionLock(), null, null);
					}
				}
				break;
			default:
				if( ca.getLimitDateTime() != null && ca.getLimitDateTime().before(new Date())) {
					throw ASExceptionHelper.invalidArgumentsException("redeemStatus");
				}
				if( ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_EXPIRED) || ca.getRedeemStatus().equals(CampaignActivity.REDEEM_STATUS_REJECTED)) {
					throw ASExceptionHelper.invalidArgumentsException("redeemStatus");
				}

				// Add restraint for default time
				lockHelper.deviceMessageLock(ca.getDeviceUUID(),
						DeviceMessageLock.SCOPE_THIS_PROMOTION,
						ca.getIdentifier(), new Date(),
						systemConfiguration.getDefaultAcceptanceLock(), null, null);

				ca.setRedeemStatus(status);
				couponHelper.createCouponCode(ca, obj.toString());
				if( ca.getExtras() == null ) ca.setExtras(new Text(""));
				action = PointsService.ACTION_COUPON_ACCEPTED;
				ca.setStatusChangeDateTime(new Date());
			}

			if(ca.getViewDateTime() == null) ca.setViewDateTime(new Date());
			caDao.update(ca);

			// Extra coupons (extremelly util for Cinepolis!)
			returnValue = generateJSONOkResponse();
			returnValue.put("couponCode", ca.getCouponCode());
			try {
				JSONObject json = new JSONObject(ca.getExtras().getValue());
				if( json.has("extraCoupon1")) returnValue.put("extraCoupon1", json.getString("extraCoupon1"));
				if( json.has("extraCoupon2")) returnValue.put("extraCoupon2", json.getString("extraCoupon2"));
			} catch( JSONException e1 ) {}


			// Points updater
			try {
				DeviceInfo di = deviceInfoDao.get(ca.getDeviceUUID(), true);
				if(!StringUtils.hasText(di.getAppId()) ||
						systemConfiguration.getDefaultBehavioursApps().contains(di.getAppId())) {
					points = pointsService.calculatePointsForAction(user.getIdentifier(), action,
							EntityKind.KIND_CAMPAIGN_ACTIVITY, couponId, null);
					pointsService.enqueue(user.getIdentifier(), action,
							EntityKind.KIND_CAMPAIGN_ACTIVITY, couponId, null);
				}
			} catch( Exception e ) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}

			// track action
			trackerHelper.enqueue( user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.CouponBzService." + method ), 
					null, null);

		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(e).toString();
		} finally {
			markEnd(start);
		}

		return addPointsUpdate(returnValue, points).toString();

	}

}
