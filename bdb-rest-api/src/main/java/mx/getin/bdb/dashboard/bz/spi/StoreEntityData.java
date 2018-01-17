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
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.Range;
import mx.getin.model.interfaces.StoreDataByHourEntity;
import mx.getin.model.interfaces.StoreDataEntity;

public abstract class StoreEntityData<T extends StoreDataEntity> extends BDBRestBaseServerResource
		implements BDBDashboardBzService, BDBPostBzService {

	@Autowired
	protected StoreDAO storeDao;
	@Autowired
	protected DashboardAPDeviceMapperService mapper;
	@Autowired
	protected ImageDAO imageDao;
	@Autowired
	protected BrandDAO brandDao;
	
	protected final Calendar CALENDAR = Calendar.getInstance();
	
	protected static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	protected static final SimpleDateFormat hdf = new SimpleDateFormat("HH:mm");
	protected static final SimpleDateFormat mdf = new SimpleDateFormat("yyyy-MM");
	protected static final long ONE_DAY = 86400000;
	protected static final long ONE_HOUR = 3600000;
	protected static final Logger log = Logger.getLogger(StoreEntityData.class.getName());
	protected static final String PREVIEW_METHOD = "previewFileUpdate";
	protected static final String UPLOAD_METHOD = "doFileUpdate";
	protected static final String STORE_ID_PARAM = "storeId";
	protected static final String STORE_NAME_PARAM = "storeName";
	protected static final String FROM_DATE_PARAM = "fromDate";
	protected static final String ORIGINAL_PARAM = "orignal";
	protected static final String ERROR_PARAM = "error";
	protected static final String TO_DATE_PARAM = "toDate";
	protected static final String DATE_PARAM = "date";
	protected static final String FROM_HOUR_PARAM = "fromHour";
	protected static final String TO_HOUR_PARAM = "toHour";
	protected static final String METHOD_PARAM = "method";
	protected static final String PERIOD_PARAM = "period";
	protected static final String BRAND_ID_PARAM = "brandId";
	protected static final String IMAGE_ID_PARAM = "imageId";
	protected static final String IS_HOURLY_PARAM = "isHourly";
	protected static final String RESPONSE_DATA = "data";
	protected static final String RESPONSE_DATES = "dates";
	protected static final String RESPONSE_DATE_LIST = "dateList";
	protected static final String RESPONSE_STORE_LIST = "storeList";

	static {
		TimeZone gmt = TimeZone.getTimeZone("GMT");
		sdf.setTimeZone(gmt);
		hdf.setTimeZone(gmt);
		mdf.setTimeZone(gmt);
	}
	
	@Override
	public String retrieve() {
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			obtainUserIdentifier(true);
			
			String storeId = obtainStringValue(STORE_ID_PARAM, null);
			String fromDate = obtainStringValue(FROM_DATE_PARAM, null);
			String toDate = obtainStringValue(TO_DATE_PARAM, null);
			String toHour = null;
			if(!StringUtils.hasText(fromDate) || !StringUtils.hasText(toDate)) {
				fromDate = obtainStringValue(DATE_PARAM, null);
				toDate = obtainStringValue(FROM_HOUR_PARAM, null);
				toHour = obtainStringValue(TO_HOUR_PARAM, null);
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
			ret.put(STORE_ID_PARAM, storeId);
			if(parsingHourly) {
				ret.put(DATE_PARAM, fromDate);
				ret.put(FROM_HOUR_PARAM, fromDate);
				ret.put(TO_HOUR_PARAM, toHour);
			} else {
				ret.put(FROM_DATE_PARAM, fromDate);
				ret.put(TO_DATE_PARAM, toDate);
			}
			ret.put(RESPONSE_DATA, jsonArray);
			ret.put(RESPONSE_DATES, dateArray);
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
			if( json.has(METHOD_PARAM)) {
				method = json.getString(METHOD_PARAM);
			}

			// Scans the operations mode
			if(PREVIEW_METHOD.equals(method)) {

				// Means a preview file update was requested
				return previewFileUpdate(json);

			} else if(UPLOAD_METHOD.equals(method)) {

				// Means a file update was requested
				return doFileUpdate(json);

			} else {

				String storeId = json.getString(STORE_ID_PARAM);
				JSONArray arr = json.getJSONArray(RESPONSE_DATA);
				String fromDate = json.getString(FROM_DATE_PARAM);
				String toDate = json.getString(TO_DATE_PARAM);
				String date;
				boolean parsingHourly = StringUtils.isEmpty(fromDate) || StringUtils.isEmpty(toDate);
				if(parsingHourly) {
					fromDate = json.getString(FROM_HOUR_PARAM);
					toDate = json.getString(TO_HOUR_PARAM);
					date = json.getString(DATE_PARAM);
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
					StoreDataEntity obj;
					try {
						obj = daoGetUsingStoreIdAndDate(storeId, date, null);
						obj.setQty(total);
						daoUpdate(obj);
					} catch( Exception e ) {
						createStoreData(store, total, date, null);	
					}
				}
				
				mapper.createStoreItemDataForDates(parsingHourly ? date : fromDate,
						parsingHourly ? date : toDate, storeId, true);

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
	
	/**
	 * Prepares a preview for a ticket import file
	 * @param json
	 * @return
	 * @throws ASException
	 */
	public String previewFileUpdate(JSONObject json) throws ASException {
		try {

			String brandId = json.getString(BRAND_ID_PARAM);
			String period = json.getString(PERIOD_PARAM);
			String imageId = json.getString(IMAGE_ID_PARAM);
			
			Image image = imageDao.get(imageId);
			Brand brand = brandDao.get(brandId);
			Date month = mdf.parse(period); 

			JSONObject message = parseExcelDataFile(image, brand, month, json.getBoolean(IS_HOURLY_PARAM));
			message.put(BRAND_ID_PARAM, brandId);
			message.put(PERIOD_PARAM, period);
			message.put(IMAGE_ID_PARAM, imageId);
			
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
	
	/**
	 * Updates tickets based in a ticket input file
	 * @param json
	 * @return
	 * @throws ASException
	 */
	public String doFileUpdate(JSONObject json) throws ASException {
		try {
			Image image = imageDao.get(json.getString(IMAGE_ID_PARAM));
			Brand brand = brandDao.get(json.getString(BRAND_ID_PARAM));
			Date month = mdf.parse(json.getString(PERIOD_PARAM));
			boolean isHourly = json.getBoolean(IS_HOURLY_PARAM);
			
			JSONObject message = parseExcelDataFile(image, brand, month, isHourly);
			
			JSONArray jsonDateList = message.getJSONArray(RESPONSE_DATE_LIST);
			List<String> dateList = CollectionFactory.createList();
			for( int i = 0; i < jsonDateList.length(); i++)
				dateList.add(jsonDateList.getString(i));
			String fromDate;
			String toDate;
			if(isHourly) {
				fromDate = message.getString(DATE_PARAM);
				toDate = fromDate;
			} else {
				fromDate = dateList.get(0);
				CALENDAR.setTime(sdf.parse(dateList.get(dateList.size() -1)));
				CALENDAR.add(Calendar.DATE, 1);
				toDate = sdf.format(CALENDAR.getTime());
			}
			
			JSONArray jsonStoreList = message.getJSONArray(RESPONSE_DATA);
			for( int i = 0; i < jsonStoreList.length(); i++ ) {
				JSONObject entry = (JSONObject)jsonStoreList.get(i);
				if(!"null".equals(entry.getString(STORE_ID_PARAM))) {
					Store store = storeDao.get(entry.getString(STORE_ID_PARAM));
					JSONArray data = entry.getJSONArray(RESPONSE_DATA);

					for( int x = 0; x < dateList.size(); x++) {
						T obj;
						try {
							obj = daoGetUsingStoreIdAndDate(store.getIdentifier(),
									isHourly ? fromDate : dateList.get(x), isHourly ? dateList.get(x) : null);
							obj.setQty(data.getInt(x));
							daoUpdate(obj);
						} catch( Exception e ) {
							createStoreData(store, data.getInt(x), isHourly ? fromDate : dateList.get(x),
									isHourly ? dateList.get(x) : null);
						}
					}
					if(isHourly) {
						int total = 0;
						for(T st : daoGetUsingStoreIdAndDatesAndRange(store.getIdentifier(), fromDate,
								"00:00", "23:00", false)) {
							total += st.getQty();
						}
			
						// Creates or updates the daily record
						StoreDataEntity obj;
						try {
							obj = daoGetUsingStoreIdAndDate(store.getIdentifier(), fromDate, null);
							obj.setQty(total);
							daoUpdate(obj);
						} catch( Exception e ) {
							createStoreData(store, total, fromDate, null);	
						}
					}
					mapper.createStoreItemDataForDates(fromDate , isHourly ? fromDate : toDate,
							store.getIdentifier(), true);
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
	
	public JSONObject parseExcelDataFile(Image image, Brand brand, Date period, boolean isHourly)
			throws ASException {

		JSONObject json = new JSONObject();

		CALENDAR.setTime(period);
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

					CALENDAR.set(isHourly ? Calendar.HOUR_OF_DAY : Calendar.DATE,
							Integer.parseInt(hCell.getStringCellValue()));
					dateList.put(sdf.format(CALENDAR.getTime()));
				} else if (CellType.NUMERIC.equals(hCell.getCellTypeEnum())) {
					CALENDAR.set(isHourly ? Calendar.HOUR_OF_DAY : Calendar.DATE,
							(int)hCell.getNumericCellValue());
					dateList.put(sdf.format(CALENDAR.getTime()));
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
					additionalFields.put(BRAND_ID_PARAM, brand.getIdentifier());
					List<Store> stores = storeDao.getUsingIndex(storeName, null, StatusHelper.statusActive(),
							range, additionalFields, null, null);
					if(stores.isEmpty()) {
						storeName = storeName.replaceAll("a", "?").replaceAll("e", "?")
								.replaceAll("i", "?").replaceAll("o", "?").replaceAll("u", "?");
						stores = storeDao.getUsingIndex(storeName, null, StatusHelper.statusActive(), range,
								additionalFields, null, null);
					}
					JSONObject jsonObject = new JSONObject();
					JSONArray ticketsArray = new JSONArray();

					if(!stores.isEmpty()) {
						Store store = stores.get(0);

						jsonObject.put(STORE_ID_PARAM, store.getIdentifier());
						jsonObject.put(STORE_NAME_PARAM, store.getName());
						jsonObject.put(ORIGINAL_PARAM, storeName);
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

							jsonObject.put(RESPONSE_DATA, ticketsArray);
						}

					} else {

						jsonObject.put(STORE_ID_PARAM, "null");
						jsonObject.put(STORE_NAME_PARAM, "No encontrado!");
						jsonObject.put(ORIGINAL_PARAM, storeName);
						jsonObject.put(ERROR_PARAM, ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE);

						jsonObject.put(RESPONSE_DATA, ticketsArray);

					}
					storeList.put(jsonObject);

					rowIndex++;
					row = sheet.getRow(rowIndex);
				} catch( Exception e ) {
					log.log(Level.WARNING, e.getMessage(), e);
					row = null;
				}
			}

			json.put(RESPONSE_DATE_LIST, dateList);
			json.put(RESPONSE_STORE_LIST, storeList);

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
	
	/**
	 * Uses the proper DAO to retrieve a list of the store entities data.
	 * @param storeId - The store which data entities are desired.
	 * @param fromDate - The starting date to fetch; or the date to fetch when loading hourly entities.
	 * @param toDateOrFromHour - The final date to load or the starting hour to fetch data.
	 * @param toHour - The final hour to fetch data (only hourly entities).
	 * @param order - If the search must be ordered by time period.
	 * @return List&lt;T&gt; - A list with the query results.
	 * @throws ASException - If something goes wrong
	 */
	protected abstract List<T> daoGetUsingStoreIdAndDatesAndRange(String storeId, String fromDate,
			String toDateOrFromHour, String toHour, boolean order) throws ASException;
	
	/**
	 * Gets an specific store entity data for a given store for a single day (and hour).
	 * @param storeId - The store which data entities is desired.
	 * @param date - The specific date in format yyyy-MM-dd to fetch data from.
	 * @param hour - The specific hour in format HH:mm
	 * @return The specific data; hourly queries can return the super class form of their data.
	 * @throws ASException - If something goes wrong.
	 */
	protected abstract <V extends StoreDataEntity> V daoGetUsingStoreIdAndDate(String storeId, String date, String hour) throws ASException;
	
	/**
	 * Updates the given store entity data. Hourly implementations can update their super class
	 * data instance.
	 * @param obj - The object to create or update.
	 * @throws ASException - If something goes wrong.
	 */
	protected abstract void daoUpdate(StoreDataEntity obj) throws ASException;
	
	/**
	 * Builds an store data entity.
	 * @param store - The store to which the entity data belongs to.
	 * @param qty - The numeric value of the entity. 
	 * @param date - The date of the entity.
	 * @param hour - The hour of the entity.
	 * @throws ASException - If something goes wrong.
	 */
	protected abstract void createStoreData(Store store, double qty, String date, String hour)
			throws ASException;
	
}