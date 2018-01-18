package mobi.allshoppings.apdevice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.model.Store;
import mx.getin.model.APDCalibration;

public interface APDVisitHelper {

	static final SimpleDateFormat tf2 = new SimpleDateFormat("HHmm");
	
	void generateAPDVisits(List<String> brandIds, List<String> storeIds, Date fromDate, Date toDate,
			boolean deletePreviousRecords, boolean updateDashboards, boolean onlyEmployees, boolean onlyDashboards)
			throws ASException;

	/*void generateAPDVisits(List<String> shoppingIds, Date fromDate, Date toDate, boolean deletePreviousRecords,
			boolean updateDashboards, boolean onlyEmployees, boolean onlyDashboards) throws ASException;*/

	/*List<APDVisit> aphEntryToVisits(APHEntry entry, Map<String, APDevice> apdCache,
			Map<String, APDAssignation> assignmentsCache, List<String> blackListMacs,
			List<String> employeeListMacs, TimeZone tz) throws ASException;*/

	List<APDVisit> aphEntryToVisits(List<APHEntry> entries, Map<String, APDCalibration> apdCache,
			Map<String, APDAssignation> assignmentsCache, List<String> blackListMacs,
			List<String> employeeListMacs, TimeZone tz) throws ASException;

	void fakeVisitsWith(Store store, Date copyFromDate, Date copyToDate,
			Date insertFromDate) throws ASException;
	
	/**
	 * Checks if a visit is valid according the device parameters
	 * 
	 * @param visit
	 *            The visit to check
	 * @param device
	 *            The device that contains the parameters
	 * @return true if valid, false if not
	 * @throws ParseException
	 */
	public static boolean isVisitValid(APDVisit visit, APDCalibration device, boolean isEmployee, Calendar cal,
			TimeZone tz) throws ParseException {
		
		int t;
		cal.clear();
		if(visit != null) {
			Long time = (visit.getCheckinFinished().getTime()  -visit.getCheckinStarted().getTime()) / 60000;
			
			// Validate Minimum time for visit
			if( time < device.getVisitMinTimeThreshold()) return false;

			// Validate Maximum time for visit  
			if( time > device.getVisitMaxThreshold()) return false;

			// Employees doesn't generate visits
			if( isEmployee ) return false;
			
			// Total segments percentage check 
			if( null != visit.getInRangeSegments() && visit.getInRangeSegments() > 0 
					&& null != visit.getTotalSegments() && visit.getTotalSegments() > 0 &&
					visit.getInRangeSegments() * 100 / visit.getTotalSegments() <
					device.getVisitMinTimeSlotPercentage()) return false;
			
			visit.setDuration(time);
			
			t = Integer.valueOf(tf2.format(visit.getCheckinStarted()));
			cal.setTime(visit.getCheckinStarted());
		} else {
			long time = System.currentTimeMillis();
			t = Integer.valueOf(tf2.format(time));
			cal.setTimeInMillis(time);
		}
		
		// Validate Monitor Hour & days  
		
		tf2.setTimeZone(tz);
		int ts = 0;
		int te = 0;
		switch(cal.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SUNDAY :
			if(!device.getVisitsOnSun()) return false;
			ts = Integer.valueOf(device.getVisitStartSun().substring(0,2)
					+device.getVisitStartSun().substring(3));
			te = Integer.valueOf(device.getVisitEndSun().substring(0, 2)
					+device.getVisitEndSun().substring(3));
			break;
		case Calendar.MONDAY :
			if(!device.getVisitsOnMon()) return false;
			ts = Integer.valueOf(device.getVisitStartMon().substring(0, 2)
					+device.getVisitStartMon().substring(3));
			te = Integer.valueOf(device.getVisitEndMon().substring(0, 2)
					+device.getVisitEndMon().substring(3));
			break;
		case Calendar.TUESDAY :
			if(!device.getVisitsOnTue()) return false;
			ts = Integer.valueOf(device.getVisitStartTue().substring(0, 2)
					+device.getVisitStartTue().substring(3));
			te = Integer.valueOf(device.getVisitEndTue().substring(0, 2)
					+device.getVisitEndTue().substring(3));
			break;
		case Calendar.WEDNESDAY :
			if(!device.getVisitsOnWed()) return false;
			ts = Integer.valueOf(device.getVisitStartWed().substring(0, 2)
					+device.getVisitStartWed().substring(3));
			te = Integer.valueOf(device.getVisitEndWed().substring(0, 2)
					+device.getVisitEndWed().substring(3));
			break;
		case Calendar.THURSDAY :
			if(!device.getVisitsOnThu()) return false;
			ts = Integer.valueOf(device.getVisitStartThu().substring(0, 2)
					+device.getVisitStartThu().substring(3));
			te = Integer.valueOf(device.getVisitEndThu().substring(0, 2)
					+device.getVisitEndThu().substring(3));
			break;
		case Calendar.FRIDAY :
			if(!device.getVisitsOnFri()) return false;
			ts = Integer.valueOf(device.getVisitStartFri().substring(0, 2)
					+device.getVisitStartFri().substring(3));
			te = Integer.valueOf(device.getVisitEndFri().substring(0, 2)
					+device.getVisitEndFri().substring(3));
			break;
		case Calendar.SATURDAY :
			if(!device.getVisitsOnSat()) return false;
			ts = Integer.valueOf(device.getVisitStartSat().substring(0, 2)
					+device.getVisitStartSat().substring(3));
			te = Integer.valueOf(device.getVisitEndSat().substring(0, 2)
					+device.getVisitEndSat().substring(3));
			break;
		} if(te == 0) te = 2400;

		if( ts > te ) te += 2400;
		return te > t && t >= ts;
	}
	
}
