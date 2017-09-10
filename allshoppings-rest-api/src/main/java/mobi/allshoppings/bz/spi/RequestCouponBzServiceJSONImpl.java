package mobi.allshoppings.bz.spi;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import mobi.allshoppings.bz.RequestCouponBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.CampaignActionDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.CampaignAction;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class RequestCouponBzServiceJSONImpl
extends RestBaseServerResource
implements RequestCouponBzService {

	private static final Logger log = Logger.getLogger(RequestCouponBzServiceJSONImpl.class.getName());

	@Autowired
	private SystemConfiguration systemConfiguration;

	@Autowired
	private CampaignActionDAO csDao;
	
	@Override
	public String post(final JsonRepresentation entity) {
		long start = markStart();
		try {

			// validate authToken
			this.getUserFromToken();
			final JSONObject obj = entity.getJsonObject();

			// get level, if not defined use default value
			String deviceUUID = obj.getString("deviceUUID");
			List<String> devices = Arrays.asList(new String[] {deviceUUID});
			
			String campaignActionId = obj.has("campaignOfferId") ? obj.getString("campaignOfferId") : null;
			@SuppressWarnings("unused")
			boolean test = obj.has("test") ? obj.getBoolean("test") : false;

			{ // Generic coupon implementation 

				RestTemplate restTemplate = new RestTemplate();
				HttpMessageConverter<?> formHttpMessageConverter = new MappingJackson2HttpMessageConverter();
				HttpMessageConverter<?> stringHttpMessageConverternew = new StringHttpMessageConverter();
				restTemplate.setMessageConverters(Arrays.asList(new HttpMessageConverter<?>[]{formHttpMessageConverter, stringHttpMessageConverternew}));
				
				CampaignAction cs = csDao.get(campaignActionId, true);

				List<Map<String, Object>> longDevices = CollectionFactory.createList();
				for( String device : devices ) {
					Map<String, Object> dev = CollectionFactory.createMap();
					List<String> coupons = CollectionFactory.createList();
					coupons.add("00000000001");
					dev.put("deviceUUID", device);
					dev.put("coupons", coupons);
					longDevices.add(dev);
				}

				Map<String, Object> parms = CollectionFactory.createMap();
				parms.put("campaignActionId", cs.getIdentifier());
				parms.put("ignoreLocks", true);
				parms.put("disableOlder", false);
				parms.put("devices", devices);
				parms.put("longDevices", longDevices);

				
				// Sends the data to the server
				int maxRetries = 3;
				while( maxRetries > 0 ) {
					try {
						Object result = restTemplate.postForObject(systemConfiguration.getExternalActivityTriggerURL()
								+ "?authToken=" + this.getParameters().get("authToken"), parms, Object.class);
						System.out.println(result);
						if( result.toString().contains("status=500")) {
							maxRetries--;
						} else {
							maxRetries = 0;
						}
					} catch( Exception e ) {
						maxRetries--;
						try {Thread.sleep(1000);} catch(Exception e1){}
					}
				}
				
			}
			
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
		private String campaignActionId;
		private String actionPrepend;
		private String brandId;
		private List<String> devices;
		private String imageUrl;

		/**
		 * @return the campaignActionId
		 */
		public String getCampaignActionId() {
			return campaignActionId;
		}
		/**
		 * @param campaignActionId the campaignActionId to set
		 */
		public void setCampaignActionId(String campaignActionId) {
			this.campaignActionId = campaignActionId;
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
			return "ExternalActivityAdapter [campaignActionId="
					+ campaignActionId + ", actionPrepend=" + actionPrepend
					+ ", brandId=" + brandId + ", devices=" + devices
					+ ", imageUrl=" + imageUrl + "]";
		}
	}
}
