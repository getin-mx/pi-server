package mobi.allshoppings.cli;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.ImageDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.image.ImageDownloader;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.CollectionFactory;


public class TouchShoppings extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TouchShoppings.class.getName());

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
			ImageDAO imageDao = (ImageDAO)getApplicationContext().getBean("image.dao.ref");
			ImageDownloader downloader = (ImageDownloader)getApplicationContext().getBean("image.downloader");

			log.log(Level.INFO, "Touching shoppings....");
			List<Shopping> list = shoppingDao.getAll();
			for( Shopping obj : list ) {
				log.log(Level.INFO, "Touching " + obj.getName() + "...");
				if( StringUtils.hasText(obj.getAvatarId())) {
					try {
						imageDao.get(obj.getAvatarId(), true);
					} catch( Exception e ) {
						downloader.downloadImage(obj.getAvatarId(), "http://allshoppings1.appspot.com/img/" + obj.getAvatarId(), null, 0,0,0,0);
					}
					shoppingDao.update(obj);
				}
			}
			
			list = shoppingDao.getAll();
			List<ModelKey> index = CollectionFactory.createList();
			for( Shopping obj : list ) {
				index.add(obj);
			}
			shoppingDao.getIndexHelper().indexObject(index);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
