package mobi.allshoppings.bdb.bz.spi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Email;
import com.inodes.util.CollectionFactory;

import mobi.allshoppings.auth.AuthHelper;
import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.bdb.bz.validation.BDBUserBzValidation;
import mobi.allshoppings.dao.AuditLogDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.AuditLog;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.tools.CommonValidator;
import mobi.allshoppings.tools.Range;


/**
 *
 */
public class BDBUserBzServiceJSONImpl
extends BDBCrudBzServiceJSONImpl<User>
implements BDBCrudBzService {

	private static final Logger log = Logger.getLogger(BDBUserBzServiceJSONImpl.class.getName());

	@Autowired
	private UserDAO dao;
	@Autowired
	private AuditLogDAO alDao;
	@Autowired
	private AuthHelper authHelper;
	@Autowired
	private CommonValidator commonValidator;
	@Autowired
	private BDBUserBzValidation userValidator;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private KeyHelper keyHelper;

	private static final String[] MANDATORY_ADD_FIELDS = new String[] {
		"contactInfo.mail",
		"password"
	};
	
	private static final String[] MANDATORY_UPDATE_FIELDS = new String[] {
		"identifier",
		"contactInfo.mail"
	};
	
	private static final String[] MANDATORY_DELETE_FIELDS = new String[] {
		"identifier"
	};
	
	@Override
	public String[] getListFields() {
		return new String[] {
				"identifier",
				"avatarId",
				"firstname",
				"lastname",
				"fullname",
				"creationDateTime",
				"statusModificationDateTime",
				"lastUpdate",
				"viewLocation.country",
				"lastLogin",
				"securitySettings.status",
				"securitySettings.role",
				"activityStatus",
				"gender"
		};
	}

	@Override
	public String retrieve()
	{
		long start = markStart();
		JSONObject returnValue;
		try {
			// obtain the id and validates the auth token
			User user = getUserFromToken();
			final String userToViewId = (obtainIdentifier(GENERAL_IDENTIFIER_KEY) == null)
					? user.getIdentifier() : obtainIdentifier("identifier");

					// Check User type
					if(!userToViewId.equals("me")) {
						if (user.getIdentifier().equals(userToViewId) || userToViewId == null ) {
							user = dao.get(user.getIdentifier(), true);
						} else { // I want to see public info from a user 
							user = dao.get(userToViewId);
						}
					}

					// Get the output fields
					String[] fields = this.obtainOutputFields(User.class, null);

					// Obtains the user JSON representation
					returnValue = getJSONRepresentationFromObject(user, fields);

					// track action
					trackerHelper.enqueue( user, getRequestIP(),
							getRequestAgent(), getFullRequestURI(),
							getI18NMessage("es_AR", "service.UserBzService"), 
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

	@Override
	public String add(final JsonRepresentation entity) {
		String response;
		long start = markStart();
		try {
			// validate authToken
			final User user = getUserFromToken();
			final JSONObject obj = entity.getJsonObject();

			//check mandatory fields
			log.info("check mandatory fields");
			checkMandatoryFields(obj, MANDATORY_ADD_FIELDS);

			if(!user.getSecuritySettings().getRole().equals(Role.ADMIN))
				throw ASExceptionHelper.forbiddenException();

			//checks that the user email is not existent to avoid duplicates
			User altUser = dao.getByEmail(new Email(obj.getJSONObject("contactInfo").getString("mail")));
			if( altUser != null ) throw ASExceptionHelper.alreadyExistsException();

			log.info("setting user attributes");
			final User newUser = new User();

			setPropertiesFromJSONObject(obj, newUser, EMPTY_SET);

			keyHelper.setKeyWithIdentifier(newUser, obtainLowerCaseIdentifierFromJSON(obj));
			newUser.getSecuritySettings().setPassword(authHelper.encodePassword(obj.getString("password")));

			if(null == newUser.getViewLocation() || !StringUtils.hasText(newUser.getViewLocation().getCountry())) {
				newUser.setViewLocation(new ViewLocation());
				newUser.getViewLocation().setCountry(systemConfiguration.getDefaultCountry());
			}

			newUser.getAddress().setCountry(newUser.getViewLocation().getCountry());
			newUser.setStatusModificationDateTime(new Date());

			// validates user data
			if (!validateFields(newUser)) {
				throw ASExceptionHelper.invalidArgumentsException(INVALID_DEFAULT_FIELDS);
			}

			dao.create(newUser);
			log.info("user created: " + newUser.getFullname());

			response = generateJSONOkResponse().toString();

			// track action
			trackerHelper.enqueue( newUser, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.UserBzService.add"), 
					null, null);

		} catch (JSONException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} catch (ASException | IOException e) {
			if( e instanceof ASException && ((ASException)e).getErrorCode() == ASExceptionHelper.AS_EXCEPTION_ALREADYEXISTS_CODE)
				return getJSONRepresentationFromException(ASExceptionHelper.alreadyExistsException()).toString();

			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(e).toString();
		} finally {
			markEnd(start);
		}

		return response;
	}

	@Override
	public String change(JsonRepresentation entity) {

		long start = markStart();
		try {

			// validate authToken
			final User user = getUserFromToken();
			final JSONObject obj = entity.getJsonObject();

			//check mandatory fields
			log.info("check mandatory fields");
			checkMandatoryFields(obj, MANDATORY_UPDATE_FIELDS);

			// checks if the authToken's user has perms to touch the identifier object
			final String identifier = obtainLowerCaseIdentifierFromJSON(obj);
			if (!StringUtils.hasText(identifier) || 
					(!user.getSecuritySettings().getRole().equals(Role.ADMIN) && !user.getIdentifier().equalsIgnoreCase(identifier))) {
				throw ASExceptionHelper.forbiddenException();
			}

			User modUser = dao.get(identifier, true);
			setPropertiesFromJSONObject(obj, modUser, EMPTY_SET);
			user.setStatusModificationDateTime(new Date());

			// validates user data
			if (!validateFields(modUser)) {
				throw ASExceptionHelper.invalidArgumentsException(INVALID_DEFAULT_FIELDS);
			}

			dao.update(modUser);
			log.info("user updated");

			// track action
			trackerHelper.enqueue( user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.UserBzService.put"), 
					null, null);

		} catch (JSONException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} catch (ASException | IOException e) {
			if( e instanceof ASException && ((ASException)e).getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE)
				return getJSONRepresentationFromException(ASExceptionHelper.notFoundException()).toString();

			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(e).toString();
		} finally {
			markEnd(start);
		}

		return generateJSONOkResponse().toString();
	}

	public String list() {
    	long start = markStart();
		JSONObject returnValue = null;
		try {
			List<User> list = new ArrayList<User>();
			
			// validate authToken
			User user = this.getUserFromToken();

			// get range, if not defined use default value
			Range range = this.obtainRange();
			
			// Get Search query
			String q = this.obtainStringValue(Q, null);

			// Additional Search Fields
			Map<String, String> additionalFields = CollectionFactory.createMap();

			// Get Status query
			String status = this.obtainStringValue(STATUS, null);
	
			// Get order
			String order = this.obtainStringValue(ORDER, null);

			// Get role
			String role = this.obtainStringValue(ROLE, null);

			// Get the output fields
			String[] fields = this.obtainOutputFields(myClazz, getListFields());

			// Get the language;
			String lang = this.obtainLang();
			
			Map<String,String> attributes = CollectionFactory.createMap();
			
			// Status filter
			List<Integer> statusList = null;
			if( StringUtils.hasText(status)) {
				statusList = CollectionFactory.createList();
				String parts[] = status.split(",");
				for(String part : parts ) {
					try {
						statusList.add(Integer.valueOf(part));
					} catch( Exception e) {}
				}
			}

			// Role filter
			List<Integer> roleList = null;
			if( StringUtils.hasText(role)) {
				roleList = CollectionFactory.createList();
				String parts[] = role.split(",");
				for(String part : parts ) {
					try {
						roleList.add(Integer.valueOf(part));
					} catch( Exception e) {}
				}
			}
			
			// retrieve all elements
			long millisPre = new Date().getTime();
			if( q != null && !q.trim().equals("")) {
				
				if( StringUtils.hasText(status) )
					additionalFields.put("status", status);
				
				if( StringUtils.hasText(role)) 
					additionalFields.put("role", role);

				list = myDao.getUsingIndex(
								myClazz.getName(), q, user.getSecuritySettings().getRole().equals(Role.ADMIN) 
								? null : user.getViewLocation(),
								StringUtils.hasText(status) 
								? statusList : null, range, 
								additionalFields, order, lang);
			} else {
				list = dao.getUsingLastUpdateStatusAndRangeAndRole(null, null,
						false, statusList, range, order, roleList,
						attributes, true);
			}
			
			long diff = new Date().getTime() - millisPre;
			
			for( User u : list ) {
				List<AuditLog> l2 = alDao.getUsingUserAndTypeAndRange(u.getIdentifier(), AuditLog.EVENT_LOGIN, new Range(0,1), "eventDate DESC", null, true);
				if( l2.size() > 0 ) {
					u.setLastLogin(l2.get(0).getEventDate());
				} else {
					u.setLastLogin(null);
				}
			}
			
			// Logs the result
			log.info("Number of elements found [" + list.size() + "] in " + diff + " millis");
			returnValue = this.getJSONRepresentationFromArrayOfObjects(list, fields);
			
			if( attributes.containsKey("recordCount"))
				returnValue.put("recordCount", Long.valueOf(attributes.get("recordCount")));
			else 
				returnValue.put("recordCount", list.size());
				
			
			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service." + myClazz.getSimpleName() + "BzService"),
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

	private boolean validateFields(final User user) throws ASException {

		if (!commonValidator.validateIdentifier(user.getIdentifier())) {
			INVALID_DEFAULT_FIELDS.add("userId");
		}

		final String lastName = user.getLastname();
		final String name = user.getFirstname();

		if( null == name )
			user.setFirstname("");

		if( null == lastName )
			user.setLastname("");

		final String city = user.getAddress() != null ? user.getAddress().getCity() : null;
		if(null == city)
			user.getAddress().setCity("");

		final String country = user.getAddress() != null ? user.getAddress().getCountry() : null;
		if(null == country)
			user.getAddress().setCountry("");

		final String gender = user.getGender();
		if(null == gender)
			user.setGender("female");

		final String fullname = user.getFullname();
		if(null == fullname) {
			user.setFirstname("");
			user.setLastname("");
		}

		if (user.getContactInfo().getMail() != null &&
				commonValidator.validateEmailAddress(user.getContactInfo().getMail().getEmail())
				&& !userValidator.validateUniqueMail(user)) {

			INVALID_DEFAULT_FIELDS.add("mail");
		}

		return INVALID_DEFAULT_FIELDS.isEmpty();
	}

	@Override
	public String delete(JsonRepresentation entity) {
		long start = markStart();
		try {

			// validate authToken
			final User user = getUserFromToken();
			final JSONObject obj = entity.getJsonObject();

			//check mandatory fields
			log.info("check mandatory fields");
			checkMandatoryFields(obj, MANDATORY_DELETE_FIELDS);

			// checks if the authToken's user has perms to touch the identifier object
			final String identifier = obtainLowerCaseIdentifierFromJSON(obj);
			if (!StringUtils.hasText(identifier) || 
					(!user.getSecuritySettings().getRole().equals(Role.ADMIN) && !user.getIdentifier().equalsIgnoreCase(identifier))) {
				throw ASExceptionHelper.forbiddenException();
			}

			dao.delete(identifier);
			log.info("user deleted");

			// track action
			trackerHelper.enqueue( user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.UserBzService.delete"), 
					null, null);

		} catch (JSONException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} catch (ASException | IOException e) {
			if( e instanceof ASException && ((ASException)e).getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE)
				return getJSONRepresentationFromException(ASExceptionHelper.notFoundException()).toString();

			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(e).toString();
		} finally {
			markEnd(start);
		}

		return generateJSONOkResponse().toString();
	}


	@Override
	public void config() {
		setMyDao(dao);
		setMyClazz(User.class);
	}

	@Override
	public void setKey(User obj, JSONObject seed) throws ASException {
		// TODO Auto-generated method stub
		
	}
}
