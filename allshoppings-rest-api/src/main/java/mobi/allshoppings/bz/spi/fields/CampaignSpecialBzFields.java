package mobi.allshoppings.bz.spi.fields;

import java.util.Arrays;

public class CampaignSpecialBzFields extends BzFields {

	protected CampaignSpecialBzFields() {
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
				"brands",
				"offerTypeId",
				"offerTypeName",
				"offerTypeRibbonText",
				"name",
				"avatarId",
				"validFrom",
				"validTo",
				"lastUpdate",
				"expired"
				));
		INTERNAL_LEVEL_ALL_FIELDS.addAll(Arrays.asList(
				"identifier",
				"shoppings",
				"brands",
				"stores",
				"offerTypeId",
				"offerTypeName",
				"offerTypeRibbonText",
				"areaId",
				"name",
				"campaignId",
				"description",
				"instructions",
				"avatarId",
				"photoId",
				"videoId",
				"creationDateTime",
				"policies",
				"validFrom",
				"validTo",
				"lastUpdate",
				"availableFinancialEntities",
				"notifyFromHour",
				"notifyToHour",
				"notifyDays",
				"genders",
				"ageFrom",
				"ageTo",
				"country",
				"timezone",
				"trigger",
				"span",
				"quantity",
				"dailyQuantity",
				"customUrl",
				"promotionType",
				"acl",
				"favorite",
				"requester",
				"expired"
				));
		INTERNAL_LEVEL_FAST_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
		INTERNAL_LEVEL_PUBLIC_FIELDS.addAll(INTERNAL_LEVEL_LIST_FIELDS);
	}
}