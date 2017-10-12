package mobi.allshoppings.bdb.bz;

import org.restlet.resource.Get;

/**
 * Dashboard Timeline Data Service Class
 */
public interface BDBTimelineDataBzService extends BDBBzService {
    @Get
    public String retrieve();
}
