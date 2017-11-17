package mobi.allshoppings.apdevice;

import java.util.Date;
import java.util.List;
import java.util.Map;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.model.Store;

public interface APDVisitHelper {

	public static final int DAY_IN_MILLIS = 86400000;
	public static final int SLOT_NUMBER_IN_DAY = DAY_IN_MILLIS /1000 /20;
	
	void generateAPDVisits(List<String> brandIds, List<String> storeIds, Date fromDate, Date toDate,
			boolean deletePreviousRecords, boolean updateDashboards, boolean onlyEmployees, boolean onlyDashboards)
			throws ASException;

	void generateAPDVisits(List<String> shoppingIds, Date fromDate, Date toDate, boolean deletePreviousRecords,
			boolean updateDashboards, boolean onlyEmployees, boolean onlyDashboards) throws ASException;

	List<APDVisit> aphEntryToVisits(APHEntry entry, Map<String, APDevice> apdCache,
			Map<String, APDAssignation> assignmentsCache, List<String> blackListMacs,
			List<String> employeeListMacs, Store store) throws ASException;

	List<APDVisit> aphEntryToVisits(List<APHEntry> entries, Map<String, APDevice> apdCache,
			Map<String, APDAssignation> assignmentsCache, List<String> blackListMacs,
			List<String> employeeListMacs, Store store) throws ASException;

	void fakeVisitsWith(String storeId, String fakeWithStoreId, Date fromDate, Date toDate) throws ASException;
	
	void fakeVisitsWith(Store store, Date copyFromDate, Date copyToDate,
			Date insertFromDate) throws ASException;
	
}
