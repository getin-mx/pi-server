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
import mobi.allshoppings.model.interfaces.StatusAware;

@PersistenceCapable(detachable="true")
public class DeviceInfo implements ModelKey, Serializable, Identificable, StatusAware, Replicable {

	private static final long serialVersionUID = 1L;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
	private Key key;
	
	private String userId;
	private String userName;
	private String deviceName;
	private String devicePlatform;
	private String deviceVersion;
	private String deviceUUID;
	private String appVersion;
	private String apiVersion;
	private String messagingToken;
	private boolean messagingSandbox;
	private String lang;
	private Date lastUpdate;
	private Date creationDateTime;
	private byte status;
	private String appId;
	private String mac;
	
	public DeviceInfo() {
		this.creationDateTime = new Date();
		this.status = StatusAware.STATUS_ENABLED;
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
	 * @return the deviceName
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * @param deviceName the deviceName to set
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	/**
	 * @return the devicePlatform
	 */
	public String getDevicePlatform() {
		return devicePlatform;
	}

	/**
	 * @param devicePlatform the devicePlatform to set
	 */
	public void setDevicePlatform(String devicePlatform) {
		this.devicePlatform = devicePlatform;
	}

	/**
	 * @return the deviceVersion
	 */
	public String getDeviceVersion() {
		return deviceVersion;
	}

	/**
	 * @param deviceVersion the deviceVersion to set
	 */
	public void setDeviceVersion(String deviceVersion) {
		this.deviceVersion = deviceVersion;
	}

	/**
	 * @return the appVersion
	 */
	public String getAppVersion() {
		return appVersion;
	}

	/**
	 * @param appVersion the appVersion to set
	 */
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	/**
	 * @return the messagingToken
	 */
	public String getMessagingToken() {
		return messagingToken;
	}

	/**
	 * @param messagingToken the messagingToken to set
	 */
	public void setMessagingToken(String messagingToken) {
		this.messagingToken = messagingToken;
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
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * @param lang the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	public void setMessagingSandbox(Boolean messagingSandbox) {
		this.messagingSandbox = messagingSandbox;
	}

	@Override
	public void preStore() {
		lastUpdate = new Date();
	}

	/**
	 * @return the status
	 */
	public byte getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(byte status) {
		this.status = status;
	}

	/**
	 * @return the apiVersion
	 */
	public String getApiVersion() {
		return apiVersion;
	}

	/**
	 * @param apiVersion the apiVersion to set
	 */
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	/**
	 * @return the messagingSandbox
	 */
	public Boolean getMessagingSandbox() {
		return messagingSandbox;
	}

	/**
	 * @return the mac
	 */
	public String getMac() {
		return mac;
	}

	/**
	 * @param mac the mac to set
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
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
		DeviceInfo other = (DeviceInfo) obj;
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
		return "DeviceInfo [key=" + key + ", userId=" + userId + ", userName="
				+ userName + ", deviceName=" + deviceName + ", devicePlatform="
				+ devicePlatform + ", deviceVersion=" + deviceVersion
				+ ", deviceUUID=" + deviceUUID + ", appVersion=" + appVersion
				+ ", apiVersion=" + apiVersion + ", messagingToken="
				+ messagingToken + ", messagingSandbox=" + messagingSandbox
				+ ", lang=" + lang + ", lastUpdate=" + lastUpdate
				+ ", creationDateTime=" + creationDateTime + ", status="
				+ status + ", appId=" + appId + ", mac=" + mac + "]";
	}

}
