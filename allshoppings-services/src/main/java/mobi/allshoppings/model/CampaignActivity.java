package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import mobi.allshoppings.model.adapter.IAdaptable;
import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.Replicable;

import org.apache.commons.lang.time.DateUtils;

import com.inodes.datanucleus.model.Key;
import com.inodes.datanucleus.model.Text;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class CampaignActivity implements ModelKey, IAdaptable, Serializable, Identificable, Replicable {

	public static final String FILTER_ACTIVE_ONLY = "offersOnlyActive";

	public static final int REDEEM_STATUS_DELIVERED = 0;
	public static final int REDEEM_STATUS_ACCEPTED = 1;
	public static final int REDEEM_STATUS_REJECTED = 2;
	public static final int REDEEM_STATUS_REDEEMED = 3;
	public static final int REDEEM_STATUS_EXPIRED = 4;
	public static final int REDEEM_STATUS_RULE_REJECTED = 5;

	public static final int REJECT_ALREADY_BOUGHT = 0;
	public static final int REJECT_OTHER_PLANS = 1;
	public static final int REJECT_DONT_LIKE = 2;
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	private String campaignSpecialId;
	private String campaignId;
	private String checkinId;
	private String deviceUUID;
	private String userId;
	private String shoppingId;
	private String brandId;
	private String financialEntityId;
	private Date creationDateTime;
	private Date creationDate;
	private Date limitDateTime;
	private Date lastUpdate;
	private String couponCode;
	private Integer redeemStatus;
	private Date statusChangeDateTime;
	private Date redeemDateTime;
	private Date viewDateTime;
	private Text extras;
	
	@Persistent(defaultFetchGroup = "true")
	private List<String> rejectionMotives;

	private String customUrl;
	private Boolean displayable;
	private String promotionType;
	
    public CampaignActivity() {
    	this.creationDateTime = new Date();
    	this.creationDate = DateUtils.truncate(creationDateTime, Calendar.DATE);
    	this.rejectionMotives = new ArrayList<String>();
    	this.extras = new Text("");
    	this.displayable = true;
    }

	/**
	 * @return the key
	 */
	public Key getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(Key key) {
		this.key = key;
	}

	/**
	 * @return this entity key
	 */
	public String getIdentifier() {
		return this.getKey() != null ? this.getKey().getName() : "";
	}

	/**
	 * Pre store information to assign index values
	 */
	@Override
	public void preStore() {
		this.lastUpdate = new Date();
		if( redeemStatus == null ) redeemStatus = REDEEM_STATUS_DELIVERED; 
	}

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
	 * @return the campaignId
	 */
	public String getCampaignId() {
		return campaignId;
	}

	/**
	 * @param campaignId the campaignId to set
	 */
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the shoppingId
	 */
	public String getShoppingId() {
		return shoppingId;
	}

	/**
	 * @param shoppingId the shoppingId to set
	 */
	public void setShoppingId(String shoppingId) {
		this.shoppingId = shoppingId;
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
	 * @return the financialEntityId
	 */
	public String getFinancialEntityId() {
		return financialEntityId;
	}

	/**
	 * @param financialEntityId the financialEntityId to set
	 */
	public void setFinancialEntityId(String financialEntityId) {
		this.financialEntityId = financialEntityId;
	}

	/**
	 * @return the creationDateTime
	 */
	public Date getCreationDateTime() {
		return creationDateTime;
	}

	/**
	 * @param creationDateTime the creationDateTime to set
	 */
	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the limitDateTime
	 */
	public Date getLimitDateTime() {
		return limitDateTime;
	}

	/**
	 * @param limitDateTime the limitDateTime to set
	 */
	public void setLimitDateTime(Date limitDateTime) {
		this.limitDateTime = limitDateTime;
	}

	/**
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * @return the couponCode
	 */
	public String getCouponCode() {
		return couponCode;
	}

	/**
	 * @param couponCode the couponCode to set
	 */
	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	/**
	 * @return the checkinId
	 */
	public String getCheckinId() {
		return checkinId;
	}

	/**
	 * @param checkinId the checkinId to set
	 */
	public void setCheckinId(String checkinId) {
		this.checkinId = checkinId;
	}

	/**
	 * @return the extras
	 */
	public Text getExtras() {
		return extras;
	}

	/**
	 * @param extras the extras to set
	 */
	public void setExtras(Text extras) {
		this.extras = extras;
	}
	
	/**
	 * @return the redeemStatus
	 */
	public Integer getRedeemStatus() {
		return redeemStatus;
	}

	/**
	 * @param redeemStatus the redeemStatus to set
	 */
	public void setRedeemStatus(Integer redeemStatus) {
		this.redeemStatus = redeemStatus;
	}

	/**
	 * @return the statusChangeDateTime
	 */
	public Date getStatusChangeDateTime() {
		return statusChangeDateTime;
	}

	/**
	 * @param statusChangeDateTime the statusChangeDateTime to set
	 */
	public void setStatusChangeDateTime(Date statusChangeDateTime) {
		this.statusChangeDateTime = statusChangeDateTime;
	}

	/**
	 * @return the redeemDateTime
	 */
	public Date getRedeemDateTime() {
		return redeemDateTime;
	}

	/**
	 * @param redeemDateTime the redeemDateTime to set
	 */
	public void setRedeemDateTime(Date redeemDateTime) {
		this.redeemDateTime = redeemDateTime;
	}

	/**
	 * @return the rejectionMotives
	 */
	public List<String> getRejectionMotives() {
		return rejectionMotives;
	}

	/**
	 * @param rejectionMotives the rejectionMotives to set
	 */
	public void setRejectionMotives(List<String> rejectionMotives) {
		this.rejectionMotives = rejectionMotives;
	}

	/**
	 * @return the customUrl
	 */
	public String getCustomUrl() {
		return customUrl;
	}

	/**
	 * @param customUrl the customUrl to set
	 */
	public void setCustomUrl(String customUrl) {
		this.customUrl = customUrl;
	}

	/**
	 * @return the deviceUUID
	 */
	public String getDeviceUUID() {
		return deviceUUID;
	}

	/**
	 * @param deviceUUID the deviceUUID to set
	 */
	public void setDeviceUUID(String deviceUUID) {
		this.deviceUUID = deviceUUID;
	}

	/**
	 * @return the displayable
	 */
	public Boolean getDisplayable() {
		return displayable;
	}

	/**
	 * @param displayable the displayable to set
	 */
	public void setDisplayable(Boolean displayable) {
		this.displayable = displayable;
	}

	/**
	 * @return the promotionType
	 */
	public String getPromotionType() {
		return promotionType;
	}

	/**
	 * @param promotionType the promotionType to set
	 */
	public void setPromotionType(String promotionType) {
		this.promotionType = promotionType;
	}

	/**
	 * @return the viewDateTime
	 */
	public Date getViewDateTime() {
		return viewDateTime;
	}

	/**
	 * @param viewDateTime the viewDateTime to set
	 */
	public void setViewDateTime(Date viewDateTime) {
		this.viewDateTime = viewDateTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CampaignActivity other = (CampaignActivity) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CampaignActivity [key=" + key + ", campaignSpecialId="
				+ campaignSpecialId + ", campaignId=" + campaignId
				+ ", checkinId=" + checkinId + ", deviceUUID=" + deviceUUID
				+ ", userId=" + userId + ", shoppingId=" + shoppingId
				+ ", brandId=" + brandId + ", financialEntityId="
				+ financialEntityId + ", creationDateTime=" + creationDateTime
				+ ", creationDate=" + creationDate + ", limitDateTime="
				+ limitDateTime + ", lastUpdate=" + lastUpdate
				+ ", couponCode=" + couponCode + ", redeemStatus="
				+ redeemStatus + ", statusChangeDateTime="
				+ statusChangeDateTime + ", redeemDateTime=" + redeemDateTime
				+ ", viewDateTime=" + viewDateTime + ", extras=" + extras
				+ ", rejectionMotives=" + rejectionMotives + ", customUrl="
				+ customUrl + ", displayable=" + displayable
				+ ", promotionType=" + promotionType + "]";
	}
}
