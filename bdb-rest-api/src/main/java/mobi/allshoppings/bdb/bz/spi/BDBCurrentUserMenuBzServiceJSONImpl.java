package mobi.allshoppings.bdb.bz.spi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bdb.bz.BDBGetBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.UserMenuDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserMenu;


/**
 *
 */
public class BDBCurrentUserMenuBzServiceJSONImpl extends BDBRestBaseServerResource implements BDBGetBzService {

	private static final Logger log = Logger.getLogger(BDBCurrentUserMenuBzServiceJSONImpl.class.getName());

	@Autowired
	private UserMenuDAO dao;
		
	/**
	 * Obtains information about a user
	 * 
	 * @return A JSON representation of the selected fields for a user
	 */
	@Override
	public String retrieve()
	{
		long start = markStart();
		JSONObject returnValue;
		try {
			// obtain the id and validates the auth token
			User user = getUserFromToken();
			UserMenu obj = null;
			
			try {
				obj = dao.get(user.getIdentifier(), true);
			} catch( ASException e ) {
				obj = dao.getByRole(user.getSecuritySettings().getRole(), true);
			}
			
			// Get the output fields
			String[] fields = this.obtainOutputFields(UserMenu.class, null);

			// Obtains the user JSON representation
			returnValue = getJSONRepresentationFromObject(obj, fields);

			// track action
			trackerHelper.enqueue( user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.UserMenuBzService"), 
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
			returnValue = getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e));
		} finally {
			markEnd(start);
		}
		return returnValue.toString();
	}
}
