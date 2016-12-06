package mobi.allshoppings.bz;


import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

/**
 * Shopping Service Class
 */
public interface APDeviceSignalBzService extends BzService {
    @Get
    public String retrieve();
    
    @Put("json")
    public String put(JsonRepresentation entity);
}
