package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.exporter.ExcelExportHelper;
import mobi.allshoppings.tools.CollectionFactory;;

public class DumpDBToExcel extends AbstractCLI {

	private static final Logger LOG = Logger.getLogger(DumpDBToExcel.class.getSimpleName());
	static OptionParser parser;
	private static final int DAY_IN_MILLIS = 1000 *60 *60* 24;
	public static final String FROM_DATE_PARAM = "fromDate";
	public static final String TO_DATE_PARAM = "toDate";
	public static final String BRAND_PARAM = "brandId";
	public static final String STORES_PARAM = "storeIds";
	public static final String DESTINATION_FILE_PARAM = "destFile";
	
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
	
	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String[] args) throws ASException {
		OptionSet options = parser.parse(args);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fromDate = options.has(FROM_DATE_PARAM) ?
			options.valueOf(FROM_DATE_PARAM).toString() :
				sdf.format(new Date(System.currentTimeMillis() -DAY_IN_MILLIS));
		String toDate = options.has(TO_DATE_PARAM) ? options.valueOf(TO_DATE_PARAM).toString() :
				sdf.format(new Date(System.currentTimeMillis() +DAY_IN_MILLIS));
		String brandId = options.has(BRAND_PARAM) ? options.valueOf(BRAND_PARAM).toString() : null;
		String aux;
		StringBuilder storesIds = new StringBuilder();
		List<String> storesId = CollectionFactory.createList();
		if(options.has(STORES_PARAM)) {
			for(String storeId : options.valueOf(STORES_PARAM).toString().split(",")) {
				aux = storeId.trim();
				if(!storesId.contains(aux)) storesId.add(aux);
				storesIds.append(aux).append('-');
			}
			
			storesIds.replace(storesIds.length() -1, storesIds.length(), "");
		}//retrieves stores (if any)
		if(!StringUtils.hasText(brandId) &&
				(storesId == null || storesId.size() == 0))
			throw ASExceptionHelper.defaultException("At least, one brand or store ID must be given",
					null);
		String destFile = StringUtils.hasText(brandId) ? brandId : "";
		destFile += storesIds.toString();
		if(destFile.length() > 200) destFile = destFile.substring(0, 200) +"__";
		ExcelExportHelper helper = (ExcelExportHelper) getApplicationContext().getBean("excel.export.helper");
		LOG.log(Level.INFO, "Exporting to Excel...");
		long benchmark = System.currentTimeMillis();
		helper.exportDB(storesId, brandId, fromDate, toDate, destFile, true);
		LOG.log(Level.INFO, "DB Exported to MS Excel in " +(System.currentTimeMillis() -benchmark) +"ms");
	}
	
}
