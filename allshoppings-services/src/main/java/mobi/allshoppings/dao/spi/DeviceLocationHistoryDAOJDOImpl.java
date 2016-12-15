package mobi.allshoppings.dao.spi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Cursor;
import com.inodes.datanucleus.model.JDOCursorHelper;
import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.DeviceLocationHistoryDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DeviceLocation;
import mobi.allshoppings.model.DeviceLocationHistory;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public class DeviceLocationHistoryDAOJDOImpl extends GenericDAOJDO<DeviceLocationHistory> implements DeviceLocationHistoryDAO {
	
	public DeviceLocationHistoryDAOJDOImpl() {
		super(DeviceLocationHistory.class);
	}

	@Override
	public synchronized Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(DeviceLocationHistory.class);
	}

	@Override
	public DeviceLocationHistory build(DeviceLocation deviceLocation) throws ASException {
		
		if( deviceLocation == null ) return null;
		
		DeviceLocationHistory dlh = new DeviceLocationHistory();
		dlh.setKey(createKey());
		dlh.setCity(deviceLocation.getCity());
		dlh.setProvince(deviceLocation.getProvince());
		dlh.setCountry(deviceLocation.getCountry());
		dlh.setDeviceUUID(deviceLocation.getDeviceUUID());
		dlh.setLat(deviceLocation.getLat());
		dlh.setLon(deviceLocation.getLon());
		dlh.setConnection(deviceLocation.getConnection());
		dlh.setUserId(deviceLocation.getUserId());
		dlh.setUserName(deviceLocation.getUserName());
		dlh.setGeohash(deviceLocation.getGeohash());
		dlh.setPrecision(deviceLocation.getPrecision());
		dlh.setOperator(deviceLocation.getOperator());
		dlh.setSignal(deviceLocation.getSignal());
		dlh.setRoaming(deviceLocation.getRoaming());
		return dlh;

	}

	@Override
	public List<DeviceLocationHistory> getUsingDatesAndRange(Date fromDate, Date toDate, Range range) throws ASException {
		return getUsingDatesAndRange(null, fromDate, toDate, range, null, true);
	}
	
	@Override
	public List<DeviceLocationHistory> getUsingDatesAndRange(PersistenceProvider pp, Date fromDate, Date toDate, Range range, String order, boolean detachable) throws ASException {
		List<DeviceLocationHistory> returnedObjs = new ArrayList<DeviceLocationHistory>();

		PersistenceManager pm;
		if( pp == null ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// From Date Parameter
			if( null != fromDate ) {
				declaredParams.add("java.util.Date fromDateParam");
				filters.add("creationDateTime >= fromDateParam");
				parameters.put("fromDateParam", fromDate);
			}

			// To Date Parameter
			if( null != toDate ) {
				declaredParams.add("java.util.Date toDateParam");
				filters.add("creationDateTime <= toDateParam");
				parameters.put("toDateParam", toDate);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			// Adds a cursor to the ranged query
			if( range != null ) {
				if( StringUtils.hasText(range.getCursor())) {
					// Query q = the same query that produced the cursor
					// String cursorString = the string from storage
					Cursor cursor = Cursor.fromWebSafeString(range.getCursor());
					Map<String, Object> extensionMap = new HashMap<String, Object>();
					extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
					query.setExtensions(extensionMap);
					query.setRange(0, (range.getTo() - range.getFrom()));
				} else {
					query.setRange(range.getFrom(), range.getTo());
				}
			}

			// Sets order as required
			if( StringUtils.hasText(order)) query.setOrdering(order);

			@SuppressWarnings("unchecked")
			List<DeviceLocationHistory> objs = parameters.size() > 0 ? (List<DeviceLocationHistory>)query.executeWithMap(parameters) : (List<DeviceLocationHistory>)query.execute();
			if (objs != null) {
				// force to read
				for (DeviceLocationHistory obj : objs) {
					if( detachable )
						returnedObjs.add(pm.detachCopy(obj));
					else
						returnedObjs.add(obj);
				}
			}

			// Store the cursorString
			if( range != null ) {
				Cursor cursor = JDOCursorHelper.getCursor(objs);
				if( cursor != null )
					range.setCursor(cursor.toWebSafeString());
			}

		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			if( null == pp ) pm.close();
		}

		return returnedObjs;

	}
	
	@Override
	public long countUsingDates(Date fromDate, Date toDate) throws ASException {
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// From Date Parameter
			if( null != fromDate ) {
				declaredParams.add("java.util.Date fromDateParam");
				filters.add("creationDateTime >= fromDateParam");
				parameters.put("fromDateParam", fromDate);
			}

			// To Date Parameter
			if( null != toDate ) {
				declaredParams.add("java.util.Date toDateParam");
				filters.add("creationDateTime <= toDateParam");
				parameters.put("toDateParam", toDate);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			query.setResult("count(this)");
			Long results = Long.parseLong(query.executeWithMap(parameters).toString());
			return (long)results;

		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			pm.close();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeviceLocationHistory> getUsingDeviceUID(String deviceUid) throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<DeviceLocationHistory> ret = CollectionFactory.createList();

		try {
			Query query = pm.newQuery(DeviceLocationHistory.class);

			query.setFilter("deviceUUID == paramUID");
			query.declareParameters("String paramUID");
			List<DeviceLocationHistory> list = (List<DeviceLocationHistory>)query.execute(deviceUid); 

			if (list != null) {
				// force to read
				for (DeviceLocationHistory dlh : list) {
					ret.add(dlh);
				}
			}
			
		}catch (Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			pm.close();
		}		
		return ret;
	}

}