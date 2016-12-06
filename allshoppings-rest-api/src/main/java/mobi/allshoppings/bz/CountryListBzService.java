package mobi.allshoppings.bz;


import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * Country list Service Class
 */
public interface CountryListBzService extends BzService {
	@Post("json") 
	@Get("json")
    public String retrieve(final JsonRepresentation entity);
}
