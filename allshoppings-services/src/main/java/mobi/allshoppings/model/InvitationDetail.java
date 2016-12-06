package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.Replicable;

import com.inodes.datanucleus.model.Key;

@PersistenceCapable(detachable="true")
public class InvitationDetail implements ModelKey, Serializable, Identificable, Replicable {

	public static final String SOURCE_FACEBOOK = "FACEBOOK";
	public static final String SOURCE_SMS = "SMS";
	public static final String SOURCE_EMAIL = "EMAIL";
	public static final String SOURCE_TWITTER = "TWITTER";
	public static final String SOURCE_WHATSAPP = "WHATSAPP";

	public static final String SOURCE_UNUSED_WHATSAPP = "WHATZAPP";

	public static final Integer STATUS_INVITED = 0;
	public static final Integer STATUS_ACCEPTED = 1;
	public static final Integer STATUS_REJECTED = 2;
	
	private static final long serialVersionUID = 1L;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
	private Key key;
	
	private String userId;
	private String invitedId;
	private String source;
	private Integer status;
	private Date lastUpdate;
	private Date creationDateTime;
	private String referralCode;

	public InvitationDetail() {
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

	/**
	 * Pre store
	 */
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
	 * @return the invitedId
	 */
	public String getInvitedId() {
		return invitedId;
	}

	/**
	 * @param invitedId the invitedId to set
	 */
	public void setInvitedId(String invitedId) {
		this.invitedId = invitedId;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		if( source.equals(SOURCE_UNUSED_WHATSAPP)) {
			this.source = SOURCE_WHATSAPP;
		} else {
			this.source = source;
		}
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
	 * @return the referalCode
	 */
	public String getReferralCode() {
		return referralCode;
	}

	/**
	 * @param referalCode the referalCode to set
	 */
	public void setReferralCode(String referalCode) {
		this.referralCode = referalCode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((invitedId == null) ? 0 : invitedId.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		InvitationDetail other = (InvitationDetail) obj;
		if (invitedId == null) {
			if (other.invitedId != null)
				return false;
		} else if (!invitedId.equals(other.invitedId))
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
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InvitationDetail [key=" + key + ", userId=" + userId
				+ ", invitedId=" + invitedId + ", source=" + source
				+ ", status=" + status + ", lastUpdate=" + lastUpdate
				+ ", creationDateTime=" + creationDateTime + "]";
	}

}
