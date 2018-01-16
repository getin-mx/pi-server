package mx.getin.bdb.dashboard.bz.spi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.dao.StoreItemDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreItem;
import mx.getin.dao.StoreItemByHourDAO;
import mx.getin.model.StoreItemByHour;
import mx.getin.model.interfaces.StoreDataEntity;

/**
 * @author ignacio
 *
 */
public class StoreItemByHourDataBzServiceJSONImpl extends StoreEntityData<StoreItemByHour> {

	@Autowired
	private StoreItemByHourDAO dao;
	@Autowired
	private StoreItemDAO stDao;
	
	@Override
	protected List<StoreItemByHour> daoGetUsingStoreIdAndDatesAndRange(String storeId, String fromDate,
			String toDateOrFromHour, String toHour, boolean order) throws ASException {
		return dao.getUsingStoreIdAndDateAndRange(storeId, fromDate, toDateOrFromHour, toHour, null,
				order ? "hour" : null, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected StoreItem daoGetUsingStoreIdAndDate(String storeId, String date, String hour) throws ASException {
		return hour == null ? stDao.getUsingStoreIdAndDate(storeId, date, true) :
			dao.getUsingStoreIdAndDateAndHour(storeId, date, hour, true);
	}

	@Override
	protected void daoUpdate(StoreDataEntity obj) throws ASException {
		if(obj instanceof StoreItemByHour) dao.update((StoreItemByHour) obj);
		else if(obj instanceof StoreItem) stDao.update((StoreItem) obj);
		else throw ASExceptionHelper.invalidArgumentsException();
		
	}

	@Override
	protected void createStoreData(Store store, double qty, String date, String hour) throws ASException {
		StoreItemByHour obj = new StoreItemByHour();
		obj.setStoreId(store.getIdentifier());
		obj.setBrandId(store.getBrandId());
		obj.setDate(date);
		obj.setHour(hour);
		obj.setQty(qty);
		obj.setKey(dao.createKey());
		dao.create(obj);
	}

}
