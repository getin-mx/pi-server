package mobi.allshoppings.bz;

import org.restlet.resource.Get;

public interface SystemConfigurationBzService extends BzService {
	@Get
	public String retrieve();
}
