package mobi.allshoppings.bz.spi;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.DashboardFloorMapDataBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.dao.FloorMapDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.WifiSpotDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Cinema;
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
public class DashboardFloorMapDataBzServiceJSONImpl
extends RestBaseServerResource
implements DashboardFloorMapDataBzService {

	private static final Logger log = Logger.getLogger(DashboardFloorMapDataBzServiceJSONImpl.class.getName());

	@Autowired
	private CinemaDAO cinemaDao;
	@Autowired
	private FloorMapDAO floormapDao;
	@Autowired
	private ShoppingDAO shoppingDao;
	@Autowired
	private WifiSpotDAO wifispotDao;
	
	private BzFields bzFields = BzFields.getBzFields(getClass());

	/**
	 * Obtains information about a user
	 * 
	 * @return A JSON representation of the selected fields for a user
	 */
	@Override
	public String retrieve()
	{
		long start = markStart();
		JSONObject returnValue = null;
		try {
			// obtain the id and validates the auth token
			// obtainUserIdentifier();

			String entityId = obtainStringValue("entityId", null);
			Integer entityKind = obtainIntegerValue("entityKind", null);
			String floorMapId = obtainStringValue("floorMapId", null);

			if( !StringUtils.hasText(floorMapId)) {
				String shoppingId = null;

				if( entityKind == null || entityKind.equals(EntityKind.KIND_SHOPPING)) {
					shoppingId = entityId;
				} else {
					Cinema cinema = cinemaDao.get(entityId, true);
					shoppingId = cinema.getShoppingId();
				}

				// get level, if not defined use default value
				String level = this.obtainStringValue(LEVEL, BzFields.LEVEL_LIST);

				// set adapter options
				Map<String,Object> options = CollectionFactory.createMap();
				options.put(FloorMapAdapter.OPTIONS_SHOPPINGDAO, shoppingDao);

				long millisPre = new Date().getTime();
				List<FloorMapAdapter> list = new GenericAdapterImpl<FloorMapAdapter>()
						.adaptList(floormapDao.getUsingStatusAndShoppingId(
								StatusAware.STATUS_ENABLED, shoppingId), null,
								null, null, options);
				long diff = new Date().getTime() - millisPre;

				// Logs the result
				log.info("Number of floor maps found [" + list.size() + "] in " + diff + " millis");
				returnValue = this.getJSONRepresentationFromArrayOfObjects(list, this.obtainOutputFields(bzFields, level));

			} else {
				String LEVEL = "all";
				
				FloorMap floorMap = floormapDao.get(floorMapId, true);
				List<WifiSpot> wifiSpots = wifispotDao.getUsingFloorMapId(floorMapId);

				BzFields floorMapBzFields = BzFields.getModelBzFields(FloorMap.class);
				final String[] floorMapFields = obtainOutputFields(floorMapBzFields, LEVEL);
				returnValue = getJSONRepresentationFromObject(floorMap, floorMapFields);

				BzFields wifiSpotBzFields = BzFields.getModelBzFields(WifiSpot.class);
				JSONObject data = getJSONRepresentationFromArrayOfObjects(wifiSpots, obtainOutputFields(wifiSpotBzFields, LEVEL));

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
}
