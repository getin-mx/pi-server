package mobi.allshoppings.bdb.bz;

import org.restlet.resource.Get;

/**
 * User Service Class
 */
public interface BDBListBzService extends BDBBzService {
    @Get
    public String list();
}
