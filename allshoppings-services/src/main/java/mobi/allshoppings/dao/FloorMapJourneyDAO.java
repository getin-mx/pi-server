package mobi.allshoppings.dao;


import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.FloorMapJourney;
import mobi.allshoppings.tools.Range;

public interface FloorMapJourneyDAO extends GenericDAO<FloorMapJourney> {

	Key createKey(FloorMapJourney obj) throws ASException;
	List<FloorMapJourney> getUsingFloorMapAndMacAndDate(String floorMapId, String mac, String fromDate, String toDate, Range range, String order) throws ASException;
	
}
