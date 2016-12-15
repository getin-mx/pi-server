package mobi.allshoppings.cli;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.CampaignSpecialDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.CampaignSpecial;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.CollectionFactory;


public class TouchCampaignSpecials extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TouchCampaignSpecials.class.getName());

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
			CampaignSpecialDAO dao = (CampaignSpecialDAO)getApplicationContext().getBean("campaignspecial.dao.ref");

			log.log(Level.INFO, "Touching Campaign Specials....");
			List<CampaignSpecial> list = dao.getAll(true);
			for( CampaignSpecial obj : list ) {
				log.log(Level.INFO, "Touching " + obj.getIdentifier() + "...");
				if( obj.getBrands() == null || obj.getBrands().size() == 0 )
					obj.addBrand("cinepolis_mx");
				if( obj.getAppIds() == null || obj.getAppIds().size() == 0 ) {
					obj.addAppId("cinepolis_mx");
					obj.addAppId("amazing_mx");
				}
				if( obj.getIdentifier().equals("1430288511084"))
					obj.setProduct("Boleto de Cine");
				if( obj.getIdentifier().equals("1432724594627"))
					obj.setProduct("Crepa de dos ingredientes");
				if( obj.getIdentifier().equals("1432724531038"))
					obj.setProduct("Bagui clasico o de pavo");
				dao.update(obj);
				
			}
			
			list = dao.getAll();
			List<ModelKey> index = CollectionFactory.createList();
			for( CampaignSpecial obj : list ) {
				index.add(obj);
			}
			dao.getIndexHelper().indexObject(index);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
