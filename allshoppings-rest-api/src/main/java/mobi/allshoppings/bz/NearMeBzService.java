package mobi.allshoppings.bz;

import org.restlet.resource.Get;

public interface NearMeBzService extends BzService {
	@Get
	public String retrieve();
}
