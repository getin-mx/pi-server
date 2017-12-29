package mobi.allshoppings.dashboards;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.icu.util.Calendar;

import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.dao.WeeklyDashboardIndicatorDataDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.WeeklyDashboardIndicatorData;
import mobi.allshoppings.tools.CollectionFactory;
import mx.getin.Constants;

public class DashboardResumeMapperService {

	public static final int PERIOD_WEEKLY = 0;
	public static final int PERIOD_MONTHLY = 0;
	public static final int PERIOD_QUARTERLY = 0;
	
	private static final Logger log = Logger.getLogger(DashboardResumeMapperService.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final TimeZone GMT = TimeZone.getTimeZone(Constants.GMT_TIMEZONE_ID);

	@Autowired
	private DashboardIndicatorDataDAO didDao;
	
	@Autowired
	private WeeklyDashboardIndicatorDataDAO wdidDao;
	

	/**
	 * Gets the first day of a week
	 * @param forDate
	 * @return
	 */
	public Date getInitialWeekDay(Date forDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(forDate);
		while( cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		return cal.getTime();
	}
	
	/**
	 * Adds days to a calendar date
	 * @param forDate
	 * @param amount
	 * @return
	 */
	public Date addDays(Date forDate, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(forDate);
		cal.add(Calendar.DAY_OF_MONTH, amount);
		return cal.getTime();
	}
	

	/**
	 * Converts a Daily Dashboard Indicator Data to a Weekly Dashboard Indicator Data
	 * @param src
	 * @return
	 * @throws ASException
	 */
	public WeeklyDashboardIndicatorData toWeeklyIndicatorData(DashboardIndicatorData src) throws ASException {

		try {
			WeeklyDashboardIndicatorData obj = new WeeklyDashboardIndicatorData();
			Date wrkDate = getInitialWeekDay(sdf.parse(src.getStringDate()));

			obj.setEntityId(src.getEntityId());
			obj.setEntityKind(src.getEntityKind());

			obj.setElementId(src.getElementId());
			obj.setElementName(src.getElementName());
			obj.setElementSubId(src.getElementSubId());
			obj.setElementSubName(src.getElementSubName());

			obj.setTimeZone(src.getTimeZone());

			obj.setStringDate(sdf.format(wrkDate));

			obj.setDayOfWeek(src.getDayOfWeek());
			obj.setDate(wrkDate);
			obj.setMovieId(null);
			obj.setMovieName(null);
			obj.setSubentityId(src.getSubentityId());
			obj.setSubentityName(src.getSubentityName());
			obj.setCountry(src.getCountry());
			obj.setCity(src.getCity());
			obj.setProvince(src.getProvince());
			obj.setVoucherType(null);
			obj.setPeriodType(src.getPeriodType());

			obj.setKey(wdidDao.createKey(obj));

			return obj;
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	/**
	 * Runs the resume process
	 * @param entityIds
	 * @param fromDate
	 * @param toDate
	 * @param deletePreviousRecords
	 * @throws ASException
	 */
	public void resumeDashboardDataForDays(List<String> entityIds, Date fromDate, Date toDate, boolean deletePreviousRecords) throws ASException {
		
		try {

			Date curDate = new Date(fromDate.getTime());
			while( curDate.before(toDate)) {

				for( String entityId : entityIds ) {
					resumeWeek(entityId, null, curDate, deletePreviousRecords);
				}

				curDate = new Date(curDate.getTime() + Constants.WEEK_IN_MILLIS);

			}

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getLocalizedMessage(), e);
		}
		
	}
	
	public Double median(List<Double> src) {
		
		if( !CollectionUtils.isEmpty(src)) {
			Collections.sort(src);
			return src.get(src.size()/2);
		}
		return 0D;		
	}
	
	/**
	 * Resumes Dashboard Indicator Data in a Weekly Manner
	 * @param entityId
	 * @param entityKind
	 * @param forDate
	 * @throws ASException
	 */
	public void resumeWeek(String entityId, Integer entityKind, Date forDate, boolean deletePreviousRecords ) throws ASException {
		
		Date initialDate = getInitialWeekDay(forDate);
		Date finalDate = addDays(initialDate, 6);
		WeeklyDashboardIndicatorData obj;
		List<DashboardIndicatorData> list;
		
		if( deletePreviousRecords ) {
			wdidDao.deleteUsingSubentityIdAndElementIdAndDate(entityId, null, initialDate, initialDate, GMT);
		}
		
		Map<WeeklyDashboardIndicatorData, WeeklyDashboardIndicatorData> indicatorsSet = CollectionFactory.createMap();

		list = didDao.getUsingFilters(null, null, "apd_visitor", "visitor_total_visits", null, entityId,
				null, sdf.format(initialDate), sdf.format(finalDate), null, null, null, null, null, null, null, null);
		for( DashboardIndicatorData did : list ) {

			obj = toWeeklyIndicatorData(did);
			
			if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
			else indicatorsSet.put(obj, obj);
			obj.setDoubleValue(obj.getDoubleValue() + did.getDoubleValue());
			
		}

		list = didDao.getUsingFilters(null, null, "apd_visitor", "visitor_total_peasents", null, entityId,
				null, sdf.format(initialDate), sdf.format(finalDate), null, null, null, null, null, null, null, null);
		for( DashboardIndicatorData did : list ) {

			obj = toWeeklyIndicatorData(did);
			
			if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
			else indicatorsSet.put(obj, obj);
			obj.setDoubleValue(obj.getDoubleValue() + did.getDoubleValue());
			
		}

		list = didDao.getUsingFilters(null, null, "apd_permanence", "permanence_hourly_visits", null, entityId,
				null, sdf.format(initialDate), sdf.format(finalDate), null, null, null, null, null, null, null, null);
		for( DashboardIndicatorData did : list ) {

			obj = toWeeklyIndicatorData(did);
			
			if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
			else indicatorsSet.put(obj, obj);
			obj.getWorkArray().add(did.getDoubleValue());
			
		}

		list = didDao.getUsingFilters(null, null, "apd_permanence", "permanence_hourly_peasents", null, entityId,
				null, sdf.format(initialDate), sdf.format(finalDate), null, null, null, null, null, null, null, null);
		for( DashboardIndicatorData did : list ) {

			obj = toWeeklyIndicatorData(did);
			
			if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
			else indicatorsSet.put(obj, obj);
			obj.getWorkArray().add(did.getDoubleValue());
			
		}

		list = didDao.getUsingFilters(null, null, "apd_occupation", "occupation_hourly_visits", null, entityId,
				null, sdf.format(initialDate), sdf.format(finalDate), null, null, null, null, null, null, null, null);
		for( DashboardIndicatorData did : list ) {

			obj = toWeeklyIndicatorData(did);
			
			if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
			else indicatorsSet.put(obj, obj);
			obj.getWorkArray().add(did.getDoubleValue());
			
		}

		list = didDao.getUsingFilters(null, null, "apd_occupation", "occupation_hourly_peasants", null, entityId,
				null, sdf.format(initialDate), sdf.format(finalDate), null, null, null, null, null, null, null, null);
		for( DashboardIndicatorData did : list ) {

			obj = toWeeklyIndicatorData(did);
			
			if(indicatorsSet.containsKey(obj)) obj = indicatorsSet.get(obj);
			else indicatorsSet.put(obj, obj);
			obj.getWorkArray().add(did.getDoubleValue());
			
		}

		// Transform median values
		Iterator<WeeklyDashboardIndicatorData> i = indicatorsSet.keySet().iterator();
		while(i.hasNext()) {
			WeeklyDashboardIndicatorData o = i.next();
			if(!o.getWorkArray().isEmpty())
				o.setDoubleValue(median(o.getWorkArray()));
		}
		
		// Finally, save all the information
		wdidDao.createOrUpdate(null, CollectionFactory.createList(indicatorsSet.values()), true);

	}

}
