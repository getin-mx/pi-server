package mobi.allshoppings.bz.spi.fields;

import java.util.Arrays;

public class APDeviceBzFields extends BzFields {

	protected APDeviceBzFields() {
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
				"shoppingId",
				"brandId",
				"storeId",
				"description",
				"visitTimeThreshold",
				"visitGapThreshold",
				"visitPowerThreshold",
				"visitMaxThreshold",
				"daysToNotRepit",
				"reportable",
				"reportStatus",
				"reportMailList",
				"status",
				"creationDateTime",
				"lastRecordDate",
				"lastRecordCount",
				"lastUpdate"
				));
		INTERNAL_LEVEL_ALL_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
		INTERNAL_LEVEL_FAST_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
		INTERNAL_LEVEL_PUBLIC_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
	}
}