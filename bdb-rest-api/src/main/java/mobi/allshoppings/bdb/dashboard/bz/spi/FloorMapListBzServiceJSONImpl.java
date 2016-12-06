package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.FloorMapDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.adapter.FloorMapAdapter;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;
import mobi.allshoppings.model.adapter.NameAndIdAdapter;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class FloorMapListBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(FloorMapListBzServiceJSONImpl.class.getName());

	@Autowired
	private FloorMapDAO floormapDao;
	@Autowired
	private ShoppingDAO shoppingDao;

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
			User user = getUserFromToken();

			// inject adapter options
			Map<String,Object> options = CollectionFactory.createMap();
			options.put(FloorMapAdapter.OPTIONS_SHOPPINGDAO, shoppingDao);

			long millisPre = new Date().getTime();
			List<FloorMapAdapter> list = null;
			list = new GenericAdapterImpl<FloorMapAdapter>()
					.adaptList(floormapDao.getUsingStatusAndUserAndRange(
							StatusAware.STATUS_ENABLED, user, null), null,
							null, null, options);
			long diff = new Date().getTime() - millisPre;

			// Logs the result
			log.info("Number of floor maps found [" + list.size() + "] in " + diff + " millis");
			
			List<NameAndIdAdapter> adapter = CollectionFactory.createList();
			HashSet<String> nodup = new HashSet<String>();
			
			for( FloorMapAdapter fma : list ) {
				if(!nodup.contains(fma.getShoppingId())) {
					NameAndIdAdapter obj = new NameAndIdAdapter();
					obj.setIdentifier(fma.getShoppingId());
					obj.setName(fma.getShoppingName());
					obj.setAvatarId(fma.getImageId());
					adapter.add(obj);
					nodup.add(fma.getShoppingId());
				}
			}
			
			returnValue = this.getJSONRepresentationFromArrayOfObjects(
					adapter, this.obtainOutputFields(NameAndIdAdapter.class));

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
