package mx.getin.bdb.dashboard.bz.spi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.dao.StoreRevenueDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreRevenue;
import mx.getin.dao.StoreRevenueByHourDAO;
import mx.getin.model.StoreRevenueByHour;
import mx.getin.model.interfaces.StoreDataEntity;

/**
 * @author ignacio
 *
 */
public class StoreRevenueByHourDataBzServiceJSONImpl extends StoreEntityData<StoreRevenueByHour> {

	@Autowired
	private StoreRevenueByHourDAO dao;
	@Autowired
	private StoreRevenueDAO stDao;
	
	@Override
	protected List<StoreRevenueByHour> daoGetUsingStoreIdAndDatesAndRange(String storeId, String fromDate,
			String toDateOrFromHour, String toHour, boolean order) throws ASException {
		return dao.getUsingStoreIdAndDateAndRange(storeId, fromDate, toDateOrFromHour, toHour, null,
				order ? "hour" : null, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected StoreRevenue daoGetUsingStoreIdAndDate(String storeId, String date, String hour)
			throws ASException {
		return hour == null ? stDao.getUsingStoreIdAndDate(storeId, date, true) :
			dao.getUsingStoreIdAndDateAndHour(storeId, date, hour, true);
	}

	@Override
	protected void daoUpdate(StoreDataEntity obj) throws ASException {
		if(obj instanceof StoreRevenueByHour) dao.update((StoreRevenueByHour) obj);
		else if(obj instanceof StoreRevenue) stDao.update((StoreRevenue) obj);
		else throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	protected void createStoreData(Store store, double qty, String date, String hour) throws ASException {
		boolean createHourly = hour != null;
		StoreRevenue obj = createHourly ? new StoreRevenueByHour() : new StoreRevenue();
		obj.setStoreId(store.getIdentifier());
		obj.setBrandId(store.getBrandId());
		obj.setDate(date);
		obj.setQty(qty);
		if(createHourly) {
			StoreRevenueByHour hourObj = (StoreRevenueByHour) obj;
			hourObj.setHour(hour);
			hourObj.setKey(dao.createKey());
			dao.create((StoreRevenueByHour) hourObj);
		} else {
			obj.setKey(stDao.createKey());
			stDao.create(obj);
		}
		
	}
	
}
