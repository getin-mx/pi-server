package mobi.allshoppings.bz;


import org.restlet.resource.Get;

/**
 * Shopping Service Class
 */
public interface FinancialEntityBzService extends BzService {
    @Get
    public String retrieve();
}
