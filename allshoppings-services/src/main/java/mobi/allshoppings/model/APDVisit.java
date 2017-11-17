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
public class APDVisit implements ModelKey, Serializable, Identificable {

	private static final long serialVersionUID = 1L;
	public final static Integer CHECKIN_AUTO = 0;
	public final static Integer CHECKIN_MANUAL = 1;
	public final static Integer CHECKIN_VISIT = 2;
	public final static Integer CHECKIN_PEASANT = 3;
	public final static Integer CHECKIN_EMPLOYEE = 4;
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
	private Key key;
	
	private String userId;
	private String deviceUUID;
	private String mac;
	private String devicePlatform;
	private String entityId;
	private Integer entityKind;
	private Date checkinStarted;
	private Date checkinFinished;
	private Date lastUpdate;
	private Date creationDateTime;
	private Boolean verified;
	private Integer checkinType;
	private Boolean hidePermanence;
	private Boolean approved;
	private String apheSource;
	private String forDate;
	private Long duration;
	
	private Integer totalSegments;
	private Integer inRangeSegments;

	public APDVisit() {
		super();
		this.creationDateTime = new Date();
		verified = false;
		checkinType = CHECKIN_AUTO;
		hidePermanence = false;
		approved = true;
		duration = 0L; 
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
	 * @return the verified
	 */
	public Boolean getVerified() {
		return verified;
	}

	/**
	 * @param verified the verified to set
	 */
	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	/**
	 * @return the checkinType
	 */
	public Integer getCheckinType() {
		return checkinType;
	}

	/**
	 * @param checkinType the checkinType to set
	 */
	public void setCheckinType(Integer checkinType) {
		this.checkinType = checkinType;
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
	 * @return the approved
	 */
	public Boolean getApproved() {
		return approved;
	}

	/**
	 * @param approved the approved to set
	 */
	public void setApproved(Boolean approved) {
		this.approved = approved;
	}
	
	/**
	 * @return the totalSegments
	 */
	public Integer getTotalSegments() {
		return totalSegments;
	}

	/**
	 * @return the forDate
	 */
	public String getForDate() {
		return forDate;
	}

	/**
	 * @param forDate the forDate to set
	 */
	public void setForDate(String forDate) {
		this.forDate = forDate;
	}

	/**
	 * @param totalSegments the totalSegments to set
	 */
	public void setTotalSegments(Integer totalSegments) {
		this.totalSegments = totalSegments;
	}

	/**
	 * @return the inRangeSegments
	 */
	public Integer getInRangeSegments() {
		return inRangeSegments;
	}

	/**
	 * @param inRangeSegments the inRangeSegments to set
	 */
	public void setInRangeSegments(Integer inRangeSegments) {
		this.inRangeSegments = inRangeSegments;
	}

	/**
	 * @return the duration
	 */
	public Long getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public void addInRangeSegment() {
		if( null == inRangeSegments ) inRangeSegments = 0;
		if( null == totalSegments ) totalSegments = 0;
		inRangeSegments++;
		totalSegments++;
	}

	public void addOffRangeSegment() {
		if( null == inRangeSegments ) inRangeSegments = 0;
		if( null == totalSegments ) totalSegments = 0;
		totalSegments++;
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
		APDVisit other = (APDVisit) obj;
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
		return "APDVisit [key=" + key + ", userId=" + userId + ", deviceUUID=" + deviceUUID + ", mac=" + mac
				+ ", platform=" + devicePlatform + ", entityId=" + entityId + ", entityKind=" + entityKind
				+ ", checkinStarted=" + checkinStarted + ", checkinFinished=" + checkinFinished + ", lastUpdate="
				+ lastUpdate + ", creationDateTime=" + creationDateTime + ", verified=" + verified + ", checkinType="
				+ checkinType + ", apheSource=" + apheSource + "]";
	}

}