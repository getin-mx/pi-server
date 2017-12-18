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

import mobi.allshoppings.bz.BrandListBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.FavoriteDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserEntityCache;
import mobi.allshoppings.model.adapter.BrandAdapter;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.Range;

/**
 *
 */
public class BrandListBzServiceJSONImpl extends RestBaseServerResource implements BrandListBzService {

    private static final Logger log = Logger.getLogger(BrandListBzServiceJSONImpl.class.getName());

    @Autowired
    private BrandDAO dao;
    @Autowired
    private SystemConfiguration systemConfiguration;
    @Autowired
    private FavoriteDAO favoriteDao;
    
    private BzFields bzFields = BzFields.getBzFields(getClass());

    @Override
    public String retrieve() {
    	
		long start = markStart();
		JSONObject returnValue = null;
		try {
			List<BrandAdapter> brands = new ArrayList<BrandAdapter>();
			
			// validate authToken
			User user = this.getUserFromToken();

			// get level, if not defined use default value
			String level = this.obtainStringValue(LEVEL, BzFields.LEVEL_LIST);
			Range range = this.obtainRange();
			
			// Get favoritesFirst Option
			Boolean favoritesFirst = this.obtainBooleanValue(FAVORITES_FIRST, systemConfiguration.getDefaultFavoritesFirst()); 
			Boolean favoritesOnly = this.obtainBooleanValue(FAVORITES_ONLY, false); 
			List<String> favorites = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_BRAND);

			// Get Search query
			String q = this.obtainStringValue(Q, null);

			// Additional Search Fields
			Map<String, String> additionalFields = CollectionFactory.createMap();

			// Get the language;
			String lang = this.obtainLang();
			
			// retrieve all brands
			long millisPre = new Date().getTime();
			if( q != null && !q.trim().equals("")) {
				brands = new GenericAdapterImpl<BrandAdapter>().adaptList(dao
						.getUsingIndex(Brand.class.getName(), q, user.getViewLocation(),
								StatusHelper.statusActive(), null, 
								additionalFields, null, lang));
			} else {
				brands = new GenericAdapterImpl<BrandAdapter>()
						.adaptList(dao.getUsingStatusAndRangeInCache(new Vector<Byte>(), range, user, 
						favoritesOnly ? UserEntityCache.TYPE_FAVORITES_ONLY :
						favoritesFirst ? UserEntityCache.TYPE_FAVORITES_FIRST : UserEntityCache.TYPE_NORMAL_SORT));
			}
			long diff = new Date().getTime() - millisPre;

			for( BrandAdapter brand : brands ) {
				if (favorites.contains(brand.getIdentifier()))
					brand.setFavorite(true);
			}

			// Logs the result
			log.info("Number of brands found [" + brands.size() + "] in " + diff + " millis");
			returnValue = this.getJSONRepresentationFromArrayOfObjects(brands, this.obtainOutputFields(bzFields, level));
			
			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.BrandListBzService"),
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
