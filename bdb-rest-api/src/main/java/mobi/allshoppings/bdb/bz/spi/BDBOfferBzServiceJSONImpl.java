package mobi.allshoppings.bdb.bz.spi;

import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.dao.OfferDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Offer;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class BDBOfferBzServiceJSONImpl extends BDBCrudByCountryBzServiceJSONImpl<Offer> implements BDBCrudBzService {

	@Autowired
	private OfferDAO dao;

	@Override
	public String[] getListFields() {
		return new String[] {
				"identifier",
				"name",
				"avatarId",
				"status",
				"country",
				"offerTypeName"
		};
	}

	@Override
	public void config() {
		setMyDao(dao);
		setMyClazz(Offer.class);
	}

	@Override
	public void setKey(Offer obj, JSONObject seed) throws ASException {
		obj.setKey(dao.createKey());
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
