package mobi.allshoppings.bz.spi.fields;

import java.util.Arrays;


public class WifiSpotBzFields extends BzFields {

	protected WifiSpotBzFields() {
		INTERNAL_ALWAYS_FIELDS.addAll(Arrays.asList(
				"identifier"
				));
		INTERNAL_DEFAULT_FIELDS.addAll(Arrays.asList(
				"identifier"
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
				"uid",
				"shoppingId",
				"floorMapId",
				"data",
				"recordStrategy",
				"calculusStrategy",
				"zoneName",
				"apDevice",
				"measures",
				"x",
				"y",
				"creationDateTime",
				"lastUpdate"
				));
		INTERNAL_LEVEL_ALL_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
		INTERNAL_INDEXING_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
		INTERNAL_LEVEL_FAST_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
		INTERNAL_LEVEL_PUBLIC_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
	}
}