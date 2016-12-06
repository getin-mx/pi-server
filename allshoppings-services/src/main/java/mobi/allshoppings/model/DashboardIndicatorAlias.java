package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;

import com.inodes.datanucleus.model.Key;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class DashboardIndicatorAlias implements ModelKey, Serializable, Identificable {
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	public String entityId;
	public Integer entityKind;
	public String screenName;
	public String elementId;
	public String elementName;
	public String elementSubId;
	public String elementSubName;
	public String subentityId;
	public String subentityName;
	
	public Date creationDateTime;
	public Date lastUpdate;
	
	public DashboardIndicatorAlias() {
		super();
		this.creationDateTime = new Date();
	}
	
	/**
	 * @param entityId
	 * @param entityKind
	 * @param elementId
	 * @param elementSubId
	 * @param subentityId
	 */
	public DashboardIndicatorAlias(String entityId, Integer entityKind, String elementId, String elementSubId) {
		this();
		this.entityId = entityId;
		this.entityKind = entityKind;
		this.elementId = elementId;
		this.elementSubId = elementSubId;
	}
	
	/**
	 * @param entityId
	 * @param entityKind
	 * @param screenName
	 * @param elementId
	 * @param elementName
	 * @param elementSubId
	 * @param elementSubName
	 * @param subentityId
	 * @param subentityName
	 */
	public DashboardIndicatorAlias(String entityId, Integer entityKind,
			String screenName, String elementId, String elementName,
			String elementSubId, String elementSubName, String subentityId,
			String subentityName) {
		this(entityId, entityKind, elementId, elementSubId);
		this.screenName = screenName;
		this.elementName = elementName;
		this.elementSubName = elementSubName;
		this.subentityName = subentityName;
		this.subentityId = subentityId;
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
	}

	/**
	 * Sets last update parameter
	 */
	@Override
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * Obtains last update parameter
	 */
	@Override
	public Date getLastUpdate() {
		return lastUpdate;
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
	 * @return the screenName
	 */
	public String getScreenName() {
		return screenName;
	}

	/**
	 * @param screenName the screenName to set
	 */
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	/**
	 * @return the elementId
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * @param elementId the elementId to set
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/**
	 * @return the elementName
	 */
	public String getElementName() {
		return elementName;
	}

	/**
	 * @param elementName the elementName to set
	 */
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	/**
	 * @return the elementSubId
	 */
	public String getElementSubId() {
		return elementSubId;
	}

	/**
	 * @param elementSubId the elementSubId to set
	 */
	public void setElementSubId(String elementSubId) {
		this.elementSubId = elementSubId;
	}

	/**
	 * @return the elementSubName
	 */
	public String getElementSubName() {
		return elementSubName;
	}

	/**
	 * @param elementSubName the elementSubName to set
	 */
	public void setElementSubName(String elementSubName) {
		this.elementSubName = elementSubName;
	}

	/**
	 * @return the subentityId
	 */
	public String getSubentityId() {
		return subentityId;
	}

	/**
	 * @param subentityId the subentityId to set
	 */
	public void setSubentityId(String subentityId) {
		this.subentityId = subentityId;
	}

	/**
	 * @return the subentityName
	 */
	public String getSubentityName() {
		return subentityName;
	}

	/**
	 * @param subentityName the subentityName to set
	 */
	public void setSubentityName(String subentityName) {
		this.subentityName = subentityName;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((elementId == null) ? 0 : elementId.hashCode());
		result = prime * result
				+ ((elementSubId == null) ? 0 : elementSubId.hashCode());
		result = prime * result
				+ ((entityId == null) ? 0 : entityId.hashCode());
		result = prime * result
				+ ((entityKind == null) ? 0 : entityKind.hashCode());
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
		DashboardIndicatorAlias other = (DashboardIndicatorAlias) obj;
		if (elementId == null) {
			if (other.elementId != null)
				return false;
		} else if (!elementId.equals(other.elementId))
			return false;
		if (elementSubId == null) {
			if (other.elementSubId != null)
				return false;
		} else if (!elementSubId.equals(other.elementSubId))
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
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DashboardIndicatorAlias [key=" + key + ", entityId=" + entityId
				+ ", entityKind=" + entityKind + ", screenName=" + screenName
				+ ", elementId=" + elementId + ", elementName=" + elementName
				+ ", elementSubId=" + elementSubId + ", elementSubName="
				+ elementSubName + ", subentityId=" + subentityId
				+ ", subentityName=" + subentityName + ", creationDateTime="
				+ creationDateTime + ", lastUpdate=" + lastUpdate + "]";
	}

}
