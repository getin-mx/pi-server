package mobi.allshoppings.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import mobi.allshoppings.auth.UserInfo;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.tools.IndexHelper;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.tools.CustomDatatableFilter;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public interface GenericDAO<T extends ModelKey> {

	// Basic Gets
	Class<?> getAffectedClass(); 
	T get(String identifier) throws ASException; 
	T get(String identifier, Boolean detachable) throws ASException;
	T get(PersistenceProvider pp, String identifier, Boolean detachable) throws ASException;
	
	// List Gets
	List<T> getAll() throws ASException;
	List<T> getAll(boolean detachable) throws ASException;
	List<T> getAll(PersistenceProvider pp) throws ASException;
	List<T> getAll(PersistenceProvider pp, boolean detachable) throws ASException;
	List<T> getUsingRange(Range range) throws ASException;
	List<T> getUsingRange(Range range, boolean detachable) throws ASException;
	List<T> getUsingRange(PersistenceProvider pp, Range range) throws ASException;
	List<T> getUsingRange(PersistenceProvider pp, Range range, boolean detachable) throws ASException;
	List<T> getUsingIdList(List<String> idList) throws ASException;
	List<T> getUsingIdList(List<String> idList, boolean detachable) throws ASException;
	List<T> getUsingIdList(PersistenceProvider pp, List<String> idList) throws ASException;
	List<T> getUsingIdList(PersistenceProvider pp, List<String> idList, boolean detachable) throws ASException;
	List<T> getAllAndOrder(String order) throws ASException;
	List<T> getAllAndOrder(String order, boolean detachable) throws ASException;
	List<T> getAllAndOrder(PersistenceProvider pp, String order) throws ASException;
	List<T> getAllAndOrder(PersistenceProvider pp, String order, boolean detachable) throws ASException;
	List<T> getUsingStatusAndRange(List<Integer> status, Range range, String order) throws ASException;
	List<T> getUsingStatusAndRange(List<Integer> status, Range range, String order, Map<String, String> aattributes, boolean detachable) throws ASException;
	List<T> getUsingStatusAndRangeAndCountry(List<Integer> status, Range range, String country, String order, Map<String, String> aattributes, boolean detachable) throws ASException;
	List<T> getUsingStatusAndRange(PersistenceProvider pp, List<Integer> status, Range range, String order) throws ASException;
	List<T> getUsingStatusAndRange(PersistenceProvider pp, List<Integer> status, Range range, String order, Map<String, String> attributes, boolean detachable) throws ASException;
	List<T> getUsingLastUpdateStatusAndRange(PersistenceProvider pp, Date lastUpdate, boolean afterLastUpdateDate, List<Integer> status, Range range, String order, Map<String, String> attributes, boolean detachable) throws ASException;

	// Basic CRUD Operations
	void create(T obj) throws ASException;
	void create(PersistenceProvider pp, T obj, boolean performPreStore) throws ASException;
	void createOrUpdate(T obj) throws ASException;
	void createOrUpdate(PersistenceProvider pp, T obj, boolean performPreStore) throws ASException;
	void createOrUpdate(PersistenceProvider pp, List<T> obj, boolean performPreStore) throws ASException;
	void update(T obj) throws ASException;
	void update(PersistenceProvider pp, T obj, boolean performPreStore) throws ASException;
	void delete(T obj) throws ASException;
	void delete(PersistenceProvider pp, T obj) throws ASException;
	void delete(String identifier) throws ASException;
	void delete(PersistenceProvider pp, String identifier) throws ASException;
	void deleteAll() throws ASException;
	void deleteAll(PersistenceProvider pp) throws ASException;

	// Search Operations
	List<T> getUsingIndex(String q, ViewLocation viewLocation, List<Integer> status, Range range, Map<String, String> additionalFields, String order, String lang) throws ASException;
	List<T> getUsingIndex(String indexName, String q, ViewLocation viewLocation, List<Integer> status, Range range, Map<String, String> additionalFields, String order, String lang) throws ASException;
	IndexHelper getIndexHelper();
	
	// Datatable Operations
	long count() throws ASException;
	long count(UserInfo userInfo) throws ASException;
	long count(String keyName, String keyValue, UserInfo userInfo) throws ASException;
	long count(List<Integer> status) throws ASException;
	List<T> getForTable(String[] columnSort, String sortDirection, String[] searchFields, String search, long first, long last, UserInfo userInfo) throws ASException;
	List<T> getForTableWidthKey(String keyName, String keyValue, String[] columnSort, String sortType, String[] searchFields, String search, long first, long last, UserInfo userInfo) throws ASException;
	
	// Internal Operations
	String toParameterList(List<String> parms);
	String toWellParametrizedFilter(List<String> filters);
	String toWellParametrizedFilter(Map<String, Object> map );

	CustomDatatableFilter buildCustomFilter(UserInfo userInfo);
	boolean safeAndInLimits(T obj, UserInfo userInfo);
	
}
