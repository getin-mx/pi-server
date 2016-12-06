package mobi.allshoppings.bz;


import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

/**
 * User Service Class
 */
public interface FavoritesBzService extends BzService {
    @Post("json")
    @Put("json")
    public String add(JsonRepresentation entity);
    
    @Delete
    public String remove();

}
