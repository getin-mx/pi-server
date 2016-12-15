package mobi.allshoppings.geocoding.impl;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mobi.allshoppings.dao.FloorMapDAO;
import mobi.allshoppings.dao.WifiSpotDAO;
import mobi.allshoppings.dao.spi.FloorMapDAOJDOImpl;
import mobi.allshoppings.dao.spi.WifiSpotDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.WifiSpotService;
import mobi.allshoppings.model.DeviceWifiLocationHistory;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.FloorMap;
import mobi.allshoppings.model.WifiSpot;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;

public class WifiSpotServiceImpl implements WifiSpotService {

	private FloorMapDAO floorMapDao = new FloorMapDAOJDOImpl();
	private WifiSpotDAO wifiSpotDao = new WifiSpotDAOJDOImpl();
	private Map<String, List<FloorMap>> floorMapCache;
	private Map<String, List<WifiSpot>> wifiSpotCache;
	
	public WifiSpotServiceImpl() {
		floorMapCache = CollectionFactory.createMap();
		wifiSpotCache = CollectionFactory.createMap();
	}
	
	@Override
	public void calculateWifiSpot(DeviceWifiLocationHistory deviceWifiLocation)
			throws ASException {

		try {
			if( deviceWifiLocation.getEntityKind() != null && deviceWifiLocation.getEntityKind() == EntityKind.KIND_SHOPPING ) {
				List<FloorMap> floorMaps = floorMapCache
						.containsKey(deviceWifiLocation.getEntityId()) 
						? floorMapCache.get(deviceWifiLocation.getEntityId()) 
						: floorMapDao.getUsingStatusAndShoppingId(StatusAware.STATUS_ENABLED, deviceWifiLocation.getEntityId());
				floorMapCache.put(deviceWifiLocation.getEntityId(), floorMaps);

				if( floorMaps.size() > 0 ) {
					List<WifiSpot> wifiSpots = wifiSpotCache.get(deviceWifiLocation.getEntityId());
					if( wifiSpots == null || wifiSpots.size() == 0 ) {
						wifiSpots = CollectionFactory.createList();
						for( FloorMap fm : floorMaps ) {
							wifiSpots.addAll(wifiSpotDao.getUsingFloorMapId(fm.getIdentifier()));
						}
						wifiSpotCache.put(deviceWifiLocation.getEntityId(), wifiSpots);
					}

					float probability = 0;
					WifiSpot bestLocation = null;
					for( WifiSpot wifiSpot : wifiSpots ) {
						float p = this.calculatePositionProbability(deviceWifiLocation.getWifiData().getValue(), wifiSpot);
						if( p > probability ) {
							probability = p;
							bestLocation = wifiSpot;
						}
					}
					if( bestLocation != null ) {
						deviceWifiLocation.setWifiSpotId(bestLocation.getIdentifier());
					}
				}
			}
		} catch(Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	/**
	 * Calculates the probability that this is the position which is measuring
	 * right now
	 * 
	 * @param wifiManager
	 *            The wifi manager to work with
	 * @return a Value to represent the probability. The higher value, the
	 *         better
	 * @throws org.json.JSONException 
	 */
	public float calculatePositionProbability(String wifiData, WifiSpot wifiSpot) throws JSONException {

		JSONArray list = new JSONArray(wifiData);
		int elementsFound = 0;
		int maxVariance = 0;
		int minVariance = 100;
		int avgVariance = 0;
		for(int i = 0; i < list.length(); i++ ) {
			JSONObject res = (JSONObject)list.get(i);
			String bssid = res.getString("bssid");
			Integer currentLevel = res.getInt("level");

			Integer level = wifiSpot.getSignals().get(bssid);
			if( level != null ) {
				// How many elements did I found. If it's less than the 51%, then discard it!
				elementsFound++;

				// Get the percentage of my current level of signal against the average 
				// level of signal
				int signal = level == 0 ? 0 : currentLevel * 100 / level;

				// Measure variance
				if( signal > maxVariance ) maxVariance = signal;
				if( signal < minVariance ) minVariance = signal;

				switch( wifiSpot.getCalculusStrategy() ) {
				case WifiSpot.TRIM_TOP:
					if( signal > 100 ) signal = 100;
					break;
				default:
					if( signal > 100 ) signal = 100 - (signal - 100);
				}
				avgVariance = avgVariance == 0 ? signal : (avgVariance + signal);
			}
		}

		// Calculate how many APs do I have in compare with how many APs did I recorded
		avgVariance = elementsFound == 0 ? 0 : avgVariance / elementsFound;
		int q = wifiSpot.getSignals().size() == 0 ? 0 : (elementsFound * 100 / wifiSpot.getSignals().size());

		// now integrates the percentage of elements found with the avgVariance
		return( avgVariance * ((float)q / 100));

	}
}
