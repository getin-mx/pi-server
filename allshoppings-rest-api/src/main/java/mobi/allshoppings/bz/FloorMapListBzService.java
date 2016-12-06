package mobi.allshoppings.bz;

import org.restlet.resource.Get;

public interface FloorMapListBzService extends BzService {
	@Get
	public String retrieve();
}
