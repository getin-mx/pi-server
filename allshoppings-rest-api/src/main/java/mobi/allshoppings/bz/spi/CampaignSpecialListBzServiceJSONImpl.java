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

import com.inodes.util.CollectionFactory;

import mobi.allshoppings.bz.CampaignSpecialListBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.CampaignSpecialDAO;
import mobi.allshoppings.dao.FavoriteDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.CampaignSpecial;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.adapter.CampaignSpecialAdapter;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.Range;

/**
 *
 */
public class CampaignSpecialListBzServiceJSONImpl extends RestBaseServerResource implements CampaignSpecialListBzService {

    private static final Logger log = Logger.getLogger(CampaignSpecialListBzServiceJSONImpl.class.getName());

    @Autowired
    private CampaignSpecialDAO dao;
    @Autowired
    private FavoriteDAO favoriteDao;

    private static String APPID = "appId";
    private static String BRANDID = "brandId";
    
    private BzFields bzFields = BzFields.getBzFields(getClass());

    @Override
    public String retrieve() {
    	
		long start = markStart();
		JSONObject returnValue = null;
		try {
			List<CampaignSpecialAdapter> list = new ArrayList<CampaignSpecialAdapter>();
			
			// validate authToken
			User user = this.getUserFromToken();

			// get level, if not defined use default value
			String level = this.obtainStringValue(LEVEL, BzFields.LEVEL_LIST);
			Range range = this.obtainRange();
			
			// Get favoritesFirst Option
			List<String> favorites = favoriteDao.getIdsUsingUserAndKind(user, EntityKind.KIND_CAMPAIGN_SPECIAL);

			// Get Search query
			String q = this.obtainStringValue(Q, null);

			// Get app Id
			String appId = this.obtainStringValue(APPID, null);
			if( !StringUtils.hasText(appId) )
				throw ASExceptionHelper.invalidArgumentsException("appId");
			
			// Get brand id
			String brandId = this.obtainStringValue(BRANDID, null);
			
			// Additional Search Fields
			Map<String, String> additionalFields = CollectionFactory.createMap();

			// Get the language;
			String lang = this.obtainLang();
			
			// retrieve all brands
			long millisPre = new Date().getTime();
			if( q != null && !q.trim().equals("")) {
				if( StringUtils.hasText(appId)) additionalFields.put("appIds", appId);
				if( StringUtils.hasText(brandId)) additionalFields.put("brands", brandId);
				list = new GenericAdapterImpl<CampaignSpecialAdapter>().adaptList(dao
						.getUsingIndex(CampaignSpecial.class.getName(), q, user.getViewLocation(),
								StatusHelper.statusActive(), null, 
								additionalFields, null, lang));
			} else {
				list = new GenericAdapterImpl<CampaignSpecialAdapter>()
						.adaptList(dao.getUsingAppAndBrandAndStatusAndRange(appId, brandId, new Vector<Integer>(), 
								range, "validFrom DESC", false));
			}
			long diff = new Date().getTime() - millisPre;

			for( CampaignSpecialAdapter cs : list ) {
				if (favorites.contains(cs.getIdentifier()))
					cs.setFavorite(true);
			}

			// Logs the result
			log.info("Number of campaign specials found [" + list.size() + "] in " + diff + " millis");
			returnValue = this.getJSONRepresentationFromArrayOfObjects(list, this.obtainOutputFields(bzFields, level));
			
			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.CampaignSpecialListBzService"),
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
