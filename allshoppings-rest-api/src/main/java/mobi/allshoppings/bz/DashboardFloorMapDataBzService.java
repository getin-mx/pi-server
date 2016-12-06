package mobi.allshoppings.bz;

import org.restlet.resource.Get;

/**
 * Dashboard Timeline Data Service Class
 */
public interface DashboardFloorMapDataBzService extends BzService {
    @Get
    public String retrieve();
}
