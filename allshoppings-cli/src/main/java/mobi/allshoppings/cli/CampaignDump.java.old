package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.inodes.datanucleus.model.Key;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.CampaignDAO;
import mobi.allshoppings.dao.ExternalAPHotspotDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.Campaign;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.model.tools.StatusHelper;


public class CampaignDump extends AbstractCLI {

	private static final Logger log = Logger.getLogger(CampaignDump.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws ASException {
		try {
			CampaignDAO campaignDao = (CampaignDAO)getApplicationContext().getBean("campaign.dao.ref");
			ShoppingDAO shoppingDao = (ShoppingDAO)getApplicationContext().getBean("shopping.dao.ref");
			BrandDAO brandDao = (BrandDAO)getApplicationContext().getBean("brand.dao.ref");
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");
			KeyHelper keyHelper = (KeyHelper)getApplicationContext().getBean("key.helper");
			ExternalAPHotspotDAO eaphDao = (ExternalAPHotspotDAO)getApplicationContext().getBean("externalaphotspot.dao.ref");
			APDAssignationDAO apdaDao = (APDAssignationDAO)getApplicationContext().getBean("apdassignation.dao.ref");
			
			final String BRAND_ID = "nutribaby_mx";
			
			// Checks for the Brand
			Brand b = null;
			try {
				b = brandDao.get(BRAND_ID, true);
			} catch( Exception e ) {
				b = new Brand();
				b.setKey((Key)keyHelper.createStringUniqueKey(Brand.class, BRAND_ID));
				b.setName("NutriBaby");
				b.setDescription("NutriBaby");
				b.setStatus(StatusAware.STATUS_ENABLED);
				brandDao.create(b);
			}

			// Checks for the Campaign
			Campaign c;
			List<Campaign> campaigns = campaignDao.getAll(true);
			if( campaigns.isEmpty()) {
				c = new Campaign();
				c.setEntityId(BRAND_ID);
				c.setEntityKind(EntityKind.KIND_BRAND);
				c.setName("Campaña NutriBaby");
				c.setDescription("Campaña NutriBaby México");
				c.setStatus(StatusAware.STATUS_ENABLED);
				c.setKey(campaignDao.createKey());
				campaignDao.create(c);
			} else {
				c = campaigns.get(0);
			}

			

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
