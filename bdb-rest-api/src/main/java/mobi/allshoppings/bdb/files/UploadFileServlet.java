package mobi.allshoppings.bdb.image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.click.extras.gae.MemoryFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.ibm.icu.util.Calendar;
import com.inodes.datanucleus.model.Blob;

import mobi.allshoppings.dao.ImageDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Image;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreTicket;
import mobi.allshoppings.model.SystemConfiguration;


@SuppressWarnings("serial")
public class UploadImageServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(UploadImageServlet.class.getName());

	@Autowired
	SystemConfiguration systemConfiguration;

	@Autowired
	ImageDAO imageDao;

	/**
	 * Initialization method
	 */
	@Override
	public void init() {
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		AutowireCapableBeanFactory bf = ctx.getAutowireCapableBeanFactory();
		bf.autowireBean(this);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		super.doDelete(req, resp);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		@SuppressWarnings("unused")
		String fileName = req.getParameter("file") != null ? req.getParameter("file") : null;

		super.doGet(req, resp);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		this.doGet(req, resp);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		long maxFileSize = systemConfiguration.getMaxUploadSize();

		FileItemFactory factory = new MemoryFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		JSONObject json = new JSONObject();

		try{ 
			// Parse the request to get file items.
			@SuppressWarnings("unchecked")
			List<FileItem> fileItems = upload.parseRequest(req);

			// Process the uploaded file items
			Iterator<FileItem> i = fileItems.iterator();

			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				if (!fi.isFormField()) {

					json.put("error", "");
					json.put("type", fi.getContentType());
					json.put("size", fi.getSize());

					if (fi.getSize() > maxFileSize) {
						json.put("error", "max_file_size");
					} else {
						try {
							String name = fi.getName();
							String type = getFileType(name);
							switch (type) {
							case "xlsx":
							case "xls":
								Blob source = new Blob(fi.get());
								InputStream excelFile = new ByteArrayInputStream(source.getBytes());
								Workbook workbook = new XSSFWorkbook(excelFile);
								Sheet datatypeSheet = workbook.getSheetAt(0);
								Iterator<Row> iterator = datatypeSheet.iterator();
								List<List<String>> table = createDataTable(iterator, json);
								createTable(table, json);
								updateTickets(json);
								break;
							case "jpg":
							case "png":
								Image image = new Image();
								image.setKey(imageDao.createKey(fi.getName()));

								image.setContents(new Blob(fi.get()));
								image.setContentType(fi.getContentType());
								image.setOriginalFileName(fi.getName());
								image.setSessionKey(req.getSession().getId());
								imageDao.create(image);

								json.put("name", image.getIdentifier());
								json.put("url", "/img" + image.getIdentifier());
								json.put("delete_url", "/img/upload?file=" + image.getIdentifier());
								json.put("delete_type", "DELETE");
								break;
							default:
								break;
							}

						} catch (Exception ex) {
							log.log(Level.SEVERE, ex.getMessage());
							json.put("error", "max_file_size");
						}
					}
				}
			}

			resp.setHeader("Vary", "Accept");
			resp.setHeader("Content-type", "text/plain");
			OutputStream os = resp.getOutputStream();

			os.write(json.toString().getBytes());

		}catch(Exception ex) {
			System.out.println(ex);
			resp.sendError(500);
		}
	}
	
	protected String getFileType(String name) {		
		String[] extension = name.split("\\.");
		if	(extension.length > 0){
			int iterator = extension.length -1;
			return extension[iterator];
		}else{
			return "";
		}
	}// end getFileType()
	
	protected String getMonth(String name) {
		String[] extension = name.split(" ");
		if	(extension.length > 0){
			int iterator = extension.length -1;
			return extension[iterator].toLowerCase();
		}else{
			return "";
		}
	}// end getMonth()
	
	private List<List<String>> createDataTable(Iterator<Row> iterator, JSONObject json){
		int monthCount = 0;
		int valueCount = 0;
		String month = null;
		List<List<String>> table = new ArrayList<>();
		while (iterator.hasNext()) {

			Row currentRow = iterator.next();
			Iterator<Cell> cellIterator = currentRow.iterator();
			int dataCount = 0;
			int line = currentRow.getLastCellNum();
			List<String> row = new ArrayList();
			while (cellIterator.hasNext()) {

				Cell currentCell = cellIterator.next();
				// getCellTypeEnum shown as deprecated for version 3.15
				// getCellTypeEnum ill be renamed to getCellType starting from version 4.0
				if (monthCount != 0) {// get the data with the values.
					if	(valueCount > 1){// escape the headers
						if	(dataCount >= 1 && dataCount < line -1) {
							if (currentCell.getCellTypeEnum() == CellType.STRING) {
								row.add(currentCell.getStringCellValue());
							} else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
								Double num = currentCell.getNumericCellValue();
								row.add(num.intValue()+"");
							}
						}
					}else { 
						if (currentCell.getCellTypeEnum() == CellType.STRING) {
							row.add(currentCell.getStringCellValue());
						} else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
							Double num = currentCell.getNumericCellValue();
							row.add(getDate(month, num));
						}
					}
				} else{ //  get the month
					month = getMonth(currentCell.getStringCellValue());
					json.put("date", getMonth(currentCell.getStringCellValue()));
				}
				dataCount++;
				monthCount++;
			}
			valueCount++;
			table.add(row);
		}
		return table;
	}// end createDataTable()
	
	private void createTable(List<List<String>> table, JSONObject json) {
		JSONArray jsonArray = new JSONArray();
		for (List<String> row : table) {
			if	(row != null && !row.isEmpty())
				jsonArray.put(row);
		}
		// Returns the final value
		json.put("data", jsonArray);
	}// end createTable()
	
	private String getDate(String month, Double day) {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String mm = null;
		String dd;
		switch (month) {
		case "enero":
			mm = "01";
			break;
		case "febrero":
			mm = "02";
			break;
		case "marzo":
			mm = "03";
			break;
		case "abril":
			mm = "04";
			break;
		case "mayo":
			mm = "05";
			break;
		case "junio":
			mm = "06";
			break;
		case "julio":
			mm = "07";
			break;
		case "agosto":
			mm = "08";
			break;
		case "septiembre":
			mm = "09";
			break;
		case "octubre":
			mm = "10";
			break;
		case "noviembre":
			mm = "11";
			break;
		case "diciembre":
			mm = "12";
			break;
		default:
			break;
		}
		dd = (day.intValue() < 10) ? "0"+day.intValue() : day.intValue()+"";
		return year+"-"+mm+"-"+dd;
		
	}// 0end getDate()
	
	protected void updateTickets(JSONObject json){
			// obtain the id and validates the auth token
			//obtainUserIdentifier(true);
			
			//JSONObject json = entity.getJsonObject();
			JSONArray data = json.getJSONArray("data");
			JSONArray dates = data.getJSONArray(1);
			for (int i = 1; i < data.length(); i++) {
				
			}
			
	}
}
