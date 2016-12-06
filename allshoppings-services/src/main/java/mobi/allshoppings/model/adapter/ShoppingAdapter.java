package mobi.allshoppings.model.adapter;

import mobi.allshoppings.model.Shopping;

public class ShoppingAdapter extends Shopping implements IGenericAdapter, IFavorite {

	private static final long serialVersionUID = 5657858175564391408L;

	private Boolean favorite = false;
	private String requester;
	private String brandAvatarId;
	private Long favoriteCount;
	private Long checkinCount;
	private Long points;
	
	@Override
	public Boolean getFavorite() {
		return this.favorite;
	}

	@Override
	public void setFavorite(Boolean favorite) {
		this.favorite = favorite;
	}

	@Override
	public String getRequester() {
		return requester;
	}

	@Override
	public void setRequester(String requester) {
		this.requester = requester;
	}

	public String getBrandAvatarId() {
		return brandAvatarId;
	}

	public void setBrandAvatarId(String brandAvatarId) {
		this.brandAvatarId = brandAvatarId;
	}

	/**
	 * @return the favoriteCount
	 */
	public Long getFavoriteCount() {
		return favoriteCount;
	}

	/**
	 * @param favoriteCount the favoriteCount to set
	 */
	public void setFavoriteCount(Long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	/**
	 * @return the checkinCount
	 */
	public Long getCheckinCount() {
		return checkinCount;
	}

	/**
	 * @param checkinCount the checkinCount to set
	 */
	public void setCheckinCount(Long checkinCount) {
		this.checkinCount = checkinCount;
	}

	/**
	 * @return the points
	 */
	public Long getPoints() {
		return points;
	}

	/**
	 * @param points the points to set
	 */
	public void setPoints(Long points) {
		this.points = points;
	}
	
}
