package mobi.allshoppings.dao;

import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.model.GeoEntity;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.adapter.LocationAwareAdapter;
import mobi.allshoppings.tx.PersistenceProvider;

public interface GeoEntityDAO extends GenericDAO<GeoEntity> {

	Key createKey() throws ASException;
	List<GeoEntity> getUsingEntityAndKind(String entityId, byte entityKind) throws ASException;
	List<GeoEntity> getUsingEntityAndKind(String entityId, byte entityKind, boolean detachable) throws ASException;
	List<GeoEntity> getUsingEntityAndKind(PersistenceProvider pp, String entityId, byte entityKind, boolean detachable) throws ASException;
	GeoEntity getUniqueUsingEntityAndKind(String entityId, byte entityKind) throws ASException;
	List<GeoEntity> getByProximity(GeoPoint geo, byte entityKind, int presition,
			boolean includeAdjacents, boolean independentOnly, boolean detachable) throws ASException;

	Shopping getNearestShopping(GeoPoint geo) throws ASException;
	Shopping getNearestShopping(GeoPoint geo, boolean detachable) throws ASException;

	LocationAwareAdapter getNearestInterestingPoint(GeoPoint geo) throws ASException;
	LocationAwareAdapter getNearestInterestingPoint(GeoPoint geo, boolean detachable) throws ASException;
}
