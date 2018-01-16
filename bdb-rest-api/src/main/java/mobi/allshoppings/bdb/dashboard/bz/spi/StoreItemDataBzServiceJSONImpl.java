package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.icu.util.Calendar;
import com.inodes.util.CollectionFactory;

import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.ImageDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.StoreItemDAO;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.Image;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreItem;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.Range;
import mx.getin.bdb.dashboard.bz.spi.StoreEntityData;


/**
 *
 */
public class StoreItemDataBzServiceJSONImpl extends StoreEntityData<StoreItem> {

	private static final SimpleDateFormat mdf = new SimpleDateFormat("yyyy-MM");
	
	@Autowired
	private StoreItemDAO dao;
	
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
