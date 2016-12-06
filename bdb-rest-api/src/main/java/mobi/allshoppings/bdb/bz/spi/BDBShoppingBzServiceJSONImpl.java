package mobi.allshoppings.bdb.bz.spi;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Shopping;

public class BDBShoppingBzServiceJSONImpl extends BDBCrudByCountryBzServiceJSONImpl<Shopping> implements BDBCrudBzService {

	@Autowired
	private ShoppingDAO dao;

	@Override
	public String[] getListFields() {
		return new String[] {
				"identifier",
				"name",
				"avatarId",
				"status",
				"country"
		};
	}

	@Override
	public void config() {
		setMyDao(dao);
		setMyClazz(Shopping.class);
	}

	@Override
	public void setKey(Shopping obj, JSONObject seed) throws ASException {
		obj.setKey(dao.createKey(seed.getString("name")));
	}

	@Override
	public String sanitizeOrder(String country, String order, Map<String, String> additionalFields) {
		if(StringUtils.hasText(order)) {
			if( order.startsWith("uName")) order = order.substring(1);
			if( order.startsWith("uCountry")) order = order.substring(1);
		}

		if( additionalFields == null ) return order;
		
		if(StringUtils.hasText(country))
			additionalFields.put("country", country);
		
		return order;
	}

}
