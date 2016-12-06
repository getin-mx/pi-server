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
@PersistenceCapable
public class PushMessageLog implements ModelKey, Serializable, Identificable {

	public static final Integer STATUS_SENDED = 0;
	public static final Integer STATUS_RECEIVED = 1;
	public static final Integer STATUS_OPENED = 2;
	public static final Integer STATUS_ERROR = 3;
	public static final Integer TYPE_MESSAGE = 0;
	public static final Integer TYPE_LOCATION = 1;
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

    private String userId;
    private String deviceUUID;

    private Date creationDateTime;
	private Date notifiactionDateTime;
    private Date receptionDateTime;
    private Date openDateTime;
    private Date lastUpdate;
    private Integer status;
    private Integer type;
	private String data;

	@Persistent(defaultFetchGroup = "true")
    private Key owner;

	public PushMessageLog() {
		creationDateTime = new Date();
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
	 * @return the notifiactionDateTime
	 */
	public Date getNotifiactionDateTime() {
		return notifiactionDateTime;
	}

	/**
	 * @param notifiactionDateTime the notifiactionDateTime to set
	 */
	public void setNotifiactionDateTime(Date notifiactionDateTime) {
		this.notifiactionDateTime = notifiactionDateTime;
	}

	/**
	 * @return the receptionDateTime
	 */
	public Date getReceptionDateTime() {
		return receptionDateTime;
	}

	/**
	 * @param receptionDateTime the receptionDateTime to set
	 */
	public void setReceptionDateTime(Date receptionDateTime) {
		this.receptionDateTime = receptionDateTime;
	}

	/**
	 * @return the openDateTime
	 */
	public Date getOpenDateTime() {
		return openDateTime;
	}

	/**
	 * @param openDateTime the openDateTime to set
	 */
	public void setOpenDateTime(Date openDateTime) {
		this.openDateTime = openDateTime;
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
	 * @return the owner
	 */
	public Key getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(Key owner) {
		this.owner = owner;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
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
				+ ((creationDateTime == null) ? 0 : creationDateTime.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result
				+ ((deviceUUID == null) ? 0 : deviceUUID.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime
				* result
				+ ((notifiactionDateTime == null) ? 0 : notifiactionDateTime
						.hashCode());
		result = prime * result
				+ ((openDateTime == null) ? 0 : openDateTime.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime
				* result
				+ ((receptionDateTime == null) ? 0 : receptionDateTime
						.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		PushMessageLog other = (PushMessageLog) obj;
		if (creationDateTime == null) {
			if (other.creationDateTime != null)
				return false;
		} else if (!creationDateTime.equals(other.creationDateTime))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (deviceUUID == null) {
			if (other.deviceUUID != null)
				return false;
		} else if (!deviceUUID.equals(other.deviceUUID))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (notifiactionDateTime == null) {
			if (other.notifiactionDateTime != null)
				return false;
		} else if (!notifiactionDateTime.equals(other.notifiactionDateTime))
			return false;
		if (openDateTime == null) {
			if (other.openDateTime != null)
				return false;
		} else if (!openDateTime.equals(other.openDateTime))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (receptionDateTime == null) {
			if (other.receptionDateTime != null)
				return false;
		} else if (!receptionDateTime.equals(other.receptionDateTime))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PushMessageLog [key=" + key + ", userId=" + userId
				+ ", deviceUUID=" + deviceUUID + ", creationDateTime="
				+ creationDateTime + ", notifiactionDateTime="
				+ notifiactionDateTime + ", receptionDateTime="
				+ receptionDateTime + ", openDateTime=" + openDateTime
				+ ", lastUpdate=" + lastUpdate + ", status=" + status
				+ ", type=" + type + ", owner=" + owner + ", data=" + data
				+ "]";
	}

}
