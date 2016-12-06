package mobi.allshoppings.bz;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public interface WifiSpotListBzService extends BzService {
	@Get
	public String retrieve();
	
    @Post("json")
    public String update(JsonRepresentation entity);

}
