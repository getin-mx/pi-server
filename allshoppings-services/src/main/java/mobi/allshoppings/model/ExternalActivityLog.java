package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.Replicable;
import mobi.allshoppings.tools.CollectionFactory;

@PersistenceCapable(detachable="true")
public class ExternalActivityLog implements ModelKey, Serializable, Identificable, Replicable {

	public static final Integer SCOPE_PROMOTIONS = 0; 
	public static final Integer SCOPE_GLOBAL = 1;
	public static final Integer SCOPE_THIS_PROMOTION = 2; 
	public static final Integer SCOPE_GEO = 3;
	
	private static final long serialVersionUID = 1L;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
	private Key key;

	private String campaignActionId;
	private String entityId;
	private String description;

	@Persistent(defaultFetchGroup = "true")
	private List<String> suggestedDevices;
	private Integer suggestedDevicesCount;

	@Persistent(defaultFetchGroup = "true")
	private List<String> lockedDevices;
	private Integer lockedDevicesCount;

	@Persistent(defaultFetchGroup = "true")
	private List<String> unavailableDevices;
	private Integer unavailableDevicesCount;

	@Persistent(defaultFetchGroup = "true")
	private List<String> sentDevices;
	private Integer sentDevicesCount;

	@Persistent(defaultFetchGroup = "true")
	private List<String> failedDevices;
	private Integer failedDevicesCount;
	
	private Date lastUpdate;
	private Date creationDateTime;

	public ExternalActivityLog() {
		super();
		this.creationDateTime = new Date();
		suggestedDevices = CollectionFactory.createList();
		lockedDevices = CollectionFactory.createList();
		unavailableDevices = CollectionFactory.createList();
		sentDevices = CollectionFactory.createList();
		failedDevices = CollectionFactory.createList();
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the suggestedDevices
	 */
	public List<String> getSuggestedDevices() {
		return suggestedDevices;
	}

	/**
	 * @param suggestedDevices the suggestedDevices to set
	 */
	public void setSuggestedDevices(List<String> suggestedDevices) {
		this.suggestedDevices = suggestedDevices;
	}

	/**
	 * @return the suggestedDevicesCount
	 */
	public Integer getSuggestedDevicesCount() {
		return suggestedDevicesCount;
	}

	/**
	 * @param suggestedDevicesCount the suggestedDevicesCount to set
	 */
	public void setSuggestedDevicesCount(Integer suggestedDevicesCount) {
		this.suggestedDevicesCount = suggestedDevicesCount;
	}

	/**
	 * @return the lockedDevices
	 */
	public List<String> getLockedDevices() {
		return lockedDevices;
	}

	/**
	 * @param lockedDevices the lockedDevices to set
	 */
	public void setLockedDevices(List<String> lockedDevices) {
		this.lockedDevices = lockedDevices;
	}

	/**
	 * @return the lockedDevicesCount
	 */
	public Integer getLockedDevicesCount() {
		return lockedDevicesCount;
	}

	/**
	 * @param lockedDevicesCount the lockedDevicesCount to set
	 */
	public void setLockedDevicesCount(Integer lockedDevicesCount) {
		this.lockedDevicesCount = lockedDevicesCount;
	}

	/**
	 * @return the unavailableDevices
	 */
	public List<String> getUnavailableDevices() {
		return unavailableDevices;
	}

	/**
	 * @param unavailableDevices the unavailableDevices to set
	 */
	public void setUnavailableDevices(List<String> unavailableDevices) {
		this.unavailableDevices = unavailableDevices;
	}

	/**
	 * @return the unavailableDevicesCount
	 */
	public Integer getUnavailableDevicesCount() {
		return unavailableDevicesCount;
	}

	/**
	 * @param unavailableDevicesCount the unavailableDevicesCount to set
	 */
	public void setUnavailableDevicesCount(Integer unavailableDevicesCount) {
		this.unavailableDevicesCount = unavailableDevicesCount;
	}

	/**
	 * @return the sentDevices
	 */
	public List<String> getSentDevices() {
		return sentDevices;
	}

	/**
	 * @param sentDevices the sentDevices to set
	 */
	public void setSentDevices(List<String> sentDevices) {
		this.sentDevices = sentDevices;
	}

	/**
	 * @return the sentDevicesCount
	 */
	public Integer getSentDevicesCount() {
		return sentDevicesCount;
	}

	/**
	 * @param sentDevicesCount the sentDevicesCount to set
	 */
	public void setSentDevicesCount(Integer sentDevicesCount) {
		this.sentDevicesCount = sentDevicesCount;
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
	 * @return the failedDevices
	 */
	public List<String> getFailedDevices() {
		return failedDevices;
	}

	/**
	 * @param failedDevices the failedDevices to set
	 */
	public void setFailedDevices(List<String> failedDevices) {
		this.failedDevices = failedDevices;
	}

	/**
	 * @return the failedDevicesCount
	 */
	public Integer getFailedDevicesCount() {
		return failedDevicesCount;
	}

	/**
	 * @param failedDevicesCount the failedDevicesCount to set
	 */
	public void setFailedDevicesCount(Integer failedDevicesCount) {
		this.failedDevicesCount = failedDevicesCount;
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
		ExternalActivityLog other = (ExternalActivityLog) obj;
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
		return "ExternalActivityLog [key=" + key + ", campaignActionId="
				+ campaignActionId + ", entityId=" + entityId
				+ ", description=" + description + ", suggestedDevices="
				+ suggestedDevices + ", suggestedDevicesCount="
				+ suggestedDevicesCount + ", lockedDevices=" + lockedDevices
				+ ", lockedDevicesCount=" + lockedDevicesCount
				+ ", unavailableDevices=" + unavailableDevices
				+ ", unavailableDevicesCount=" + unavailableDevicesCount
				+ ", sentDevices=" + sentDevices + ", sentDevicesCount="
				+ sentDevicesCount + ", failedDevices=" + failedDevices
				+ ", failedDevicesCount=" + failedDevicesCount
				+ ", lastUpdate=" + lastUpdate + ", creationDateTime="
				+ creationDateTime + "]";
	}

}
