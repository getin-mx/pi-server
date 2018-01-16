package mx.getin.bdb.dashboard.bz.spi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.springframework.util.StringUtils;

import com.ibm.icu.util.Calendar;
import com.inodes.util.CollectionFactory;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBPostBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.ImageDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.Image;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreItem;
import mobi.allshoppings.model.StoreRevenue;
import mobi.allshoppings.model.StoreTicket;
import mobi.allshoppings.model.StoreTicketByHour;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.Range;
import mx.getin.Constants;
import mx.getin.model.StoreItemByHour;
import mx.getin.model.StoreRevenueByHour;
import mx.getin.model.interfaces.StoreDataByHourEntity;
import mx.getin.model.interfaces.StoreDataEntity;

public abstract class StoreEntityData<T extends StoreDataEntity> extends BDBRestBaseServerResource
		implements BDBDashboardBzService, BDBPostBzService {

	@Autowired
	protected StoreDAO storeDao;
	@Autowired
	private DashboardAPDeviceMapperService mapper;
	@Autowired
	private ImageDAO imageDao;
	@Autowired
	private BrandDAO brandDao;
	
	protected static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	protected static final SimpleDateFormat hdf = new SimpleDateFormat("HH:mm");
	protected static final long ONE_DAY = 86400000;
	protected static final long ONE_HOUR = 3600000;
	protected static final Logger log = Logger.getLogger(StoreEntityData.class.getName());
	protected static final String PREVIEW_METHOD = "previewFileUpdate";
	protected static final String UPLOAD_METHOD = "doFileUpdate";

	static {
		TimeZone gmt = TimeZone.getTimeZone("GMT");
		sdf.setTimeZone(gmt);
		hdf.setTimeZone(gmt);
	}
	
	@Override
	public String retrieve() {
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			obtainUserIdentifier(true);
			
			String storeId = obtainStringValue("storeId", null);
			String fromDate = obtainStringValue("fromDate", null);
			String toDate = obtainStringValue("toDate", null);
			String toHour = null;
			if(!StringUtils.hasText(fromDate) || !StringUtils.hasText(toDate)) {
				fromDate = obtainStringValue("date", null);
				toDate = obtainStringValue("fromHour", null);
				toHour = obtainStringValue("toHour", null);
			}

			boolean parsingHourly = StringUtils.hasText(toHour);
			
			List<T> list = daoGetUsingStoreIdAndDatesAndRange(storeId, fromDate, toDate, toHour, true);
			Map<String, T> tmp = CollectionFactory.createMap();
			for( T obj : list ) {
				if(obj instanceof StoreDataByHourEntity) {
					tmp.put(((StoreDataByHourEntity) obj).getHour(), obj);
				} else /*if(obj instanceof StoreDataEntity)*/ {
					tmp.put(obj.getDate(), obj);
				}
			}
			
			Date curDate = sdf.parse(fromDate);
			byte curHour = (byte) (parsingHourly ? Integer.parseInt(toDate.substring(0, 2)) : 0);
			Date limitDate = parsingHourly ? new Date(curDate.getTime()
					+Integer.parseInt(toHour.substring(0, 2)) *60 *60 *1000) : sdf.parse(toDate);
			if(parsingHourly) curDate.setTime(curDate.getTime() +curHour *60 *60 *1000);
			JSONArray jsonArray = new JSONArray();
			JSONArray dateArray = new JSONArray();
			String parsedDate;
			while( curDate.before(limitDate) || curDate.equals(limitDate) ) {
				parsedDate = parsingHourly ? hdf.format(curDate) : sdf.format(curDate);
				dateArray.put(parsingHourly ? String.format("%02d:00", curHour) : parsedDate);
				T obj = tmp.get(parsedDate);
				jsonArray.put(obj == null ? 0 : obj.getQty());
				curHour++;
				curDate.setTime(curDate.getTime() +(parsingHourly ? ONE_HOUR : ONE_DAY));
			}
			
			// Returns the final value
			JSONObject ret = new JSONObject();
			ret.put("storeId", storeId);
			if(parsingHourly) {
				ret.put("date", fromDate);
				ret.put("fromHour", fromDate);
				ret.put("toHour", toHour);
			} else {
				ret.put("fromDate", fromDate);
				ret.put("toDate", toDate);
			}
			ret.put("data", jsonArray);
			ret.put("dates", dateArray);
			return ret.toString();

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
	
	@Override
	public String change(JsonRepresentation entity) {
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			obtainUserIdentifier(true);

			JSONObject json = entity.getJsonObject();
			String method = new String();
			if( json.has("method")) {
				method = json.getString("method");
			}

			// Scans the operations mode
			if(PREVIEW_METHOD.equals(method)) {

				// Means a preview file update was requested
				return previewFileUpdate(json);

			} else if(UPLOAD_METHOD.equals(method)) {

				// Means a file update was requested
				return doFileUpdate(json);

			} else {

				String storeId = json.getString("storeId");
				JSONArray arr = json.getJSONArray("data");
				String fromDate = json.getString("fromDate");
				String toDate = json.getString("toDate");
				String date;
				boolean parsingHourly = StringUtils.isEmpty(fromDate) || StringUtils.isEmpty(toDate);
				if(parsingHourly) {
					fromDate = json.getString("fromHour");
					toDate = json.getString("toHour");
					date = json.getString("date");
				} else date = null;

				Store store = storeDao.get(storeId, true);

				Date curDate = parsingHourly ? hdf.parse(fromDate) : sdf.parse(fromDate);
				Date limitDate = parsingHourly ? hdf.parse(toDate) : sdf.parse(toDate);
				int i = 0;
				while( curDate.before(limitDate) || curDate.equals(limitDate) ) {
					int qty = arr.getInt(i);
					T obj;
					String parsedData = parsingHourly ?  hdf.format(curDate) : sdf.format(curDate);
					try {
						obj = daoGetUsingStoreIdAndDate(storeId, parsingHourly ? date : parsedData,
								parsingHourly ? parsedData : null);
						obj.setQty(qty);
						daoUpdate(obj);
					} catch( Exception e ) {
						createStoreData(store, qty, parsingHourly ? date : parsedData,
								parsingHourly ? parsedData : null);
					}
					i++;
					curDate.setTime(curDate.getTime() +(parsingHourly ? ONE_DAY : ONE_HOUR));
				}
				if(parsingHourly) {
					int total = 0;
					for(T st : daoGetUsingStoreIdAndDatesAndRange(storeId, date, "00:00", "23:00", false)) {
						total += st.getQty();
					}
		
					// Creates or updates the daily record
					T obj;
					try {
						obj = daoGetUsingStoreIdAndDate(storeId, date, null);
						obj.setQty(total);
						stDao.update(obj);
					} catch( Exception e ) {
						obj = new StoreTicket();
						obj.setStoreId(storeId);
						obj.setBrandId(store.getBrandId());
						obj.setDate(date);
						obj.setQty(total);
						obj.setKey(stDao.createKey());
						stDao.create(obj);
					}
				}
				
				mapper.createStoreItemDataForDates(fromDate, toDate, storeId, true);

				return generateJSONOkResponse().toString();
			}
			
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
	
	public String previewFileUpdate(JSONObject json) throws ASException {
		try {

			String brandId = json.getString("brandId");
			String period = json.getString("period");
			String imageId = json.getString("imageId");
			
			Image image = imageDao.get(imageId);
			Brand brand = brandDao.get(brandId);
			Date month = mdf.parse(period); 

			JSONObject message = parseExcelTicketFile(image, brand, month);
			message.put("brandId", brandId);
			message.put("period", period);
			message.put("imageId", imageId);
			
			return message.toString();

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
		}		
	}
	
	public String doFileUpdate(JSONObject json) throws ASException {
		try {

			String brandId = json.getString("brandId");
			String period = json.getString("period");
			String imageId = json.getString("imageId");
			
			Image image = imageDao.get(imageId);
			Brand brand = brandDao.get(brandId);
			Date month = mdf.parse(period); 
			
			JSONObject message = parseExcelTicketFile(image, brand, month);
			
			JSONArray jsonDateList = message.getJSONArray("dateList");
			List<String> dateList = CollectionFactory.createList();
			for( int i = 0; i < jsonDateList.length(); i++)
				dateList.add(jsonDateList.getString(i));
			String fromDate = dateList.get(0);
			String toDate = dateList.get(dateList.size() -1);
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(toDate));
			cal.add(Calendar.DATE, 1);
			toDate = sdf.format(cal.getTime());
			
			JSONArray jsonStoreList = message.getJSONArray("storeList");
			for( int i = 0; i < jsonStoreList.length(); i++ ) {
				JSONObject entry = (JSONObject)jsonStoreList.get(i);
				if(!"null".equals(entry.getString("storeId"))) {
					Store store = storeDao.get(entry.getString("storeId"));
					JSONArray data = entry.getJSONArray("tickets");

					for( int x = 0; x < dateList.size(); x++) {
						StoreItem obj;
						try {
							obj = dao.getUsingStoreIdAndDate(store.getIdentifier(), dateList.get(x), true);
							obj.setQty(data.getInt(x));
							dao.update(obj);
						} catch( Exception e ) {
							obj = new StoreItem();
							obj.setStoreId(store.getIdentifier());
							obj.setBrandId(store.getBrandId());
							obj.setDate(dateList.get(x));
							obj.setQty(data.getInt(x));
							obj.setKey(dao.createKey());
							dao.create(obj);
						}
					}
					mapper.createStoreItemDataForDates(fromDate, toDate, store.getIdentifier(), true);
				}
			}
			
			imageDao.delete(image.getIdentifier());

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
		}		
		return generateJSONOkResponse().toString();	
	}
	
	public JSONObject parseExcelTicketFile(Image image, Brand brand, Date period) throws ASException {

		JSONObject json = new JSONObject();

		Calendar cal = Calendar.getInstance();
		cal.setTime(period);
		Range range = new Range(0,1);

		InputStream excelFile = null;
		Workbook workbook = null;

		try {
			excelFile = new ByteArrayInputStream(image.getContents().getBytes());
			workbook = new XSSFWorkbook(excelFile);
			Sheet sheet = workbook.getSheetAt(0);

			int rowIndex = 2;
			int colIndex = 2;
			int minColIndex = 2;
			int maxColIndex = 0;

			JSONArray dateList = new JSONArray();
			JSONArray storeList = new JSONArray();

			Row headers = sheet.getRow(rowIndex);
			Cell hCell = headers.getCell(colIndex);
			while( hCell != null ) {

				if (CellType.STRING.equals(hCell.getCellTypeEnum())) {
					if(hCell.getStringCellValue().equalsIgnoreCase("TOTAL"))
						break;
					if(hCell.getStringCellValue().equalsIgnoreCase("TOTALES"))
						break;
					if(hCell.getStringCellValue().startsWith("T"))
						break;
					if(hCell.getStringCellValue().startsWith("t"))
						break;

					Integer num = Integer.parseInt(hCell.getStringCellValue());
					cal.set(Calendar.DATE, num);
					dateList.put(sdf.format(cal.getTime()));
				} else if (CellType.NUMERIC.equals(hCell.getCellTypeEnum())) {
					Integer num = (int)hCell.getNumericCellValue();
					cal.set(Calendar.DATE, num);
					dateList.put(sdf.format(cal.getTime()));
				}

				colIndex++;
				maxColIndex = colIndex;
				hCell = headers.getCell(colIndex);
			}
			rowIndex++;

			Row row = sheet.getRow(rowIndex);
			while( row != null ) {
				try {
					String storeName = row.getCell(1).getStringCellValue();
					Map<String, String> additionalFields = CollectionFactory.createMap();
					additionalFields.put("brandId", brand.getIdentifier());
					String adaptedStoreName = new String(storeName);
					adaptedStoreName = adaptedStoreName.replaceAll("a", "?");
					adaptedStoreName = adaptedStoreName.replaceAll("e", "?");
					adaptedStoreName = adaptedStoreName.replaceAll("i", "?");
					adaptedStoreName = adaptedStoreName.replaceAll("o", "?");
					adaptedStoreName = adaptedStoreName.replaceAll("u", "?");
					List<Store> stores = storeDao.getUsingIndex(adaptedStoreName, null, StatusHelper.statusActive(), range, additionalFields, null, null);

					JSONObject jsonObject = new JSONObject();
					JSONArray ticketsArray = new JSONArray();

					if(!stores.isEmpty()) {
						Store store = stores.get(0);

						jsonObject.put("storeId", store.getIdentifier());
						jsonObject.put("storeName", store.getName());
						jsonObject.put("original", storeName);
						jsonObject.put("error", "");

						for( colIndex = minColIndex; colIndex < maxColIndex; colIndex++ ) {
							hCell = row.getCell(colIndex);
							if( hCell != null ) {

								if (CellType.STRING.equals(hCell.getCellTypeEnum())) {
									Integer num = Integer.parseInt(hCell.getStringCellValue());
									ticketsArray.put(num);
								} else if (CellType.NUMERIC.equals(hCell.getCellTypeEnum())) {
									Integer num = (int)hCell.getNumericCellValue();
									ticketsArray.put(num);
								}
							}

							jsonObject.put("tickets", ticketsArray);
						}

					} else {

						jsonObject.put("storeId", "null");
						jsonObject.put("storeName", "No encontrado!");
						jsonObject.put("original", storeName);
						jsonObject.put("error", ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE);

						jsonObject.put("tickets", ticketsArray);

					}
					storeList.put(jsonObject);

					rowIndex++;
					row = sheet.getRow(rowIndex);
				} catch( Exception e ) {
					log.log(Level.WARNING, e.getMessage(), e);
					row = null;
				}
			}

			json.put("dateList", dateList);
			json.put("storeList", storeList);

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			try {
				excelFile.close();
				workbook.close();
			} catch( Exception e1 ) {
				log.log(Level.SEVERE, e1.getMessage(), e1);
			}
		}
		return json;
	}
	
	protected abstract List<T> daoGetUsingStoreIdAndDatesAndRange(String storeId, String fromDate,
			String toDateOrFromHour, String toHour, boolean order) throws ASException;
	
	protected abstract T daoGetUsingStoreIdAndDate(String storeId, String date, String hour) throws ASException;
	
	protected abstract void daoUpdate(T obj) throws ASException;
	
	protected abstract void createStoreData(Store store, double qty, String date, String hour)
			throws ASException;
	
}