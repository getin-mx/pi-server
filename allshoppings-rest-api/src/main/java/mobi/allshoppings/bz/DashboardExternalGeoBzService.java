package mobi.allshoppings.bz;

import org.restlet.resource.Get;

/**
 * Dashboard Timeline Data Service Class
 */
public interface DashboardExternalGeoBzService extends BzService {
    @Get
    public String retrieve();
}
