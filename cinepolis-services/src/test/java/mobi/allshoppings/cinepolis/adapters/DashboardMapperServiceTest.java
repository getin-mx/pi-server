package mobi.allshoppings.cinepolis.adapters;

import java.text.SimpleDateFormat;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.services.DashboardMapperService;

import org.junit.Test;

public class DashboardMapperServiceTest extends TestCase {

	@Test
	public void test0001() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DashboardMapperService service = new DashboardMapperService();
			service.createTicketPerformanceDashboardForDay(sdf.parse("2015-08-04 18:31:22"));
			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}


	@Test
	public void test0002() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DashboardMapperService service = new DashboardMapperService();
			service.createPromoPerformanceDashboardForDay(sdf.parse("2015-07-09 18:31:22"));
			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}


	@Test
	public void test0003() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DashboardMapperService service = new DashboardMapperService();
			service.createCheckinPerformanceDashboardForDay(sdf.parse("2015-07-09 18:31:22"));
			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0004() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DashboardMapperService service = new DashboardMapperService();
			service.createHeatmapDashboardForDay("/tmp/dump", sdf.parse("2015-08-17 18:31:22"));
			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}
}
