package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.APDMAEmployeeDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDMAEmployee;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.model.adapter.NameAndIdAdapter;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class AssignedEmployeeListBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(AssignedEmployeeListBzServiceJSONImpl.class.getName());

	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private APDMAEmployeeDAO apdmaeDao;

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
			List<Store> storeList = CollectionFactory.createList();
			long diff = 0;
			
			long millisPre = new Date().getTime();

			String entityId = obtainStringValue("entityId", null);
			byte entityKind = obtainByteValue("entityKind", (byte) -1);
			List<Byte> status = Arrays.asList(StatusAware.STATUS_ENABLED);
			
			
			if( EntityKind.KIND_SHOPPING == entityKind ) {
				storeList = storeDao.getUsingShoppingAndStatus(entityId, status, "uName");
			} else if( EntityKind.KIND_BRAND == entityKind ) {
				storeList = storeDao.getUsingBrandAndStatus(entityId, status, "uName");
			}
			
			if( user.getSecuritySettings().getRole().equals(Role.STORE)) {
				List<Store> tmpList = CollectionFactory.createList();
				for( Store store : storeList ) {
					if(user.getSecuritySettings().getStores().contains(store.getIdentifier())) {
						tmpList.add(store);
					}
				}
				storeList.clear();
				storeList.addAll(tmpList);
			}
			
			diff = new Date().getTime() - millisPre;

			// Logs the result
			log.info("Number of stores found [" + storeList.size() + "] in " + diff + " millis");

			List<APDMAEmployee> emps = CollectionFactory.createList();
			if( EntityKind.KIND_BRAND == entityKind || EntityKind.KIND_SHOPPING == entityKind ) {
				emps.addAll(apdmaeDao.getUsingEntityIdAndRange(entityId, entityKind, null, null, null, true));
			}
			for( Store store : storeList ) {
				emps.addAll(apdmaeDao.getUsingEntityIdAndRange(store.getIdentifier(), EntityKind.KIND_STORE, null, null, null, true));
			}
			
			// Creates the list
			List<NameAndIdAdapter> adapter = CollectionFactory.createList();
			for( APDMAEmployee b : emps ) {
					NameAndIdAdapter obj = new NameAndIdAdapter();
					obj.setIdentifier(b.getIdentifier());
					obj.setName(b.getDescription());
					adapter.add(obj);
			}

			Collections.sort(adapter, new Comparator<NameAndIdAdapter>() {
			    @Override
			    public int compare(NameAndIdAdapter o1, NameAndIdAdapter o2) {
			        return o1.getName().compareTo(o2.getName());
			    }
			});

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
