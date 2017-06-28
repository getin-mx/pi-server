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

	void generateAPDVisits(List<String> brandIds, List<String> storeIds, Date fromDate, Date toDate,
			boolean deletePreviousRecords, boolean updateDashboards, boolean onlyEmployees, boolean onlyDashboards)
			throws ASException;

	void generateAPDVisits(List<String> shoppingIds, Date fromDate, Date toDate, boolean deletePreviousRecords,
			boolean updateDashboards, boolean onlyEmployees, boolean onlyDashboards) throws ASException;

	List<APDVisit> aphEntryToVisits(APHEntry entry, Map<String, APDevice> apdCache,
			Map<String, APDAssignation> assignmentsCache, List<String> blackListMacs, List<String> employeeListMacs)
			throws ASException;

	List<APDVisit> aphEntryToVisits(List<APHEntry> entries, Map<String, APDevice> apdCache,
			Map<String, APDAssignation> assignmentsCache, List<String> blackListMacs, List<String> employeeListMacs)
			throws ASException;

	void fakeVisitsWith(String storeId, String fakeWithStoreId, Date fromDate, Date toDate) throws ASException;
	
}
