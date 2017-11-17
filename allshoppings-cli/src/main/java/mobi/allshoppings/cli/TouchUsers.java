package mobi.allshoppings.cli;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.ImageDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.image.ImageDownloader;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.CollectionFactory;


public class TouchUsers extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TouchUsers.class.getName());

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
			UserDAO userDao = (UserDAO)getApplicationContext().getBean("user.dao.ref");
			ImageDAO imageDao = (ImageDAO)getApplicationContext().getBean("image.dao.ref");
			ImageDownloader downloader = (ImageDownloader)getApplicationContext().getBean("image.downloader");

			log.log(Level.INFO, "Touching brands....");
//			List<User> list = userDao.getAll();
//			List<User> list = userDao.getUsingStatusAndRange(null, new Range(0,2), null);
			List<Integer> role = Arrays.asList(new Integer[] {1,3,5,7,9,11,13,15,17});
			List<User> list = userDao.getUsingLastUpdateStatusAndRangeAndRole(null, null, false, null, null, null, role, null, true);
			for( User obj : list ) {
				log.log(Level.INFO, "Touching " + obj.getIdentifier() + "...");
				if( StringUtils.hasText(obj.getAvatarId())) {
					try {
						imageDao.get(obj.getAvatarId(), true);
					} catch( Exception e ) {
						try {
							downloader.downloadImage(obj.getAvatarId(),
									"http://allshoppings1.appspot.com/img/"
											+obj.getAvatarId(), null, 0,0,0,0);
						} catch(Exception e1) {
							log.log(Level.WARN, "Could not retreive user image from: "
									+"http://allshoppings1.appspot.com/img/"
									+obj.getAvatarId(), e1);
							continue;
						}
					}
				}
				try {
					userDao.update(obj);
				} catch( Exception e ) {
					log.log(Level.WARN, e.getMessage(), e);
				}
			}
			
			list = userDao.getUsingLastUpdateStatusAndRangeAndRole(null, null, false, null, null, null, role, null, true);
			List<ModelKey> index = CollectionFactory.createList();
			for( User obj : list ) {
				index.add(obj);
			}
			userDao.getIndexHelper().indexObject(index);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
