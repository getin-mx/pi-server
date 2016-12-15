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

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class DashboardConfiguration implements ModelKey, Serializable, Identificable {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	private Date creationDateTime;
	private Date lastUpdate;

	private String entityId;
	private Integer entityKind;
	
	private String timezone;
	private String storeLabel;
	
	public DashboardConfiguration() {
		this.creationDateTime = new Date();
    }

	public DashboardConfiguration(String entityId, Integer entityKind) {
		this();
		this.entityId = entityId;
		this.entityKind = entityKind;
		this.storeLabel = "Tienda";
		this.timezone = "SERVER";
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
	 * @return the identifier
	 */
	public String getIdentifier() {
		return this.getKey() != null ? this.getKey().getName() : "";
	}

	@Override
	public void preStore() {
		this.lastUpdate = new Date();
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
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * @param timezone the timezone to set
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	/**
	 * @return the storeLabel
	 */
	public String getStoreLabel() {
		return storeLabel;
	}

	/**
	 * @param storeLabel the storeLabel to set
	 */
	public void setStoreLabel(String storeLabel) {
		this.storeLabel = storeLabel;
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
		DashboardConfiguration other = (DashboardConfiguration) obj;
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
		return "DashboardConfiguration [key=" + key + ", creationDateTime=" + creationDateTime + ", lastUpdate="
				+ lastUpdate + ", entityId=" + entityId + ", entityKind=" + entityKind + ", timezone=" + timezone
				+ ", storeLabel=" + storeLabel + "]";
	}

}
