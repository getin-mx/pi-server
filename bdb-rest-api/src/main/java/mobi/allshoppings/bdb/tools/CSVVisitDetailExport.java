package mobi.allshoppings.bdb.tools;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.util.CollectionFactory;

import mobi.allshoppings.dao.APDVisitDAO;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.interfaces.StatusAware;

public class CSVVisitDetailExport {

	@Autowired
	private BrandDAO brandDao;
	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private APDVisitDAO apdvDao;
	@Autowired
	private ShoppingDAO shoppingDao;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
	
	public byte[] createCSVRepresentation(String authToken, String baseUrl, String shoppingId, String brandId, String storeId, Date dateFrom, Date dateTo, boolean onlyRepeated) throws ASException {
		
		// Get the Brand
		Brand brand = null;
		if( StringUtils.hasText(brandId))
			brand = brandDao.get(brandId, true);

		// Get the shopping
		Shopping shopping = null;
		if( StringUtils.hasText(shoppingId))
			shopping = shoppingDao.get(shoppingId, true);
		
		// Get the Stores
		Map<String, Store> storeCache = CollectionFactory.createMap();
		List<String> storeIds = CollectionFactory.createList();
		Store store = !StringUtils.hasText(storeId) ? null : storeDao.get(storeId, true);
		if( store != null ) {
			storeIds.add(store.getIdentifier());
			storeCache.put(store.getIdentifier(), store);
		} else {
			if( brand != null ) {
				List<Store> stores = storeDao.getUsingBrandAndStatus(brand.getIdentifier(), Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED}), null);
				for(Store obj : stores ) {
					if( StringUtils.hasText(obj.getExternalId())) {
						storeIds.add(obj.getIdentifier());
						storeCache.put(obj.getIdentifier(), obj);
					}
				}
			}
		}

		if( storeIds.isEmpty() && !StringUtils.hasText(shoppingId) ) 
			throw ASExceptionHelper.notAcceptedException();
		
		// Data Format ------------------------------------------------------------------------------------------------------

		try {

			StringBuffer sb = new StringBuffer();
			
			// Get Visits list
			List<APDVisit> list = null;
			if( StringUtils.hasText(shoppingId)) {
				Map<String, String> attributes = CollectionFactory.createMap();
				list = apdvDao.getUsingEntityIdAndEntityKindAndDate(shoppingId, EntityKind.KIND_SHOPPING, dateFrom, dateTo, APDVisit.CHECKIN_VISIT, null, attributes, false);
			} else {
				list = apdvDao.getUsingStoresAndDate(storeIds, dateFrom, dateTo, null, false); 
			}
			
			// First pass is to count mac address ocurrences
			Map<String, Long> macCountCache = CollectionFactory.createMap();
			for( APDVisit obj : list ) {
				String key = obj.getMac();
				Long val = macCountCache.get(key);
				if( null == val ) val = new Long(0);
				val = val +1;
				macCountCache.put(key, val);
			}
			
			// Second pass for report building
			for( APDVisit obj : list ) {
				Store st = storeCache.get(obj.getEntityId());
				if( null != obj ) {
					Long count = macCountCache.get(obj.getMac());
					if( !onlyRepeated || null != count && count > 1 ) {
						if( st != null ) {
							sb.append("\"").append(st.getExternalId()).append("\",");
						} else if(shopping != null){
							sb.append("\"").append(shopping.getName()).append("\",");
						}
						sb.append("\"").append(obj.getMac())
						.append("\",\"").append(sdf.format(obj.getCheckinStarted()))
						.append("\",\"").append(sdfTime.format(obj.getCheckinStarted()))
						.append("\",\"").append(getPermanence(obj.getCheckinStarted(), obj.getCheckinFinished()))
						.append("\"\n");
					}
				}
			}
			
			return sb.toString().getBytes();
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
	
	private long getPermanence(Date start, Date end) {
		if( null == start || null == end )
			return 0;
		
		return(long)((end.getTime() - start.getTime()) / 1000 / 60);
	}
}
