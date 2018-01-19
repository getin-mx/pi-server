package mobi.allshoppings.bdb.bz.spi;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Brand;

public class BDBBrandBzServiceJSONImpl extends BDBCrudByCountryBzServiceJSONImpl<Brand> implements BDBCrudBzService {

	@Autowired
	private BrandDAO dao;

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
		setMyClazz(Brand.class);
	}

	@Override
	public void setKey(Brand obj, JSONObject seed) throws ASException {
		obj.setKey(dao.createKey(seed.getString("name"), seed.getString("country")));
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
