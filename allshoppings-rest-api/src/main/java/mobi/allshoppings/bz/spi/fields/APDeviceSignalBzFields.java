package mobi.allshoppings.bz.spi.fields;

import java.util.Arrays;

public class APDeviceSignalBzFields extends BzFields {

	protected APDeviceSignalBzFields() {
		INTERNAL_ALWAYS_FIELDS.addAll(Arrays.asList(
				"hostname"
				));
		INTERNAL_DEFAULT_FIELDS.addAll(Arrays.asList(
				"hostname"
				));
		INTERNAL_INVALID_FIELDS.addAll(Arrays.asList(
				"key"
				));
		INTERNAL_READONLY_FIELDS.addAll(Arrays.asList(
				"identifier",
				"key"
				));
		INTERNAL_LEVEL_LIST_FIELDS.addAll(Arrays.asList(
				"identifier",
				"hostname",
				"mac",
				"rssi",
				"distance",
				"deviceUUID",
				"insideStore",
				"creationDateTime",
				"lastUpdate"
				));
		INTERNAL_LEVEL_ALL_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
		INTERNAL_LEVEL_FAST_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
		INTERNAL_LEVEL_PUBLIC_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
	}
}