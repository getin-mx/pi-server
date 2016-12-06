package mobi.allshoppings.model.tools;

import java.util.Arrays;
import java.util.List;

import mobi.allshoppings.model.interfaces.StatusAware;

public class StatusHelper {

	public static List<Integer> statusActive() {
		return Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED});
	}

	public static List<Integer> statusNotDisabled() {
		return Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED, StatusAware.STATUS_PENDING});
	}

	public static List<Integer> statusNotificationVisible() {
		return Arrays.asList(new Integer[] {StatusAware.STATUS_NEW, StatusAware.STATUS_VIEWED});
	}
}
