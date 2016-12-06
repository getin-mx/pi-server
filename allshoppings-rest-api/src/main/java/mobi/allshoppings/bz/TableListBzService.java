package mobi.allshoppings.bz;

import org.restlet.resource.Get;

public interface TableListBzService extends BzService {
	@Get
	public String retrieve();
}
