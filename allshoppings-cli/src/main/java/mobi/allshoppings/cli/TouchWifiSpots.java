package mobi.allshoppings.cli;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.WifiSpotDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.WifiSpot;


public class TouchWifiSpots extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TouchWifiSpots.class.getName());

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
			WifiSpotDAO dao = (WifiSpotDAO)getApplicationContext().getBean("wifispot.dao.ref");

			log.log(Level.INFO, "Touching wifi spots....");
			List<WifiSpot> list = dao.getAll();
			for( WifiSpot obj : list ) {
				log.log(Level.INFO, "Touching " + obj.getIdentifier() + "...");
				if( !StringUtils.hasText(obj.getWordAlias()))
					obj.setWordAlias(dao.getNextSequence());
				dao.update(obj);
			}
						
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
