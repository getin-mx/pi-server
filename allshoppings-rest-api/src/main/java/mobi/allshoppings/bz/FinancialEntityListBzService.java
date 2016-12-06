package mobi.allshoppings.bz;

import org.restlet.resource.Get;

public interface FinancialEntityListBzService extends BzService {
	@Get
	public String retrieve();
}
