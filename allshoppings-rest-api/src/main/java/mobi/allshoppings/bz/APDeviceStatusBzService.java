package mobi.allshoppings.bz;


import org.restlet.resource.Get;

/**
 * Shopping Service Class
 */
public interface APDeviceStatusBzService extends BzService {
    @Get
    public String retrieve();
}
