package mobi.allshoppings.cli;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.ImageDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.image.ImageDownloader;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.CollectionFactory;


public class TouchBrands extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TouchBrands.class.getName());

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
			BrandDAO brandDao = (BrandDAO)getApplicationContext().getBean("brand.dao.ref");
			ImageDAO imageDao = (ImageDAO)getApplicationContext().getBean("image.dao.ref");
			ImageDownloader downloader = (ImageDownloader)getApplicationContext().getBean("image.downloader");

			log.log(Level.INFO, "Touching brands....");
			List<Brand> list = brandDao.getAll();
//			List<Brand> list = brandDao.getUsingStatusAndRange(null, new Range(0,5), null);
			for( Brand obj : list ) {
				log.log(Level.INFO, "Touching " + obj.getIdentifier() + "...");
				if( StringUtils.hasText(obj.getAvatarId())) {
					try {
						imageDao.get(obj.getAvatarId(), true);
					} catch( Exception e ) {
						downloader.downloadImage(obj.getAvatarId(), "http://allshoppings1.appspot.com/img/" + obj.getAvatarId(), null, 0,0,0,0);
					}
				}
				brandDao.update(obj);
			}
			
			list = brandDao.getAll();
			List<ModelKey> index = CollectionFactory.createList();
			for( Brand obj : list ) {
				index.add(obj);
			}
			brandDao.getIndexHelper().indexObject(index);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
