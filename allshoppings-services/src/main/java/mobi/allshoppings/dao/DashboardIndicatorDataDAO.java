package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.DashboardIndicatorData;

public interface DashboardIndicatorDataDAO extends GenericDAO<DashboardIndicatorData> {

	Key createKey(DashboardIndicatorData obj) throws ASException;
	List<DashboardIndicatorData> getUsingFilters(String entityId,
			Integer entityKind, String elementId, String elementSubId,
			String shoppingId, String subentityId, String periodType,
			String fromStringDate, String toStringDate, String movieId,
			String voucherType, Integer dayOfWeek, Integer timeZone, 
			String order, String country, String province, String city)
			throws ASException;

	List<DashboardIndicatorData> getUsingFilters(String entityId,
			Integer entityKind, List<String> elementId, String elementSubId,
			String shoppingId, String subentityId, String periodType,
			String fromStringDate, String toStringDate, String movieId,
			String voucherType, Integer dayOfWeek, Integer timeZone, 
			String order, String country, String province, String city)
			throws ASException;

	List<DashboardIndicatorData> getUsingFilters(String entityId,
			Integer entityKind, List<String> elementId, List<String> elementSubId,
			String shoppingId, String subentityId, String periodType,
			String fromStringDate, String toStringDate, String movieId,
			String voucherType, Integer dayOfWeek, Integer timeZone, String order, 
			String country, String province, String city)
			throws ASException;

	List<DashboardIndicatorData> getUsingFilters(List<String> entityId,
			Integer entityKind, List<String> elementId, List<String> elementSubId,
			String shoppingId, List<String> subentityId, String periodType,
			String fromStringDate, String toStringDate, String movieId,
			String voucherType, Integer dayOfWeek, Integer timeZone, String order, 
			String country, String province, String city)
			throws ASException;

	void deleteUsingSubentityIdAndElementIdAndDate(String subentityId,
			List<String> elementId, Date fromDate, Date toDate)
			throws ASException;
	
	/**
	 * Prepares the DAO to use a time zoned date to string parser. This will only
	 * take effect on the first call to any CRUD operation. Be aware that this method
	 * is mean to be temporal, and not only it should dissapear when all time
	 * zone constraints are fixed; but not all methods may use a time zoned parser.
	 */
	void useTimedZone();
}
