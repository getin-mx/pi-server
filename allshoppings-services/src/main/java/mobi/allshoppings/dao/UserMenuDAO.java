package mobi.allshoppings.dao;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.UserMenu;

public interface UserMenuDAO extends GenericDAO<UserMenu> {

	Key createKey(String seed) throws ASException;
	UserMenu getByRole(Integer role, Boolean detachable) throws ASException;

}
