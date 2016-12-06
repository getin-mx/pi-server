package mobi.allshoppings.bz;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

public interface CouponBzService extends BzService {
    @Get
    public String retrieve();

    @Post("json")
    public String post(JsonRepresentation entity);

	@Put("json")
    public String put(JsonRepresentation entity);

	@Delete("json")
    public String delete(JsonRepresentation entity);
}
