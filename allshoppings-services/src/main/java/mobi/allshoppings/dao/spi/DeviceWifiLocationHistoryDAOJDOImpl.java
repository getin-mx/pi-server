package mobi.allshoppings.dao.spi;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.DeviceWifiLocationHistoryDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DeviceWifiLocationHistory;
import mobi.allshoppings.tools.CollectionFactory;

public class DeviceWifiLocationHistoryDAOJDOImpl extends GenericDAOJDO<DeviceWifiLocationHistory> implements DeviceWifiLocationHistoryDAO {
	
	public DeviceWifiLocationHistoryDAOJDOImpl() {
		super(DeviceWifiLocationHistory.class);
	}

	@Override
	public synchronized Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(DeviceWifiLocationHistory.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeviceWifiLocationHistory> getUsingDeviceUID(String deviceUid) throws ASException {

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		List<DeviceWifiLocationHistory> ret = CollectionFactory.createList();

		try {
			Query query = pm.newQuery(DeviceWifiLocationHistory.class);

			query.setFilter("deviceUUID == paramUID");
			query.declareParameters("String paramUID");
			List<DeviceWifiLocationHistory> list = (List<DeviceWifiLocationHistory>)query.execute(deviceUid); 

			if (list != null) {
				// force to read
				for (DeviceWifiLocationHistory dlh : list) {
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