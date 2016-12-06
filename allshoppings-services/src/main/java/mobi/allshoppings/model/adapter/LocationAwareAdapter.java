package mobi.allshoppings.model.adapter;

import java.io.Serializable;

import mobi.allshoppings.geocoding.DistanceAndFavoriteAware;

public class LocationAwareAdapter extends NameAndIdAdapter implements IFavorite, DistanceAndFavoriteAware, Serializable {

	private static final long serialVersionUID = 8155838792316556687L;

	private String requester = null;
	private Boolean favorite = false;
	private int kind;
	private double lat = 0;
	private double lon = 0;
	private int distance;
	private String description;
	private String brandName;
	private String offerTypeId;
	
	/**
	 * @return the requester
	 */
	public String getRequester() {
		return requester;
	}
	/**
	 * @param requester the requester to set
	 */
	public void setRequester(String requester) {
		this.requester = requester;
	}
	/**
	 * @return the favorite
	 */
	public Boolean getFavorite() {
		return favorite;
	}
	/**
	 * @param favorite the favorite to set
	 */
	public void setFavorite(Boolean favorite) {
		this.favorite = favorite;
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
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}
	/**
	 * @param distance the distance to set
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}
	/**
	 * @return the kind
	 */
	public int getKind() {
		return kind;
	}
	/**
	 * @param kind the kind to set
	 */
	public void setKind(int kind) {
		this.kind = kind;
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
	 * @return the brandName
	 */
	public String getBrandName() {
		return brandName;
	}
	/**
	 * @param brandName the brandName to set
	 */
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	/**
	 * @return the offerTypeId
	 */
	public String getOfferTypeId() {
		return offerTypeId;
	}
	/**
	 * @param offerTypeId the offerTypeId to set
	 */
	public void setOfferTypeId(String offerTypeId) {
		this.offerTypeId = offerTypeId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LocationAwareAdapter [requester=" + requester + ", favorite="
				+ favorite + ", kind=" + kind + ", lat=" + lat + ", lon=" + lon
				+ ", distance=" + distance + ", description=" + description
				+ ", brandName=" + brandName + ", offerTypeId=" + offerTypeId
				+ "] - " + super.toString();
	}
}
