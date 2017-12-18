package mobi.allshoppings.model;

import java.util.HashMap;
import java.util.Map;

public class EntityKind {

	public static final byte NONE = -2;
	public static final byte ALL = -1;
	public static final byte KIND_SHOPPING = 0;
	public static final byte KIND_BRAND = 1; 
	public static final byte KIND_OFFER = 2;
	public static final byte KIND_STORE = 3;
	public static final byte KIND_COMMENT = 4;
	public static final byte KIND_USER = 5;
	public static final byte KIND_FINANCIAL_ENTITY = 6;
	public static final byte KIND_AREA = 7;
	public static final byte KIND_SERVICE = 8;
	public static final byte KIND_OFFER_TYPE = 9;
	public static final byte KIND_NOTIFICATION_LOG = 10;
	public static final byte KIND_REWARD = 11;
	public static final byte KIND_OFFER_PROPOSAL = 12;
	public static final byte KIND_CAMPAIGN_ACTION = 13;
	public static final byte KIND_CHALLENGE = 14;
	public static final byte KIND_FLOOR_MAP = 15;
	public static final byte KIND_WIFI_SPOT = 16;
	public static final byte KIND_CAMPAIGN_ACTIVITY = 17;
	public static final byte KIND_GEO_ACTION_ZONE = 18;
	public static final byte KIND_PROCESS = 19;
	public static final byte KIND_INNER_ZONE = 20;
	
	public static final Map<String, Byte> kindMap = new HashMap<>();
	
	static {
		kindMap.put("shopping", KIND_SHOPPING);
		kindMap.put("shoppings", KIND_SHOPPING);
		kindMap.put("brand", KIND_BRAND);
		kindMap.put("brands", KIND_BRAND);
		kindMap.put("offer", KIND_OFFER);
		kindMap.put("offers", KIND_OFFER);
		kindMap.put("store", KIND_STORE);
		kindMap.put("stores", KIND_STORE);
		kindMap.put("comment", KIND_COMMENT);
		kindMap.put("comments", KIND_COMMENT);
		kindMap.put("user", KIND_USER);
		kindMap.put("users", KIND_USER);
		kindMap.put("area", KIND_AREA);
		kindMap.put("areas", KIND_AREA);
		kindMap.put("service", KIND_SERVICE);
		kindMap.put("services", KIND_SERVICE);
		kindMap.put("offerType", KIND_OFFER_TYPE);
		kindMap.put("offerTypes", KIND_OFFER_TYPE);
		kindMap.put("offertype", KIND_OFFER_TYPE);
		kindMap.put("offertypes", KIND_OFFER_TYPE);
		kindMap.put("financialEntity", KIND_FINANCIAL_ENTITY);
		kindMap.put("financialEntities", KIND_FINANCIAL_ENTITY);
		kindMap.put("financialentity", KIND_FINANCIAL_ENTITY);
		kindMap.put("financialentities", KIND_FINANCIAL_ENTITY);
		kindMap.put("notificationlog", KIND_NOTIFICATION_LOG);
		kindMap.put("notificationlogs", KIND_NOTIFICATION_LOG);
		kindMap.put("reward", KIND_REWARD);
		kindMap.put("rewards", KIND_REWARD);
		kindMap.put("offerproposal", KIND_OFFER_PROPOSAL);
		kindMap.put("offerproposals", KIND_OFFER_PROPOSAL);
		kindMap.put("campaignaction", KIND_CAMPAIGN_ACTION);
		kindMap.put("campaignactions", KIND_CAMPAIGN_ACTION);
		kindMap.put("challenge", KIND_CHALLENGE);
		kindMap.put("challenges", KIND_CHALLENGE);
		kindMap.put("floorMap", KIND_FLOOR_MAP);
		kindMap.put("floorMaps", KIND_FLOOR_MAP);
		kindMap.put("floormap", KIND_FLOOR_MAP);
		kindMap.put("floormaps", KIND_FLOOR_MAP);
		kindMap.put("wifiSpot", KIND_WIFI_SPOT);
		kindMap.put("wifiSpots", KIND_WIFI_SPOT);
		kindMap.put("wifispot", KIND_WIFI_SPOT);
		kindMap.put("wifispots", KIND_WIFI_SPOT);
		kindMap.put("campaignactivity", KIND_CAMPAIGN_ACTIVITY);
		kindMap.put("campaignActivity", KIND_CAMPAIGN_ACTIVITY);
		kindMap.put("campaignactivities", KIND_CAMPAIGN_ACTIVITY);
		kindMap.put("campaignActivities", KIND_CAMPAIGN_ACTIVITY);
		kindMap.put("geoactionzone", KIND_GEO_ACTION_ZONE);
		kindMap.put("geoActionZone", KIND_GEO_ACTION_ZONE);
		kindMap.put("geoactionzones", KIND_GEO_ACTION_ZONE);
		kindMap.put("geoActionZones", KIND_GEO_ACTION_ZONE);
		kindMap.put("process", KIND_PROCESS);
		kindMap.put("processes", KIND_PROCESS);
		kindMap.put("innerZone", KIND_INNER_ZONE);
		kindMap.put("innerzone", KIND_INNER_ZONE);
		kindMap.put("innerZones", KIND_INNER_ZONE);
		kindMap.put("innerzones", KIND_INNER_ZONE);
	}
	
	public static Class<?> getClassByKind(byte entityKind) {
		switch(entityKind) {
		case KIND_SHOPPING:
			return Shopping.class;
		case KIND_BRAND:
			return Brand.class;
		case KIND_STORE:
			return Store.class;
		case KIND_OFFER:
			return Offer.class;
		case KIND_COMMENT:
			return null;
		case KIND_USER:
			return User.class;
		case KIND_FINANCIAL_ENTITY:
			return FinancialEntity.class;
		case KIND_AREA:
			return Area.class;
		case KIND_SERVICE:
			return Service.class;
		case KIND_OFFER_TYPE:
			return OfferType.class;
		case KIND_NOTIFICATION_LOG:
			return NotificationLog.class;
		case KIND_REWARD:
			return null;
		case KIND_OFFER_PROPOSAL:
			return null;
		case KIND_CAMPAIGN_ACTION:
			return CampaignAction.class;
		case KIND_CHALLENGE:
			return null;
		case KIND_FLOOR_MAP:
			return FloorMap.class;
		case KIND_WIFI_SPOT:
			return WifiSpot.class;
		case KIND_CAMPAIGN_ACTIVITY:
			return CampaignActivity.class;
		case KIND_GEO_ACTION_ZONE:
			return null;
		case KIND_PROCESS:
			return Process.class;
		case KIND_INNER_ZONE:
			return InnerZone.class;
		default:
			return null;
		}
	}
	
	public static byte resolveByName(String name) {
		if(name == null ) return -1;
		String parts[] = name.toLowerCase().split("\\.");
		return kindMap.get(parts[parts.length - 1]);
	}
	
	public static byte resolveByClass(Class<?> clazz) {
		String clazzName = clazz.getSimpleName().toLowerCase();
		return resolveByName(clazzName);
	}
	
	public static boolean isKindValid(byte entityKind) {
		return kindToString(entityKind, false) != null;
	}
	
	public static boolean isKindValidForGeo(Integer entityKind ) {
		if (entityKind == KIND_SHOPPING || entityKind == KIND_BRAND
				|| entityKind == KIND_STORE || entityKind == KIND_OFFER
				|| entityKind == KIND_REWARD || entityKind == KIND_CAMPAIGN_ACTION 
				|| entityKind == KIND_CHALLENGE || entityKind == KIND_GEO_ACTION_ZONE)
			return true;
		return false;
	}

	public static String getEntityKindName(int kind) {
		switch( kind ) {
		case EntityKind.KIND_SHOPPING:
			return "Mall";
		case EntityKind.KIND_BRAND:
			return "Marca";
		case EntityKind.KIND_OFFER:
			return "Beneficio";
		case EntityKind.KIND_STORE:
			return "Tienda";
		case EntityKind.KIND_COMMENT:
			return "Comentario";
		case EntityKind.KIND_USER:
			return "Usuario";
		case EntityKind.KIND_AREA:
			return "Rubro";
		case EntityKind.KIND_SERVICE:
			return "Servicio";
		case EntityKind.KIND_OFFER_TYPE:
			return "Tipo de Oferta";
		case EntityKind.KIND_FINANCIAL_ENTITY:
			return "Tarjeta";
		case EntityKind.KIND_NOTIFICATION_LOG:
			return "Notificacion";
		case EntityKind.KIND_REWARD:
			return "Premio";
		case EntityKind.KIND_OFFER_PROPOSAL:
			return "Oferta Propuesta";
		case EntityKind.KIND_CAMPAIGN_ACTION:
			return "Campa&ntilde;a";
		case EntityKind.KIND_CHALLENGE:
			return "Reto";
		case EntityKind.KIND_FLOOR_MAP:
			return "Mapa";
		case EntityKind.KIND_WIFI_SPOT:
			return "Ubicacion Wifi";
		case EntityKind.KIND_CAMPAIGN_ACTIVITY:
			return "Actividad de Campa&ntilde;a";
		case EntityKind.KIND_GEO_ACTION_ZONE:
			return "Geo Action Zone";
		case EntityKind.KIND_PROCESS:
			return "Proceso";
		case EntityKind.KIND_INNER_ZONE:
			return "Zona";
		}
		return null;
	}

	public static String kindToString(byte entityKind, boolean plural) {
		if( plural) {
			switch( entityKind ) {
			case EntityKind.KIND_SHOPPING:
				return "shoppings";
			case EntityKind.KIND_BRAND:
				return "brands";
			case EntityKind.KIND_OFFER:
				return "offers";
			case EntityKind.KIND_STORE:
				return "stores";
			case EntityKind.KIND_COMMENT:
				return "comments";
			case EntityKind.KIND_USER:
				return "users";
			case EntityKind.KIND_AREA:
				return "areas";
			case EntityKind.KIND_SERVICE:
				return "services";
			case EntityKind.KIND_OFFER_TYPE:
				return "offerTypes";
			case EntityKind.KIND_FINANCIAL_ENTITY:
				return "financialEntities";
			case EntityKind.KIND_NOTIFICATION_LOG:
				return "NotificationLogs";
			case EntityKind.KIND_REWARD:
				return "rewards";
			case EntityKind.KIND_OFFER_PROPOSAL:
				return "offerProposals";
			case EntityKind.KIND_CAMPAIGN_ACTION:
				return "campaignActions";
			case EntityKind.KIND_CHALLENGE:
				return "challenges";
			case EntityKind.KIND_FLOOR_MAP:
				return "floorMaps";
			case EntityKind.KIND_WIFI_SPOT:
				return "wifiSpots";
			case EntityKind.KIND_CAMPAIGN_ACTIVITY:
				return "campaginActivities";
			case EntityKind.KIND_GEO_ACTION_ZONE:
				return "geoActionZones";
			case EntityKind.KIND_PROCESS:
				return "processes";
			case EntityKind.KIND_INNER_ZONE:
				return "innerZones";
			}
		} else {
			switch( entityKind ) {
			case EntityKind.KIND_SHOPPING:
				return "shopping";
			case EntityKind.KIND_BRAND:
				return "brand";
			case EntityKind.KIND_OFFER:
				return "offer";
			case EntityKind.KIND_STORE:
				return "store";
			case EntityKind.KIND_COMMENT:
				return "comment";
			case EntityKind.KIND_USER:
				return "user";
			case EntityKind.KIND_AREA:
				return "area";
			case EntityKind.KIND_SERVICE:
				return "service";
			case EntityKind.KIND_OFFER_TYPE:
				return "offerType";
			case EntityKind.KIND_FINANCIAL_ENTITY:
				return "financialEntity";
			case EntityKind.KIND_NOTIFICATION_LOG:
				return "notificationLog";
			case EntityKind.KIND_REWARD:
				return "reward";
			case EntityKind.KIND_OFFER_PROPOSAL:
				return "offerProposal";
			case EntityKind.KIND_CAMPAIGN_ACTION:
				return "campaignAction";
			case EntityKind.KIND_CHALLENGE:
				return "challenge";
			case EntityKind.KIND_FLOOR_MAP:
				return "floorMap";
			case EntityKind.KIND_WIFI_SPOT:
				return "wifiSpot";
			case EntityKind.KIND_CAMPAIGN_ACTIVITY:
				return "campaignActivity";
			case EntityKind.KIND_GEO_ACTION_ZONE:
				return "geoActionZone";
			case EntityKind.KIND_PROCESS:
				return "process";
			case EntityKind.KIND_INNER_ZONE:
				return "innerZone";
			}
		}
		return null;
	}

}
