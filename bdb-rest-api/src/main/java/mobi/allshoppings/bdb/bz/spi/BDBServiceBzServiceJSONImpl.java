package mobi.allshoppings.bdb.bz.spi;

import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.dao.ServiceDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class BDBServiceBzServiceJSONImpl extends BDBCrudBzServiceJSONImpl<Service> implements BDBCrudBzService {

	@Autowired
	private ServiceDAO dao;

	@Override
	public void config() {
		setMyDao(dao);
		setMyClazz(Service.class);
	}

	@Override
	public void setKey(Service obj, JSONObject seed) throws ASException {
		obj.setKey(dao.createKey());
	}

}
