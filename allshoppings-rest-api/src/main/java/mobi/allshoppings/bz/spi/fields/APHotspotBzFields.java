package mobi.allshoppings.bz.spi.fields;

import java.util.Arrays;


public class APHotspotBzFields extends BzFields {

	protected APHotspotBzFields() {
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
				"hostname",
				"mac",
				"count",
				"firstSeen",
				"lastSeen",
				"signalDB",
				"wifiSpotId",
				"lastUpdate",
				"creationDateTime"
				));
		INTERNAL_LEVEL_ALL_FIELDS.addAll(Arrays.asList(
				"identifier",
				"hostname",
				"mac",
				"count",
				"firstSeen",
				"lastSeen",
				"signalDB",
				"wifiSpotId",
				"lastUpdate",
				"creationDateTime"
				));
		INTERNAL_LEVEL_FAST_FIELDS.addAll(Arrays.asList(
				"identifier",
				"hostname",
				"mac",
				"count",
				"firstSeen",
				"lastSeen",
				"signalDB",
				"wifiSpotId",
				"lastUpdate",
				"creationDateTime"
				));
		INTERNAL_LEVEL_PUBLIC_FIELDS.addAll(Arrays.asList(
				"identifier",
				"hostname",
				"mac",
				"count",
				"firstSeen",
				"lastSeen",
				"signalDB",
				"wifiSpotId",
				"lastUpdate",
				"creationDateTime"
				));
	}
}