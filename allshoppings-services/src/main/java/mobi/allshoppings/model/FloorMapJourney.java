package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.CollectionFactory;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class FloorMapJourney implements ModelKey, Serializable, Identificable {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	private String floorMapId;
	private String mac;
	private String deviceUUID;
	private String date;

	@Persistent(defaultFetchGroup = "true")
	private Map<String, String> wifiPoints;
	private Integer dataCount;

	@Persistent(defaultFetchGroup = "true")
	private List<String> word;
	private Integer wordLength;

	private Date creationDateTime;
	private Date lastUpdate;
	
	@NotPersistent
	private boolean doIndexNow = true;
	
    public FloorMapJourney() {
		this.creationDateTime = new Date();
		this.wifiPoints = CollectionFactory.createMap();
		this.word = CollectionFactory.createList();
		this.dataCount = 0;
		this.wordLength = 0;
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
	 * @return the floorMapId
	 */
	public String getFloorMapId() {
		return floorMapId;
	}

	/**
	 * @param floorMapId the floorMapId to set
	 */
	public void setFloorMapId(String floorMapId) {
		this.floorMapId = floorMapId;
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
	 * @return the wifiPoints
	 */
	public Map<String, String> getWifiPoints() {
		return wifiPoints;
	}

	/**
	 * @param wifiPoints the wifiPoints to set
	 */
	public void setWifiPoints(Map<String, String> wifiPoints) {
		this.wifiPoints = wifiPoints;
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
	 * @return the word
	 */
	public List<String> getWord() {
		return word;
	}

	/**
	 * @param word the word to set
	 */
	public void setWord(List<String> word) {
		this.word = word;
	}

	/**
	 * @return the wordLength
	 */
	public Integer getWordLength() {
		return wordLength;
	}

	/**
	 * @param wordLength the wordLength to set
	 */
	public void setWordLength(Integer wordLength) {
		this.wordLength = wordLength;
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
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((deviceUUID == null) ? 0 : deviceUUID.hashCode());
		result = prime * result + ((floorMapId == null) ? 0 : floorMapId.hashCode());
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
		FloorMapJourney other = (FloorMapJourney) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (deviceUUID == null) {
			if (other.deviceUUID != null)
				return false;
		} else if (!deviceUUID.equals(other.deviceUUID))
			return false;
		if (floorMapId == null) {
			if (other.floorMapId != null)
				return false;
		} else if (!floorMapId.equals(other.floorMapId))
			return false;
		if (mac == null) {
			if (other.mac != null)
				return false;
		} else if (!mac.equals(other.mac))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FloorMapJourney [key=" + key + ", floorMapId=" + floorMapId + ", mac=" + mac + ", deviceUUID="
				+ deviceUUID + ", date=" + date + ", wifiPoints=" + wifiPoints + ", dataCount=" + dataCount + ", word="
				+ word + ", wordLength=" + wordLength + ", creationDateTime=" + creationDateTime + ", lastUpdate="
				+ lastUpdate + ", doIndexNow=" + doIndexNow + "]";
	}

}
