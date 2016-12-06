package mobi.allshoppings.cli;

import java.util.Iterator;
import java.util.Map;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.location.LocationUpdateReportService;

public class DisplayLocationUpdateReport extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "showProgress", "Show Progress while getting results (defaults to false)" ).withRequiredArg().ofType( Boolean.class );
		parser.accepts( "cumulative", "Show Cumulative Results (defaults to false)" ).withRequiredArg().ofType( Boolean.class );
		return parser;
	}

	public static void main(String args[]) {

		// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
		OptionSet options = parser.parse(args);

		boolean showProgress = false;
		boolean cumulative = false;

		try {
			if( options.has("showProgress")) showProgress = (Boolean)options.valueOf("showProgress");
		} catch( Exception e ) {}

		try {
			if( options.has("cumulative")) cumulative = (Boolean)options.valueOf("cumulative");
		} catch( Exception e ) {}

		try {
			LocationUpdateReportService service = new LocationUpdateReportService();
			Map<String, Long> results = service.getLocationUpdateReport(cumulative, showProgress);

			long total = 0;

			if( cumulative ) {
				total = results.get(LocationUpdateReportService.OTHERS);
			} else {
				Iterator<String> i = results.keySet().iterator();
				while(i.hasNext()) {
					String key = i.next();
					Long value = results.get(key);
					total += value;
				}
			}

			System.out.println(line(results, LocationUpdateReportService.LESS_ONE_MINUTE, total));
			System.out.println(line(results, LocationUpdateReportService.LESS_FIVE_MINUTES, total));
			System.out.println(line(results, LocationUpdateReportService.LESS_FIFTEEN_MINUTES, total));
			System.out.println(line(results, LocationUpdateReportService.LESS_THIRTY_MINUTES, total));
			System.out.println(line(results, LocationUpdateReportService.LESS_ONE_HOUR, total));
			System.out.println(line(results, LocationUpdateReportService.LESS_TWO_HOURS, total));
			System.out.println(line(results, LocationUpdateReportService.LESS_FIVE_HOURS, total));
			System.out.println(line(results, LocationUpdateReportService.LESS_ONE_DAY, total));
			System.out.println(line(results, LocationUpdateReportService.LESS_TWO_DAYS, total));
			System.out.println(line(results, LocationUpdateReportService.LESS_THREE_DAYS, total));
			System.out.println(line(results, LocationUpdateReportService.LESS_ONE_WEEK, total));
			System.out.println(line(results, LocationUpdateReportService.LESS_TWO_WEEKS, total));
			System.out.println(line(results, LocationUpdateReportService.LESS_THREE_WEEKS, total));
			System.out.println(line(results, LocationUpdateReportService.LESS_ONE_MONTH, total));
			System.out.println(line(results, LocationUpdateReportService.LESS_TWO_MONTHS, total));
			System.out.println(line(results, LocationUpdateReportService.OTHERS, total));

			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String line(Map<String, Long>results, String key, long totals) {
		Long val = results.get(key);
		int percent = (int)(val * 100 / totals);
		return(key + ": " + val + " (" + percent + "%)");
	}
}
