package mobi.allshoppings.bz.spi.fields;

import java.util.Arrays;


public class DeviceMessageLockBzFields extends BzFields {

	protected DeviceMessageLockBzFields() {
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
				"deviceId",
				"userId",
				"entityId",
				"entityKind",
				"subEntityId",
				"subEntityKind",
				"scope",
				"campaignActivityId",
				"fromDate",
				"toDate",
				"lastUpdate",
				"creationDateTime"
				));
		INTERNAL_LEVEL_ALL_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
		INTERNAL_LEVEL_FAST_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
		INTERNAL_LEVEL_PUBLIC_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
	}
}