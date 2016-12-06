package mobi.allshoppings.bz.spi.fields;

import java.util.Arrays;


public class DeviceWifiLocationHistoryBzFields extends BzFields {

	protected DeviceWifiLocationHistoryBzFields() {
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
				"deviceUUID",
				"entityId",
				"entityKind",
				"wifiData",
				"lastUpdate",
				"creationDateTime"
				));
		INTERNAL_LEVEL_ALL_FIELDS.addAll(Arrays.asList(
				"identifier",
				"userId",
				"deviceUUID",
				"entityId",
				"entityKind",
				"wifiData",
				"lastUpdate",
				"creationDateTime"
				));
		INTERNAL_LEVEL_FAST_FIELDS.addAll(Arrays.asList(
				"identifier",
				"userId",
				"deviceUUID",
				"entityId",
				"entityKind",
				"wifiData",
				"lastUpdate",
				"creationDateTime"
				));
		INTERNAL_LEVEL_PUBLIC_FIELDS.addAll(Arrays.asList(
				"identifier",
				"userId",
				"deviceUUID",
				"entityId",
				"entityKind",
				"wifiData",
				"lastUpdate",
				"creationDateTime"
				));
	}
}