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
public class GeoEntity implements ModelKey, Serializable, IAdaptable, Identificable, Replicable {
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	private String entityId;
	private byte entityKind;
	private String geohash;
	private double lat;
	private double lon;
	private Date lastUpdate;
	private boolean independent;
	
    public GeoEntity() {
    	this.independent = true;
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
	public byte getEntityKind() {
		return entityKind;
	}

	/**
	 * @param entityKind the entity kind to set
	 */
	public void setEntityKind(byte entityKind) {
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
	 * @return the independent
	 */
	public Boolean getIndependent() {
		return independent;
	}

	/**
	 * @param independent the independent to set
	 */
	public void setIndependent(Boolean independent) {
		this.independent = independent;
	}

	public Date getCreationDateTime() {
		return lastUpdate;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((entityId == null) ? 0 : entityId.hashCode());
		result = prime * result + entityKind;
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
		GeoEntity other = (GeoEntity) obj;
		if (entityId == null) {
			if (other.entityId != null)
				return false;
		} else if (!entityId.equals(other.entityId))
			return false;
		if (entityKind != other.entityKind)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GeoEntity [key=" + key + ", entityId=" + entityId
				+ ", entityKind=" + entityKind + ", geohash=" + geohash
				+ ", lat=" + lat + ", lon=" + lon + ", lastUpdate="
				+ lastUpdate + ", independent=" + independent + "]";
	}

}
