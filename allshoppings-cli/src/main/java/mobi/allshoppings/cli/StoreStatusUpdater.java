package mobi.allshoppings.cli;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import com.inodes.util.CollectionFactory;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.interfaces.StatusAware;


public class StoreStatusUpdater extends AbstractCLI {

	private static final Logger log = Logger.getLogger(StoreStatusUpdater.class.getName());

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
			ShoppingDAO shoppingDao = (ShoppingDAO)getApplicationContext().getBean("shopping.dao.ref");
			BrandDAO brandDao = (BrandDAO)getApplicationContext().getBean("brand.dao.ref");
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");

			log.log(Level.INFO, "Updating store status....");

			List<String> shoppingIds = CollectionFactory.createList();
			List<String> brandIds = CollectionFactory.createList();
			
			List<Store> list = storeDao.getAll();
			for( Store obj : list ) {
				log.log(Level.INFO, "Checking " + obj.getName() + "...");
				if( StringUtils.hasText(obj.getExternalId())) {
					if(StringUtils.hasText(obj.getShoppingId()) && !shoppingIds.contains(obj.getShoppingId()))
						shoppingIds.add(obj.getShoppingId());
					if(StringUtils.hasText(obj.getBrandId()) && !brandIds.contains(obj.getBrandId()))
						brandIds.add(obj.getBrandId());
				} else {
					if( obj.getStatus() == StatusAware.STATUS_ENABLED ) {
						log.log(Level.INFO, "Disabling store " + obj);
						obj.setStatus(StatusAware.STATUS_DISABLED);
						storeDao.update(obj);
					}
				}
			}

			List<Shopping> ls = shoppingDao.getAll();
			for( Shopping obj : ls ) {
				log.log(Level.INFO, "Checking " + obj.getName() + "...");
				if( !shoppingIds.contains(obj.getIdentifier()) && obj.getStatus() == StatusAware.STATUS_ENABLED) {
					log.log(Level.INFO, "Disabling shopping " + obj);
					obj.setStatus(StatusAware.STATUS_DISABLED);
					shoppingDao.update(obj);
				}
			}

			List<Brand> lb = brandDao.getAll();
			for( Brand obj : lb ) {
				log.log(Level.INFO, "Checking " + obj.getName() + "...");
				if( !brandIds.contains(obj.getIdentifier()) && obj.getStatus() == StatusAware.STATUS_ENABLED) {
					log.log(Level.INFO, "Disabling brand " + obj);
					obj.setStatus(StatusAware.STATUS_DISABLED);
					brandDao.update(obj);
				}
			}
						
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
