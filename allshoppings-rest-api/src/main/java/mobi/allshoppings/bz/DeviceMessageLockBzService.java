package mobi.allshoppings.bz;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public interface DeviceMessageLockBzService extends BzService {
	@Post("json")
    public String post(JsonRepresentation entity);

	@Get
    public String retrieve();
}
