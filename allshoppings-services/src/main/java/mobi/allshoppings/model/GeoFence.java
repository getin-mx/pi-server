package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.model.adapter.IAdaptable;
import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.Replicable;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class GeoFence implements ModelKey, Serializable, IAdaptable, Identificable, Replicable {
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	private String entityId;
	private Integer entityKind;
	private String geohash;
	private Double lat;
	private Double lon;
	private String name;
	private Integer approachDistance;
	private Integer interactionDistance;
	private Date lastUpdate;
	private Date creationDateTime;
	
    public GeoFence() {
    	super();
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
	 * @return the entity kind
	 */
	public Integer getEntityKind() {
		return entityKind;
	}

	/**
	 * @param entityKind the entity kind to set
	 */
	public void setEntityKind(Integer entityKind) {
		this.entityKind = entityKind;
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
	 * @return the lat
	 */
	public Double getLat() {
		return lat;
	}

	/**
	 * @param lat the lat to set
	 */
	public void setLat(Double lat) {
		if( lat == null ) this.lat = 0.0;
		else this.lat = lat;
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
		if( lon == null ) this.lon = 0.0;
		else this.lon = lon;
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
	 * @return a new GeoPoint based in lat, lon, and geohash
	 */
	public GeoPoint getGeoPoint() {
		return new GeoPoint(lat, lon, geohash);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the approachDistance
	 */
	public Integer getApproachDistance() {
		return approachDistance;
	}

	/**
	 * @param approachDistance the approachDistance to set
	 */
	public void setApproachDistance(Integer approachDistance) {
		this.approachDistance = approachDistance;
	}

	/**
	 * @return the interactionDistance
	 */
	public Integer getInteractionDistance() {
		return interactionDistance;
	}

	/**
	 * @param interactionDistance the interactionDistance to set
	 */
	public void setInteractionDistance(Integer interactionDistance) {
		this.interactionDistance = interactionDistance;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GeoFence [key=" + key + ", entityId=" + entityId + ", entityKind=" + entityKind + ", geohash=" + geohash
				+ ", lat=" + lat + ", lon=" + lon + ", name=" + name + ", approachDistance=" + approachDistance
				+ ", interactionDistance=" + interactionDistance + ", lastUpdate=" + lastUpdate + ", creationDateTime="
				+ creationDateTime + "]";
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
		GeoFence other = (GeoFence) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

}
