package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.ExportUnitDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.exporter.ExportHelper;
import mobi.allshoppings.model.ExportUnit;


public class RunExportUnits extends AbstractCLI {

	private static final Logger log = Logger.getLogger(RunExportUnits.class.getName());
	public static final long TWENTY_FOUR_HOURS = 86400000;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "fromDate", "Export from date (yyyy-MM-dd)").withRequiredArg().ofType( String.class );
		parser.accepts( "toDate", "Export to date (yyyy-MM-dd)").withRequiredArg().ofType( String.class );
		parser.accepts( "exportUnit", "Export unit").withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {

			ExportHelper helper = (ExportHelper)getApplicationContext().getBean("export.helper");
			ExportUnitDAO dao = (ExportUnitDAO)getApplicationContext().getBean("exportunit.dao.ref");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String sFromDate = null;
			String sToDate = null;
			Date fromDate = null;
			Date toDate = null;
			String exportUnit = null;
			
			try {
				if( options.has("fromDate")) {
					sFromDate = (String)options.valueOf("fromDate");
					fromDate = sdf.parse(sFromDate);
				}

				if( options.has("toDate")) {
					sToDate = (String)options.valueOf("toDate");
					toDate = sdf.parse(sToDate);
				}
				
				if( options.has("exportUnit")) {
					exportUnit = (String)options.valueOf("exportUnit");
				}
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Executing export units...");
			if( StringUtils.hasText(exportUnit)) {
				ExportUnit unit = dao.get(exportUnit);
				helper.export(unit, fromDate, toDate);
			} else {
				helper.export(fromDate, toDate);
			}
			
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}

}
