package mobi.allshoppings.location.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumperHelperImpl;
import mobi.allshoppings.model.DeviceLocationHistory;

public class LocationHistoryDumperServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {
			
			DumperHelper<DeviceLocationHistory> dumper = new DumperHelperImpl<DeviceLocationHistory>("/tmp/dumps", DeviceLocationHistory.class);
			dumper.dumpModelKey("DeviceLocationHistory", null, null, false, false);

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0002() {
		try {
			
			int count1 = 0;
			int count2 = 0;
			int count3 = 0;
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH");
			DumperHelper<DeviceLocationHistory> dumper = new DumperHelperImpl<DeviceLocationHistory>("/tmp/dump", DeviceLocationHistory.class);
			Iterator<DeviceLocationHistory> i = dumper.iterator(sdf.parse("2015-06-01:00"), sdf.parse("2015-07-01:00"));
			Date check = new Date(0);
			while(i.hasNext()) {
				DeviceLocationHistory obj = i.next();
				System.out.println(obj.getCreationDateTime());
				if(obj.getCreationDateTime() != null ) {
					Date checkDate = new Date(obj.getCreationDateTime().getTime() + 60000);
					if( checkDate.before(check)) {
						count1++;
						System.out.println("Fecha mal ordenada");
					}
				}
				check = new Date(obj.getCreationDateTime().getTime());
			}

			System.out.println("Last Update.....................................");

			i = dumper.iterator(sdf.parse("2015-06-01:00"), sdf.parse("2015-07-01:00"));
			check = new Date(0);
			while(i.hasNext()) {
				DeviceLocationHistory obj = i.next();
				System.out.println(obj.getLastUpdate());
				if(obj.getLastUpdate() != null ) {
					Date checkDate = new Date(obj.getCreationDateTime().getTime() + 60000);
					if( checkDate.before(check)) {
						count2++;
						System.out.println("Fecha mal ordenada");
					}
				}
				if( obj.getLastUpdate() != null ) {
					check = new Date(obj.getLastUpdate().getTime());
				} else {
					count3++;
				}
			}

			System.out.println( count1 + " mal ordenanas en creationDateTime y " + count2 + " mal ordenadas en lastUpdate y " + count3 + " null");
			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}
	
	@Test
	public void test0003() {
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			DumperHelper<DeviceLocationHistory> dumper = new DumperHelperImpl<DeviceLocationHistory>("/tmp/dump", DeviceLocationHistory.class);
			dumper.fakeModelKey(sdf.parse("2015-06-26"), sdf.parse("2015-07-01"));

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
