package mobi.allshoppings.bz;

import org.restlet.resource.Get;

public interface OfferListBzService extends BzService {
	@Get
	public String retrieve();
}
