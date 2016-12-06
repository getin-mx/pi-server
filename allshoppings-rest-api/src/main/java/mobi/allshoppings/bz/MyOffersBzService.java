package mobi.allshoppings.bz;

import org.restlet.resource.Get;

public interface MyOffersBzService extends BzService {
	@Get
	public String retrieve();
}
