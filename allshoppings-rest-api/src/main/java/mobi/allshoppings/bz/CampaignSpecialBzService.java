package mobi.allshoppings.bz;


import org.restlet.resource.Get;

/**
 * Shopping Service Class
 */
public interface CampaignSpecialBzService extends BzService {
    @Get
    public String retrieve();
}
