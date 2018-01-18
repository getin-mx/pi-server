package mobi.allshoppings.geocoding;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Address;
import mobi.allshoppings.model.AddressComponentsCache;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.Offer;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;

public interface GeoCodingHelper {
	
    public GeoPoint getGeoPointFromAddress(String address) throws ASException;
    public GeoPoint getGeoPoint(double lat, double lon) throws ASException;
	GeoPoint getGeoPoint(double lat, double lon, String deviceUUID) throws ASException;
    public void rebuildShoppingGeoPoints() throws ASException;
    public void rebuildShoppingGeoEntities() throws ASException;
    
	void updateShoppingGeoPoint(Shopping obj) throws ASException;
	void rebuildBrandGeoEntities() throws ASException;
	void rebuildOfferGeoEntities() throws ASException;
	void addGeoEntity(String identifier, byte entityKind) throws ASException;
	void addGeoEntity(Shopping obj) throws ASException;
	void addGeoEntity(Brand obj) throws ASException;
	void addGeoEntity(Store obj) throws ASException;
	void addGeoEntity(Offer obj) throws ASException;
	void removeGeoEntity(String identifier, byte entityKind) throws ASException;
	void evictDeadOffers() throws ASException;

	void addUserDefaultLocation() throws ASException;
	void addBrandDefaultLocation() throws ASException;
	void addStoreDefaultLocation() throws ASException;
	void addOfferDefaultLocation() throws ASException;

	void calculateBrandLocationUsingStores(Brand brand) throws ASException;
	void calculateStoreLocation(Store store) throws ASException;
	String[] getAdjacentPoints(GeoPoint geo, int presition);
	
	String encodeGeohash(double lat, double lon);
	GeoPoint decodeGeohash(String geohash);
	Integer calculateDistance(double myLat, double myLon, double otherLat, double otherLon);

	boolean isUserNearShopping(User user, Set<String>candidates);

	String getCountryUsingCityAndState(String cityCommaState) throws IOException, URISyntaxException;
	AddressComponentsCache getAddressHLComponents(double lat, double lon) throws ASException;
	Address getAddressUsingGeohash(String geohash) throws IOException, URISyntaxException;
	
}
