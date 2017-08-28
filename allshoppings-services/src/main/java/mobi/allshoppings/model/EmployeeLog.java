package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Cacheable;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;

@PersistenceCapable(detachable="true")
@Cacheable("false")
public class EmployeeLog implements ModelKey, Serializable, Identificable {

	private static final long serialVersionUID = 1L;
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
	private Key key;
	
	private String userId;
	private String deviceUUID;
	private String mac;
	private String entityId;
	private Integer entityKind;
	private Date checkinStarted;
	private Date checkinFinished;
	private Date lastUpdate;
	private Date creationDateTime;
	private String employeeId;
	private String employeeName;
	private Boolean hidePermanence;
	private String apheSource;
	
	public EmployeeLog() {
		super();
		this.creationDateTime = new Date();
		hidePermanence = false;
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

	@Override
	public void preStore() {
		lastUpdate = new Date();
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
	 * @return the checkinStarted
	 */
	public Date getCheckinStarted() {
		return checkinStarted;
	}

	/**
	 * @param checkinStarted the checkinStarted to set
	 */
	public void setCheckinStarted(Date checkinStarted) {
		this.checkinStarted = checkinStarted;
	}

	/**
	 * @return the checkinFinished
	 */
	public Date getCheckinFinished() {
		return checkinFinished;
	}

	/**
	 * @param checkinFinished the checkinFinished to set
	 */
	public void setCheckinFinished(Date checkinFinished) {
		this.checkinFinished = checkinFinished;
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
	 * @return the apheSource
	 */
	public String getApheSource() {
		return apheSource;
	}

	/**
	 * @param apheSource the apheSource to set
	 */
	public void setApheSource(String apheSource) {
		this.apheSource = apheSource;
	}

	/**
	 * @return the hidePermanence
	 */
	public Boolean getHidePermanence() {
		return hidePermanence;
	}

	/**
	 * @param hidePermanence the hidePermanence to set
	 */
	public void setHidePermanence(Boolean hidePermanence) {
		this.hidePermanence = hidePermanence;
	}

	/**
	 * @return the employeeId
	 */
	public String getEmployeeId() {
		return employeeId;
	}

	/**
	 * @param employeeId the employeeId to set
	 */
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	/**
	 * @return the employeeName
	 */
	public String getEmployeeName() {
		return employeeName;
	}

	/**
	 * @param employeeName the employeeName to set
	 */
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
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
		EmployeeLog other = (EmployeeLog) obj;
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
		return "EmployeeLog [key=" + key + ", mac=" + mac + ", entityId=" + entityId + ", entityKind=" + entityKind
				+ ", checkinStarted=" + checkinStarted + ", checkinFinished=" + checkinFinished + ", employeeId="
				+ employeeId + ", employeeName=" + employeeName + "]";
	}

}