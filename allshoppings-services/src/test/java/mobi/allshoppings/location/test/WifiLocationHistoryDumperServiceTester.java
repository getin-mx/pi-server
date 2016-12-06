package mobi.allshoppings.location.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DeviceWifiLocationHistoryDumperPlugin;
import mobi.allshoppings.dump.impl.DumperHelperImpl;
import mobi.allshoppings.model.DeviceWifiLocationHistory;

import org.junit.Test;

public class WifiLocationHistoryDumperServiceTester extends TestCase {

	@Test
	public void test0001() {
		try {
			DumperHelper<DeviceWifiLocationHistory> dumper = new DumperHelperImpl<DeviceWifiLocationHistory>("/tmp/dumps", DeviceWifiLocationHistory.class);
			dumper.registerPlugin(new DeviceWifiLocationHistoryDumperPlugin());
			dumper.dumpModelKey("DeviceWifiLocationHistory", null, null, false, true);
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0002() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH");

			DumperHelper<DeviceWifiLocationHistory> dumper = new DumperHelperImpl<DeviceWifiLocationHistory>("/tmp/dumps", DeviceWifiLocationHistory.class);
			List<DeviceWifiLocationHistory> list = dumper.retrieveModelKeyList(sdf.parse("2015-06-01:00"), sdf.parse("2015-09-01:00"));

			System.out.println("List has " + list.size() + " elements");
			for( DeviceWifiLocationHistory element : list ) {
				System.out.println(element);
			}
			System.out.println("List has " + list.size() + " elements");

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0003() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH");

			DumperHelper<DeviceWifiLocationHistory> dumper = new DumperHelperImpl<DeviceWifiLocationHistory>("/tmp/dumps", DeviceWifiLocationHistory.class);
			List<DeviceWifiLocationHistory> list = dumper.retrieveModelKeyList(sdf.parse("2015-06-01:00"), sdf.parse("2015-09-01:00"));
			Iterator<DeviceWifiLocationHistory> i = dumper.iterator(sdf.parse("2015-06-01:00"), sdf.parse("2015-09-01:00"));

			int count = 0;
			while(i.hasNext()) {
				DeviceWifiLocationHistory element = i.next();
				System.out.println(element);

				if(!element.equals(list.get(count))) {
					System.out.println(list.get(count));
					System.out.println("Error at element " + count);
					break;
				}

				count++;
			}
			System.out.println("List has " + count + " elements");

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0004() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH");

			DumperHelper<DeviceWifiLocationHistory> dumper = new DumperHelperImpl<DeviceWifiLocationHistory>("/tmp/dumps", DeviceWifiLocationHistory.class);
			Iterator<DeviceWifiLocationHistory> i = dumper.iterator(sdf.parse("2015-08-10:12"), sdf.parse("2015-08-10:14"));

			int count = 0;
			while(i.hasNext()) {
				DeviceWifiLocationHistory element = i.next();
				System.out.println(element);
				count++;
			}
			System.out.println("List has " + count + " elements");

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0005() {
		try {

			int count1 = 0;
			int count2 = 0;
			int count3 = 0;

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH");
			DumperHelper<DeviceWifiLocationHistory> dumper = new DumperHelperImpl<DeviceWifiLocationHistory>("/home/mhapanowicz/workspace-aspi/dump", DeviceWifiLocationHistory.class);
			Iterator<DeviceWifiLocationHistory> i = dumper.iterator(sdf.parse("2016-02-14:00"), sdf.parse("2016-02-15:00"));
			Date check = new Date(0);
			while(i.hasNext()) {
				DeviceWifiLocationHistory obj = i.next();
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
				DeviceWifiLocationHistory obj = i.next();
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
}
