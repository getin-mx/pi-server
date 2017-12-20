package mobi.allshoppings.model.adapter;

public class HotSpotAdapter {

	private String identifier;
	private byte entityKind;
	private Double lat;
	private Double lon;
	private int distance;
	private int checkinDistance;
	private int pointDistance;
	
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
	
	/**
	 * @return the distance
	 */
	public Integer getDistance() {
		return distance;
	}
	
	/**
	 * @param distance the distance to set
	 */
	public void setDistance(Integer distance) {
		this.distance = distance;
	}
	
	/**
	 * @return the checkinDistance
	 */
	public Integer getCheckinDistance() {
		return checkinDistance;
	}
	
	/**
	 * @param checkinDistance the checkinDistance to set
	 */
	public void setCheckinDistance(Integer checkinDistance) {
		this.checkinDistance = checkinDistance;
	}
	
	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * @return the pointDistance
	 */
	public Integer getPointDistance() {
		return pointDistance;
	}

	/**
	 * @param pointDistance the pointDistance to set
	 */
	public void setPointDistance(Integer pointDistance) {
		this.pointDistance = pointDistance;
	}

	/**
	 * @return the kind
	 */
	public byte getEntityKind() {
		return entityKind;
	}

	/**
	 * @param kind the kind to set
	 */
	public void setEntityKind(byte kind) {
		this.entityKind = kind;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + distance;
		result = prime * result + ((lat == null) ? 0 : lat.hashCode());
		result = prime * result + ((lon == null) ? 0 : lon.hashCode());
		return result;
	}
	
	/**
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
		HotSpotAdapter other = (HotSpotAdapter) obj;
		if (distance != other.distance)
			return false;
		if (lat == null) {
			if (other.lat != null)
				return false;
		} else if (!lat.equals(other.lat))
			return false;
		if (lon == null) {
			if (other.lon != null)
				return false;
		} else if (!lon.equals(other.lon))
			return false;
		return true;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "{\"id\":\"" + identifier + "\",\"la\":" + lat + ",\"lo\":"
				+ lon + ",\"di\":" + distance + ",\"ci\":" + checkinDistance
				+ "}";
	}
	
}
