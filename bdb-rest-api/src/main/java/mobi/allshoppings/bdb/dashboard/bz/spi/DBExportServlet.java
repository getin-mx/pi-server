package mobi.allshoppings.bdb.dashboard.bz.spi;

import java.io.IOException;	
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.exporter.ExcelExportHelper;
import mobi.allshoppings.tools.CollectionFactory;

public class DBExportServlet extends HttpServlet {

	// class attributes
	
	private List<String> storesId;
	private String fromDate, toDate, countryISO, languageISO, destFile, brandId;
	
	private static final long serialVersionUID = 6006971372478805453L;
	private static final Logger LOG = Logger.getLogger(DBExportServlet.class.getSimpleName());
	static OptionParser parser;
	private static final int DAY_IN_MILLIS = 1000 *60 *60* 24;
	public static final String FROM_DATE_PARAM = "fromDate";
	public static final String TO_DATE_PARAM = "toDate";
	public static final String BRAND_PARAM = "brandId";
	public static final String STORES_PARAM = "storeIds";
	public static final String DESTINATION_FILE_PARAM = "destFile";
	
	@Autowired
	private ExcelExportHelper exportHelper;
	
	// implementation methods
	
	@Override
	public void init() {
		WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext())
				.getAutowireCapableBeanFactory().autowireBean(this);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			byte[] rawCsv;
			boolean workFromWeb = req != null && resp != null;
			if(workFromWeb) {
				//FIXME: Check Auth Token
				fromDate = req.getParameter(FROM_DATE_PARAM);
				toDate = req.getParameter(TO_DATE_PARAM);
				brandId = req.getParameter(BRAND_PARAM);
				StringBuilder storesIds = new StringBuilder();
				String aux;
				storesId = CollectionFactory.createList();
				for(String store : req.getParameter(STORES_PARAM).split(",")) {
					aux = store.trim();
					if(!storesId.contains(aux)) storesId.add(aux);
					storesIds.append(aux).append('-');
				}
				storesIds.replace(storesIds.length() -1, storesIds.length(), "");
				if(!StringUtils.hasText(brandId) && (storesId == null || storesId.size() == 0))
					throw ASExceptionHelper.defaultException("At least, one brand or store ID must be given",
							null);
				destFile = StringUtils.hasText(brandId) ? brandId : "";
				destFile += storesIds.toString();
			}//if parameters are null, read from class attributes
			long benchmark = System.currentTimeMillis();
			rawCsv = exportHelper.exportDB(storesId, brandId, fromDate, toDate, countryISO,
					languageISO, destFile, !workFromWeb);
			String fileName = req.getParameter(DESTINATION_FILE_PARAM);
			int aux;
			if(fileName == null) fileName = "report";
			else if((aux = fileName.indexOf('.')) >= 0) 
				fileName = aux == 0 ? "report" : fileName.substring(0, aux);
			resp.setHeader("Content-Disposition", "inline; filename=\"" +fileName +".xlsx\"");
			resp.setHeader("Content-type",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Length", String.valueOf(rawCsv.length));
			OutputStream os = resp.getOutputStream();
			os.write(rawCsv);
			os.flush();
			LOG.log(Level.FINE, "DB Exported to MS Excel in "
						+(System.currentTimeMillis() -benchmark) +"ms");
		} catch(ASException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
		
	}
	
	// static methods
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts(FROM_DATE_PARAM, "Date From").withRequiredArg().ofType( String.class );
		parser.accepts(TO_DATE_PARAM, "Date To").withRequiredArg().ofType( String.class );
		parser.accepts(BRAND_PARAM, "Brand identifier").withRequiredArg().ofType( String.class );
		parser.accepts(STORES_PARAM, "List of comma separated stores (superseeds brandIds)")
				.withRequiredArg().ofType( String.class );
		/*parser.accepts("generatePerDay", //TODO ???
				"True (Default) outputs a database with only daily data. "
				+ "False would generate a hourly one (if possible)")
				.withRequiredArg().ofType( Boolean.class );*/
		parser.accepts(DESTINATION_FILE_PARAM, "Specifies the route where the result XML should be "
				+ "written into. If a directorry is given, the file will be named '<givenIds>.xml'."
				+ " If a file with an extension different from XML is given, '.xml' will be "
				+ "appended to the file name. By default, the file is writen to '<givenIds>.xml' in"
				+ "current working directory").withRequiredArg().ofType(String.class);
		return parser;
	}
	
	public static void main(String[] args) throws ASException {
		OptionSet options = parser.parse(args);
		DBExportServlet dbExport = new DBExportServlet();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		dbExport.fromDate = options.has(FROM_DATE_PARAM) ?
			options.valueOf(FROM_DATE_PARAM).toString() :
				sdf.format(new Date(System.currentTimeMillis() -DAY_IN_MILLIS));
		dbExport.toDate = options.has(TO_DATE_PARAM) ? options.valueOf(TO_DATE_PARAM).toString() :
				sdf.format(new Date(System.currentTimeMillis() +DAY_IN_MILLIS));
		if(options.has(BRAND_PARAM))
			dbExport.brandId = options.valueOf(BRAND_PARAM).toString();
		String aux;
		StringBuilder storesIds = new StringBuilder();
		if(options.has(STORES_PARAM)) {
			dbExport.storesId = CollectionFactory.createList();
			for(String storeId : options.valueOf(STORES_PARAM).toString().split(",")) {
				aux = storeId.trim();
				if(!dbExport.storesId.contains(aux)) dbExport.storesId.add(aux);
				storesIds.append(aux).append('-');
			}
			
			storesIds.replace(storesIds.length() -1, storesIds.length(), "");
		}//retrieves stores (if any)
		if(!StringUtils.hasText(dbExport.brandId) &&
				(dbExport.storesId == null || dbExport.storesId.size() == 0))
			throw ASExceptionHelper.defaultException("At least, one brand or store ID must be given",
					null);
		dbExport.destFile = StringUtils.hasText(dbExport.brandId) ? dbExport.brandId : "";
		dbExport.destFile += storesIds.toString();
		try {
			dbExport.doGet(null, null);
		} catch(IOException | ServletException e) {
			throw ASExceptionHelper.defaultException("Couldn't export DB", e);
		}
	}
	
}
