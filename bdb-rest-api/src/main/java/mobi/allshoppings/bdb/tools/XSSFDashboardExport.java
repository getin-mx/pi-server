package mobi.allshoppings.bdb.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bdb.dashboard.bz.spi.BrandExportServlet;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.exporter.impl.ExcelExportHelperImpl;
import mobi.allshoppings.mail.MailHelper;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;

public class XSSFDashboardExport {

	@Autowired
	private BrandDAO brandDao;
	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private UserDAO userDao;
	@Autowired
	private MailHelper mailHelper;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
	
	public byte[] createXSSFBrandDashboardRepresentation(String authToken, String baseUrl, String brandId, String storeId, Date dateFrom, Date dateTo) throws ASException {
		
		// Get the Brand
		Brand brand = brandDao.get(brandId, true);
		
		// Get the Store
		Store store = !StringUtils.hasText(storeId) ? null : storeDao.get(storeId, true);
		
		User user = userDao.getByAuthToken(authToken);

		// Data Acquisition ---------------------------------------------------------------------------------------------
		// Table --------------------------------------------------------------------------------------------------------
		String tableUrl = baseUrl + "dashboard/brandTableData?authToken=" + authToken + "&entityId=" + brandId
				+ "&entityKind=1" + "&fromStringDate=" + sdf.format(dateFrom) + "&toStringDate=" + sdf.format(dateTo)
				+ "&onlyExternalIds=true";
		String tableString = get(tableUrl);
		JSONArray tableJson = new JSONArray(tableString);
		
//		System.out.println(tableJson);

		// Daily Visits -------------------------------------------------------------------------------------------------
		String dailyUrl = baseUrl + "dashboard/timelineData?authToken=" + authToken + "&entityId=" + brandId + "&entityKind=1"
				+ (StringUtils.hasText(storeId) ? "&subentityId=" + storeId : "")
				+ "&elementId=apd_visitor&subIdOrder=visitor_total_peasents,visitor_total_visits,visitor_total_peasents_ios,"
				+ "visitor_total_peasents_android,visitor_total_visits_ios,visitor_total_visits_android,visitor_total_tickets,visitor_total_items,visitor_total_revenue"
				+ "&fromStringDate=" + sdf.format(dateFrom) + "&toStringDate=" + sdf.format(dateTo)
				+ "&eraseBlanks=false";
		String dailyString = get(dailyUrl);
		JSONObject dailyJson = new JSONObject(dailyString);		
		
//		System.out.println(dailyJson);
		
		// Hourly Visits -------------------------------------------------------------------------------------------------
		String trafHourUrl = baseUrl + "dashboard/timelineHour?authToken=" + authToken + "&entityId=" + brandId + "&entityKind=1"
				+ (StringUtils.hasText(storeId) ? "&subentityId=" + storeId : "")
				+ "&elementId=apd_permanence&subIdOrder=permanence_hourly_peasents,permanence_hourly_visits,"
				+ "permanence_hourly_peasents_ios,permanence_hourly_peasents_android,permanence_hourly_visits_ios,permanence_hourly_visits_android"
				+ "&fromStringDate=" + sdf.format(dateFrom) + "&toStringDate=" + sdf.format(dateTo)
				+ "&average=true&toMinutes=true&eraseBlanks=true";
		String trafHourString = get(trafHourUrl);
		JSONObject trafHourJson = new JSONObject(trafHourString);		
		
//		System.out.println(trafHourJson);
		
		// Daily Permanence -------------------------------------------------------------------------------------------------
		String permHourUrl = baseUrl + "dashboard/timelineHour?authToken=" + authToken + "&entityId=" + brandId + "&entityKind=1"
				+ (StringUtils.hasText(storeId) ? "&subentityId=" + storeId : "")
				+ "&elementId=apd_visitor&subIdOrder=visitor_total_peasents,visitor_total_visits,visitor_total_peasents_ios,"
				+ "visitor_total_peasents_android,visitor_total_visits_ios,visitor_total_visits_android"
				+ "&fromStringDate=" + sdf.format(dateFrom) + "&toStringDate=" + sdf.format(dateTo)
				+ "&eraseBlanks=true";
		String permHourString = get(permHourUrl);
		JSONObject permHourJson = new JSONObject(permHourString);		
		
//		System.out.println(permHourJson);

		// Data Format ------------------------------------------------------------------------------------------------------

		try {
			@SuppressWarnings("resource")
			HSSFWorkbook wb = new HSSFWorkbook();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
		    HSSFFont defaultFont= wb.createFont();
		    defaultFont.setFontHeightInPoints((short)10);
		    defaultFont.setFontName("Arial");
		    defaultFont.setColor(IndexedColors.BLACK.getIndex());
		    defaultFont.setBold(false);
		    defaultFont.setItalic(false);

		    HSSFFont font = wb.createFont();
		    font.setFontHeightInPoints((short)10);
		    font.setFontName("Arial");
		    font.setColor(IndexedColors.BLACK.getIndex());
		    font.setBold(true);
		    font.setItalic(false);
			
		    HSSFCellStyle bold = wb.createCellStyle();
		    bold.setFont(font);

		    // General Table ---------------------------------------------------------------------------------------------------------------
			HSSFSheet sheet = wb.createSheet("General");
			int rowId = 0;
			
			HSSFRow row = sheet.createRow(rowId++);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue("Reporte de Cadena " + brand.getName() + (store != null ? " - " + store.getName() : "")
					+ " del dia " + sdf2.format(dateFrom) + " al dia " + sdf2.format(dateTo));
			cell.setCellStyle(bold);
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,8));
			
			row = sheet.createRow(rowId++);
			
			for( int i = 0; i < tableJson.length(); i++) {
				JSONArray jsonRow = tableJson.getJSONArray(i);
				row = sheet.createRow(rowId++);
				for( int x = 0; x < jsonRow.length(); x++ ) {
					cell = row.createCell(x);
					cell.setCellValue(jsonRow.getString(x));
					if( i == 0 || i == (tableJson.length() -1)) {
						cell.setCellStyle(bold);
					}
				}
			}
			
			for( int i = 0; i < 9; i ++ ) {
				sheet.autoSizeColumn(i);
			}
			
		    // Traffic ---------------------------------------------------------------------------------------------------------------
			sheet = wb.createSheet("Trafico por Dia");
			rowId = 0;
			
			row = sheet.createRow(rowId++);
			cell = row.createCell(0);
			cell.setCellValue("Reporte de Cadena " + brand.getName() + (store != null ? " - " + store.getName() : "")
					+ " del dia " + sdf2.format(dateFrom) + " al dia " + sdf2.format(dateTo));
			cell.setCellStyle(bold);
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,8));
			
			row = sheet.createRow(rowId++);
			
			JSONArray categories = dailyJson.getJSONArray("categories");
			JSONArray series = dailyJson.getJSONArray("series");
			row = sheet.createRow(rowId++);
			cell = row.createCell(0);
			cell.setCellValue("Dia");
			cell.setCellStyle(bold);
			for( int x = 0; x < series.length(); x++) {
				if(series.getJSONObject(x).has("name")) {
					cell = row.createCell(x+1);
					cell.setCellValue(series.getJSONObject(x).getString("name"));
					cell.setCellStyle(bold);
				}
			}

			for( int i = 0; i < categories.length(); i++) {
				row = sheet.createRow(rowId++);
				cell = row.createCell(0);
				cell.setCellValue(categories.getString(i));
				for( int x = 0; x < series.length(); x++) {
					if(series.getJSONObject(x).has("name")) {
						cell = row.createCell(x+1);
						cell.setCellValue(series.getJSONObject(x).getJSONArray("data").getDouble(i));
					}
				}
			}
			
			for( int i = 0; i < 9; i ++ ) {
				sheet.autoSizeColumn(i);
			}

			// Visits per Hour ---------------------------------------------------------------------------------------------------------------
			sheet = wb.createSheet("Trafico por Hora");
			rowId = 0;
			
			row = sheet.createRow(rowId++);
			cell = row.createCell(0);
			cell.setCellValue("Reporte de Cadena " + brand.getName() + (store != null ? " - " + store.getName() : "")
					+ " del dia " + sdf2.format(dateFrom) + " al dia " + sdf2.format(dateTo));
			cell.setCellStyle(bold);
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,8));
			
			row = sheet.createRow(rowId++);
			
			categories = permHourJson.getJSONArray("categories");
			series = permHourJson.getJSONArray("series");
			row = sheet.createRow(rowId++);
			cell = row.createCell(0);
			cell.setCellValue("Hora");
			cell.setCellStyle(bold);
			for( int x = 0; x < series.length(); x++) {
				if(series.getJSONObject(x).has("name")) {
					cell = row.createCell(x+1);
					cell.setCellValue(series.getJSONObject(x).getString("name"));
					cell.setCellStyle(bold);
				}
			}

			for( int i = 0; i < categories.length(); i++) {
				row = sheet.createRow(rowId++);
				cell = row.createCell(0);
				cell.setCellValue(categories.getString(i));
				for( int x = 0; x < series.length(); x++) {
					if(series.getJSONObject(x).has("name")) {
						cell = row.createCell(x+1);
						cell.setCellValue(series.getJSONObject(x).getJSONArray("data").getInt(i));
					}
				}
			}
			
			for( int i = 0; i < 9; i ++ ) {
				sheet.autoSizeColumn(i);
			}
			
			// Permanence ---------------------------------------------------------------------------------------------------------------
			sheet = wb.createSheet("Permanencia Promedio");
			rowId = 0;
			
			row = sheet.createRow(rowId++);
			cell = row.createCell(0);
			cell.setCellValue("Reporte de Cadena " + brand.getName() + (store != null ? " - " + store.getName() : "")
					+ " del dia " + sdf2.format(dateFrom) + " al dia " + sdf2.format(dateTo));
			cell.setCellStyle(bold);
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,8));
			
			row = sheet.createRow(rowId++);
			
			categories = trafHourJson.getJSONArray("categories");
			series = trafHourJson.getJSONArray("series");
			row = sheet.createRow(rowId++);
			cell = row.createCell(0);
			cell.setCellValue("Hora");
			cell.setCellStyle(bold);
			for( int x = 0; x < series.length(); x++) {
				if(series.getJSONObject(x).has("name")) {
					cell = row.createCell(x+1);
					cell.setCellValue(series.getJSONObject(x).getString("name"));
					cell.setCellStyle(bold);
				}
			}

			for( int i = 0; i < categories.length(); i++) {
				row = sheet.createRow(rowId++);
				cell = row.createCell(0);
				cell.setCellValue(categories.getString(i));
				for( int x = 0; x < series.length(); x++) {
					if(series.getJSONObject(x).has("name")) {
						cell = row.createCell(x+1);
						cell.setCellValue(series.getJSONObject(x).getJSONArray("data").getInt(i));
					}
				}
			}
			
			for( int i = 0; i < 9; i ++ ) {
				sheet.autoSizeColumn(i);
			}
			
			wb.write(bos);
			
			File tmp = File.createTempFile(StringUtils.hasText(brandId) ?
					brandId : storeId, ".xlsx");
			FileOutputStream fos = new FileOutputStream(tmp);
			wb.write(fos);
			fos.flush();
			fos.close();
			
			bos.close();
			
			ExcelExportHelperImpl.sendReportMail(mailHelper,
					user, tmp, Logger.getLogger(
							BrandExportServlet.class
							.getSimpleName()));
			
			return bos.toByteArray();
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
	
	private String get(String url) throws ASException {
		try {
			URL burl = new URL(url);
			byte[] bContents = null;
			int count = 5;
			while(( bContents == null || bContents.length == 0 ) && count > 0 ) {
				if( count < 5 ) try {Thread.sleep(500);}catch(Exception e1){}
				bContents = IOUtils.toByteArray(burl.openStream());
				count--;
			}
			String ret = new String(bContents);
			return ret;
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
	
}