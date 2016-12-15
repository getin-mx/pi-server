package mobi.allshoppings.bz.spi;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.FavoritesBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.FavoriteDAO;
import mobi.allshoppings.dao.FinancialEntityDAO;
import mobi.allshoppings.dao.OfferDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Favorite;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.ranking.PointsService;
import mobi.allshoppings.uec.UserEntityCacheBzService;


/**
 *
 */
public class FavoritesBzServiceJSONImpl
        extends RestBaseServerResource
        implements FavoritesBzService {

    private static final Logger log = Logger.getLogger(FavoritesBzServiceJSONImpl.class.getName());
    
    @Autowired
    private FavoriteDAO dao;
    @Autowired
    private BrandDAO brandDao;
    @Autowired
    private ShoppingDAO shoppingDao;
    @Autowired
    private OfferDAO offerDao;
    @Autowired
    private StoreDAO storeDao;
    @Autowired
    private FinancialEntityDAO financialEntityDao;
    @Autowired
    private UserEntityCacheBzService uecService;
    @Autowired
    private PointsService pointsService;
    
    private BzFields bzFields = BzFields.getBzFields(getClass());
    
    private final static String IDENTIFIER = "entityId";
    private final static String ENTITY_KIND = "kind";
    private final static String ENTITY_ID = "entityId";

    @Override
    public String remove() {
    	
    	long start = markStart();
        long points = 0;
        try {
            final User user = getUserFromToken();
    		final String userId = obtainUserIdentifier();
    		if(!user.getIdentifier().equals(userId)) {
    			throw ASExceptionHelper.forbiddenException();
    		}
    		
    		final String entityId = obtainStringValue(IDENTIFIER, null);
            if(!StringUtils.hasText(entityId)) throw ASExceptionHelper.invalidArgumentsException(IDENTIFIER);
            final Integer entityKind = obtainIntegerValue(ENTITY_KIND, null);
            if(null == entityKind) throw ASExceptionHelper.invalidArgumentsException(ENTITY_KIND);
            
            
            final Favorite favorite = dao.getUsingUserAndEntityAndKind(user, entityId, entityKind, true);
            if( favorite != null ) { 
            	dao.delete(favorite.getIdentifier());

            	uecService.rebuildUsingUserAndKind(user, favorite.getEntityKind());
            	if(!favorite.getEntityKind().equals(EntityKind.KIND_OFFER))
            		uecService.rebuildUsingUserAndKind(user, EntityKind.KIND_OFFER);

                // Points updater
                points = pointsService.calculatePointsForAction(userId, PointsService.ACTION_UNFAVORITE,
						favorite.getEntityKind(), favorite.getEntityId(), null);
				pointsService.enqueue(userId, PointsService.ACTION_UNFAVORITE,
						favorite.getEntityKind(), favorite.getEntityId(), null);
            }

            
            // track action
    		trackerHelper.enqueue( user, getRequestIP(),
    				getRequestAgent(), getFullRequestURI(),
    				getI18NMessage("es_AR", "service.FavoritesBzService.remove"), 
    				null, null);

        } catch (ASException | IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
            return getJSONRepresentationFromException(e).toString();
        } finally {
        	markEnd(start);
        }

        return addPointsUpdate(generateJSONOkResponse(), points).toString();
    }

    @Override
    public String add(final JsonRepresentation entity) {
    	
    	long start = markStart();
    	long points = 0;
        try {
            final User user = getUserFromToken();
    		final String userId = obtainUserIdentifier();
    		if(!user.getIdentifier().equals(userId)) {
    			throw ASExceptionHelper.forbiddenException();
    		}

    		final JSONObject obj = entity.getJsonObject();
    		obj.put("entityKind", obj.get("kind"));

            //check mandatory fields
            log.info("check mandatory fields");
            if (!hasDefaultParameters(obj, bzFields.DEFAULT_FIELDS.toArray(EMPTY_STRING_ARRAY))) {
                throw ASExceptionHelper.invalidArgumentsException(INVALID_DEFAULT_FIELDS);
            }
            
            //check that kind is between ranges
            Integer entityKind = obj.getInt(ENTITY_KIND);
            if( !EntityKind.isKindValid(entityKind) ) {
            	throw ASExceptionHelper.invalidArgumentsException(ENTITY_KIND);
            }

            //checks that the selected favorite exists
            ModelKey mk = null;
            String entityId = obj.getString(ENTITY_ID); 
            if( entityKind == EntityKind.KIND_BRAND ) {
            	mk = brandDao.get(entityId, true); 
            } else if ( entityKind == EntityKind.KIND_SHOPPING ) {
            	mk = shoppingDao.get(entityId, true);
            } else if ( entityKind == EntityKind.KIND_OFFER ) {
            	mk = offerDao.get(entityId, true);
            } else if ( entityKind == EntityKind.KIND_STORE ) {
            	mk = storeDao.get(entityId, true);
            } else if ( entityKind == EntityKind.KIND_FINANCIAL_ENTITY ) {
            	mk = financialEntityDao.get(entityId, true);
            }
            if( mk == null ) {
            	throw ASExceptionHelper.invalidArgumentsException(ENTITY_ID);
            }

            //checks for duplicates
            try {
	            Favorite check = dao.getUsingUserAndEntityAndKind(user, 
	            		obj.getString(ENTITY_ID), obj.getInt(ENTITY_KIND), true);
	            if( check != null ) {
	            	throw ASExceptionHelper.alreadyExistsException();
	            }
            } catch( ASException e ) {
            	if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
            		throw e;
            	}
            }

            //if we got here... then we are ok!
            final Favorite favorite = new Favorite();
            setPropertiesFromJSONObject(obj, favorite, bzFields.READONLY_FIELDS);
            favorite.setUserId(userId);
            favorite.setKey(dao.createKey());
            // creates the favorite entity in an isolated transaction
            dao.create(favorite);

            // Points updater
            points = pointsService.calculatePointsForAction(userId, PointsService.ACTION_FAVORITE,
					favorite.getEntityKind(), favorite.getEntityId(), null);
			pointsService.enqueue(userId, PointsService.ACTION_FAVORITE,
					favorite.getEntityKind(), favorite.getEntityId(), null);

           	uecService.rebuildUsingUserAndKind(user, favorite.getEntityKind());
           	if(!favorite.getEntityKind().equals(EntityKind.KIND_OFFER))
           		uecService.rebuildUsingUserAndKind(user, EntityKind.KIND_OFFER);
		           
            // track action
    		trackerHelper.enqueue( user, getRequestIP(),
    				getRequestAgent(), getFullRequestURI(),
    				getI18NMessage("es_AR", "service.FavoritesBzService.add"), 
    				null, null);

            log.info("add to Favorties end");

        } catch (JSONException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
            return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
        } catch (ASException | IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
            return getJSONRepresentationFromException(e).toString();
        } finally {
        	markEnd(start);
        }

        return addPointsUpdate(generateJSONOkResponse(), points).toString();
    }

}
