package mobi.allshoppings.cinepolis.adapters;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.cinepolis.services.SendPromoTicketsService;

public class SendPromoTicketsServiceTester extends TestCase {

	public static List<String> products = Arrays.asList(new String[] {
			"1432724594627", // Crepas
			"1432724531038" //  Baguis
	});
			
	
	public static List<String> testDevices = Arrays.asList(new String[] {
			"309C51B1-B6B4-4529-A3C8-95996ED990A3", // iPhone 4s (Fer)
			"DD39A3CC-AE95-4B60-9ABC-44C3A0DF834E", //  iPhone 4 testing
			"083B37E5-36B0-4688-904B-1DDD9797E21A", //  iPhone 6+ testing
			"8ea3fc131a6c44de@cinepolis_mx", //  Samsumg G4 testing
			"38b172b6ca65b513@cinepolis_mx", //  Moto G2 (Testing Kari)
			"776AE2D6-840A-45BF-A1D0-3873B54D6D72", //  iPhone 4s (Mat)
			"c254507c3690abd9@cinepolis_mx", //  Samsung G1 (Mat)
			"4d36f854b29fcdb8@cinepolis_mx", //  Samsung G3 mini (Max)
			"146ad8700cf629ab@cinepolis_mx" //  Huawei (Romy)
	});
	
	@Test
	public void test0001() {
		try {

			System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(new Date()));
			SendPromoTicketsService service = new SendPromoTicketsService();
			service.doProcess(
					new Date(),
					"http://api.allshoppings.mobi/app/externalActivityTrigger?authToken="
							+ "2A2C192578F597CEE6BC3EE8A26B9F7DB0DBA4B0CD8930FDC3D2BB0E7D1B8CC9",
					3600000, Arrays.asList(new String[]{"776AE2D6-840A-45BF-A1D0-3873B54D6D72"}),
					Arrays.asList(new String[]{"776AE2D6-840A-45BF-A1D0-3873B54D6D72"}),
					Arrays.asList(new String[]{"cinepolis_mx_449"}), 
					false, true, "1432724531038", true, true);

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}


	@Test
	public void test0002() {
		try {

			SendPromoTicketsService service = new SendPromoTicketsService();
			service.doProcess(
					new Date(),
					"http://127.0.0.1:8080/app/externalActivityTrigger?authToken=" +
					"2A2C192578F597CEE6BC3EE8A26B9F7DB0DBA4B0CD8930FDC3D2BB0E7D1B8CC9",
					3600000 /* 1 hour */, Arrays.asList(new String[]{"776AE2D6-840A-45BF-A1D0-3873B54D6D72"}),
					Arrays.asList(new String[]{"776AE2D6-840A-45BF-A1D0-3873B54D6D72"}),
					Arrays.asList(new String[]{"cinepolis_mx_339"}), 
					false, true, null, true, true);

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0003() {
		try {

			SendPromoTicketsService service = new SendPromoTicketsService();
			service.doProcess(
					new Date(),
					"http://api.allshoppings.mobi/app/externalActivityTrigger?authToken="
							+ "2A2C192578F597CEE6BC3EE8A26B9F7DB0DBA4B0CD8930FDC3D2BB0E7D1B8CC9",
							3600000 /* 1 hour */,
							testDevices, null, Arrays.asList(new String[] { "cinepolis_mx_339" }), 
							true, true, null, true, true);


		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

	@Test
	public void test0004() {
		try {

			SendPromoTicketsService service = new SendPromoTicketsService();
			service.doProcess(
					new Date(),
					"http://api.allshoppings.mobi/app/externalActivityTrigger?authToken="
							+ "2A2C192578F597CEE6BC3EE8A26B9F7DB0DBA4B0CD8930FDC3D2BB0E7D1B8CC9",
							3600000 /* 1 hour */,
							null, null, Arrays.asList(new String[] { "cinepolis_mx_339", "cinepolis_mx_449", "cinepolis_mx_479" }), 
							false, true, null, false, true);


		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
