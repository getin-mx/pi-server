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
public class DeviceLocationHistory implements ModelKey, Serializable, Identificable, Replicable {

	private static final long serialVersionUID = 1L;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
	private Key key;
	
	private String userId;
	private String userName;
	private String deviceUUID;
	private Double lat;
	private Double lon;
	private String connection;
	private String country;
	private String province;
	private String city;
	private Date creationDateTime;
	private Date lastUpdate;
	private String geohash;
	private Double precision;
	private String operator;
	private Integer signal;
	private Boolean roaming;
	
	public DeviceLocationHistory() {
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
	 * @return the lat
	 */
	public Double getLat() {
		return lat;
	}

	/**
	 * @param lat the lat to set
	 */
	public void setLat(Double lat) {
		this.lat = lat;
	}

	/**
	 * @return the lon
	 */
	public Double getLon() {
		return lon;
	}

	/**
	 * @param lon the lon to set
	 */
	public void setLon(Double lon) {
		this.lon = lon;
	}

	@Override
	public void preStore() {
		lastUpdate = new Date();
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
		DeviceLocationHistory other = (DeviceLocationHistory) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * @return the connection
	 */
	public String getConnection() {
		return connection;
	}

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(String connection) {
		this.connection = connection;
	}

	/**
	 * @return the geoHash
	 */
	public String getGeohash() {
		return geohash;
	}

	/**
	 * @param geohash the geoHash to set
	 */
	public void setGeohash(String geohash) {
		this.geohash = geohash;
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

	/**
	 * @return the precision
	 */
	public Double getPrecision() {
		return precision;
	}

	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(Double precision) {
		this.precision = precision;
	}

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * @return the signal
	 */
	public Integer getSignal() {
		return signal;
	}

	/**
	 * @param signal the signal to set
	 */
	public void setSignal(Integer signal) {
		this.signal = signal;
	}

	/**
	 * @return the roaming
	 */
	public Boolean getRoaming() {
		return roaming;
	}

	/**
	 * @param roaming the roaming to set
	 */
	public void setRoaming(Boolean roaming) {
		this.roaming = roaming;
	}

	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeviceLocationHistory [key=" + key + ", userId=" + userId
				+ ", userName=" + userName + ", deviceUUID=" + deviceUUID
				+ ", lat=" + lat + ", lon=" + lon + ", connection="
				+ connection + ", country=" + country + ", city=" + city
				+ ", creationDateTime=" + creationDateTime + ", lastUpdate="
				+ lastUpdate + ", geohash=" + geohash + "]";
	}

}
