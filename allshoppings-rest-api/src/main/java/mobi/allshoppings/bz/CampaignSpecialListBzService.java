package mobi.allshoppings.bz;

import org.restlet.resource.Get;

public interface CampaignSpecialListBzService extends BzService {
	@Get
	public String retrieve();
}
