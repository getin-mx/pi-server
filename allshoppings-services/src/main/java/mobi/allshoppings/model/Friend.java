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

@PersistenceCapable(detachable="true")
public class Friend implements ModelKey, Serializable, Identificable{

	private static final long serialVersionUID = 1L;
	
	public static final Integer STATUS_INVITED = 0;
	public static final Integer STATUS_ACCEPTED = 1;
	public static final Integer STATUS_REJECTED = 2;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
	private Key key;
	
	private String userId1;
	private String userId2;
	private Date creationDateTime;
	private Date since;
	private Date lastUpdate;
	private Integer status;

	public Friend() {
		super();
		creationDateTime = new Date();
		since = new Date();
		status = STATUS_INVITED;
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
	 * @return the userId1
	 */
	public String getUserId1() {
		return userId1;
	}

	/**
	 * @param userId1 the userId1 to set
	 */
	public void setUserId1(String userId1) {
		this.userId1 = userId1;
	}

	/**
	 * @return the userId2
	 */
	public String getUserId2() {
		return userId2;
	}

	/**
	 * @param userId2 the userId2 to set
	 */
	public void setUserId2(String userId2) {
		this.userId2 = userId2;
	}

	/**
	 * @return the since
	 */
	public Date getSince() {
		return since;
	}

	/**
	 * @param since the since to set
	 */
	public void setSince(Date since) {
		this.since = since;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId1 == null) ? 0 : userId1.hashCode());
		result = prime * result + ((userId2 == null) ? 0 : userId2.hashCode());
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
		Friend other = (Friend) obj;
		if (userId1 == null) {
			if (other.userId1 != null)
				return false;
		} else if (!userId1.equals(other.userId1))
			return false;
		if (userId2 == null) {
			if (other.userId2 != null)
				return false;
		} else if (!userId2.equals(other.userId2))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Friend [key=" + key + ", userId1=" + userId1 + ", userId2="
				+ userId2 + ", creationDateTime=" + creationDateTime
				+ ", since=" + since + ", lastUpdate=" + lastUpdate
				+ ", status=" + status + "]";
	}

	@Override
	public void preStore() {
		lastUpdate = new Date();
	}
}