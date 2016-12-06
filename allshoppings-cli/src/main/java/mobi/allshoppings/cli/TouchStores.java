package mobi.allshoppings.cli;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.ImageDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.image.ImageDownloader;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.CollectionFactory;


public class TouchStores extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TouchStores.class.getName());

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
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");
			ImageDAO imageDao = (ImageDAO)getApplicationContext().getBean("image.dao.ref");
			ImageDownloader downloader = (ImageDownloader)getApplicationContext().getBean("image.downloader");

			log.log(Level.INFO, "Touching stores....");
			List<Store> list = storeDao.getAll();
			for( Store obj : list ) {
				log.log(Level.INFO, "Touching " + obj.getName() + "...");
				if( StringUtils.hasText(obj.getAvatarId())) {
					try {
						imageDao.get(obj.getAvatarId(), true);
					} catch( Exception e ) {
						downloader.downloadImage(obj.getAvatarId(), "http://allshoppings1.appspot.com/img/" + obj.getAvatarId(), null, 0,0,0,0);
					}
				}
				storeDao.update(obj);
			}
			
			list = storeDao.getAll();
			List<ModelKey> index = CollectionFactory.createList();
			for( Store obj : list ) {
				index.add(obj);
			}
			storeDao.getIndexHelper().indexObject(index);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
