package mobi.allshoppings.exporter.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.google.common.io.Files;

import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.StoreItemDAO;
import mobi.allshoppings.dao.StoreRevenueDAO;
import mobi.allshoppings.dao.StoreTicketDAO;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.exporter.ExcelExportHelper;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreItem;
import mobi.allshoppings.model.StoreRevenue;
import mobi.allshoppings.model.StoreTicket;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.tools.CollectionFactory;

public class ExcelExportHelperImpl implements ExcelExportHelper {

	private static final Logger log = Logger.getLogger(ExcelExportHelperImpl.class.getName());
	
	static final DecimalFormat DF = new DecimalFormat("00");
	
	@Autowired
	DashboardIndicatorDataDAO didDao;
	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private StoreRevenueDAO sRevenueDao;
	@Autowired
	private StoreItemDAO sItemDao;
	@Autowired
	private StoreTicketDAO sTicketDao;
	
	private static final SimpleDateFormat year = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat month = new SimpleDateFormat("MM");
	private static final int DAY_IN_MILLIS = 86400000;
	
	private static final byte DATETIME_CELL_INDEX = 0;
	private static final byte MONTH_CELL_INDEX = 1;
	private static final byte WEEK_OF_YEAR_INDEX = 2;
	private static final byte DAY_OF_WEEK_INDEX = 3;
	private static final byte PEASENTS_CELL_INDEX = 4;
	private static final byte VISITS_CELL_INDEX = 5;
	private static final byte UPTIME_CELL_INDEX = 6;
	private static final byte STORE_NAME_CELL_INDEX = 7;
	
	private static final byte HOUR_CELL_INDEX = 4;
	
	private static final String CHECKIN_DATETIME_CELL_TITLE = "checkinDate";
	private static final String CHECKIN_DURATION_CELL_TITLE = "checkingDurationSeconds";
	private static final String DEVICE_CELL_TITLE = "devicePlatform";
	private static final String REVENUE_CELL_TITLE = "revenue";
	private static final String ITEMS_CELL_TITLE = "item";
	private static final String TICKETS_CELL_TITLE = "tickets";
	private static final String DATA_DATE_CELL_TITLE = "dataDate";
	
	@SuppressWarnings("deprecation")
	@Override
	public byte[] export(String storeId, String fromDate, String toDate, int weeks, String outDir)
			throws ASException {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			final Store store = storeDao.get(storeId, false);
			final String brandId = store.getBrandId();
			final String storeName = store.getName();

			// Defines working variables
			Map<String, TrafficEntry> trafficMap = CollectionFactory.createMap();
			Map<String, HourEntry> hourMap = CollectionFactory.createMap();
			Map<String, PermanenceEntry> permanenceMap = CollectionFactory.createMap();
			Map<String, DateAndHourEntry> dateAndHourMap = CollectionFactory.createMap();
			Date initialDate = sdf.parse(fromDate);
			Date finalDate = sdf.parse(toDate);
			Date curDate = new Date(initialDate.getTime());
			String dateName = getStringDate(finalDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(finalDate);
			cal.add(Calendar.DATE, -(weeks*7));
			cal.add(Calendar.DATE, +1);
			Date limitDate = cal.getTime();

			log.log(Level.INFO, "Processing store " + storeName + "...");

			// Creates the data map
			while(curDate.compareTo(finalDate) < 1) {
				TrafficEntry e = new TrafficEntry();
				e.setUnformmatedDate(curDate);
				trafficMap.put(sdf.format(curDate), e);
				curDate.setTime(curDate.getTime() + DAY_IN_MILLIS);
			}

			// Creates the data map
			for(int j = 0; j < 24; j++ ) {
				HourEntry e = new HourEntry();
				e.setUnformattedHour(j);
				hourMap.put(e.getHour(), e);
			}

			// Creates the data map
			for(int j = 0; j < 24; j++ ) {
				PermanenceEntry e = new PermanenceEntry();
				e.setUnformattedHour(j);
				permanenceMap.put(e.getHour(), e);
			}

			// Creates the data map
			for(int j = 0; j < 24; j++ ) {
				for( int k = 1; k < 8; k++ ) {
					DateAndHourEntry e = new DateAndHourEntry();
					e.setDay(k);
					e.setHour(j);
					dateAndHourMap.put(e.getKey(), e);
				}
			}

			String parsedInitD = sdf.format(initialDate);
			String parsedFinalD = sdf.format(finalDate); 
			
			// Now iterates the Dashboard Indicator Data List for Traffic by Day graph 
			List<DashboardIndicatorData> list = didDao.getUsingFilters(brandId,
					EntityKind.KIND_BRAND, Arrays.asList("apd_visitor"), Arrays.asList(
							"visitor_total_peasents", "visitor_total_visits",
							"visitor_total_tickets"), null, storeId, "D", parsedInitD,
					parsedFinalD, null, null, null, null, null, null, null, null);

			log.log(Level.INFO, "Using " + list.size() + " elements...");

			Iterator<DashboardIndicatorData> i = list.iterator();
			while(i.hasNext()) {
				DashboardIndicatorData obj = i.next();
				TrafficEntry e = trafficMap.get(obj.getStringDate());
				try {
					if( "visitor_total_peasents".equals(obj.getElementSubId())) {
						e.setPeasants(e.getPeasants() + obj.getDoubleValue().longValue());
					} else if( "visitor_total_visits".equals(obj.getElementSubId())) {
						e.setVisits(e.getVisits() + obj.getDoubleValue().longValue());
					} else if( "visitor_total_tickets".equals(obj.getElementSubId())) {
						e.setTickets(e.getTickets() + obj.getDoubleValue().longValue());
					}
				} catch( Exception e1 ) {
					e1.printStackTrace();
				}
				trafficMap.put(obj.getStringDate(), e);
			}

			String parsedLimitD = sdf.format(limitDate);
			
			// How iterates the Dashboard Indicator Data List for Traffic by Hour graph
			list = didDao.getUsingFilters(brandId, EntityKind.KIND_BRAND, Arrays.asList("apd_visitor"),
					Arrays.asList("visitor_total_peasents", "visitor_total_visits"), null, storeId, "D",
					parsedLimitD, parsedFinalD, null, null, null, null, null, null, null, null);

			log.log(Level.INFO, "Using " + list.size() + " elements...");

			i = list.iterator();
			while(i.hasNext()) {
				DashboardIndicatorData obj = i.next();
				HourEntry e = hourMap.get(HourEntry.calculateHour(obj.getTimeZone()));
				if( "visitor_total_peasents".equals(obj.getElementSubId())) {
					e.setPeasants(e.getPeasants() + obj.getDoubleValue().longValue());
				} else if( "visitor_total_visits".equals(obj.getElementSubId())) {
					e.setVisits(e.getVisits() + obj.getDoubleValue().longValue());

					DateAndHourEntry e2 = dateAndHourMap.get(DateAndHourEntry.getKey(obj.getDate(),
							obj.getTimeZone()));
					e2.setVisits(e2.getVisits() + obj.getDoubleValue().longValue());
					dateAndHourMap.put(e2.getKey(), e2);

				}
				hourMap.put(e.getHour(), e);
			}

			// Now iterates the Dashboard Indicator Data List for Permanence Graph 
			list = didDao.getUsingFilters(brandId, EntityKind.KIND_BRAND, Arrays.asList("apd_permanence"),
					Arrays.asList("permanence_hourly_peasents", "permanence_hourly_visits"), null, storeId,
					"D", parsedLimitD, parsedFinalD, null, null, null, null, null, null,
					null, null);

			log.log(Level.INFO, "Using " + list.size() + " elements...");

			long totalVisitsPermanence = 0;
			long totalVisitsCount = 0;
			i = list.iterator();
			while(i.hasNext()) {
				DashboardIndicatorData obj = i.next();
				System.out.println(obj.getStringDate());
				PermanenceEntry e = permanenceMap.get(PermanenceEntry.calculateHour(obj.getTimeZone()));
				if( "permanence_hourly_peasents".equals(obj.getElementSubId())) {
					e.setPeasants(e.getPeasants() + obj.getDoubleValue().longValue());
					e.setPeasantsCount(e.getPeasantsCount() + obj.getRecordCount());
				} else if( "permanence_hourly_visits".equals(obj.getElementSubId())) {
					e.setVisits(e.getVisits() + obj.getDoubleValue().longValue());
					e.setVisitsCount(e.getVisitsCount() + obj.getRecordCount());
					totalVisitsPermanence += obj.getDoubleValue().longValue();
					totalVisitsCount += obj.getRecordCount();
				}
				permanenceMap.put(e.getHour(), e);
			}
			
			// Opens the template
			ZipSecureFile.setMinInflateRatio(0);

			String filename = resolveDumpFileName(outDir, storeName, finalDate);
			File dir = new File(filename).getParentFile();
			if( !dir.exists() ) dir.mkdirs();
			Files.copy(
					new File(systemConfiguration.getResourcesDir() + File.separator
							+ systemConfiguration.getExcelTemplate()),
					new File(filename));
			XSSFWorkbook workbook = new XSSFWorkbook(filename);
			XSSFSheet trafficByDay = workbook.getSheet("Trafico por Dia");
			XSSFSheet trafficByHour = workbook.getSheet("Trafico por Hora");
			XSSFSheet permanence = workbook.getSheet("Permanencia");
			XSSFSheet highHours = workbook.getSheet("Horas Pico");
			XSSFSheet deadHours = workbook.getSheet("Horas Muertas");
			XSSFSheet formulae = workbook.getSheet("Formulae");
			XSSFSheet print = workbook.getSheet("Impresión PDF");

			// Gets the model cells
			CellCopyPolicy policy = new CellCopyPolicy();
			policy.setCopyCellFormula(true);
			policy.setCopyCellStyle(true);

			List<XSSFCell> modelTrafficByDay1 = CollectionFactory.createList();
			List<XSSFCell> modelTrafficByDay2 = CollectionFactory.createList();
			List<XSSFCell> modelTrafficByHour = CollectionFactory.createList();
			List<XSSFCell> modelPermanence = CollectionFactory.createList();
			List<XSSFCell> modelHour = CollectionFactory.createList();

			// Gets the model cells
			Iterator<Row> rows = formulae.iterator();
			while(rows.hasNext()) {
				XSSFRow row = (XSSFRow)rows.next();
				getTemplateRow(modelTrafficByDay1, row, "Trafico por Dia 1");
				getTemplateRow(modelTrafficByDay2, row, "Trafico por Dia 2");
				getTemplateRow(modelTrafficByHour, row, "Trafico por Hora");
				getTemplateRow(modelPermanence, row, "Permanencia");
				getTemplateRow(modelHour, row, "Horas");
			}

			// Iterates the data map for the Traffic by day sheet
			int rowIndex = 10;
			curDate.setTime(initialDate.getTime());
			int partialIndex = 0;
			while(curDate.compareTo(finalDate) < 1) {
				
				StoreRevenue revenue;
				StoreItem items;
				String parsedDate = sdf.format(curDate);
				try {
					revenue = sRevenueDao.getUsingStoreIdAndDate(storeId, parsedDate, true);
				} catch(ASException e) {
					revenue = new StoreRevenue();
					revenue.setQty(0.0);
				} try {
					items = sItemDao.getUsingStoreIdAndDate(storeId, parsedDate, false);
				} catch(ASException e) {
					items = new StoreItem();
					items.setQty(0);
				}
				TrafficEntry e = trafficMap.get(parsedDate);
				XSSFRow row = trafficByDay.getRow(rowIndex);
				if( null == row ) row = trafficByDay.createRow(rowIndex);
				List<XSSFCell> model = partialIndex == 0 ? modelTrafficByDay1 : modelTrafficByDay2;

				XSSFCell cell;
				for(int j = 0; j < model.size(); j++) {
					cell = row.createCell(j);
					cell.copyCellFrom(model.get(j), policy);
					if(model.get(j).getCellTypeEnum() == CellType.STRING &&
							model.get(j).getStringCellValue().equals("<stringDate>")) {
						cell.setCellType(CellType.STRING);
						cell.setCellValue(e.getDate());
					} else if(model.get(j).getCellTypeEnum() == CellType.STRING &&
							model.get(j).getStringCellValue().equals("<peasants>")) {
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(e.getPeasants());
					} if(model.get(j).getCellTypeEnum() == CellType.STRING &&
							model.get(j).getStringCellValue().equals("<visits>")) {
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(e.getVisits());
					} if(model.get(j).getCellTypeEnum() == CellType.STRING &&
							model.get(j).getStringCellValue().equals("<tickets>")) {
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(e.getTickets());
					} if(model.get(j).getCellTypeEnum() == CellType.STRING &&
							model.get(j).getStringCellValue().equals("<items>")) {
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(items.getQty());
					} if(model.get(j).getCellTypeEnum() == CellType.STRING &&
							model.get(j).getStringCellValue().equals("<revenue>")) {
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(revenue.getQty());
					} if(model.get(j).getCellTypeEnum() == CellType.FORMULA) {
						XSSFEvaluationWorkbook fpWb = XSSFEvaluationWorkbook.create(workbook);
						Ptg[] tokens = FormulaParser.parse(model.get(j).getCellFormula(), fpWb,
								FormulaType.CELL, workbook.getSheetIndex(formulae));
						for(Ptg token : tokens) {
							if(token instanceof RefPtg) {
								RefPtg refPtg = (RefPtg) token;
								refPtg.setColumn(refPtg.getColumn() -1);
								if(refPtg.getRow() == model.get(j).getRowIndex())
									refPtg.setRow(rowIndex);
								else
									refPtg.setRow(refPtg.getRow() + rowIndex - 1);
							}
						}
						cell.setCellFormula(FormulaRenderer.toFormulaString(fpWb, tokens));
					}

				}

				curDate.setTime(curDate.getTime() + DAY_IN_MILLIS);
				partialIndex++;
				rowIndex++;
			}

			// Iterates the data map for the Traffic by hour sheet
			rowIndex = 3;
			partialIndex = 0;
			for( int k = 0; k < 24; k++ ) {
				HourEntry e = hourMap.get(HourEntry.calculateHour(k));

				XSSFRow row = trafficByHour.getRow(rowIndex);
				if(null == row) row = trafficByHour.createRow(rowIndex);
				List<XSSFCell> model = modelTrafficByHour;

				XSSFCell cell;
				for( int j = 0; j < model.size(); j++ ) {
					cell = row.createCell(j);
					cell.copyCellFrom(model.get(j), policy);
					if(model.get(j).getCellTypeEnum() == CellType.STRING &&
							model.get(j).getStringCellValue().equals("<stringHour>")) {
						cell.setCellType(CellType.STRING);
						cell.setCellValue(e.getHour());
					} else if(model.get(j).getCellTypeEnum() == CellType.STRING &&
						model.get(j).getStringCellValue().equals("<peasants>")) {
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(e.getPeasants());
					} if(model.get(j).getCellTypeEnum() == CellType.STRING && 
							model.get(j).getStringCellValue().equals("<visits>")) {
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(e.getVisits());
					} if(model.get(j).getCellTypeEnum() == CellType.FORMULA) {
						XSSFEvaluationWorkbook fpWb = XSSFEvaluationWorkbook.create(workbook);
						Ptg [] tokens = FormulaParser.parse(model.get(j).getCellFormula(), fpWb,
								FormulaType.CELL, workbook.getSheetIndex(formulae));
						for(Ptg token : tokens) {
							if(token instanceof RefPtg) {
								RefPtg refPtg = (RefPtg) token;
								refPtg.setColumn(refPtg.getColumn() -1);
								if(refPtg.getRow() == model.get(j).getRowIndex())
									refPtg.setRow(rowIndex);
								else
									refPtg.setRow(refPtg.getRow() + rowIndex - 1);
							}
						}
						cell.setCellFormula(FormulaRenderer.toFormulaString(fpWb, tokens));
					}
				}
				partialIndex++;
				rowIndex++;
			}

			// Iterates the data map for the Permanence sheet
			rowIndex = 3;
			partialIndex = 0;
			for( int k = 0; k < 24; k++ ) {
				PermanenceEntry e = permanenceMap.get(PermanenceEntry.calculateHour(k));

				XSSFRow row = permanence.getRow(rowIndex);
				if( null == row ) row = permanence.createRow(rowIndex);
				List<XSSFCell> model = modelPermanence;

				XSSFCell cell;
				for( int j = 0; j < model.size(); j++ ) {
					cell = row.createCell(j);
					cell.copyCellFrom(model.get(j), policy);
					if( model.get(j).getCellTypeEnum() == CellType.STRING &&
							model.get(j).getStringCellValue().equals("<stringHour>")) {
						cell.setCellType(CellType.STRING);
						cell.setCellValue(e.getHour());
					} else if(model.get(j).getCellTypeEnum() == CellType.STRING &&
							model.get(j).getStringCellValue().equals("<peasants>")) {
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(e.getFormattedPeasants());
					} if(model.get(j).getCellTypeEnum() == CellType.STRING &&
							model.get(j).getStringCellValue().equals("<visits>")) {
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(e.getFormattedVisits());
					} if(model.get(j).getCellTypeEnum() == CellType.FORMULA) {
						XSSFEvaluationWorkbook fpWb = XSSFEvaluationWorkbook.create(workbook);
						Ptg [] tokens = FormulaParser.parse(model.get(j).getCellFormula(), fpWb,
								FormulaType.CELL, workbook.getSheetIndex(formulae));
						for(Ptg token : tokens) {
							if(token instanceof RefPtg) {
								RefPtg refPtg = (RefPtg) token;
								refPtg.setColumn(refPtg.getColumn() -1);
								if(refPtg.getRow() == model.get(j).getRowIndex())
									refPtg.setRow(rowIndex);
								else
									refPtg.setRow(refPtg.getRow() + rowIndex - 1);
							}
						}
						cell.setCellFormula(FormulaRenderer.toFormulaString(fpWb, tokens));
					}
				}
				partialIndex++;
				rowIndex++;
			}
			// Iterates the data map for the High and Dead hours sheets
			int cellIndex = 0;
			Iterator<String> keys = dateAndHourMap.keySet().iterator();
			while(keys.hasNext()) {
				String key = keys.next();
				DateAndHourEntry e = dateAndHourMap.get(key);
				switch (e.getDay()) {
				case Calendar.SUNDAY:
					rowIndex = 8;
					break;
				case Calendar.MONDAY:
					rowIndex = 2;
					break;
				case Calendar.TUESDAY:
					rowIndex = 3;
					break;
				case Calendar.WEDNESDAY:
					rowIndex = 4;
					break;
				case Calendar.THURSDAY:
					rowIndex = 5;
					break;
				case Calendar.FRIDAY:
					rowIndex = 6;
					break;
				case Calendar.SATURDAY:
					rowIndex = 7;
					break;
				}
				cellIndex = 4 + e.getHour();
				XSSFRow row = highHours.getRow(rowIndex);
				if( null == row ) row = highHours.createRow(rowIndex);
				List<XSSFCell> model = modelHour;
				XSSFCell cell = row.getCell(cellIndex);
				if( cell == null ) cell = row.createCell(cellIndex);
				cell.copyCellFrom(model.get(0), policy);
				if( e.getVisits() > 0 ) {
					cell.setCellValue(e.getVisits());
				}
				cellIndex = 3 + e.getHour();
				row = deadHours.getRow(rowIndex);
				if(null == row) row = deadHours.createRow(rowIndex);
				cell = row.getCell(cellIndex);
				if( cell == null ) cell = row.createCell(cellIndex);
				cell.copyCellFrom(model.get(0), policy);
				if( e.getVisits() > 0 ) {
					cell.setCellValue(e.getVisits());
				}
			}
			// Iterates the printing page sheets
			Iterator<Row> prows = print.iterator();
			while(prows.hasNext()) {
				XSSFRow row = (XSSFRow) prows.next();
				Iterator<Cell> pcells = row.iterator();
				while(pcells.hasNext()) {
					XSSFCell cell = (XSSFCell) pcells.next();
					if(cell.getCellTypeEnum() == CellType.STRING &&
							cell.getStringCellValue().equals("<storeName>")) {
						cell.setCellValue(storeName);
					} if(cell.getCellTypeEnum() == CellType.STRING &&
							cell.getStringCellValue().equals("<dateName>")) {
						cell.setCellValue(dateName);
					} if(cell.getCellTypeEnum() == CellType.STRING &&
							cell.getStringCellValue().equals("<permanence>")) {
						int p = (int)(totalVisitsCount > 0 ?
								(totalVisitsPermanence / totalVisitsCount / 60000) : 0);
						cell.setCellValue(p);
					}
				}
			}
			log.log(Level.INFO, "Rendering Formulae...");
			try {
				XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
			} catch( Exception e ) {
				e.printStackTrace();
			}
			File tmp = File.createTempFile("getin", "data");
			FileOutputStream fos = new FileOutputStream(tmp);
			log.log(Level.INFO, "Written to: " +tmp.getAbsolutePath());
			workbook.write(fos);
			fos.flush();
			fos.close();
			tmp.delete();
			workbook.write(bos);
			bos.close();
			workbook.close();
			return bos.toByteArray();
		} catch( Exception ex ) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			throw ASExceptionHelper.defaultException(ex.getMessage(), ex);
		}
	}

	public void getTemplateRow(List<XSSFCell> target, XSSFRow row, String search) {
		XSSFCell cell = row.getCell(0);
		if( null != cell && cell.getStringCellValue().equals(search)) {
			target.clear();
			Iterator<Cell> cells = row.iterator();
			int index = 0;
			while(cells.hasNext()) {
				cell = (XSSFCell)cells.next();
				if( index > 0 ) {
					target.add(cell);
				}
				index++;
			}
		}
	}

	public String getStringDate(Date date) {
		StringBuffer sb = new StringBuffer();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		switch (month) {
		case Calendar.JANUARY:
			sb.append("Enero ");
			break;
		case Calendar.FEBRUARY:
			sb.append("Febrero ");
			break;
		case Calendar.MARCH:
			sb.append("Marzo ");
			break;
		case Calendar.APRIL:
			sb.append("Abril ");
			break;
		case Calendar.MAY:
			sb.append("Mayo ");
			break;
		case Calendar.JUNE:
			sb.append("Julio ");
			break;
		case Calendar.JULY:
			sb.append("Julio ");
			break;
		case Calendar.AUGUST:
			sb.append("Agosto ");
			break;
		case Calendar.SEPTEMBER:
			sb.append("Septiembre ");
			break;
		case Calendar.OCTOBER:
			sb.append("Octubre ");
			break;
		case Calendar.NOVEMBER:
			sb.append("Noviembre ");
			break;
		case Calendar.DECEMBER:
			sb.append("Diciembre ");
			break;
		}
		
		sb.append(cal.get(Calendar.YEAR));
		return sb.toString();
	}
	
	public static class DateAndHourEntry {
		private int day;
		private int hour;
		private long visits;
		
		public DateAndHourEntry() {
			day = 0;
			hour = 0;
			visits = 0;
		}

		public static String getKey(Date date, int hour) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int day = cal.get(Calendar.DAY_OF_WEEK);
			return DF.format(day) + "-" + DF.format(hour);
		}
		
		public String getKey() {
			DecimalFormat DF = new DecimalFormat("00");
			return DF.format(day) + "-" + DF.format(hour);
		}
		
		/**
		 * @return the day
		 */
		public int getDay() {
			return day;
		}

		/**
		 * @param day the day to set
		 */
		public void setDay(int day) {
			this.day = day;
		}

		/**
		 * @param day the day to set
		 */
		public void setDay(Date day) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(day);
			this.day = cal.get(Calendar.DATE);
		}

		/**
		 * @return the hour
		 */
		public int getHour() {
			return hour;
		}

		/**
		 * @param hour the hour to set
		 */
		public void setHour(int hour) {
			this.hour = hour;
		}

		/**
		 * @return the visits
		 */
		public long getVisits() {
			return visits;
		}

		/**
		 * @param visits the visits to set
		 */
		public void setVisits(long visits) {
			this.visits = visits;
		}

	}
	
	public static class HourEntry {
		
		private String hour;
		private long peasants;
		private long visits;
		
		public HourEntry() {
			hour = "";
			peasants = 0;
			visits = 0;
		}

		/**
		 * @return the hour
		 */
		public String getHour() {
			return hour;
		}

		/**
		 * @param hour the hour to set
		 */
		public void setHour(String hour) {
			this.hour = hour;
		}

		/**
		 * @return the peasants
		 */
		public long getPeasants() {
			return peasants;
		}

		/**
		 * @param peasants the peasants to set
		 */
		public void setPeasants(long peasants) {
			this.peasants = peasants;
		}

		/**
		 * @return the visits
		 */
		public long getVisits() {
			return visits;
		}

		/**
		 * @param visits the visits to set
		 */
		public void setVisits(long visits) {
			this.visits = visits;
		}

		public void setUnformattedHour(int hour) {
			setHour(calculateHour(hour));
		}
		
		public static String calculateHour(int hour) {
			DecimalFormat df = new DecimalFormat("00");
			return df.format(hour) + ":00Hs";
		}
		
		public String toString() {
			return hour + ", " + peasants + ", " + visits;
		}
		
	}
	
	public static class PermanenceEntry {
		
		private String hour;
		private long peasants;
		private long peasantsCount;
		private long visits;
		private long visitsCount;
		
		public PermanenceEntry() {
			hour = "";
			peasants = 0;
			peasantsCount = 0;
			visits = 0;
			visitsCount = 0;
		}

		/**
		 * @return the hour
		 */
		public String getHour() {
			return hour;
		}

		/**
		 * @param hour the hour to set
		 */
		public void setHour(String hour) {
			this.hour = hour;
		}

		/**
		 * @return the peasants
		 */
		public long getPeasants() {
			return peasants;
		}

		public long getFormattedPeasants() {
			if( peasantsCount > 0 )
				return peasants / peasantsCount / 60000;
			else
				return 0;
		}

		public long getFormattedVisits() {
			if( visitsCount > 0 )
				return visits / visitsCount / 60000;
			else
				return 0;
		}

		/**
		 * @param peasants the peasants to set
		 */
		public void setPeasants(long peasants) {
			this.peasants = peasants;
		}

		/**
		 * @return the peasantsCount
		 */
		public long getPeasantsCount() {
			return peasantsCount;
		}

		/**
		 * @param peasantsCount the peasantsCount to set
		 */
		public void setPeasantsCount(long peasantsCount) {
			this.peasantsCount = peasantsCount;
		}

		/**
		 * @return the visits
		 */
		public long getVisits() {
			return visits;
		}

		/**
		 * @param visits the visits to set
		 */
		public void setVisits(long visits) {
			this.visits = visits;
		}

		/**
		 * @return the visitsCount
		 */
		public long getVisitsCount() {
			return visitsCount;
		}

		/**
		 * @param visitsCount the visitsCount to set
		 */
		public void setVisitsCount(long visitsCount) {
			this.visitsCount = visitsCount;
		}

		public void setUnformattedHour(int hour) {
			setHour(calculateHour(hour));
		}
		
		public static String calculateHour(int hour) {
			return DF.format(hour) + ":00Hs";
		}
		
		public String toString() {
			return hour + ", " + peasants + ", " + visits;
		}
		
	}
	
	public class TrafficEntry {

		private String date;
		private long peasants;
		private long visits;
		private long tickets;

		public TrafficEntry() {
			date = "";
			peasants = 0;
			visits = 0;
			tickets = 0;
		}
		
		/**
		 * @return the date
		 */
		public String getDate() {
			return date;
		}
		/**
		 * @param date the date to set
		 */
		public void setDate(String date) {
			this.date = date;
		}
		/**
		 * @return the peasants
		 */
		public long getPeasants() {
			return peasants;
		}
		/**
		 * @param peasants the peasants to set
		 */
		public void setPeasants(long peasants) {
			this.peasants = peasants;
		}
		/**
		 * @return the visits
		 */
		public long getVisits() {
			return visits;
		}
		/**
		 * @param visits the visits to set
		 */
		public void setVisits(long visits) {
			this.visits = visits;
		}
		/**
		 * @return the tickets
		 */
		public long getTickets() {
			return tickets;
		}
		/**
		 * @param tickets the tickets to set
		 */
		public void setTickets(long tickets) {
			this.tickets = tickets;
		}

		public void setUnformmatedDate(Date date) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			StringBuffer sb = new StringBuffer();
			
			int dof = cal.get(Calendar.DAY_OF_WEEK);
			switch (dof) {
			case Calendar.SUNDAY:
				sb.append("Dom ");
				break;
			case Calendar.MONDAY:
				sb.append("Lun ");
				break;
			case Calendar.TUESDAY:
				sb.append("Mar ");
				break;
			case Calendar.WEDNESDAY:
				sb.append("Mie ");
				break;
			case Calendar.THURSDAY:
				sb.append("Jue ");
				break;
			case Calendar.FRIDAY:
				sb.append("Vie ");
				break;
			case Calendar.SATURDAY:
				sb.append("Sab ");
				break;
			}
			
			sb.append(DF.format(cal.get(Calendar.DATE)));
			sb.append("/");
			sb.append(DF.format(cal.get(Calendar.MONTH) + 1));
			
			this.setDate(sb.toString());
		}

		public String toString() {
			return date + ", " + peasants + ", " + visits + ", " + tickets;
		}
	}

	public static String resolveDumpFileName(String baseDir, String baseName, Date forDate) {
		String myYear = year.format(forDate);
		String myMonth = month.format(forDate);
		baseName = baseName.replaceAll("\t", " ");
		
		StringBuffer sb = new StringBuffer();
		sb.append(baseDir);
		if(!baseDir.endsWith(File.separator)) sb.append(File.separator);
		sb.append("visits").append(File.separator);
		sb.append(myYear).append(File.separator);
		sb.append(myMonth).append(File.separator);
		sb.append(baseName).append(".xlsm");

		return sb.toString();
	}
	
	@Override
	public byte[] exportDB(List<String> storesId, String brandId, String fromDate, String toDate,
			String outDir, boolean saveTmp) throws ASException {
		if(brandId != null && StringUtils.hasText(brandId)) {
			for(Store current : storeDao.getUsingBrandAndStatus(brandId, null, null)) {
				if(!storesId.contains(current.getIdentifier()))
					storesId.add(current.getIdentifier());
			}
		}
		Workbook workbook = new XSSFWorkbook();
		Sheet storeSheet;
		Row row;
		CreationHelper helper = workbook.getCreationHelper();
		CellStyle checkinStyle = workbook.createCellStyle();
		checkinStyle.setDataFormat(helper.createDataFormat().getFormat("m/d/yy h:mm:ss"));
		CellStyle dataDStyle = workbook.createCellStyle();
		dataDStyle.setDataFormat(helper.createDataFormat().getFormat("m/d/yy"));
		CellStyle currencyStyle = workbook.createCellStyle();
		currencyStyle.setDataFormat(helper.createDataFormat().getFormat("$#,##0.00"));
		Cell cell;
		int rowIndex;
		Store store;
		DumperHelper<APDVisit> apdVisitDump = new DumpFactory<APDVisit>().build(null, APDVisit.class);
		Iterator<APDVisit> apdVisitIterator;
		APDVisit visit;
		Date initialDate,finalDate, curDate;
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			initialDate = sdf.parse(fromDate);
			finalDate = sdf.parse(toDate);
		} catch(ParseException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			try { workbook.close(); } catch(IOException e) {}
			throw ASExceptionHelper.defaultException(ex.getMessage(), ex);
		}
		storeSheet = workbook.createSheet(WorkbookUtil.createSafeSheetName(brandId));
		//TODO sheet per day and sheet per hours
		String parsedDate;
		StoreRevenue revenue;
		StoreItem items;
		StoreTicket tickets;
		// processing begins
		for(String storeId : storesId) {
			store = storeDao.get(storeId, false);
			log.log(Level.INFO, "Processing store " + store.getName() +" for period " +fromDate +" - "
						+toDate + "...");
			//creates a workbook sheet for each store, and adds some headers
			rowIndex = 0;
			row = storeSheet.createRow(rowIndex++);
			// FIXME
			/*row.createCell(CHECKIN_DATETIME_CELL_INDEX)
					.setCellValue(helper.createRichTextString(CHECKIN_DATETIME_CELL_TITLE));
			row.createCell(CHECKIN_DURATION_CELL_INDEX)
					.setCellValue(helper.createRichTextString(CHECKIN_DURATION_CELL_TITLE));
			row.createCell(DEVICE_CELL_INDEX)
					.setCellValue(helper.createRichTextString(DEVICE_CELL_TITLE));
			row.createCell(REVENUE_CELL_INDEX)
					.setCellValue(helper.createRichTextString(REVENUE_CELL_TITLE));
			row.createCell(ITEMS_CELL_INDEX)
					.setCellValue(helper.createRichTextString(ITEMS_CELL_TITLE));
			row.createCell(TICKETS_CELL_INDEX)
					.setCellValue(helper.createRichTextString(TICKETS_CELL_TITLE));
			row.createCell(DATA_DATE_CELL_INDEX)
					.setCellValue(helper.createRichTextString(DATA_DATE_CELL_TITLE));
			curDate = new Date(initialDate.getTime());
			// adds a row for each segment of data
			while(curDate.compareTo(finalDate) <= 0) {
				parsedDate = sdf.format(curDate);
				row = storeSheet.createRow(rowIndex++);
				try {
					revenue = sRevenueDao.getUsingStoreIdAndDate(storeId, parsedDate, true);
				} catch(ASException e) {
					revenue = new StoreRevenue();
					revenue.setQty(0.0);
				} try {
					items = sItemDao.getUsingStoreIdAndDate(storeId, parsedDate, false);
				} catch(ASException e) {
					items = new StoreItem();
					items.setQty(0);
				} try {
					tickets = sTicketDao.getUsingStoreIdAndDate(storeId, parsedDate, false);
				} catch(ASException e) {
					tickets = new StoreTicket();
					tickets.setQty(0);
				}
				cell = row.createCell(REVENUE_CELL_INDEX);
				cell.setCellValue(revenue.getQty());
				cell.setCellStyle(currencyStyle);
				row.createCell(ITEMS_CELL_INDEX).setCellValue(items.getQty());
				row.createCell(TICKETS_CELL_INDEX).setCellValue(tickets.getQty());
				cell = row.createCell(DATA_DATE_CELL_INDEX);
				cell.setCellValue(curDate);
				cell.setCellStyle(dataDStyle);
				curDate.setTime(curDate.getTime() +DAY_IN_MILLIS);
			}
			apdVisitDump.setFilter(storeId);
			apdVisitIterator = apdVisitDump.iterator(initialDate, finalDate);
			rowIndex = 1;
			while(apdVisitIterator.hasNext()) {
				row = storeSheet.getRow(rowIndex);
				if(row == null) row = storeSheet.createRow(rowIndex);
				rowIndex++;
				visit = apdVisitIterator.next();
				cell = row.createCell(CHECKIN_DATETIME_CELL_INDEX);
				cell.setCellValue(visit.getCheckinStarted());
				cell.setCellStyle(checkinStyle);
				row.createCell(CHECKIN_DURATION_CELL_INDEX).setCellValue(visit.getDuration() /1000);
				row.createCell(DEVICE_CELL_INDEX).setCellValue(helper.createRichTextString(
						visit.getDevicePlatform()));
			}
			storeSheet.autoSizeColumn(CHECKIN_DATETIME_CELL_INDEX);
			storeSheet.autoSizeColumn(CHECKIN_DURATION_CELL_INDEX);
			storeSheet.autoSizeColumn(DEVICE_CELL_INDEX);
			storeSheet.autoSizeColumn(REVENUE_CELL_INDEX);
			storeSheet.autoSizeColumn(ITEMS_CELL_INDEX);
			storeSheet.autoSizeColumn(TICKETS_CELL_INDEX);
			storeSheet.autoSizeColumn(DATA_DATE_CELL_INDEX);*/
		}
		apdVisitDump.dispose();
		try {
			if(saveTmp) {
				FileOutputStream fos;
				File tmp = File.createTempFile(outDir +"_", "_data.xlsx");
				fos = new FileOutputStream(tmp);
				workbook.write(fos);
				fos.flush();
				log.log(Level.INFO, "Excel created in " +tmp.getAbsolutePath());
				fos.close();
				workbook.close();
				return null;
			} else {
				ByteArrayOutputStream bos;
				bos = new ByteArrayOutputStream();
				workbook.write(bos);
				bos.flush();
				workbook.close();
				return bos.toByteArray();
			}
		} catch(IOException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			throw ASExceptionHelper.defaultException(ex.getMessage(), ex);
		}
	}
	
}
