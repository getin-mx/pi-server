package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBPostBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.FloorMapDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.WifiSpotDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.FloorMap;
import mobi.allshoppings.model.WifiSpot;
import mobi.allshoppings.model.adapter.FloorMapAdapter;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class FloorMapDataBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService, BDBPostBzService {

	private static final Logger log = Logger.getLogger(FloorMapDataBzServiceJSONImpl.class.getName());

	@Autowired
	private FloorMapDAO floormapDao;
	@Autowired
	private ShoppingDAO shoppingDao;
	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private WifiSpotDAO wifispotDao;
	
	/**
	 * Obtains a list of FloorMap points
	 * 
	 * @return A JSON representation of the selected fields for a FloorMap
	 */
	@Override
	public String retrieve()
	{
		long start = markStart();
		JSONObject returnValue = null;
		try {
			// obtain the id and validates the auth token
//			obtainUserIdentifier(true);

			String entityId = obtainStringValue("entityId", null);
			byte entityKind = obtainByteValue("entityKind", (byte) -1);
			String floorMapId = obtainStringValue("floorMapId", null);

			if( !StringUtils.hasText(floorMapId)) {
				String shoppingId = null;

				if( entityKind < 0 || entityKind == EntityKind.KIND_SHOPPING) {
					shoppingId = entityId;
				}/* else {
					Cinema cinema = cinemaDao.get(entityId, true);
					shoppingId = cinema.getShoppingId();
				}*/

				// inject adapter options
				Map<String,Object> options = CollectionFactory.createMap();
				options.put(FloorMapAdapter.OPTIONS_SHOPPINGDAO, shoppingDao);
				options.put(FloorMapAdapter.OPTIONS_STOREDAO, storeDao);

				long millisPre = System.currentTimeMillis();
				List<FloorMapAdapter> list = new GenericAdapterImpl<FloorMapAdapter>()
						.adaptList(floormapDao.getUsingStatusAndShoppingId(
								StatusAware.STATUS_ENABLED, shoppingId), null,
								null, null, options);
				long diff = System.currentTimeMillis() - millisPre;

				// Logs the result
				log.info("Number of floor maps found [" + list.size() + "] in " + diff + " millis");
				returnValue = this.getJSONRepresentationFromArrayOfObjects(
						list, this.obtainOutputFields(FloorMapAdapter.class));

			} else {
				
				FloorMap floorMap = floormapDao.get(floorMapId, true);
				List<WifiSpot> wifiSpots = wifispotDao.getUsingFloorMapId(floorMapId);

				// Get the output fields
				String[] floorMapFields = this.obtainOutputFields(FloorMap.class);
				returnValue = getJSONRepresentationFromObject(floorMap, floorMapFields);

				String[] wifiSpotFields = this.obtainOutputFields(WifiSpot.class);
				JSONObject data = getJSONRepresentationFromArrayOfObjects(wifiSpots, wifiSpotFields);

				returnValue.put("data", data.get("data"));
				
			}
			
			return returnValue.toString();

		} catch (ASException e) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE || 
					e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE) {
				log.log(Level.INFO, e.getMessage());
			} else {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			return getJSONRepresentationFromException(e).toString();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} finally {
			markEnd(start);
		}
	}

	@Override
	public String change(JsonRepresentation entity) {
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			// obtainUserIdentifier(true);
			
			JSONObject json = entity.getJsonObject();
			String identifier = json.getString("identifier");

			FloorMap floormap = floormapDao.get(identifier, true);
			
			JSONArray arr = json.getJSONArray("data");
			for( int i = 0; i < arr.length(); i++ ) {
				JSONObject el = arr.getJSONObject(i);
				String elIdentifier = el.getString("identifier");
				int elX = el.getInt("x");
				int elY = el.getInt("y");
				
				WifiSpot obj = wifispotDao.get(elIdentifier);
				obj.setX(elX);
				obj.setY(elY);
				wifispotDao.update(obj);
			}
			
			floormapDao.update(floormap);
			
			return generateJSONOkResponse().toString();
			
		} catch (ASException e) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE || 
					e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE) {
				log.log(Level.INFO, e.getMessage());
			} else {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			return getJSONRepresentationFromException(e).toString();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} finally {			
			markEnd(start);
		}
	}
}
