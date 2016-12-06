package mobi.allshoppings.bz.spi;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.WifiSpotListBzService;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.WifiSpotDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.WifiSpot;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;
import mobi.allshoppings.model.adapter.WifiSpotAdapter;
import mobi.allshoppings.tools.CollectionFactory;

/**
 *
 */
public class WifiSpotListBzServiceJSONImpl extends RestBaseServerResource implements WifiSpotListBzService {

	private static final Logger log = Logger.getLogger(WifiSpotListBzServiceJSONImpl.class.getName());

	@Autowired
	private WifiSpotDAO dao;

	private BzFields bzFields = BzFields.getBzFields(getClass());
	private static final String IDENTIFIER = "floorMapId";

	@Override
	public String retrieve() {

		long start = markStart();
		JSONObject returnValue = null;
		try {
			List<WifiSpotAdapter> wifiSpots = new ArrayList<WifiSpotAdapter>();

			// validate authToken
			User user = this.getUserFromToken();
			String identifier = obtainIdentifier(IDENTIFIER);

			// get level, if not defined use default value
			String level = this.obtainStringValue(LEVEL, BzFields.LEVEL_LIST);

			// set adapter options
			Map<String,Object> options = CollectionFactory.createMap();

			// retrieve all Floor Maps
			long millisPre = new Date().getTime();
			wifiSpots = new GenericAdapterImpl<WifiSpotAdapter>().adaptList(
					dao.getUsingFloorMapId(identifier),
					user.getIdentifier(), null, null, options);
			long diff = new Date().getTime() - millisPre;

			// Logs the result
			log.info("Number of wifi spots found [" + wifiSpots.size() + "] in " + diff + " millis");
			returnValue = this.getJSONRepresentationFromArrayOfObjects(wifiSpots, this.obtainOutputFields(bzFields, level));

			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.WifiSpotListBzService"),
					null, null);

		} catch (ASException e) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE || 
					e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE) {
				log.log(Level.INFO, e.getMessage());
			} else {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			returnValue = getJSONRepresentationFromException(e);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			returnValue = this.getJSONRepresentationFromException(e);
		} finally {
			markEnd(start);
		}
		return returnValue.toString();
	}

	@Override
	public String update(final JsonRepresentation entity) {

		long start = markStart();
		try {
			final User user = getUserFromToken();
			log.info("Update WifiSpots start");

			final JSONObject obj = entity.getJsonObject();
			final JSONArray arr = obj.getJSONArray("data");

			WifiSpot spot;
			
			for( int i = 0; i < arr.length(); i++ ) {
				JSONObject ele = arr.getJSONObject(i);

				//check mandatory fields
				log.info("check mandatory fields");
				if (!hasDefaultParameters(ele, bzFields.DEFAULT_FIELDS.toArray(EMPTY_STRING_ARRAY))) {
					throw ASExceptionHelper.invalidArgumentsException(INVALID_DEFAULT_FIELDS);
				}

				//update wifi spot object
				boolean newSpot = false;
				try {
					spot = dao.get(ele.getString("identifier"));
					setPropertiesFromJSONObject(ele, spot, bzFields.READONLY_FIELDS);
				} catch( ASException e ) {
					newSpot = true;
					spot = new WifiSpot();
					setPropertiesFromJSONObject(ele, spot, bzFields.READONLY_FIELDS);
					spot.setKey(dao.createKey(spot));
				}
				
				if(!StringUtils.hasText(spot.getWordAlias()))
					spot.setWordAlias(dao.getNextSequence());
				
				if( newSpot ) {
					dao.create(spot);
				} else {
					dao.update(spot);
				}
			}

			// track action
			trackerHelper.enqueue( user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.WifiSpotListBzService.update"), 
					null, null);

			log.info("Update WifiSpots end");

		} catch (JSONException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} catch (ASException | IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(e).toString();
		} finally {
			markEnd(start);
		}

		return generateJSONOkResponse().toString();
	}
}
