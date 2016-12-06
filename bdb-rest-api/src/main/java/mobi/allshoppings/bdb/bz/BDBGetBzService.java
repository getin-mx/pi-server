package mobi.allshoppings.bdb.bz;

import org.restlet.resource.Get;

/**
 * User Service Class
 */
public interface BDBGetBzService extends BDBBzService {
    @Get
    public String retrieve();
}
