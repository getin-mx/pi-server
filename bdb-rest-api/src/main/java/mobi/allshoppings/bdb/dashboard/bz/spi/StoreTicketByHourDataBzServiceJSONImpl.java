package mobi.allshoppings.bdb.dashboard.bz.spi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.dao.StoreTicketByHourDAO;
import mobi.allshoppings.dao.StoreTicketDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreTicket;
import mobi.allshoppings.model.StoreTicketByHour;
import mx.getin.bdb.dashboard.bz.spi.StoreEntityData;
import mx.getin.model.interfaces.StoreDataEntity;

/**
 * Updates &amp; retreives tickets by hour for a given store in a given period of time.
 * @author Matias Hapanowicz
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 1.0, ?
 * @since Allshoppings
 */
public class StoreTicketByHourDataBzServiceJSONImpl extends StoreEntityData<StoreTicketByHour> {

	@Autowired
	private StoreTicketByHourDAO dao;
	@Autowired
	private StoreTicketDAO stDao;

	@Override
	protected List<StoreTicketByHour> daoGetUsingStoreIdAndDatesAndRange(String storeId, String fromDate,
			String toDateOrFromHour, String toHour, boolean order) throws ASException {
		return dao.getUsingStoreIdAndDateAndRange(storeId, fromDate, toDateOrFromHour, toHour, null,
				order ? "hour" : null, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected StoreTicket daoGetUsingStoreIdAndDate(String storeId, String date, String hour) throws ASException {
		return hour == null ? stDao.getUsingStoreIdAndDate(storeId, date, true) :
			dao.getUsingStoreIdAndDateAndHour(storeId, date, hour, true);
	}

	@Override
	protected void daoUpdate(StoreDataEntity obj) throws ASException {
		if(obj instanceof StoreTicketByHour) dao.update((StoreTicketByHour) obj);
		else if(obj instanceof StoreTicket) stDao.update((StoreTicket) obj);
		else throw ASExceptionHelper.invalidArgumentsException();
	}

	@Override
	protected void createStoreData(Store store, double qty, String date, String hour) throws ASException {
		boolean createHourly = hour != null;
		StoreTicket obj = createHourly ? new StoreTicketByHour() : new StoreTicket();
		obj.setStoreId(store.getIdentifier());
		obj.setBrandId(store.getBrandId());
		obj.setDate(date);
		obj.setQty(qty);
		if(createHourly) {
			StoreTicketByHour hourObj = (StoreTicketByHour) obj;
			hourObj.setHour(hour);
			hourObj.setKey(dao.createKey());
			dao.create((StoreTicketByHour) hourObj);
		} else {
			obj.setKey(stDao.createKey());
			stDao.create(obj);
		}
	}	

}
