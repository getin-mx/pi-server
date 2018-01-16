package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.dao.StoreItemDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreItem;
import mx.getin.bdb.dashboard.bz.spi.StoreEntityData;
import mx.getin.model.interfaces.StoreDataEntity;

/**
 *
 */
public class StoreItemDataBzServiceJSONImpl extends StoreEntityData<StoreItem> {

	@Autowired
	private StoreItemDAO dao;
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<StoreItem> daoGetUsingStoreIdAndDatesAndRange(String storeId, String fromDate,
			String toDate, String toHour, boolean order) throws ASException {
		return dao.getUsingStoreIdAndDatesAndRange(storeId, fromDate, toDate, null,
				order ? "date" : null, false);
	}
	
	@Override
	protected StoreItem daoGetUsingStoreIdAndDate(String storeId, String date, String hour) throws ASException {
		return dao.getUsingStoreIdAndDate(storeId, date, true);
	}

	@Override
	protected void daoUpdate(StoreItem obj) throws ASException {
		dao.update(obj);
	}

	@Override
	protected void createStoreData(Store store, double qty, String date, String hour) throws ASException {
		StoreItem obj = new StoreItem();
		obj.setStoreId(store.getIdentifier());
		obj.setBrandId(store.getBrandId());
		obj.setDate(date);
		obj.setQty(qty);
		obj.setKey(dao.createKey());
		dao.create(obj);
	}

}
