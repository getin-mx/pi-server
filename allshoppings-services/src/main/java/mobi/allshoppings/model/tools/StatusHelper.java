package mobi.allshoppings.model.tools;

import java.util.Arrays;
import java.util.List;

import mobi.allshoppings.model.interfaces.StatusAware;

public class StatusHelper {

	public static List<Byte> statusActive() {
		return Arrays.asList(new Byte[] {StatusAware.STATUS_ENABLED});
	}

	public static List<Byte> statusNotDisabled() {
		return Arrays.asList(new Byte[] {StatusAware.STATUS_ENABLED, StatusAware.STATUS_PENDING});
	}

	public static List<Byte> statusNotificationVisible() {
		return Arrays.asList(new Byte[] {StatusAware.STATUS_NEW, StatusAware.STATUS_VIEWED});
	}
}
