package mobi.allshoppings.location.test;

import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.location.LocationUpdateReportService;

public class LocationUpdateReportServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {
			LocationUpdateReportService service = new LocationUpdateReportService();
			Map<String, Long> results = service.getLocationUpdateReport(false, true);

			long total = 0;
			
			Iterator<String> i = results.keySet().iterator();
			while(i.hasNext()) {
				String key = i.next();
				Long value = results.get(key);
				total += value;
			}

			System.out.println(line(results, LocationUpdateReportService.LESS_ONE_MINUTE, total));
			System.out.println(line(results, LocationUpdateReportService.LESS_FIVE_MINUTES, total));
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

			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}
	
	public String line(Map<String, Long>results, String key, long totals) {
		Long val = results.get(key);
		int percent = (int)(val * 100 / totals);
		return(key + ": " + val + " (" + percent + "%)");
	}
}