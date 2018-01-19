package mobi.allshoppings.bdb.bz.spi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bdb.bz.BDBPostBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.ProcessDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Process;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.interfaces.Indexable;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.tools.IndexHelper;
import mobi.allshoppings.process.ProcessUtils;

public class BDBRequestProcessBzServiceJSONImpl extends BDBRestBaseServerResource implements BDBPostBzService {

	private static final Logger log = Logger.getLogger(BDBRequestProcessBzServiceJSONImpl.class.getName());
	
	@Autowired
	private ProcessDAO dao;
	@Autowired
	private ShoppingDAO shoppingDao;
	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private IndexHelper indexHelper;
	@Autowired
	private ProcessUtils processHelper;

	
	public String change(JsonRepresentation entity) {
		long start = markStart();
		try {

			// validate authToken
			final User user = getUserFromToken();
			final JSONObject obj = entity.getJsonObject();

			final String entityId = obj.getString("entityId");
			final byte entityKind = (byte) obj.getInt("entityKind");
			final String fromDate = obj.getString("fromDate");
			final String toDate = obj.getString("toDate");
			final Boolean processAPHE = obj.getBoolean("processAPHE");
			String brandId = null;
			
			Process p = new Process();
			
			p.setEntityId(entityId);
			p.setEntityKind(entityKind);
			p.setData(obj.toString());
			p.setUserId(user.getIdentifier());
			p.setStatus(StatusAware.STATUS_PREPARED);
			p.setProcessType(Process.PROCESS_TYPE_GENERATE_VISITS);
			p.setGenerateAPHE(processAPHE);
			
			String name = entityId;
			if( p.getEntityKind() == EntityKind.KIND_SHOPPING) {
				try {
					Shopping tmp = shoppingDao.get(entityId);
					name = tmp.getName();
				} catch( Exception e ) {}
			} else if( p.getEntityKind() == EntityKind.KIND_STORE) {
				try {
					Store tmp = storeDao.get(entityId);
					brandId = tmp.getBrandId();
					name = tmp.getName();
				} catch( Exception e ) {}
			}
			
			p.setName("Reproceso de " + name + " de: " + fromDate +" hasta: " + toDate);
			p.setKey(dao.createKey());		
			
			// Checks not to process a forbidden brand
			if( StringUtils.hasText(brandId) && systemConfiguration.getForbiddenBrandsToReprocess().contains(brandId))
				throw ASExceptionHelper.defaultException("Cannot process brand " + brandId, new Exception());
			
			dao.create(p);
			
			// index object if needed
			if( p instanceof Indexable ) {
				indexHelper.indexObject(p);
				log.info("object indexed: " + p.getIdentifier());
			}

			processHelper.startProcess(p.getIdentifier(), false);
			
			// track action
			trackerHelper.enqueue( user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.BDBRequestProcessBzService.put"), 
					null, null);

			return getJSONRepresentationFromObject(p, obtainOutputFields(Process.class)).toString();
			
		} catch (JSONException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} catch (Exception e) {
			if( e instanceof ASException && ((ASException)e).getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE)
				return getJSONRepresentationFromException(ASExceptionHelper.notFoundException()).toString();
			
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(e).toString();
		} finally {
			markEnd(start);
		}
		
	}
}
