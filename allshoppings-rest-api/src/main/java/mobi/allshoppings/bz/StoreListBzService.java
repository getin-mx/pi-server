package mobi.allshoppings.bz;

import org.restlet.resource.Get;

public interface StoreListBzService extends BzService {
	@Get
	public String retrieve();
}
