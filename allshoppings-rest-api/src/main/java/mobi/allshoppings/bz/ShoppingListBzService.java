package mobi.allshoppings.bz;

import org.restlet.resource.Get;

public interface ShoppingListBzService extends BzService {
	@Get
	public String retrieve();
}
