package mobi.allshoppings.cli;

import java.util.Arrays;
import java.util.List;

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
			//final String STORE_ID = "b52192c9-37aa-464d-9243-81eb7cf51124"; // Areas Mexico A70
			// final List<String> storeIds = Arrays.asList("ec9cec0d-d8a1-488e-b3e2-bf4b4f667b66","aebd6f9e-158e-469a-8c43-2668677a4edb","b52192c9-37aa-464d-9243-81eb7cf51124","bcd4d47a-432b-4fab-8391-83f56080ebd4");
			// final List<String> storeIds = Arrays.asList("d40631ed-c981-4be9-8585-b74fe552832e","1471039822790","1471039822813","c0919283-ef3b-4a62-b5e6-22a3ce9d5271","d2cfebcb-2cfd-4b63-90a1-8235b5d51e1d","8aeea969-57d3-4bd5-bddb-dd98caf1fc00","39132498-241e-4efb-a416-06f37f2f5b10","1473220850997");
			final List<String> storeIds = Arrays.asList("1473220850997"); // Adolfo dominguez andares
			final String FROM_DATE = "2017-01-02"; // Retail Calendar initial 2017 day
			final String TO_DATE = "2017-03-26"; // Retail Calendar final march day
			final String outDir = "/tmp";
			
			for( String store : storeIds ) {
				helper.export(store, FROM_DATE, TO_DATE, outDir);
			}
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
