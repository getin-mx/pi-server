package mobi.allshoppings.bdb.bz.spi;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.dao.UserMenuDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.UserMenu;

public class BDBUserMenuBzServiceJSONImpl extends BDBCrudBzServiceJSONImpl<UserMenu> implements BDBCrudBzService {

	@Autowired
	private UserMenuDAO dao;

	@Override
	public String[] getListFields() {
		return new String[] {
				"identifier",
				"entries"
		};
	}

	@Override
	public void config() {
		setMyDao(dao);
		setMyClazz(UserMenu.class);
	}

	@Override
	public void setKey(UserMenu obj, JSONObject seed) throws ASException {
		obj.setKey(dao.createKey(seed.getString("menuName")));
	}

}
