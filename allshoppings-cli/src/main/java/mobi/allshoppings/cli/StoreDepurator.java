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


public class StoreDepurator extends AbstractCLI {

	private static final Logger log = Logger.getLogger(StoreDepurator.class.getName());

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

			log.log(Level.INFO, "Depurating stores....");

			List<String> shoppingIds = CollectionFactory.createList();
			List<Shopping> shoppings = shoppingDao.getAll();
			for( Shopping o : shoppings )
				shoppingIds.add(o.getIdentifier());
			
			List<String> brandIds = CollectionFactory.createList();
			List<Brand> brands = brandDao.getAll();
			for( Brand o : brands )
				brandIds.add(o.getIdentifier());
			
			
			List<Store> list = storeDao.getAll();
			for( Store obj : list ) {
				log.log(Level.INFO, "Checking " + obj.getName() + "...");
				if( StringUtils.hasText(obj.getShoppingId()) && !shoppingIds.contains(obj.getShoppingId())) {
					log.log(Level.INFO, "Shopping " + obj.getShoppingId() + " not found for store " + obj.getName());
					Store s = storeDao.get(obj.getIdentifier(), true);
					storeDao.delete(s);
				} else if( StringUtils.hasText(obj.getBrandId()) && !brandIds.contains(obj.getBrandId())) {
					log.log(Level.INFO, "Brand " + obj.getBrandId() + " not found for store " + obj.getName());
					Store s = storeDao.get(obj.getIdentifier(), true);
					storeDao.delete(s);
				} else {
					log.log(Level.INFO, "All good with store " + obj.getName());
				}
			}

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
