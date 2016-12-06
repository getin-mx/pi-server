package mobi.allshoppings.bdb.auth;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public interface BDBAuthBzService {
	
	@Post("json")
    public String login(JsonRepresentation entity);
    
    @Get
    public String validate();
    
    @Delete
    public String logout();
    
}
