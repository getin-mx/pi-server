package mobi.allshoppings.bdb.bz.spi;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;

public class BDBStoreBzServiceJSONImpl extends BDBCrudBzServiceJSONImpl<Store> implements BDBCrudBzService {

	@Autowired
	private StoreDAO dao;
	
	@Autowired
	private ShoppingDAO sDao;
	
	@Autowired
	private BrandDAO bDao;

	@Override
	public String[] getListFields() {
		return new String[] {
				"identifier",
				"name",
				"avatarId",
				"status",
				"country",
				"brandName",
				"shoppingName"
		};
	}

	@Override
	public void config() {
		setMyDao(dao);
		setMyClazz(Store.class);
	}

	@Override
	public void setKey(Store obj, JSONObject seed) throws ASException {
		obj.setKey(dao.createKey());
	}
	
	@Override
	public void prePersist(Store obj, JSONObject seed) throws ASException {
		if( StringUtils.hasText(obj.getShoppingId())) {
			Shopping s = sDao.get(obj.getShoppingId(), true);
			obj.setShoppingName(s.getName());
		} else {
			obj.setShoppingName("---");
		}
		
		if( StringUtils.hasText(obj.getBrandId())) {
			Brand b = bDao.get(obj.getBrandId(), true);
			obj.setBrandName(b.getName());
		} else {
			obj.setBrandName("---");
		}
	}
	
}
