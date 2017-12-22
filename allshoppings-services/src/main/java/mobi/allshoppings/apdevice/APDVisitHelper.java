package mobi.allshoppings.apdevice;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APHEntry;

public interface APDVisitHelper {

	public static final int DAY_IN_MILLIS = 86400000;
	public static final int SLOT_NUMBER_IN_DAY = DAY_IN_MILLIS /1000 /20;
	
	void generateAPDVisits(List<String> brandIds, List<String> storeIds, Date fromDate, Date toDate,
			boolean deletePreviousRecords, boolean updateDashboards, boolean onlyEmployees, boolean onlyDashboards,
			boolean isDailyProcess) throws ASException;

	void generateAPDVisits(List<String> shoppingIds, Date fromDate, Date toDate, boolean deletePreviousRecords,
			boolean updateDashboards, boolean onlyEmployees, boolean onlyDashboards, boolean isDailyProcess)
					throws ASException;

	List<APDVisit> aphEntryToVisits(APHEntry entry, Map<String, APDevice> apdCache,
			Map<String, APDAssignation> assignmentsCache, List<String> blackListMacs,
			List<String> employeeListMacs, TimeZone tz) throws ASException;

	List<APDVisit> aphEntryToVisits(List<APHEntry> entries, Map<String, APDevice> apdCache,
			Map<String, APDAssignation> assignmentsCache, List<String> blackListMacs,
			List<String> employeeListMacs, TimeZone tz) throws ASException;	
}
