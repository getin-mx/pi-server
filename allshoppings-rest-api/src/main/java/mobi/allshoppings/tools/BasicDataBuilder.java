package mobi.allshoppings.tools;

import java.util.logging.Logger;

import mobi.allshoppings.auth.AuthHelper;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.dao.spi.UserDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;

public class BasicDataBuilder {

	private static final Logger log = Logger.getLogger(BasicDataBuilder.class.getName());
	
	/**
	 * User DAO to use. By now, it is fixed to UserDAOJDOImpl
	 */
	private UserDAO userDao = new UserDAOJDOImpl();

	/**
	 * Key Helper to use. By now, it is fixed to KeyHelperGaeImpl
	 */
    private KeyHelper keyHelper = new KeyHelperGaeImpl();

	/**
	 * Auth Helper to use. By now, it is fixed to AuthHelper
	 */
	private AuthHelper authHelper = new AuthHelper();

    /**
     * Builds all the basic data needed to run the app
     * @throws ASException
     */
	public static void buildBasicData() throws ASException {
		BasicDataBuilder bdb = new BasicDataBuilder();
		bdb.buildDefaultUsers();
	}
	
	/**
	 * Does a simple get as a warm up request
	 * @throws ASException
	 */
	public static void warmUp() throws ASException {
		long time = System.currentTimeMillis();
		BasicDataBuilder bdb = new BasicDataBuilder();
		bdb.userDao.get("admin", true);
		time = System.currentTimeMillis() -time;
		log.info("warmup in " + time + " ms.");
	}
	
	/**
	 * Builds a default new user, in case of blank database
	 * @throws ASException
	 */
	public void buildDefaultUsers() throws ASException {
		User user;
		try {
			user = userDao.get("admin", false);
		} catch( ASException e ) {
			user = new User();
			String password = authHelper.encodePassword("admin");
			keyHelper.setKeyWithIdentifier(user, "admin");
			user.getSecuritySettings().setPassword(password);
			user.setFirstname("admin");
			user.setLastname("");
			userDao.create(user);
		}
	}
}
