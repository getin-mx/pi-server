package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.UserMenuDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.UserMenu;

public class UserMenuDAOJDOImpl extends GenericDAOJDO<UserMenu> implements UserMenuDAO {
	private static final Logger log = Logger.getLogger(UserMenuDAOJDOImpl.class.getName());

	public UserMenuDAOJDOImpl() {
		super(UserMenu.class);
	}

	@Override
	public Key createKey(String seed) throws ASException {
		return (Key)keyHelper.obtainKey(UserMenu.class, seed);
	}
	
	/**
	 * Gets a menu using an user role
	 * 
	 * @param identifier
	 *            The entity Identifier
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public UserMenu getByRole(Integer role, Boolean detachable) throws ASException {

		if (role == null || role.equals("null")) {
			log.info("not accepted:id null");
			throw ASExceptionHelper.notAcceptedException();
		}

		String identifier = "role-" + role;

		return get(identifier, detachable);
	}

}
