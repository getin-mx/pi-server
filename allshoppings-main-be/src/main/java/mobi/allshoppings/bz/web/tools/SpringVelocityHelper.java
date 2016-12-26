package mobi.allshoppings.bz.web.tools;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import mobi.allshoppings.auth.UserInfo;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.tools.CountryHelper;

@SuppressWarnings("serial")
public class SpringVelocityHelper implements Serializable {

	/**
	 * 
	 */
	public static final String GENERIC_REDIRECTOR = "redirect/genericRedirector";
	
	/**
	 * JSON Request expected parameter
	 */
	public static final String JSON_REQUEST = "jsonRequest";
	
	/**
	 * Generic JSON Response Velocity template location
	 */
	public static final String JSON_VM = "json/genericJSONResponse";

	/**
	 * JSON Object Attribute
	 */
	public static final String JSON_OBJECT = "jsonObject";
	
	/**
	 * Redirect to attribute name
	 */
	public static final String TARGET_URI = "redirect";
	
	/**
	 * Response Type Success
	 */
	public static final int SUCCESS = 1;
	
	/**
	 * Response Type Error
	 */
	public static final int ERROR = 2;

	/**
	 * Fail Marker
	 */
	public static final String FAIL = "fail";

	/**
	 * OK Marker
	 */
	public static final String OK = "ok";

	/**
	 * User Session Attribute 
	 */
	private final static String SESSION_USER = "user";

	/**
	 * An instance of this helper to be used in velocity macros
	 */
	private final static String SVH = "svh";

	/**
	 * Singleton instance
	 */
	@SuppressWarnings("unused")
	private static SpringVelocityHelper singleton;
	
	private UserInfo user;
	
	public SpringVelocityHelper() {
		super();
	}
	
	public SpringVelocityHelper(UserInfo user) {
		super();
		this.user = user;
	}
	
	/**
	 * Response wrapper
	 * @param target The target Velocity Template
	 * @param attributeMap The collected attribute map to respond
	 * @param request The HttpServlerRequest being managed
	 * @return The final Velocity Template to render
	 * @throws JSONException
	 */
    public static String respond(final int responseType, final String target, final HashMap<String,Object> attributeMap, final HttpServletRequest request) {
    	try {
    		
    		if(!attributeMap.containsKey(TARGET_URI)) {
    			attributeMap.put(TARGET_URI, target);
    		}

    		if( responseType == SUCCESS ) {
    			attributeMap.put("response", "success");
    		} else {
    			attributeMap.put("response", "fail");
    		}
    		
    		if( isJSONResponseNeeded(request)) {
    			JSONObject json = getJSONRepresentation(attributeMap);
    			request.setAttribute(JSON_OBJECT, json.toString());
    			return JSON_VM;
    		} else {
    			addAttributeMapToRequest(attributeMap, request);
    			return target;
    		}
    	} catch( JSONException e ) {
    		throw new RuntimeException(e);
    	}
    }

    /**
     * @return a singleton instance of this helper
     */
    public static SpringVelocityHelper getInstance(UserInfo user) {
    	return new SpringVelocityHelper(user);
//    	if( singleton == null ) {
//    		singleton = new SpringVelocityHelper();
//    	}
//    	
//    	return singleton;
    }
    
	/**
	 * Response wrapper
	 * @param target The target Velocity Template
	 * @param request The HttpServlerRequest being managed
	 * @return The final Velocity Template to render
	 * @throws JSONException
	 */
    public static String respond(final int responseType, final String target, final HttpServletRequest request) {
    	HashMap<String, Object> attributeMap = new HashMap<String, Object>();
    	String key;
    	Object value;
    	
    	Enumeration<?> e = request.getAttributeNames();
    	while(e.hasMoreElements()) {
    		key = e.nextElement().toString();
    		value = request.getAttribute(key);
    		attributeMap.put(key, value);
    	}
    	
    	return respond(responseType, target, attributeMap, request);
    }
    
    /**
     * Adds an entire attribute map to an HttpServletRequest
     * @param attributeMap The attribute map to add
     * @param request the request to be modified
     */
    public static void addAttributeMapToRequest(final HashMap<String, Object> attributeMap, final HttpServletRequest request) {
    	String key;
    	Object value;
    	Iterator<String> i = attributeMap.keySet().iterator();

    	while(i.hasNext()) {
    		key = i.next();
    		value = attributeMap.get(key);
    		request.setAttribute(key, value);
    	}
    }
   
    /**
     * Converts a Map into a JSON Object
     * @param attributeMap The Map to convert
     * @return The Map's JSON Representation
     * @throws JSONException
     */
    public static JSONObject getJSONRepresentation(HashMap<String, Object> attributeMap) throws JSONException {
    	JSONObject json = new JSONObject();
    	String key;
    	Object value;
    	Iterator<String> i = attributeMap.keySet().iterator();

    	while(i.hasNext()) {
    		key = i.next();
    		value = attributeMap.get(key);
    		json.put(key, value);
    	}
    	
    	return json;
    }
    
	/**
	 * Determines if the response needed is a JSON response format, checking the
	 * existence of a request parameter named 'json-request' with true value
	 * 
	 * @param request
	 *            The request to analyze
	 * @return Returns true if the request has a json return format, false if
	 *         not
	 */
    public static boolean isJSONResponseNeeded(final HttpServletRequest request) {
    	String jsonRequest = (String)request.getParameter(JSON_REQUEST);
    	if( jsonRequest != null ) {
    		return Boolean.parseBoolean(jsonRequest);
    	} else {
    		return false;
    	}
    }

	/**
	 * Obtains the session stored user info <br>
	 * 
	 * @see mobi.allshoppings.auth.UserInfo
	 * @param request
	 *            the request to analyze
	 * @return the session user info
	 */
	public static UserInfo getUserInfo(final HttpServletRequest request) {
		final HttpSession session = request.getSession();
		UserInfo user = (UserInfo) session.getAttribute(SESSION_USER);
		if (user == null) {
			user = new UserInfo();
			session.setAttribute(SESSION_USER, user);
		}
		SpringVelocityHelper svh = (SpringVelocityHelper) session.getAttribute(SVH);
		if( svh == null ) {
			svh = getInstance(user);
			session.setAttribute(SVH, svh);
		}
		
		return user;
	}

	/**
	 * Checks if the user is logged in or not
	 * 
	 * @param request
	 *            the request to analyze
	 * @return true if the user is logged in, false if not
	 */
	public static boolean isUserLoggedIn(final HttpServletRequest request) {
		final UserInfo user = getUserInfo(request);
		return user.isLoggedIn();
	}
	
	/**
	 * Converts a string parameter into a safe number
	 * 
	 * @param parm
	 *            The parameter
	 * @return a safe number
	 */
	public static Integer parameterToInt(String parm) {
		if (parm == null || parm.equals(""))
			return 0;
		try {
			Integer i = Integer.parseInt(parm);
			return i;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	/**
	 * Converts a string parameter into a safe number
	 * 
	 * @param parm
	 *            The parameter
	 * @return a safe number
	 */
	public static Double parameterToDouble(String parm) {
		if (parm == null || parm.equals(""))
			return new Double(0);
		try {
			Double i = Double.parseDouble(parm);
			return i;
		} catch (NumberFormatException e) {
			return new Double(0);
		}
	}

	/**
	 * Returns the day part of a date
	 * @param date The date to process
	 * @return the integer representation of the day part
	 */
	public int getDateDay(final Date date) {
		return Integer.parseInt(new SimpleDateFormat("d").format(date));
	}
	
	/**
	 * Returns the month part of a date
	 * @param date The date to process
	 * @return the integer representation of the month part
	 */
	public int getDateMonth(final Date date) {
		return Integer.parseInt(new SimpleDateFormat("M").format(date));
	}

	/**
	 * Returns the year part of a date
	 * @param date The date to process
	 * @return the integer representation of the year part
	 */
	public int getDateYear(final Date date) {
		return Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
	}

	/**
	 * @return a list of Month numbers ( from 1 to 12 )
	 */
	public Vector<String> getMonthValues() {
		Vector<String> ret = new Vector<String>();
		ret.add("1");
		ret.add("2");
		ret.add("3");
		ret.add("4");
		ret.add("5");
		ret.add("6");
		ret.add("7");
		ret.add("8");
		ret.add("9");
		ret.add("10");
		ret.add("11");
		ret.add("12");
		
		return ret;
	}

	/**
	 * @return a list of i18n month names
	 */
	public Vector<String> getMonthNames(String lang) {
		Vector<String> ret = new Vector<String>();
		ret.add("Enero");
		ret.add("Febrero");
		ret.add("Marzo");
		ret.add("Abril");
		ret.add("Mayo");
		ret.add("Junio");
		ret.add("Julio");
		ret.add("Agosto");
		ret.add("Septiembre");
		ret.add("Octubre");
		ret.add("Noviembre");
		ret.add("Diciembre");
		
		return ret;
	}

	/**
	 * @return a list of country names
	 */
	//FIXME: I'm Hardcoded!!!
	public Vector<String> getCountryNames(String lang) {
		Vector<String> ret = new Vector<String>();
		if(user != null && user.getAvailableCountries() != null && user.getAvailableCountries().size() != 0 ) {
			ret.addAll(user.getAvailableCountries());
		} else {
			ret.addAll(CountryHelper.getCountryNamesAsList());
		}
		return ret;
	}

	/**
	 * @return a list of inscriptions names
	 */
	public Vector<String> getInscriptionsNames(String lang) {
		Vector<String> ret = new Vector<String>();
		ret.add("Responsable Inscripto");
		ret.add("Responsable No Inscripto");
		ret.add("Excento");
		
		return ret;
	}

	/**
	 * @return a list of i18n gender names
	 */
	public Vector<String> getGenderNames(String lang) {

		Vector<String> ret = new Vector<String>();
		ret.add("Masculino");
		ret.add("Femenino");
		
		return ret;
	}

	/**
	 * @return a list of gender values
	 */
	public Vector<String> getGenderValues() {
		Vector<String> ret = new Vector<String>();
		ret.add("male");
		ret.add("female");
		
		return ret;
	}

	/**
	 * @return a list of i18n role names
	 */
	public Vector<String> getRoleNames(String lang) {

		Vector<String> ret = new Vector<String>();
		if( lang.startsWith("en")) {
			ret.add("Standard User");
			ret.add("Mall Manager");
			ret.add("Brand Manager");
			ret.add("Financial Entity");
			ret.add("Data Entry");
			ret.add("Country Manager");
			ret.add("Super Administrator");
			ret.add("Read Only");
			ret.add("Coupon Entry");
			ret.add("Application");
		} else {
			ret.add("Usuario Standard");
			ret.add("Mall Manager");
			ret.add("Brand Manager");
			ret.add("Financial Entity");
			ret.add("Data Entry");
			ret.add("Country Manager");
			ret.add("Super Admin");
			ret.add("Solo Lectura");
			ret.add("Entrada de Cupones");
			ret.add("Aplicacion");
		}
		
		return ret;
	}

	/**
	 * Returns a list with all the status names
	 * 
	 * @param lang
	 *            The language to use
	 * @return
	 */
	public Vector<String> getStatusNames(String lang) {
		Vector<String> ret = new Vector<String>();
		if( lang.startsWith("en")) {
			ret.add("Enabled");
			ret.add("Disabled");
			ret.add("Pending");
		} else {
			ret.add("Habilitado");
			ret.add("Deshabilitado");
			ret.add("Pendiente");
		}
		
		return ret;
	}

	/**
	 * Returns a list with all the status values
	 * 
	 * @return
	 */
	public Vector<String> getStatusValues() {
		Vector<String> ret = new Vector<String>();
		ret.add(String.valueOf(StatusAware.STATUS_ENABLED));
		ret.add(String.valueOf(StatusAware.STATUS_DISABLED));
		ret.add(String.valueOf(StatusAware.STATUS_PENDING));
		
		return ret;
	}
	
	/**
	 * @return a list of role values
	 */
	public Vector<String> getRoleValues() {
		Vector<String> ret = new Vector<String>();
		ret.add(String.valueOf(UserSecurity.Role.USER));
		ret.add(String.valueOf(UserSecurity.Role.SHOPPING));
		ret.add(String.valueOf(UserSecurity.Role.BRAND));
		ret.add(String.valueOf(UserSecurity.Role.STORE));
		ret.add(String.valueOf(UserSecurity.Role.DATAENTRY));
		ret.add(String.valueOf(UserSecurity.Role.COUNTRY_ADMIN));
		ret.add(String.valueOf(UserSecurity.Role.ADMIN));
		ret.add(String.valueOf(UserSecurity.Role.READ_ONLY));
		ret.add(String.valueOf(UserSecurity.Role.COUPON_ENTRY));
		ret.add(String.valueOf(UserSecurity.Role.APPLICATION));
		
		return ret;
	}

	/**
	 * @return a list of i18n boolean names
	 */
	public Vector<String> getBooleanNames(String lang) {

		Vector<String> ret = new Vector<String>();
		ret.add("Si");
		ret.add("No");
		
		return ret;
	}

	/**
	 * @return a list of boolean values
	 */
	public Vector<String> getBooleanValues() {
		Vector<String> ret = new Vector<String>();
		ret.add("true");
		ret.add("false");
		
		return ret;
	}

	/**
	 * @return a list of i18n boolean names
	 */
	public Vector<String> getActiveInactiveNames(String lang) {

		Vector<String> ret = new Vector<String>();
		ret.add("Todos");
		ret.add("Activos");
		ret.add("Inactivos");
		
		return ret;
	}

	/**
	 * @return a list of boolean values
	 */
	public Vector<String> getActiveInactiveValues() {
		Vector<String> ret = new Vector<String>();
		ret.add("0");
		ret.add("1");
		ret.add("2");
		
		return ret;
	}

	/**
	 * @return Redirection helper
	 * @param uri
	 *            The uri to redirect to
	 * @return
	 */
	public static String sendRedirect(HttpServletRequest request, String uri) {
		request.setAttribute(TARGET_URI, uri);
		return GENERIC_REDIRECTOR;
	}

	/**
	 * Converts a date into a string
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		if( date != null )
			return new SimpleDateFormat("dd/MM/yyyy").format(date);
		else
			return "";
	}

	/**
	 * Converts a time into a string
	 * @param date
	 * @return
	 */
	public static String formatTime(Date date) {
		if( date != null )
			return new SimpleDateFormat("HH:mm").format(date);
		else
			return "";
	}
	
	/**
	 * Shows a string representation of the user activity status
	 * @param status
	 * @return
	 */
	public static String formatUserActivityStatus(Integer status) {
		if( status == null ) {
			return "";
		} else if( status == User.STATUS_ACTIVE ) {
			return "Activo";
		} else if ( status == User.STATUS_INACTIVE ) {
			return "Inactivo";
		} else {
			return "";
		}
	}
}
