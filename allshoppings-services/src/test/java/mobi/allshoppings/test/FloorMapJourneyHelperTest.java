package mobi.allshoppings.test;


import junit.framework.TestCase;
import mobi.allshoppings.apdevice.impl.FloorMapJourneyHelperImpl;
import mobi.allshoppings.tools.Range;

public class FloorMapJourneyHelperTest extends TestCase {
	
	@SuppressWarnings("unused")
	public void test0001() {
		FloorMapJourneyHelperImpl fmjh = new FloorMapJourneyHelperImpl();
		try {
			Range x =new Range(0, 100);
	//		fmjh.process(floorMapId, mac, fromDate, toDate, range, order)(10, x);
			
		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
