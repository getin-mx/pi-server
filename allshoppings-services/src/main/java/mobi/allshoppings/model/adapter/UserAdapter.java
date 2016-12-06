package mobi.allshoppings.model.adapter;

import mobi.allshoppings.model.User;

public class UserAdapter extends User implements IGenericAdapter, IFriend {

	private static final long serialVersionUID = 5657858175564391408L;

	private Boolean friend = false;
	private String requester;
	
	/**
	 * @return the friend
	 */
	public Boolean getFriend() {
		return friend;
	}
	
	/**
	 * @param friend the friend to set
	 */
	public void setFriend(Boolean friend) {
		this.friend = friend;
	}
	
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
	
}
