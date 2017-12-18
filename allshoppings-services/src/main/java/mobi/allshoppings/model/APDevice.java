package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.Indexable;
import mobi.allshoppings.model.interfaces.ModelKey;

@PersistenceCapable(detachable="true")
public class APDevice implements ModelKey, Serializable, Identificable, Indexable {

	private static final long serialVersionUID = 1L;
	
	public static final byte REPORT_STATUS_REPORTED = 1;
	public static final byte REPORT_STATUS_NOT_REPORTED = 0;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
	private Key key;

	// Basic APDevice data
	private String hostname;
	private String description;
	

	// Model and installed data
	private String model;
	private String mode;
	private Date lastUpdate;
	
	private Date lastInfoUpdate;
	private boolean external;

	// Geo location
	private double lat;
	private double lon;
	
	// Parameters
	private long visitTimeThreshold;
	
	private Date creationDateTime;
	
	@NotPersistent
	private boolean doIndexNow = true;

	public APDevice() {
		this.creationDateTime = new Date();
		this.external = false;
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
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
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
	 * @return the visitTimeThreshold
	 */
	public long getVisitTimeThreshold() {
		return visitTimeThreshold;
	}

	/**
	 * @param visitTimeThreshold the visitTimeThreshold to set
	 */
	public void setVisitTimeThreshold(long visitTimeThreshold) {
		this.visitTimeThreshold = visitTimeThreshold;
	}

	/**
	 * @return the lat
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * @param lat the lat to set
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}

	/**
	 * @return the lon
	 */
	public double getLon() {
		return lon;
	}

	/**
	 * @param lon the lon to set
	 */
	public void setLon(double lon) {
		this.lon = lon;
	}

	/**
	 * @return the doIndexNow
	 */
	public boolean isDoIndexNow() {
		return doIndexNow;
	}

	/**
	 * @param doIndexNow the doIndexNow to set
	 */
	public void setDoIndexNow(boolean doIndexNow) {
		this.doIndexNow = doIndexNow;
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
		APDevice other = (APDevice) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
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
	 * @return the lastInfoUpdate
	 */
	public Date getLastInfoUpdate() {
		return lastInfoUpdate;
	}

	/**
	 * @param lastInfoUpdate the lastInfoUpdate to set
	 */
	public void setLastInfoUpdate(Date lastInfoUpdate) {
		this.lastInfoUpdate = lastInfoUpdate;
	}

	/**
	 * @return the external
	 */
	public boolean getExternal() {
		return external;
	}

	/**
	 * @param external the external to set
	 */
	public void setExternal(boolean external) {
		this.external = external;
	}	

	@Override
	public boolean doIndex() {
		return doIndexNow;
	}

	@Override
	public void disableIndexing(boolean val) {
		this.doIndexNow = !val;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "APDevice [key=" + key + ", hostname=" + hostname + ", description=" + description 
				+ ", model=" + model + ", mode=" + mode + ", lastInfoUpdate=" + lastInfoUpdate + ", external="
				+ external + ", lat=" + lat + ", lon=" + lon + ", visitTimeThreshold=" + visitTimeThreshold
				+ ", creationDateTime=" + creationDateTime + ", lastUpdate=" + lastUpdate + ", doIndexNow="
				+ doIndexNow + "]";
	}

	@Override
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public Date getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public void preStore() {
		this.lastUpdate = new Date(); 
	}

}
