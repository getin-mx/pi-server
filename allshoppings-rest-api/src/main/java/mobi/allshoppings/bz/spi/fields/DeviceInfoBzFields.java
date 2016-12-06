package mobi.allshoppings.bz.spi.fields;

import java.util.Arrays;


public class DeviceInfoBzFields extends BzFields {

	protected DeviceInfoBzFields() {
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
				"deviceName",
				"devicePlatform",
				"deviceVersion",
				"deviceUUID",
				"appVersion",
				"messagingToken",
				"mac",
				"lastUpdate",
				"creationDateTime"
				));
		INTERNAL_LEVEL_ALL_FIELDS.addAll(Arrays.asList(
				"identifier",
				"userId",
				"userName",
				"deviceName",
				"devicePlatform",
				"deviceVersion",
				"deviceUUID",
				"appVersion",
				"apiVersion",
				"messagingToken",
				"messagingSandbox",
				"lang",
				"lastUpdate",
				"creationDateTime",
				"status",
				"appId",
				"mac"
				));
		INTERNAL_LEVEL_FAST_FIELDS.addAll(Arrays.asList(
				"identifier",
				"userId",
				"userName",
				"deviceName",
				"devicePlatform",
				"deviceVersion",
				"deviceUUID",
				"appVersion",
				"mac",
				"lastUpdate",
				"creationDateTime"
				));
		INTERNAL_LEVEL_PUBLIC_FIELDS.addAll(Arrays.asList(
				"identifier",
				"userId",
				"userName",
				"deviceName",
				"devicePlatform",
				"deviceVersion",
				"deviceUUID",
				"appVersion",
				"mac",
				"lastUpdate",
				"creationDateTime"
				));
	}
}