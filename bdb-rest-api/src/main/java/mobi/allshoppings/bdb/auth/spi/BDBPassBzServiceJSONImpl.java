package mobi.allshoppings.bdb.auth.spi;

import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import mobi.allshoppings.auth.AuthHelper;
import mobi.allshoppings.bdb.auth.BDBPassBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.mail.MailHelper;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity.Role;


public class BDBPassBzServiceJSONImpl  extends BDBRestBaseServerResource implements BDBPassBzService {
	private static final Logger log = Logger.getLogger(BDBPassBzServiceJSONImpl.class.getName());
	private static final String[] DEFAULT_FIELDS = { "currentPassword","newPassword"};
	private static final String[] SET_FIELDS = { "identifier","newPassword"};
	private static final String CURRENT_PASSWORD = "currentPassword";
	private static final String NEW_PASSWORD = "newPassword";
	private static final String EMAIL = "email";
	private static final String LANG = "lang";
	
	@Autowired
	UserDAO dao;
	@Autowired
	AuthHelper authHelper;
	@Autowired
	MailHelper mailHelper;
	@Autowired
	MessageSource messageSource;

	/**
	 * Change a user password
	 */
	@Override
	public String change(JsonRepresentation entity) {
		JSONObject jsonOut = new JSONObject();
		try{
			User user = this.getUserFromToken();
			JSONObject obj = entity.getJsonObject();
			
			checkMandatoryFields(obj, DEFAULT_FIELDS);
			String currentPassword = authHelper.encodePassword(obj.getString(CURRENT_PASSWORD));
			String newPassword = authHelper.encodePassword(obj.getString(NEW_PASSWORD));
			if(!user.getSecuritySettings().getPassword().equals(currentPassword)){
				throw ASExceptionHelper.invalidArgumentsException();
			}
			user.getSecuritySettings().setPassword(newPassword);
			dao.updateWithoutChangingMail(user);
			jsonOut = this.generateJSONOkResponse();
			// TODO de la mierda
		}catch(ASException e){
			log.log(Level.SEVERE, "exception catched", e);
			jsonOut = this.getJSONRepresentationFromException(e);
			
		}catch(Exception e){
			log.log(Level.SEVERE, "exception catched", e);
			jsonOut = this.getJSONRepresentationFromException(e);
		}
		return jsonOut.toString();
	}

	/**
	 * Force a user password
	 */
	@Override
	public String force(JsonRepresentation entity) {
		JSONObject jsonOut = new JSONObject();
		try{
			User user = this.getUserFromToken();
			JSONObject obj = entity.getJsonObject();

			if(!user.getSecuritySettings().getRole().equals(Role.ADMIN)) {
				return this.getJSONRepresentationFromException(ASExceptionHelper.forbiddenException()).toString();
			}
			
			checkMandatoryFields(obj, SET_FIELDS);
			
			User target = dao.get(obj.getString("identifier"), true);
			if(target == null) {
				return this.getJSONRepresentationFromException(ASExceptionHelper.notFoundException()).toString();
			}
			
			String newPassword = authHelper.encodePassword(obj.getString(NEW_PASSWORD));
			target.getSecuritySettings().setPassword(newPassword);
			dao.updateWithoutChangingMail(target);
			jsonOut = this.generateJSONOkResponse();

		}catch(ASException e){
			log.log(Level.SEVERE, "exception catched", e);
			jsonOut = this.getJSONRepresentationFromException(e);
			
		}catch(Exception e){
			log.log(Level.SEVERE, "exception catched", e);
			jsonOut = this.getJSONRepresentationFromException(e);
		}
		return jsonOut.toString();
	}

	/**
	 * Recovers a user password
	 */
	@Override
	public String recover() {
		JSONObject jsonOut = new JSONObject();
		try {
			String mail = this.obtainLowerCaseStringValue(EMAIL, "");
			String lang = this.obtainLowerCaseStringValue(LANG, "");
			if (!StringUtils.hasText(mail)) {
				throw ASExceptionHelper.notAcceptedException();
			}

			User user = dao.getByEmail(mail);
			if( user == null ) throw ASExceptionHelper.notFoundException();
			String newPassword = authHelper.generateAltPasswordForUser(user);
			dao.updateWithoutChangingMail(user);
			
			// mail helper
			Locale loc = normalizeLocale(lang);
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("newPassword", newPassword);
			parameters.put("recoveryTokenValidity", user.getSecuritySettings().getRecoveryTokenValidity());
			mailHelper.sendMessage(user, getI18NMessage(loc.toString(), "mail.passwordrecoverymail"),
					"mails/apppasswordrecovery.vm",
					parameters);

			jsonOut = this.generateJSONOkResponse();
		} catch (Exception e) {
			log.log(Level.SEVERE, "exception catched", e);
			jsonOut = this.getJSONRepresentationFromException(e);
		}
		return jsonOut.toString();
	}

}
