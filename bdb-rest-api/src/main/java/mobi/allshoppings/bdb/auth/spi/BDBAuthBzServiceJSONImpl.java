package mobi.allshoppings.bdb.auth.spi;


import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import com.inodes.datanucleus.model.Email;

import mobi.allshoppings.auth.AuthHelper;
import mobi.allshoppings.bdb.auth.BDBAuthBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.AuditLogDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.AuditLog;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity;
import mobi.allshoppings.tools.CommonValidator;

public class BDBAuthBzServiceJSONImpl extends BDBRestBaseServerResource implements BDBAuthBzService {
	private static final String[] DEFAULT_FIELDS = { "identifier", "firstname", "lastname", "avatarId", "tokenValidity" };
	private static Set<String> invalidIdentifierNames = null;
	private static final Logger log = Logger.getLogger(BDBAuthBzServiceJSONImpl.class.getName());
	private static final String PASSWORD_FIELD = "password";

	@Autowired
	private UserDAO dao;
	@Autowired
	private AuditLogDAO alDao;
	@Autowired
	private AuthHelper authHelper;
	@Autowired
	private CommonValidator validator;

	/**
	 * Corresponding to /app/auth GET method. <br>
	 * It validates if a username exists. If the user already exists, it returns
	 * a 405 error. If not, it returns a standard void message
	 */
	@Override
	public String validate() {
		JSONObject jsonOut = new JSONObject();		
		try {
			String userId = this.obtainIdentifier("userId");

			// check for invalid names
			if (invalidIdentifierNames.contains(userId)) {
				throw ASExceptionHelper.notAcceptedException();
			}

			// check valid characters, length, and so on
			if (!validator.validateIdentifier(userId)) {
				throw ASExceptionHelper.notAcceptedException();
			}

			// checks that the new user is a valid email address
			if (!validator.validateEmailAddress(userId)) {
				throw ASExceptionHelper.notAcceptedException();
			}

			try {
				// check if exists on database
				dao.get(userId);
				//exists, so we blow! 
				throw ASExceptionHelper.alreadyExistsException();
			} catch (ASException e) {
				if (e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE) {
					try {
						// check if exists as an email
						if(dao.getByEmail(new Email(userId)) != null) {
							//exists, so we blow! 
							throw ASExceptionHelper.alreadyExistsException();
						} else {
							// does not exists
							jsonOut = this.generateJSONOkResponse();
						}
					} catch (ASException e1) {
						if (e1.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE) {
							// does not exists
							jsonOut = this.generateJSONOkResponse();
						} else {
							// other error
							jsonOut = this.getJSONRepresentationFromException(e1);
						}
					}
				} else {
					// other error
					jsonOut = this.getJSONRepresentationFromException(e);
				}
			}catch (Exception e) {
				jsonOut = this.getJSONRepresentationFromException(e);
			}
		} catch (ASException e) {
			jsonOut = this.getJSONRepresentationFromException(e);
		} catch (Exception e) {
			jsonOut = this.getJSONRepresentationFromException(e);
		}

		return jsonOut.toString();
	}

	/**
	 * Corresponding to /app/auth POST method.<br>
	 * Issues a login and return the user token if the login was valid, or an
	 * error if not
	 */
	@Override
	public String login(JsonRepresentation entity) {
		JSONObject jsonOut;
		JSONObject jsonIn = null;
		long start = markStart();
		try {
			User user;
			jsonIn = entity.getJsonObject();
			String identifier = this.obtainLowerCaseIdentifierFromJSON(jsonIn);
			String password = authHelper.encodePassword(jsonIn.getString(PASSWORD_FIELD));

			log.finest("DATA:["+identifier +"]["+ password +"]");
			user = dao.get(identifier, false);
			if( user.getSecuritySettings().getRole() > UserSecurity.Role.USER ) {
				if (user.getSecuritySettings().getPassword() != null) {
					if (password.equals(user.getSecuritySettings().getPassword()) 
							|| password.equals(user.getSecuritySettings().getAltPassword()) 
							&& new Date().before(user.getSecuritySettings().getRecoveryTokenValidity())) {
						String token = user.getSecuritySettings().getAuthToken();

						if( password.equals(user.getSecuritySettings().getAltPassword())) {
							authHelper.applyAltPasswordAfterLogin(user);
							dao.updateWithoutChangingMail(user);
						}

						jsonOut = this.getJSONRepresentationFromObject(user, DEFAULT_FIELDS);

						// validate the token
						Date now = new Date();
						if (token == null || user.getSecuritySettings().getAuthTokenValidity() == null || 
								now.after(user.getSecuritySettings().getAuthTokenValidity())) {
							user = dao.get(identifier, true);
							token = authHelper.generateTokenForUser(user);
							dao.update(user);
						}

						jsonOut.put("token", token);
						jsonOut.put("tokenValidity", DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT
								.format(user.getSecuritySettings().getAuthTokenValidity()));
						jsonOut.put("email", user.getEmail());
						jsonOut.put("role", user.getSecuritySettings().getRole());
						
						// Saves Audit Log
						AuditLog al = new AuditLog();
						al.setUserId(user.getIdentifier());
						al.setEventDate(new Date());
						al.setEventType(AuditLog.EVENT_LOGIN);
						al.setKey(alDao.createKey());
						alDao.create(al);

						// track action
						trackerHelper.enqueue( user, getRequestIP(),
								getRequestAgent(), getFullRequestURI(),
								getI18NMessage("es_AR", "service.UserBzService.login"), 
								null, null);

					} else {
						log.info("forbiden wrong data "  + (jsonIn == null ? "[null]" : jsonIn.toString()));
						jsonOut = this.getJSONRepresentationFromException(ASExceptionHelper.forbiddenException());
					}
				} else {
					log.info("forbiden wrong data 2 "  + (jsonIn == null ? "[null]" : jsonIn.toString()));
					jsonOut = this.getJSONRepresentationFromException(ASExceptionHelper.forbiddenException());
				}
			} else {
				log.info("forbiden wrong data 2 "  + (jsonIn == null ? "[null]" : jsonIn.toString()));
				jsonOut = this.getJSONRepresentationFromException(ASExceptionHelper.forbiddenException());
			}

		} catch (Exception e) {
			log.info("forbiden wrong data "  + (jsonIn == null ? "[null]" : jsonIn.toString()));
			jsonOut = this.getJSONRepresentationFromException(ASExceptionHelper.forbiddenException());
		} finally {
			markEnd(start);
		}

		return jsonOut.toString();
	}

	/**
	 * Logs a user out, disposing its token
	 */
	@Override
	public String logout() {
		JSONObject jsonOut = new JSONObject();
		try {
			this.invalidateToken();
			jsonOut = this.generateJSONOkResponse();

		} catch (ASException e) {
			if (e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ||
					e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE ||
					e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE ) {
				// does not exists
				jsonOut = this.generateJSONOkResponse();
			} else {
				// other error
				jsonOut = this.getJSONRepresentationFromException(e);
			}
		}catch (Exception e) {
			jsonOut = this.getJSONRepresentationFromException(e);
		}

		return jsonOut.toString();
	}
}
