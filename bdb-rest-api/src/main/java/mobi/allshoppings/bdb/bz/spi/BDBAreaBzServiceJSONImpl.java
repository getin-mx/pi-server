package mobi.allshoppings.bdb.bz.spi;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.dao.AreaDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Area;

public class BDBAreaBzServiceJSONImpl extends BDBCrudBzServiceJSONImpl<Area> implements BDBCrudBzService {

	@Autowired
	private AreaDAO dao;

	@Override
	public void config() {
		setMyDao(dao);
		setMyClazz(Area.class);
	}

	@Override
	public void setKey(Area obj, JSONObject seed) throws ASException {
		obj.setKey(dao.createKey());
	}

}
