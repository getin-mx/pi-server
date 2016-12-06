package mobi.allshoppings.cli;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.DeviceLocationDAO;
import mobi.allshoppings.dao.spi.DeviceLocationDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;


public class GetCounter extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws ASException {

		try {
			DeviceLocationDAO dao = new DeviceLocationDAOJDOImpl();
			System.out.println("Found " + dao.count() + " entities");
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}

	}
}
