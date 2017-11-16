package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDVAnalysis;
import mobi.allshoppings.tools.Range;

public interface APDVAnalysisDAO extends GenericDAO<APDVAnalysis> {

	Key createKey(APDVAnalysis obj) throws ASException;
	List<APDVAnalysis> getUsingHostnameAndDates(List<String> hostname, Date fromDate, Date toDate, Range range, boolean detachable) throws ASException;
	List<String> getMacsUsingHostnameAndDates(List<String> hostname, Date fromDate, Date toDate) throws ASException;
}
