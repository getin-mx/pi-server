package mobi.allshoppings.bz.spi.fields;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import mobi.allshoppings.bz.BzService;
import mobi.allshoppings.bz.spi.APDeviceSignalBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.APDeviceStatusBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.AreaListBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.BrandBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.BrandListBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.ClearMessageLockBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.CouponBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.CouponListBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.DashboardFloorMapDataBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.DeviceInfoBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.DeviceInfoBzServicePIJSONImpl;
import mobi.allshoppings.bz.spi.DeviceLocationBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.DeviceLocationBzServicePIJSONImpl;
import mobi.allshoppings.bz.spi.DeviceMessageLockBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.DeviceWifiLocationBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.FloorMapListBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.MyOffersBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.OfferBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.OfferListBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.OfferTypeListBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.ReportBeaconBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.ServiceListBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.ShoppingBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.ShoppingListBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.StoreBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.StoreListBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.SystemConfigurationBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.UserBzServiceJSONImpl;
import mobi.allshoppings.bz.spi.WifiSpotListBzServiceJSONImpl;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APDeviceSignal;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.model.Area;
import mobi.allshoppings.model.BeaconHotspot;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.DeviceLocation;
import mobi.allshoppings.model.DeviceLocationHistory;
import mobi.allshoppings.model.DeviceWifiLocationHistory;
import mobi.allshoppings.model.FloorMap;
import mobi.allshoppings.model.Offer;
import mobi.allshoppings.model.OfferType;
import mobi.allshoppings.model.Service;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.WifiSpot;
import mobi.allshoppings.model.adapter.IGenericAdapter;
import mobi.allshoppings.model.interfaces.ModelKey;


/**
 * Utility Class to handle which fields will be shown for a specific entity on
 * REST Requests
 * 
 * @author mhapanowicz
 * 
 */
public abstract class BzFields {

    // Fields to be filled by subclasses
    protected final Set<String> INTERNAL_DEFAULT_FIELDS       = new HashSet<String>();
    protected final Set<String> INTERNAL_ALWAYS_FIELDS        = new HashSet<String>();
    protected final Set<String> INTERNAL_INVALID_FIELDS       = new HashSet<String>();
    protected final Set<String> INTERNAL_READONLY_FIELDS      = new HashSet<String>();
    protected final Set<String> INTERNAL_LEVEL_LIST_FIELDS    = new HashSet<String>();
    protected final Set<String> INTERNAL_LEVEL_FAST_FIELDS    = new HashSet<String>();
    protected final Set<String> INTERNAL_LEVEL_ALL_FIELDS     = new HashSet<String>();
    protected final Set<String> INTERNAL_LEVEL_PUBLIC_FIELDS  = new HashSet<String>();
    protected final Set<String> INTERNAL_INDEXING_FIELDS  = new HashSet<String>();

    public final Set<String> DEFAULT_FIELDS      = Collections.unmodifiableSet(INTERNAL_DEFAULT_FIELDS);
    public final Set<String> ALWAYS_FIELDS       = Collections.unmodifiableSet(INTERNAL_ALWAYS_FIELDS);
    public final Set<String> INVALID_FIELDS      = Collections.unmodifiableSet(INTERNAL_INVALID_FIELDS);
    public final Set<String> READONLY_FIELDS     = Collections.unmodifiableSet(INTERNAL_READONLY_FIELDS);
    public final Set<String> LEVEL_ALL_FIELDS    = Collections.unmodifiableSet(INTERNAL_LEVEL_ALL_FIELDS);
    public final Set<String> LEVEL_LIST_FIELDS   = Collections.unmodifiableSet(INTERNAL_LEVEL_LIST_FIELDS);
    public final Set<String> LEVEL_FAST_FIELDS   = Collections.unmodifiableSet(INTERNAL_LEVEL_FAST_FIELDS);
    public final Set<String> LEVEL_PUBLIC_FIELDS = Collections.unmodifiableSet(INTERNAL_LEVEL_PUBLIC_FIELDS);
    public final Set<String> INDEXING_FIELDS 	 = Collections.unmodifiableSet(INTERNAL_INDEXING_FIELDS);

    public final static String LEVEL_ALL = "all";
    public final static String LEVEL_LIST = "list";
    public final static String LEVEL_FAST = "fast";
    public final static String LEVEL_PUBLIC = "all_public";
    public final static String INDEXING = "indexing";
    
    /**
     * This is quicker and dirtier than factory method pattern with class parameter, but
     * lots simpler
     * Implements a map that holds the relationship between the BzFields class and
     * its corresponding BzService class
     */
    public static final BzFields DEVICEINFO = new DeviceInfoBzFields();
    public static final BzFields DEVICELOCATION = new DeviceLocationBzFields();
    public static final BzFields DEVICEMESSAGELOCK = new DeviceMessageLockBzFields();
    public static final BzFields DEVICELOCATIONHISTORY = new DeviceLocationHistoryBzFields();
    public static final BzFields DEVICEWIFILOCATIONHISTORY = new DeviceWifiLocationHistoryBzFields();
    public static final BzFields BEACONHOTSPOT = new BeaconHotspotBzFields();
    public static final BzFields APDEVICE = new APDeviceBzFields();
    public static final BzFields APDEVICESIGNAL = new APDeviceSignalBzFields();
    public static final BzFields APHOTSPOT = new APHotspotBzFields();
    public static final BzFields FLOORMAP = new FloorMapBzFields();
    public static final BzFields WIFISPOT = new WifiSpotBzFields();
    public static final BzFields USER = new UserBzFields();
    public static final BzFields COUPON = new CouponBzFields();
    public static final BzFields CONFIG = new SystemConfigurationBzFields();
    public static final BzFields TABLE = new TableBzFields();
    public static final BzFields OFFER_TYPE = new OfferTypeBzFields();
    public static final BzFields BRAND = new BrandBzFields();
    public static final BzFields STORE = new StoreBzFields();
    public static final BzFields OFFER = new OfferBzFields();
    public static final BzFields SHOPPING = new ShoppingBzFields();

    private static final Map<Class<? extends BzService>,BzFields> fieldsMap;
    private static final Map<Class<? extends ModelKey>,BzFields> modelFieldsMap;
    private static final Map<Class<? extends IGenericAdapter>,BzFields> adapterFieldsMap;

    static {
        fieldsMap = new HashMap<Class<? extends BzService>, BzFields>();
        fieldsMap.put(UserBzServiceJSONImpl.class, USER);
        fieldsMap.put(DeviceWifiLocationBzServiceJSONImpl.class, DEVICEWIFILOCATIONHISTORY);
        fieldsMap.put(DeviceInfoBzServiceJSONImpl.class, DEVICEINFO);
        fieldsMap.put(DeviceInfoBzServicePIJSONImpl.class, DEVICEINFO);
        fieldsMap.put(DeviceLocationBzServiceJSONImpl.class, DEVICELOCATION);
        fieldsMap.put(DeviceLocationBzServicePIJSONImpl.class, DEVICELOCATION);
        fieldsMap.put(ReportBeaconBzServiceJSONImpl.class, BEACONHOTSPOT);
        fieldsMap.put(FloorMapListBzServiceJSONImpl.class, FLOORMAP);
        fieldsMap.put(WifiSpotListBzServiceJSONImpl.class, WIFISPOT);
        fieldsMap.put(DashboardFloorMapDataBzServiceJSONImpl.class, FLOORMAP);
        fieldsMap.put(DeviceMessageLockBzServiceJSONImpl.class, DEVICEMESSAGELOCK);
        fieldsMap.put(ClearMessageLockBzServiceJSONImpl.class, DEVICEMESSAGELOCK);
        fieldsMap.put(CouponListBzServiceJSONImpl.class, COUPON);
        fieldsMap.put(CouponBzServiceJSONImpl.class, COUPON);
        fieldsMap.put(SystemConfigurationBzServiceJSONImpl.class, CONFIG);
        fieldsMap.put(AreaListBzServiceJSONImpl.class, TABLE);
        fieldsMap.put(ServiceListBzServiceJSONImpl.class, TABLE);
        fieldsMap.put(OfferTypeListBzServiceJSONImpl.class, OFFER_TYPE);
        fieldsMap.put(BrandListBzServiceJSONImpl.class, BRAND);
        fieldsMap.put(BrandBzServiceJSONImpl.class, BRAND);
        fieldsMap.put(StoreListBzServiceJSONImpl.class, STORE);
        fieldsMap.put(StoreBzServiceJSONImpl.class, STORE);
        fieldsMap.put(OfferListBzServiceJSONImpl.class, OFFER);
        fieldsMap.put(OfferBzServiceJSONImpl.class, OFFER);
        fieldsMap.put(MyOffersBzServiceJSONImpl.class, OFFER);
        fieldsMap.put(ShoppingListBzServiceJSONImpl.class, SHOPPING);
        fieldsMap.put(ShoppingBzServiceJSONImpl.class, SHOPPING);
        fieldsMap.put(APDeviceStatusBzServiceJSONImpl.class, APDEVICE);
        fieldsMap.put(APDeviceSignalBzServiceJSONImpl.class, APDEVICESIGNAL);

        modelFieldsMap = new HashMap<Class<? extends ModelKey>, BzFields>();
        modelFieldsMap.put(DeviceInfo.class, DEVICEINFO);
        modelFieldsMap.put(DeviceLocation.class, DEVICELOCATION);
        modelFieldsMap.put(DeviceLocationHistory.class, DEVICELOCATIONHISTORY);
        modelFieldsMap.put(DeviceWifiLocationHistory.class, DEVICEWIFILOCATIONHISTORY);
        modelFieldsMap.put(APHotspot.class, APHOTSPOT);
        modelFieldsMap.put(BeaconHotspot.class, BEACONHOTSPOT);
        modelFieldsMap.put(FloorMap.class, FLOORMAP);
        modelFieldsMap.put(WifiSpot.class, WIFISPOT);
        modelFieldsMap.put(User.class, USER);
        modelFieldsMap.put(CampaignActivity.class, COUPON);
        modelFieldsMap.put(Area.class, TABLE);
        modelFieldsMap.put(Service.class, TABLE);
        modelFieldsMap.put(OfferType.class, OFFER_TYPE);
        modelFieldsMap.put(Brand.class, BRAND);
        modelFieldsMap.put(Offer.class, OFFER);
        modelFieldsMap.put(Store.class, STORE);
        modelFieldsMap.put(Shopping.class, SHOPPING);
        modelFieldsMap.put(APDevice.class, APDEVICE);
        modelFieldsMap.put(APDeviceSignal.class, APDEVICESIGNAL);

        adapterFieldsMap = new HashMap<Class<? extends IGenericAdapter>, BzFields>();
    
    }

	/**
	 * Obtains the output fields marked for an entity
	 * 
	 * @param level
	 *            The access level to filter
	 * @param fields
	 *            Field list required by the user.
	 * @return A list with the permitted fields to show.
	 */
    public Set<String> getOutputFields(final String level, final String fields) {
        if (LEVEL_ALL.equals(level)) {
            return getOutputFields(LEVEL_ALL_FIELDS, fields);
        }
        if (LEVEL_LIST.equals(level)) {
            return getOutputFields(LEVEL_LIST_FIELDS, fields);
        }
        if (LEVEL_FAST.equals(level)) {
            return getOutputFields(LEVEL_FAST_FIELDS, fields);
        }
        if (LEVEL_PUBLIC.equals(level)) {
            return getOutputFields(LEVEL_PUBLIC_FIELDS, fields);
        }
        if (INDEXING.equals(level)) {
            return getOutputFields(INDEXING_FIELDS, fields);
        }
        return getOutputFields(DEFAULT_FIELDS, fields);
    }

	/**
	 * Obtains the output fields marked for an entity
	 * 
	 * @param defaultFields
	 *            Default field list for an entity. This is set by the service
	 *            which requests this information.
	 * @param fields
	 *            Field list required by the user.
	 * @return A list with the permitted fields to show.
	 */
    private Set<String> getOutputFields(final Set<String> defaultFields, final String fields) {
        final Set<String> result = new HashSet<String>();

        if (fields != null) {
            final StringTokenizer tokenizer = new StringTokenizer(fields.trim(), ",");

            while (tokenizer.hasMoreTokens()) {
                final String fieldName = tokenizer.nextToken();
                if (fieldName != null && fieldName.length() > 0 && !isInvalidField(fieldName)) {
                    result.add(fieldName);
                }
            }
        }
        if (result.size() == 0) {
            return defaultFields;
        }
        return result;
    }
    
	/**
	 * Checks if a given field is invalid
	 * 
	 * @param fieldName
	 *            Field Name to Check
	 * @return true if the field is invalid, false otherwise
	 */
    public boolean isInvalidField(String fieldName) {
    	return isInvalidField(fieldName, INVALID_FIELDS);
    }

	/**
	 * Checks if a given field is invalid
	 * 
	 * @param fieldName
	 *            Field name to check
	 * @param fieldName
	 *            A set of invalid fields
	 * @return true if the field is invalid, false otherwise
	 */
    public static boolean isInvalidField(String fieldName, Set<String> invalidFields) {
    	boolean ret = false;
    	for(String invalidField : invalidFields) {
    		if( invalidField.equals(fieldName)) return true;
    		if( invalidField.endsWith("*") && fieldName.startsWith(
    				invalidField.substring(0, invalidField.length() - 1))) return true;
    	}
    	return ret;
    }
    
	/**
	 * Creates a field map for a specific entity class
	 * 
	 * @param clazz
	 *            The entity class to describe
	 * @return A BzFields object which allows to handle the file list
	 */
    public static BzFields getBzFields(Class<? extends BzService> clazz) {
        return fieldsMap.get(clazz);
    }

	/**
	 * Creates a field map for a specific entity class
	 * 
	 * @param clazz
	 *            The entity class to describe
	 * @return A BzFields object which allows to handle the file list
	 */
    public static BzFields getModelBzFields(Class<? extends ModelKey> clazz) {
        return modelFieldsMap.get(clazz);
    }
    
	/**
	 * Creates a field map for a specific adapter class
	 * 
	 * @param clazz
	 *            The entity class to describe
	 * @return A BzFields object which allows to handle the file list
	 */
    public static BzFields getAdapterBzFields(Class<? extends IGenericAdapter> clazz) {
    	return adapterFieldsMap.get(clazz);
    }
    
	/**
	 * Creates a field map for a specific class
	 * 
	 * @param clazz
	 *            The entity class to describe
	 * @return A BzFields object which allows to handle the file list
	 */
    public static BzFields guessBzFields(Class<?> clazz) {
    	BzFields ret = null;
    	ret = adapterFieldsMap.get(clazz);
    	if( ret == null ) ret = modelFieldsMap.get(clazz);
    	if( ret == null ) ret = fieldsMap.get(clazz);
    	return ret;
    }
}
