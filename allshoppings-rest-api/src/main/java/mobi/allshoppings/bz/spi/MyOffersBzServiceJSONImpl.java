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

import com.inodes.util.CollectionFactory;

import mobi.allshoppings.bz.MyOffersBzService;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.FavoriteDAO;
import mobi.allshoppings.dao.OfferDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Offer;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserEntityCache;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;
import mobi.allshoppings.model.adapter.OfferAdapter;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.Range;

/**
 *
 */
public class MyOffersBzServiceJSONImpl extends OfferRestServerResource implements MyOffersBzService {

    private static final Logger log = Logger.getLogger(MyOffersBzServiceJSONImpl.class.getName());

    @Autowired
    private OfferDAO dao;
    @Autowired
    private FavoriteDAO favoriteDao;
    
    private BzFields bzFields = BzFields.getBzFields(getClass());
    
    @Override
    public String retrieve() {
    	
    	long start = markStart();
		JSONObject returnValue = null;
		try {
			List<OfferAdapter> offers = new ArrayList<OfferAdapter>();
			
			// validate authToken
			User user = this.getUserFromToken();

			// get level, if not defined use default value
			String level = this.obtainStringValue(LEVEL, BzFields.LEVEL_LIST);
			Range range = this.obtainRange();
			
			// Get Search query
			String q = this.obtainStringValue(Q, null);
			
			// Additional Search Fields
			Map<String, String> additionalFields = CollectionFactory.createMap();

			// Get favoritesFirst Option
			List<String> favorites = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_OFFER);
			
			// retrieve all offers
			long millisPre = new Date().getTime();
			if( q != null && !q.trim().equals("")) {
				offers = new GenericAdapterImpl<OfferAdapter>().adaptList(
						dao.getUsingIndex(Offer.class.getName(), q, 
								user.getViewLocation(), StatusHelper.statusActive(), null, 
								additionalFields, null, null));
			} else {
				offers = new GenericAdapterImpl<OfferAdapter>().adaptList(dao
						.getUsingStatusAndRangeInCache(new Vector<Integer>(),
								range, user, UserEntityCache.TYPE_MY_OFFERS));
			}
			long diff = new Date().getTime() - millisPre;
			
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
					getI18NMessage("es_AR", "service.MyOffersBzService"),
					null, null);

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
