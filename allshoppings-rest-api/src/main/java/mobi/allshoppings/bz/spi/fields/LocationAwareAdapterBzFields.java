package mobi.allshoppings.bz.spi.fields;

import java.util.Arrays;


public class LocationAwareAdapterBzFields extends BzFields {

	protected LocationAwareAdapterBzFields() {
		INTERNAL_ALWAYS_FIELDS.addAll(Arrays.asList(
				"identifier"
				));
		INTERNAL_DEFAULT_FIELDS.addAll(Arrays.asList(
				"identifier",
				"kind",
				"name",
				"avatarId",
				"favorite",
				"lat",
				"lon",
				"description",
				"brandName",
				"offerTypeId",
				"distance"
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
				"kind",
				"name",
				"avatarId",
				"favorite",
				"lat",
				"lon",
				"description",
				"brandName",
				"offerTypeId",
				"distance"
				));
		INTERNAL_LEVEL_ALL_FIELDS.addAll(Arrays.asList(
				"identifier",
				"kind",
				"name",
				"avatarId",
				"favorite",
				"lat",
				"lon",
				"description",
				"brandName",
				"offerTypeId",
				"distance"
				));
		INTERNAL_LEVEL_FAST_FIELDS.addAll(Arrays.asList(
				"identifier",
				"kind",
				"name",
				"avatarId",
				"favorite",
				"lat",
				"lon",
				"description",
				"brandName",
				"offerTypeId",
				"distance"
				));
		INTERNAL_LEVEL_PUBLIC_FIELDS.addAll(Arrays.asList(
				"identifier",
				"kind",
				"name",
				"avatarId",
				"favorite",
				"lat",
				"lon",
				"description",
				"brandName",
				"offerTypeId",
				"distance"
				));
	}
}