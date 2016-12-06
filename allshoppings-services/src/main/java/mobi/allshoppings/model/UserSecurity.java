package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import mobi.allshoppings.tools.CollectionFactory;

/**
 * This class represents the security status of an User, such as password,
 * tokens, status and validity
 * 
 * @author mhapanowicz
 * 
 */
@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
@EmbeddedOnly
public class UserSecurity implements Serializable {

	public interface UserSecurityStatusType {

		/**
		 * An administrative put the user on hold
		 */
		static final int LOCKED_BY_ADMINISTRATION = 1;

		/**
		 * the user himself has been put on hold, because several wrong
		 * passwords or something else
		 */
		static final int LOCKED_BY_USER = 3;

		/**
		 * Deleted. The user is unrecoverable (not yet)
		 */
		static final int DELETED = 5;

		/**
		 * The user is enabled and fully qualified to work
		 */
		static final int ENABLED = 7;

	}

	public interface Role {

		/**
		 * Single user
		 */
		static final int USER = 0;

		/**
		 * Super Administrator
		 */
		static final int ADMIN = 1;

		/**
		 * Country Administrator
		 */
		static final int COUNTRY_ADMIN = 3;

		/**
		 * Mall Manager
		 */
		static final int SHOPPING = 5;

		/**
		 * Brand Manager
		 */
		static final int BRAND = 7;

		/**
		 * Financial Entity Manager
		 */
		static final int FINANCIAL = 9;

		/**
		 * Data Entry
		 */
		static final int DATAENTRY = 11;

		/**
		 * Read Only
		 */
		static final int READ_ONLY = 13;

		/**
		 * Coupon Entry
		 */
		static final int COUPON_ENTRY = 15;

		/**
		 * Coupon Entry
		 */
		static final int APPLICATION = 17;

	}

	@Persistent
	/**
	 * User Password
	 */
	private String password;

	@Persistent
	/**
	 * User Alternative Password (for recovery)
	 */
	private String altPassword;

	@Persistent
	/**
	 * Token obtained after a successful login
	 */
	private String authToken;

	@Persistent
	/**
	 * After this date the token become invalid and the user must be re authenticate to obtain a valid token <br>
	 * Null values means the token does not expire
	 */
	private Date authTokenValidity;

	@Persistent
	/**
	 * Token obtained after a successful facebook login
	 */
	private String facebookToken;

	@Persistent
	/**
	 * After this date the token become invalid and the user must be re authenticate to obtain a valid token <br>
	 * Null values means the token does not expire
	 */
	private Date facebookTokenValidity;

	@Persistent
	/**
	 * Token obtained after request for password recovery
	 */
	private String recoveryToken;

	@Persistent
	/**
	 * After this date the password recovery token become invalid and the user must 
	 * be request this token again to obtain a valid token <br>
	 * Null values means the token does not expire
	 */
	private Date recoveryTokenValidity;

	@Persistent
	/**
	 * The user can be enabled or disabled. There are a few reasons to be disabled.
	 * See details on the enumeration
	 */
	private int status;

	/**
	 * The role the user belongs to. Only roles superior to Single User can
	 * access the backend
	 */
	private Integer role;

	/**
	 * Countries this user has access for, just thinking in Country Manager Role
	 */
	private List<String> availableCountries;

	/**
	 * Shoppings this user has access for, just thinking in Mall Manager Role
	 */
	private List<String> shoppings;

	/**
	 * Brands this user has access for, just thinking in Brand Manager Role
	 */
	private List<String> brands;

	/**
	 * Financial Entities this user has access for, just thinking in Financial Entity Role
	 */
	private List<String> financialEntities;

	/**
	 * Has this user access to manage users?
	 */
	private Boolean userManager = false;

	/**
	 * Has this user access to manage tables?
	 */
	private Boolean tableManager = false;

	/**
	 * Default constructor. Sets the security status type in ENABLED
	 */
	public UserSecurity() {
		this.setStatus(UserSecurityStatusType.ENABLED);
		this.setRole(Role.USER);
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the altPassword
	 */
	public String getAltPassword() {
		return altPassword;
	}

	/**
	 * @param altPassword the altPassword to set
	 */
	public void setAltPassword(String altPassword) {
		this.altPassword = altPassword;
	}

	/**
	 * @return the authToken
	 */
	public String getAuthToken() {
		return authToken;
	}

	/**
	 * @param authToken the authToken to set
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	/**
	 * @return the authTokenValidity
	 */
	public Date getAuthTokenValidity() {
		return authTokenValidity;
	}

	/**
	 * @param authTokenValidity the authTokenValidity to set
	 */
	public void setAuthTokenValidity(Date authTokenValidity) {
		this.authTokenValidity = authTokenValidity;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the role
	 */
	public Integer getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(Integer role) {
		this.role = role;
	}

	/**
	 * @return the availableCountries
	 */
	public List<String> getAvailableCountries() {
		return availableCountries;
	}

	/**
	 * @param availableCountries the availableCountries to set
	 */
	public void setAvailableCountries(List<String> availableCountries) {
		this.availableCountries = availableCountries;
	}

	/**
	 * Adds a new country to the available countries list
	 * @param country The country to add
	 */
	public void addAvailableCountry(String country) {
		if(!availableCountries.contains(country))
			availableCountries.add(country);
	}

	/**
	 * Removes a country from the available countries list
	 * @param country the country to remove
	 */
	public void removeAvailableCountry(String country) {
		if(availableCountries.contains(country))
			availableCountries.remove(country);
	}

	/**
	 * Clears the available countries list
	 */
	public void clearAvailableCountries() {
		this.availableCountries = CollectionFactory.createList();
	}

	/**
	 * @return the shoppings
	 */
	public List<String> getShoppings() {
		return shoppings;
	}

	/**
	 * @param shoppings the shoppings to set
	 */
	public void setShoppings(List<String> shoppings) {
		this.shoppings = shoppings;
	}

	/**
	 * Adds a new shopping to the available shoppings list
	 * 
	 * @param shopping
	 *            The shopping id to add
	 */
	public void addShopping(String shopping) {
		if(!shoppings.contains(shopping))
			shoppings.add(shopping);
	}

	/**
	 * Removes a shopping from the available shoppings list
	 * 
	 * @param shopping
	 *            The shopping id to remove
	 */
	public void removeShopping(String shopping) {
		if(shoppings.contains(shopping))
			shoppings.remove(shopping);
	}

	/**
	 * Clears the available shoppings list
	 */
	public void clearShoppings() {
		shoppings.clear();
	}

	/**
	 * @return the brands
	 */
	public List<String> getBrands() {
		return brands;
	}

	/**
	 * @param shoppings the brands to set
	 */
	public void setBrands(List<String> brands) {
		this.brands = brands;
	}

	/**
	 * Adds a new brand to the available brand list
	 * 
	 * @param brand
	 *            The brand id to add
	 */
	public void addBrand(String brand) {
		if(!brands.contains(brand))
			brands.add(brand);
	}

	/**
	 * Removes a brand from the available brand list
	 * 
	 * @param brand
	 *            The brand id to remove
	 */
	public void removeBrand(String brand) {
		if(brands.contains(brand))
			brands.remove(brand);
	}

	/**
	 * Clears the available brand list
	 */
	public void clearBrands() {
		brands.clear();
	}

	/**
	 * @return the financial Entities
	 */
	public List<String> getFinancialEntities() {
		return financialEntities;
	}

	/**
	 * @param financialEntities the financialEntities to set
	 */
	public void setFinancialEntities(List<String> financialEntities) {
		this.financialEntities = financialEntities;
	}

	/**
	 * Adds a new financial entity to the available financial entities list
	 * 
	 * @param financialEntity
	 *            The entity id to add
	 */
	public void addFinancialEntity(String financialEntity) {
		if(!financialEntities.contains(financialEntity))
			financialEntities.add(financialEntity);
	}

	/**
	 * Removes a financial entity from the available financial entities list
	 * 
	 * @param financialEntity
	 *            The entity id to remove
	 */
	public void removeFinancialEntity(String financialEntity) {
		if(financialEntities.contains(financialEntity))
			financialEntities.remove(financialEntity);
	}

	/**
	 * Clears the available financial entities list
	 */
	public void clearFinancialEntities() {
		financialEntities.clear();
	}

	/**
	 * @return the userManager
	 */
	public Boolean getUserManager() {
		return userManager;
	}

	/**
	 * @param userManager the userManager to set
	 */
	public void setUserManager(Boolean userManager) {
		this.userManager = userManager;
	}

	/**
	 * @return the tableManager
	 */
	public Boolean getTableManager() {
		return tableManager;
	}

	/**
	 * @param tableManager the tableManager to set
	 */
	public void setTableManager(Boolean tableManager) {
		this.tableManager = tableManager;
	}

	
	/**
	 * @return the recoveryToken
	 */
	public String getRecoveryToken() {
		return recoveryToken;
	}

	/**
	 * @param recoveryToken the recoveryToken to set
	 */
	public void setRecoveryToken(String recoveryToken) {
		this.recoveryToken = recoveryToken;
	}

	/**
	 * @return the recoveryTokenValidity
	 */
	public Date getRecoveryTokenValidity() {
		return recoveryTokenValidity;
	}

	/**
	 * @param recoveryTokenValidity the recoveryTokenValidity to set
	 */
	public void setRecoveryTokenValidity(Date recoveryTokenValidity) {
		this.recoveryTokenValidity = recoveryTokenValidity;
	}

	/**
	 * @return the facebookToken
	 */
	public String getFacebookToken() {
		return facebookToken;
	}

	/**
	 * @param facebookToken the facebookToken to set
	 */
	public void setFacebookToken(String facebookToken) {
		this.facebookToken = facebookToken;
	}

	/**
	 * @return the facebookTokenValidity
	 */
	public Date getFacebookTokenValidity() {
		return facebookTokenValidity;
	}

	/**
	 * @param facebookTokenValidity the facebookTokenValidity to set
	 */
	public void setFacebookTokenValidity(Date facebookTokenValidity) {
		this.facebookTokenValidity = facebookTokenValidity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((altPassword == null) ? 0 : altPassword.hashCode());
		result = prime * result
				+ ((authToken == null) ? 0 : authToken.hashCode());
		result = prime
				* result
				+ ((authTokenValidity == null) ? 0 : authTokenValidity
						.hashCode());
		result = prime
				* result
				+ ((availableCountries == null) ? 0 : availableCountries
						.hashCode());
		result = prime * result + ((brands == null) ? 0 : brands.hashCode());
		result = prime * result
				+ ((facebookToken == null) ? 0 : facebookToken.hashCode());
		result = prime
				* result
				+ ((facebookTokenValidity == null) ? 0 : facebookTokenValidity
						.hashCode());
		result = prime
				* result
				+ ((financialEntities == null) ? 0 : financialEntities
						.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((recoveryToken == null) ? 0 : recoveryToken.hashCode());
		result = prime
				* result
				+ ((recoveryTokenValidity == null) ? 0 : recoveryTokenValidity
						.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result
				+ ((shoppings == null) ? 0 : shoppings.hashCode());
		result = prime * result + status;
		result = prime * result
				+ ((tableManager == null) ? 0 : tableManager.hashCode());
		result = prime * result
				+ ((userManager == null) ? 0 : userManager.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserSecurity other = (UserSecurity) obj;
		if (altPassword == null) {
			if (other.altPassword != null)
				return false;
		} else if (!altPassword.equals(other.altPassword))
			return false;
		if (authToken == null) {
			if (other.authToken != null)
				return false;
		} else if (!authToken.equals(other.authToken))
			return false;
		if (authTokenValidity == null) {
			if (other.authTokenValidity != null)
				return false;
		} else if (!authTokenValidity.equals(other.authTokenValidity))
			return false;
		if (availableCountries == null) {
			if (other.availableCountries != null)
				return false;
		} else if (!availableCountries.equals(other.availableCountries))
			return false;
		if (brands == null) {
			if (other.brands != null)
				return false;
		} else if (!brands.equals(other.brands))
			return false;
		if (facebookToken == null) {
			if (other.facebookToken != null)
				return false;
		} else if (!facebookToken.equals(other.facebookToken))
			return false;
		if (facebookTokenValidity == null) {
			if (other.facebookTokenValidity != null)
				return false;
		} else if (!facebookTokenValidity.equals(other.facebookTokenValidity))
			return false;
		if (financialEntities == null) {
			if (other.financialEntities != null)
				return false;
		} else if (!financialEntities.equals(other.financialEntities))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (recoveryToken == null) {
			if (other.recoveryToken != null)
				return false;
		} else if (!recoveryToken.equals(other.recoveryToken))
			return false;
		if (recoveryTokenValidity == null) {
			if (other.recoveryTokenValidity != null)
				return false;
		} else if (!recoveryTokenValidity.equals(other.recoveryTokenValidity))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (shoppings == null) {
			if (other.shoppings != null)
				return false;
		} else if (!shoppings.equals(other.shoppings))
			return false;
		if (status != other.status)
			return false;
		if (tableManager == null) {
			if (other.tableManager != null)
				return false;
		} else if (!tableManager.equals(other.tableManager))
			return false;
		if (userManager == null) {
			if (other.userManager != null)
				return false;
		} else if (!userManager.equals(other.userManager))
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserSecurity [authToken=" + authToken + ", authTokenValidity="
				+ authTokenValidity + ", status=" + status + "]";
	}

}
