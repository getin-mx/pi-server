package mobi.allshoppings.model.test;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.DeviceLocationDAO;
import mobi.allshoppings.dao.spi.CinemaDAOJDOImpl;
import mobi.allshoppings.dao.spi.DeviceInfoDAOJDOImpl;
import mobi.allshoppings.dao.spi.DeviceLocationDAOJDOImpl;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.geocoding.impl.GeoCodingHelperGMapsImpl;
import mobi.allshoppings.model.Cinema;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.DeviceLocation;
import mobi.allshoppings.model.embedded.AlternateLocation;
import mobi.allshoppings.tools.CollectionFactory;

import org.junit.Test;

public class LocationTest extends TestCase {

	private CinemaDAO cinemaDao = new CinemaDAOJDOImpl();
	private DeviceInfoDAO deviceInfoDao = new DeviceInfoDAOJDOImpl();
	private DeviceLocationDAO deviceLocationDao = new DeviceLocationDAOJDOImpl();
	private GeoCodingHelper geocoder = new GeoCodingHelperGMapsImpl();

	@Test
	public void test0001() {
		try {
			// Initialization settings
			deviceLocationDao.setGeocoder(geocoder);
			deviceInfoDao.setDeviceLocationDao(deviceLocationDao);

			boolean useAltLocations = false;
			Cinema cinema = cinemaDao.get("cinepolis_mx_339", true);

			// Locate candidate devices
			// Only when there are no restrictToDevices
			List<String> devices = CollectionFactory.createList();
			Date lastLocationUpdate = new Date((long)(new Date().getTime() - (3600000)));
			List<DeviceInfo> candidateDevices = deviceInfoDao.getByProximity(
					new GeoPoint(cinema.getAddress().getLatitude(), cinema
							.getAddress().getLongitude(), geocoder
							.encodeGeohash(cinema.getAddress().getLatitude(),
									cinema.getAddress().getLongitude())),
									new Integer(5), new Integer(200), cinema.getBrandId(),
									lastLocationUpdate, true);

			if( useAltLocations ) {
				for( AlternateLocation location : cinema.getAlternateLocations() ) {
					List<DeviceInfo> tmpdevices = deviceInfoDao.getByProximity(
							new GeoPoint(location.getLatitude(), location
									.getLongitude(), geocoder
									.encodeGeohash(location.getLatitude(),
											location.getLongitude())),
											new Integer(5), new Integer(200), cinema.getBrandId(),
											lastLocationUpdate, true);
					for( DeviceInfo obj : tmpdevices ) {
						if(!candidateDevices.contains(obj)) candidateDevices.add(obj);
					}
				}
			}

			for( DeviceInfo obj : candidateDevices ) {
				if( !devices.contains(obj.getIdentifier())) devices.add(obj.getIdentifier());
			}

			int counter = 0;
			for( DeviceInfo obj : candidateDevices ) {
				DeviceLocation dl = deviceLocationDao.get(obj.getIdentifier(), true);
				System.out.println(dl.getLastUpdate() + " - " + dl.getIdentifier());
				counter++;
			}
			
			System.out.println("Found " + counter + " people in " + cinema.getName());

		} catch( Exception e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
