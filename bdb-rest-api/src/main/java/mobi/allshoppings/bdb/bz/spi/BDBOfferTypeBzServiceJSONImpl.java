package mobi.allshoppings.bdb.bz.spi;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.dao.OfferTypeDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.OfferType;

public class BDBOfferTypeBzServiceJSONImpl extends BDBCrudBzServiceJSONImpl<OfferType> implements BDBCrudBzService {

	@Autowired
	private OfferTypeDAO dao;

	@Override
	public String[] getListFields() {
		return new String[] {
				"identifier",
				"avatarId",
				"name",
				"ribbonText"
		};
	}

	@Override
	public void config() {
		setMyDao(dao);
		setMyClazz(OfferType.class);
	}

	@Override
	public void setKey(OfferType obj, JSONObject seed) throws ASException {
		obj.setKey(dao.createKey());
	}

}
