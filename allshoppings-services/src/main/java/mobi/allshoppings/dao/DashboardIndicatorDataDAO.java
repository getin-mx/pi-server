package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.DashboardIndicatorData;

public interface DashboardIndicatorDataDAO extends GenericDAO<DashboardIndicatorData> {

	Key createKey(DashboardIndicatorData obj) throws ASException;
	List<DashboardIndicatorData> getUsingFilters(String entityId, byte entityKind, String elementId,
			String elementSubId, String shoppingId, String subentityId, String periodType,
			String fromStringDate, String toStringDate, String movieId, String voucherType,
			byte dayOfWeek, byte timeZone, String order, String country, String province, String city)
			throws ASException;

	List<DashboardIndicatorData> getUsingFilters(String entityId,
			byte entityKind, List<String> elementId, String elementSubId, String shoppingId,
			String subentityId, String periodType, String fromStringDate, String toStringDate,
			String movieId, String voucherType, byte dayOfWeek, byte timeZone,  String order,
			String country, String province, String city) throws ASException;

	List<DashboardIndicatorData> getUsingFilters(String entityId,
			byte entityKind, List<String> elementId, List<String> elementSubId, String shoppingId,
			String subentityId, String periodType, String fromStringDate, String toStringDate,
			String movieId, String voucherType, byte dayOfWeek, byte timeZone, String order, 
			String country, String province, String city) throws ASException;

	List<DashboardIndicatorData> getUsingFilters(List<String> entityId, byte entityKind,
			List<String> elementId, List<String> elementSubId, String shoppingId,
			List<String> subentityId, String periodType, String fromStringDate, String toStringDate,
			String movieId, String voucherType, byte dayOfWeek, byte timeZone, String order, 
			String country, String province, String city) throws ASException;

	void deleteUsingSubentityIdAndElementIdAndDate(String subentityId, List<String> elementId,
			Date fromDate, Date toDate, TimeZone tz) throws ASException;
}
