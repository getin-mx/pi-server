package mobi.allshoppings.model.adapter;

import java.util.Map;

import mobi.allshoppings.dao.OfferDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.NotificationLog;
import mobi.allshoppings.model.Offer;

public class NotificationLogAdapter extends NotificationLog implements IGenericAdapter, ICompletableAdapter, IFavorite {

	private static final long serialVersionUID = 5657858175564391408L;
	public static final String OPTIONS_OFFERDAO = "OfferDAO";
	
	private String avatarId;
	private String requester;
	private boolean favorite;

	public NotificationLogAdapter() {
		favorite = false;
	}
	
	/**
	 * @return the avatarId
	 */
	public String getAvatarId() {
		return avatarId;
	}

	/**
	 * @param avatarId the avatarId to set
	 */
	public void setAvatarId(String avatarId) {
		this.avatarId = avatarId;
	}
	
	@Override
	public void completeAdaptation(Map<String, Object> options) throws ASException {

		// This notification reffers an offer
		if( EntityKind.KIND_OFFER == getEntityKind() ) {
			OfferDAO offerDao = (OfferDAO)options.get(OPTIONS_OFFERDAO);
			if( offerDao == null ) return;

			try {
				Offer offer = offerDao.get(getEntityId());
				setAvatarId(offer.getAvatarId());
				setTitle(offer.getName());
				setData(offer.getDescription());
			} catch( ASException e ) {
				if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
					return;
				} else {
					throw e;
				}
			}
		}
	}

	@Override
	public Boolean getFavorite() {
		return favorite;
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
