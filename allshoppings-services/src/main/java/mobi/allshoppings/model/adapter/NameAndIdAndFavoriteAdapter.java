package mobi.allshoppings.model.adapter;

public class NameAndIdAndFavoriteAdapter extends NameAndIdAdapter implements IFavorite {

	public NameAndIdAndFavoriteAdapter() {
		
	}
	
	public NameAndIdAndFavoriteAdapter(String identifier, String name, String avatarId) {
		super(identifier, name, avatarId);
	}

	private String requester = null;
	private Boolean favorite = false;
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
	
}
