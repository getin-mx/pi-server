package mobi.allshoppings.bz;


import org.restlet.resource.Get;

/**
 * Shopping Service Class
 */
public interface BrandBzService extends BzService {
    @Get
    public String retrieve();
}
