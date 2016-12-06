package mobi.allshoppings.bz;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

/**
 * User Service Class
 */
public interface UserBzService extends BzService {
    @Get
    public String retrieve();
    
    @Post("json")
    public String add(JsonRepresentation entity);
    
    @Put("json")
    public String put(JsonRepresentation entity);

}
