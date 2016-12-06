package mobi.allshoppings.bz;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Post;

/**
 * This is a dummy service made as a place holder for certain old API URLs
 */
public interface DummyBzService extends BzService {
	@Post("json")
    public String post(JsonRepresentation entity);
}
