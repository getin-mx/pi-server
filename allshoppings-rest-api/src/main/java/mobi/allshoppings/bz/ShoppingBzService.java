package mobi.allshoppings.bz;


import org.restlet.resource.Get;

/**
 * Shopping Service Class
 */
public interface ShoppingBzService extends BzService {
    @Get
    public String retrieve();
}
