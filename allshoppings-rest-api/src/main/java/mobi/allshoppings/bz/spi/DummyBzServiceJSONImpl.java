package mobi.allshoppings.bz.spi;

import org.restlet.ext.json.JsonRepresentation;

import mobi.allshoppings.bz.DummyBzService;
import mobi.allshoppings.bz.RestBaseServerResource;

/**
 * This is a dummy service made as a place holder for certain old API URLs
 */
public class DummyBzServiceJSONImpl extends RestBaseServerResource implements DummyBzService {
	@Override
	public String post(final JsonRepresentation entity) {
		return generateJSONOkResponse().toString();
	}
}
