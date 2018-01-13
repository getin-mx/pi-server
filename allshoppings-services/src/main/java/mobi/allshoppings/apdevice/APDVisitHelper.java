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
import mx.getin.Constants;

public interface APDVisitHelper {

	public static final int SLOT_NUMBER_IN_DAY = Constants.DAY_IN_MILLIS /1000 /20;
	
	void generateAPDVisits(List<String> brandIds, List<String> storeIds, List<String> ignoredBrands,
			Date fromDate, Date toDate, boolean deletePreviousRecords, boolean updateDashboards,
			boolean onlyEmployees, boolean onlyDashboards, boolean isDailyProcess, byte startHour,
			byte endHour) throws ASException;

	List<APDVisit> aphEntryToVisits(List<APHEntry> entries, Map<String, APDevice> apdCache,
			Map<String, APDAssignation> assignmentsCache, List<String> blackListMacs,
			List<String> employeeListMacs, TimeZone tz, String date) throws ASException;	
}
