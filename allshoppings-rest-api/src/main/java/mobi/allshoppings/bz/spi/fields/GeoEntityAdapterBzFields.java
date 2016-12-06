package mobi.allshoppings.bz.spi.fields;

import java.util.Arrays;


public class GeoEntityAdapterBzFields extends BzFields {

	protected GeoEntityAdapterBzFields() {
		INTERNAL_ALWAYS_FIELDS.addAll(Arrays.asList(
				"lat",
				"lon"
				));
		INTERNAL_DEFAULT_FIELDS.addAll(Arrays.asList(
				"lat",
				"lon"
				));
		INTERNAL_INVALID_FIELDS.addAll(Arrays.asList(
				"key"
				));
		INTERNAL_READONLY_FIELDS.addAll(Arrays.asList(
				"key"
				));
		INTERNAL_LEVEL_LIST_FIELDS.addAll(Arrays.asList(
				"lat",
				"lon"
				));
		INTERNAL_LEVEL_ALL_FIELDS.addAll(Arrays.asList(
				"lat",
				"lon"
				));
		INTERNAL_LEVEL_FAST_FIELDS.addAll(Arrays.asList(
				"lat",
				"lon"
				));
		INTERNAL_LEVEL_PUBLIC_FIELDS.addAll(Arrays.asList(
				"lat",
				"lon"
				));
	}
}