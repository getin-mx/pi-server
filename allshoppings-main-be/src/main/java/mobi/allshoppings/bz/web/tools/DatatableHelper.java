package mobi.allshoppings.bz.web.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import mobi.allshoppings.auth.UserInfo;
import mobi.allshoppings.dao.GenericDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.tools.MultiLang;

public class DatatableHelper {

	public static final Logger log = Logger.getLogger(DatatableHelper.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	/**
	 * The Class wich will be managed by this datatable;
	 */
	private Class<?> clazz;

	/**
	 * This datatable action list.<br>
	 * Each action is rendered as a button in the record's action bar
	 */
	private List<Action> actions;

	/**
	 * This datatable properties list.<br>
	 * Each property is rendered as a field of this record (<<TD>>)<br>.
	 * Please, do not confuse with sortFields. Properties are resolved by
	 * getting a get + property name reflection call, while sortFields are the
	 * plain attribute names.
	 * 
	 * @see mobi.allshoppings.bz.web.DatatableHelper#sortFields
	 */
	private List<String> properties;

	/**
	 * The list of attribute names to sort for.<br>
	 * Please, do not confuse with properties. Properties are resolved by
	 * getting a get + property name reflection call, while sortFields are the
	 * plain attribute names.
	 * 
	 * @see mobi.allshoppings.bz.web.DatatableHelper#properties
	 */
	private List<String[]> sortFields;

	/**
	 * Decorators plugin.
	 * 
	 * @see mobi.allshoppings.bz.web.DatatableHelper#properties
	 */
	private Object decorator;

	/**
	 * Table Language
	 */
	private String lang;
	
	private String keyName;
	private String keyValue;

	private String[] masterOrderField;
	private String masterOrderType;
	
	/**
	 * Generic constructor
	 */
	public DatatableHelper(Class<?> clazz) {
		super();
		this.keyName = null;
		this.keyValue = null;
		this.clazz = clazz;
		this.actions = new Vector<Action>();
		this.properties = new Vector<String>();
		this.sortFields = new Vector<String[]>();
	}

	/**
	 * Renders this datatable in a final JSONObject
	 * 
	 * @param dao
	 *            The dao to work with
	 * @param request
	 *            The incoming request
	 * @return The hole table data embedded in a JSONObject
	 * @throws ASException
	 */
	public JSONObject render(GenericDAO<?> dao, HttpServletRequest request)
			throws ASException {
		
		long from = Long.parseLong(request.getParameter("iDisplayStart").toString());
		long to = Long.parseLong(request.getParameter("iDisplayLength").toString()) + from;
		String[] orderField = (masterOrderField != null) ? masterOrderField : sortFields.size() > 0 ? sortFields.get(Integer.parseInt(request.getParameter("iSortCol_0"))) : new String[0];
		String orderType = StringUtils.hasText(masterOrderType) ? masterOrderType : request.getParameter("sSortDir_0");
		String search = request.getParameter("sSearch");

		try {
			ArrayList<String> searchFields = new ArrayList<String>();
			for( int i = 0; i < sortFields.size(); i++ ) {
				for( int j = 0; j < sortFields.get(i).length; j++ ) {
					searchFields.add(sortFields.get(i)[j]);
				}
			}

			UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
			
			List<?> l;
			if( keyName == null) {
				l = dao.getForTable(orderField, orderType, searchFields.toArray(new String[searchFields.size()]), search, from, to, userInfo);
			} else {
				l = dao.getForTableWidthKey(keyName, keyValue, orderField, orderType, searchFields.toArray(new String[searchFields.size()]), search, from, to, userInfo);
			}

//			if( StringUtils.hasText(search)) {
//				boolean asc = true;
//				if( orderType.equals("desc")) asc = false;
//				if( orderField != null && orderField.length > 0 ) Collections.sort(l, new GenericModelKeyComparator(clazz, orderField[0], asc, null));
//			}
			
			long elementCount = 0;
			if (keyName == null ) {
//				if( StringUtils.hasText(search)) {
					elementCount = l.size();
//				} else {
//					elementCount = dao.count(userInfo);
//				}
			} else {
//				if( StringUtils.hasText(search)) {
					elementCount = l.size();
//				} else {
//					elementCount = dao.count(keyName, keyValue, userInfo);
//				}
			}
			Vector<String[]> aaData = this.render(l);

			JSONObject json = new JSONObject();
			json.put("aaData", aaData);
			json.put("iTotalDisplayRecords", elementCount);
			json.put("iTotalRecords", elementCount);
			json.put("sEcho", Long.parseLong(request.getParameter("sEcho").toString()) + 1);

			return json;

		} catch (JSONException | IllegalArgumentException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	/**
	 * Renders this datatable
	 * 
	 * @param dataInput
	 *            The filtered list of objects to render
	 * @return An aaData array to be sent in JSON to the Web Browser
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Vector<String[]> render(List<?> dataInput)
			throws IllegalArgumentException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {

		Vector<String[]> aaData = new Vector<String[]>();
		for (int i = 0; i < dataInput.size(); i++) {
			String[] elem = new String[properties.size() + 1];
			Object o = dataInput.get(i);
			for (int j = 0; j < properties.size(); j++) {
				elem[j] = decorate(o, getProperty(o, properties.get(j)), properties.get(j));
			}
			StringBuffer actionSB = new StringBuffer();
			for (int j = 0; j < actions.size(); j++) {
				actionSB.append(actions.get(j).render(
						decorate(o, getProperty(o, "identifier"), "identifier"), o));
			}
			elem[properties.size()] = actionSB.toString();
			aaData.add(elem);
		}

		return aaData;
	}

	/**
	 * Resolves a property string mode
	 * @param o The object to get the property from
	 * @param property The property name to get
	 * @return The property string value, or a null if no value is set.
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Object getProperty(Object o, String property)
			throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		String name;
		Class<?> params[] = {};
		Object args[] = {};
		Class<?> clazz = this.clazz;

		try {
			String[] parts = property.split("\\.");
			for( int i = 0; i < parts.length; i++ ) {
				if( o == null ) return null;
				if( parts[i] == null || parts[i].trim().equals("")) return null;

				if (parts[i].trim().length() > 1) {
					name = "get" + parts[i].trim().substring(0, 1).toUpperCase()
							+ parts[i].trim().substring(1);
				} else {
					name = "get" + parts[i].trim().toUpperCase();
				}
				Method method = clazz.getDeclaredMethod(name, params);
				o = method.invoke(o, args);
				if( o == null ) return null;
				clazz = o.getClass();
			}
			if( o instanceof Date ) {
				return sdf.format((Date)o);
			} else if( o instanceof MultiLang ) {
				if( StringUtils.hasText(lang)) {
					return ((MultiLang)o).get(lang);
				} else {
					return o.toString();
				}
			} else {
				return o.toString();
			}
		} catch( NoSuchMethodException e ) {
			return o;
		}
	}

	/**
	 * Decorates a response
	 * @param value the value to decorate
	 * @param property the property to find
	 * @return a decorated value
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private String decorate(Object obj, Object value, String property)
			throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		if( value == null ) {
			return "";
		}

		if(!StringUtils.hasText(value.toString())) {
			return "";
		}

		if(!StringUtils.hasText(property)) {
			return value.toString();
		}

		if( decorator == null ) {
			return value.toString();
		}

		String name;
		Class<?> params[] = {String.class};
		Class<?> specializedParams[] = {obj.getClass()};
		Object args[] = {value};
		Object specializedArgs[] = {obj};
		Class<?> clazz = decorator.getClass();
		Object o = decorator;

		String[] parts = property.split("\\.");
		int i = parts.length -1;
		if( i < 0 ) return value.toString();

		if( parts[i] == null || parts[i].trim().equals("")) return value.toString();

		if (parts[i].trim().length() > 1) {
			name = "get" + parts[i].trim().substring(0, 1).toUpperCase()
					+ parts[i].trim().substring(1);
		} else {
			name = "get" + parts[i].trim().toUpperCase();
		}
		try {
			Method method = clazz.getDeclaredMethod(name, specializedParams);
			o = method.invoke(o, specializedArgs);
			if( o == null ) return value.toString();
		} catch( NoSuchMethodException e ) {
			try {
				Method method = clazz.getDeclaredMethod(name, params);
				o = method.invoke(o, args);
				if( o == null ) return value.toString();
			} catch( NoSuchMethodException e1 ) {
				return value.toString();
			}
		}
		return o.toString();

	}

	/**
	 * Adds a property to the properties list
	 * 
	 * @param property
	 *            The property to add
	 */
	public void addProperty(String property) {
		if (!properties.contains(property)) {
			properties.add(property);
		}
	}

	/**
	 * Removes a property from the properties list
	 * 
	 * @param property
	 *            The property to remove
	 */
	public void removeProperty(String property) {
		if (properties.contains(property)) {
			properties.remove(property);
		}
	}

	/**
	 * Adds several properties to the properties list
	 * 
	 * @param properties
	 *            A String array of properties to add
	 */
	public void addProperties(String[] properties) {
		for (int i = 0; i < properties.length; i++) {
			this.addProperty(properties[i]);
		}
	}

	/**
	 * Removes all the properties from the properties list
	 */
	public void clearProperties() {
		this.properties.clear();
	}

	/**
	 * Adds a Field to the sortFields list
	 * 
	 * @param sortField
	 *            The Sort Field to add
	 */
	public void addSortField(String[] sortField) {
		if (!sortFields.contains(sortField)) {
			sortFields.add(sortField);
		}
	}

	/**
	 * Adds a Field to the sortFields list
	 * 
	 * @param sortField
	 *            The Sort Field to add
	 */
	public void addSortField(String sortField) {
		if (!sortFields.contains(sortField)) {
			sortFields.add(new String[] {sortField});
		}
	}

	/**
	 * Removes a field from the sort Fields list
	 * 
	 * @param sortField
	 *            The sort Field to remove
	 */
	public void removeSortField(String[] sortField) {
		if (sortFields.contains(sortField)) {
			sortFields.remove(sortField);
		}
	}

	/**
	 * Removes a field from the sort Fields list
	 * 
	 * @param sortField
	 *            The sort Field to remove
	 */
	public void removeSortField(String sortField) {
		String[] sf = new String[] {sortField};
		if (sortFields.contains(sf)) {
			sortFields.remove(sf);
		}
	}

	/**
	 * Adds several fields to the sortFields list
	 * 
	 * @param sortFields
	 *            A String array of sortFields to add
	 */
	public void addSortFields(String[][] sortFields) {
		for (int i = 0; i < sortFields.length; i++) {
			this.addSortField(sortFields[i]);
		}
	}
	
	/**
	 * Adds several fields to the sortFields list
	 * 
	 * @param sortFields
	 *            A String array of sortFields to add
	 */
	public void addSortFields(String[] sortFields) {
		for (int i = 0; i < sortFields.length; i++) {
			String[] sf = new String[] {sortFields[i]};
			this.addSortField(sf);
		}
	}

	/**
	 * Removes all the fields from the sortFields list
	 */
	public void clearSortFields() {
		this.sortFields.clear();
	}

	/**
	 * Removes all the actions from the actions list
	 */
	public void clearActions() {
		this.actions.clear();
	}

	/**
	 * Adds a new action to the action list
	 * 
	 * @param actionId
	 *            The ID to identify this action
	 * @param actionIcon
	 *            The action icon
	 * @param actionFunction
	 *            The action function
	 */
	public void addAction(String actionId, String actionIcon,
			String actionFunction) {
		addAction(actionId, actionIcon, actionFunction, false, null, false, Action.GET_METHOD, null);
	}

	/**
	 * Adds a new action to the action list
	 * 
	 * @param actionId
	 *            The ID to identify this action
	 * @param actionIcon
	 *            The action icon
	 * @param actionFunction
	 *            The action function
	 * @param condition
	 *            A condition to make this action visible or not
	 */
	public void addAction(String actionId, String actionIcon,
			String actionFunction, ActionCondition condition) {
		addAction(actionId, actionIcon, actionFunction, false, null, false, Action.GET_METHOD, condition);
	}

	/**
	 * Adds a new action to the action list
	 * 
	 * @param actionId
	 *            The ID to identify this action
	 * @param actionIcon
	 *            The action icon
	 * @param actionFunction
	 *            The action function
	 * @param requestConfirmation
	 * 			  Should this action request a confirmation message?
	 * @param confirmationMessage
	 * 			  Confirmation message to show
	 * @param ajaxRequest
	 * 			  Should this action be managed as an ajax request?
	 */
	public void addAction(String actionId, String actionIcon, String actionFunction, 
			boolean requestConfirmation, String confirmationMessage, boolean ajaxRequest,
			String method) {
		Action action = new Action(actionId, actionIcon, actionFunction, 
				requestConfirmation, confirmationMessage, ajaxRequest, method, null);
		if (!actions.contains(action)) {
			actions.add(action);
		}
	}

	/**
	 * Adds a new action to the action list
	 * 
	 * @param actionId
	 *            The ID to identify this action
	 * @param actionIcon
	 *            The action icon
	 * @param actionFunction
	 *            The action function
	 * @param requestConfirmation
	 * 			  Should this action request a confirmation message?
	 * @param confirmationMessage
	 * 			  Confirmation message to show
	 * @param ajaxRequest
	 * 			  Should this action be managed as an ajax request?
	 * @param method
	 * 			  GET, POST or DELETE method
	 */
	public void addAction(String actionId, String actionIcon, String actionFunction, 
			boolean requestConfirmation, String confirmationMessage, boolean ajaxRequest,
			String method, ActionCondition condition) {
		Action action = new Action(actionId, actionIcon, actionFunction, 
				requestConfirmation, confirmationMessage, ajaxRequest, method, condition);
		if (!actions.contains(action)) {
			actions.add(action);
		}
	}

	/**
	 * Removes an action from the actions list
	 * 
	 * @param actionId
	 *            The action ID to remove
	 */
	public void removeAction(String actionId) {
		Action action = new Action(actionId, null, null);
		if (actions.contains(action)) {
			actions.remove(action);
		}
	}
	
	/**
	 * Inner interface to represent conditionals in actions
	 * 
	 * @author mhapanowicz
	 *
	 */
	public interface ActionCondition {
		public boolean checkCondition(Object element);
	}

	/**
	 * Inner class to represent an action in the action bar of the datatable
	 * 
	 * @author mhapanowicz
	 */
	public class Action {
		
		public final static String GET_METHOD = "get";
		public final static String POST_METHOD = "post";
		public final static String DELETE_METHOD = "delete";

		/**
		 * The ID to identify an action
		 */
		private String id;

		/**
		 * Icon location to show
		 */
		private String icon;

		/**
		 * Function to execute
		 */
		private String function;
		
		/**
		 * Confirmation request flag. If this is set to true, then a
		 * confirmation message will be shown previous to calling the action. <br>
		 * Very useful for delete actions.
		 */
		private boolean requestConfirmation;
		
		/**
		 * Confirmation message for the request confirmation flag. It only works
		 * when the confirmation flag is set to true.<br>
		 * 
		 * @see mobi.allshopiings.bz.web.DatatableHelper#requestConfirmation
		 */
		private String confirmationMessage;
		
		/**
		 * This action should redirect to a simple URL or the action will be
		 * handled by Ajax?
		 */
		private boolean ajaxRequest;

		/**
		 * GET, POST or DELETE method
		 */
		private String method;
		
		/**
		 * Sets a specific condition for showing an action
		 */
		private ActionCondition condition;
		
		/**
		 * Standard constructor
		 * 
		 * @param id
		 *            This action Identifier
		 * @param icon
		 *            This action icon
		 * @param function
		 *            This action function
		 */
		public Action(String id, String icon, String function) {
			this( id, icon, function, false, null, false, GET_METHOD, null);
		}

		/**
		 * Standard constructor
		 * 
		 * @param id
		 *            This action Identifier
		 * @param icon
		 *            This action icon
		 * @param function
		 *            This action function to call
		 * @param requestConfirmation
		 *            Need a confirmation request?
		 * @param confirmationMessage
		 *            This action confirmation message
		 * @param ajaxRequest
		 *            Will this be managed as an ajax request?
		 * @param method
		 *            GET, POST or DELETE methods
		 * @param condition
		 * 			  A Special action condition to be checked at render time
		 */
		public Action(String id, String icon, String function,
				boolean requestConfirmation, String confirmationMessage,
				boolean ajaxRequest, String method, ActionCondition condition) {
			this.id = id;
			this.icon = icon;
			this.function = function;
			this.requestConfirmation = requestConfirmation;
			this.confirmationMessage = confirmationMessage;
			this.ajaxRequest = ajaxRequest;
			this.method = method;
			this.setCondition(condition);
		}
		
		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @param id
		 *            the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * @return the icon
		 */
		public String getIcon() {
			return icon;
		}

		/**
		 * @param icon
		 *            the icon to set
		 */
		public void setIcon(String icon) {
			this.icon = icon;
		}

		/**
		 * @return the function
		 */
		public String getFunction() {
			return function;
		}

		/**
		 * @param function
		 *            the function to set
		 */
		public void setFunction(String function) {
			this.function = function;
		}

		/**
		 * @return the requestConfirmation
		 */
		public boolean isRequestConfirmation() {
			return requestConfirmation;
		}

		/**
		 * @param requestConfirmation
		 *            the requestConfirmation to set
		 */
		public void setRequestConfirmation(boolean requestConfirmation) {
			this.requestConfirmation = requestConfirmation;
		}

		/**
		 * @return the confirmationMessage
		 */
		public String getConfirmationMessage() {
			return confirmationMessage;
		}

		/**
		 * @param confirmationMessage
		 *            the confirmationMessage to set
		 */
		public void setConfirmationMessage(String confirmationMessage) {
			this.confirmationMessage = confirmationMessage;
		}

		/**
		 * @return the ajaxRequest
		 */
		public boolean isAjaxRequest() {
			return ajaxRequest;
		}

		/**
		 * @param ajaxRequest
		 *            the ajaxRequest to set
		 */
		public void setAjaxRequest(boolean ajaxRequest) {
			this.ajaxRequest = ajaxRequest;
		}
		
		/**
		 * @return the method
		 */
		public String getMethod() {
			return method;
		}

		/**
		 * @param method the method to set
		 */
		public void setMethod(String method) {
			this.method = method;
		}

		/**
		 * Renders this action button in HTML mode
		 * 
		 * @param id
		 *            The id to redirect this action
		 * @param id
		 *            The object to render in this
		 * @return The rendered HTML action code to be included in the datatable
		 *         record
		 */
		public String render(String id, Object o) {
			StringBuffer sb = new StringBuffer();

			if( null == condition || condition.checkCondition(o)) {
				if( !requestConfirmation ) {
					sb.append("<a href=\"").append(this.function).append("/")
					.append(id)
					.append("\"><img src=\"/main-be/css/images/icons/dark/")
					.append(this.icon)
					.append("\" width=\"16\" height=\"16\"></img></a>");
				} else {
					sb.append("<a href=\"#\" onclick=\"")
					.append("$.confirm($.i18n._('").append(confirmationMessage).append("'), ")
					.append("function() {window.location='").append(this.function).append("/").append(id).append("'});")
					.append("\"><img src=\"/main-be/css/images/icons/dark/")
					.append(this.icon)
					.append("\" width=\"16\" height=\"16\"></img></a>");
				}
			}
			
			return sb.toString();
		}

		/**
		 * @see java.lang.Object#equals(Object)
		 */
		@Override
		public boolean equals(Object another) {
			if (another instanceof Action) {
				Action anothera = (Action) another;
				if (this.id == null)
					return false;
				if (anothera.id == null)
					return false;
				if (anothera.id.equals(this.id)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * @return the condition
		 */
		public ActionCondition getCondition() {
			return condition;
		}

		/**
		 * @param condition the condition to set
		 */
		public void setCondition(ActionCondition condition) {
			this.condition = condition;
		}
		
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Object getDecorator() {
		return decorator;
	}

	public void setDecorator(Object decorator) {
		this.decorator = decorator;
	}

	public String[] getMasterOrderField() {
		return masterOrderField;
	}

	public void setMasterOrderField(String[] masterOrderField) {
		this.masterOrderField = masterOrderField;
	}

	public String getMasterOrderType() {
		return masterOrderType;
	}

	public void setMasterOrderType(String masterOrderType) {
		this.masterOrderType = masterOrderType;
	}
	
	
}
