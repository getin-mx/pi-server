package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.ImageDAO;
import mobi.allshoppings.dao.OfferDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.image.ImageDownloader;
import mobi.allshoppings.model.Offer;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.CollectionFactory;


public class TouchOffers extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TouchOffers.class.getName());
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
			OfferDAO offerDao = (OfferDAO)getApplicationContext().getBean("offer.dao.ref");
			ImageDAO imageDao = (ImageDAO)getApplicationContext().getBean("image.dao.ref");
			ImageDownloader downloader = (ImageDownloader)getApplicationContext().getBean("image.downloader");
			Date validTo = sdf.parse("2018-01-01");

			log.log(Level.INFO, "Touching offers....");
			List<Offer> list = offerDao.getAll();
			for( Offer obj : list ) {
				if( StringUtils.hasText(obj.getAvatarId())) {
					obj.setValidTo(validTo);
					offerDao.update(obj);
					try {
						imageDao.get(obj.getAvatarId(), true);
					} catch( Exception e ) {
						downloader.downloadImage(obj.getAvatarId(), "http://allshoppings1.appspot.com/img/" + obj.getAvatarId(), null, 0,0,0,0);
					}
				} else {
					offerDao.delete(obj.getIdentifier());
				}
			}
			
			list = offerDao.getAll();
			List<ModelKey> index = CollectionFactory.createList();
			for( Offer obj : list ) {
				index.add(obj);
			}
			offerDao.getIndexHelper().indexObject(index);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
