package mobi.allshoppings.bz;

import org.restlet.resource.Get;

public interface CouponListBzService extends BzService {
	@Get
	public String retrieve();
}
