package mobi.allshoppings.model.adapter;

import mobi.allshoppings.model.Store;

public class StoreAdapter extends Store implements IGenericAdapter {

	private static final long serialVersionUID = 5657858175564391408L;
	private boolean favorite = false;

	/**
	 * @return the favorite
	 */
	public boolean isFavorite() {
		return favorite;
	}
	
	/**
	 * @param favorite the favorite to set
	 */
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

}
