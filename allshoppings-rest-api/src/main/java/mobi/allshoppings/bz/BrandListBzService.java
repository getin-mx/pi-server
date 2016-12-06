package mobi.allshoppings.bz;

import org.restlet.resource.Get;

public interface BrandListBzService extends BzService {
	@Get
	public String retrieve();
}
