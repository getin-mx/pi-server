package mobi.allshoppings.bdb.bz;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Post;

/**
 * User Service Class
 */
public interface BDBPostBzService extends BDBBzService {

    @Post("json")
    public String change(JsonRepresentation entity);
    
}
