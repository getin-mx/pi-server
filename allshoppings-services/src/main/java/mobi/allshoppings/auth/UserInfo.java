package mobi.allshoppings.auth;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public class UserInfo implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -7237820795468195085L;
	String userName;
    String userId;
    Integer role;
    List<String> availableCountries;
    List<String> shoppings;
    List<String> brands;
    List<String> financialEntities;
    boolean loggedIn;
    boolean userManager;
    boolean tableManager;
    Map<String, Object> sessionParameters;

    public UserInfo() {
        loggedIn = false;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
    
	public boolean isLoggedIn() {
        return loggedIn;
    }
	
	public Integer getRole() {
		return role;
	}
	
	public List<String> getAvailableCountries() {
		return availableCountries;
	}

	public List<String> getShoppings() {
		return shoppings;
	}

	public List<String> getBrands() {
		return brands;
	}

	public List<String> getFinancialEntities() {
		return financialEntities;
	}
	
	public boolean isUserManager() {
		return userManager;
	}

	public boolean isTableManager() {
		return tableManager;
	}

	public Map<String, Object> getSessionParameters() {
		return sessionParameters;
	}
	
}
