package mobi.allshoppings.auth;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.dao.TFTokenDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.TFToken;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity;
import mobi.allshoppings.tools.CollectionFactory;

public class AuthHelper {
	private static final char HEXES[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	private static final int DEFAULT_PASSWORD_LEN = 6;
	private static final String ADMIN_IDENTIFIER = "admin";

	private static final Logger log = Logger.getLogger(AuthHelper.class.getName());
	
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private TFTokenDAO tfTokenDao;
	
	private final List<String> validReferersList = CollectionFactory.createList();

	public SystemConfiguration getSystemConfiguration() {
		return this.systemConfiguration;
	}

	public void setSystemConfiguration(SystemConfiguration systemConfiguration) {
		this.systemConfiguration = systemConfiguration;
	}
	
	/**
	 * Encodes (Hashes) a String using Hmac SHA
	 * 
	 * @param input
	 *            The string to encode
	 * @return the Encoded (Hashed) String
	 */
	private String encodeString(String input) {
		 String output = "";
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			byte keyBytes[] = "MSIR33L264H1VVXSINHR".getBytes(); 
			SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
			mac.init(key);
			mac.update(input.getBytes());
			byte macBytes[] = mac.doFinal();

			StringBuilder hexString = new StringBuilder(macBytes.length * 2);
			for (byte b : macBytes) {
				hexString.append(HEXES[((b & 0xF0) >> 4)]).append(HEXES[(b & 0x0F)]);
			}
			output = hexString.toString();
		} catch (Exception e) {
		}
		return output;
	}
	
	/**
	 * Creates a new token for a user
	 * 
	 * @param user
	 *            The user on which the token will be created
	 * @return The string representation of the auth token
	 */
	public String generateTokenForUser(User user) {
		Calendar calendar = Calendar.getInstance();	
		Date now = calendar.getTime();
		String input = user.getIdentifier() + "-" + now.getTime(); 
		String token = this.encodeString(input);
		
		int days = user.getSecuritySettings().getRole().equals(UserSecurity.Role.APPLICATION) 
				? this.getSystemConfiguration().getAppTokenValidityInDays() 
				: this.getSystemConfiguration().getAuthTokenValidityInDays();
		if (days > 0) {
			calendar.add(Calendar.DAY_OF_MONTH, days);
			Date validity = calendar.getTime();
			user.getSecuritySettings().setAuthTokenValidity(validity);
		}
		user.getSecuritySettings().setAuthToken(token);
		user.setLastLogin(new Date());
		user.setActivityStatus(User.STATUS_ACTIVE);
		
		return token;
	}

	/**
	 * Creates a new temporary token for a user This token is only valid for two
	 * factor authentication, such as mac address
	 * 
	 * @param user
	 *            The user on which the token will be created
	 * @return The corresponding Two Factor Token object
	 */
	public TFToken generateTFTokenForUser(User user) {

		Calendar calendar = Calendar.getInstance();	
		Date now = calendar.getTime();

		TFToken tfToken = null;
		try {
			tfToken = tfTokenDao.getLastUsingUser(user.getIdentifier());
			if (tfToken.getStatus().equals(TFToken.STATUS_USED)
					|| now.after(tfToken.getTokenValidity()))
				throw ASExceptionHelper.invalidArgumentsException();
		} catch( ASException e ) {
			tfToken = new TFToken();
			String input = user.getIdentifier() + "-" + now.getTime(); 
			String token = this.encodeString(input);
			
			int days = this.getSystemConfiguration().getAuthTokenValidityInDays();
			if (days > 0) {
				calendar.add(Calendar.DAY_OF_MONTH, days);
				Date validity = calendar.getTime();
				tfToken.setTokenValidity(validity);
			}
			try {
				tfToken.setKey(tfTokenDao.createKey(token));
				tfToken.setUserId(user.getIdentifier());
			} catch(ASException e1) {
				log.log(Level.SEVERE, e1.getMessage(), e1);
				return null;
			}
		}
		
		return tfToken;
	}

	/**
	 * Assigns a temporal alternative password for a user. <br/>
	 * This procedure is used in password recovery
	 * 
	 * @param user
	 *            The user to affect
	 * @return a String with the new alternative password decoded
	 */
	public String generateAltPasswordForUser(User user) {
		String newPassword = generateRandomPassword();
		
		int days = this.getSystemConfiguration().getAuthTokenValidityInDays();
		if (days > 0) {
			Calendar calendar = Calendar.getInstance();	
			calendar.add(Calendar.DAY_OF_MONTH, days);
			Date validity = calendar.getTime();
			user.getSecuritySettings().setRecoveryTokenValidity(validity);
		}
		user.getSecuritySettings().setAltPassword(encodePassword(newPassword));
		
		return newPassword;
	}

	/**
	 * Apply alternative password as definitive after a user login
	 * 
	 * @param user
	 *            The user to affect
	 */
	public void applyAltPasswordAfterLogin(User user) {
		user.getSecuritySettings().setPassword(user.getSecuritySettings().getAltPassword());
		user.getSecuritySettings().setAltPassword(null);
		user.getSecuritySettings().setRecoveryToken(null);
		user.getSecuritySettings().setRecoveryTokenValidity(null);
	}
	
	/**
	 * Creates a new recovery token for a user
	 * 
	 * @param user
	 *            The user to which the recovery token will be created
	 * @return The string representation of the recovery token
	 */
	public String generateRecoveryTokenForUser(User user) {
		Calendar calendar = Calendar.getInstance();	
		Date now = calendar.getTime();
		String input = user.getIdentifier() + "-" + now.getTime(); 
		String token = this.encodeString(input);
		
		int days = this.getSystemConfiguration().getAuthTokenValidityInDays();
		if (days > 0) {
			calendar.add(Calendar.DAY_OF_MONTH, days);
			Date validity = calendar.getTime();
			user.getSecuritySettings().setRecoveryTokenValidity(validity);
		}
		user.getSecuritySettings().setRecoveryToken(token);
		
		return token;
	}

	/**
	 * Invalidates a password recovery token for a given user
	 * 
	 * @param user
	 *            The user to invalidate the recovery token
	 */
	public void invalidateRecoveryTokenForUser(User user) {
		user.getSecuritySettings().setRecoveryToken(null);
		user.getSecuritySettings().setRecoveryTokenValidity(null);
	}
	
	public boolean validateIfUserCanAccessOtherUser(User caller, String identifier) {
		if (caller.getIdentifier().equalsIgnoreCase(identifier)) {
			return true;
		}
		
		if (caller.getIdentifier().equalsIgnoreCase(ADMIN_IDENTIFIER)) {
			return true;
		}
		
		return false;
	}

	/**
	 * Generates a random password
	 * 
	 * @return A random generated password
	 */
	public String generateRandomPassword(){
		String password = RandomStringUtils.randomAlphanumeric(DEFAULT_PASSWORD_LEN).toLowerCase();
		return password;
	}

	/**
	 * Encodes a password using encodeString <br>
	 * 
	 * @see AuthHelper#encodeString(String)
	 * @param password
	 *            The password to encode
	 * @return A hashed form of the input password
	 */
	public String encodePassword(String password){
		return this.encodeString(password);
	}

	public List<String> getValidReferersList() {
		return validReferersList;
	}

	public void setValidReferersList(List<String> validReferersList) {
		this.validReferersList.clear();
		this.validReferersList.addAll(validReferersList);
	}
}
