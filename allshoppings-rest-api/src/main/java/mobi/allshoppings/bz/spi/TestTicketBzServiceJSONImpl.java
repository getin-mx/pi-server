package mobi.allshoppings.bz.spi;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.TestTicketBzService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.SystemConfiguration;


/**
 *
 */
public class TestTicketBzServiceJSONImpl
extends RestBaseServerResource
implements TestTicketBzService {

	private static final Logger log = Logger.getLogger(TestTicketBzServiceJSONImpl.class.getName());

	@Autowired
	private SystemConfiguration systemConfiguration;
	
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
			
			String campaignSpecialId = obj.has("campaignOfferId") ? obj.getString("campaignOfferId") : null;

			
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
