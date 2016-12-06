package mobi.allshoppings.bz;


import org.restlet.resource.Get;

/**
 * Shopping Service Class
 */
public interface OfferBzService extends BzService {
    @Get
    public String retrieve();
}
