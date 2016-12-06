package mobi.allshoppings.bz;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Post;

public interface DeviceInfoBzService extends BzService {
	@Post("json")
    public String post(JsonRepresentation entity);
}
