package mobi.allshoppings.bz.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Email;

import mobi.allshoppings.auth.UserInfo;
import mobi.allshoppings.bz.web.tools.DatatableHelper.ActionCondition;
import mobi.allshoppings.dao.AreaDAO;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.FinancialEntityDAO;
import mobi.allshoppings.dao.OfferTypeDAO;
import mobi.allshoppings.dao.ServiceDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Area;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.FinancialEntity;
import mobi.allshoppings.model.OfferType;
import mobi.allshoppings.model.Service;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.UserSecurity;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.tools.CountryHelper;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.tools.CollectionFactory;

public class BaseWebController {

	public static final String R400 = "error/error_400";
	public static final String R403 = "error/error_403";
	public static final String R404 = "error/error_404";
	public static final String R405 = "error/error_405";
	public static final String R500 = "error/error_500";
	public static final String R501 = "error/error_501";
	public static final String R505 = "error/error_505";

	public static final String CAN_WRITE = "canwrite";
	
	public static final String ENTITY_ID = "entityId";
	public static final String ENTITY_KIND = "entityKind";
	
	public static final String OFFER_TYPE_IDS = "offerTypeIds";
	public static final String OFFER_TYPE_NAMES = "offerTypeNames";
	public static final String BRAND_IDS = "brandIds";
	public static final String BRAND_NAMES = "brandNames";
	public static final String BRAND_OFFER_IDS = "brandOfferIds";
	public static final String BRAND_OFFER_NAMES = "brandOfferNames";
	public static final String SHOPPING_IDS = "shoppingIds";
	public static final String SHOPPING_NAMES = "shoppingNames";
	public static final String STORE_SHOPPING_IDS = "storeShoppingIds";
	public static final String STORE_SHOPPING_NAMES = "storeShoppingNames";
	public static final String SHOPPING_OFFER_IDS = "shoppingOfferIds";
	public static final String SHOPPING_OFFER_NAMES = "shoppingOfferNames";
	public static final String SERVICE_IDS = "serviceIds";
	public static final String SERVICE_NAMES = "serviceNames";
	public static final String AREA_IDS = "areaIds";
	public static final String AREA_NAMES = "areaNames";
	public static final String FINANCIAL_ENTITY_IDS = "financialEntityIds";
	public static final String FINANCIAL_ENTITY_NAMES = "financialEntityNames";
	public static final String STORE_IDS = "storeIds";
	public static final String STORE_NAMES = "storeNames";
	public static final String ROLE_IDS = "roleIds";
	public static final String ROLE_NAMES = "roleNames";
	public static final String STATUS_IDS = "statusIds";
	public static final String STATUS_NAMES = "statusNames";

	@Autowired
	AreaDAO areaDao;
	@Autowired
	ServiceDAO serviceDao;
	@Autowired
	BrandDAO brandDao;
	@Autowired
	StoreDAO storeDao;
	@Autowired
	ShoppingDAO shoppingDao;
	@Autowired
	OfferTypeDAO offerTypeDao;
	@Autowired
	FinancialEntityDAO financialEntityDao;
	@Autowired
	SystemConfiguration systemConfiguration;

	/**
	 * Safe recording of an email. It turns out that if you store a email entity
	 * and you do not set the email content as a valid email address... then the
	 * Google data store viewer crashes like a duck!!!!
	 * 
	 * @param emailSource
	 *            The email address to set
	 * @return a Safe Email entity
	 */
	public Email safeEmail(String emailSource) {
		if( emailSource == null || emailSource.trim().equals("")) {
			return null;
		} else {
			return new Email(emailSource);
		}
	}

	/**
	 * Adds a valid role array to the session to be processed by the
	 * assigned VM
	 * 
	 * @param request
	 *            The request to be managed
	 * @throws ASException
	 */
	protected void addRoleListToRequest(HttpServletRequest request) throws ASException {
		if( null == request.getSession().getAttribute(ROLE_IDS)) {
			List<Integer> roleIds = new Vector<Integer>();
			List<String> roleNames = new Vector<String>();
			String lang = getSessionLang(request);

			if( lang.startsWith("en")) {
				roleIds.add(UserSecurity.Role.USER);
				roleNames.add("Standard User");
				roleIds.add(UserSecurity.Role.SHOPPING);
				roleNames.add("Mall Manager");
				roleIds.add(UserSecurity.Role.BRAND);
				roleNames.add("Brand Manager");
				roleIds.add(UserSecurity.Role.STORE);
				roleNames.add("Financial Entity");
				roleIds.add(UserSecurity.Role.DATAENTRY);
				roleNames.add("Data Entry");
				roleIds.add(UserSecurity.Role.COUNTRY_ADMIN);
				roleNames.add("Country Manager");
				roleIds.add(UserSecurity.Role.ADMIN);
				roleNames.add("Super Administrator");
				roleIds.add(UserSecurity.Role.READ_ONLY);
				roleNames.add("Read Only");
				roleIds.add(UserSecurity.Role.COUPON_ENTRY);
				roleNames.add("Coupon Entry");
				roleIds.add(UserSecurity.Role.APPLICATION);
				roleNames.add("Application");
			} else {
				roleIds.add(UserSecurity.Role.USER);
				roleNames.add("Usuario Standard");
				roleIds.add(UserSecurity.Role.SHOPPING);
				roleNames.add("Mall Manager");
				roleIds.add(UserSecurity.Role.BRAND);
				roleNames.add("Brand Manager");
				roleIds.add(UserSecurity.Role.STORE);
				roleNames.add("Financial Entity");
				roleIds.add(UserSecurity.Role.DATAENTRY);
				roleNames.add("Data Entry");
				roleIds.add(UserSecurity.Role.COUNTRY_ADMIN);
				roleNames.add("Country Manager");
				roleIds.add(UserSecurity.Role.ADMIN);
				roleNames.add("Super Admin");
				roleIds.add(UserSecurity.Role.READ_ONLY);
				roleNames.add("Solo Lectura");
				roleIds.add(UserSecurity.Role.COUPON_ENTRY);
				roleNames.add("Entrada de Cupones");
				roleIds.add(UserSecurity.Role.APPLICATION);
				roleNames.add("Aplicacion");
			}

			request.getSession().setAttribute(ROLE_IDS, roleIds);
			request.getSession().setAttribute(ROLE_NAMES, roleNames);
		}
	}

	public void removeRoleListFromRequest(HttpServletRequest request) throws ASException {
		request.getSession().removeAttribute(ROLE_IDS);
		request.getSession().removeAttribute(ROLE_NAMES);
	}

	/**
	 * Adds a valid status array to the session to be processed by the
	 * assigned VM
	 * 
	 * @param request
	 *            The request to be managed
	 * @throws ASException
	 */
	protected void addStatusListToRequest(HttpServletRequest request) throws ASException {
		if( null == request.getSession().getAttribute(ROLE_IDS)) {
			List<Integer> statusIds = new Vector<Integer>();
			List<String> statusNames = new Vector<String>();
			String lang = getSessionLang(request);

			statusIds.add(StatusAware.STATUS_ENABLED);
			statusIds.add(StatusAware.STATUS_DISABLED);
			statusIds.add(StatusAware.STATUS_PENDING);
			if(!lang.startsWith("en")) {
				statusNames.add("Habilitado");
				statusNames.add("Deshabilitado");
				statusNames.add("Pendiente");
			} else {
				statusNames.add("Enabled");
				statusNames.add("Disabled");
				statusNames.add("PEnding");
			}

			request.getSession().setAttribute(STATUS_IDS, statusIds);
			request.getSession().setAttribute(STATUS_NAMES, statusNames);
		}
	}

	public void removeStatusListFromRequest(HttpServletRequest request) throws ASException {
		request.getSession().removeAttribute(STATUS_IDS);
		request.getSession().removeAttribute(STATUS_NAMES);
	}


	/**
	 * Adds a valid area array to the session to be processed by the
	 * assigned VM
	 * 
	 * @param request
	 *            The request to be managed
	 * @throws ASException
	 */
	protected void addAreaListToRequest(HttpServletRequest request) throws ASException {
		if( null == request.getSession().getAttribute(AREA_IDS)) {
			String lang = getSessionLang(request);
			List<Area> areas = areaDao.getAll();
			List<String> areaIds = new Vector<String>();
			List<String> areaNames = new Vector<String>();
			HashMap<String, String> tmp = new HashMap<String, String>();

			Iterator<Area> i = areas.iterator();
			while(i.hasNext()) {
				Area obj = i.next();
				tmp.put(obj.getName().get(lang), obj.getIdentifier());
			}

			Object[] keys = tmp.keySet().toArray();
			Arrays.sort(keys);

			for( Object k : keys ) {
				if( k != null ) {
					areaIds.add(tmp.get(k));
					areaNames.add(k.toString());
				}
			}

			request.getSession().setAttribute(AREA_IDS, areaIds);
			request.getSession().setAttribute(AREA_NAMES, areaNames);
		}
	}

	public void removeAreaListFromRequest(HttpServletRequest request) throws ASException {
		request.getSession().removeAttribute(AREA_IDS);
		request.getSession().removeAttribute(AREA_NAMES);
	}

	/**
	 * Adds a valid financial asset array to the session to be processed by the
	 * assigned VM
	 * 
	 * @param request
	 *            The request to be managed
	 * @throws ASException
	 */
	protected void addFinancialEntityListToRequest(HttpServletRequest request) throws ASException {
		if( null == request.getSession().getAttribute(FINANCIAL_ENTITY_IDS)) {
			List<FinancialEntity> financialEntities = financialEntityDao.getAllAndOrder("name");
			List<String> financialEntityIds = new Vector<String>();
			List<String> financialEntityNames = new Vector<String>();
			Iterator<FinancialEntity> i = financialEntities.iterator();
			List<String> availableCountries = CollectionFactory.createList();
			UserInfo u = (UserInfo)request.getSession().getAttribute("userInfo");
			
			if( u.getRole() == UserSecurity.Role.STORE ) {
				for( String feId : u.getFinancialEntities()) {
					FinancialEntity obj = financialEntityDao.get(feId, true);
					financialEntityIds.add(obj.getIdentifier());
					financialEntityNames.add(obj.getName());
				}
			} else {

				if( u.getRole() == UserSecurity.Role.COUNTRY_ADMIN  || u.getRole() == UserSecurity.Role.DATAENTRY || u.getRole() == UserSecurity.Role.READ_ONLY ) {
					availableCountries = u.getAvailableCountries(); 
				}
				if( u.getRole() == UserSecurity.Role.SHOPPING) {
					for( String shopId : u.getShoppings()) {
						Shopping s = shoppingDao.get(shopId, true);
						if(!availableCountries.contains(s.getAddress().getCountry())) 
							availableCountries.add(s.getAddress().getCountry());
					}
				} else if( u.getRole() == UserSecurity.Role.BRAND ) {
					for( String brandId : u.getBrands() ) {
						Brand s = brandDao.get(brandId, true);
						if(!availableCountries.contains(s.getCountry()))
							availableCountries.add(s.getCountry());
					}
				}

				if(availableCountries != null && availableCountries.size() > 0 ) {
					while(i.hasNext()) {
						FinancialEntity obj = i.next();
						for(String c : availableCountries) {
							ViewLocation vl = new ViewLocation();
							vl.setCountry(c);
							if( obj.isAvailableFor(vl)){
								financialEntityIds.add(obj.getIdentifier());
								financialEntityNames.add(obj.getName());
								break;
							}
						}
					}
				} else {
					while(i.hasNext()) {
						FinancialEntity obj = i.next();
						financialEntityIds.add(obj.getIdentifier());
						financialEntityNames.add(obj.getName());
					}
				}
			}
			request.getSession().setAttribute(FINANCIAL_ENTITY_IDS, financialEntityIds);
			request.getSession().setAttribute(FINANCIAL_ENTITY_NAMES, financialEntityNames);
		}
	}

	public void removeFinancialEntityListFromRequest(HttpServletRequest request) throws ASException {
		request.getSession().removeAttribute(FINANCIAL_ENTITY_IDS);
		request.getSession().removeAttribute(FINANCIAL_ENTITY_NAMES);
	}

	/**
	 * Adds a valid available country array to the session to be processed by the
	 * assigned VM
	 * 
	 * @param request
	 *            The request to be managed
	 * @throws ASException
	 */
	//FIXME: I'm Hardcoded!!!
	protected void addAvailableCountriesListToRequest(HttpServletRequest request) throws ASException {
		List<String> availableCountryIds = CollectionFactory.createList();
		List<String> availableCountryNames = CollectionFactory.createList();

		UserInfo u = (UserInfo)request.getSession().getAttribute("userInfo");
		if( u.getRole() == Role.COUNTRY_ADMIN || u.getRole() == Role.DATAENTRY || u.getRole() == Role.READ_ONLY ) {
			availableCountryIds = u.getAvailableCountries();
		} else if( u.getRole() == Role.SHOPPING ) {
			for( String shopId : u.getShoppings() ) {
				Shopping s = shoppingDao.get(shopId, true);
				if( !availableCountryIds.contains(s.getAddress().getCountry()) )
					availableCountryIds.add(s.getAddress().getCountry());
			}
		} else if( u.getRole() == Role.BRAND ) {
			for( String brandId : u.getBrands() ) {
				Brand s = brandDao.get(brandId, true);
				if(!availableCountryIds.contains(s.getCountry()))
					availableCountryIds.add(s.getCountry());
			}
		} else if( u.getRole() == Role.STORE ) {
			for( String feId : u.getFinancialEntities() ) {
				FinancialEntity s = financialEntityDao.get(feId, true);
				if( !availableCountryIds.contains(s.getCountry()))
					availableCountryIds.add(s.getCountry());
			}
		} else {
			availableCountryIds.addAll(CountryHelper.getCountryNamesAsList());
		}

		availableCountryNames.addAll(availableCountryIds);
		request.getSession().setAttribute("availableCountryIds", availableCountryIds);
		request.getSession().setAttribute("availableCountryNames", availableCountryNames);
	}

	/**
	 * Adds a valid available days array to the session to be processed by the
	 * assigned VM
	 * 
	 * @param request
	 *            The request to be managed
	 * @throws ASException
	 */
	//FIXME: I'm Hardcoded!!!
	protected void addDaysListToRequest(HttpServletRequest request) throws ASException {
		List<String> availableDaysIds = CollectionFactory.createList();
		List<String> availableDaysNames = CollectionFactory.createList();
		availableDaysIds.add("0");
		availableDaysIds.add("1");
		availableDaysIds.add("2");
		availableDaysIds.add("3");
		availableDaysIds.add("4");
		availableDaysIds.add("5");
		availableDaysIds.add("6");

		availableDaysNames.add("Lunes");
		availableDaysNames.add("Martes");
		availableDaysNames.add("Miercoles");
		availableDaysNames.add("Jueves");
		availableDaysNames.add("Viernes");
		availableDaysNames.add("Sabado");
		availableDaysNames.add("Domingo");

		request.getSession().setAttribute("availableDaysIds", availableDaysIds);
		request.getSession().setAttribute("availableDaysNames", availableDaysNames);
	}


	/**
	 * Adds a valid service array to the session to be processed by the
	 * assigned VM
	 * 
	 * @param request
	 *            The request to be managed
	 * @throws ASException
	 */
	protected void addServiceListToRequest(HttpServletRequest request) throws ASException {
		if( null == request.getSession().getAttribute(SERVICE_IDS)) {
			String lang = getSessionLang(request);
			List<Service> services = serviceDao.getAll();
			List<String> serviceIds = new Vector<String>();
			List<String> serviceNames = new Vector<String>();
			HashMap<String, String> tmp = new HashMap<String, String>();

			Iterator<Service> i = services.iterator();
			while(i.hasNext()) {
				Service obj = i.next();
				tmp.put(obj.getName().get(lang), obj.getIdentifier());
			}

			Object[] keys = tmp.keySet().toArray();
			Arrays.sort(keys);

			for( Object k : keys ) {
				if( k != null ) {
					serviceIds.add(tmp.get(k));
					serviceNames.add(k.toString());
				}
			}

			request.setAttribute(SERVICE_IDS, serviceIds);
			request.setAttribute(SERVICE_NAMES, serviceNames);
		}
	}

	public void removeServiceListFromRequest(HttpServletRequest request) throws ASException {
		request.getSession().removeAttribute(SERVICE_IDS);
		request.getSession().removeAttribute(SERVICE_NAMES);
	}

	/**
	 * Adds a valid shopping array to the session to be processed by the
	 * assigned VM
	 * 
	 * @param request
	 *            The request to be managed
	 * @throws ASException
	 */
	public void addShoppingListToRequest(HttpServletRequest request) throws ASException {
		if( null == request.getSession().getAttribute(SHOPPING_IDS)) {
			List<Shopping> shoppings = shoppingDao.getAllAndOrder("name");
			List<String> shoppingIds = new Vector<String>();
			List<String> shoppingNames = new Vector<String>();
			Iterator<Shopping> i = shoppings.iterator();

			UserInfo u = (UserInfo)request.getSession().getAttribute("userInfo");
			if( u.getRole() == UserSecurity.Role.SHOPPING ) {
				for( String shopId : u.getShoppings()) {
					Shopping obj = shoppingDao.get(shopId, true);
					shoppingIds.add(obj.getIdentifier());
					shoppingNames.add(obj.getName());
				}
			} else {
				List<String> availableCountries = CollectionFactory.createList();

				if( u.getRole() == UserSecurity.Role.COUNTRY_ADMIN || u.getRole() == UserSecurity.Role.DATAENTRY || u.getRole() == UserSecurity.Role.READ_ONLY ) {
					availableCountries = u.getAvailableCountries(); 
				} else if ( u.getRole() == UserSecurity.Role.STORE ) {
					for( String feId : u.getFinancialEntities() ) {
						FinancialEntity obj = financialEntityDao.get(feId, true);
						if( !availableCountries.contains(obj.getCountry()))
							availableCountries.add(obj.getCountry());
					}
				}

				if( availableCountries != null && availableCountries.size() > 0 ) {
					while(i.hasNext()) {
						Shopping obj = i.next();
						for(String c : availableCountries) {
							ViewLocation vl = new ViewLocation();
							vl.setCountry(c);
							if( obj.isAvailableFor(vl)){
								shoppingIds.add(obj.getIdentifier());
								shoppingNames.add(obj.getName());
								break;
							}
						}
					}
				} else {
					while(i.hasNext()) {
						Shopping obj = i.next();
						shoppingIds.add(obj.getIdentifier());
						shoppingNames.add(obj.getName());
					}
				}
			}

			request.getSession().setAttribute(SHOPPING_IDS, shoppingIds);
			request.getSession().setAttribute(SHOPPING_NAMES, shoppingNames);
		}
	}

	public void removeShoppingListFromRequest(HttpServletRequest request) throws ASException {
		request.getSession().removeAttribute(SHOPPING_IDS);
		request.getSession().removeAttribute(SHOPPING_NAMES);
		request.getSession().removeAttribute(SHOPPING_OFFER_IDS);
		request.getSession().removeAttribute(SHOPPING_OFFER_NAMES);
	}

	/**
	 * Adds a valid shopping array to the session to be processed by the
	 * assigned VM
	 * 
	 * @param request
	 *            The request to be managed
	 * @throws ASException
	 */
	public void addStoreShoppingListToRequest(HttpServletRequest request) throws ASException {
		if( null == request.getSession().getAttribute(STORE_SHOPPING_IDS)) {
			List<Shopping> shoppings = shoppingDao.getAllAndOrder("name");
			List<String> shoppingIds = new Vector<String>();
			List<String> shoppingNames = new Vector<String>();
			Iterator<Shopping> i = shoppings.iterator();

			UserInfo u = (UserInfo)request.getSession().getAttribute("userInfo");

			shoppingIds.add(null);
			shoppingNames.add("--- TIENDA DE CALLE ---");
			
			if( u.getRole() == UserSecurity.Role.SHOPPING ) {
				for( String shopId : u.getShoppings()) {
					Shopping obj = shoppingDao.get(shopId, true);
					shoppingIds.add(obj.getIdentifier());
					shoppingNames.add(obj.getName());
				}
			} else {
				List<String> availableCountries = CollectionFactory.createList();

				if( u.getRole() == UserSecurity.Role.COUNTRY_ADMIN || u.getRole() == UserSecurity.Role.DATAENTRY || u.getRole() == UserSecurity.Role.READ_ONLY ) {
					availableCountries = u.getAvailableCountries(); 
				} else if ( u.getRole() == UserSecurity.Role.STORE ) {
					for( String feId : u.getFinancialEntities() ) {
						FinancialEntity obj = financialEntityDao.get(feId, true);
						if( !availableCountries.contains(obj.getCountry()))
							availableCountries.add(obj.getCountry());
					}
				}

				if( availableCountries != null && availableCountries.size() > 0 ) {
					while(i.hasNext()) {
						Shopping obj = i.next();
						for(String c : availableCountries) {
							ViewLocation vl = new ViewLocation();
							vl.setCountry(c);
							if( obj.isAvailableFor(vl)){
								shoppingIds.add(obj.getIdentifier());
								shoppingNames.add(obj.getName());
								break;
							}
						}
					}
				} else {
					while(i.hasNext()) {
						Shopping obj = i.next();
						shoppingIds.add(obj.getIdentifier());
						shoppingNames.add(obj.getName());
					}
				}
			}

			request.getSession().setAttribute(STORE_SHOPPING_IDS, shoppingIds);
			request.getSession().setAttribute(STORE_SHOPPING_NAMES, shoppingNames);
		}
	}

	public void removeStoreShoppingListFromRequest(HttpServletRequest request) throws ASException {
		request.getSession().removeAttribute(STORE_SHOPPING_IDS);
		request.getSession().removeAttribute(STORE_SHOPPING_NAMES);
	}

	/**
	 * Adds a valid shopping array to the session to be processed by the
	 * assigned VM
	 * 
	 * @param request
	 *            The request to be managed
	 * @throws ASException
	 */
	public void addShoppingOfferListToRequest(HttpServletRequest request) throws ASException {
		if( null == request.getSession().getAttribute(SHOPPING_OFFER_IDS) ) {
			UserInfo u = (UserInfo)request.getSession().getAttribute("userInfo");
			if( u.getRole() == UserSecurity.Role.BRAND) {
				
				List<Shopping> shoppings = shoppingDao.getAllAndOrder("uIdentifier");
				List<String> shoppingIds = new Vector<String>();
				List<String> shoppingIds2 = new Vector<String>();
				List<String> shoppingNames = new Vector<String>();
				
				Iterator<Shopping> i = shoppings.iterator();

				for( String brandId : u.getBrands()) {
					List<Store> stores = storeDao.getUsingBrandAndStatus(brandId, null, "key");
					for( Store store : stores ) {
						if(!shoppingIds2.contains(store.getShoppingId())) {
							shoppingIds2.add(store.getShoppingId());
						}
					}
				}
				
				while(i.hasNext()) {
					Shopping b = i.next();
					if( shoppingIds2.contains(b.getIdentifier())) {
						shoppingIds.add(b.getIdentifier());
						shoppingNames.add(b.getName());
					}
				}
				
				request.getSession().setAttribute(SHOPPING_OFFER_IDS, shoppingIds);
				request.getSession().setAttribute(SHOPPING_OFFER_NAMES, shoppingNames);

			} else {
				addShoppingListToRequest(request);
				request.getSession().setAttribute(SHOPPING_OFFER_IDS, request.getSession().getAttribute(SHOPPING_IDS));
				request.getSession().setAttribute(SHOPPING_OFFER_NAMES, request.getSession().getAttribute(SHOPPING_NAMES));
			}
		}
	}

	/**
	 * Adds a valid brand array to the session to be processed by the
	 * assigned VM
	 * 
	 * @param request
	 *            The request to be managed
	 * @throws ASException
	 */
	public void addBrandOfferListToRequest(HttpServletRequest request) throws ASException {
		if( null == request.getSession().getAttribute(BRAND_OFFER_IDS) ) {
			UserInfo u = (UserInfo)request.getSession().getAttribute("userInfo");
			if( u.getRole() == UserSecurity.Role.SHOPPING) {
				
				List<Brand> brands = brandDao.getAllAndOrder("uName");
				List<String> brandIds = new Vector<String>();
				List<String> brandIds2 = new Vector<String>();
				List<String> brandNames = new Vector<String>();
				
				Iterator<Brand> i = brands.iterator();

				for( String shopId : u.getShoppings()) {
					List<Store> stores = storeDao.getUsingShoppingAndStatus(shopId, null, "key");
					for( Store store : stores ) {
						if(!brandIds2.contains(store.getBrandId())) {
							brandIds2.add(store.getBrandId());
						}
					}
				}
				
				while(i.hasNext()) {
					Brand b = i.next();
					if( brandIds2.contains(b.getIdentifier())) {
						brandIds.add(b.getIdentifier());
						brandNames.add(b.getName());
					}
				}
				
				request.getSession().setAttribute(BRAND_OFFER_IDS, brandIds);
				request.getSession().setAttribute(BRAND_OFFER_NAMES, brandNames);

			} else {
				addBrandListToRequest(request);
				request.getSession().setAttribute(BRAND_OFFER_IDS, request.getSession().getAttribute(BRAND_IDS));
				request.getSession().setAttribute(BRAND_OFFER_NAMES, request.getSession().getAttribute(BRAND_NAMES));
			}
		}
	}

	/**
	 * Adds a valid brand array to the session to be processed by the
	 * assigned VM
	 * 
	 * @param request
	 *            The request to be managed
	 * @throws ASException
	 */
	public void addBrandListToRequest(HttpServletRequest request) throws ASException {
		if( null == request.getSession().getAttribute(BRAND_IDS) ) {
			List<Brand> brands = brandDao.getAllAndOrder("uName");
			List<String> brandIds = new Vector<String>();
			List<String> brandNames = new Vector<String>();
			Iterator<Brand> i = brands.iterator();
			List<String> availableCountries = CollectionFactory.createList();
			UserInfo u = (UserInfo)request.getSession().getAttribute("userInfo");

			if( u.getRole() == UserSecurity.Role.BRAND ) {
				for( String brandId : u.getBrands() ) {
					Brand obj = brandDao.get(brandId, true);
					brandIds.add(obj.getIdentifier());
					brandNames.add(obj.getName());
				}
			} else {
				if( u.getRole() == UserSecurity.Role.COUNTRY_ADMIN || u.getRole() == UserSecurity.Role.DATAENTRY || u.getRole() == UserSecurity.Role.READ_ONLY ) {
					availableCountries = u.getAvailableCountries(); 
				} else if( u.getRole() == UserSecurity.Role.SHOPPING) {
					for( String shopId : u.getShoppings()) {
						Shopping s = shoppingDao.get(shopId, true);
						if(!availableCountries.contains(s.getAddress().getCountry())) 
							availableCountries.add(s.getAddress().getCountry());
					}
				} else if( u.getRole() == Role.STORE ) {
					for( String feId : u.getFinancialEntities() ) {
						FinancialEntity s = financialEntityDao.get(feId, true);
						if( !availableCountries.contains(s.getCountry()))
							availableCountries.add(s.getCountry());
					}
				}

				if(availableCountries != null && availableCountries.size() > 0 ) {
					while(i.hasNext()) {
						Brand obj = i.next();
						for(String c : availableCountries) {
							ViewLocation vl = new ViewLocation();
							vl.setCountry(c);
							if( obj.isAvailableFor(vl)){
								brandIds.add(obj.getIdentifier());
								brandNames.add(obj.getName());
								break;
							}
						}
					}
				} else {
					while(i.hasNext()) {
						Brand obj = i.next();
						brandIds.add(obj.getIdentifier());
						brandNames.add(obj.getName());
					}
				}
			}
			request.getSession().setAttribute(BRAND_IDS, brandIds);
			request.getSession().setAttribute(BRAND_NAMES, brandNames);
		}
	}

	public void removeBrandListFromRequest(HttpServletRequest request) throws ASException {
		request.getSession().removeAttribute(BRAND_IDS);
		request.getSession().removeAttribute(BRAND_NAMES);
		request.getSession().removeAttribute(BRAND_OFFER_IDS);
		request.getSession().removeAttribute(BRAND_OFFER_NAMES);
	}

	public String getSessionLang(HttpServletRequest request) {
		String lang = (String)request.getSession().getAttribute("lang");
		if(!StringUtils.hasText(lang)) lang = systemConfiguration.getDefaultLang();
		if( lang.length() > 2 ) {
			lang = lang.substring(0, 2);
		}
		return lang;
	}

	/**
	 * Adds a valid offer type array to the session to be processed by the
	 * assigned VM
	 * 
	 * @param request
	 *            The request to be managed
	 * @throws ASException
	 */
	public void addOfferTypeListToRequest(HttpServletRequest request) throws ASException {
		if( null == request.getSession().getAttribute(OFFER_TYPE_IDS)) {
			String lang = getSessionLang(request);
			List<OfferType> offerTypes = offerTypeDao.getAll();
			List<String> offerTypeIds = new Vector<String>();
			List<String> offerTypeNames = new Vector<String>();
			HashMap<String, String> tmp = new HashMap<String, String>();

			Iterator<OfferType> i = offerTypes.iterator();
			while(i.hasNext()) {
				OfferType obj = i.next();
				tmp.put(obj.getName().get(lang), obj.getIdentifier());
			}

			Object[] keys = tmp.keySet().toArray();
			Arrays.sort(keys);

			for( Object k : keys ) {
				if( k != null ) {
					offerTypeIds.add(tmp.get(k));
					offerTypeNames.add(k.toString());
				}
			}

			request.getSession().setAttribute(OFFER_TYPE_IDS, offerTypeIds);
			request.getSession().setAttribute(OFFER_TYPE_NAMES, offerTypeNames);
		}
	}

	public void removeOfferTypeListFromRequest(HttpServletRequest request) throws ASException {
		request.getSession().removeAttribute(OFFER_TYPE_IDS);
		request.getSession().removeAttribute(OFFER_TYPE_NAMES);
	}

	/**
	 * Adds a valid store array to the session to be processed by the
	 * assigned VM
	 * 
	 * @param request
	 *            The request to be managed
	 * @throws ASException
	 */
	public void addStoreListToRequest(HttpServletRequest request) throws ASException {
		if( null == request.getSession().getAttribute(STORE_IDS)) {
			List<Store> stores = storeDao.getAllAndOrder("uStoreName");
			List<String> storeIds = new Vector<String>();
			List<String> storeNames = new Vector<String>();
			Iterator<Store> i = stores.iterator();
			List<String> availableCountries = ((UserInfo)request.getSession().getAttribute("userInfo")).getAvailableCountries();
			if(availableCountries != null && availableCountries.size() > 0 ) {
				while(i.hasNext()) {
					Store obj = i.next();
					for(String c : availableCountries) {
						ViewLocation vl = new ViewLocation();
						vl.setCountry(c);
						if( obj.isAvailableFor(vl)){
							storeIds.add(obj.getIdentifier());
							storeNames.add(obj.getName());
							break;
						}
					}
				}
			} else {
				while(i.hasNext()) {
					Store obj = i.next();
					storeIds.add(obj.getIdentifier());
					storeNames.add(obj.getName());
				}
			}
			request.getSession().setAttribute(STORE_IDS, storeIds);
			request.getSession().setAttribute(STORE_NAMES, storeNames);
		}
	}

	public void removeStoreListFromRequest(HttpServletRequest request) throws ASException {
		request.getSession().removeAttribute(STORE_IDS);
		request.getSession().removeAttribute(STORE_NAMES);
	}

	public UserInfo getUserInfo(final HttpServletRequest request) {
		return (UserInfo)request.getSession().getAttribute("userInfo");
	}


	/**
	 * Creates a write condition for the table
	 * 
	 * @param userInfo
	 *            The user to check permissions
	 * 
	 * @return A functional action condition
	 */
	public ActionCondition writeCondition(final UserInfo userInfo) {
		return new ActionCondition() {
			@Override
			public boolean checkCondition(Object element) {
				if( userInfo.getRole() == Role.ADMIN ) return true;
				else return false;
			}
		};
	}
	
	/**
	 * Creates a read only condition for the table
	 * 
	 * @param userInfo
	 *            The user to check permissions
	 * 
	 * @return A functional action condition
	 */
	public ActionCondition readOnlyCondition(final UserInfo userInfo) {
		return new ActionCondition() {
			@Override
			public boolean checkCondition(Object element) {
				if( userInfo.getRole() == Role.READ_ONLY ) return true;
				else return false;
			}
		};
	}
}
