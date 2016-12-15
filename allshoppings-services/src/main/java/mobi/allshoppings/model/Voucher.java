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

@PersistenceCapable(detachable="true")
public class Voucher implements ModelKey, Serializable, Identificable, Replicable {

	private static final long serialVersionUID = 1L;

	public static final Integer STATUS_AVAILABLE = 1;
	public static final Integer STATUS_OFFERED = 2;
	public static final Integer STATUS_USED = 3;
	public static final Integer STATUS_EXPIRED = 4;
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
	private Key key;

	private String brandId;
	private String type;
	private String code;
	private String subcode1;
	private String subcode2;
	private Integer status;
	private Date creationDateTime;
	private Date expirationDate;
	private Date lastUpdate;
	private Date assignationDate;
	private String deviceUUID;
	private String assignationMember;

	public Voucher() {
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

	@Override
	public void preStore() {
		this.lastUpdate = new Date();
	}

	/**
	 * @return the brandId
	 */
	public String getBrandId() {
		return brandId;
	}

	/**
	 * @param brandId the brandId to set
	 */
	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the subcode1
	 */
	public String getSubcode1() {
		return subcode1;
	}

	/**
	 * @param subcode1 the subcode1 to set
	 */
	public void setSubcode1(String subcode1) {
		this.subcode1 = subcode1;
	}

	/**
	 * @return the subcode2
	 */
	public String getSubcode2() {
		return subcode2;
	}

	/**
	 * @param subcode2 the subcode2 to set
	 */
	public void setSubcode2(String subcode2) {
		this.subcode2 = subcode2;
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
	 * @return the expirationDate
	 */
	public Date getExpirationDate() {
		return expirationDate;
	}

	/**
	 * @param expirationDate the expirationDate to set
	 */
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * @return the assignationDate
	 */
	public Date getAssignationDate() {
		return assignationDate;
	}

	/**
	 * @param assignationDate the assignationDate to set
	 */
	public void setAssignationDate(Date assignationDate) {
		this.assignationDate = assignationDate;
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
	 * @return the assignationMember
	 */
	public String getAssignationMember() {
		return assignationMember;
	}

	/**
	 * @param assignationMember the assignationMember to set
	 */
	public void setAssignationMember(String assignationMember) {
		this.assignationMember = assignationMember;
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
		Voucher other = (Voucher) obj;
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
		return "Voucher [key=" + key + ", brandId=" + brandId + ", type="
				+ type + ", code=" + code + ", subcode1=" + subcode1
				+ ", subcode2=" + subcode2 + ", status=" + status
				+ ", creationDateTime=" + creationDateTime
				+ ", expirationDate=" + expirationDate + ", lastUpdate="
				+ lastUpdate + ", assignationDate=" + assignationDate
				+ ", deviceUUID=" + deviceUUID + ", assignationMember="
				+ assignationMember + "]";
	}

}
