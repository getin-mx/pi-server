package mobi.allshoppings.bdb.bz.spi;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.dao.ProcessDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Process;

public class BDBProcessBzServiceJSONImpl extends BDBCrudBzServiceJSONImpl<Process> implements BDBCrudBzService {

	@Autowired
	private ProcessDAO dao;
	
	@Override
	public String[] getListFields() {
		return new String[] {
				"identifier",
				"name",
				"status",
				"userId",
				"startDateTime",
				"endDateTime"
		};
	}

	@Override
	public void config() {
		setMyDao(dao);
		setMyClazz(Process.class);
	}

	@Override
	public void setKey(Process obj, JSONObject seed) throws ASException {
		obj.setKey(dao.createKey());
	}
	
	@Override
	public void prePersist(Process obj, JSONObject seed) throws ASException {
	}
	
}
