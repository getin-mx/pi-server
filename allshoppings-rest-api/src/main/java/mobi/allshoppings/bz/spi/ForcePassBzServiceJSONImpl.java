package mobi.allshoppings.bz.spi;

import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.auth.AuthHelper;
import mobi.allshoppings.bz.ForcePassBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.model.User;

import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class ForcePassBzServiceJSONImpl
extends RestBaseServerResource 
implements ForcePassBzService {

	private static final Logger log = Logger.getLogger(ForcePassBzServiceJSONImpl.class.getName());

	@Autowired
	private UserDAO userDao;
	@Autowired
	private AuthHelper authHelper;

	@Override
	public String post(final JsonRepresentation entity) {

		long start = markStart();
		try {

			final JSONObject json = entity.getJsonObject();
			User user = userDao.get(json.getString("identifier"), true);
			if( user != null ) {
				if(json.has("plain") && json.getBoolean("plain")) {
					user.getSecuritySettings().setPassword(json.getString("password"));
				} else {
					user.getSecuritySettings().setPassword(authHelper.encodePassword(json.getString("password")));
				}
				userDao.updateWithoutChangingMail(user);
			}

			return generateJSONOkResponse().toString();

		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(e).toString();
		} finally {
			markEnd(start);
		}

	}

}
