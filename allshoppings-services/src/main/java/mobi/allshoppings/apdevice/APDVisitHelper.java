package mobi.allshoppings.apdevice;

import java.util.Date;
import java.util.List;
import java.util.Map;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APHEntry;

public interface APDVisitHelper {

	List<APDVisit> aphEntryToVisits(APHEntry entry, Map<String, APDevice> adpCache, Map<String, APDAssignation> assignmentsCache) throws ASException;
	List<APDVisit> aphEntryToVisits(List<APHEntry> entries, Map<String, APDevice> adpCache, Map<String, APDAssignation> assignmentsCache) throws ASException;
	void generateAPDVisits(List<String> brandIds, List<String> storeIds, Date fromDate, Date toDate, boolean deletePreviousRecords, boolean updateDashboards) throws ASException;
	
}
