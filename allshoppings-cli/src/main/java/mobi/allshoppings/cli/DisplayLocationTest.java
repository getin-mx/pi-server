package mobi.allshoppings.cli;

import java.util.Date;
import java.util.List;

import joptsimple.OptionParser;

import mobi.allshoppings.dao.DeviceLocationDAO;
import mobi.allshoppings.dao.spi.DeviceLocationDAOJDOImpl;
import mobi.allshoppings.model.DeviceLocation;

public class DisplayLocationTest extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) {

		Date now = new Date();
		try {
			DeviceLocationDAO dlDao = new DeviceLocationDAOJDOImpl();
			List<DeviceLocation> locs = dlDao.getUsingIdList(TestDevices.testDevices);
			for( DeviceLocation loc : locs ) {
				int old = (int)((now.getTime() - loc.getLastUpdate().getTime()) / 60 / 1000);
				System.out.println(loc.getDeviceUUID() + (loc.getDeviceUUID().length() < 32  ? "\t\t" : "\t") 
						+ loc.getLastUpdate() + "\t" + loc.getLat() + "\t" + loc.getLon() + "\t" + loc.getGeohash() 
						+ " - " + loc.getCity() + " - " + old + " mins old");
			}
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
