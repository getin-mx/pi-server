package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.Replicable;

@PersistenceCapable(detachable="true")
public class DeviceMessageLock implements ModelKey, Serializable, Identificable, Replicable {

	public static final Integer SCOPE_PROMOTIONS = 0; 
	public static final Integer SCOPE_GLOBAL = 1;
	public static final Integer SCOPE_THIS_PROMOTION = 2; 
	public static final Integer SCOPE_GEO = 3;
	
	private static final long serialVersionUID = 1L;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
	private Key key;

	private String deviceId;
	private String userId;
	private String entityId;
	private Integer entityKind;
	private String subEntityId;
	private Integer subEntityKind;
	private Integer scope;
	private String campaignActivityId; 
	private Date fromDate;
	private Date toDate;
	private Date lastUpdate;
	private Date creationDateTime;

	public DeviceMessageLock() {
		super();
		this.creationDateTime = new Date();
	}
	
	/**
	 * @return this entity key
	 */
	public String getIdentifier() {
		return this.getKey() != null ? this.getKey().getName() : "";
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

	@Override
	public void preStore() {
		this.lastUpdate = new Date();
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
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
	 * @return the fromDate
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return the toDate
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
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
	 * @return the entityId
	 */
	public String getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the entityKind
	 */
	public Integer getEntityKind() {
		return entityKind;
	}

	/**
	 * @param entityKind the entityKind to set
	 */
	public void setEntityKind(Integer entityKind) {
		this.entityKind = entityKind;
	}

	/**
	 * @return the scope
	 */
	public Integer getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(Integer scope) {
		this.scope = scope;
	}

	/**
	 * @return the campaignActivityId
	 */
	public String getCampaignActivityId() {
		return campaignActivityId;
	}

	/**
	 * @param campaignActivityId the campaignActivityId to set
	 */
	public void setCampaignActivityId(String campaignActivityId) {
		this.campaignActivityId = campaignActivityId;
	}

	/**
	 * @return the subEntityId
	 */
	public String getSubEntityId() {
		return subEntityId;
	}

	/**
	 * @param subEntityId the subEntityId to set
	 */
	public void setSubEntityId(String subEntityId) {
		this.subEntityId = subEntityId;
	}

	/**
	 * @return the subEntityKind
	 */
	public Integer getSubEntityKind() {
		return subEntityKind;
	}

	/**
	 * @param subEntityKind the subEntityKind to set
	 */
	public void setSubEntityKind(Integer subEntityKind) {
		this.subEntityKind = subEntityKind;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((campaignActivityId == null) ? 0 : campaignActivityId
						.hashCode());
		result = prime * result
				+ ((deviceId == null) ? 0 : deviceId.hashCode());
		result = prime * result
				+ ((entityId == null) ? 0 : entityId.hashCode());
		result = prime * result
				+ ((entityKind == null) ? 0 : entityKind.hashCode());
		result = prime * result
				+ ((fromDate == null) ? 0 : fromDate.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
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
		DeviceMessageLock other = (DeviceMessageLock) obj;
		if (campaignActivityId == null) {
			if (other.campaignActivityId != null)
				return false;
		} else if (!campaignActivityId.equals(other.campaignActivityId))
			return false;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		if (entityId == null) {
			if (other.entityId != null)
				return false;
		} else if (!entityId.equals(other.entityId))
			return false;
		if (entityKind == null) {
			if (other.entityKind != null)
				return false;
		} else if (!entityKind.equals(other.entityKind))
			return false;
		if (fromDate == null) {
			if (other.fromDate != null)
				return false;
		} else if (!fromDate.equals(other.fromDate))
			return false;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeviceMessageLock [key=" + key + ", deviceId=" + deviceId
				+ ", userId=" + userId + ", entityId=" + entityId
				+ ", entityKind=" + entityKind + ", subEntityId=" + subEntityId
				+ ", subEntityKind=" + subEntityKind + ", scope=" + scope
				+ ", campaignActivityId=" + campaignActivityId + ", fromDate="
				+ fromDate + ", toDate=" + toDate + ", lastUpdate="
				+ lastUpdate + ", creationDateTime=" + creationDateTime + "]";
	}

}
