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
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class ExportUnit implements ModelKey, Serializable, Identificable, StatusAware {

	public static final int TARGET_MYSQL = 0;
	
	public static final int SOURCE_VISITS = 0;
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	private String name;
	private String description;
	
	private Integer status;

	@Persistent(defaultFetchGroup = "true")
	private List<String> entityIds;
	private Integer entityKind;

	private Boolean hideMac = false;
	private Integer sourceType;
	private Integer targetType;
	private String targetURL;
	private String targetDBName;
	private String targetUser;
	private String targetPassword;
	
	private Date creationDateTime;
	private Date lastUpdate;

    public ExportUnit() {
		this.creationDateTime = new Date();
		this.entityIds = CollectionFactory.createList();
		this.hideMac = false;
		this.status = StatusAware.STATUS_ENABLED;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return the entityIds
	 */
	public List<String> getEntityIds() {
		return entityIds;
	}

	/**
	 * @param entityIds the entityIds to set
	 */
	public void setEntityIds(List<String> entityIds) {
		this.entityIds = entityIds;
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
	 * @return the targetType
	 */
	public Integer getTargetType() {
		return targetType;
	}

	/**
	 * @param targetType the targetType to set
	 */
	public void setTargetType(Integer targetType) {
		this.targetType = targetType;
	}

	/**
	 * @return the sourceType
	 */
	public Integer getSourceType() {
		return sourceType;
	}

	/**
	 * @param sourceType the sourceType to set
	 */
	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}

	/**
	 * @return the targetURL
	 */
	public String getTargetURL() {
		return targetURL;
	}

	/**
	 * @param targetURL the targetURL to set
	 */
	public void setTargetURL(String targetURL) {
		this.targetURL = targetURL;
	}

	/**
	 * @return the targetUser
	 */
	public String getTargetUser() {
		return targetUser;
	}

	/**
	 * @param targetUser the targetUser to set
	 */
	public void setTargetUser(String targetUser) {
		this.targetUser = targetUser;
	}

	/**
	 * @return the targetPassword
	 */
	public String getTargetPassword() {
		return targetPassword;
	}

	/**
	 * @param targetPassword the targetPassword to set
	 */
	public void setTargetPassword(String targetPassword) {
		this.targetPassword = targetPassword;
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
	 * @return the hideMac
	 */
	public Boolean getHideMac() {
		return hideMac;
	}

	/**
	 * @param hideMac the hideMac to set
	 */
	public void setHideMac(Boolean hideMac) {
		this.hideMac = hideMac;
	}

	/**
	 * @return the targetDBName
	 */
	public String getTargetDBName() {
		return targetDBName;
	}

	/**
	 * @param targetDBName the targetDBName to set
	 */
	public void setTargetDBName(String targetDBName) {
		this.targetDBName = targetDBName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		ExportUnit other = (ExportUnit) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExportUnit [key=" + key + ", name=" + name + ", description=" + description + ", status=" + status
				+ ", entityIds=" + entityIds + ", entityKind=" + entityKind + ", hideMac=" + hideMac + ", sourceType="
				+ sourceType + ", targetType=" + targetType + ", targetURL=" + targetURL + ", targetDBName="
				+ targetDBName + ", targetUser=" + targetUser + ", targetPassword=" + targetPassword
				+ ", creationDateTime=" + creationDateTime + ", lastUpdate=" + lastUpdate + "]";
	}
}
