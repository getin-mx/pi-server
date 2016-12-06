package mobi.allshoppings.bz.spi;


import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.bz.OfferBzService;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.campaign.CampaignHelper;
import mobi.allshoppings.dao.CampaignActivityDAO;
import mobi.allshoppings.dao.FavoriteDAO;
import mobi.allshoppings.dao.OfferDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Favorite;
import mobi.allshoppings.model.Offer;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;
import mobi.allshoppings.model.adapter.OfferAdapter;
import mobi.allshoppings.task.QueueTaskHelper;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 */
public class OfferBzServiceJSONImpl extends OfferRestServerResource implements OfferBzService {

	private static final Logger log = Logger.getLogger(OfferBzServiceJSONImpl.class.getName());
	
	@Autowired
	private OfferDAO dao;
	@Autowired
	private CampaignHelper campaignHelper;
	@Autowired
	private CampaignActivityDAO campaignActivityDao;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private FavoriteDAO favoriteDao;
	@Autowired
	private QueueTaskHelper queueTaskHelper;
	
	private static final String IDENTIFIER = "offerId";
	private BzFields bzFields = BzFields.getBzFields(getClass());

	/**
	 * Obtains information about a shopping
	 * 
	 * @return A JSON representation of the selected fields for a shopping
	 */
	@Override
	public String retrieve() {
		long start = markStart();
		JSONObject returnValue;
		try {
			// validate authToken
			User user = this.getUserFromToken();

			// obtain the id
			final String offerId = obtainIdentifier(IDENTIFIER);
			Offer obj1 = null;
			try {
				obj1 = dao.get(offerId);
			} catch(ASException e1 ) {
				CampaignActivity activity = campaignActivityDao.get(offerId, true);
				obj1 = campaignHelper.campaignActivityToOffer(activity);
			}
			
			final Offer obj = obj1;
			final OfferAdapter offer;
			
			queueTaskHelper.enqueueNotificationLogReceived(user.getIdentifier(), offerId, EntityKind.KIND_OFFER);
			
			// track action
			trackerHelper.enqueue( user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.OfferBzService") + obj.getName(), 
					null, null);

//			String cachedJson = getCachedJSONString(offerId, EntityKind.KIND_OFFER, user, true);
//			if( null != cachedJson ) return cachedJson;

			// Obtains the shopping entity
			String level = obtainStringValue(LEVEL, systemConfiguration.getDefaultLevel());
			offer = new GenericAdapterImpl<OfferAdapter>().adapt(obj);
			Favorite favorite = favoriteDao.getUsingUserAndEntityAndKind(user, offer.getIdentifier(), EntityKind.KIND_OFFER, true);
			if( favorite != null ) offer.setFavorite(true);
			offer.setPoints(user.getPoints());
			completeAdaptationFor(offer, user);

			// Obtains the user JSON representation
			final String[] fields = obtainOutputFields(bzFields, level);
			returnValue = getJSONRepresentationFromObject(offer, fields);
			
//			setCachedJSONString(returnValue.toString());

		} catch (ASException e) {
    		if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE || 
    				e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE) {
    			log.log(Level.INFO, e.getMessage());
    		} else {
    			log.log(Level.SEVERE, e.getMessage(), e);
    		}
    		returnValue = getJSONRepresentationFromException(e);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			returnValue = getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e));
		} finally {
			markEnd(start);
		}
		return returnValue.toString();
	}
}
