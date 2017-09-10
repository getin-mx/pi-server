package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.CampaignActionDAO;
import mobi.allshoppings.dao.OfferTypeDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.CampaignAction;
import mobi.allshoppings.model.OfferType;
import mobi.allshoppings.model.tools.IndexHelper;


public class CouponsDump extends AbstractCLI {

	private static final Logger log = Logger.getLogger(CouponsDump.class.getName());

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
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			CampaignActionDAO csDao = (CampaignActionDAO)getApplicationContext().getBean("campaignaction.dao.ref");
			BrandDAO brandDao = (BrandDAO)getApplicationContext().getBean("brand.dao.ref");
			OfferTypeDAO otDao = (OfferTypeDAO)getApplicationContext().getBean("offertype.dao.ref");
			IndexHelper indexHelper = (IndexHelper)getApplicationContext().getBean("index.helper");

			log.log(Level.INFO, "Dumping Coupons Data....");
			
			Brand b = brandDao.get("chilimbalam_mx");
			OfferType ot = otDao.get("1384133723158");
			List<CampaignAction> list = csDao.getUsingAppAndBrandAndStatusAndRange("amazing_mx", "chilimbalam_mx", null, null, null, true);
			CampaignAction cs = null;
			boolean forUpdate = false;
			if( CollectionUtils.isEmpty(list)) {
				cs = new CampaignAction();
				cs.setKey(csDao.createKey());
			} else {
				cs = list.get(0);
				forUpdate = true;
			}
			
			cs.setAgeFrom(0);
			cs.setAgeTo(99);
			cs.addAppId("amazing_mx");
			cs.setCountry("Mexico");
			cs.addBrand(b);
			cs.setOfferType(ot);
			cs.setCustomUrl("/main-be/custom/promotions/cinepolis_mx/promos/{identifier}");
			cs.setValidFrom(sdf.parse("2016-12-01"));
			cs.setValidTo(sdf.parse("2017-12-01"));
			cs.setAvatarId("chilimbalam_test.png");
			cs.setTimezone(-6F);
			cs.setSpan(7200000L);
			cs.setQuantity(0L);
			cs.setPromotionType("Alimentos");
			cs.setNotifyFromHour("11:00");
			cs.setNotifyToHour("20:00");
			cs.setNotifyDays(Arrays.asList(new String[] {"0","1","2","3","4","5","6"}));
			cs.setName("20% En Chilim Balam");
			cs.setGenders(Arrays.asList(new String[] {"male","female"}));
			cs.setExpired(false);
			cs.setDescription("20% en toda la tienda");
			cs.setInstructions("Valido unicamente para canje dentro de la tienda");
			cs.setProduct("Toda la Tienda");
			
			if(forUpdate)
				csDao.update(cs);
			else
				csDao.create(cs);
			
			indexHelper.indexObject(cs);

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
