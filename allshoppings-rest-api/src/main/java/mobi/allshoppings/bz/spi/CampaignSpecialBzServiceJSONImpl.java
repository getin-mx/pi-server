package mobi.allshoppings.bz.spi;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bz.CampaignSpecialBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.CampaignSpecialDAO;
import mobi.allshoppings.dao.FavoriteDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.CampaignSpecial;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Favorite;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.adapter.CampaignSpecialAdapter;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;


/**
 *
 */
public class CampaignSpecialBzServiceJSONImpl extends RestBaseServerResource implements CampaignSpecialBzService {

	private static final Logger log = Logger.getLogger(CampaignSpecialBzServiceJSONImpl.class.getName());

	@Autowired
	private CampaignSpecialDAO dao;

	@Autowired
	private SystemConfiguration systemConfiguration;
	
	@Autowired
	private FavoriteDAO favoriteDao;

	private static final String IDENTIFIER = "identifier";
	private BzFields bzFields = BzFields.getBzFields(getClass());

	/**
	 * Obtains information about a brand
	 * 
	 * @return A JSON representation of the selected fields for a brand
	 */
	@Override
	public String retrieve() {
		
		long start = markStart();
		JSONObject returnValue;
		try {
			// validate authToken
			User user = this.getUserFromToken();
			
			// obtain the id
			final String identifier = obtainIdentifier(IDENTIFIER);
			CampaignSpecial obj = dao.get(identifier);
						
			// track action
			trackerHelper.enqueue( user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.CampaignSpecialBzService") + obj.getName(), 
					null, null);

			final CampaignSpecialAdapter cs;

			// Obtains the Campaign Special entity
			String level = obtainStringValue(LEVEL, systemConfiguration.getDefaultLevelOnBrandBzService());
			cs = new GenericAdapterImpl<CampaignSpecialAdapter>().adapt(obj);
			Favorite favorite = favoriteDao.getUsingUserAndEntityAndKind(user, cs.getIdentifier(), EntityKind.KIND_CAMPAIGN_SPECIAL, true);
			if( favorite != null ) cs.setFavorite(true);

			// Obtains the user JSON representation
			final String[] fields = obtainOutputFields(bzFields, level);
			returnValue = getJSONRepresentationFromObject(cs, fields);

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
