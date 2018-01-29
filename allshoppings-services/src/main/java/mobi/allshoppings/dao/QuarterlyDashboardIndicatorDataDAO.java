package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.QuarterlyDashboardIndicatorData;

public interface QuarterlyDashboardIndicatorDataDAO extends GenericDAO<QuarterlyDashboardIndicatorData> {

	Key createKey(QuarterlyDashboardIndicatorData obj) throws ASException;
	List<QuarterlyDashboardIndicatorData> getUsingFilters(String entityId,
			Integer entityKind, String elementId, String elementSubId,
			String shoppingId, String subentityId, String periodType,
			String fromStringDate, String toStringDate, String movieId,
			String voucherType, Integer dayOfWeek, Integer timeZone, 
			String order, String country, String province, String city)
			throws ASException;

	List<QuarterlyDashboardIndicatorData> getUsingFilters(String entityId,
			Integer entityKind, List<String> elementId, String elementSubId,
			String shoppingId, String subentityId, String periodType,
			String fromStringDate, String toStringDate, String movieId,
			String voucherType, Integer dayOfWeek, Integer timeZone, 
			String order, String country, String province, String city)
			throws ASException;

	List<QuarterlyDashboardIndicatorData> getUsingFilters(String entityId,
			Integer entityKind, List<String> elementId, List<String> elementSubId,
			String shoppingId, String subentityId, String periodType,
			String fromStringDate, String toStringDate, String movieId,
			String voucherType, Integer dayOfWeek, Integer timeZone, String order, 
			String country, String province, String city)
			throws ASException;

	List<QuarterlyDashboardIndicatorData> getUsingFilters(List<String> entityId,
			Integer entityKind, List<String> elementId, List<String> elementSubId,
			String shoppingId, List<String> subentityId, String periodType,
			String fromStringDate, String toStringDate, String movieId,
			String voucherType, Integer dayOfWeek, Integer timeZone, String order, 
			String country, String province, String city)
			throws ASException;

	void deleteUsingSubentityIdAndElementIdAndDate(String subentityId,
			List<String> elementId, Date fromDate, Date toDate, TimeZone tz)
			throws ASException;
	
	void deleteUsingSubentityIdAndElementIdAndDateAndTimezoneOffset(String subentityId, List<String> elementId,
			Date fromDate, Date toDate, TimeZone referenceTz, byte didLocalTzOffset) throws ASException;
	
}
