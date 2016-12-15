package mobi.allshoppings.model.adapter;

import mobi.allshoppings.model.CampaignSpecial;

public class CampaignSpecialAdapter extends CampaignSpecial implements IGenericAdapter, IFavorite {

	private static final long serialVersionUID = 5657858175564391408L;

	private Boolean favorite = false;
	private String requester;
	
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
	
}
