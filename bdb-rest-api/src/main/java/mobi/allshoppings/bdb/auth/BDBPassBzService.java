package mobi.allshoppings.bdb.auth;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

public interface BDBPassBzService {

    @Post("json")
    public String change(JsonRepresentation entity);

    @Put("json")
    public String force(JsonRepresentation entity);

    @Get
    public String recover();
    

}
