package mobi.allshoppings.bdb.bz.spi;

import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;

import org.restlet.ext.json.JsonRepresentation;

/**
 * This is a dummy service made as a place holder for certain old API URLs
 */
public class BDBDummyBzServiceJSONImpl extends BDBRestBaseServerResource implements BDBCrudBzService {

	@Override
	public String selectRetrieveOrList() {
		return generateJSONOkResponse().toString();
	}

	@Override
	public String add(JsonRepresentation entity) {
		return generateJSONOkResponse().toString();
	}

	@Override
	public String change(JsonRepresentation entity) {
		return generateJSONOkResponse().toString();
	}

	@Override
	public String delete(JsonRepresentation entity) {
		return generateJSONOkResponse().toString();
	}
}
