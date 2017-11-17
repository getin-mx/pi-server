package mobi.allshoppings.cli;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.exporter.ExcelExportHelper;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.CollectionFactory;


public class ExcelReport extends AbstractCLI {

	//private static final String FROM_DATE_PARAM = "fromDate";
	private static final String TO_DATE_PARAM = "toDate";
	private static final String BRANDS_PARAM = "brandIds";
	private static final String STORES_PARAM = "storeIds";
	private static final String DATE_FORMAT = "yyyy-MM";
	private static final SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT);
	
	static {
		SDF.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		/*parser.accepts(FROM_DATE_PARAM, "(Optional) Starting date for the report. By "
				+ "default, only the current month is reported. Date must be in the "
				+ "format " +DATE_FORMAT).withRequiredArg().ofType(String.class);*/
		parser.accepts(TO_DATE_PARAM, "(Optional) The limit date to report. Date "
				+ "must be in the format " +DATE_FORMAT).withRequiredArg()
						.ofType(String.class);
		parser.accepts(BRANDS_PARAM, "(Optional if " +STORES_PARAM +" is specified; "
				+ "required otherwise) Separated comma list of the desired brands to "
				+ "report. A five weeks report; for every month and every store in "
				+ "the given brand will be created. This parameter can be used with "
				+ "the " +STORES_PARAM +" parameter to produce full and partial brands"
				+ " reports").withRequiredArg().ofType(String.class);
		parser.accepts(STORES_PARAM, "(Optional if " +BRANDS_PARAM +" is specified; "
				+ "required otherwise) Separated comma list of the desired stores to "
				+ "report. A five weeks; for every month and every given store will "
				+ "be created. This parameter can be used with the " +BRANDS_PARAM
				+" to produce partial and full brands reports").withRequiredArg()
				.ofType(String.class);
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			
			final ExcelExportHelper helper = (ExcelExportHelper)getApplicationContext().getBean("excel.export.helper");
			final Logger log = Logger.getLogger(ExcelReport.class.getName());
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			//final String fromDate = buildDate(options.valueOf(FROM_DATE_PARAM), false);
			final String fromDate = "2017-01-02";
			final String toDate = buildDate(options.valueOf(TO_DATE_PARAM), true); 
			final String outDir = "/usr/local/allshoppings/dump/";

			final List<String> storeIds = CollectionFactory.createList();
			
			Object arg = options.valueOf(STORES_PARAM);
			if(arg != null) {
				for(String id : arg.toString().split(",")) 
					if(!storeIds.contains(id)) storeIds.add(id);
			}
			arg = options.valueOf(BRANDS_PARAM);
			if(arg != null) {
				StoreDAO sDao = (StoreDAO)getApplicationContext()
						.getBean("store.dao.ref");
				for(String id : arg.toString().split(",")) {
					List<Store> aux = sDao.getUsingBrandAndStatus(id,
							StatusHelper.statusActive(), null);
					for(Store s : aux) if(!storeIds.contains(s.getIdentifier()))
						storeIds.add(s.getIdentifier());
				}
				
			}
			
			List<Thread> tList = CollectionFactory.createList();
			
			
			for( final String store : storeIds ) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							helper.export(store, fromDate, toDate, 5, outDir, null);
						} catch (ASException e) {
							e.printStackTrace();
						}
					}
				});
				t.setName("Thread-" + store);
				tList.add(t);
				t.start();
			}
			
			for( Thread t : tList ) {
				t.join();
			}
			
			log.log(Level.INFO, "Process Finished!");
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
	private static String buildDate(Object arg, boolean isLimit) throws ASException {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		if(arg != null && StringUtils.hasText(arg.toString())) {
			String res = arg.toString();
			try {
				Date d = SDF.parse(res);
				if(isLimit) cal.setTime(d);
				else return res +"-02";
			} catch(ParseException e) {
				if(isLimit) throw ASExceptionHelper.defaultException("Bad limit date", e);
				Logger.getLogger(ExcelReport.class.getName()).log(Level.WARNING,
						"Given date " +arg +" is invalid, using current month", e);
			}
		}
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		if(isLimit) cal.add(Calendar.MONTH, -1);
		return SDF.format(cal.getTimeInMillis()) + (isLimit ? "-"
				+cal.getActualMaximum(Calendar.DAY_OF_MONTH) : "-02");
	}
}
