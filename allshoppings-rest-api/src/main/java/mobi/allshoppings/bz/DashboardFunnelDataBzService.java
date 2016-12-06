package mobi.allshoppings.bz;

import org.restlet.resource.Get;

/**
 * Dashboard Timeline Data Service Class
 */
public interface DashboardFunnelDataBzService extends BzService {
    @Get
    public String retrieve();
}
