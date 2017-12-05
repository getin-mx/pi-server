package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.Replicable;
import mobi.allshoppings.tools.CollectionFactory;

@PersistenceCapable(detachable="true")
public class APHEntry implements ModelKey, Serializable, Identificable, Replicable {

	private static final long serialVersionUID = 1L;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
	private Key key;

	private String hostname;
	private String mac;
	private String date;
	
	@Deprecated
	@NotPersistent
	private transient String devicePlatform;

	@Persistent(defaultFetchGroup = "true")
	private Map<String, Integer> rssi;
	
	@NotPersistent
	private Map<String, Integer> artificialRssi;

	private Integer dataCount;
	private Integer minRssi;
	private Integer maxRssi;

	private Date lastUpdate;
	private Date creationDateTime;
	
	@NotPersistent
	private transient byte shiftDay;
	
	public static final byte PREVIOUS = -1;
	public static final byte NEXT = 1;
	public static final byte NO_SHIFT = 0;

	public APHEntry() {
		super();
		this.rssi = CollectionFactory.createMap();
		this.artificialRssi = CollectionFactory.createMap();
		this.dataCount = 0;
		this.creationDateTime = new Date();
	}

	public APHEntry(String hostname, String mac, String date) {
		super();
		this.hostname = hostname;
		this.mac = mac;
		this.date = date;
		this.rssi = CollectionFactory.createMap();
		this.artificialRssi = CollectionFactory.createMap();
		this.dataCount = 0;
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
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @return the dataCount
	 */
	public Integer getDataCount() {
		return dataCount;
	}

	/**
	 * @param dataCount the dataCount to set
	 */
	public void setDataCount(Integer dataCount) {
		this.dataCount = dataCount;
	}

	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
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
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
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
	 * @return the minRssi
	 */
	public Integer getMinRssi() {
		return minRssi;
	}

	/**
	 * @param minRssi the minRssi to set
	 */
	public void setMinRssi(Integer minRssi) {
		this.minRssi = minRssi;
	}

	/**
	 * @return the maxRssi
	 */
	public Integer getMaxRssi() {
		return maxRssi;
	}

	/**
	 * @param maxRssi the maxRssi to set
	 */
	public void setMaxRssi(Integer maxRssi) {
		this.maxRssi = maxRssi;
	}
	
	/**
	 * @return the rssi
	 */
	public Map<String, Integer> getRssi() {
		return rssi;
	}

	/**
	 * @param rssi the rssi to set
	 */
	public void setRssi(Map<String, Integer> rssi) {
		this.rssi = rssi;
	}

	/**
	 * @return the artificialRssi
	 */
	public Map<String, Integer> getArtificialRssi() {
		return artificialRssi;
	}

	/**
	 * @param artificialRssi the artificialRssi to set
	 */
	public void setArtificialRssi(Map<String, Integer> artificialRssi) {
		this.artificialRssi = artificialRssi;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + ((mac == null) ? 0 : mac.hashCode());
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
		APHEntry other = (APHEntry) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (mac == null) {
			if (other.mac != null)
				return false;
		} else if (!mac.equals(other.mac))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "APHEntry [key=" + key + ", hostname=" + hostname + ", mac=" + mac + ", date=" + date + ", dataCount="
				+ dataCount + ", minRssi=" + minRssi + ", maxRssi=" + maxRssi + "]";
	}
	
	public void setShiftDay(byte shift) {
		this.shiftDay = shift;
	}
	
	public byte getShiftDay() {
		return this.shiftDay;
	}

}
