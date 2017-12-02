package mobi.allshoppings.bz;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.engine.util.Base64;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Blob;
import com.inodes.datanucleus.model.Email;
import com.inodes.datanucleus.model.Key;
import com.inodes.datanucleus.model.Text;

import mobi.allshoppings.auth.AuthHelper;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.FavoriteDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.Favorite;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.model.adapter.IGenericAdapter;
import mobi.allshoppings.model.tools.CacheHelper;
import mobi.allshoppings.model.tools.MultiLang;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.I18NUtils;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tracker.TrackerHelper;

public abstract class RestBaseServerResource extends ServerResource {

	protected static final Logger logger = Logger.getLogger(RestBaseServerResource.class.getName()); 
	
	protected static final String[] EMPTY_STRING_ARRAY = new String[0];
	protected static final List<String> INVALID_DEFAULT_FIELDS = CollectionFactory.createList();

	protected static final String UPLOAD_URL_KEY = "uploadURL";
	protected static final String GENERAL_IDENTIFIER_KEY = "identifier";
	protected static final String USER_IDENTIFIER_KEY = "userId";
	protected static final String USER_IDENTIFIER_ALIAS = "me";
	protected static final String EVENT_IDENTIFIER_KEY = "eventId";
	
	protected static final String LEVEL = "level";
	protected static final String LAST_UPDATE = "lastUpdate";
	protected static final String ORDER = "order";
	protected static final String FROM_DATE = "fromDate";
	protected static final String Q = "q";
	protected static final String LAT = "lat";
	protected static final String LON = "lon";
	protected static final String DEVICE_UUID = "deviceUUID";
	protected static final String PRESITION = "presition";
	protected static final String FAVORITES_FIRST = "favoritesFirst";
	protected static final String FAVORITES_ONLY = "favoritesOnly";
	
	protected static final String POINTS_UPDATE = "pointsUpdate";
	
	protected static final String ADMIN_USER = "admin";
	protected static final List<String> KNOWN_HOSTS;

	@Autowired
	private UserDAO dao;
	@Autowired
	private AuthHelper authHelper;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	protected CacheHelper cacheHelper;
	@Autowired
	private FavoriteDAO favoriteDao;
	@Autowired
	protected TrackerHelper trackerHelper;
	
	static {
		KNOWN_HOSTS = CollectionFactory.createList();
		KNOWN_HOSTS.add("0.0.0.0");
		KNOWN_HOSTS.add("127.0.0.1");
		KNOWN_HOSTS.add("0:0:0:0:0:0:0:1");
		KNOWN_HOSTS.add("0:0:0:0:0:0:0:0");
		KNOWN_HOSTS.add("::1");
		KNOWN_HOSTS.add("::");
	}
	
	@Override
	public void init(Context arg0, Request arg1, Response arg2) {
		super.init(arg0, arg1, arg2);
		try {
			KNOWN_HOSTS.add(InetAddress.getByName(
					systemConfiguration.getFrontendAddress()).getHostAddress());
		} catch(UnknownHostException e) {
			logger.log(Level.SEVERE, "Bad frontend address. The server will "
					+ "start, but it shouldn't!");
		}
	}
	
	public JSONObject generateJSONOkResponse() {
		JSONObject response = null;
		try {
			response = new JSONObject();
			response.put("status", 1);
			this.putSystemDateTime(response);
		} catch(JSONException e) {
			// Not even possible
		}
		return response;
	}

	protected JSONObject addPointsUpdate(JSONObject base, long points) {
		try {
			if( points != 0 ) base.put(POINTS_UPDATE, points);
		} catch(JSONException e) {
			// Not even possible
		}
		return base;
	}

	/**
	 * Inserts System Date into a JSON Object to sequence it and to give it
	 * unicity
	 * 
	 * @param object
	 *            The JSON Object to modify
	 * @throws JSONException
	 */
	public void putSystemDateTime(JSONObject object) throws JSONException {
		object.put("systemDateTime", DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
	}
	
	public Map<String,String> getParameters() {
		if( this.getRequest() == null ) return new HashMap<String, String>();
		Reference url = this.getRequest().getResourceRef();
		Form query = url.getQueryAsForm();
		//
		return query.getValuesMap();
	}

	public boolean hasDefaultParameters(Object obj, String[] fields, String lang) {
		JSONObject input = getJSONRepresentationFromObject(obj, fields, lang);
		return hasDefaultParameters(input, fields);
	}
	
	public boolean hasDefaultParameters(JSONObject obj, String[] fields){
        
		if(!INVALID_DEFAULT_FIELDS.isEmpty())
        	INVALID_DEFAULT_FIELDS.clear();
        
		for (String field : fields) {
			try {
				if (!obj.has(field) || !StringUtils.hasText(obj.get(field).toString())) {
					INVALID_DEFAULT_FIELDS.add(field);
				}
			} catch( JSONException e ) {
				INVALID_DEFAULT_FIELDS.add(field);
			}
		}
        
		return INVALID_DEFAULT_FIELDS.isEmpty();
	}
	
	public List<String> getInvalidDefaultParameters(){
		return INVALID_DEFAULT_FIELDS;
	}

	/**
	 * Searches the identifier attribute on a JSON object and returns the lower
	 * case value if the attributes exists
	 * 
	 * @param object
	 * @return
	 */
	public String obtainLowerCaseIdentifierFromJSON(JSONObject object) {
		String identifier = null;
		try {
			identifier = object.getString(GENERAL_IDENTIFIER_KEY);
		} catch (JSONException e) {
		}
		if (identifier != null) {
			return identifier.toLowerCase();
		}
		return null;
	}
	
	/**
	 * Searches the key parameter on the URL's query. If found returns the value
	 * otherwise the defaultValue
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String obtainStringValue(String key, String defaultValue) {
		String identifierAttribute = (String)getRequest().getAttributes().get(key);
		if (identifierAttribute != null && !"".equals(identifierAttribute) && !"null".equals(identifierAttribute)) {
			return identifierAttribute;
		}

		Map<String,String> parameters = this.getParameters();
		String value = parameters.get(key);
		if (value == null || "null".equals(value)) {
			return defaultValue;
		}
		return value;
	}
	
	/**
	 * Searches the key parameter on the URL's query. If found returns the value
	 * otherwise the defaultValue
	 *
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Integer obtainIntegerValue(String key, Integer defaultValue) {
		String val = obtainStringValue(key, null);
		try {
			return StringUtils.hasText(val) ? Integer.valueOf(val) : defaultValue;
		} catch( NumberFormatException e ) {
			logger.finer("Cannot convert value " + val + " to integer!");
			return defaultValue;
		}
	}
	
	/**
	 * Searches the key parameter on the URL's query. If found returns the value
	 * otherwise the defaultValue
	 *
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Double obtainDoubleValue(String key, Double defaultValue) {
		String val = obtainStringValue(key, null);
		try {
			return StringUtils.hasText(val) ? Double.valueOf(val) : defaultValue;
		} catch( NumberFormatException e ) {
			logger.finer("Cannot convert value " + val + " to double!");
			return defaultValue;
		}
	}

	/**
	 * Useful to retrieve identifiers on URL's Query
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String obtainLowerCaseStringValue(String key, String defaultValue) {
		String value = this.obtainStringValue(key, defaultValue);
		if (value != null) {
			return value.toLowerCase();
		}
		return value;
	}
	

	/**
	 * Searches the key parameter on the URL's query. If found returns the value
	 * otherwise the defaultValue. The date must be in
	 * ISO_DATETIME_TIME_ZONE_FORMAT format.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Date obtainDateValue(String key, Date defaultValue) {
		Map<String,String> parameters = this.getParameters();
		String value = parameters.get(key);
		if (value == null) {
			return defaultValue;
		}

		String pattern = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern();
		Date date = null;
		try {
			date = DateUtils.parseDate((String)value, new String[] { pattern });
		} catch (ParseException e) {
		}
		if (date == null) {
			return defaultValue;
		}
		return date;
	}

	/**
	 * Searches the parameters TO and FROM on the URL's query. If found returns
	 * the value otherwise the system default values See:
	 * http://db.apache.org/jdo
	 * /api20/apidocs/javax/jdo/Query.html#setRange(long, long) fromIncl -
	 * 0-based inclusive start index toExcl - 0-based exclusive end index.
	 * 
	 * @return
	 */
	public Range obtainRange() {
		int defaultTo = this.systemConfiguration.getDefaultToRange();
		int defaultFrom = this.systemConfiguration.getDefaultFromRange();
		int from = this.obtainIntValue("from", defaultFrom);
		int to = this.obtainIntValue("to", from+defaultTo);
		
		if (from < 0) {
			from = defaultFrom;
		}
		if (to < from) {
			to = defaultTo;
			from = defaultFrom;
		}
		return new Range(from, to);
	}
	
	/**
	 * Searches the key parameter on the URL's query. If found returns the value
	 * otherwise the defaultValue
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Boolean obtainBooleanValue(String key, Boolean defaultValue) {
		Map<String,String> parameters = this.getParameters();
		String value = parameters.get(key);
		if (value == null) {
			return defaultValue;
		}
		Boolean booleanValue = defaultValue;
		if (value.compareToIgnoreCase("true") == 0) {
			return true;
		} else 	if (value.compareToIgnoreCase("false") == 0) {
			return false;
		}
		return booleanValue;
	}
	
	/**
	 * Searches the key parameter on the URL's query. The parameter can be a
	 * list of values (comma separated) or a single value If found returns the
	 * value/s on a collection otherwise the defaultValue
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public List<Integer> obtainIntValues(String key, List<Integer> defaultValue) {
		List<Integer> ret = CollectionFactory.createList();
		Map<String,String> parameters = this.getParameters();
		String value = parameters.get(key);
		if (value == null) {
			return defaultValue;
		}
		StringTokenizer st = new StringTokenizer(value, ",");
		while (st.hasMoreTokens()) { 
			String token = st.nextToken(); 
			try {
				int intValue = Integer.parseInt(token.trim());
				ret.add(intValue);
			} catch (NumberFormatException e) {
			}
		}
		return ret;
	}
	
	/**
	 * Searches the key parameter on the URL's query. If found returns the value
	 * otherwise the defaultValue
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int obtainIntValue(String key, int defaultValue) {
		Map<String,String> parameters = this.getParameters();
		String value = parameters.get(key);
		if (value == null) {
			return defaultValue;
		}
		int intValue = defaultValue;
		try {
		     intValue = Integer.parseInt(value);
		     if (intValue < 0) {
		    	 intValue = defaultValue;
		     }
		} catch (NumberFormatException e) {
		}
		return intValue;
	}

	/**
	 * Obtains a user based in its auth token
	 * 
	 * @return The found user
	 * @throws ASException
	 *             Returns an exception if no token was found, or if the found
	 *             token is not valid anymore
	 */
	public User getUserFromToken() throws ASException {
		// take the authToken form request
		Map<String,String> parameters = this.getParameters();
		String authToken = parameters.get("authToken");
		if (authToken == null) {
			throw ASExceptionHelper.authTokenMissingException();
		}
		
		String clientAddress = getClientInfo().getAddress();
		if(!KNOWN_HOSTS.contains(clientAddress)) {
			logger.log(Level.SEVERE, "Somebody tried to access the server from an "
					+ "unknown origin! The attacker address is: "
					+clientAddress);
			throw ASExceptionHelper.forbiddenException();
		}
		
		User authUser = null;
		
		try {
			// obtain the use from the token
			authUser = (User)cacheHelper.get(authToken);
			if( authUser == null ) {
				authUser = dao.getByAuthToken(authToken);
			}
			
			if( authUser == null || authUser.getSecuritySettings() == null 
					|| authUser.getSecuritySettings().getAuthTokenValidity() == null)
				throw ASExceptionHelper.tokenExpiredException();
			
			// validate the token
			Date now = new Date();
			if (authUser.getSecuritySettings().getAuthTokenValidity().before(now)) {
				try {
					// if the token is invalid remove the token and the date and update the user...
					authUser.getSecuritySettings().setAuthToken(null);
					authUser.getSecuritySettings().setAuthTokenValidity(null);
					dao.updateWithoutChangingMail(authUser);
					cacheHelper.put(authToken, authUser);
				} catch (Exception e) {
				}
				//  ...then send a specific error to the client
				throw ASExceptionHelper.tokenExpiredException();
			}
			
			cacheHelper.put(authToken, authUser);
			
			return authUser;
		} catch (ASException e) {
			// re throw the exception
			throw e;
		} catch (Exception e) {
			throw ASExceptionHelper.notAcceptedException();
		}
	}

	/**
	 * Obtains language
	 * 
	 * @return The found language
	 * @throws ASException
	 *             Returns an exception if no token was found, or if the found
	 *             token is not valid anymore
	 */
	public String obtainLang() {
		// take the authToken form request
		Map<String,String> parameters = this.getParameters();
		String lang = parameters.get("lang");
		if(!StringUtils.hasText(lang)) lang = systemConfiguration.getDefaultLang();
		return lang;
	}
	
	/**
	 * Obtains language, or null if it is not available
	 * 
	 * @return The found language
	 * @throws ASException
	 *             Returns an exception if no token was found, or if the found
	 *             token is not valid anymore
	 */
	public String obtainLangOrNull() {
		return this.getParameters().get("lang");
	}

	/**
	 * Invalidates a login token
	 * 
	 * @throws ASException
	 */
	public void invalidateToken() throws ASException {
		// take the authToken form request
		Map<String,String> parameters = this.getParameters();
		String authToken = parameters.get("authToken");
		if (authToken == null) {
			throw ASExceptionHelper.authTokenMissingException();
		}
		User authUser = null;
		try {
			// obtain the use from the token
			authUser = dao.getByAuthToken(authToken);
			
				try {
					// if the token is invalid remove the token and the date and update the user...
					authUser.getSecuritySettings().setAuthToken(null);
					authUser.getSecuritySettings().setAuthTokenValidity(null);
					dao.updateWithoutChangingMail(authUser);
				} catch (Exception e) {
				}
		} catch (ASException e) {
			// re throw the exception
			throw e;
		} catch (Exception e) {
			throw ASExceptionHelper.notAcceptedException();
		}
	}

	/**
	 * Searches the identifierName on the URL using the REST syntax. If found
	 * returns the value on lower case. Otherwise searches the identifier
	 * parameter on the URL's query.
	 * 
	 * @param identifierName
	 * @return
	 * @throws ASException
	 */
	public String obtainIdentifier(String identifierName) throws ASException {
		return this.obtainIdentifier(identifierName, false);
	}
	public String obtainIdentifier(String identifierName, boolean caseSensitive) throws ASException {
		String identifierAttribute = (String)getRequest().getAttributes().get(identifierName);
		if (identifierAttribute != null && !"".equals(identifierAttribute)) {
			return (caseSensitive) ? identifierAttribute : identifierAttribute.toLowerCase();
		}
		
		// look on URL parameters
		Map<String,String> parameters = this.getParameters();
		String identifier = parameters.get(identifierName);
		if (identifier == null || "".equals(identifier)) {
			return null;
		}
		return (caseSensitive) ? identifier : identifier.toLowerCase();
	}

	/**
	 * Returns userID from request parameters if exist or from URI path One of
	 * them has to exist.
	 * 
	 * @return
	 * @throws ASException
	 **/
	public String obtainUserIdentifier() throws ASException {
		return obtainUserIdentifier(true);
	}

	/**
	 * Returns userID from request parameters if exist or from URI path One of
	 * them has to exist.
	 * 
	 * @return
	 * @throws ASException
	 **/
	public String obtainUserIdentifier(boolean throwUpOnExpired) throws ASException {
		try {
			String identifierAttribute = (String)getRequest().getAttributes().get(USER_IDENTIFIER_KEY);
			User authUser = this.getUserFromToken();

			// validate me shortcut
			if (USER_IDENTIFIER_ALIAS.equalsIgnoreCase(identifierAttribute)) {
				return authUser.getIdentifier();
			}

			if (identifierAttribute != null && !"".equalsIgnoreCase(identifierAttribute)) {
				// check if the caller can access the identifier's data
				if (authHelper.validateIfUserCanAccessOtherUser(authUser, identifierAttribute) == false) {
					throw ASExceptionHelper.forbiddenException();
				}
				return identifierAttribute.toLowerCase();
			}

			// look on URL parameters
			Map<String,String> parameters = this.getParameters();
			String identifier = parameters.get(GENERAL_IDENTIFIER_KEY);
			if (identifier == null || "".equalsIgnoreCase(identifier) || "me".equalsIgnoreCase(identifier)) {
				// If the client does not send the identifier attribute use the authUser.identifier
				identifier = authUser.getIdentifier();
			}
			// check if the caller can access the identifier's data
			if (authHelper.validateIfUserCanAccessOtherUser(authUser, identifier) == false) {
				throw ASExceptionHelper.forbiddenException();
			}
			return identifier.toLowerCase();
		} catch( ASException e ) {
			if ((	e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE || 
					e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE)
					&& !throwUpOnExpired) {
				return null;
			} else {
				throw e;
			}
		}
	}

	/**
	 * Builds a list of output fields, according to a list of fields required by
	 * the client, the list of "Always" fields, and removing the forbidden
	 * fields for the entity
	 * 
	 * @param defaults
	 *            Default field list
	 * @param always
	 *            Must Have field list
	 * @param invalidFields
	 *            Must Not Have field list
	 * @return a normalized field list
	 */
	public String[] obtainOutputFields(String[] defaults, String[]always, Set<String> invalidFields) {
		Map<String,String> parameters = this.getParameters();
		String fields = parameters.get("fields");
		List<String> fieldVector = new ArrayList<String>();

		if (fields != null) {
			StringTokenizer tokenizer = new StringTokenizer(fields.trim(), ",");

			while (tokenizer.hasMoreTokens()) {
				String fieldName = tokenizer.nextToken();
				if (fieldName != null && !"".equals(fieldName) && !invalidFields.contains(fieldName)) {
					fieldVector.add(fieldName);
				}
			}
		}
		if (fieldVector.size() == 0) {
			return defaults;
		} else {
			for (int idx = 0; idx < always.length; idx++) {
				if (!fieldVector.contains(always[idx])) {
					fieldVector.add(always[idx]);
				}
			}
			return fieldVector.toArray(EMPTY_STRING_ARRAY);
		}
	}

	/**
	 * Obtains a list of fields available to see for an entity
	 * 
	 * @param bzFields
	 *            The entity descriptor which lists the requested fields
	 * @param level
	 *            The level id
	 * @return A list of permitted fields to return
	 */
	public String[] obtainOutputFields(final BzFields bzFields, final String level) {
        final String fields = getParameters().get("fields");
        final Set<String> outputFields = bzFields.getOutputFields(level, fields);

        return outputFields.toArray(EMPTY_STRING_ARRAY);
    }
	
	public String[] obtainOutputFields(final Object obj) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return guessGenericFields(obj, "all");
	}

	public String[] guessGenericFields(final Object obj, final String level) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		BzFields bzFields = BzFields.guessBzFields(obj.getClass());
		if( bzFields != null ) {
			return obtainOutputFields(bzFields, level);
		} else {
			@SuppressWarnings("unchecked")
			Map<String, Object> fields = PropertyUtils.describe(obj);
			List<String> outputFields = new ArrayList<String>(fields.keySet());
			outputFields.remove("class");
			Iterator<String> i = fields.keySet().iterator();
			while(i.hasNext()) {
				String key = i.next();
				Object o = fields.get(key);
				if (o != null 
						&& !(o instanceof String) && !(o instanceof Integer)
						&& !(o instanceof Long) && !(o instanceof Float)
						&& !(o instanceof Double) && !(o instanceof Date)
						&& !(o instanceof Collection) && !(o instanceof Text)
						&& !(o instanceof Key) & !(o instanceof Class)) {
					@SuppressWarnings("unchecked")
					Map<String, Object> subFields = PropertyUtils.describe(o);
					outputFields.remove(key);
					Iterator<String> i2 = subFields.keySet().iterator();
					while(i2.hasNext()) {
						String subKey = i2.next();
						if(!subKey.equals("class"))
							outputFields.add(key + "." + subKey);
					}
				}
			}
			return outputFields.toArray(EMPTY_STRING_ARRAY);
		}
	}
	
	public Object safeString(Object from) {
		try {
			if( from instanceof String ) {
				return new String(((String)from).getBytes());
			} else {
				return from;
			}
		} catch( Exception e ) {
			logger.log(Level.INFO, e.getMessage(), e);
			return from;
		}
	}
	
	/**
	 * Sets properties of an entity object based in the attributes received in
	 * JSON representation
	 * 
	 * @param jsonObj
	 *            The JSON representation which contains the input data
	 * @param obj
	 *            The object to be modified
	 * @param excludeFields
	 *            a list of fields which cannot be modified
	 */
	@SuppressWarnings("unchecked")
	public void setPropertiesFromJSONObject(JSONObject jsonObj, Object obj, Set<String> excludeFields) {
		for (Iterator<String> it = jsonObj.keys(); it.hasNext(); ) {
			try {
				String key = it.next();
				if (excludeFields != null){
					if (!BzFields.isInvalidField(key, excludeFields)) {
						Object fieldValue = jsonObj.get(key);
						String fieldParts[] = key.split("\\.");
						if( fieldParts.length > 1 ) {
							Object data = PropertyUtils.getProperty(obj, fieldParts[0]);
							StringBuffer subValue = new StringBuffer();
							for( int i = 1; i < fieldParts.length; i++ ) {
								subValue.append(fieldParts[i]);
								if( i < (fieldParts.length - 1)) subValue.append(".");
							}
							JSONObject newObj = new JSONObject();
							newObj.put(subValue.toString(), fieldValue);
							setPropertiesFromJSONObject(newObj, data, excludeFields);
						} else {
							if (PropertyUtils.getPropertyType(obj, key) == Text.class) {
								Text text = new Text(fieldValue.toString());
								PropertyUtils.setProperty(obj, key, text);
							} else if (PropertyUtils.getPropertyType(obj, key) == Email.class){
								Email mail = new Email(((String)safeString(fieldValue)).toLowerCase());
								PropertyUtils.setProperty(obj, key, mail);
							} else if (PropertyUtils.getPropertyType(obj, key) == Date.class) {
								String pattern = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern();
								Date date = DateUtils.parseDate((String)fieldValue, new String[] { pattern });
								PropertyUtils.setProperty(obj, key, date);
							} else if (PropertyUtils.getPropertyType(obj, key) == Key.class) {
								String[] parts = ((String)fieldValue).split("\"");
								Class<?> c = Class.forName("mobi.allshoppings.model." + parts[0].split("\\(")[0]);
								Key data = new KeyHelperGaeImpl().obtainKey(c, parts[1]);
								PropertyUtils.setProperty(obj, key, data);
							} else if (PropertyUtils.getPropertyType(obj, key) == Blob.class) {
								Blob data = new Blob(Base64.decode((String)fieldValue));
								PropertyUtils.setProperty(obj, key, data);
							} else if (fieldValue instanceof JSONArray) {
								JSONArray array = (JSONArray)fieldValue;
								Collection<String> col = new ArrayList<String>();
								for (int idx = 0; idx < array.length(); idx++) {
									String value = array.getString(idx);
									col.add(value);
								}
								BeanUtils.setProperty(obj, key, col);
							} else {
								BeanUtils.setProperty(obj, key, safeString(fieldValue));
							}
						}
					}
				}
			} catch (Exception e) {
				// ignore property
				logger.log(Level.INFO, "Error setting properties from JSON", e);
			}
		}
	}
	
	/**
	 * Converts an entity object into a JSON Object
	 * 
	 * @param input
	 *            The object to convert
	 * @param fields
	 *            Field list to convert
	 * @return A JSON Representation of the entity input object
	 */
	public JSONObject getJSONRepresentationFromObject(Object input, String[] fields) {
		return getJSONRepresentationFromObject(input, fields, this.obtainLang());
	}
	
	/**
	 * Converts an entity object into a JSON Object
	 * 
	 * @param input
	 *            The object to convert
	 * @param fields
	 *            Field list to convert
	 * @param lang
	 *            Language to use in multi language wrappers
	 * @return A JSON Representation of the entity input object
	 */
	@SuppressWarnings("unchecked")
	public JSONObject getJSONRepresentationFromObject(Object input, String[] fields, String lang) {
		if(!StringUtils.hasText(lang)) lang = systemConfiguration.getDefaultLang();
		JSONObject jsonobj = new JSONObject();
		try{
			if( input != null ) {
				Map<String, Object> properties = PropertyUtils.describe(input);
				for (int idx = 0; idx < fields.length; idx++) {
					try {
						String fieldName = fields[idx];
						String fieldParts[] = fieldName.split("\\.");
						if( fieldParts.length > 1 ) {
							Object fieldValue = properties.get(fieldParts[0]);
							StringBuffer subValue = new StringBuffer();
							for( int i = 1; i < fieldParts.length; i++ ) {
								subValue.append(fieldParts[i]);
								if( i < (fieldParts.length - 1)) subValue.append(".");
							}
							JSONObject obj = getJSONRepresentationFromObject(fieldValue, new String[] {subValue.toString()}, lang);
							Iterator<String> it = obj.keys();
							while(it.hasNext()) {
								String key = it.next();
								if(!key.equals("systemDateTime")) jsonobj.put(fieldParts[0] + "." + key, obj.get(key));
							}
						} else {
							Object fieldValue = properties.get(fieldName);

							// Process different types for the right output
							if (fieldValue != null) {
								if (fieldValue instanceof Email) {
									fieldValue = ((Email)fieldValue).getEmail();
								} else if (fieldValue instanceof Text) {
									fieldValue = ((Text)fieldValue).getValue();
								} else if (fieldValue instanceof Date) {
									fieldValue = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(fieldValue);
								} else if (fieldValue instanceof Blob) {
									fieldValue = Base64.encode(((Blob)fieldValue).getBytes(), true);
								} else if (fieldValue instanceof MultiLang) {
									fieldValue = ((MultiLang)fieldValue).get(lang);
								} else if (fieldValue instanceof List ) {
									if(((List<?>)fieldValue).size() > 0 ) {
										if(((List<?>)fieldValue).get(0) instanceof IGenericAdapter) {
											String[] subFields = guessGenericFields(((List<?>)fieldValue).get(0), "all");
											fieldValue = this.getJSONRepresentationFromArrayOfObjects((List<?>)fieldValue, subFields, fieldName, jsonobj, lang);
										}
									}
								}
								if(!( fieldValue instanceof JSONObject ))
									jsonobj.put(fieldName, fieldValue);
							} else {
								jsonobj.put(fieldName, "");
							}
						}
					} catch (Exception e) {
						// ignore property
					}
				}
			}
			this.putSystemDateTime(jsonobj);
		}catch(Exception e){
			e.printStackTrace();
		}

		return jsonobj;
	}
	
	public List<User> getArrayOfUsersFromJSONRepresentation(JSONObject obj, String key, String[] defaultFields, Set<String> excludeFields) throws ASException{
		List<User> users = new ArrayList<User>();
		try{
			if (obj != null) {
				JSONArray jsonArray = obj.getJSONArray(key);
				if(jsonArray != null){
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObj = (JSONObject) jsonArray.get(i);
						if(this.hasDefaultParameters(jsonObj, defaultFields) == false){
							throw ASExceptionHelper.invalidArgumentsException();
						}
						User user = new User();
						this.setPropertiesFromJSONObject(jsonObj, user, excludeFields);
						users.add(user);
					}
				}
			}
		}catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
			
		return users;
	}
	
	public JSONObject getJSONRepresentationFromArrayOfObjects(List<?>list, String[] fields) {
		return getJSONRepresentationFromArrayOfObjects(list, fields, null, null, this.obtainLang());
	}
	
	public JSONObject getJSONRepresentationFromArrayOfObjects(List<?>list, String[] fields, String lang) {
		return getJSONRepresentationFromArrayOfObjects(list, fields, null, null, lang);
	}

	public JSONObject getJSONRepresentationFromArrayOfObjects(List<?>list, String[] fields, String dataName, JSONObject appender) {
		return getJSONRepresentationFromArrayOfObjects(list, fields, dataName, appender, this.obtainLang());
	}
	public JSONObject getJSONRepresentationFromArrayOfObjects(List<?>list, String[] fields, String dataName, JSONObject appender, String lang) {
		
		// Tries to add the appender
		JSONObject jsonobj = (appender == null ) ? new JSONObject() : appender;
		
		// if dataName is null... then the simple name is "data"
		String data = dataName == null ? "data" : dataName; 
		
		List<JSONObject> objects = new ArrayList<JSONObject>();
		try {
			if (list != null) {
				for (Iterator<?> it = list.iterator(); it.hasNext();) {
					Object object = it.next();
					JSONObject json = this.getJSONRepresentationFromObject(object, fields, lang);
					objects.add(json);
				}
			}
			jsonobj.put(data, objects);
			jsonobj.put("systemDateTime", DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
		} catch (JSONException e) {
		}
		return jsonobj;
	}

	public List<String> getArrayOfStringsFromJSONArray(JSONObject obj, String key) {
		List<String> objects = new ArrayList<String>();
		try {
			if (obj != null) {
				JSONArray jsonArray = obj.getJSONArray(key);
				if(jsonArray != null){
					for (int i = 0; i < jsonArray.length(); i++) {
						objects.add(jsonArray.getString(i));
					}
				}
			}
		}catch(Exception e){
			//TODO pcosta ver excepcion
			// FIXME khe?
		}
		return objects;
	}

	/**
	 * Converts an ASException into a JSON Object that represents it
	 * 
	 * @param input
	 *            The input Exception
	 * @return A JSON Representation of the exception
	 */
	public JSONObject getJSONRepresentationFromException(ASException input) {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("error_message", input.getErrorMessage());
			jsonobj.put("error_code", input.getErrorCode());
			return jsonobj;
		} catch (Exception e) {
		}
		return jsonobj;
	}

	/**
	 * Converts an ASException into a JSON Object that represents it
	 * 
	 * @param input
	 *            The input Exception
	 * @return A JSON Representation of the exception
	 */
	public JSONObject getJSONRepresentationFromException(Exception input) {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("error_message", input.getLocalizedMessage());
			jsonobj.put("error_code", 500);
		} catch (JSONException e) {
		}
		return jsonobj;
	}

	public ArrayList<String> buildPropertiesList(String prefix, Object sample) throws Exception {
		ArrayList<String> properties = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		Map<String, Object> pmap = (Map<String, Object>)PropertyUtils.describe(sample);
		Iterator<String> i = pmap.keySet().iterator();
		while(i.hasNext()) {
			String key = i.next();
			if(!key.equals("class")) {
				Object val = pmap.get(key);
				if (val != null && !(val instanceof String)
						&& !(val instanceof Date) && !(val instanceof Key)
						&& !(val instanceof Blob) && !(val instanceof Integer)
						&& !(val instanceof Long) && !(val instanceof Float)
						&& !(val instanceof Double) && !(val instanceof Boolean)
						&& !(val instanceof Vector) && !(val instanceof ArrayList) 
						&& !(val instanceof Map)) {
					properties.addAll(buildPropertiesList(key, val));
				} else {
					if( prefix == null ) {
						properties.add(key);
					} else {
						properties.add(prefix + "." + key);
					}
				}
			}
		}
		return properties;
	}
	
	public String getCachedJSONString(String identifier) {
		return getCachedJSONString(identifier, 0, null, false);
	}
	
	public String getCachedJSONString(String identifier, int kind, User user, boolean applyFavorite) {
		String urlKey = getRequestURI();
		String cachedJSON = (String)cacheHelper.get(urlKey);
		if( null == cachedJSON ) return null;

		try {
			if( applyFavorite ) {
				JSONObject obj = new JSONObject(cachedJSON);
				Favorite fav = favoriteDao.getUsingUserAndEntityAndKind(user, identifier, kind, true);
				if( fav == null ) {
					obj.put("favorite", false);
				} else {
					obj.put("favorite", true);
				}
				return obj.toString();
			} else {
				return cachedJSON;
			}
		} catch( Exception e ) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
	
	public void setCachedJSONString(String jsonString) {
		cacheHelper.put(getRequestURI(), jsonString);
	}
	
	public String getRequestURI() {
		try {
			return ServletUtils.getRequest(getRequest()).getRequestURI();
		} catch( Exception e ) {
			return "/null";
		}
	}
	
	public String getFullRequestURI() {
		try {
			return ServletUtils.getRequest(getRequest()).getRequestURL().toString();
		} catch( Exception e ) {
			return "/null";
		}
	}
	
	public String getRequestIP() {
		try {
			return ServletUtils.getRequest(getRequest()).getRemoteAddr();
		} catch( Exception e ) {
			return "/null";
		}
	}

	public String getRequestAgent() {
		try {
			return ServletUtils.getRequest(getRequest()).getHeader("User-Agent");
		} catch( Exception e ) {
			return "/null";
		}
	}

	public long markStart() {
		return markStart(null);
	}
	
	public long markStart(String text) {
		logger.log(Level.INFO, "Request " + getRequestURI() + " begins...");
		return System.currentTimeMillis();
	}
	
	public long markEnd(long start) {
		return markEnd(null, start);
	}
	
	public long markEnd(String text, long start) {
		long end = System.currentTimeMillis();
		long time = end - start;
		logger.log(Level.INFO, "Request " + getRequestURI() + " ended in " + time + "ms.");
		return end;
	}

	public String getI18NMessage(String lang, String message) throws IOException {
		return I18NUtils.getI18NMessage(lang, message);
	}
	
	public Locale normalizeLocale(final String lang) {
		if(!StringUtils.hasText(lang)) return new Locale("es", "AR");
		String lang2;
		if( lang.equals("en")) lang2 = "en_US";
		else if( lang.equals("es")) lang2 = "es_AR";
		else lang2 = "es_AR";
		String parts[] = lang2.split("_");
		return new Locale(parts[0], parts[1]);
	}

	public boolean isValidForUser(User user, DashboardIndicatorData data) {
		if( user.getSecuritySettings().getRole().equals(Role.STORE)) {
			if( user.getSecuritySettings().getStores().contains(data.getSubentityId()))
				return true;
			else
				if (!CollectionUtils.isEmpty(user.getSecuritySettings().getShoppings())
						&& user.getSecuritySettings().getShoppings().contains(data.getSubentityId()))
					return true;
				else
					return false;
		} else 
			return true;
	}

	public boolean isValidForUser(User user, Store store) {
		if( user.getSecuritySettings().getRole().equals(Role.STORE)) {
			if( user.getSecuritySettings().getStores().contains(store.getIdentifier()))
				return true;
			else
				return false;
		} else 
			return true;
	}

}
