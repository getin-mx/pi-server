package mobi.allshoppings.bz.spi.fields;

import java.util.Arrays;


public class SystemConfigurationBzFields extends BzFields {

	protected SystemConfigurationBzFields() {
		INTERNAL_LEVEL_ALL_FIELDS.addAll(Arrays.asList(
				"authTokenValidityInDays",
				"appTokenValidityInDays",
				"defaultLevel",
				"maxUploadSize",
				"baseShareURL",
				"baseImageURL",
				"facebookShareCaption",
				"homeCarouselImages",
				"maxNearMeLocations",
				"defaultViewLocationCountry",
				"saveLocationHistory",
				"pushMessageResponseUrl",
				"defaultFenceSize",
				"defaultCheckinAreaSize",
				"defaultLang",
				"deviceLocationRequestInterval",
				"deviceLocationReportInterval",
				"generalAppMail",
				"generalAppMailName",
				"staticContentURL",
				"passwordRecoveryUrl",
				"defaultCountry",
				"defaultGeoEntityPresition",
				"checkinCloseLimitMillis",
				"defaultMessageLockTimeMillis",
				"beaconProximityUUID",
				"defaultRejectionLock",
				"defaultAcceptanceLock",
				"defaultProximityLock",
				"deviceLocationPullQueueCount",
				"defaultAppId",
				"cinepolisHotspots",
				"defaultBehavioursApps"));
		INTERNAL_ALWAYS_FIELDS.addAll(INTERNAL_LEVEL_ALL_FIELDS);
		INTERNAL_DEFAULT_FIELDS.addAll(INTERNAL_LEVEL_ALL_FIELDS);
		INTERNAL_READONLY_FIELDS.addAll(INTERNAL_LEVEL_ALL_FIELDS);
		INTERNAL_LEVEL_LIST_FIELDS.addAll(INTERNAL_LEVEL_ALL_FIELDS);
		INTERNAL_LEVEL_FAST_FIELDS.addAll(INTERNAL_LEVEL_ALL_FIELDS);
		INTERNAL_LEVEL_PUBLIC_FIELDS.addAll(INTERNAL_LEVEL_ALL_FIELDS);
	}
}