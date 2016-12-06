package mobi.allshoppings.bz.spi;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.ShoppingListBzService;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.FavoriteDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserEntityCache;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;
import mobi.allshoppings.model.adapter.ShoppingAdapter;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.Range;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.inodes.util.CollectionFactory;

/**
 *
 */
public class ShoppingListBzServiceJSONImpl extends RestBaseServerResource implements ShoppingListBzService {

    private static final Logger log = Logger.getLogger(ShoppingListBzServiceJSONImpl.class.getName());

    @Autowired
    private ShoppingDAO dao;
    @Autowired
    private FavoriteDAO favoriteDao;
    @Autowired
    private SystemConfiguration systemConfiguration;
    
    private BzFields bzFields = BzFields.getBzFields(getClass());

    @Override
    public String retrieve() {
    	
    	long start = markStart();
		JSONObject returnValue = null;
		try {
			List<ShoppingAdapter> shoppings = new ArrayList<ShoppingAdapter>();
			
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
			Boolean favoritesFirst = this.obtainBooleanValue(FAVORITES_FIRST, systemConfiguration.getDefaultFavoritesFirst()); 
			Boolean favoritesOnly = this.obtainBooleanValue(FAVORITES_ONLY, false); 
			List<String> favorites = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_SHOPPING);

			// Get the language;
			String lang = this.obtainLang();
			
			// retrieve all shoppings
			long millisPre = new Date().getTime();
			if( q != null && !q.trim().equals("")) {
				shoppings = new GenericAdapterImpl<ShoppingAdapter>()
						.adaptList(dao.getUsingIndex(
								Shopping.class.getName(), q, user.getViewLocation(),
								StatusHelper.statusActive(), null, 
								additionalFields, null, lang));
			} else {
				shoppings = new GenericAdapterImpl<ShoppingAdapter>().adaptList(dao.getUsingStatusAndRangeInCache(
						new Vector<Integer>(), range, user, 
						favoritesOnly ? UserEntityCache.TYPE_FAVORITES_ONLY :
						favoritesFirst ? UserEntityCache.TYPE_FAVORITES_FIRST : UserEntityCache.TYPE_NORMAL_SORT, 
								null));
			}
			long diff = new Date().getTime() - millisPre;
			
			for( ShoppingAdapter shop : shoppings ) {
				if (favorites.contains(shop.getIdentifier()))
					shop.setFavorite(true);
			}
			
			// Logs the result
			log.info("Number of shoppings found [" + shoppings.size() + "] in " + diff + " millis");
			returnValue = this.getJSONRepresentationFromArrayOfObjects(shoppings, this.obtainOutputFields(bzFields, level));
			
			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.ShoppingListBzService"),
					q, null);

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
