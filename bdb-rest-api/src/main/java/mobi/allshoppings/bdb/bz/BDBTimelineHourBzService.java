package mobi.allshoppings.bdb.bz;

import org.restlet.resource.Get;

/**
 * Dashboard Timeline Data Service Class
 */
public interface BDBTimelineHourBzService extends BDBBzService {
    @Get
    public String retrieve();
}
