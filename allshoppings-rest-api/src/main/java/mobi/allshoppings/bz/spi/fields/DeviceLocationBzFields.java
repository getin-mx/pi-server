package mobi.allshoppings.bz.spi.fields;

import java.util.Arrays;


public class DeviceLocationBzFields extends BzFields {

	protected DeviceLocationBzFields() {
		INTERNAL_ALWAYS_FIELDS.addAll(Arrays.asList(
				"deviceUUID"
				));
		INTERNAL_DEFAULT_FIELDS.addAll(Arrays.asList(
				"deviceUUID"
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
				"userId",
				"userName",
				"deviceUUID",
				"lat",
				"lon",
				"geohash",
				"connection",
				"country",
				"province",
				"city",
				"nearSpots",
				"requestInterval",
				"reportInterval",
				"beaconProximityUUID",
				"lastUpdate",
				"creationDateTime",
				"precision",
				"operator",
				"signal",
				"roaming"
				));
		INTERNAL_LEVEL_ALL_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
		INTERNAL_LEVEL_FAST_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
		INTERNAL_LEVEL_PUBLIC_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
	}
}