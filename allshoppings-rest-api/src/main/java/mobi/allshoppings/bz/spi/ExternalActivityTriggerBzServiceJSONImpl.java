package mobi.allshoppings.bz.spi;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.bz.ExternalActivityTriggerBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.campaign.CampaignHelper;
import mobi.allshoppings.coupon.CouponHelper;
import mobi.allshoppings.dao.CampaignActivityDAO;
import mobi.allshoppings.dao.CampaignSpecialDAO;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.ExternalActivityLogDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.image.ImageDownloader;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.CampaignSpecial;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.ExternalActivityLog;
import mobi.allshoppings.model.Image;
import mobi.allshoppings.model.User;
import mobi.allshoppings.push.PushMessageHelper;
import mobi.allshoppings.tools.CollectionFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Text;


/**
 *
 */
public class ExternalActivityTriggerBzServiceJSONImpl
extends RestBaseServerResource
implements ExternalActivityTriggerBzService {

	private static final Logger log = Logger.getLogger(ExternalActivityTriggerBzServiceJSONImpl.class.getName());

	@Autowired
	private CouponHelper couponHelper;
	@Autowired
	private DeviceInfoDAO diDao;
	@Autowired
	private UserDAO userDao;
	@Autowired
	private CampaignSpecialDAO csDao;
	@Autowired
	private CampaignActivityDAO caDao;
	@Autowired
	private ExternalActivityLogDAO eaDao;
	@Autowired
	private PushMessageHelper pushMessageHelper;
	@Autowired
	private CampaignHelper campaignHelper;
	@Autowired
	private ImageDownloader imageDownloader;

	@Override
	public String post(final JsonRepresentation entity) {
		long start = markStart();
		try {

			// validates auth token
			User app = getUserFromToken();
			final JSONObject obj = entity.getJsonObject();
			
			log.log(Level.INFO, obj.toString());
			
			final Map<String, JSONArray> deviceCoupons = CollectionFactory.createMap();
			final JSONArray longDevices = obj.getJSONArray("longDevices");
			
			for(int i = 0; i < longDevices.length(); i++ ) {
				JSONObject json = longDevices.getJSONObject(i);
				JSONArray coupons = json.getJSONArray("coupons");
				String key = json.getString("deviceUUID");
				deviceCoupons.put(key, coupons);
			}

			// Maps received information
			ExternalActivityAdapter adapter = new ExternalActivityAdapter();
			setPropertiesFromJSONObject(obj, adapter, new HashSet<String>());

			CampaignSpecial cs = csDao.get(adapter.getCampaignSpecialId());

			ExternalActivityLog eaLog = new ExternalActivityLog();
			eaLog.setKey(eaDao.createKey());
			eaLog.setCampaignSpecialId(cs.getIdentifier());
			eaLog.setEntityId(obj.has("entityId") ? obj.getString("entityId") : null);
			eaLog.setDescription(obj.has("description") ? obj.getString("description") : null);
			
			boolean ignoreLocks = (obj.has("ignoreLocks") ? obj.getBoolean("ignoreLocks") : false);
			
			// Obtains all the referred devices and starts to send activities
			List<DeviceInfo> devices = diDao.getUsingIdList(adapter.getDevices());
			for( DeviceInfo device : devices ) {
				eaLog.getSuggestedDevices().add(device.getDeviceUUID());
				
				// Checks locks for user
				try {
					if( ignoreLocks || couponHelper.deviceHasAvailability(device.getDeviceUUID(), adapter.getCampaignSpecialId())) {

						if(ignoreLocks || !couponHelper.deviceHasActiveCoupons(device.getDeviceUUID())) {
							User user = userDao.get(device.getUserId());

							// Stops displaying all the other campaigns
							campaignHelper.setNonDisplayableForUser(device.getUserId());

							// Creates the campaign activity
							CampaignActivity ca = new CampaignActivity();
							ca.setBrandId(adapter.getBrandId());
							ca.setCampaignId(null);
							ca.setCampaignSpecialId(cs.getIdentifier());
							ca.setCheckinId(null);
							ca.setCouponCode(null);
							ca.setExtras(new Text(""));
							ca.setFinancialEntityId(null);
							ca.setLimitDateTime(new Date(new Date().getTime() + cs.getSpan()));
							ca.setRedeemStatus(CampaignActivity.REDEEM_STATUS_DELIVERED);
							ca.setShoppingId(null);
							ca.setUserId(device.getUserId());
							ca.setDeviceUUID(device.getIdentifier());
							ca.setPromotionType(cs.getPromotionType());

							ca.setKey(caDao.createKey());
							ca.setCustomUrl(campaignHelper.assingCustomUrl(cs, ca));

							JSONObject extras = obj.getJSONObject("extras");
							extras.put("suggestedCoupons", deviceCoupons.get(device.getIdentifier()));
							
							// Custom image processing
							if(StringUtils.hasText(adapter.getImageUrl())) {
								// And Brings the user Picture
								try {
									Image image = imageDownloader.downloadImage(adapter.getImageUrl(), cs.getKey());
									extras.put("avatarId", image.getIdentifier());
								} catch( Exception e ) {
									log.log(Level.WARNING, e.getMessage(), e);
								}
							}

							ca.setExtras(new Text(extras.toString()));

							caDao.create(ca);

							String action = adapter.getActionPrepend() == null ? "notification/" : adapter.getActionPrepend() + ca.getIdentifier();

							// Sends notification
							String message = cs.getName();
							pushMessageHelper.sendNotification(user, app.getFirstname(),
									cs.getAvatarId(), message, action, device,
									ca.getIdentifier(), EntityKind.KIND_CAMPAIGN_ACTIVITY);
							
							eaLog.getSentDevices().add(device.getDeviceUUID());

						} else {
							eaLog.getUnavailableDevices().add(device.getDeviceUUID());
							log.log(Level.INFO, "User " + device.getUserId() + " currently has active coupons");
						}
					} else {
						eaLog.getLockedDevices().add(device.getDeviceUUID());
						log.log(Level.INFO, "User " + device.getUserId() + " currently has locks");
					}
				} catch( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}

			eaDao.create(eaLog);
			
			// finally returns the result
			return generateJSONOkResponse().toString();

		} catch (JSONException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} catch (ASException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(e).toString();
		} finally {
			markEnd(start);
		}
	}

	public class ExternalActivityAdapter {
		private String campaignSpecialId;
		private String actionPrepend;
		private String brandId;
		private List<String> devices;
		private String imageUrl;

		/**
		 * @return the campaignSpecialId
		 */
		public String getCampaignSpecialId() {
			return campaignSpecialId;
		}
		/**
		 * @param campaignSpecialId the campaignSpecialId to set
		 */
		public void setCampaignSpecialId(String campaignSpecialId) {
			this.campaignSpecialId = campaignSpecialId;
		}
		/**
		 * @return the actionPrepend
		 */
		public String getActionPrepend() {
			return actionPrepend;
		}
		/**
		 * @param actionPrepend the actionPrepend to set
		 */
		public void setActionPrepend(String actionPrepend) {
			this.actionPrepend = actionPrepend;
		}
		/**
		 * @return the devices
		 */
		public List<String> getDevices() {
			return devices;
		}
		/**
		 * @param devices the devices to set
		 */
		public void setDevices(List<String> devices) {
			this.devices = devices;
		}
		/**
		 * @return the brandId
		 */
		public String getBrandId() {
			return brandId;
		}
		/**
		 * @param brandId the brandId to set
		 */
		public void setBrandId(String brandId) {
			this.brandId = brandId;
		}
		/**
		 * @return the imageUrl
		 */
		public String getImageUrl() {
			return imageUrl;
		}
		/**
		 * @param imageUrl the imageUrl to set
		 */
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ExternalActivityAdapter [campaignSpecialId="
					+ campaignSpecialId + ", actionPrepend=" + actionPrepend
					+ ", brandId=" + brandId + ", devices=" + devices
					+ ", imageUrl=" + imageUrl + "]";
		}
	}
}
