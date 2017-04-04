package mobi.allshoppings.cli;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.exporter.ExcelExportHelper;


public class TestExcelReport extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			
			ExcelExportHelper helper = (ExcelExportHelper)getApplicationContext().getBean("excel.export.helper");
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			@SuppressWarnings("unused")
			OptionSet options = parser.parse(args);

			//final String STORE_ID = "1478641696271"; // Botanicus Angelopolis
			//final String STORE_ID = "1473220851475"; // Adolfo Dominguez interlomas
			final String STORE_ID = "b52192c9-37aa-464d-9243-81eb7cf51124"; // Areas Mexico A70
			final String FROM_DATE = "2017-01-02"; // Retail Calendar initial 2017 day
			final String TO_DATE = "2017-03-26"; // Retail Calendar final march day
			final String outDir = "/tmp";
			
			helper.export(STORE_ID, FROM_DATE, TO_DATE, outDir);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
