package mobi.allshoppings.bz.spi;


import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Email;

import mobi.allshoppings.auth.AuthHelper;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.UserBzService;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.bz.validation.UserBzValidation;
import mobi.allshoppings.dao.CheckinDAO;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.FavoriteDAO;
import mobi.allshoppings.dao.FriendDAO;
import mobi.allshoppings.dao.GeoEntityDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.tools.CommonValidator;


/**
 *
 */
public class UserBzServiceJSONImpl
extends RestBaseServerResource
implements UserBzService {

	private static final Logger log = Logger.getLogger(UserBzServiceJSONImpl.class.getName());

	@Autowired
	private UserDAO dao;
	@Autowired
	private CheckinDAO checkinDao;
	@Autowired
	private AuthHelper authHelper;
	@Autowired
	private CommonValidator commonValidator;
	@Autowired
	private UserBzValidation userValidator;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private KeyHelper keyHelper;
	@Autowired
	private GeoCodingHelper geocoder;
	@Autowired
	private FavoriteDAO favoriteDao;
	@Autowired
	private GeoEntityDAO geoDao;
	@Autowired
	private FriendDAO friendDao;
	@Autowired
	private DeviceInfoDAO diDao;

	private BzFields bzFields = BzFields.getBzFields(getClass());

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
			final String userId = obtainUserIdentifier();
			final String userToViewId = (obtainIdentifier(USER_IDENTIFIER_KEY) == null || 
					obtainIdentifier(USER_IDENTIFIER_KEY).equalsIgnoreCase(USER_IDENTIFIER_ALIAS))
					? userId : obtainIdentifier("userId");
			final User user;
			// get level, if not defined use default value
			String level = systemConfiguration.getDefaultLevelOnUserBzService();

			// Check User type
			if (userId.equals(userToViewId) || userToViewId == null ) {
				level = obtainStringValue(LEVEL, systemConfiguration.getOwnLevelOnUserBzService());
				user = dao.get(userId);
			} else { // I want to see public info from a user 
				user = dao.get(userToViewId);
			}

			// Obtains the user JSON representation
			final String[] fields = obtainOutputFields(bzFields, level);
			returnValue = getJSONRepresentationFromObject(user, fields);

			long favoriteCount = 0;
			try {
				favoriteCount = favoriteDao.getUserFavoriteCount(user.getIdentifier());
			} catch( Exception e ) {
				log.log(Level.WARNING, e.getMessage(), e);
			}

			returnValue.put("friendCount", friendDao.getUserFriendCount(user.getIdentifier()));
			returnValue.put("favoriteCount", favoriteCount);
			returnValue.put("checkinCount", checkinDao.getUserCheckinCount(user.getIdentifier()));

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
			final JSONObject obj = entity.getJsonObject();
//			final String lang = obj.get("lang").toString();
			//check mandatory fields
			log.info("check mandatory fields");
			if (!hasDefaultParameters(obj, bzFields.DEFAULT_FIELDS.toArray(EMPTY_STRING_ARRAY))) {
				throw ASExceptionHelper.invalidArgumentsException(INVALID_DEFAULT_FIELDS);
			}

			//checks that the user email is not existent to avoid duplicates
			User altUser = dao.getByEmail(new Email(obj.get("email").toString()));
			if( altUser != null ) throw ASExceptionHelper.alreadyExistsException();

			log.info("setting user attributes");
			final User user = new User();

			setPropertiesFromJSONObject(obj, user, bzFields.READONLY_FIELDS);

			keyHelper.setKeyWithIdentifier(user, obtainLowerCaseIdentifierFromJSON(obj));
			user.getSecuritySettings().setPassword(authHelper.encodePassword(obj.getJSONObject("securitySettings").getString("password")));
			final String token = authHelper.generateTokenForUser(user);

			if(null == user.getViewLocation() || !StringUtils.hasText(user.getViewLocation().getCountry())) {
				user.setViewLocation(new ViewLocation());
				try {
					if( obj.has("lat") && obj.has("lon")) {
						double lat = obj.getDouble("lat");
						double lon = obj.getDouble("lon");
						GeoPoint p1 = geocoder.getGeoPoint(lat, lon);
						Shopping nearest = geoDao.getNearestShopping(new GeoPoint(
								p1.getLat(), p1.getLon(), geocoder
								.encodeGeohash(p1.getLat(),
										p1.getLon())));
						if( nearest != null ) {
							user.getViewLocation().setCountry(nearest.getAddress().getCountry());
						}
					} else {
						user.getViewLocation().setCountry(obj.getJSONObject("viewLocation").getString("country"));
					}
					if(null == user.getViewLocation() || !StringUtils.hasText(user.getViewLocation().getCountry())) {
						user.getViewLocation().setCountry(systemConfiguration.getDefaultViewLocationCountry());
					}            		
				} catch( Exception e ) {
					user.getViewLocation().setCountry(systemConfiguration.getDefaultViewLocationCountry());
				}
			}

			user.getAddress().setCountry(user.getViewLocation().getCountry());

			user.setStatusModificationDateTime(new Date());

			// validates user data
			if (!validateFields(user)) {
				throw ASExceptionHelper.invalidArgumentsException(INVALID_DEFAULT_FIELDS);
			}

			dao.create(user);
			log.info("user created: " + user.getFullname());

			final JSONObject jsonobj = new JSONObject();
			jsonobj.put("token", token);
			jsonobj.put("identifier", user.getIdentifier());
			response = jsonobj.toString();

			// track action
			trackerHelper.enqueue( user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.UserBzService.add"), 
					null, null);

			// mail helper
//			Locale loc = normalizeLocale(lang);
//			mailHelper.sendMessage(user, getI18NMessage(loc.toString(), "mail.newusermail"), "mails/newuser.vm", new HashMap<String, Object>());

		} catch (JSONException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} catch (ASException | IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(e).toString();
		} finally {
			markEnd(start);
		}

		return response;
	}

	private void changeMessagingSettings(JSONObject json) throws ASException, JSONException {
		DeviceInfo di = diDao.get(json.getString("deviceUUID"), true);
		User user = dao.get(di.getUserId(), true);
		user.setGeoFenceEnabled(json.getBoolean("geoFenceEnabled"));
		user.setReceivePushMessages(json.getBoolean("receivePushMessages"));
		dao.updateWithoutChangingMail(user);
	}

	@Override
	public String put(JsonRepresentation entity) {

		long start = markStart();
		try {

			// validate authToken
			final User user = getUserFromToken();
			final JSONObject obj = entity.getJsonObject();

			// Check for messaging options
			if( obj.has("deviceUUID") && !hasDefaultParameters(obj, bzFields.ALWAYS_FIELDS.toArray(EMPTY_STRING_ARRAY))) {
				changeMessagingSettings(obj);
			} else {

				//check mandatory fields
				if (!hasDefaultParameters(obj, bzFields.ALWAYS_FIELDS.toArray(EMPTY_STRING_ARRAY))) {
					throw ASExceptionHelper.invalidArgumentsException(INVALID_DEFAULT_FIELDS);
				}

				// checks if the authToken's user has perms to touch the identifier object
				final String identifier = obtainLowerCaseIdentifierFromJSON(obj);
				if (identifier == null || identifier.length() == 0 || !user.getIdentifier().equalsIgnoreCase(identifier)) {
					throw ASExceptionHelper.forbiddenException();
				}

				setPropertiesFromJSONObject(obj, user, bzFields.READONLY_FIELDS);
				if( obj.has("viewLocationCountry")) {
					user.getViewLocation().setCountry(obj.getString("viewLocationCountry"));
				}
				user.setStatusModificationDateTime(new Date());
				// validates user data
				if (!validateFields(user)) {
					throw ASExceptionHelper.invalidArgumentsException(INVALID_DEFAULT_FIELDS);
				}

				dao.update(user);

			}

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
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(e).toString();
		} finally {
			markEnd(start);
		}

		return generateJSONOkResponse().toString();
	}

	private boolean validateFields(final User user) throws ASException {

		if (!commonValidator.validateIdentifier(user.getIdentifier())) {
			INVALID_DEFAULT_FIELDS.add("userId");
		}

		final String lastName = user.getLastname();
		final String name = user.getFirstname();

		if( null == name ) {
			user.setFirstname("");
		}

		if( null == lastName ) {
			user.setLastname("");
		}

		final String city = user.getAddress() != null ? user.getAddress().getCity() : null;
		if(null == city) {
			user.getAddress().setCity("");
		}

		final String country = user.getAddress() != null ? user.getAddress().getCountry() : null;
		if(null == country) {
			user.getAddress().setCountry("");
		}

		final String gender = user.getGender();
		if(null == gender) {
			user.setGender("female");
		}

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
}
