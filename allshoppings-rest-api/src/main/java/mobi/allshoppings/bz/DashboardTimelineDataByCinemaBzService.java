package mobi.allshoppings.bz;

import org.restlet.resource.Get;

/**
 * Dashboard Timeline Data Service Class
 */
public interface DashboardTimelineDataByCinemaBzService extends BzService {
    @Get
    public String retrieve();
}
