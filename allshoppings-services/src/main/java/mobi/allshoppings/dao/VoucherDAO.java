package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Voucher;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public interface VoucherDAO extends GenericDAO<Voucher> {

	Key createKey(String identifier) throws ASException;
	Long getNextSequence() throws ASException;
	Voucher getNextAvailable(String type) throws ASException;
	List<Voucher> getUsingStatusAndBrandAndType(List<Integer> status, String brandId, List<String> type) throws ASException;
	List<Voucher> getUsingStatusAndBrandAndType(PersistenceProvider pp, List<Integer> status, String brandId, List<String> type, Range range, String order, boolean detachable) throws ASException;
	List<Voucher> getUsingDatesAndType(Date fromDate, Date toDate, List<String> type, Range range, String order) throws ASException;
	List<Voucher> getUsingDatesAndType(PersistenceProvider pp, Date fromDate, Date toDate, List<String> type, Range range, String order, boolean detachable) throws ASException;

}
