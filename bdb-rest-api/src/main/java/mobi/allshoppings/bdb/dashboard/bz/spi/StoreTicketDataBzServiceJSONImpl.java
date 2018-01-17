package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.dao.StoreTicketDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreTicket;
import mx.getin.bdb.dashboard.bz.spi.StoreEntityData;
import mx.getin.model.interfaces.StoreDataEntity;

/**
 * Updates &amp; retreives tickets for a given store in a given period of time.
 * @author Matias Hapanowicz
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 1.0, ?
 * @since Allshoppings
 */
public class StoreTicketDataBzServiceJSONImpl extends StoreEntityData<StoreTicket> {

	@Autowired
	private StoreTicketDAO dao;
	@Autowired
	
	@Override
	protected List<StoreTicket> daoGetUsingStoreIdAndDatesAndRange(String storeId, String fromDate,
			String toDate, String toHour, boolean order) throws ASException {
		return dao.getUsingStoreIdAndDatesAndRange(storeId, fromDate, toDate, null,
				order ? "date" : null, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected StoreTicket daoGetUsingStoreIdAndDate(String storeId, String date, String hour) throws ASException {
		return dao.getUsingStoreIdAndDate(storeId, date, true);
	}

	@Override
	protected void daoUpdate(StoreDataEntity obj) throws ASException {
		if(obj instanceof StoreTicket) dao.update((StoreTicket) obj);
		else throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	protected void createStoreData(Store store, double qty, String date, String hour) throws ASException {
		StoreTicket obj = new StoreTicket();
		obj.setStoreId(store.getIdentifier());
		obj.setBrandId(store.getBrandId());
		obj.setDate(date);
		obj.setQty(qty);
		obj.setKey(dao.createKey());
		dao.create(obj);
	}

}