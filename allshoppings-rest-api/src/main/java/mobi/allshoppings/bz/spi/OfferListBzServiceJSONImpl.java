package mobi.allshoppings.bz.spi;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.OfferListBzService;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.FavoriteDAO;
import mobi.allshoppings.dao.OfferDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Offer;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserEntityCache;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;
import mobi.allshoppings.model.adapter.OfferAdapter;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

/**
 *
 */
public class OfferListBzServiceJSONImpl extends OfferRestServerResource implements OfferListBzService {

    private static final Logger log = Logger.getLogger(OfferListBzServiceJSONImpl.class.getName());

    @Autowired
    private OfferDAO dao;
    @Autowired
    private SystemConfiguration systemConfiguration;
    @Autowired
    private FavoriteDAO favoriteDao;
    
    private BzFields bzFields = BzFields.getBzFields(getClass());
    
    private final static String ENTITY_KIND = "kind";
    private static final String ENTITY_IDENTIFIER = "entityId";

    @Override
    public String retrieve() {
    	
    	long start = markStart();
		JSONObject returnValue = null;
		try {
			List<OfferAdapter> offers = new ArrayList<OfferAdapter>();
			
			// validate authToken
			User user = this.getUserFromToken();
			boolean forDay = this.obtainBooleanValue("forDay", false);
			byte entityKind = EntityKind.resolveByName(this.obtainStringValue(ENTITY_KIND, null));
			String entityId = this.obtainStringValue(ENTITY_IDENTIFIER, null); 

			// get level, if not defined use default value
			String level = this.obtainStringValue(LEVEL, BzFields.LEVEL_LIST);
			Range range = this.obtainRange();
			
			// Get Search query
			String q = this.obtainStringValue(Q, null);
			
			// Additional Search Fields
			Map<String, String> additionalFields = CollectionFactory.createMap();

			// Get favoritesFirst Option
			Boolean favoritesOnly = this.obtainBooleanValue(FAVORITES_ONLY, false); 
			List<String> favorites = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_OFFER);

			// retrieve all offers
			long millisPre = new Date().getTime();
			if( StringUtils.hasText(entityId)) {
				offers = new GenericAdapterImpl<OfferAdapter>().adaptList(
						dao.getUsingKindAndRangeInCache(entityId, entityKind, range, user,
								UserEntityCache.TYPE_GEOPRIORITY));
			} else {
				if( q != null && !q.trim().equals("")) {
					offers = new GenericAdapterImpl<OfferAdapter>().adaptList(
							dao.getUsingIndex(Offer.class.getName(), q, 
									user.getViewLocation(), StatusHelper.statusActive(), null, 
									additionalFields, null, null));
				} else {
					offers = new GenericAdapterImpl<OfferAdapter>().adaptList(
							dao.getUsingStatusAndRangeInCache(new Vector<Integer>(), range, user, 
									favoritesOnly ? UserEntityCache.TYPE_FAVORITES_ONLY :
									UserEntityCache.TYPE_GEOPRIORITY));
				}
			}
			long diff = new Date().getTime() - millisPre;

			// Checks if the offer is valid for day
			if( forDay ) {
				List<OfferAdapter> tmpOffers = CollectionFactory.createList();
				tmpOffers.addAll(offers);
				offers.clear();
				Date now = new Date();
				for(OfferAdapter offer : tmpOffers ) {
					if( offer.appliesForDate(now))
						offers.add(offer);
				}
			}
			
			for( OfferAdapter offer : offers ) {
				if (favorites.contains(offer.getIdentifier()))
					offer.setFavorite(true);
				completeAdaptationWithoutLists(offer, user.getIdentifier());
			}

			// Logs the result
			log.info("Number of offers found [" + offers.size() + "] in " + diff + " millis");
			returnValue = this.getJSONRepresentationFromArrayOfObjects(	offers, this.obtainOutputFields(bzFields, level));
			
			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.OfferListBzService"),
					q, null);

		} catch (ASException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			returnValue = this.getJSONRepresentationFromException(e);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			returnValue = this.getJSONRepresentationFromException(e);
		} finally {
			markEnd(start);
		}
		return returnValue.toString();
    }

    public String retrieveByEntityAndKind(String entityId, byte entityKind) {
    	long start = markStart();
		JSONObject returnValue = null;
		try {
			List<OfferAdapter> offers = new ArrayList<OfferAdapter>();
			
			// validate authToken
			User user = this.getUserFromToken();

			// get level, if not defined use default value
			String level = this.obtainStringValue(LEVEL, BzFields.LEVEL_LIST);
			Range range = this.obtainRange();
			
			// Get favoritesFirst Option
			Boolean favoritesFirst = this.obtainBooleanValue(FAVORITES_FIRST, systemConfiguration.getDefaultFavoritesFirst()); 
			Boolean favoritesOnly = this.obtainBooleanValue(FAVORITES_ONLY, false); 
			List<String> favorites = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_SHOPPING);
			
			// retrieve all offers
			long millisPre = new Date().getTime();
			offers = new GenericAdapterImpl<OfferAdapter>().adaptList(dao.getUsingKindAndRangeInCache(entityId, 
					entityKind, range, user,
					favoritesOnly ? UserEntityCache.TYPE_FAVORITES_ONLY :
					favoritesFirst ? UserEntityCache.TYPE_FAVORITES_FIRST : UserEntityCache.TYPE_NORMAL_SORT));
			long diff = new Date().getTime() - millisPre;
			
			for( OfferAdapter offer : offers ) {
				if (favorites.contains(offer.getIdentifier()))
					offer.setFavorite(true);
				completeAdaptationWithoutLists(offer, user.getIdentifier());
			}

			// Logs the result
			log.info("Number of offers found [" + offers.size() + "] in " + diff + " millis");
			returnValue = this.getJSONRepresentationFromArrayOfObjects(	offers, this.obtainOutputFields(bzFields, level));
			
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
			returnValue = this.getJSONRepresentationFromException(e);
		} finally {
			markEnd(start);
		}
		return returnValue.toString();
    }

}
