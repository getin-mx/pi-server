package mobi.allshoppings.geocoding;

import org.apache.commons.lang.Validate;

public class GeoPoint {

    private double lat;
    private double lon;
    private String geohash;
    
    public GeoPoint() {
    	
    }

    public GeoPoint(double lat, double lon, String geohash) {
    	Validate.isTrue(!(lat > 90.0 || lat < -90.0), "Latitude must be in [-90, 90]  but was ", lat);
    	Validate.isTrue(!(lon > 180.0 || lon < -180.0), "Longitude must be in [-180, 180] but was ", lon);
        this.lat = lat;
        this.lon = lon;
        this.geohash = geohash;
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
	 * @return the geohash
	 */
	public String getGeohash() {
		return geohash;
	}

	/**
	 * @param geohash the geohash to set
	 */
	public void setGeohash(String geohash) {
		this.geohash = geohash;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lon);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		GeoPoint other = (GeoPoint) obj;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (Double.doubleToLongBits(lon) != Double.doubleToLongBits(other.lon))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GeoPoint [lat=" + lat + ", lon=" + lon + ", geohash=" + geohash
				+ "]";
	}

}
